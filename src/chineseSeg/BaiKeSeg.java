package chineseSeg;
/*
 * 对百度百科的文章进行分词
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import tagger.Tag;

public class BaiKeSeg {
	public void wordSegs(String content, int boost) {
		Analyzer analyzer = new PaodingAnalyzer();
		StringReader reader;
		reader = new StringReader(content);
		TokenStream ts = analyzer.tokenStream("pao", reader);
		try {
			Token t = ts.next();
			while (t != null) {
				int count = boost;
				if (wordmap.containsKey(t.termText())) {
					count = wordmap.get(t.termText());
					count += boost;
				}
				wordmap.put(t.termText(), count);
				t = ts.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void wordSegs(String content) {
		wordSegs(content, 1);
	}
	
	public List<Tag> analyzeHtml(String filepath) {
		related_words.clear();
		wordmap.clear();
		File file = new File(filepath);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String fstr = "";
		try{
			fstr = new String(filecontent,"GB2312");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		String title = "<title>(.+?)</title>";
		String more_word = "<div class=\"word_more_con\"(.+?)</div>";
		Pattern p = Pattern.compile(title);
		Matcher matcher = p.matcher(fstr);
		if(matcher.find()) {
			related_words.add(matcher.group(1));
		}
		p = Pattern.compile(more_word,Pattern.DOTALL);
		matcher = p.matcher(fstr);
		if(matcher.find()) {
			more_word = "<a.*>(.+?)</a>";
			matcher = Pattern.compile(more_word).matcher(matcher.group(1));
			while(matcher.find()) {
				//System.out.println(matcher.group(1));
				related_words.add(matcher.group(1));
			}
		}
		
		String tag_re = "<[^>]*>";
		String style_re = "<style[^>]*>.*?</style>";
		String script_re = "<script[^>]*>.*?</script>";
		wordmap = new HashMap<String, Integer>();
		fstr = Pattern.compile(style_re + "|" + script_re,Pattern.DOTALL).matcher(fstr).replaceAll("");
		fstr = fstr.replaceAll(tag_re, " ");
		wordSegs(fstr);
		
		List<Tag> tag_list = new ArrayList<Tag>();
		ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
				wordmap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		for (Entry<String, Integer> e : list) {
			tag_list.add(new Tag(e.getKey(), e.getValue(), false, false));
		}
		
		return tag_list;
	}
	
	public List<String> getRelatedWords() {
		return related_words;
	}

	private Map<String, Integer> wordmap = new HashMap<String, Integer>();
	private List<String> related_words = new ArrayList<String>();
}
