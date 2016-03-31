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

public class ProcessListTest {

	ProcessList pl;
	TaskList tl;
	CalendarDate today;
	CalendarDate tomorrow;
	CalendarDate yesterday;
	
	@Before
	public void setUp() throws Exception {
		pl = CurrentProject.getProcessList();
		tl = CurrentProject.getTaskList();
		yesterday = new CalendarDate(1, 3, 2016);
		today = new CalendarDate(2, 3, 2016);
		tomorrow = new CalendarDate(3, 3, 2016);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		pl.createProcess("process", today, today);
		Process p1 = pl.createProcess("test", today, today);
		assertTrue(p1 != null);
		
		Process p2 = pl.getProcess(p1.getID());
		assertTrue(p2 != null);
		
		int sizeOne = pl.getAllProcesses().size();
		
		assertTrue(sizeOne>0);
		
		pl.removeProcess(p1.getID());
		
		int sizeTwo = pl.getAllProcesses().size();
		
		assertEquals(1, sizeOne-sizeTwo, 0);
		
		assertTrue(pl.getProcess("badID") == null);
		
		Process p3 = pl.createProcess("complete process", today, today);
		Task t1 = tl.createTask(today, today, "text", "type", 0, 10, "description", null);
		p3.addTask(t1.getID());
		t1.setProgress(100);

		Process p4 = pl.createProcess("future process", tomorrow, tomorrow);
		
		sizeOne = pl.getAllProcesses().size();
		sizeTwo = pl.getActiveProcesses(today).size();
		
		assertTrue((sizeOne - sizeTwo) >= 2);
	}
}
