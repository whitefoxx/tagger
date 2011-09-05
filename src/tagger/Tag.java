package tagger;

public class Tag {
	public Tag(String t, int c,boolean indb, boolean intitle) {
		tag = t;
		count = c;
		inDB = indb;
		inTitle = intitle;
	}
	Tag(int i, String t, int c, boolean indb, boolean intitle) {
		id = i;
		tag = t;
		count = c;
		inDB = indb;
		inTitle = intitle;
	}
	public int id;
	public String tag;
	public int count;
	public boolean inDB;
	public boolean inTitle;
	public double IDF;
	
	public String getTag() {
		return tag;
	}
	
	public int getCount() {
		return count;
	}
}
