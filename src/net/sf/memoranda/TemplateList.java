package net.sf.memoranda;

import java.util.Collection;

import net.sf.memoranda.date.CalendarDate;

public interface TemplateList {
	
	Project getProject();
	Template getTemplate(String id);
	
	Template createTemplate(CalendarDate startDate, CalendarDate endDate, String title, String type, int priority, long effort, String description);
	
	void removeTemplate(Template template);
	
	public Collection<String> getIds();
	
	nu.xom.Document getXmlContent();
}
