package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tagger.ExtractTags;
import tagger.Tag;
import tagger.Tagger;

public class Test_ExtractTags {
	public static void main(String[] args) {
		String webpage = "E:/data/webpage2/";
		String dburl="jdbc:mysql://localhost:3306/xifenfen?user=root&password=q1w2e3";
		test_1(webpage,dburl);
	}
	
	public static void test_1(String webpage, String dburl) {
		String en_gram = "E:/data/lancorpus/en_words_prefix.txt";
		Map<String, Boolean> en_word_grams = new HashMap<String, Boolean>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(en_gram));
			String line;
			while((line=reader.readLine()) != null) {
				String word = line.substring(0, line.length()-2).toLowerCase();
				if(line.endsWith("0")) {					
					if(en_word_grams.get(word) == null) {
						en_word_grams.put(word,false);
					}
				}
				else{
					en_word_grams.put(word,true);
				}
			}
			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		//String fname = "0zwh13uptvb5qx2e7id6lfk9c4ogajmy.html";
		String fname = "ve5jlmh9f17qogktxbzu3yawn462rdsi.html";
		Tagger tagger = new Tagger();
		ExtractTags extracter = tagger.getExtracter();		
		List<Tag> tmp_list = extracter.analyzeHtmlWithMainContent(webpage+fname);
		List<Tag> list1 = extracter.filterTags(tmp_list,0);
		for(Tag t : list1) {
			System.out.println(t.getTag() + " " + t.getCount());
		}
	}
}
