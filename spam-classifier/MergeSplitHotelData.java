import java.io.*;

public class MergeSplitHotelData  {
	public static void main(String [] a) throws Exception{
		// Script for splitting reviews in  a file (one review per line) to a set of files (one review / file)
		
		BufferedReader br = new BufferedReader(new FileReader("Unfilt_Res_2"));
		String line = "";
		int count = 932;
		while((line = br.readLine())!=null){
			++count;
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					new File ("./yelpRestaurantReviews/test_ND/ham/" + count)
					));
			bw.write(line);
			bw.flush(); bw.close();
			
			
			}
		br.close();
		
		
		
		
		// Script for merging reviews per file from a directory to one line per review in a file
		/*
		BufferedWriter bw = new BufferedWriter(new FileWriter("Hotel_Spam.txt"));
		BufferedReader br = null;
		File folder = new File("./Hotel/Spam");
		int i = 0;
		for(File f:folder.listFiles()){
			br = new BufferedReader(new FileReader(f));
			String review = "";
			String line;
			while((line = br.readLine())!=null && line.length()>0){
				review += line + " ";
			}
			bw.write(review);
			bw.newLine();
			i++;
			System.out.println(i + ") " + f + "  Review: " + review );
		br.close();
		bw.flush();
		
		}
		bw.close();
		*/
			
	}
}
