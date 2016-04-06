package net.sf.memoranda.tests;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import net.sf.memoranda.ui.TimerDialog;

/**
 * Unit tests for the TimerDialog.
 * @author James Smith
 *
 */
public class TimerDialogTest {

	/**
	 * Unit test to verify that the timeToString method will return a valid
	 * String for displaying in the TimerDialog.
	 */
	@Test
	public void testTimeToString() throws
			NoSuchMethodException,
			SecurityException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		
		String ten = "10:10:10";
		String nine = "09:09:09";
		long tenLong = 
				(10 * 1000 * 60 * 60)
				+ (10 * 1000 * 60)
				+ (10 * 1000);
		long nineLong = 
				(9 * 1000 * 60 * 60)
				+ (9 * 1000 * 60)
				+ (9 * 1000);
		
		Method timeToString =
				TimerDialog.class.getDeclaredMethod(
						"timeToString",
						Long.TYPE);
		timeToString.setAccessible(true);
		
		String tenTest = (String) timeToString.invoke(null, tenLong);
		String nineTest = (String) timeToString.invoke(null, nineLong);
		
		assertEquals(
				"TimerDialog.timeToString incorrectly formatting 10:10:10",
				ten,
				tenTest);
		assertEquals("TimerDialog.timeToString incorrectly formatting 09:09:09",
				nine,
				nineTest);
	}
}