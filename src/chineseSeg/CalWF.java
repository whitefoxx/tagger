package chineseSeg;

/*
 * 计算词频
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CalWF {
	public static void main(String[] args) {
		Map<String,Integer> wordmap = new HashMap<String, Integer>();
		for(int i = 0; i <= 40; i++) {
			String dir_s = "E:/data/lancorpus/word/" + i + "/";
			File dir = new File(dir_s);
			File[] files = dir.listFiles();
			for(File f : files) {
				String filein = f.getName();
				if(!filein.endsWith(".w"))
					continue;
				System.out.println(filein);
				try{
					BufferedReader reader = new BufferedReader(new FileReader(dir_s + filein));
					String line = null; 
					while((line = reader.readLine()) != null) {
						String[] tokens = line.split(" ");
						if(tokens.length > 1) {
							String word = tokens[0];
							//int wordcount = Integer.parseInt(tokens[1]);
							int doccount = 1;
							if (wordmap.containsKey(word)) {
								doccount = wordmap.get(word);
								doccount += 1;
							}
							wordmap.put(word, doccount);
						}
					}
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String out = "E:/data/lancorpus/DF.txt";
		try{
			FileWriter fw = new FileWriter(out);
			Set<Entry<String,Integer>> wordset = wordmap.entrySet();
			for(Entry<String,Integer> e : wordset) {
				fw.write(e.getKey() + " " + e.getValue() + "\n");
			}
			fw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}	

}
