import java.io.*;
import java.util.*;

public class ExpForSetting1_3 {

	public static void main(String [] a){
		// Ctrl+A or Start of Header Delimiter
		char [] c = {0x1};
		String soh = new String(c);
		int count = 0;
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter("reviews_all.txt"));
			BufferedReader br = new BufferedReader(new FileReader("Hotel_Spam.txt"));
			String line = "";
			while((line = br.readLine())!=null){
				line = "+1" + soh + line + soh + ++count;
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			br = new BufferedReader(new FileReader("20H_Ham_400.txt"));
			while((line = br.readLine())!=null){
				line = "-1" + soh + line + soh + ++count;
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			br = new BufferedReader(new FileReader("Spam_Hot50.txt"));
			while((line = br.readLine())!=null){
				line = "+1" + soh + line + soh + ++count;
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			br = new BufferedReader(new FileReader("Ham_Hot50.txt"));
			while((line = br.readLine())!=null){
				line = "-1" + soh + line + soh + ++count;
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();

			// File generated. Now build unigram FVs.
			HashMap<Integer, String> fvs = null;
			InduceUnigramFeaturesVectors f_model = new InduceUnigramFeaturesVectors();
			f_model.buildFeatures("reviews_all.txt");
			fvs = f_model.buildDocFeatureVectors("reviews_all.txt", "all_feature_vectors", 0, 0,null);
			
			// Now build train and test set
			BufferedWriter bw_1 = new BufferedWriter(new FileWriter("train.dat"));
			for(int i = 1; i <= 800; i++)
				bw_1.write(fvs.get(i) + "\n");
			bw_1.flush();
			bw_1.close();
			
			bw_1 = new BufferedWriter(new FileWriter("test.dat"));
			for(int i = 801; i <= 3348; i++) //1122; 2431; 2031
				bw_1.write(fvs.get(i) + "\n");
			bw_1.flush();
			bw_1.close();
			
			// Lastly, perform classification
			SVMLightModel model = new SVMLightModel();
			model.train("train.dat");
			System.out.println(model.getClassificationResult("test.dat"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
