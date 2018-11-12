/**
 * This class is the main classifier / driver which takes in
 * text instances from two files (each belonging to a +1 and -1)
 * class, and computes 5-fold CV accuracy, precision, recall stats
 * using the SVMLightModel which is java text interface to SVMLight
 *
 * @author arjun
 * updated by @santosh
 *
 */
import java.io.*;
import java.util.*;

public class SVMClassifierDriver {
	static int min_line_length = 2;
	// Ctrl+A or Start of Header Delimiter
	char [] c = {0x1};
	String soh = new String(c);
	// <instance_index, instance_feature_vector> of all +/- instances
	HashMap<Integer, String> doc_feature_vectors = null;
	// + vectorized instances
	ArrayList<String> pos_set = null;
	// - vectorized instances
	ArrayList<String> neg_set = null;
	static ArrayList<String> time_features = new ArrayList<String>();


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
			BufferedReader br = new BufferedReader(new FileReader(new File(class1)));
			String line;
			while((line = br.readLine())!=null ){
				if(!(line.length() > min_line_length && line.contains(soh)))
					continue;
				String [] instance = line.split(soh);
				//System.out.println(instance.length);
				if (!(instance.length==3))
					continue;
				time_features.add(instance[2]);
				bw.write("+1" + soh + instance[1].replaceAll("[^a-zA-Z0-9 ]", "") + soh + instance[0]);
				bw.newLine();
			}
			br.close();
			br = new BufferedReader(new FileReader(new File(class2)));
			while((line = br.readLine())!=null){
				if(!(line.length() > min_line_length && line.contains(soh)))
					continue;

				String [] instance = line.split(soh);
				if (!(instance.length==3))
					continue;
				time_features.add(instance[2]);
				bw.write("-1" + soh + instance[1].replaceAll("[^a-zA-Z0-9 ]", "") + soh + instance[0]);
				bw.newLine();
			}
			br.close();
			bw.flush();
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	void populate_features(){
		doc_feature_vectors = new HashMap<Integer, String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File("./features/policy1-v5/all_feature_vectors")));
			String line;
			int line_count = 1;
			while((line = br.readLine())!=null ){
				doc_feature_vectors.put(line_count, line);
				line_count++;
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
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

	public static void main(String a []){
		SVMClassifierDriver c_model = new SVMClassifierDriver();
		//c_model.populate_features();
		//*
		// Prepare data by merging files so that features vectors can be computed once and for all
		// merge(pos class data, neg class data, merged file)
		c_model.mergeFiles("files with features");
		//c_model.mergeFiles("./All_Data_id_time_features/Filt_all_res.txt", "./All_Data_id_time_features/Unfilt_all_res.txt", "reviews_all.txt");
		// Build features
		InduceUnigramFeaturesVectors f_model = new InduceUnigramFeaturesVectors();
		//InduceBigramFeaturesVectors f_model = new InduceBigramFeaturesVectors();
		f_model.buildFeatures("reviews_all.txt");

		// Vectorize documents with feature vectors and save feature vectors for all +/- instances
		c_model.doc_feature_vectors = f_model.buildDocFeatureVectors("reviews_all.txt", "all_feature_vectors", 0, 0, time_features);
		//*/

		// Get back the positive and negative feature vectors and store them for further processing
		ArrayList<String> pos = new ArrayList<String>();
		ArrayList<String> neg = new ArrayList<String>();
		for(int i=1; i<=c_model.doc_feature_vectors.size(); i++ ){ //
			String fv = c_model.doc_feature_vectors.get(i);
			if(fv == null)
				continue;
			if(fv.startsWith("-")) // negative class instance
				neg.add(fv);
			else
				pos.add(fv);
		}
		c_model.pos_set = pos;
		c_model.neg_set = neg;


		//shuffle the negative and positive set
		c_model.neg_set = c_model.shuffle(c_model.neg_set);
		c_model.pos_set = c_model.shuffle(c_model.pos_set);

		// Save the positive and neagtive set of feature vectorized instances
		UseSerialization.doSave(c_model.pos_set, "pos_feature_vectors.ser");
		UseSerialization.doSave(c_model.neg_set, "neg_feature_vectors.ser");

		// load them back from serialized files (this is only for multiple runs
		// when you don't want to induce features again as that is time consuming)

		c_model.pos_set = (ArrayList<String>)UseSerialization.doLoad("pos_feature_vectors.ser");
		c_model.neg_set = (ArrayList<String>)UseSerialization.doLoad("neg_feature_vectors.ser");


		// shuffle again for being sure

		c_model.neg_set = c_model.shuffle(c_model.neg_set);
		c_model.pos_set = c_model.shuffle(c_model.pos_set);
		System.out.println("Shuffle done");

		{

			K_Fold_SVMClassifier svm_cv_model = new K_Fold_SVMClassifier(c_model.pos_set,
					new ArrayList<String>(
							c_model.neg_set.subList(0, c_model.pos_set.size()) )
					);
			svm_cv_model.performCrossValidation(5);
		}



		// Now we can juggle around with the data and do all sorts of experiments
		ArrayList<String> neg_set_50_50 = new ArrayList<String>(
				c_model.neg_set.subList(0, c_model.pos_set.size()) );
		ArrayList<String> rest_neg = new ArrayList<String>(
				c_model.neg_set.subList(c_model.pos_set.size(), c_model.neg_set.size()) );

		// For 50-50 setting (use entire pos and equal no. of negative samples)



		// For Natural distribution, we just need to provide the rest of the negative instances
		/*
		System.out.println("\n\nNATURAL DIST\n\n");
		{
		K_Fold_Natural_Dist_SVMClassifier svm_cv_ND_model = new K_Fold_Natural_Dist_SVMClassifier(
				c_model.pos_set, neg_set_50_50, rest_neg, 5);
		svm_cv_ND_model.performCrossValidation(5);
		}

		*/

		/**
		 *  For CV on arbitrary ratio of +/- simply select the required no. of
		 *  neg samples from c_model.neg_set and pass it to  K_Fold_SVMClassifier.
		 *
		 *  For CV with Natural Distribution on arbitrary ratio of +/-, simply select
		 *  required no. of neg samples from c_model.neg_set to ensure the required ratio
		 *  and store it in some var say neg_custom.
		 *  Then, store the rest of the negative samples in some var say rest_neg
		 *  and then pass c_model.pos_set, neg_custom, rest_neg to
		 *  _Fold_Natural_Dist_SVMClassifier
		 *
		 *  I am not implementing these as these are dead simple
		 */


	}

}
