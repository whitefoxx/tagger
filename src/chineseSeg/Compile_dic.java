package chineseSeg;
/*
 * 编译庖丁词库
 */
import java.io.File;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

public class Compile_dic {
	static String dic = "E:/paoding/dic/";
	public static void main(String args[]) {
		File dir = new File(dic + ".compiled");
		if(dir.exists()) {
			deleteFolder(dir);
		}
		new PaodingAnalyzer();
	}
	
	public static void deleteFolder(File dir)   { 
	    File filelist[]=dir.listFiles(); 
	    int listlen=filelist.length; 
	    for(int i=0;i < listlen;i++)   { 
	        if(filelist[i].isDirectory()) { 
	            deleteFolder(filelist[i]); 
	        } 
	        else { 
	            filelist[i].delete(); 
	        } 
	    } 
	    dir.delete();
	}
}
