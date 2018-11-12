/**
 * This class takes in positive and negative feature vectorized 
 * data performes k-fold CV accuracy, precision, recall stats on
 * the natural distribution of the entire collection of positive 
 * and negative instances using the SVMLightModel 
 * which is java text interface to SVMLight.
 * 
 * To be more precise the class works as follows:
 * It takes as input P, N, and N'
 * Let P and N, N' denote the set of positive and negative examples. 
 * N' is the set of negative examples which we split into K folds
 * and add it to the validation set when we perform K-fold CV
 * First, the class performs a full shuffle of N' (We don't need
 * to shuffle P, N because it is part of CV).
 * Then it splits N' in K equal parts N_1, N_2, ... N_k each of size
 * N'/K . Where by size I mean the number of instances in N'.
 * 
 * The rest of this class is self explanatory
 * 
 * @author arjun
 *
 */
import java.io.*;
import java.util.*;

public class K_Fold_Natural_Dist_SVMClassifier {

	// <instance_index, instance_feature_vector> for performing binning required for CV
	HashMap<Integer, String> doc_feature_vectors;
	
	//List of shuffled and split negative segments (the constructor does the shuffling and splitting)
	ArrayList<ArrayList<String>> rest_neg_samples;

	/**
	 * Constructor to booststrap/initialze this class
	 * @param pos_vectors ArrayList of positive class feature vectors +1 fv
	 * @param neg_vectors ArrayList of negative class feature vectors -1 fv (N)
	 * @param rest_neg ArrayList of rest of negative class feature vectors -1 fv (N')
	 * @param K no. of folds of CV
	 */
	public K_Fold_Natural_Dist_SVMClassifier(ArrayList<String> pos_vectors, ArrayList<String> neg_vectors,
			ArrayList<String> rest_neg, int K){
		doc_feature_vectors = new HashMap<Integer, String>();
		rest_neg_samples = new ArrayList<ArrayList<String>>();
		// add instances of both pos and neg vectors in doc_feature_vectors
		int i = 0;
		for(String s : pos_vectors){
			i++;
			doc_feature_vectors.put(i, s);
		}
		for(String s : neg_vectors){
			i++;
			doc_feature_vectors.put(i, s);
		}
		//Now first shuffle contents of rest negative samples
		rest_neg = shuffle(rest_neg);
		// Then split it into several parts each of size rest_neg.size()/K and add it to rest_neg_samples
		int size_Nk = rest_neg.size()/K;
		ArrayList<String> temp = new ArrayList<String>();
		for(int j = 0; j<rest_neg.size(); j++){
			temp.add(rest_neg.get(j));
			if(temp.size()%size_Nk == 0){
				rest_neg_samples.add(temp);
				temp = new ArrayList<String>();
			}
		}
		// If owing to integer division there are more than K segments, merge the last two
		if(rest_neg_samples.size()>K){
			rest_neg_samples.get(K-1).addAll(rest_neg_samples.get(K));
			rest_neg_samples.remove(K);
		}
		
	}
	
	/**
	 * Returns a truly "Shuffled" copy of the list by randomizing the order of the list
	 * @param list
	 */
	public static ArrayList<String> shuffle(ArrayList<String> list){
		/*
		 * HashMap<Integer, String> list_content = new HashMap<Integer, String>();
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
		*/
		
		// Faster way, yse native methods
		Collections.shuffle(list);
		return new ArrayList<String>(list);
	}
	
	/**
	 * Perform cross validation and saves the model parameters in each fold
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
				//System.out.print(random_instance_index + "  ");
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

				bw.flush(); bw.close();
				
				// creating the testing file.

				bw = new BufferedWriter(new FileWriter("test.dat"));

				for(int index : test_set_instance_indices){
					String feature_vector = doc_feature_vectors.get(index);
					bw.write(feature_vector);
					bw.newLine();
					if(feature_vector.startsWith("-"))
						test_labels.add(-1);
					else
						test_labels.add(+1);
				}

				// Add other negative samples to ensure the test/validation set is the natural distribution
				for(String feature_vector : rest_neg_samples.get(i)){
					bw.write(feature_vector);
					bw.newLine();
					if(feature_vector.startsWith("-"))
						test_labels.add(-1);
					else
						test_labels.add(+1);
				}
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
			//System.out.println("Printing instance ID Map" + model.getIDMap() + "\n");
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


	public static void main(String a []){
		String [] pos_fvs = {"+1 2:.09 13:1.2 23:.02", "1 1:1 2:.98", "+1 5:-.32 71:.21"};
		String [] neg_fvs = {"-1 2:.09 13:1.2 23:.02", "-1 1:1 2:.98", "-1 5:-.32 71:.21"};
		ArrayList<String> pos_vectors = new ArrayList<String>(Arrays.asList(pos_fvs));
		ArrayList<String> neg_vectors = new ArrayList<String>(Arrays.asList(neg_fvs));
		K_Fold_Natural_Dist_SVMClassifier c_model = new K_Fold_Natural_Dist_SVMClassifier(pos_vectors, neg_vectors, neg_vectors, 2);

		c_model.performCrossValidation(2);
	}

}
