package net.sf.memoranda;

import java.util.Collection;

/**
 * Report.java
 * 
 * Interface class for satisfying the "Generate Report" requirement (TMS-UC-007)
 */
public interface Report {
	
	public static final int STYLE_MINIMUM = 0;
	public static final int STYLE_MEDIUM = 1;
	public static final int STYLE_MAXIMUM = 2;
	
	public int getStyle();
	public void setStyle(int style);
	
	public Collection<Task> getTasks();
	public void setTasks(String[] ids);
	
	public void exportHtml();
	public void exportHtmlMin();
	public void exportHtmlMed();
	public void exportHtmlMax();
}
