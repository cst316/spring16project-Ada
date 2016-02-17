package net.sf.memoranda;

import net.sf.memoranda.date.CalendarDate;
import nu.xom.Attribute;
import nu.xom.Element;

public class TemplateImpl implements Template {
	private Element _element = null;
	private TemplateList _tl = null;

    private void setAttr(String a, String value) {
        Attribute attr = _element.getAttribute(a);
        if (attr == null)
           _element.addAttribute(new Attribute(a, value));
        else
            attr.setValue(value);
    }
	
	public TemplateImpl(Element templateElement, TemplateList tl) {
		_element = templateElement;
		_tl = tl;
	}
	
	public int[] getDateDifference() {
		int[] diff = new int[3];
		
		diff[0] = Integer.parseInt(_element.getAttribute("day_difference").getValue());
		diff[1] = Integer.parseInt(_element.getAttribute("month_difference").getValue());
		diff[2] = Integer.parseInt(_element.getAttribute("year_difference").getValue());
		
		return diff;
	}
	
	public void setDateDifference(CalendarDate startDate, CalendarDate endDate) {
		int days;
		int months;
		int years;
		
		days = endDate.getDay() - startDate.getDay();
		months = endDate.getMonth() - startDate.getMonth();
		years = endDate.getYear() - startDate.getYear();

		setAttr("day_difference", Integer.toString(days));
		setAttr("month_difference", Integer.toString(months));
		setAttr("year_difference", Integer.toString(years));
	}
	
	public String getId() {
		return _element.getAttribute("id").getValue();
	}
	
	public String getTitle() {
		return _element.getFirstChildElement("title").getValue();
	}
	
	public void setTitle(String title) {
		_element.getFirstChildElement("title").removeChildren();
		_element.getFirstChildElement("title").appendChild(title);
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
    
    public void setType(String type) {
    	Element _type = _element.getFirstChildElement("type");
    	if (_type == null) {
    		_type = new Element("type");
    		_type.appendChild(type);
    		_element.appendChild(_type);
    	}
    	else {
    		_type.removeChildren();
    		_type.appendChild(type);
    	}
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

    public int getPriority() {
        Attribute pa = _element.getAttribute("priority");
        if (pa == null)
            return Template.PRIORITY_NORMAL;
        return new Integer(pa.getValue()).intValue();
    }
    
    public void setPriority(int p) {
        setAttr("priority", String.valueOf(p));
    }
	
	public Element getContent() {
		return _element;
	}
}
