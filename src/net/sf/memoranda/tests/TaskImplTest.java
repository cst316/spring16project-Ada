package net.sf.memoranda;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.memoranda.date.CalendarDate;

public class TaskImplTest {

	private static final String TYPE_A = "Type A";
	private static final String TYPE_B = "Type B";
	
	private static Task task;
	private static TaskList taskList;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		taskList = CurrentProject.getTaskList();
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
}
