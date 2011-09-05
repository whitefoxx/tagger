package chineseSeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RelatedWord {
	public static void main(String[] args) {		
		try{
			String out = "E:/data/lancorpus/relatedwords.txt";
			FileWriter fw = new FileWriter(out);
			for(int i = 0; i <= 40; i++) {
				String dir_s = "E:/data/lancorpus/word/" + i + "/";
				File dir = new File(dir_s);
				File[] files = dir.listFiles();
				for(File f : files) {
					String fname = f.getName();
					if(!fname.endsWith(".r"))
						continue;
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = reader.readLine();
					if(line != null) {
						String[] words = line.split("_");
						if(words.length > 0) {
							fw.write(words[0] + "\n");
							while((line = reader.readLine()) != null) {
								fw.write(line + "\n");
							}
						}
					}
					fw.write("\n");
					reader.close();
				}
			}
			fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
