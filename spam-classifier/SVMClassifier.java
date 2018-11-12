/**
 * This class is deprecated and no longer used. It was the raw ver
 * of the driver. The newer and better version is SVMClassifierDriver
 * 
 * 
 * This class is the main classifier / driver which takes in 
 * text instances from two files (each belonging to a +1 and -1)
 * class, and computes 5-fold CV accuracy, precision, recall stats
 * using the SVMLightModel which is java text interface to SVMLight
 * 
 * @author arjun
 *
 */
import java.io.*;
import java.util.*;

public class SVMClassifier {

	// Ctrl+A or Start of Header Delimiter
	char [] c = {0x1};
	String soh = new String(c);
	// <instance_index, instance_feature_vector> for performing binning required for CV
	HashMap<Integer, String> doc_feature_vectors = null;
	
	/*
	 * variables for computing feature vectors for previously unobserved data (even features are not observed
	 * so, we need to backup the observed features and create the feature vectors for these new instances
	 * from what features we observed previously 
	 */
	
	HashSet<String> vocab = new HashSet<String>();
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	
	/**
	 * This method merges the raw contents of text instances in each class files (class1/class2)
	 * to the format required by InduceUnigramFeature class
	 * @param class1 file for positive class
	 * @param class2 file for negative class
	 * @param output file for writing the merge
	 */
	void mergeFiles(String class1, String class2, String merge){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(merge));
			BufferedReader br = new BufferedReader(new FileReader(class1));
			String line;
			while((line = br.readLine())!=null){
				bw.write("+1" + soh + line);
				bw.newLine();
			}
			br.close();
			br = new BufferedReader(new FileReader(class2));
			while((line = br.readLine())!=null){
				bw.write("-1" + soh + line);
				bw.newLine();
			}
			br.close();
			bw.flush();
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Perform cross validation and saves the state of the model in each fold
	 * for computation of aggregate statistics later on. It assumes that the 
	 * class varaible doc_feature_vectors have already been set
	 * @param K # of folds
	 */
	void performCrossValidation(int K){
		// First randomize the ordering and performing binning for k folds validation
		int total_instances = doc_feature_vectors.size();
		ArrayList<Integer> random_instance_order = new ArrayList<Integer>();
		while(random_instance_order.size()!=total_instances){
			int random_instance_index = (int)(Math.random() * total_instances) + 1;
			if(!random_instance_order.contains(random_instance_index)){
				random_instance_order.add(random_instance_index);
				System.out.print(random_instance_index + "  ");
			}
		}
		
		// sanity check to see whether all indices are included
		for(int i=1; i<=total_instances; i++)
			if(! (random_instance_order.contains(i)))
				System.err.println("Random number generation FAILED");

		int bin_size = total_instances/K;
		double p = 0, r = 0, a = 0, f = 0;
		SVMLightModel model = null;
		// Now perform cross validation
		for(int i =0; i < K; i++){
			// fold i
			model = new SVMLightModel();
			// build the test set instances
			ArrayList<Integer> test_set_instance_indices = new ArrayList<Integer>();
			for(int j = i*bin_size; j < Math.min((i+1)*bin_size, random_instance_order.size()); j++)
				test_set_instance_indices.add(random_instance_order.get(j));

			// training set is everything apart the test set instances, so very simple!
			ArrayList<Integer> train_set_instance_indices = new ArrayList<Integer>();
			for(int j = 1; j<=total_instances; j++)
				if(!(test_set_instance_indices.contains(j)))
					train_set_instance_indices.add(j);
			// check
			if(test_set_instance_indices.size() + train_set_instance_indices.size() != total_instances)
				System.err.println("Sum doesn't match");
			
			// Print to check the ordering
			/*
			System.out.println("Random indices for fold: " + (i+1) + "\n test set:" + test_set_instance_indices
					+ "\n train set: " + train_set_instance_indices);
			*/
			
			// variable for out_labels
			ArrayList<Integer> test_labels = new ArrayList<Integer>();
			
			model.flush();
			try{
				// creating the training file.
				BufferedWriter bw = new BufferedWriter(new FileWriter("train.dat"));
				
				for(int index : train_set_instance_indices){
					bw.write(doc_feature_vectors.get(index));
					bw.newLine();
				}
				
				// use entire data for training
				/*for(int j = 1; j<=doc_feature_vectors.size(); j++){
					bw.write(doc_feature_vectors.get(j));
					bw.newLine();
				}*/
				bw.flush(); bw.close();
				// creating the testing file.
				
				
				bw = new BufferedWriter(new FileWriter("test.dat"));
				
				// Now preparing test set for validation with features previously pbserved
				
				for(int index : test_set_instance_indices){
					String feature_vector = doc_feature_vectors.get(index);
					bw.write(feature_vector);
					bw.newLine();
					if(feature_vector.startsWith("-"))
						test_labels.add(-1);
					else
						test_labels.add(+1);
					}
				
				
				// Now comes the litmus test! Get more unfiltered data to make the 
				// validation set as the natural distribution
			    	
			
			    /*
			    ArrayList<String> new_positive_class = getFeatureVectors(new String("F_"+(i+1)+".txt"), "+1");
				System.out.println("Done computing fvs for unseen positive data");
				//Now update test_set_labels and test.dat for positive unseen data
				for(String fv : new_positive_class){
					test_labels.add(+1);
					bw.write(fv);
					bw.newLine();
				}
				bw.flush();
				
				*/
				/*
				ArrayList<String> new_negative_class = getFeatureVectors(new String("Unfilt_"+(i+1)), "-1");
				System.out.println("Done computing fvs for unseen negative data");
				// Also update the test_set labels and test.dat for negative class unseen data
				for(String fv : new_negative_class){
					test_labels.add(-1);
					bw.write(fv);
					bw.newLine();
				}
				
					
				*/
				bw.flush();
				bw.close();
				
				System.out.println("Finished writing files.");
			}catch(Exception e){
				e.printStackTrace();
			}
			// now induce the SVMLight model
			
			/*
			System.out.println("Finished model induction. Printing test set labels\n{");
			for(int l : test_labels)
				System.out.print(l + ", ");
			*/
			
			model.train("train.dat");
			int output_labels[] = model.classify("test.dat");
			int [][] c_mat = model.getConfusionMatrix(ArrayListToArray(test_labels));
			
			
			double prec = model.getPrecision();
			double rec = model.getRecall();
			double acc = model.getAccuracy();
			double f1 = model.getF1Score();
			// Print stats for this fold
			System.out.println("Statistics for fold " + (i+1));
			model.printCMat();
			System.out.println("Prec: " + prec);
			System.out.println("Rec: " + rec);
			System.out.println("F1: " + f1);
			System.out.println("Acc: " + acc);
			
			// Aggregate statistics
			p += prec;
			r += rec;
			a += acc;
			f += f1;
		}
		System.out.println("Printing final Cross validation statisitcs: \n"
				+ "Precision: " + p/(float)K
				+ "\n Recall: " + r/(float)K
				+ "\n Accuracy: " + a/(float)K 
				+ "\n F1: " + f/(float)K + "\n\n");

	}
	/**
	 * Simple method for conversion
	 * @param list<Integer>
	 * @return array[]
	 */
	int[] ArrayListToArray(ArrayList<Integer> list) {
		int new_array [] = new int[list.size()];
		for(int i = 0; i<list.size(); i++)
			new_array[i] = list.get(i).intValue();
		return new_array;
	}
	
