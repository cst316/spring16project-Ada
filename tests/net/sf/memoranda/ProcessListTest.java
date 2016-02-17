package net.sf.memoranda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessListTest {

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
	public void test() {
		Process p1 = pl.createProcess("test");
		assertTrue(p1 != null);
		
		Process p2 = pl.getProcess(p1.getID());
		assertTrue(p2 != null);
		
		int sizeOne = pl.getAllProcesses().size();
		
		assertTrue(sizeOne>0);
		
		pl.removeProcess(p1.getID());
		
		int sizeTwo = pl.getAllProcesses().size();
		
		assertEquals(1, sizeOne-sizeTwo, 0);
		
		assertTrue(pl.getProcess("badID") == null);
	}
}
