package net.sf.memoranda;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.ReportImpl;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.FileStorage;
import net.sf.memoranda.util.Util;

public class ReportTest {

	private static FileStorage store;
	private ReportImpl report;
	private Random rand;
	private static TaskList taskList = CurrentProject.getTaskList();
	private static ArrayList<String> allIds;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		store = new FileStorage();
		
		for (int i = 0; i < 1000; i++) {
			taskList.createTask(new CalendarDate(2016, 1, 1), new CalendarDate(2016, 1, 30), String.format("Task %d", i), "Foo", 3, (long)2.5, "blah", null);
		}

		store.storeTaskList(taskList, CurrentProject.get());
		
		allIds = (ArrayList<String>)(CurrentProject.getTaskList().getIds());
	}

	@Before
	public void setUp() throws Exception {
		report = new ReportImpl();
		rand = new Random();
	}

	@After
	public void tearDown() throws Exception {
		report = null;
		rand = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Delete tasks

		for (int i = 0; i < 1000; i++) {
			taskList.removeTask(taskList.getTask(allIds.get(i)));
		}

		store.storeTaskList(taskList, CurrentProject.get());
		
		taskList = null;
		store = null;
		
	}

	@Test
	public void testGetStyle() {
		report.setStyle(3);
		assertEquals(Integer.valueOf(0), Integer.valueOf(report.getStyle()));
		
		report.setStyle(2);
		assertEquals(Integer.valueOf(2), Integer.valueOf(report.getStyle()));
	
		report.setStyle(-1);
		assertEquals(Integer.valueOf(0), Integer.valueOf(report.getStyle()));
	}

	@Test
	public void testGetTasks() {
		
		Collection<String> taskIds = CurrentProject.getTaskList().getIds();

		int size = rand.nextInt(1000); // get number from 0-999
		
		String[] someIds = new String[size];
		
		for (int i = 0; i < size; i++) {
			someIds[i] = allIds.get(i);
		}
		
		report.setTasks(someIds);
		
		Collection<Task> tasks = report.getTasks();
	}
	
	@Test
	public void testExportHtmlMin() {
		report.setStyle(0);
		report.exportHtml();
	}
	
	@Test
	public void testExportHtmlMed() {
		report.setStyle(1);
		report.exportHtml();
	}
	
	@Test
	public void testExportHtmlMax() {
		report.setStyle(2);
		report.exportHtml();
	}
}
