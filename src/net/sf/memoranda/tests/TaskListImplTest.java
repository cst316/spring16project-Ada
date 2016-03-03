package net.sf.memoranda.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sf.memoranda.CurrentProject;

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
