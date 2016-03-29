package net.sf.memoranda.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Process;
import net.sf.memoranda.ProcessList;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.LogPair;
import net.sf.memoranda.util.Util;

public class TaskImplTest {

	private static final String TYPE_A = "Type A";
	private static final String TYPE_B = "Type B";
	
	private static final int LOG_A = 3600000;
	private static final int LOG_B = 1800000;
	private static final int LOG_C = 600000;
	
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
	 * Save new task via constructor and setting type.
	 */
	@Test
	public void testSaveAndRetrieveType() {
		task = taskList.createTask(
				CalendarDate.today(),
				CalendarDate.today(),
				"Name",
				TYPE_A,
				0,
				0,
				"Description",
				null);
		assertTrue(task.getType().equals(TYPE_A));
		
		task.setType(TYPE_B);
		assertTrue(task.getType().equals(TYPE_B));
	}
	
	@Test
	public void testDatesWithProcess() {
		CalendarDate yesterday = new CalendarDate(1, 3, 2016);
		CalendarDate today = new CalendarDate(2, 3, 2016);
		Process process = processList.createProcess("test", yesterday, yesterday);
		Task task = taskList.createTask(
				today,
				today,
				"text",
				"type",
				0,
				0,
				"description",
				null);
		
		assertTrue(today.equals(task.getStartDate()));
		assertTrue(today.equals(task.getEndDate()));
		
		process.addTask(task.getID());
		
		assertTrue(yesterday.equals(task.getStartDate()));
		assertTrue(yesterday.equals(task.getEndDate()));
	}
	
	/**
	 * Helper method to setup Tasks with logged times.
	 */
	private void setupTaskWithLoggedTimes() {
		// Task creation
		task = taskList.createTask(
				CalendarDate.today(),
				CalendarDate.today(),
				"Log Test",
				TYPE_A,
				0,
				0,
				"Description",
				null);
		
		// Add logged times
		task.addLoggedTime(CalendarDate.yesterday().toString(), LOG_A);
		task.addLoggedTime(CalendarDate.yesterday().toString(), LOG_B);
		task.addLoggedTime(CalendarDate.today().toString(), LOG_C);
	}
	
	/**
	 * Tests adding logged time to a Task.
	 */
	@Test
	public void testAddLoggedTime() {
		setupTaskWithLoggedTimes();
		
		// Calculate
		long result = LOG_A + LOG_B + LOG_C;
		
		assertTrue(result == task.getLoggedTime());
		
		// Add negative logged time
		task.addLoggedTime(CalendarDate.today().toString(), -1000L);
		assertEquals(result, task.getLoggedTime());
		
		// Add zero logged time
		int logCount = task.getLoggedTimes().size();
		task.addLoggedTime(CalendarDate.today().toString(), 0L);
		assertEquals(logCount, task.getLoggedTimes().size());
	}
	
	/**
	 * Tests getting a map of all logged time instances for a Task.
	 */
	@Test
	public void testGetLoggedTimes() {
		setupTaskWithLoggedTimes();
		
		long actual = 0;
		
		List<LogPair> list = task.getLoggedTimes();
		
		for (int i = 0; i < list.size(); i++) {
			actual += list.get(i).getLength();
		}
		
		long expected = LOG_A + LOG_B + LOG_C;
		
		assertTrue(expected == actual);
	}
	
	/**
	 * Tests editing a specific instance of logged time within a Task.
	 */
	@Test
	public void testEditLoggedTime() {
		setupTaskWithLoggedTimes();
		
		// Edit second instance
		task.editLoggedTime(1, CalendarDate.today().toString(), LOG_C);
		
		long result = LOG_A + LOG_C + LOG_C;
		
		assertTrue(result == task.getLoggedTime());
		
		// Edit time with negative value
		task.editLoggedTime(1, CalendarDate.today().toString(), -1000L);
		assertEquals(result, task.getLoggedTime());
		
		// Edit time with zero value
		task.editLoggedTime(1, CalendarDate.today().toString(), 0L);
		assertEquals(result, task.getLoggedTime());
		
		// Edit time with invalid index
		task.editLoggedTime(
				task.getLoggedTimes().size(),
				CalendarDate.today().toString(),
				LOG_C);
		assertTrue(result == task.getLoggedTime());
	}
	
	/**
	 * Tests removing a specific instance of logged time within a Task.
	 */
	@Test
	public void testRemoveLoggedTime() {
		Util.debug("remove");
		
		setupTaskWithLoggedTimes();
		
		// Remove second instance
		assertTrue(task.removeLoggedTime(1));
		
		long result = LOG_A + LOG_C;
		
		assertTrue(result == task.getLoggedTime());
		
		// Remove using invalid index
		assertFalse(task.removeLoggedTime(task.getLoggedTimes().size()));
	}
}
