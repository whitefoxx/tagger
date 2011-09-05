package chineseSeg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WFProcessor {
	public static void main(String[] args) {
		String df = "E:/data/lancorpus/DF.txt";
		Map<String,Integer> wordmap = new HashMap<String, Integer>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(df));
			String line = null;
			while((line=reader.readLine()) != null) {
				String[] tokens = line.split(" ");
				if(tokens.length > 1) {
					String word = tokens[0];
					int doccount = Integer.parseInt(tokens[1]);
					if(doccount >= 3 && word.length() <= 10) {
						wordmap.put(word, doccount);
					}
				}
			}
			reader.close();
			
			ArrayList<Entry<String,Integer>> list = new ArrayList<Entry<String,Integer>>(wordmap.entrySet());
			Collections.sort(list, new Comparator<Entry<String, Integer>>() {
				public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			String out = "E:/data/lancorpus/DF2.txt";
			FileWriter fw = new FileWriter(out);
			for (Entry<String, Integer> e : list) {
				fw.write(e.getKey() + " " + e.getValue() + "\n");
			}
			fw.close();

		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
