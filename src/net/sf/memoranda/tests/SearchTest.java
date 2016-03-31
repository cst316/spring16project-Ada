package net.sf.memoranda.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Note;
import net.sf.memoranda.NoteList;
import net.sf.memoranda.Process;
import net.sf.memoranda.ProcessList;
import net.sf.memoranda.Project;
import net.sf.memoranda.ProjectManager;
import net.sf.memoranda.Search;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.Template;
import net.sf.memoranda.TemplateList;
import net.sf.memoranda.date.CalendarDate;

/**
 * Unit tests for Search class.
 * @author James Smith
 *
 */
public class SearchTest {

	private static final String SEARCH_ONE = "one";
	private static final String SEARCH_TWO = "two";
	private static final String SEARCH_THREE = "three";
	
	private static final CalendarDate YESTERDAY = CalendarDate.yesterday();
	private static final CalendarDate TODAY = CalendarDate.today();
	private static final CalendarDate TOMORROW = CalendarDate.tomorrow();
	
	private Project project;
	private TaskList taskList;
	private TemplateList templateList;
	private ProcessList processList;
	private NoteList noteList;
	
	@Before
	public void before() {
		// Create new lists to search
		project = ProjectManager.createProject("test project", YESTERDAY, TOMORROW);
		CurrentProject.set(project);
		taskList = CurrentProject.getTaskList();
		templateList = CurrentProject.getTemplateList();
		processList = CurrentProject.getProcessList();
		noteList = CurrentProject.getNoteList();
		
		// Add test data to search for.
		taskList.createTask(
				YESTERDAY,
				YESTERDAY,
				"",
				SEARCH_THREE,
				0,
				0,
				"",
				null);
		Task parent = taskList.createTask(
				TODAY,
				TODAY,
				SEARCH_TWO + SEARCH_THREE,
				"",
				0,
				0,
				"",
				null);
		taskList.createTask(
				TOMORROW,
				TOMORROW,
				"",
				"",
				0,
				0,
				SEARCH_ONE + SEARCH_TWO + SEARCH_THREE,
				parent.getID());
		
		templateList.createTemplate(
				YESTERDAY,
				YESTERDAY,
				SEARCH_THREE,
				"",
				0,
				0,
				"");
		templateList.createTemplate(
				TODAY,
				TODAY,
				"template2",
				SEARCH_TWO + SEARCH_THREE,
				0,
				0,
				"");
		templateList.createTemplate(
				TOMORROW,
				TOMORROW,
				"template3",
				"",
				0,
				0,
				SEARCH_ONE + SEARCH_TWO + SEARCH_THREE);
		
		processList.createProcess(
				SEARCH_THREE,
				YESTERDAY,
				YESTERDAY);
		processList.createProcess(
				SEARCH_TWO + SEARCH_THREE,
				TODAY,
				TODAY);
		processList.createProcess(
				SEARCH_ONE + SEARCH_TWO + SEARCH_THREE,
				TOMORROW,
				TOMORROW);
		
		noteList.createNoteForDate(YESTERDAY).
				setTitle(SEARCH_THREE);
		noteList.createNoteForDate(TODAY).
				setTitle(SEARCH_TWO + SEARCH_THREE);
		noteList.createNoteForDate(TOMORROW).
				setTitle(SEARCH_ONE + SEARCH_TWO + SEARCH_THREE);
	}

	@After
	public void tearDownAfterClass() {
		ProjectManager.removeProject(project.getID());
	}

	@Test
	public void testSearchTasks() {
		Collection<Task> searchOne = Search.searchTasks(SEARCH_ONE, taskList);
		Collection<Task> searchTwo = Search.searchTasks(SEARCH_TWO, taskList);
		Collection<Task> searchThree = Search.searchTasks(SEARCH_THREE, taskList);
		
		// verify correct number of results
		assertEquals(1, searchOne.size());
		assertEquals(2, searchTwo.size());
		assertEquals(3, searchThree.size());
		
		// verify order is correct
		Task previousTask = null;
		for (Task currentTask : searchThree) {
			if (previousTask != null) {
				assertTrue(
						currentTask.getStartDate().
						after(previousTask.getStartDate()));
			}
			
			previousTask = currentTask;
		}
	}

	@Test
	public void testSearchTemplates() {
		Collection<Template> searchOne =
				Search.searchTemplates(SEARCH_ONE, templateList);
		Collection<Template> searchTwo =
				Search.searchTemplates(SEARCH_TWO, templateList);
		Collection<Template> searchThree =
				Search.searchTemplates(SEARCH_THREE, templateList);

		// verify correct number of results
		assertEquals(1, searchOne.size());
		assertEquals(2, searchTwo.size());
		assertEquals(3, searchThree.size());
		
		// verify order is correct
		Template previousTemplate = null;
		for (Template currentTemplate : searchThree) {
			if (previousTemplate != null) {
				assertTrue(
						currentTemplate.getTitle().
						compareTo(previousTemplate.getTitle()) > 0);
			}
			
			previousTemplate = currentTemplate;
		}
	}

	@Test
	public void testSearchProcesses() {
		Collection<Process> searchOne = Search.searchProcesses(SEARCH_ONE, processList);
		Collection<Process> searchTwo = Search.searchProcesses(SEARCH_TWO, processList);
		Collection<Process> searchThree = Search.searchProcesses(SEARCH_THREE, processList);

		// verify correct number of results
		assertEquals(1, searchOne.size());
		assertEquals(2, searchTwo.size());
		assertEquals(3, searchThree.size());
		
		// verify order is correct
		Process previousProcess = null;
		for (Process currentProcess : searchThree) {
			if (previousProcess != null) {
				assertTrue(
						currentProcess.getStartDate().
						after(previousProcess.getStartDate()));
			}
			
			previousProcess = currentProcess;
		}
	}

	@Test
	public void testSearchNotes() {
		Collection<Note> searchOne = Search.searchNotes(SEARCH_ONE, noteList);
		Collection<Note> searchTwo = Search.searchNotes(SEARCH_TWO, noteList);
		Collection<Note> searchThree = Search.searchNotes(SEARCH_THREE, noteList);
	
		// verify correct number of results
		assertEquals(1, searchOne.size());
		assertEquals(2, searchTwo.size());
		assertEquals(3, searchThree.size());
		
		// verify order is correct
		Note previousNote = null;
		for (Note currentNote : searchThree) {
			if (previousNote != null) {
				assertTrue(currentNote.getDate().after(previousNote.getDate()));
			}
			
			previousNote = currentNote;
		}
	}

}
