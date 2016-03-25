package net.sf.memoranda;

import java.util.Collection;
import java.util.Comparator;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Provides methods for searching the content in Memoranda.
 * @author James Smith
 *
 */
public interface Search {
	
	/**
	 * Search tasks for a given search string.
	 * @param searchString	String to find
	 * @param taskList		TaskList to search
	 * @return				Ordered by start date collection of Tasks
	 */
	static Collection<Task> searchTasks(String searchString, TaskList taskList) {
		Stack<Task> searchStack = new Stack<Task>();
		TreeSet<Task> tasks = new TreeSet<Task>(new Comparator<Task>() {

			@Override
			public int compare(Task t1, Task t2) {
				return t1.getStartDate().getDate().compareTo(
						t2.getStartDate().getDate());
			}	
		});
		
		searchStack.addAll(taskList.getTopLevelTasks());
		
		while (!searchStack.empty()) {
			Task task = searchStack.pop();
			searchStack.addAll(taskList.getAllSubTasks(task.getID()));
			
			if (task.getDescription().contains(searchString)
					|| task.getText().contains(searchString)
					|| task.getType().contains(searchString)) {
				
				tasks.add(task);
			}
		}
		
		return tasks;
	}
	
	/**
	 * Search templates for a given search string.
	 * @param searchString	String to find
	 * @param templateList	TemplateList to search
	 * @return				Ordered by title collection of Templates
	 */
	static Collection<Template> searchTemplates(
			String searchString,
			TemplateList templateList) {
		
		TreeSet<Template> templates = new TreeSet<Template>(new Comparator<Template>() {

			@Override
			public int compare(Template t1, Template t2) {
				return t1.getTitle().compareTo(t2.getTitle());
			}
		});
		
		for (String id : templateList.getIds()) {
			Template template = templateList.getTemplate(id);
			
			if (template.getDescription().contains(searchString)
					|| template.getTitle().contains(searchString)
					|| template.getType().contains(searchString)) {
				
				templates.add(template);
			}
		}
		
		return templates;
	}
	
	/**
	 * Search processes for a given string.
	 * @param searchString	String to find
	 * @param processList	ProcessList to search
	 * @return				Ordered by start date collection of Processes
	 */
	static Collection<Process> searchProcesses(String searchString, ProcessList processList) {
		TreeSet<Process> processes = new TreeSet<Process>(new Comparator<Process>() {

			@Override
			public int compare(Process p1, Process p2) {
				return p1.getStartDate().getDate().compareTo(
						p2.getStartDate().getDate());
			}
		});
		
		for (Process process : processList.getAllProcesses()) {
			if (process.getName().contains(searchString)) {
				processes.add(process);
			}
		}
		
		return processes;
	}
	
	/**
	 * Search notes for a given string.
	 * @param searchString	String to find
	 * @param noteList		NoteList to search
	 * @return				Ordered by date collection of Notes
	 */
	static Collection<Note> searchNotes(String searchString, NoteList noteList) {
		TreeSet<Note> notes = new TreeSet<Note>(new Comparator<Note>() {

			@Override
			public int compare(Note n1, Note n2) {
				return n1.getDate().getDate().compareTo(n2.getDate().getDate());
			}
		});
		
		for (Object object : noteList.getAllNotes()) {
			if (object instanceof Note) {
				Note note = (Note) object;
				
				if (note.getTitle().contains(searchString)) {
					notes.add(note);
				}
			}
		}
		
		return notes;
	}
}