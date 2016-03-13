package net.sf.memoranda.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Template;
import net.sf.memoranda.TemplateList;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.CurrentStorage;
import net.sf.memoranda.util.FileStorage;
import net.sf.memoranda.util.Util;

public class TemplateImplTest {
	
	private static final int START_DAY = 20;
	private static final int START_MONTH = 2;
	private static final int START_YEAR = 2016;
	
	private static final int END_DAY = 22;
	private static final int END_MONTH = 2;
	private static final int END_YEAR = 2016;
	
	private static final String TITLE = "Template Title";
	private static final String TYPE = "Template Type";
	private static final String DESCRIPTION = "Template Description";
	
	private static Template template = null;
	private static TemplateList templateList = null;
	
	private static String _id = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		templateList = CurrentProject.getTemplateList();
	}
	
	@Test
	public void testSaveAndEditTemplate() {
		template = templateList.createTemplate(new CalendarDate(START_DAY, START_MONTH, START_YEAR), new CalendarDate(END_DAY, END_MONTH, END_YEAR), TITLE, TYPE, Template.PRIORITY_NORMAL, 0, DESCRIPTION);
		
		template.setDateDifference(new CalendarDate(START_DAY, START_MONTH, START_YEAR), new CalendarDate(END_DAY, END_MONTH, END_YEAR));
		template.setDescription(DESCRIPTION);
		template.setEffort(0);
		template.setPriority(Template.PRIORITY_NORMAL);
		template.setTitle(TITLE);
		template.setType(TYPE);
		
		CurrentStorage.get().storeTemplateList(templateList, CurrentProject.get());
	}

	@Test
	public void testRetrieveTemplate() {
		_id = new String();
		
		for (String id : templateList.getIds()) {
			_id = id;
		}
		
		template = templateList.getTemplate(_id);
		
		assertTrue(template.getId().equals(_id));
		
		int[] diff = {END_DAY - START_DAY, END_MONTH - START_MONTH, END_YEAR - START_YEAR};
		
		for (int i = 0; i < diff.length; i++) {
			assertTrue(template.getDateDifference()[i] == diff[i]);
		}
		
		assertTrue(template.getTitle().equals(TITLE));
		assertTrue(template.getType().equals(TYPE));
		assertTrue(template.getPriority() == Template.PRIORITY_NORMAL);
		assertTrue(template.getEffort() == 0);
		assertTrue(template.getDescription().equals(DESCRIPTION));
	}
	
	@Test
	public void testRemoveTemplate() {
		templateList.removeTemplate(template);
	}
	
	@Test
	public void testNullValues() {
		Template t = templateList.createTemplate(null, null, "Empty Template", null, 0, 0, null);
		int[] diff = t.getDateDifference();
		
		assertEquals(-1, diff[0]);
		assertEquals(-1, diff[1]);
		assertEquals(-1, diff[2]);
		assertEquals("", t.getDescription());
		assertEquals("", t.getType());
	}
}
