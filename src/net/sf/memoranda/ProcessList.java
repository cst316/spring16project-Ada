package net.sf.memoranda;

import java.util.Collection;

import net.sf.memoranda.date.CalendarDate;
import nu.xom.Document;

public interface ProcessList {

	public Process createProcess(String name, CalendarDate startDate, CalendarDate endDate);
	public Process getProcess(String id);
	public boolean removeProcess(String id);
	public Collection<Process> getActiveProcesses(CalendarDate date);
	public Collection<Process> getAllProcesses();
	public Document getXMLContent();
	
    
    /**
     * Gets all the Processes that occur on a date.
     * @param date The date on which Processes occur
     * @return Collection of Processes
     */
    Collection<Process> getProcessesByDate(CalendarDate date);
}