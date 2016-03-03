package net.sf.memoranda.tests;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.date.CalendarDate;

public class TaskImplTest {

	private static final String TYPE_A = "Type A";
	private static final String TYPE_B = "Type B";
	
	private static Task task;
	private static TaskList taskList;
	private static ProcessList processList;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		taskList = CurrentProject.getTaskList();
		processList = CurrentProject.getProcessList();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		task = null;
		taskList = null;
	}

	/**
	 * Save new task via constructor and setting type
	 */
	@Test
	public void testSaveAndRetrieveType() {
		task = taskList.createTask(CalendarDate.today(), CalendarDate.today(), "Name", TYPE_A, 0, 0, "Description", null);
		assertTrue(task.getType().equals(TYPE_A));
		
		task.setType(TYPE_B);
		assertTrue(task.getType().equals(TYPE_B));
	}
	
	@Test
	public void testDatesWithProcess() {
		CalendarDate yesterday = CalendarDate.yesterday();
		CalendarDate today = CalendarDate.today();
		Process p = processList.createProcess("test", yesterday, yesterday);
		Task t = taskList.createTask(today, today, "text", "type", 0, 0, "description", null);
		
		assertTrue(today.equals(t.getStartDate()));
		assertTrue(today.equals(t.getEndDate()));
		
		p.addTask(t.getID());
		
		assertTrue(yesterday.equals(t.getStartDate()));
		assertTrue(yesterday.equals(t.getEndDate()));
	}
}
