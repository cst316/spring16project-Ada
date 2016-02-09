package net.sf.memoranda;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ReportImpl.java
 * 
 * Implementation of Report interface
 */
public class ReportImpl implements Report {

	private int _style = 0;
	private String[] _ids = new String[0];
	
	/**
	 * Gets the style for HTML export
	 * @return The selected style for report
	 */
	public int getStyle() {
		return _style;
	}

	/**
	 * Sets the formatting style for HTML export
	 * @param style - int from 0-2 representing minimum, medium, or maximum 
	 */
	public void setStyle(int style) {
		if (style >= 0 && style <= 2) {
			_style = style;
		} else {
			_style = 0;
		}
	}

	/**
	 * Gets all selected tasks
	 * @return Collection of Task elements
	 */
	public Collection<Task> getTasks() {
		TaskList allTasks = CurrentProject.getTaskList();
		
		ArrayList<Task> tasks = new ArrayList<>();
		
		for (int i = 0; i < _ids.length; i++) {
			tasks.add(allTasks.getTask(_ids[i]));
		}
		
		return tasks;
	}

	public void setTasks(String[] ids) {
		_ids = ids;
	}

}
