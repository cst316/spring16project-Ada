/**
 * TaskList.java
 * Created on 21.02.2003, 12:25:16 Alex
 * Package: net.sf.memoranda
 * 
 * @author Alex V. Alishevskikh, alex@openmechanics.net
 * Copyright (c) 2003 Memoranda Team. http://memoranda.sf.net
 */
package net.sf.memoranda;
import java.util.Collection;

import net.sf.memoranda.date.CalendarDate;
/**
 * 
 */
/*$Id: TaskList.java,v 1.8 2005/12/01 08:12:26 alexeya Exp $*/
public interface TaskList {

	Project getProject();
    Task getTask(String id);

    Task createTask(CalendarDate startDate, CalendarDate endDate, String text, String type, int priority, long effort, String description, String parentTaskId);

    void removeTask(Task task);

    public boolean hasSubTasks(String id);
    
	public boolean hasParentTask(String id);

	public Collection<Task> getTopLevelTasks();
	public Collection<Task> getTopLevelNoProcessTasks();
	public Collection<Task> getActiveTopLevelNoProcessTasks(CalendarDate date);
	
    public Collection<Task> getAllSubTasks(String taskId);
    public Collection<Task> getActiveSubTasks(String taskId,CalendarDate date);
    
    public Collection<String> getIds();
    public Collection<String> getTaskTypes();
    
    /**
     * Gets all the top-level Tasks that are active on a date.
     * @param date The date on which Tasks are active
     * @return Collection of active Tasks
     */
    Collection<Task> getTasksByDate(CalendarDate date);
    
//    public void adjustParentTasks(Task t);
    
    public long calculateTotalEffortFromSubTasks(Task t);
    public CalendarDate getLatestEndDateFromSubTasks(Task t);
    public CalendarDate getEarliestStartDateFromSubTasks(Task t);
    public long[] calculateCompletionFromSubTasks(Task t);

    nu.xom.Document getXMLContent();

}
