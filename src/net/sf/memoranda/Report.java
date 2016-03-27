package net.sf.memoranda;

import java.util.Collection;

/**
 * Report.java
 * 
 * Interface class for satisfying the "Generate Report" requirement (TMS-UC-007)
 */
public interface Report {
	
	static final int STYLE_MINIMUM = 0;
	static final int STYLE_MEDIUM = 1;
	static final int STYLE_MAXIMUM = 2;
	
	int getStyle();
	void setStyle(int style);
	
	Collection<Task> getTasks();
	void setTasks(String[] ids);
	
	void exportHtml();
	void exportHtmlMin();
	void exportHtmlMed();
	void exportHtmlMax();
}
