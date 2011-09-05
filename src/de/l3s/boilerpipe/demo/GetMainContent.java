package de.l3s.boilerpipe.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.xml.sax.InputSource;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLFetcher;

/**
 * Demonstrates how to use Boilerpipe when working with {@link InputSource}s.
 * 
 * @author Christian Kohlschütter
 */
public class GetMainContent {
	public static void get() {
		Connection con;
		Statement sql;
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		} 
		catch(ClassNotFoundException e) {}
		
		try{
			con = DriverManager.getConnection("jdbc:mysql://localhost/test","root","q1w2e3");
			sql = con.createStatement();
		    String sql_s = "select url from main_item";
		    ResultSet rs = sql.executeQuery(sql_s);
		    while(rs.next()) {
		    	String url = rs.getString("url");
		    	System.out.println(url);
		    }
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
    public static void main(final String[] args) throws Exception {
        get();
    }
}