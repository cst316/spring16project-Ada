/**
 * DefaultTask.java
 * Created on 12.02.2003, 15:30:40 Alex
 * Package: net.sf.memoranda
 *
 * @author Alex V. Alishevskikh, alex@openmechanics.net
 * Copyright (c) 2003 Memoranda Team. http://memoranda.sf.net
 */
package net.sf.memoranda;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import java.util.Calendar;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.date.CurrentDate;
import net.sf.memoranda.util.LogPair;
import net.sf.memoranda.util.Util;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

/**
 *
 */
/*$Id: TaskImpl.java,v 1.15 2005/12/01 08:12:26 alexeya Exp $*/
public class TaskImpl implements Task, Comparable {

    private Element _element = null;
    private TaskList _tl = null;

    /**
     * Constructor for DefaultTask.
     */
    public TaskImpl(Element taskElement, TaskList tl) {
        _element = taskElement;
        _tl = tl;
    }

    public Element getContent() {
        return _element;
    }

    public CalendarDate getStartDate() {
    	CalendarDate date;
    	Process p = getProcess();
    	
    	if (p == null) {
    		date = new CalendarDate(_element.getAttribute("startDate").getValue());
    	}
    	else {
    		date = p.getStartDate();
    	}
        return date;
    }

    public void setStartDate(CalendarDate date) {
           setAttr("startDate", date.toString());
    }

    public CalendarDate getEndDate() {
    	Process p = getProcess();
    	if (p != null) {
    		return p.getEndDate();
    	}
		String ed = _element.getAttribute("endDate").getValue();
		if (ed != "")
			return new CalendarDate(_element.getAttribute("endDate").getValue());
		Task parent = this.getParentTask();
		if (parent != null)
			return parent.getEndDate();
		Project pr = this._tl.getProject();
		if (pr.getEndDate() != null)
			return pr.getEndDate();
		return this.getStartDate();
        
    }

    public void setEndDate(CalendarDate date) {
		if (date == null)
			setAttr("endDate", "");
		setAttr("endDate", date.toString());
    }

    public long getEffort() {
    	Attribute attr = _element.getAttribute("effort");
    	if (attr == null) {
    		return 0;
    	}
    	else {
    		try {
        		return Long.parseLong(attr.getValue());
    		}
    		catch (NumberFormatException e) {
    			return 0;
    		}
    	}
    }

    public void setEffort(long effort) {
        setAttr("effort", String.valueOf(effort));
    }
	
	/* 
	 * @see net.sf.memoranda.Task#getParentTask()
	 */
	public Task getParentTask() {
		Node parentNode = _element.getParent();
    	if (parentNode instanceof Element) {
    	    Element parent = (Element) parentNode;
        	if (parent.getLocalName().equalsIgnoreCase("task")) 
        	    return new TaskImpl(parent, _tl);
    	}
    	return null;
	}
	
	public String getParentId() {
		Task parent = this.getParentTask();
		if (parent != null)
			return parent.getID();
		return null;
	}

    public String getDescription() {
    	Element thisElement = _element.getFirstChildElement("description");
    	if (thisElement == null) {
    		return null;
    	}
    	else {
       		return thisElement.getValue();
    	}
    }

    public void setDescription(String s) {
    	Element desc = _element.getFirstChildElement("description");
    	if (desc == null) {
        	desc = new Element("description");
            desc.appendChild(s);
            _element.appendChild(desc);    	
    	}
    	else {
            desc.removeChildren();
            desc.appendChild(s);    	
    	}
    }
    
    public String getType() {
    	Element thisElement = _element.getFirstChildElement("type");
    	if (thisElement == null) {
    		return "";
    	}
    	else {
    		return thisElement.getValue();
    	}
    }
    
    public void setType(String s) {
    	Element type = _element.getFirstChildElement("type");
    	if (type == null) {
    		type = new Element("type");
    		type.appendChild(s);
    		_element.appendChild(type);
    	}
    	else {
    		type.removeChildren();
    		type.appendChild(s);
    	}
    }

