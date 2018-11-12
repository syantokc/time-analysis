/**
 * This class takes in positive and negative feature vectorized 
 * data performes k-fold CV accuracy, precision, recall stats
 * using the SVMLightModel which is java text interface to SVMLight.
 * 
 * @author arjun
 *
 */
import java.io.*;
import java.util.*;

public class K_Fold_SVMClassifier {

	// <instance_index, instance_feature_vector> for performing binning required for CV
	HashMap<Integer, String> doc_feature_vectors;

	/**
	 * Constructor to booststrap/initialze this class
	 * @param pos_vectors ArrayList of positive class feature vectors +1 fv
	 * @param neg_vectors ArrayList of negative class feature vectors -1 fv
	 */
	public K_Fold_SVMClassifier(ArrayList<String> pos_vectors, ArrayList<String> neg_vectors){
		doc_feature_vectors = new HashMap<Integer, String>();

		// add instances of both in doc_feature_vectors
		int i = 0;
		for(String s : pos_vectors){
			i++;
			doc_feature_vectors.put(i, s);
		}
		for(String s : neg_vectors){
			i++;
			doc_feature_vectors.put(i, s);
		}
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


	public static void main(String a []){
		String [] pos_fvs = {"+1 2:.09 13:1.2 23:.02", "1 1:1 2:.98", "+1 5:-.32 71:.21"};
		String [] neg_fvs = {"-1 2:.09 13:1.2 23:.02", "-1 1:1 2:.98", "-1 5:-.32 71:.21"};
		ArrayList<String> pos_vectors = new ArrayList<String>(Arrays.asList(pos_fvs));
		ArrayList<String> neg_vectors = new ArrayList<String>(Arrays.asList(neg_fvs));
		K_Fold_SVMClassifier c_model = new K_Fold_SVMClassifier(pos_vectors, neg_vectors);

		c_model.performCrossValidation(2);
	}

}
