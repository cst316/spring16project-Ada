package net.sf.memoranda;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Util;

public class TaskListImplTest {
	
	private static int TASKS_TO_CREATE = 50;
	private static int CHILD_TASKS_TO_CREATE = 50;

	private static final String TYPE_ONE = "TestType";
	private static final String TYPE_TWO = "SecondType";

	private TaskList tl;
	
	@Before
	public void setUp() throws Exception {
		tl = CurrentProject.getTaskList();
		
		for (int i=0; i<TASKS_TO_CREATE; i++) {
			Task parent = tl.createTask(CalendarDate.today(), CalendarDate.tomorrow(), "text", Util.generateId(), 0, 0, "description", null);
			
			for (int j=0; j<CHILD_TASKS_TO_CREATE; j++) {
				Task child = tl.createTask(CalendarDate.today(), CalendarDate.tomorrow(), "text", Util.generateId(), 0, 0, "description", parent.getID());
			}
		}
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetTaskTypes() {
		long startTime = System.currentTimeMillis();
		Collection<String> types = tl.getTaskTypes();
		long endTime = System.currentTimeMillis();
		
		if (types.size() < TASKS_TO_CREATE * CHILD_TASKS_TO_CREATE + TASKS_TO_CREATE) {
			fail("Not enough Task Types.");
		}
		else {
			for (String s : types) {
				Util.debug(s);
			}
			Util.debug("getTaskTypes completed in " + (endTime-startTime) + " ms") ;
		}
	}

}
