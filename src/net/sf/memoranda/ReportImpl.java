package net.sf.memoranda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.sf.memoranda.util.FileStorage;
import net.sf.memoranda.util.Util;

/**
 * ReportImpl.java
 * 
 * Implementation of Report interface
 */
public class ReportImpl implements Report {

	private int _style = 0;
	private String[] _ids = new String[0];
	private TaskList allTasks = CurrentProject.getTaskList();
	
	private StringBuilder htmlBuilder = new StringBuilder();
	private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	
	private Collection<Task> tasks = new ArrayList<Task>();
	
	
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
		return tasks;
	}

	/**
	 * Sets the task list as a String array of Task IDs
	 */
	public void setTasks(String[] ids) {
		_ids = ids;
		
		tasks.clear();
		
		for (String id : _ids) {
			tasks.add(allTasks.getTask(id));
		}
	}
	
	/**
	 * Handles selected style to make HTML report with correct formatting
	 */
	public void exportHtml() {
		// Open
		htmlBuilder.append("<html>");
		
		// Styles
		htmlBuilder.append(
				"<head><style>" + 
				"table{width:100%;}" +
				"table, th, td {border: 0px solid gray;font-family: sans-serif;}" +
				"th, td {padding: 10px;text-align: left;}" +
				"table tr:nth-child(even) {background-color: #eee;}" +
				"table tr:nth-child(odd) {background-color:#fff;}" +
				"th {background-color: #333;color: white;}" +
				"table {background-color: #222;}" +
				"</style></head>");
		
		// Heading
		htmlBuilder.append("<h2 align=\"center\"><font face=\"arial\">Task Report</font></h2>");
		
		switch (_style) {
			case STYLE_MINIMUM:
				exportHtmlMin();
				break;
			case STYLE_MEDIUM:
				exportHtmlMed();
				break;
			case STYLE_MAXIMUM:
				exportHtmlMax();
				break;
			default:
				exportHtmlMed();
				break;
		}
		
		// Close Table
		htmlBuilder.append("</table>");
		
		// Print current date
		htmlBuilder.append("Report generated on: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		
		// Close Document
		htmlBuilder.append("</body></html>");
		
		// Convert
		String html = htmlBuilder.toString();
		saveHtml(html);
	}
	
	/**
	 * Persistently stores an HTML file for retrieval
	 * @param html The HTML markup of the report
	 */
	private void saveHtml(String html) {
		Util.debug("HTML: " + html);
		
		// Save
		FileStorage store = new FileStorage();
		store.storeReport(html);
	}
	
	/**
	 * For each task, only the following are reported:
	 * Name, Type, Start Date, End Date, Priority, Status, % Done.
	 * The formatting is compact and minimal
	 */
	public void exportHtmlMin() {
		// Table Heading
		htmlBuilder.append("<table><tr><th>Task</th><th>Type</th><th>Start Date</th><th>End Date</th><th>Priority</th><th>Status</th><th>% Done</th></tr>");

		// Table rows
		for (Task task : tasks) {
			htmlBuilder.append("<tr>");
			
			for (int j = 0; j < 7; j++) {
				
				htmlBuilder.append("<td>");
				
				switch (j) {
					case 0: // Task Name
						htmlBuilder.append(task.getText());
						break;
					case 1: // Type
						htmlBuilder.append(task.getType());
						break;
					case 2: // Start Date
						htmlBuilder.append(dateFormat.format(task.getStartDate().getDate()));
						break;
					case 3: // End Date
						htmlBuilder.append(dateFormat.format(task.getEndDate().getDate()));
						break;
					case 4: // Priority
						htmlBuilder.append(task.getPriorityString());
						break;
					case 5: // Status
						htmlBuilder.append(task.getStatusString());
						break;
					case 6: // % Done
						htmlBuilder.append(task.getProgress());
						break;
					default:
						break;
				}
				
				htmlBuilder.append("</td>");
			}
			
			htmlBuilder.append("</tr>");
			
		}
	}
	
	/**
	 * For each task, only the following are reported:
	 * Name, Type, Start Date, End Date, Priority, Status, % Done, Est. Effort, Description
	 * The formatting is compact and minimal
	 */
	public void exportHtmlMed() {
		// Table Heading
		htmlBuilder.append("<table><tr><th>Task</th><th>Type</th><th>Start Date</th><th>End Date</th><th>Priority</th><th>Status</th><th>% Done</th><th>Est. Effort (hrs)</th><th>Description</th></tr>");

		// Table rows
		for (Task task : tasks) {
			htmlBuilder.append("<tr>");
			
			for (int j = 0; j < 9; j++) {
				
				htmlBuilder.append("<td>");
				
				switch (j) {
					case 0: // Task Name
						htmlBuilder.append(task.getText());
						break;
					case 1: // Type
						htmlBuilder.append(task.getType());
						break;
					case 2: // Start Date
						htmlBuilder.append(dateFormat.format(task.getStartDate().getDate()));
						break;
					case 3: // End Date
						htmlBuilder.append(dateFormat.format(task.getEndDate().getDate()));
						break;
					case 4: // Priority
						htmlBuilder.append(task.getPriorityString());
						break;
					case 5: // Status
						htmlBuilder.append(task.getStatusString());
						break;
					case 6: // % Done
						htmlBuilder.append(task.getProgress());
						break;
					case 7: // Est Effort
						htmlBuilder.append(task.getEffort());
						break;
					case 8: // Description
						htmlBuilder.append(task.getDescription());
						break;
					default:
						break;
				}
				
				htmlBuilder.append("</td>");
			}
			
			htmlBuilder.append("</tr>");
			
		}
	}

	
	/**
	 * For each task, only the following are reported:
	 * Name, Type, Start Date, End Date, Priority, Status, % Done, Est. Effort, Description, Analysis
	 *
	 * The formatting is compact and minimal
	 */
	public void exportHtmlMax() {
		// Table Heading
		htmlBuilder.append("<table><tr><th>Task</th><th>Type</th><th>Start Date</th><th>End Date</th><th>Priority</th><th>Status</th><th>% Done</th><th>Est. Effort (hrs)</th><th>Description</th><th>Analysis</th></tr>");

		// Table rows
		for (Task task : tasks) {
			htmlBuilder.append("<tr>");
			
			for (int j = 0; j < 10; j++) {
				
				htmlBuilder.append("<td>");
				
				switch (j) {
					case 0: // Task Name
						htmlBuilder.append(task.getText());
						break;
					case 1: // Type
						htmlBuilder.append(task.getType());
						break;
					case 2: // Start Date
						htmlBuilder.append(dateFormat.format(task.getStartDate().getDate()));
						break;
					case 3: // End Date
						htmlBuilder.append(dateFormat.format(task.getEndDate().getDate()));
						break;
					case 4: // Priority
						htmlBuilder.append(task.getPriorityString());
						break;
					case 5: // Status
						htmlBuilder.append(task.getStatusString());
						break;
					case 6: // % Done
						htmlBuilder.append(task.getProgress());
						break;
					case 7: // Est Effort
						htmlBuilder.append(task.getEffort());
						break;
					case 8: // Description
						htmlBuilder.append(task.getDescription());
						break;
					case 9: // Analysis
						switch (task.getAnalysis()) {
							case 0:
								htmlBuilder.append("You underestimated");
								break;
							case 1:
								htmlBuilder.append("Good job, your estimate was accurate");
								break;
							case 2:
								htmlBuilder.append("You overestimated");
								break;
						}
						break;
					default:
						break;
				}
				
				htmlBuilder.append("</td>");
			}
			
			htmlBuilder.append("</tr>");
			
		}
	}

}
