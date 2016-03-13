package net.sf.memoranda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Util;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class ProcessListImpl implements ProcessList {

	private Project project;
	private Document document;
	private Element root;
	private Hashtable<String, Element> elements;
	
	public ProcessListImpl(Project project) {
		this.root = new Element("processlist");
		this.document = new Document(root);
		elements = new Hashtable<String, Element>();
		this.project = project;
	}
	
	public ProcessListImpl(Project project, Document document) {
		this.project = project;
		this.document = document;
		this.root = document.getRootElement();
		elements = new Hashtable<String, Element>();
		buildElements(root);
	}
	
	@Override
	public Process createProcess(String name, CalendarDate startDate, CalendarDate endDate) {
		Element e = new Element("process");
		String id = Util.generateId();
		Process p = new ProcessImpl(e, this);
		
		e.addAttribute(new Attribute("id", id));
		p.setName(name);
		p.setStartDate(startDate);
		p.setEndDate(endDate);
		
		root.appendChild(e);
		elements.put(id, e);
		
		return p;
	}

	@Override
	public Process getProcess(String id) {
		Process p = null;
		Element e = elements.get(id);
		
		if (e != null) {
			p = new ProcessImpl(e, this);
		}
		
		return p;
	}

	@Override
	public boolean removeProcess(String id) {
		boolean removed = false;
		Element e = elements.get(id);
		
		if (e != null) {
			root.removeChild(e);
			elements.remove(id);
			removed = true;
		}
		
		return removed;
	}

	@Override
	public Collection<Process> getActiveProcesses(CalendarDate date) {
		ArrayList<Process> processes = new ArrayList<Process>();
		Elements elements = root.getChildElements("process");
		
		for (int i=0; i<elements.size(); i++) {
			Process p = new ProcessImpl(elements.get(i), this);
			
			if (p.getProgress() < 100 && !date.before(p.getStartDate())) {
				processes.add(new ProcessImpl(elements.get(i), this));
			}
		}
		
		return processes;
	}

	@Override
	public Collection<Process> getAllProcesses() {
		ArrayList<Process> processes = new ArrayList<Process>();
		Elements elements = root.getChildElements("process");
		
		for (int i=0; i<elements.size(); i++) {
			processes.add(new ProcessImpl(elements.get(i), this));
		}
		
		return processes;
	}

	private void buildElements(Element root) {
		Elements elements = root.getChildElements("process");
		
		for (int i=0; i<elements.size(); i++) {
			Element e = elements.get(i);
			this.elements.put(e.getAttribute("id").getValue(), e);
		}
	}

	@Override
	public Document getXMLContent() {
		return document;
	}
}