    /**s
     * @see net.sf.memoranda.Task#getStatus()
     */
    public int getStatus(CalendarDate date) {
        CalendarDate start = getStartDate();
        CalendarDate end = getEndDate();
        if (isFrozen())
            return Task.FROZEN;
        if (isCompleted())
                return Task.COMPLETED;
        
		if (date.inPeriod(start, end)) {
            if (date.equals(end))
                return Task.DEADLINE;
            else
                return Task.ACTIVE;
        }
		else if(date.before(start)) {
				return Task.SCHEDULED;
		}
		
		if(start.after(end)) {
			return Task.ACTIVE;
		}

        return Task.FAILED;
    }
    
    public String getStatusString() {
    	String status = new String();
    	
    	switch (getStatus(new CalendarDate())) {
    		case Task.SCHEDULED:
    			status = "Scheduled";
    			break;
    		case Task.ACTIVE:
    			status = "Active";
    			break;
    		case Task.COMPLETED:
    			status = "Completed";
    			break;
    		case Task.FROZEN:
    			status = "Frozen";
    			break;
    		case Task.FAILED:
    			status = "Failed";
    			break;
    		case Task.LOCKED:
    			status = "Locked";
    			break;
    		case Task.DEADLINE:
    			status = "Deadline";
    			break;
    		default:
    			break;
    	}
    	
    	return status;
    }
    
    /**
     * Method isDependsCompleted.
     * @return boolean
     */
/*
    private boolean isDependsCompleted() {
        Vector v = (Vector) getDependsFrom();
        boolean check = true;
        for (Enumeration en = v.elements(); en.hasMoreElements();) {
            Task t = (Task) en.nextElement();
            if (t.getStatus() != Task.COMPLETED)
                check = false;
        }
        return check;
    }
*/
    private boolean isFrozen() {
        return _element.getAttribute("frozen") != null;
    }

    private boolean isCompleted() {
        return getProgress() == 100;
    }

    /**
     * @see net.sf.memoranda.Task#getID()
     */
    public String getID() {
        return _element.getAttribute("id").getValue();
    }

    /**
     * @see net.sf.memoranda.Task#getText()
     */
    public String getText() {
        return _element.getFirstChildElement("text").getValue();
    }

    public String toString() {
        return getText();
    }
    
    /**
     * @see net.sf.memoranda.Task#setText()
     */
    public void setText(String s) {
        _element.getFirstChildElement("text").removeChildren();
        _element.getFirstChildElement("text").appendChild(s);
    }

    /**
     * @see net.sf.memoranda.Task#freeze()
     */
    public void freeze() {
        setAttr("frozen", "yes");
    }

    /**
     * @see net.sf.memoranda.Task#unfreeze()
     */
    public void unfreeze() {
        if (this.isFrozen())
            _element.removeAttribute(new Attribute("frozen", "yes"));
    }

    /**
     * @see net.sf.memoranda.Task#getDependsFrom()
     */
    public Collection getDependsFrom() {
        Vector v = new Vector();
        Elements deps = _element.getChildElements("dependsFrom");
        for (int i = 0; i < deps.size(); i++) {
            String id = deps.get(i).getAttribute("idRef").getValue();
            Task t = _tl.getTask(id);
            if (t != null)
                v.add(t);
        }
        return v;
    }
    /**
     * @see net.sf.memoranda.Task#addDependsFrom(net.sf.memoranda.Task)
     */
    public void addDependsFrom(Task task) {
        Element dep = new Element("dependsFrom");
        dep.addAttribute(new Attribute("idRef", task.getID()));
        _element.appendChild(dep);
    }
    /**
     * @see net.sf.memoranda.Task#removeDependsFrom(net.sf.memoranda.Task)
     */
    public void removeDependsFrom(Task task) {
        Elements deps = _element.getChildElements("dependsFrom");
        for (int i = 0; i < deps.size(); i++) {
            String id = deps.get(i).getAttribute("idRef").getValue();
            if (id.equals(task.getID())) {
                _element.removeChild(deps.get(i));
                return;
            }
        }
    }
    /**
     * @see net.sf.memoranda.Task#getProgress()
     */
    public int getProgress() {
        return new Integer(_element.getAttribute("progress").getValue()).intValue();
    }
    /**
     * @see net.sf.memoranda.Task#setProgress(int)
     */
    public void setProgress(int p) {
        if ((p >= 0) && (p <= 100))
            setAttr("progress", new Integer(p).toString());
    }
    
