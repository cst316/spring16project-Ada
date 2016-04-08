package net.sf.memoranda.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.date.CalendarDate;

public class TaskListImplTest {

	@Test
	public void testGetTaskTypes() {
		Collection<String> types = CurrentProject.getTaskList().getTaskTypes();
		String previousType = null;
		
		for (String currentType : types) {
			if (previousType != null) {
				assertFalse(previousType.equals(currentType));
				assertTrue(previousType.compareTo(currentType) < 0);
			}
			
			previousType = currentType;
		}
	}
	
	@Test
	public void testGetTasksByDate() {
		Collection<Task> result;
		
		TaskList taskList = CurrentProject.getTaskList();
		
		Task task1 = taskList.createTask(
				new CalendarDate(20, 4, 2016), 
				new CalendarDate(29, 4, 2016), 
				"Task 1", 
				"Type", 
				0, 
				0, 
				"Description", 
				null);
		
		Task task2 = taskList.createTask(
				new CalendarDate(20, 4, 2016), 
				new CalendarDate(20, 4, 2016), 
				"Task 2", 
				"Type", 
				0, 
				0, 
				"Description", 
				null);
		
		result = taskList.getTasksByDate(new CalendarDate(25, 4, 2016));
		assertTrue(result.contains(task1));
		assertFalse(result.contains(task2));
		
		result = taskList.getTasksByDate(new CalendarDate(20, 4, 2016));
		assertTrue(result.contains(task1));
		assertTrue(result.contains(task2));
		
		result = taskList.getTasksByDate(new CalendarDate(29, 4, 2016));
		assertTrue(result.contains(task1));
		assertFalse(result.contains(task2));
		
		result = taskList.getTasksByDate(new CalendarDate(19, 4, 2016));
		assertFalse(result.contains(task1));
		assertFalse(result.contains(task2));
		
		result = taskList.getTasksByDate(new CalendarDate(30, 4, 2016));
		assertFalse(result.contains(task1));
		assertFalse(result.contains(task2));
	}

}
