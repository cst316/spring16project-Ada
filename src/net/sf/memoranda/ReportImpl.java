package net.sf.memoranda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import net.sf.memoranda.util.FileStorage;
import net.sf.memoranda.util.LogPair;
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
				"th, td {padding: 10px;}" +
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
		htmlBuilder.append("<br>Report generated on: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date()) + "<br>");
		
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
	 * Name, Type, Start Date, End Date, Priority, Status, Actual Effort.
	 * The formatting is compact and minimal
	 */
	public void exportHtmlMin() {
		// Table Heading
		htmlBuilder.append("<table><tr><th>Task</th><th>Type</th><th>Start Date</th><th>End Date</th><th>Priority</th><th>Status</th><th>Actual Effort (hrs)</th></tr>");

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
					case 6: // Actual Effort
						htmlBuilder.append((float)task.getLoggedTime() / 3600000.0f);
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
		htmlBuilder.append("<table><tr><th>Task</th><th>Type</th><th>Description</th><th>Start Date</th><th>End Date</th><th>Priority</th><th>Status</th><th>% Done</th><th>Est. Effort (hrs)</th><th>Actual Effort (hrs)</th></tr>");

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
					case 2: // Description
						htmlBuilder.append(task.getDescription());
						break;
					case 3: // Start Date
						htmlBuilder.append(dateFormat.format(task.getStartDate().getDate()));
						break;
					case 4: // End Date
						htmlBuilder.append(dateFormat.format(task.getEndDate().getDate()));
						break;
					case 5: // Priority
						htmlBuilder.append(task.getPriorityString());
						break;
					case 6: // Status
						htmlBuilder.append(task.getStatusString());
						break;
					case 7: // % Done
						htmlBuilder.append(task.getProgress());
						break;
					case 8: // Est Effort
						htmlBuilder.append((float)task.getEffort() / 3600000.0f);
						break;
					case 9: // Actual Effort
						htmlBuilder.append((float)task.getLoggedTime() / 3600000.0f);
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
		htmlBuilder.append("<table><tr><th>Task</th><th>Type</th><th>Description</th><th>Start Date</th><th>End Date</th><th>Priority</th><th>Status</th><th>% Done</th><th>Est. Effort (hrs)</th><th>Actual Effort (hrs)</th><th>Accuracy</th></tr>");

		// Table rows
		for (Task task : tasks) {
			htmlBuilder.append("<tr>");
			
			for (int j = 0; j < 11; j++) {
				
				htmlBuilder.append("<td>");
				
				switch (j) {
					case 0: // Task Name
						htmlBuilder.append(task.getText());
						break;
					case 1: // Type
						htmlBuilder.append(task.getType());
						break;
					case 2: // Description
						htmlBuilder.append(task.getDescription());
						break;
					case 3: // Start Date
						htmlBuilder.append(dateFormat.format(task.getStartDate().getDate()));
						break;
					case 4: // End Date
						htmlBuilder.append(dateFormat.format(task.getEndDate().getDate()));
						break;
					case 5: // Priority
						htmlBuilder.append(task.getPriorityString());
						break;
					case 6: // Status
						htmlBuilder.append(task.getStatusString());
						break;
					case 7: // % Done
						htmlBuilder.append(task.getProgress());
						break;
					case 8: // Est Effort
						htmlBuilder.append((float)task.getEffort() / 3600000.0f);
						break;
					case 9: // Actual Effort
						htmlBuilder.append((float)task.getLoggedTime() / 3600000.0f);
						break;
					case 10: // Analysis
						switch (task.getAccuracy()) {
							case 0:
								htmlBuilder.append("Underestimated");
								break;
							case 1:
								htmlBuilder.append("Accurate");
								break;
							case 2:
								htmlBuilder.append("Overestimated");
								break;
							case 3:
								htmlBuilder.append("N/A");
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
		/*
		if (task.getLoggedTimes().size() > 0) {
			// Time logs
			htmlBuilder.append("<tr><th>Time logs</th></tr>");
		}
		*/
		
		// Close this table so we can make new one for time logs
		htmlBuilder.append("</table><br>");
		
		// Header
		htmlBuilder.append("<table><tr><th align=\"center\" colspan=3>Time Logs</th></tr>");
		htmlBuilder.append("<tr><th>Task</th><th>Date</th><th>Length (hrs)</th></tr>");
		
		for (Task task : tasks) {
			int count = 0;
			
			Map<Integer, LogPair> map = task.getLoggedTimes();
			
			for (LogPair lp : map.values()) {
				if (count == 0) {
					htmlBuilder.append("<tr><td>" + task.getText() + "</td><td>" + lp.getDate() + "</td><td>" + (lp.getLength() / 3600000.0f) + "</td></tr>");
				} else {
					htmlBuilder.append("<tr><td></td><td>" + lp.getDate() + "</td><td>" + (lp.getLength() / 3600000.0f) + "</td></tr>");
				}
				
				count++;
			}
		}
	}

}