    public long getLoggedTime() {
    	long loggedTime = 0;
    	
    	Element thisElement = _element.getFirstChildElement("loggedTime");
    	if (thisElement != null) {
    		Elements instances = thisElement.getChildElements();
    		
    		for (int i = 0; i < instances.size(); i++) {
    			Element instance = instances.get(i);

    			loggedTime += Long.parseLong(instance.getAttributeValue("len"));
    		}
    	}
    	
    	return loggedTime;
    }
    
    public Map<Integer, LogPair> getLoggedTimes() {
    	Map<Integer, LogPair> map = new HashMap<>();
    	
    	Element thisElement = _element.getFirstChildElement("loggedTime");
    	if (thisElement != null) {
    		Elements instances = thisElement.getChildElements();
    		
    		for (int i = 0; i < instances.size(); i++) {
    			Element instance = instances.get(i);
    			
    			map.put(i, new LogPair(instance.getAttributeValue("date"), Long.parseLong(instance.getAttributeValue("len"))));
    		}
    	}
    	
    	return map;
    }
    
    public void addLoggedTime(String date, long len) {
    	Element log = _element.getFirstChildElement("loggedTime");
    	if (log == null) {
    		log = new Element("loggedTime");
    		
    		Element instance = new Element("log");
    		instance.addAttribute(new Attribute("date", date));
    		instance.addAttribute(new Attribute("len", Long.toString(len)));
    		
    		log.appendChild(instance);
    		_element.appendChild(log);
    	}
    	else {
    		Element instance = new Element("log");
    		instance.addAttribute(new Attribute("date", date));
    		instance.addAttribute(new Attribute("len", Long.toString(len)));
    		
    		log.appendChild(instance);
    	}
    }
    
    public void editLoggedTime(int index, String date, long len) {
    	Element log = _element.getFirstChildElement("loggedTime");
		Elements instances = log.getChildElements();
    	if (log != null) {
    		Element instance = instances.get(index);
    		instance.removeAttribute(instance.getAttribute("date"));
    		instance.removeAttribute(instance.getAttribute("len"));
    		
    		instance.addAttribute(new Attribute("date", date));
    		instance.addAttribute(new Attribute("len", Long.toString(len)));
    	}
    }
    
    public boolean removeLoggedTime(int index) {
    	Element log = _element.getFirstChildElement("loggedTime");
    	if (log != null) {
    		log.removeChild(index);
    		
    		return true;
    	}
    	
    	return false;
    }
    
    
    /**
     * @see net.sf.memoranda.Task#getPriority()
     */
    public int getPriority() {
        Attribute pa = _element.getAttribute("priority");
        if (pa == null)
            return Task.PRIORITY_NORMAL;
        return new Integer(pa.getValue()).intValue();
    }
    
    public String getPriorityString() {
    	String priority = new String();
    	
    	switch (getPriority()) {
    		case Task.PRIORITY_LOWEST:
    			priority = "Lowest";
    			break;
    		case Task.PRIORITY_LOW:
    			priority = "Low";
    			break;
    		case Task.PRIORITY_NORMAL:
    			priority = "Normal";
    			break;
    		case Task.PRIORITY_HIGH:
    			priority = "High";
    			break;
    		case Task.PRIORITY_HIGHEST:
    			priority = "Highest";
    			break;
    		default:
    			break;
    	}
    	
    	return priority;
    }
    
    /**
     * @see net.sf.memoranda.Task#setPriority(int)
     */
    public void setPriority(int p) {
        setAttr("priority", String.valueOf(p));
    }

    private void setAttr(String a, String value) {
        Attribute attr = _element.getAttribute(a);
        if (attr == null)
           _element.addAttribute(new Attribute(a, value));
        else
            attr.setValue(value);
    }

	/**
	 * A "Task rate" is an informal index of importance of the task
	 * considering priority, number of days to deadline and current 
	 * progress. 
	 * 
	 * rate = (100-progress) / (numOfDays+1) * (priority+1)
	 * @param CalendarDate
	 * @return long
	 */

	private long calcTaskRate(CalendarDate d) {
		Calendar endDateCal = getEndDate().getCalendar();
		Calendar dateCal = d.getCalendar();
		int numOfDays = (endDateCal.get(Calendar.YEAR)*365 + endDateCal.get(Calendar.DAY_OF_YEAR)) - 
						(dateCal.get(Calendar.YEAR)*365 + dateCal.get(Calendar.DAY_OF_YEAR));
		if (numOfDays < 0) return -1; //Something wrong ?
		return (100-getProgress()) / (numOfDays+1) * (getPriority()+1);
	}

