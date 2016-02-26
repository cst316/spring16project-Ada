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

}
