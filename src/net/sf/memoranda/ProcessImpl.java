package net.sf.memoranda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Util;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class ProcessImpl implements Process {

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
		ArrayList<Task> tasks = new ArrayList<Task>();
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
