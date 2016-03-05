package net.sf.memoranda.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Process;
import net.sf.memoranda.ProcessList;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.date.CalendarDate;

public class ProcessTest {

	private static final long ONE_HOUR = 1 * 60 * 60 * 1000;
	ProcessList pl;
	TaskList tl;
	CalendarDate yesterday;
	CalendarDate today;
	CalendarDate tomorrow;
	
	@Before
	public void setUp() throws Exception {
		pl = CurrentProject.getProcessList();
		tl = CurrentProject.getTaskList();
		yesterday = new CalendarDate(29, 2, 2016);
		today = new CalendarDate(1, 3, 2016);
		tomorrow = new CalendarDate(2, 3, 2016);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testName() {
		Process process = pl.createProcess("test", today, today);
		
		process.setName("name");
		assertEquals("name", process.getName());
		process.setName(null);
		assertEquals("name", process.getName());
	}
	
	@Test
	public void testTasks() {
		Process process = pl.createProcess("test", today, today);
		Task task = tl.createTask(
				today, today, "text", "type", Task.PRIORITY_LOW, 0, "desc", null);
		
		assertTrue(process.addTask(task.getID()));
		assertTrue(process.hasTask(task.getID()));
		assertEquals(process.getID(), task.getProcess().getID());
		assertEquals(1, process.getTasks().size(), 0);
		
		for (Task t2 : process.getTasks()) {
			assertEquals(task.getID(), t2.getID());
		}
		
		// test switching task to a different process
		Process p2 = pl.createProcess("test2", today, today);
		p2.addTask(task.getID());
		
		assertEquals(p2.getID(), task.getProcess().getID());
		assertEquals(0, process.getTasks().size(), 0);
		
		// test removing task from second process
		p2.removeTask(task.getID());
		assertEquals(0, p2.getTasks().size(), 0);
		assertTrue(task.getProcess() == null);
		
		// add test back to process and test deleting task
		process.addTask(task.getID());
		tl.removeTask(task);
		assertEquals(0, process.getTasks().size(), 0);
	}
	
	@Test
	public void testProgress() {
		CalendarDate today = new CalendarDate();
		Process process = pl.createProcess("test", today, today);
		Task task1 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				ONE_HOUR,
				"desc",
				null);
		Task task2 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				2 * ONE_HOUR, "desc",
				null);
		Task task3 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				3 * ONE_HOUR,
				"desc",
				null);
		
		task1.setProgress(50);
		task2.setProgress(25);
		task3.setProgress(0);
		
		process.addTask(task1.getID());
		process.addTask(task2.getID());
		process.addTask(task3.getID());
		
		assertEquals(16, process.getProgress(), 1);
	}
	
	@Test
	public void testDates() {
		Process process = pl.createProcess("test", yesterday, yesterday);
		assertTrue(yesterday.equals(process.getStartDate()));
		assertTrue(yesterday.equals(process.getEndDate()));
		
		assertFalse(process.setStartDate(today));
		assertTrue(process.setEndDate(tomorrow));
		assertTrue(yesterday.equals(process.getStartDate()));
		assertTrue(tomorrow.equals(process.getEndDate()));
		
		assertTrue(process.setStartDate(today));
		assertFalse(process.setEndDate(yesterday));
		assertTrue(today.equals(process.getStartDate()));
		assertTrue(tomorrow.equals(process.getEndDate()));
	}
	
	@Test
	public void testGetActiveTasks() {
		Process process = pl.createProcess("test", today, today);
		Task task1 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				ONE_HOUR,
				"desc",
				null);
		Task task2 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				2 * ONE_HOUR, "desc",
				null);
		Task task3 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				3 * ONE_HOUR,
				"desc",
				null);
		
		process.addTask(task1.getID());
		process.addTask(task2.getID());
		process.addTask(task3.getID());
		task1.setProgress(100);
		
		assertEquals(3, process.getTasks().size());
		assertEquals(2, process.getActiveTasks(today).size());
		assertEquals(0, process.getActiveTasks(yesterday).size());
	}
	
	@Test
	public void testTaskOrder() {
		Process process = pl.createProcess("test", today, today);
		Task task1 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				ONE_HOUR,
				"desc",
				null);
		Task task2 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				ONE_HOUR,
				"desc",
				null);
		Task task3 = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				ONE_HOUR,
				"desc",
				null);
		Task outsideTask = tl.createTask(
				today,
				today,
				"text",
				"type",
				Task.PRIORITY_LOW,
				ONE_HOUR,
				"desc",
				null);
		
		process.addTask(task1.getID());
		process.addTask(task2.getID());
		process.addTask(task3.getID());
		
		// testing valid order
		String[] validOrder = {task1.getID(), task2.getID(), task3.getID()};
		
		assertTrue(process.setTaskOrder(validOrder));
		
		// testing too few tasks
		String[] tooFew = {task1.getID(), task2.getID()};
		
		assertFalse(process.setTaskOrder(tooFew));
		
		// testing with task not in process
		String[] wrongTask = {task1.getID(), task2.getID(), outsideTask.getID()};
		
		assertFalse(process.setTaskOrder(wrongTask));
		
		// testing with a duplicate task
		String[] duplicate = {task3.getID(), task2.getID(), task3.getID(), task1.getID()};
		
		assertFalse(process.setTaskOrder(duplicate));
		
		// testing with an invalid id
		String[] invalidId = {task2.getID(), "bad id", task3.getID(), task1.getID()};
		
		assertFalse(process.setTaskOrder(invalidId));
		
		// testing order of tasks is correct
		int index = 0;
		for (Task t : process.getTasks()) {
			assertEquals(validOrder[index], t.getID());
			index++;
		}
		
		String[] validOrder2 = {task3.getID(), task2.getID(), task1.getID()};
		
		assertTrue(process.setTaskOrder(validOrder2));
		index = 0;
		for (Task t : process.getTasks()) {
			assertEquals(validOrder2[index], t.getID());
			index++;
		}
	}
}