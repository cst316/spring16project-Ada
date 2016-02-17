package net.sf.memoranda;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Hashtable;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Util;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class TemplateListImpl implements TemplateList {
	
	private Project _project = null;
	private Document _doc = null;
	private Element _root = null;
	
	private Hashtable<String, Element> elements = new Hashtable<>();
	
	/**
	 * Builds hashtable of "template" XOM elements recursively
	 * @param parent the parent XOM element
	 */
	private void buildElements(Element parent) {
		Elements els = parent.getChildElements("template");
		for (int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			elements.put(el.getAttribute("id").getValue(), el);
			buildElements(el);
		}
	}
	
	private Element getTemplateElement(String id) {
		Element el = (Element)elements.get(id);
		if (el == null) {
			Util.debug("getTemplateElement(" + id + "): ID is null!");
		}
		return el;
	}
	
	public TemplateListImpl(Document doc, Project proj) {
		_doc = doc;
		_root = _doc.getRootElement();
		_project = proj;
		buildElements(_root);
	}
	
	public TemplateListImpl(Project proj) {
		_root = new Element("templatelist");
		_doc = new Document(_root);
		_project = proj;
	}
	
	public Project getProject() {
		return _project;
	}
	
	public Template getTemplate(String id) {
		Util.debug("TemplateListImpl.getTemplate(" + id + ")...");
		return new TemplateImpl(getTemplateElement(id), this);
	}
	
	/**
	 * Creates Template with unique ID and saves it to storage
	 */
	public Template createTemplate(CalendarDate startDate, CalendarDate endDate, String title, String type, int priority, long effort, String description) {
		int days;
		int months;
		int years;
		
		days = endDate.getDay() - startDate.getDay();
		months = endDate.getMonth() - startDate.getMonth();
		years = endDate.getYear() - startDate.getYear();
		
		Element el = new Element("template");
		el.addAttribute(new Attribute("day_difference", Integer.toString(days)));
		el.addAttribute(new Attribute("month_difference", Integer.toString(months)));
		el.addAttribute(new Attribute("year_difference", Integer.toString(years)));
		
		String id = Util.generateId();
		el.addAttribute(new Attribute("id", id));
		
		el.addAttribute(new Attribute("effort", Long.toString(effort)));
		el.addAttribute(new Attribute("priority", Integer.toString(priority)));
		
		Element _title = new Element("title");
		_title.appendChild(title);
		el.appendChild(_title);
		
		Element _type = new Element("type");
		_type.appendChild(type);
		el.appendChild(_type);
		
		Element _description = new Element("description");
		_description.appendChild(description);
		el.appendChild(_description);
		
		_root.appendChild(el);
		
		elements.put(id, el);
		
		Util.debug("Created Template with ID: " + id);
		
		return new TemplateImpl(el, this);
	}
	
	/**
	 * Removes Template from project and erases it from storage
	 */
	/*
	public void removeTemplate(Template template) {
		_root.removeChild(template.getContent());
		elements.remove(template.getId());
	}
	*/
	/**
	 * Gets all IDs associated with Templates
	 * 
	 * @return collection of all Template IDs
	 */
	public Collection<String> getIds() {
		Collection<String> ids = new ArrayList<String>();
		Deque<Element> templateStack = new ArrayDeque<Element>();
		templateStack.push(_root);
		
		while (templateStack.peek() != null) {
			Element current = templateStack.pop();
			Elements children = current.getChildElements("template");
			
			Template template = new TemplateImpl(current, this);
			
			try {
				String id = template.getId();
				ids.add(id);
			} catch (Exception e) {
				Util.debug("Error in TemplateListImpl: " + e.getMessage());
			}
			
			for (int i = 0; i < children.size(); i++) {
				templateStack.push(children.get(i));
			}
		}
		
		return ids;
	}
	
	public Document getXmlContent() {
		return _doc;
	}
}
