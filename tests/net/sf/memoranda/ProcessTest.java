package net.sf.memoranda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.memoranda.date.CalendarDate;

public class ProcessTest {

	private static final long ONE_HOUR = 1 * 60 * 60 * 1000;
	ProcessList pl;
	TaskList tl;
	
	@Before
	public void setUp() throws Exception {
		pl = CurrentProject.getProcessList();
		tl = CurrentProject.getTaskList();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testName() {
		Process p = pl.createProcess("test");
		
		p.setName("name");
		assertEquals("name", p.getName());
		p.setName(null);
		assertEquals("name", p.getName());
	}
	
	@Test
	public void testTasks() {
		CalendarDate today = new CalendarDate();
		Process p = pl.createProcess("test");
		Task t = tl.createTask(today, today, "text", "type", Task.PRIORITY_LOW, 0, "desc", null);
		
		assertTrue(p.addTask(t.getID()));
		assertTrue(p.hasTask(t.getID()));
		assertEquals(p.getID(), t.getProcess().getID());
		assertEquals(1, p.getTasks().size(), 0);
		
		for (Task t2 : p.getTasks()) {
			assertEquals(t.getID(), t2.getID());
		}
		
		// test switching task to a different process
		Process p2 = pl.createProcess("test2");
		p2.addTask(t.getID());
		
		assertEquals(p2.getID(), t.getProcess().getID());
		assertEquals(0, p.getTasks().size(), 0);
		
		// test removing task from second process
		p2.removeTask(t.getID());
		assertEquals(0, p2.getTasks().size(), 0);
		assertTrue(t.getProcess() == null);
		
		// add test back to process and test deleting task
		p.addTask(t.getID());
		tl.removeTask(t);
		assertEquals(0, p.getTasks().size(), 0);
	}
	
	@Test
	public void testProgress() {
		CalendarDate today = new CalendarDate();
		Process p = pl.createProcess("test");
		Task t1 = tl.createTask(today, today, "text", "type", Task.PRIORITY_LOW, ONE_HOUR, "desc", null);
		Task t2 = tl.createTask(today, today, "text", "type", Task.PRIORITY_LOW, 2 * ONE_HOUR, "desc", null);
		Task t3 = tl.createTask(today, today, "text", "type", Task.PRIORITY_LOW, 3 * ONE_HOUR, "desc", null);
		
		t1.setProgress(50);
		t2.setProgress(25);
		t3.setProgress(0);
		
		p.addTask(t1.getID());
		p.addTask(t2.getID());
		p.addTask(t3.getID());
		
		assertEquals(16, p.getProgress(), 1);
	}
}