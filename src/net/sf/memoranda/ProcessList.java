package net.sf.memoranda;

import java.util.Collection;

import net.sf.memoranda.date.CalendarDate;
import nu.xom.Document;

public interface ProcessList {

	public Process createProcess(String name);
	public Process getProcess(String id);
	public boolean removeProcess(String id);
	public Collection<Process> getActiveProcesses();
	public Collection<Process> getAllProcesses();
	public Document getXMLContent();
}