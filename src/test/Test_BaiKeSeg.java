package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import chineseSeg.BaiKeSeg;

import tagger.Tag;

public class Test_BaiKeSeg {
	public static void main(String[] args) {
		test_1();
	}
	
	public static void test_1() {
		BaiKeSeg seg = new BaiKeSeg();
		Pattern pattern = Pattern.compile("([0-9]|\\.|-)*");
		for(int i = 0; i <= 40; i++) {
			String dir_s = "E:/data/lancorpus/bdbaike/" + i + "/";
			String outdir_s = "E:/data/lancorpus/word/" + i + "/";
			File dir = new File(dir_s);
			File[] files = dir.listFiles();
			for(File f : files) {
				String filein = f.getName();
				if(!filein.endsWith(".htm"))
					continue;
				System.out.println(filein);
				String fileout1 = outdir_s + filein.split("\\.")[0] + ".w";
				String fileout2 = outdir_s + filein.split("\\.")[0] + ".r";
				List<Tag> tag_list = seg.analyzeHtml(dir_s + filein);
				List<String> r_words = seg.getRelatedWords();
				try {
					FileWriter fw1 = new FileWriter(fileout1);
					FileWriter fw2 = new FileWriter(fileout2);
					for(Tag t : tag_list) {
						String tag = t.getTag();
						if(tag.length() > 1 && !pattern.matcher(tag).matches() && !tag.contains(".htm")
								&& !tag.contains("quot") && !tag.contains("http")) {
							fw1.write(tag + " " + t.getCount() + "\n");
						}
					}
					for(String w : r_words) {
						fw2.write(w + "\n");
					}
					fw1.close();
					fw2.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
