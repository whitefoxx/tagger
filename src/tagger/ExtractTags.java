package tagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import de.l3s.boilerpipe.extractors.DefaultExtractor;

public class ExtractTags {
	public ExtractTags(Map<String, Boolean> en_prefix,Set<String> xw,Map<String, Double> w_idf, String db) {
		wordidf = w_idf;
		xwords = xw;		
		en_word_prefix = en_prefix;
		analyzer = new PaodingAnalyzer();
		default_idf = 2;
		is_zh = true;
		db_url = db;
	}
	
	public void extractTags(String content, int boost) {
		if(is_zh)
			extractTags_zh(content, boost);
		else
			extractTags_en(content, boost);
	}
	
	private void extractTags_en(String content, int boost) {
		StringReader reader;
		reader = new StringReader(content);
		TokenStream ts = analyzer.tokenStream("pao", reader);
		try {
			List<String> list = new ArrayList<String>();
			Token t = ts.next();
			while (t != null) {
				list.add(t.termText());
				t = ts.next();
			}
			for(int i=0; i<list.size();) {
				int j = i;
				String word = list.get(i).toLowerCase();
				String tag = null;
				Boolean is_topic = null;
				while((is_topic = en_word_prefix.get(word)) != null) {	
					if(is_topic.booleanValue()) {
						tag = word;
					}
					if(i<list.size()-1)
						word = word + " " + list.get(++i);
					else
						break;
				}
				if(tag != null) {
					int count = boost;
					Integer c;
					if ((c = wordcount.get(tag)) != null) {
						count = c + boost;
					}
					wordcount.put(tag, count);
				}
				if(i == j)
					++i;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void extractTags_zh(String content, int boost) {		
		StringReader reader;
		reader = new StringReader(content);
		TokenStream ts = analyzer.tokenStream("pao", reader);
		try {
			Token t = ts.next();
			while (t != null) {
				int count = boost;
				if (wordcount.containsKey(t.termText())) {
					count = wordcount.get(t.termText());
					count += boost;
				}
				wordcount.put(t.termText(), count);
				t = ts.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Tag> filterTags(List<Tag> list, int filterMethod) {
		List<Tag> tag_list = new ArrayList<Tag>();
		List<Tag> tmp_list1 = new ArrayList<Tag>();
		List<Tag> tmp_list2 = new ArrayList<Tag>();
		String tag_name;
		int tag_count;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch( ClassNotFoundException e ) {
			e.printStackTrace();
			return null;
		}
		
		boolean candidate = false;
		if(candidate_tags.size() == 0)
			candidate = true;
		
		try{
			Connection con = DriverManager.getConnection(db_url);
			Statement sql = con.createStatement();
			int count = 0;
			int avg_count = 0;
			for(Tag tag : list) {
				tag_name = tag.getTag();
				if(tag_name.length() < 2)
					continue;
				if(xwords.contains(tag_name) && !tag.inTitle)
					continue;
				boolean tagged = false;
				tag_count = tag.getCount();
				ResultSet rs = sql.executeQuery("select id from main_topic where name=\"" + tag_name + "\"");
				if(rs.next()) {
					int topic_id = rs.getInt("id");
					tmp_list1.add(new Tag(topic_id,tag_name,tag.getCount(),true,tag.inTitle));
					count++;
					avg_count += tag_count;
					if(count >= 15)
						break;	
					tagged = true;
				}
				else if(!is_zh && !tag_name.endsWith("s")) {
					tag_name += "s";
					rs = sql.executeQuery("select id from main_topic where name=\"" + tag_name + "\"");
					if(rs.next()) {
						int topic_id = rs.getInt("id");
						tmp_list1.add(new Tag(topic_id,tag_name,tag.getCount(),true,tag.inTitle));
						count++;
						avg_count += tag_count;
						if(count >= 15)
							break;	
						tagged = true;
					}
				}
				if(!tagged && candidate){
					candidate_tags.add(new Tag(tag_name,tag.getCount(),false,tag.inTitle));
				}
			}
			String tag_str = "";
			if(count > 0) {
				avg_count /= count;
				for(Tag tag : tmp_list1) {
					if(count > 5){
						if(tag.getCount() >= avg_count){
							tmp_list2.add(tag);
							tag_str += (tag.getTag() + "|");
						}
					}
					else{
						tmp_list2.add(tag);
						tag_str += (tag.getTag() + "|");
					}
				}
			}
			for(Tag tag : tmp_list2) {
				if(is_zh || (!tag_str.contains(tag.getTag() + " ") && !tag_str.contains(" " + tag.getTag())))
					tag_list.add(tag);
			}
			con.close();
		}
		catch( SQLException e ) {
			e.printStackTrace();
			return null;
		}
		
		if(filterMethod == 0) 	//TF
			return tag_list;
		else {					//TFIDF
			Collections.sort(tag_list, new Comparator<Tag>() {
				public int compare(Tag o1,Tag o2) {
					Double tfidf1,tfidf2;
					tfidf1 = wordidf.get(o1.getTag());
					tfidf2 = wordidf.get(o2.getTag());
					if(tfidf1 != null)
						tfidf1 *= o1.getCount();
					else
						tfidf1 = default_idf * o1.getCount();
					if(tfidf2 != null)
						tfidf2 *= o2.getCount();
					else
						tfidf2 = default_idf * o2.getCount();
					if(tfidf1 > tfidf2)
						return 0;
					return 1;
				}
			});
			
			return tag_list;
		}
	}

	public List<Tag> analyzeHtmlWithMainContent(String filepath) {
		File file = new File(filepath);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		String fstr = "";
		String title="";
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			fstr = new String(filecontent,"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		String mainContent = "";
		wordcount = new HashMap<String, Integer>();
		candidate_tags = new ArrayList<Tag>();
		
		try{
			URL url;
	        url = new URL("file:///" + filepath);	
	        mainContent = DefaultExtractor.INSTANCE.getText(url);
	        if(mainContent.length()*1.0/mainContent.getBytes().length > 0.9)
	        	is_zh = false;
	        else
	        	is_zh = true;
	        extractTags(mainContent, 1);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		String title_re = "<title[^>]*>(.+?)</title>";
		String des_re = "<meta.*?name=\"[Dd]escription\".*?content=\"(.+?)\">";
		String h1_re = "<h1[^>]*>(.+?)</h1>";
		String h2_re = "<h2[^>]*>(.+?)</h2>";
		String h3_re = "<h3[^>]*>(.+?)</h3>";
		Pattern title_pattern = Pattern.compile(title_re);
		Pattern des_pattern = Pattern.compile(des_re);
		Pattern h1_pattern = Pattern.compile(h1_re);
		Pattern h2_pattern = Pattern.compile(h2_re);
		Pattern h3_pattern = Pattern.compile(h3_re);

		Matcher matcher = title_pattern.matcher(fstr);
		if (matcher.find()) {
			String t = matcher.group(1);
			t = t.replaceAll("<.+?>", "");
			String decs = "\\||-|&#8212;|—|_";
			int max = -1;
			String[] tokens = t.split(decs);				
			for(String token : tokens) {
				if(token.getBytes().length > max) {
					max = token.getBytes().length;
					title = token;
				}
			}
			extractTags(title, 20);
		}

		matcher = des_pattern.matcher(fstr);
		if (matcher.find()) {
			String des = matcher.group(1);
			extractTags(des, 8);
		}

		matcher = h1_pattern.matcher(fstr);
		String h1 = "";
		while (matcher.find()) {
			String tmp = matcher.group(1).replaceAll("<.+?>", "");
			if(mainContent.contentEquals(tmp))
				h1 += tmp;
		}
		extractTags(h1, 6);

		matcher = h2_pattern.matcher(fstr);
		String h2 = "";
		while (matcher.find()) {
			String tmp = matcher.group(1).replaceAll("<.+?>", "");
			if(mainContent.contentEquals(tmp))
				h2 += tmp;
		}
		extractTags(h2, 4);

		matcher = h3_pattern.matcher(fstr);
		String h3 = "";
		while (matcher.find()) {
			String tmp = matcher.group(1).replaceAll("<.+?>", "");
			if(mainContent.contentEquals(tmp))
				h3 += tmp;
		}
		extractTags(h3, 2);		

		List<Tag> tag_list = new ArrayList<Tag>();
		ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
				wordcount.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});

		for (Entry<String, Integer> e : list) {
			tag_list.add(new Tag(e.getKey(), e.getValue(), false, title.contains(e.getKey())));
		}
		System.out.println(title);
		return tag_list;
	}

	private Map<String, Integer> wordcount;
	private Map<String, Double> wordidf;
	private Set<String> xwords;
	public List<Tag> candidate_tags;
	private Map<String, Boolean> en_word_prefix;
	Analyzer analyzer;
	private double default_idf;
	private boolean is_zh;
	private String db_url;
}
