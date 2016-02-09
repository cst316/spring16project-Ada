package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.ReportImpl;
import net.sf.memoranda.Task;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.FileStorage;
import net.sf.memoranda.util.Util;

public class ReportTest {

	private static FileStorage store;
	private ReportImpl report;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		store = new FileStorage();
		
		CurrentProject.getTaskList().createTask(new CalendarDate(2016, 1, 1), new CalendarDate(2016, 1, 30), "Task A", "Foo", 3, (long)2.5, "blah", null);
		CurrentProject.getTaskList().createTask(new CalendarDate(2016, 1, 1), new CalendarDate(2016, 1, 30), "Task B", "Bar", 3, (long)2.5, "bloo", null);
		CurrentProject.getTaskList().createTask(new CalendarDate(2016, 1, 1), new CalendarDate(2016, 1, 30), "Task C", "Type", 3, (long)2.5, "blop", null);

		store.storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
	}

	@Before
	public void setUp() throws Exception {
		report = new ReportImpl();
	}

	@After
	public void tearDown() throws Exception {
		report = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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

		ArrayList<String> allIds = (ArrayList<String>)(CurrentProject.getTaskList().getIds());
		
		String[] someIds = new String[2];
		
		someIds[0] = allIds.get(1);
		someIds[1] = allIds.get(2);
		
		for (int i = 0; i < someIds.length; i++) {
			System.out.printf("%d: %s\n", i, someIds[i]);
			Util.debug("Task Name: " + CurrentProject.getTaskList().getTask(someIds[i]).getText());
		}
		
		report.setTasks(someIds);
		
		Collection<Task> tasks = report.getTasks();
		
		for (Task task : tasks) {
			System.out.println("ID: " + task.getID());
			Util.debug("Name: " + task.getText());
		}
	}
}
