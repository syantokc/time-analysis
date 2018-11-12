/**
 * This class ensure a pure "shuffle" of the training/test files
 * so that there is no bias in the data.
 * @author arjun
 *
 */
import java.util.*;
import java.io.*;
public class DataShuffler {
	/**
	 * Main class for shuffling
	 * @param inFile input File path
	 * @param outFile output File path to write the shuffled data
	 */
	public void shuffler(String inFile, String outFile){
		HashMap<Integer, String> linewise_content = new HashMap<Integer, String>(); 
		try{
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line; int line_no = 0;
			while((line = br.readLine())!=null){
				line_no++;
				linewise_content.put(line_no, line);
				if(line.length()<=0)
					System.out.println(line_no);
				
			}
			br.close();

			System.out.println("Total lines in file: " + linewise_content.size());

			ArrayList<Integer> random_list = null;
			for(int shuffle_count = 1; shuffle_count<=10; shuffle_count++){
				// Get a new array of random integers ranging from 1 to total line count
				random_list = new ArrayList<Integer>();
				
				// This is a very sloppy way of doing this. I cannot help but need to get it done fast
				while(random_list.size() != linewise_content.size()){
					int rand = (int)(Math.random() * linewise_content.size()) + 1;
					if(!random_list.contains(rand))
						random_list.add(rand);
				}
				
				System.out.println("Got here!");
				// check to see whether all numbers are present or not;
				for(int i = 1; i<=linewise_content.size(); i++)
					if(!random_list.contains(i))
						System.err.println("Random number generation FAILED");
			}
			System.out.println(random_list + "\n\n");
			// Write the shuffled file back to disk
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			for(int i=0; i<random_list.size(); i++){
				bw.write(linewise_content.get(random_list.get(i)));
				// if last line no need to give a newline break
				if(i != (random_list.size()-1) )
					bw.newLine();
			}
			bw.flush();
			bw.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * This method splits a file into a number of segments
	 * @param inFile input file to split
	 * @param segment_prefix prefix to name the segments 
	 * @param segment_sizes int array of segment sizes (in no. of lines)
	 */
	void splitter(String inFile, String segment_prefix, int segment_sizes[]){
		HashMap<Integer, String> linewise_content = new HashMap<Integer, String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line = null;
			int line_count = 0;
			while((line = br.readLine())!=null){
				line_count++;
				linewise_content.put(line_count, line);

			}

			// Now write the file segments according to specified sizes
			int start = 1;
			int j = 0;
			for(int i = 0; i<segment_sizes.length; i++){
				BufferedWriter bw = new BufferedWriter(new FileWriter(segment_prefix + "_" + i) );
				
				for(j = start; j <= (start + segment_sizes[i] - 1); j++){
					bw.write(linewise_content.get(j));
					
					if(j != (start + segment_sizes[i] - 1))
						bw.newLine();
					
				}
				start = j;
				bw.flush();	 bw.close();
				System.out.println("Segement done!");
			}
			
		}catch(Exception e){

		}
	}

	/**
	 * Driver for testing the methods
	 * @param a
	 */
	public static void main(String a[]){
		DataShuffler ds = new DataShuffler();
		//ds.shuffler("Filt_all_hotel.txt", "Filt_all_hotel_S.txt");
		//ds.shuffler("Unfilt_all_hotel.txt", "Unfilt_all_hotel_S.txt");
		int sizes [] =  {3729, 932, 5944};
		ds.splitter("Unfilt_all_Restaurant_S.txt", "Unfilt_Res", sizes);
	}
}
