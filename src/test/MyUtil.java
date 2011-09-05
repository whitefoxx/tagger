package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MyUtil {
	public static void filterWord() {
		String df = "E:/data/lancorpus/DF2.txt";
		String out = "E:/data/lancorpus/DF3.txt";
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch( ClassNotFoundException e ) {
			e.printStackTrace();
			return;
		}
		
		try{
			String url="jdbc:mysql://localhost:3306/topic?user=root&password=q1w2e3";
			Connection con = DriverManager.getConnection(url);
			Statement sql = con.createStatement();
			
			FileWriter fw = new FileWriter(out);
			BufferedReader reader = new BufferedReader(new FileReader(df));
			String line = null;
			while((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");
				if(tokens.length > 1) {
					String word = tokens[0];
//					int wf = Integer.parseInt(tokens[1]);
					String q = "select * from main_topic where name=\"" + word + "\"";
					ResultSet rs = sql.executeQuery(q);
					if(rs.next()) {
						fw.write(line + "\n");
					}
				}
			}
			fw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void idf() {
		int total_doc = 240639;
		double avg_idf = 0;
		int w_c = 0;
		try{
			String fin = "E:/data/lancorpus/DF3.txt";
			String fout = "E:/data/lancorpus/idf.txt";
			FileWriter fw = new FileWriter(fout);
			BufferedReader reader = new BufferedReader(new FileReader(fin));
			String line = null;
			while((line=reader.readLine()) != null) {
				String[] tokens = line.split(" ");
				if(tokens.length > 1) {
					int count = Integer.parseInt(tokens[1]);
					double idf = Math.log(total_doc*1.0/count);
					w_c++;
					avg_idf += idf;
					fw.write(line + " " + idf + "\n");
				}
			}
			fw.close();
			reader.close();
			
			avg_idf /= w_c; //7.740837802601763
			System.out.println(avg_idf);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void shareAll() {
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch( ClassNotFoundException e ) {
			e.printStackTrace();
		}
		
		try{
			String url="jdbc:mysql://localhost:3306/xifenfen?user=root&password=q1w2e3";
			Connection con = DriverManager.getConnection(url);
			Statement sql = con.createStatement();
			Statement sql2 = con.createStatement();
			
			ResultSet rs = sql.executeQuery("select id from main_item");
			while(rs.next()) {
				int item_id = rs.getInt("id");
				ResultSet rs2 = sql2.executeQuery("select * from main_user_item where user_id=1 and item_id=" + item_id);
				if(rs2.next()) continue;
				sql2.executeUpdate("insert into main_user_item(user_id,item_id,unread,important,source,create_date) values(1," + item_id + ",0,0,\"0000\",\"2011-08-25 23:20:05\")");
			}
			//con.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		//filterWord();
		//idf();
		shareAll();
	}
}
