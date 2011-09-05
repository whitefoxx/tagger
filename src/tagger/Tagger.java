package tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Tagger {
	public Tagger() {
		String conf = "./conf/config.conf";
		String idf_f = null;
		String xw_f = null;
		String en_prefix_f = null;
		db_url = null;
		webpage_dir = null;
		filter_method = 0;
		min_count = 5;
		wordidf = new HashMap<String, Double>();
		xwords = new HashSet<String>();
		en_word_prefix = new HashMap<String, Boolean>();
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(conf));
			String line = null;
			while((line=reader.readLine()) != null) {
				if(!line.startsWith("#")){
					String[] tokens = line.split(" |#");
					if(tokens.length > 1) {
						if(tokens[0].compareTo("idf_f") == 0)
							idf_f = tokens[1].trim();
						else if(tokens[0].compareTo("xw_f") == 0) {
							xw_f = tokens[1].trim();
						}
						else if(tokens[0].compareTo("en_prefix_f") == 0) {
							en_prefix_f = tokens[1].trim();
						}
						else if(tokens[0].compareTo("db_url") == 0) {
							db_url = tokens[1].trim();
						}
						else if(tokens[0].compareTo("webpage_dir") == 0) {
							webpage_dir = tokens[1].trim();
						}
						else if(tokens[0].compareTo("filter_method") == 0) {
							filter_method = Integer.parseInt(tokens[1].trim());
						}
						else if(tokens[0].compareTo("min_count") == 0) {
							filter_method = Integer.parseInt(tokens[1].trim());
						}
					}
				}
			}
			reader.close();

			if(filter_method == 1) {
				reader = new BufferedReader(new FileReader(idf_f));
				while((line=reader.readLine()) != null) {
					String[] tokens = line.split(" ");
					if(tokens.length > 2) {
						String word = tokens[0];
						double idf = Double.parseDouble(tokens[2]);
						wordidf.put(word.toLowerCase(), idf);
					}
				}
				reader.close();
			}
			
			reader = new BufferedReader(new FileReader(xw_f));
			while((line=reader.readLine()) != null) {
				xwords.add(line);
			}
			reader.close();
			
			reader = new BufferedReader(new FileReader(en_prefix_f));
			while((line=reader.readLine()) != null) {
				String word = line.substring(0, line.length()-2).toLowerCase();
				if(line.endsWith("0")) {					
					if(en_word_prefix.get(word) == null) {
						en_word_prefix.put(word,false);
					}
				}
				else{
					en_word_prefix.put(word,true);
				}
			}
			reader.close();
			
			extracter = new ExtractTags(en_word_prefix,xwords,wordidf,db_url);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ExtractTags getExtracter() {
		return extracter;
	}
	
	public void tag() {
		long t1 = (new Date()).getTime();
		int page_count = 0;	
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch( ClassNotFoundException e ) {
			e.printStackTrace();
		}		
		try{
			Connection con = DriverManager.getConnection(db_url);
			Statement sql = con.createStatement();
			Statement sql2 = con.createStatement();			
			ResultSet file_rs = sql2.executeQuery("select id,file from main_item where is_tagged=0");			
			while(file_rs.next()) {
				String file = file_rs.getString("file");
				int item_id = file_rs.getInt("id");
				String fname = file.split("/")[1];
				List<Tag> tmp_list = extracter.analyzeHtmlWithMainContent(webpage_dir+fname);
				List<Tag> list1 = extracter.filterTags(tmp_list,filter_method);
				String[] tokens = fname.split("\\.");
				FileWriter fw = new FileWriter(new File(webpage_dir + tokens[0] + ".txt"));
				int count = 0;
				for(Tag t : list1) {
					if(t.getCount() >= min_count) {						
						ResultSet rs2 = sql.executeQuery("select * from main_item_topic where item_id=" + item_id + " and topic_id=" + t.id);
						if(rs2.next()) continue;
						sql.executeUpdate("insert into main_item_topic(item_id,topic_id,deleted,topic_deleted)" +
								" values(" + item_id + "," + t.id + ",0,0)");	
						if(t.inDB)
							fw.write(t.getTag() + " " + t.getCount() + " 1\n");
						else
							fw.write(t.getTag() + " " + t.getCount() + " 0\n");
						count ++;
						if(count >= 5)
							break;
					}					
				}
				fw.close();
				/* update is_tagged column */
//				if(count > 0) {
//					sql.executeUpdate("update main_item set is_tagged=1 where id=" + item_id);
//				}
				/* extract and store candidate tags */
				List<Tag> candidate_tags = extracter.candidate_tags;
				for(Tag t : candidate_tags) {
					if(t.getCount() < 30)
						continue;
					ResultSet rs3 = sql.executeQuery("select count from main_candidatetopic where name=\"" + t.getTag() + "\"" );
					if(rs3.next()){
						count = rs3.getInt("count") + 1;
						sql.executeUpdate("update main_candidatetopic set count=" + count + " where name=\"" + t.getTag() + "\"");
					}
					else{
						String lan = "zh";
						if(t.getTag().length() == t.getTag().getBytes().length)
							lan = "en";
						sql.executeUpdate("insert into main_candidatetopic(name,language,count) values(\"" + t.getTag() + "\",\"" + 
								lan + "\",1)");
					}
				}
				page_count ++;
			}
			con.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		long t2 = (new Date()).getTime();
		System.out.println("Pages: " + page_count + ", Time(s): " + (t2 - t1)*1.0/1000);
	}
	
	private Map<String, Double> wordidf;
	private Set<String> xwords;
	private Map<String, Boolean> en_word_prefix;
	private String db_url;
	private String webpage_dir;
	private int filter_method;
	private int min_count;
	private ExtractTags extracter;
}
