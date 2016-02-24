package net.sf.memoranda;

import java.util.Collection;

import net.sf.memoranda.date.CalendarDate;

/**
 * A process is a linear sequence of tasks to be performed.
 */
public interface Process {

	public String getID();
	public String getName();
	public void setName(String name);
	public int getProgress();
	public CalendarDate getStartDate();
	public boolean setStartDate(CalendarDate date);
	public CalendarDate getEndDate();
	public boolean setEndDate(CalendarDate date);
	public boolean addTask(String id);
	public boolean hasTask(String id);
	public boolean removeTask(String id);
	public Collection<Task> getTasks();
	public Collection<Task> getActiveTasks(CalendarDate date);
}
