package net.sf.memoranda.util;

public class LogPair {
	private String date;
	private long length;
	private int index;
	
	public LogPair(String date, long length, int index) {
		setDate(date);
		setLength(length);
		this.index = index;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public long getLength() {
		return length;
	}
	
	public void setLength(long length) {
		this.length = length;
	}
	
	public int getIndex() {
		return this.index;
	}
}
