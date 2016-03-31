/**
 * Task.java
 * Created on 11.02.2003, 16:39:13 Alex
 * Package: net.sf.memoranda
 * 
 * @author Alex V. Alishevskikh, alex@openmechanics.net
 * Copyright (c) 2003 Memoranda Team. http://memoranda.sf.net
 */
package net.sf.memoranda;

import java.util.Collection;
import java.util.List;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.LogPair;

/**
 * 
 */
/*$Id: Task.java,v 1.9 2005/06/16 04:21:32 alexeya Exp $*/
public interface Task {
    
    public static final int SCHEDULED = 0;
    public static final int ACTIVE = 1;
    public static final int COMPLETED = 2;
    public static final int FROZEN = 4;
    public static final int FAILED = 5;
    public static final int LOCKED = 6;
    public static final int DEADLINE = 7;
    
    public static final int PRIORITY_LOWEST = 0;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_HIGHEST = 4;
    
    public static final int ANALYSIS_UNDERESTIMATED = 0;
    public static final int ANALYSIS_ACCURATE = 1;
    public static final int ANALYSIS_OVERESTIMATED = 2;
    public static final int ANALYSIS_UNKNOWN = 3;
    
    CalendarDate getStartDate();
    void setStartDate(CalendarDate date);

    CalendarDate getEndDate();
    void setEndDate(CalendarDate date);
    
    int getStatus(CalendarDate date);
    String getStatusString();
    
    int getProgress();
    void setProgress(int p);
    
    /**
     * Calculates total logged time for all logged instances in the Task.
     * @return Logged time in milliseconds
     */
    long getLoggedTime();
    
    /**
     * Gets all instances of logged times for the Task.
     * @return A List of LogPairs of Dates (String) and Times (Long)
     */
    List<LogPair> getLoggedTimes();
    
    /**
     * Appends a logged time to the Task.
     * @param date The date of the log
     * @param len The amount of time spent
     */
    void addLoggedTime(String date, long len);
    
    /**
     * Edits a logged time at a specific index.
     * @param index The index of the logged time within the Task
     * @param date The new value for the date
     * @param len The new value for the amount of time spent
     */
    void editLoggedTime(int index, String date, long len);
    
    /**
     * Removes a logged time index from the Task.
     * @param index The index of the logged time within the Task
     * @return If deletion was successful
     */
    boolean removeLoggedTime(int index);
    
    int getPriority();
    void setPriority(int p);
    String getPriorityString();
    
    public Process getProcess();
    
    /**
     * Used to determine the order of tasks in a process. Lower weight tasks
     * are to be done before higher weight tasks.
     * @return	int
     */
    public int getProcessWeight();
    
    String getID();
    
    String getText();
    void setText(String s);
    
    /*Collection getDependsFrom();
    
    void addDependsFrom(Task task);
    
    void removeDependsFrom(Task task);*/
            
    Collection<Task> getSubTasks();    
    Task getSubTask(String id);
    
    boolean hasSubTasks(String id);
    
    void setEffort(long effort);
    long getEffort();
    
    void setDescription(String description);
    String getDescription();
    
    void setType(String type);
    String getType();
    
    int getAccuracy();

    Task getParentTask();
    String getParentId();
    
    void freeze();
    void unfreeze();
	long getRate();
    
    nu.xom.Element getContent();
}
