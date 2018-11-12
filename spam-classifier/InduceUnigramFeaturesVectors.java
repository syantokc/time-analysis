/**
 * Assumes a file with two classes
 * The file should have exactly one instance per line
 * with a class label as follows
 * where 0x1 (Ctrl-A) is the delimiter
 *  -1/+1 0x1 text body of the instance 
 * @author arjun
 *
 */
import java.util.*;
import java.io.*;

public class InduceUnigramFeaturesVectors {
	static int Feature_Start = 50;
	static int min_line_length = 5;
	int total_documents;
	HashSet<String> vocab = new HashSet<String>();
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	HashMap<Integer, String> i_map = new HashMap<Integer, String>(); //inverse map
	HashMap<Integer, Integer> feature_counts = new HashMap<Integer, Integer>(); // <features_index , feature_count>
	HashMap<Integer, Integer> feature_doc_count = new HashMap<Integer, Integer>(); // <feature_index, # of docs in which it appears>

	// Ctrl+A or Start of Header Delimiter
	char [] c = {0x1};
	String soh = new String(c);
	
	static ArrayList<String>  parseSimple(String s){
		ArrayList<String> tokens = new ArrayList();		
		for(String st : s.toLowerCase().split("[\\p{Punct}]+")){
			//System.out.println(st);		
			tokens.addAll( Arrays.asList( st.trim().split("[\\p{Space}]+") ) );
		}
		return tokens;
	}
	/**
	 * Takes a file and parses it to build the features set
	 * 
	 * @param path the file to parse in the required format of this class
	 */
	public void buildFeatures(String path){
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			int feature_index = Feature_Start -1 ;// start at index 30 (other will be added later on)
			int doc_count = 0; // a counter for documents (document is one per line)
			while((line = br.readLine())!=null ){
				if(!(line.length() > min_line_length && line.contains(soh)))
					continue;
				doc_count++;
				// Parse the text content of this instance
				String class_and_doc [] = line.split(soh);
				if(!(class_and_doc.length == 3))
					continue; // it does not contain class label and document
				ArrayList<String> tokens = parseSimple(class_and_doc[1]);//ParseDocument.parseDoc( class_and_doc[1] );
				//System.out.println("Printing the token list: " + tokens);
				for(String s : tokens){
					if(!vocab.contains(s)){
						vocab.add(s);
						feature_index++;
						map.put(s, feature_index);
						i_map.put(feature_index, s);
						feature_counts.put(feature_index, 1);
					} else{
						int existing_index = map.get(s);
						int old_count = feature_counts.get(existing_index);
						feature_counts.put(feature_index, old_count+1);
					}
				}
				// Build a set of distinct terms in this document to compute IDF
				HashSet<String> distinct_tokens = new HashSet<String>(tokens);
				for(String s : distinct_tokens){
					int f_index = map.get(s);
					int old_count = feature_doc_count.containsKey(f_index)?feature_doc_count.get(f_index):0;
					if(old_count == 0)
						feature_doc_count.put(f_index, 1);
					else
						feature_doc_count.put(f_index, old_count + 1);
				}
			}
			br.close();
			total_documents = doc_count; // set the total number of documents in this collection
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Takes a file in the format of this class and exports a file with 
	 * document vectors with boolean/TF feature value assignment in the format of SVMLight
	 * @param inFile source file to parse
	 * @param outFile output file in SVMLight format
	 * @param mincount threshold for the minimum no. of time that a feature must appear in the corpus to
	 * be accounted while computing document vectors 
	 * @param assignment : 0: boolean feature values assignment, 1: (TF) Term frequency, 2:TF-IDF
	 * @return An HashMap of [instance_serial_no, instance_feature_vector] with instance serial no. starting at index 1
	 */
	public HashMap<Integer, String> buildDocFeatureVectors(String inFile, String outFile, int mincount, int assignment, ArrayList<String> time_features){
		HashMap<Integer, String> feature_vectors = new HashMap<Integer, String>();

		// First select the features based on the mincount and add those feature indices to the set of candidate
		// features.
		ArrayList<Integer> candidate_features = new ArrayList<Integer>();
		// this for loop ensures that features are ordered in increasing order, a KEY requirement of SVMLight
		// First get the feature indices and sort them
		ArrayList<Integer> feature_indices = new ArrayList<Integer>();
		for(int i : feature_counts.keySet())
			feature_indices.add(i);
		Collections.sort(feature_indices);
		for(int i : feature_indices) 
			if(feature_counts.get(i)>mincount)
				candidate_features.add(i);

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));	
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line;
			int line_count = 0;
			int loop_count = -1;
			while((line = br.readLine())!=null ){
				loop_count++;
				if(!(line.length() > min_line_length && line.contains(soh)))
					continue;
				line_count++;
				String class_and_doc [] = line.split(soh);
				if(!(class_and_doc.length == 3))
					continue; // it does not contain class label and document

				String doc_feature_vector = class_and_doc[0] + " "; // Add the class
				String doc_feature_vector1 = class_and_doc[0] + " "+time_features.get(loop_count); // Add the class
				//*
				//String doc_feature_vector = class_and_doc[0] + " "+time_features.get(loop_count); // Add the class
				ArrayList<String> tokens = parseSimple(class_and_doc[1]);//ParseDocument.parseDoc( class_and_doc[1] );


				if(assignment == 0){
					// performing boolean feature value assignment. No need to assign 0 values
					for(int i : candidate_features){

						if(tokens.contains(i_map.get(i)))
							doc_feature_vector += i + ":1 ";

					}

				} else if (assignment == 1){ // TF feature value assignment
					for(int i : candidate_features){
						String token = i_map.get(i);
						if(tokens.contains(token))
							doc_feature_vector += i + ":" + 
									(float)Collections.frequency(tokens, token)/(float)tokens.size() + " ";
					}
				}
				else if (assignment == 2){ // need to perform TF-IDF feature value assignment scheme
					for(int i : candidate_features){
						String token = i_map.get(i);
						if(tokens.contains(token))
							doc_feature_vector += i + ":" + 
									(
											(float)Collections.frequency(tokens, token)/(float)tokens.size() *
											Math.log10((float)total_documents/(float)feature_doc_count.get(i))  
											)
											+ " ";
					}

				}
				doc_feature_vector += " #" + class_and_doc[2];
				//*/
				doc_feature_vector1 += " #" + class_and_doc[2];
				bw.write(doc_feature_vector1);
				bw.newLine();
				//feature_vectors.put(line_count, doc_feature_vector1);
				feature_vectors.put(line_count, doc_feature_vector);
			}
			br.close();
			bw.flush();
			bw.close();
		}catch(Exception e ){
			e.printStackTrace();
		}
		return feature_vectors;
	}

	/*
	public void addHeldoutFeatures() {
		String [] paths = {"F_1.txt", "F_2.txt", "F_3.txt", "F_4.txt", "F_5.txt", 
				"UF_50_1.txt", "UF_50_2.txt", "UF_50_3.txt", "UF_50_4.txt", "UF_50_5.txt"}; 
		for (String path : paths) {
			System.out.println("Printing vocab size: " + vocab.size() + "map size: " + map.size()
					+ "feature_count size: " + feature_counts.size());
			try {
				BufferedReader br = new BufferedReader(new FileReader(path));
				String line = "";
				int feature_index = vocab.size();// start at index 1
				while ((line = br.readLine()) != null) {
					// Parse the text content of this instance 
					ArrayList<String> tokens = ParseDocument.parseDoc(line);
					for (String s : tokens) {
						if (!vocab.contains(s)) {
							vocab.add(s);
							feature_index++;
							map.put(s, feature_index);
							i_map.put(feature_index, s);
							feature_counts.put(feature_index, 1);
						} else {
							int existing_index = map.get(s);
							int old_count = feature_counts.get(existing_index);
							feature_counts.put(feature_index, old_count + 1);
						}
					}
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Features processed for : " + path);
		}

	}
	 */

}
