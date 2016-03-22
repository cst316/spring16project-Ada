package net.sf.memoranda.util;

public class LogPair {
	private String date;
	private long length;
	
	public LogPair(String date, long length) {
		setDate(date);
		setLength(length);
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
}