    /**
     * @see net.sf.memoranda.Task#getRate()
     */
	 
     public long getRate() {
/*	   Task t = (Task)task;
	   switch (mode) {
		   case BY_IMP_RATE: return -1*calcTaskRate(t, date);
		   case BY_END_DATE: return t.getEndDate().getDate().getTime();
		   case BY_PRIORITY: return 5-t.getPriority();
		   case BY_COMPLETION: return 100-t.getProgress();
	   }
       return -1;
*/
		return -1*calcTaskRate(CurrentDate.get());
	 }
	   
	 /*
	  * Comparable interface
	  */
	  
	 public int compareTo(Object o) {
		 Task task = (Task) o;
		 	if(getRate() > task.getRate())
				return 1;
			else if(getRate() < task.getRate())
				return -1;
			else 
				return 0;
	 }
	 
	 public boolean equals(Object o) {
	     return ((o instanceof Task) && (((Task)o).getID().equals(this.getID())));
	 }

	/* 
	 * @see net.sf.memoranda.Task#getSubTasks()
	 */
	public Collection getSubTasks() {
		Elements subTasks = _element.getChildElements("task");
            return convertToTaskObjects(subTasks);
	}

	private Collection convertToTaskObjects(Elements tasks) {
        Vector v = new Vector();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = new TaskImpl(tasks.get(i), _tl);
            v.add(t);
        }
        return v;
    }
	
	/* 
	 * @see net.sf.memoranda.Task#getSubTask(java.lang.String)
	 */
	public Task getSubTask(String id) {
		Elements subTasks = _element.getChildElements("task");
		for (int i = 0; i < subTasks.size(); i++) {
			if (subTasks.get(i).getAttribute("id").getValue().equals(id))
				return new TaskImpl(subTasks.get(i), _tl);
		}
		return null;
	}

	/* 
	 * @see net.sf.memoranda.Task#hasSubTasks()
	 */
	public boolean hasSubTasks(String id) {
		Elements subTasks = _element.getChildElements("task");
		for (int i = 0; i < subTasks.size(); i++) 
			if (subTasks.get(i).getAttribute("id").getValue().equals(id))
				return true;
		return false;
	}
	
	/**
	 * Analysis is conducted by comparing the estimated effort (hours)
	 * against the amount of days it took to complete the task (measured 8hrs/day)
	 * If a task is completed more than a day (8+ hours) behind schedule, it is considered underestimation
	 * If a task is completed within one work day, it is considered an accurate estimation
	 * If a task is completed more than a day (8+ hours) ahead of schedule, it is an overestimation
	 */
	public int getAnalysis() {
		long effort = getEffort();
		
		CalendarDate endCalDate = getEndDate();
		Date endDate = CalendarDate.toDate(endCalDate.getDay(), endCalDate.getMonth(), endCalDate.getYear());
		
		CalendarDate startCalDate = getStartDate();
		Date startDate = CalendarDate.toDate(startCalDate.getDay(), startCalDate.getMonth(), startCalDate.getYear());
		
		int diffInHours = (int)( (endDate.getTime() - startDate.getTime()) 
                / (1000 * 60 * 60) );
		
		if (effort <= diffInHours) {
			if (diffInHours - effort <= 8) {
				return ANALYSIS_ACCURATE;
			} else {
				return ANALYSIS_UNDERESTIMATED;
			}
		} else {
			return ANALYSIS_OVERESTIMATED;
		}
	}

	public Process getProcess() {
		Attribute a = _element.getAttribute("process");
		Process p = null;
		
		if (a != null) {
			p = CurrentProject.getProcessList().getProcess(a.getValue());
		}
		else {
			Task parent = this.getParentTask();
			if (parent != null) {
			p = parent.getProcess();	
			}
		}
		return p;
	}

	/* (non-Javadoc)
	 * @see net.sf.memoranda.Task#getProcessWeight()
	 */
	@Override
	public int getProcessWeight() {
		int weight = 0;
		Attribute a = _element.getAttribute("process_weight");
		
		if (a != null) {
			weight = Integer.parseInt(a.getValue());
		}
		
		return weight;
	}
}
