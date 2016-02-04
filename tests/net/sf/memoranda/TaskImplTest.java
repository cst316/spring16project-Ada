package net.sf.memoranda;

import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sf.memoranda.date.CalendarDate;

public class TaskImplTest {

	private static final String TYPE_ONE = "One";
	private static final String TYPE_TWO = "Two";
	
	private Task task;
	private TaskList tl;
	
	@Before
	public void setUp() throws Exception {
		tl = CurrentProject.getTaskList();
		
		task = tl.createTask(CalendarDate.today(), CalendarDate.today(), "text", TYPE_ONE, 0, 0, "description", null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSetType() {		
		Assert.assertTrue(task.getType().equals(TYPE_ONE));
		
		task.setType(TYPE_TWO);
		
		Assert.assertTrue(task.getType().equals(TYPE_TWO));
	}
}
