package test;
/*
 * 一个庖丁解牛分词的例子
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

public class Test_PaoSeg {
	public static void main(String args[]) {
		test_1();
	}
	
	public static void test_1() {
		String str = "一个女软件工程师的征婚PPT";
		Analyzer analyzer = new PaodingAnalyzer();
		StringReader reader = new StringReader(str);
		TokenStream ts = analyzer.tokenStream("test", reader);
		try{
			Token t = ts.next();
			while( t != null ){
				System.out.println(t.termText());
				t = ts.next();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void test_2() {
		Analyzer analyzer = new PaodingAnalyzer();

		FileReader f_reader;
		try{
			f_reader = new FileReader("E:/data/tmp/en1.html");
		}
		catch( FileNotFoundException e){
			e.printStackTrace();
			return;
		}
		Map<String,Integer> wordmap = new HashMap<String, Integer>();
		TokenStream ts = analyzer.tokenStream("test", f_reader);
		try{
			Token t = ts.next();
			while( t != null ){
				int count = 1;
				if(wordmap.containsKey(t.termText())) {
					count = (Integer)wordmap.get(t.termText());
					count++;
				}
				wordmap.put(t.termText(), count);
				t = ts.next();
			}
			ArrayList<Entry<String,Integer>> list = 
				new ArrayList<Entry<String,Integer>>(wordmap.entrySet());
			Collections.sort(list, new Comparator<Entry<String, Integer>>() {    
	            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {    
	                return (o2.getValue() - o1.getValue());    
	            }    
	        });  
			for(Entry<String,Integer> e : list) {  
	            System.out.println(e.getKey() + " " + e.getValue());  
	        } 
		}
		catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	public static void test_3() {
		Analyzer analyzer = new PaodingAnalyzer();

		FileReader f_reader;
		try{
			f_reader = new FileReader("E:/data/tmp/test.txt");
		}
		catch( FileNotFoundException e){
			e.printStackTrace();
			return;
		}
		
		TokenStream ts = analyzer.tokenStream("test", f_reader);
		try{
			Token t = ts.next();
			while( t != null ){
				System.out.println(t.termText());
				t = ts.next();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
