package net.sf.memoranda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Util;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class ProcessImpl implements Process {

	public static final Comparator<Task> TASK_COMPARATOR = new Comparator<Task>() {

		@Override
		public int compare(Task t1, Task t2) {
			int difference = t1.getProcessWeight() - t2.getProcessWeight();
			
			if (difference == 0) {
				difference = t1.getText().compareTo(t2.getText());
				
				if (difference == 0) {
					difference = t1.getID().compareTo(t2.getID());
				}
			}
			
			return difference;
		}
	};
	
    private Element element;
    private ProcessList pl;
    
	public ProcessImpl(Element processElement, ProcessList pl) {
		this.element = processElement;
		this.pl = pl;
	}
	
	@Override
	public String getID() {
		return element.getAttribute("id").getValue();
	}
	
	@Override
	public String getName() {
		return element.getAttributeValue("name");
	}

	@Override
	public void setName(String name) {
		if (name != null) {
			setAttr("name", name);
		}
	}

	@Override
	public int getProgress() {
		int progress = 0;
		long progressSum = 0;
		long effortSum = 0;
		Collection<Task> tasks = getTasks();
		
		for (Task t : tasks) {
			progressSum += (t.getEffort() * t.getProgress()) / 100L;
			effortSum += t.getEffort();
		}
		
		if (effortSum != 0) {
			progress = (int) ((100 * progressSum) / effortSum);
		}
		
		return progress;
	}

	@Override
	public CalendarDate getStartDate() {
		Attribute attr = element.getAttribute("startdate");
		CalendarDate date = null;
		
		if (attr != null) {
			date = new CalendarDate(attr.getValue());
		}
		return date;
	}

	@Override
	public boolean setStartDate(CalendarDate date) {
		boolean isValid = false;
		CalendarDate endDate = this.getEndDate();
		
		if (date != null && (endDate == null || !date.after(endDate))) {
			setAttr("startdate", date.toString());
			isValid = true;
		}
		
		return isValid;
	}

	@Override
	public CalendarDate getEndDate() {
		Attribute attr = element.getAttribute("enddate");
		CalendarDate date = null;
		
		if (attr != null) {
			date = new CalendarDate(attr.getValue());
		}
		return date;
	}

	@Override
	public boolean setEndDate(CalendarDate date) {
		boolean isValid = false;
		CalendarDate startDate = this.getStartDate();
		
		if (date != null && (startDate == null || !date.before(startDate))) {
			setAttr("enddate", date.toString());
			isValid = true;
		}
		
		return isValid;
	}

	@Override
	public boolean addTask(String id) {
		Util.debug("Getting Task ID: " + id + "...");
		
		boolean isTaskAdded = false;
		
		Task task = CurrentProject.getTaskList().getTask(id);
		
		if (!hasTask(id) && task != null) {
			Util.debug("Found Task!");
			
			Element child = new Element("task");
			child.addAttribute(new Attribute("id", id));
			element.appendChild(child);
			
			setTaskProcess(task);
			
			isTaskAdded = true;
		}
		
		Util.debug("Added? " + Boolean.toString(isTaskAdded));
		
		return isTaskAdded;
	}
	
	@Override
	public boolean hasTask(String id) {
		boolean hasTask = false;
		Elements tasks = element.getChildElements("task");
		
		for  (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).getAttribute("id").getValue().equals(id)) {
				hasTask = true;
			}
		}
		
		return hasTask;
	}

	@Override
	public boolean removeTask(String id) {
		boolean isTaskRemoved = false;
		Elements tasks = element.getChildElements("task");
		
		for (int i=0; i<tasks.size(); i++) {
			Element task = tasks.get(i);
			if (task.getAttributeValue("id").equals(id)) {
				element.removeChild(task);
				
				removeTaskProcess(CurrentProject.getTaskList().getTask(task.getAttributeValue("id")));
				
				isTaskRemoved = true;
			}
		}
		
		return isTaskRemoved;
	}

	@Override
	public Collection<Task> getTasks() {
		Elements elements = element.getChildElements("task");
		TreeSet<Task> tasks = new TreeSet<Task>(TASK_COMPARATOR);
		TaskList tl = CurrentProject.getTaskList();
		
		for (int i=0; i<elements.size(); i++) {
			tasks.add(tl.getTask(elements.get(i).getAttributeValue("id")));
		}
		
		return tasks;
	}
	
    @Override
	public Collection<Task> getActiveTasks(CalendarDate date) {
		return filterActiveTasks(getTasks(), date);
	}

	/* (non-Javadoc)
	 * @see net.sf.memoranda.Process#setTaskOrder(java.lang.String[])
	 */
	@Override
	public boolean setTaskOrder(String[] ids) {
		boolean isValid = false;
		Collection<Task> tasks = this.getTasks();
		// stores each id as it is checked to verify there are no duplicates
		TreeSet<String> checkedIds = new TreeSet<String>();
		
		TaskList taskList = CurrentProject.getTaskList();
		
		// validate the array contains all tasks and all tasks are in the process
		if (ids.length == tasks.size()) {
			isValid = true;
			
			for (int i=0; i<ids.length && isValid; i++) {
				Task t = null;
				Process p = null;
				
				if (!checkedIds.contains(ids[i])) {
					t = taskList.getTask(ids[i]);
					p = t.getProcess();
				}
				
				if (t != null && p != null && taskList.getTask(ids[i]).getProcess().equals(this)) {					
					checkedIds.add(ids[i]);
				}
				else {
					isValid = false;
				}
			}
		}
		
		// validate the final size of the new set then apply the changes
		if (isValid && checkedIds.size() == tasks.size()) {
			for (int i=0; i<ids.length; i++) {
				Element e = taskList.getTask(ids[i]).getContent();
				Attribute a = e.getAttribute("process_weight");
				
				if (a == null) {
					e.addAttribute(new Attribute("process_weight", i + ""));
				}
				else {
					a.setValue(i + "");
				}
			}
		}
		
		return isValid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean equals;
		
		if (obj instanceof Process) {
			Process p = (Process) obj;
			
			equals = this.getID().equals(p.getID());
		}
		else {
			equals = false;
		}
		
		return equals;
	}

	private void setAttr(String a, String value) {
        Attribute attr = element.getAttribute(a);
        if (attr == null)
           element.addAttribute(new Attribute(a, value));
        else
            attr.setValue(value);
    }
    
    private void setTaskProcess(Task t) {
    	Element e = t.getContent();
    	Attribute a = e.getAttribute("process");
    	
    	if (a != null) {
    		pl.getProcess(a.getValue()).removeTask(t.getID());
    	}
    	e.addAttribute(new Attribute("process", this.getID()));
    }
    
    private void removeTaskProcess(Task t) {
    	Element e = t.getContent();
    	Attribute a = e.getAttribute("process");
    	
    	if (a != null) {
    		e.removeAttribute(a);
    	}
    }
    
    /**
     * Code copied from TaskListImpl.java on 2/19/2016
     */
    private Collection<Task> filterActiveTasks(Collection<Task> tasks,CalendarDate date) {
        Vector<Task> v = new Vector<>();
        for (Iterator<Task> iter = tasks.iterator(); iter.hasNext();) {
            Task t = (Task) iter.next();
            if(isActive(t,date)) {
                v.add(t);
            }
        }
        return v;
    }

    /**
     * Code copied from TaskListImpl.java on 2/19/2016
     */
    private boolean isActive(Task t,CalendarDate date) {
    	if ((t.getStatus(date) == Task.ACTIVE) || (t.getStatus(date) == Task.DEADLINE) || (t.getStatus(date) == Task.FAILED)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
