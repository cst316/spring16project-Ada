package net.sf.memoranda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import net.sf.memoranda.util.FileStorage;

public class ReportTest {

	private Report report;
	private Collection<Task> tasks;
	private FileStorage store;
	
	@Before
	public void before() {
		store = new FileStorage();
		tasks = CurrentProject.getTaskList().getTopLevelTasks();
		String[] ids = new String[tasks.size()];
		int i = 0;
		
		for (Task t : tasks) {
			ids[i] = t.getID();
			i++;
		}
		
		report = new ReportImpl();
		report.setTasks(ids);
		
		File file = new File(store.getReportPath());
		if (file.exists()) {
			file.delete();
		}
	}
	
	@Test
	public void testGetSetStyle() {
		report.setStyle(Report.STYLE_MINIMUM);
		assertEquals(Report.STYLE_MINIMUM, report.getStyle(), 0);
		report.setStyle(Report.STYLE_MEDIUM);
		assertEquals(Report.STYLE_MEDIUM, report.getStyle(), 0);
		report.setStyle(Report.STYLE_MAXIMUM);
		assertEquals(Report.STYLE_MAXIMUM, report.getStyle(), 0);
	}
	
	@Test
	public void testGetTasks() {
		Collection<Task> gotTasks = report.getTasks();
		
		assertEquals(tasks, gotTasks);
	}
	
	@Test
	public void testExportHtmlMin() {
		report.setStyle(Report.STYLE_MINIMUM);
		report.exportHtml();
		
		File file = new File(store.getReportPath());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}
	
	@Test
	public void testExportHtmlMed() {
		report.setStyle(Report.STYLE_MEDIUM);
		report.exportHtml();
		
		File file = new File(store.getReportPath());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}
	
	@Test
	public void testExportHtmlMax() {
		report.setStyle(Report.STYLE_MAXIMUM);
		report.exportHtml();
		
		File file = new File(store.getReportPath());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}
}