	/**
	 * This method creates feature vectors from old features for new data
	 * By new data I mean the fully unobserved data whose features were NOT
	 * seen during training phase. The feature value assingment is fixed
	 * to boolean for now.
	 * @param fileName file containing documents of this class
	 * @param label +1/-1
	 * @return
	 */
	private ArrayList<String> getFeatureVectors(String fileName, String label) {
		ArrayList<String> fvs = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader((new FileReader(fileName)));
			String line;
			
			/**
			 *  ensures feature_index number in increasing order (a key requirement of SVMLight)
			 *  Hence using SortedSet
			 */
			SortedSet<Integer> feature_indices;
			String fv;
			int line_no = 0;
			while((line = br.readLine())!=null){
				fv = new String(label) + " ";
				feature_indices = new TreeSet<Integer>();
				ArrayList<String> tokens = ParseDocument.parseDoc(line);
				for(String s : tokens)
					if(vocab.contains(s))
						feature_indices.add(map.get(s));
				// Now compute the feature vector
				for(int i:feature_indices)
					fv += i + ":1 ";
				// And finally add to list of all fvs
				fvs.add(fv);
				/*++line_no;
				if(line_no%100 == 0)
					System.out.println(line_no );*/
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return fvs;
	}
	
	public static ArrayList<String> shuffle(ArrayList<String> list){
		HashMap<Integer, String> list_content = new HashMap<Integer, String>();
		for(int i = 0; i<list.size(); i++)
			list_content.put(i+1, list.get(i));
		ArrayList<Integer> random_list = new ArrayList<Integer>();

		// This is a very sloppy way of doing this. I cannot help but need to get it done fast
		while(random_list.size() != list_content.size()){
			int rand = (int)(Math.random() * list_content.size()) + 1;
			if(!random_list.contains(rand))
				random_list.add(rand);
		}
		// Now traverse the list_content in the order of random_list and update the list;
		ArrayList<String> new_list = new ArrayList<String>();
		for(int i : random_list){
			//System.out.println(i + ".) " + list_content.get(i));
			new_list.add(list_content.get(i));
		}
		return new_list;
	}
	
	public static void main(String arg []){
		String a [] = {"sdgs", "maxi", "et", "fadsf", "gjafsd", "ouou", "jhf", "jydtsgt"};
		
		ArrayList<String> ab = new ArrayList<String>(Arrays.asList(a));
		System.out.println(ab);
		ab = shuffle(ab);
		System.out.println(ab);
		ab = shuffle(ab);
		System.out.println(ab);
		Collections.shuffle(ab);
		System.out.println(ab);
		
		/*
		 SVMClassifier c_model = new SVMClassifier();
		
		c_model.mergeFiles("Filt_all_S.txt", "Unfiltered_MixedCuisine.txt", "reviews_all.txt");
		InduceUnigramFeaturesVectors f_model = new InduceUnigramFeaturesVectors();
		f_model.buildFeatures("reviews_all.txt");
		
		//f_model.addHeldoutFeatures();
		
		// save feature vectors for all observed instances 
		c_model.doc_feature_vectors = f_model.buildDocFeatureVectors("reviews_all.txt", "all_feature_vectors", 0, 0);
		UseSerialization.doSave(c_model.doc_feature_vectors, "doc_feature_vectors.ser");
		
		// save the observed features and its map
		c_model.vocab = f_model.vocab;
		c_model.map = f_model.map;
		UseSerialization.doSave(f_model.vocab, "vocab.ser");
		UseSerialization.doSave(f_model.map, "map.ser");
		UseSerialization.doSave(f_model.i_map, "i_map.ser");
		UseSerialization.doSave(f_model.feature_counts, "feature_counts.ser");
		
		// set the observed features and document feature vectors from serialized files
		c_model.doc_feature_vectors = (HashMap<Integer, String>)UseSerialization.doLoad("doc_feature_vectors.ser");
		c_model.vocab = (HashSet<String>)UseSerialization.doLoad("vocab.ser");
		c_model.map = (HashMap<String, Integer>)UseSerialization.doLoad("map.ser");
		
		c_model.performCrossValidation(5);
		*/
	}

}
