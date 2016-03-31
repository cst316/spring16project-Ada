package net.sf.memoranda.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

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
import net.sf.memoranda.date.CurrentDate;
import net.sf.memoranda.util.CurrentStorage;
import net.sf.memoranda.util.Local;

/**
 * Dialog for performing a search and viewing the results.
 * @author James Smith
 *
 */
public class SearchDialog extends JDialog {

	private static final long serialVersionUID = -8815728489880238796L;
	
	// Column indexes for GridBagConstraints
	private static final int COL_NAME = 0;
	private static final int COL_START_DATE = 1;
	private static final int COL_END_DATE = 2;
	private static final int COL_PROJECT = 3;
	private static final int COL_GOTO_BUTTON = 4;
	
	/*
	 * Creating maps for the search results. The key String is the
	 * name of the project those search results belong to.
	 */
	private Map<Project, Collection<Task>> taskResults;
	private Map<Project, Collection<Template>> templateResults;
	private Map<Project, Collection<Process>> processResults;
	private Map<Project, Collection<Note>> noteResults;

	private Border defaultBorder;
	private DateFormat dateFormat;
	private Frame frame;
	
	// Header
	private JPanel headerPanel;
    private JLabel headerLabel;
    
    // Search Input
    private JCheckBox allProjectsCheckBox;
    private JTextField searchText;
    
    // Results
    private JPanel taskResultsPanel;
    private JPanel templateResultsPanel;
    private JPanel processResultsPanel;
    private JPanel noteResultsPanel;
    
    // Buttons
    private JButton okButton;
    private JPanel buttonPanel;
	
	public SearchDialog(
			Frame frame,
			String title,
			String searchString,
			boolean includeAllProjects) {
		
		super(frame, title, true);
		
		// initialize instance variables
		this.taskResults = new HashMap<Project, Collection<Task>>();
    	this.templateResults = new HashMap<Project, Collection<Template>>();
    	this.processResults = new HashMap<Project, Collection<Process>>();
    	this.noteResults = new HashMap<Project, Collection<Note>>();
    	this.dateFormat = CalendarDate.getSimpleDateFormat();
    	this.frame = frame;
		
		if (searchString == null) {
			searchString = "";
		}
		
		try {
			jbInit(searchString, includeAllProjects);
			pack();
		} catch (Exception ex) {
            new ExceptionDialog(ex);
            ex.printStackTrace();
        }
	}
	
	/**
	 * Initializes GUI components.
	 * @param searchString			default search String
	 * @param includeAllProjects	default value for include all check box
	 */
	private void jbInit(String searchString, boolean includeAllProjects) {
		this.setResizable(false);
		defaultBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		this.getContentPane().setLayout(new BoxLayout(
				this.getContentPane(),
				BoxLayout.Y_AXIS));
		
		drawHeaderPanel();
		drawSearchInputPanel(searchString, includeAllProjects);

		JPanel innerPanel = new JPanel();
		
		// scroll pane encloses all the result panels
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		taskResultsPanel = new JPanel(new GridBagLayout());
		templateResultsPanel = new JPanel(new GridBagLayout());
		processResultsPanel = new JPanel(new GridBagLayout());
		noteResultsPanel = new JPanel(new GridBagLayout());
		innerPanel.add(taskResultsPanel);
		innerPanel.add(templateResultsPanel);
		innerPanel.add(processResultsPanel);
		innerPanel.add(noteResultsPanel);
		JScrollPane resultsPane = new JScrollPane(innerPanel);
		resultsPane.setPreferredSize(new Dimension(300, 300));
		this.getContentPane().add(resultsPane);
		
		drawButtonsPanel();
		
		// perform an initial search if a search string was provided
		if (searchString != "") {
			executeSearch();
			drawTaskResultsPanel();
			drawTemplateResultsPanel();
			drawProcessResultsPanel();
			drawNoteResultsPanel();
		}
	}
	
	/*
	 * BEGIN DRAW PANELS METHODS
	 */
	
	/**
	 * Draws the header.
	 */
	private void drawHeaderPanel() {
		headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        headerLabel = new JLabel();
        headerLabel.setFont(new java.awt.Font("Dialog", 0, 20));
        headerLabel.setForeground(new Color(0, 0, 124));
        headerLabel.setText(Local.getString("Search"));
        headerPanel.add(headerLabel);
        this.getContentPane().add(headerPanel);
	}
	
	/**
	 * Draws the search input panel.
	 * @param searchString		Default search String
	 * @param includeAllResults	Default option for include all check box
	 */
	private void drawSearchInputPanel(String searchString, boolean includeAllResults) {
		JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		allProjectsCheckBox = new JCheckBox(Local.getString("Search all projects"));
		searchText = new JTextField(searchString);
		JButton searchButton = new JButton(Local.getString("Search"));

		allProjectsCheckBox.setSelected(includeAllResults);
		searchText.setPreferredSize(new Dimension(200, 26));

		searchButton.setMaximumSize(new Dimension(100, 26));
		searchButton.setMinimumSize(new Dimension(100, 26));
		searchButton.setPreferredSize(new Dimension(100, 26));
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				executeSearch();
				drawTaskResultsPanel();
				drawTemplateResultsPanel();
				drawProcessResultsPanel();
				drawNoteResultsPanel();
				
				pack();
			}
		});
		
		searchInputPanel.add(allProjectsCheckBox);
		searchInputPanel.add(searchText);
		searchInputPanel.add(searchButton);
		this.getContentPane().add(searchInputPanel);
	}
	
	/**
	 * Draw the Task search results.
	 */
	private void drawTaskResultsPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		initResultsPanel(taskResultsPanel, gc, "Tasks");
		
		int resultCount = 0;
		
		for (Entry<Project, Collection<Task>> entry : taskResults.entrySet()) {
			Collection<Task> tasks = entry.getValue();
			Project project = entry.getKey();

			for (Task task : tasks){
				gc.gridy++;
				drawResult(task, project, taskResultsPanel, gc);
				resultCount++;
			}
		}
		
		drawResultCount(resultCount, taskResultsPanel, gc);
	}
	
	/**
	 * Draw the Template search results.
	 */
	private void drawTemplateResultsPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		initResultsPanel(templateResultsPanel, gc, "Templates");
		
		int resultCount = 0;
		
		for (Entry<Project, Collection<Template>> entry : templateResults.entrySet()) {
			Collection<Template> templates = entry.getValue();
			Project project = entry.getKey();
			
			for (Template template : templates){
				gc.gridy++;
				drawResult(template, project, templateResultsPanel, gc);
				resultCount++;
			}
		}
		
		drawResultCount(resultCount, templateResultsPanel, gc);
	}
	
	/**
	 * Draw the Process search results.
	 */
	private void drawProcessResultsPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		initResultsPanel(processResultsPanel, gc, "Processes");
		
		int resultCount = 0;
		
		for (Entry<Project, Collection<Process>> entry : processResults.entrySet()) {
			Collection<Process> processes = entry.getValue();
			Project project = entry.getKey();
			
			for (Process process : processes){
				gc.gridy++;
				drawResult(process, project, processResultsPanel, gc);
				resultCount++;
			}
		}
		
		drawResultCount(resultCount, processResultsPanel, gc);
	}
	
	/**
	 * Draw the Note search results.
	 */
	private void drawNoteResultsPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		initResultsPanel(noteResultsPanel, gc, "Notes");
		
		int resultCount = 0;
		
		for (Entry<Project, Collection<Note>> entry : noteResults.entrySet()) {
			Collection<Note> notes = entry.getValue();
			Project project = entry.getKey();
			
			for (Note note : notes){
				gc.gridy++;
				drawResult(note, project, noteResultsPanel, gc);
				resultCount++;
			}
		}
		
		drawResultCount(resultCount, noteResultsPanel, gc);
	}
	
	/**
	 * Draw the button panel at the bottom.
	 */
	private void drawButtonsPanel() {
		okButton = new JButton();
		okButton.setMaximumSize(new Dimension(100, 26));
		okButton.setMinimumSize(new Dimension(100, 26));
		okButton.setPreferredSize(new Dimension(100, 26));
		okButton.setText(Local.getString("Ok"));
		okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	SearchDialog.this.dispose();
            }
        });
		
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(defaultBorder);
        buttonPanel.add(okButton);
        this.getContentPane().add(buttonPanel);
	}
	
	/*
	 * END DRAW PANELS METHODS
	 */
	
	/*
	 * BEGIN HELPER METHODS
	 */
	
	/**
	 * Helper method to initialize a result panel.
	 * @param panel		The panel to initialize
	 * @param gc		GridBagConstraints used for layout
	 * @param labelText	The header String for the panel
	 */
	private void initResultsPanel(JPanel panel, GridBagConstraints gc, String labelText) {
		panel.removeAll();

		JLabel label = new JLabel(labelText);
		gc.ipadx = 3;
		gc.ipady = 3;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.WEST;
		label.setFont(new java.awt.Font("Dialog", 0, 20));
		label.setForeground(new Color(0, 0, 124));
		
		panel.add(label, gc);
		gc.gridy = 1;
	}
	
	/**
	 * Helper method for drawing a single search result.
	 * @param result	The search result Object
	 * @param project	The project the result belongs to
	 * @param panel		The enclosing panel
	 * @param gc		The GridBagConstraints used for layout
	 */
	private void drawResult(
			final Object result,
			final Project project,
			JPanel panel,
			GridBagConstraints gc) {
		
		if (result instanceof Task) {
			Task task = (Task) result;
			gc.gridx = COL_NAME;
			panel.add(new JLabel(task.getText()), gc);
			gc.gridx = COL_START_DATE;
			panel.add(new JLabel(dateFormat.format(task.getStartDate().getDate())), gc);
			gc.gridx = COL_END_DATE;
			panel.add(new JLabel(dateFormat.format(task.getEndDate().getDate())), gc);
		} else if (result instanceof Template) {
			Template template = (Template) result;
			gc.gridx = COL_NAME;
			panel.add(new JLabel(template.getTitle()), gc);
			gc.gridx = COL_START_DATE;
			panel.add(new JLabel(), gc);
			gc.gridx = COL_END_DATE;
			panel.add(new JLabel(), gc);
		} else if (result instanceof Process) {
			Process process = (Process) result;
			gc.gridx = COL_NAME;
			panel.add(new JLabel(process.getName()), gc);
			gc.gridx = COL_START_DATE;
			panel.add(new JLabel(
					dateFormat.format(process.getStartDate().getDate())),
					gc);
			gc.gridx = COL_END_DATE;
			panel.add(new JLabel(
					dateFormat.format(process.getEndDate().getDate())),
					gc);
		} else if (result instanceof Note) {
			Note note = (Note) result;
			gc.gridx = COL_NAME;
			panel.add(new JLabel(note.getTitle()), gc);
			gc.gridx = COL_START_DATE;
			panel.add(new JLabel(dateFormat.format(note.getDate().getDate())), gc);
			gc.gridx = COL_END_DATE;
			panel.add(new JLabel(), gc);
		}
		
		gc.gridx = COL_PROJECT;
		panel.add(new JLabel(project.getTitle()), gc);
		gc.gridx = COL_GOTO_BUTTON;
		JButton button = new JButton("Go to");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				goTo_actionPerformed(result, project, event);
			}
		});
		panel.add(button, gc);
	}
	
	/**
	 * Helper method for displaying the total results found for a panel.
	 * @param resultCount	The count of results
	 * @param panel			The enclosing panel
	 * @param gc			The GridBagConstrainst used for layout
	 */
	private void drawResultCount(int resultCount, JPanel panel, GridBagConstraints gc) {
		gc.gridx = 0;
		gc.gridy = 1;
		panel.add(new JLabel(resultCount + " results found"), gc);
	}
	
	/*
	 * END HELPER METHODS
	 */
	
	/*
	 * BEGIN SEARCH METHODS
	 */
	
	/**
	 * Populates search results method using the search input fields.
	 */
	private void executeSearch() {
		
		this.taskResults.clear();
		this.templateResults.clear();
		this.processResults.clear();
		this.noteResults.clear();
    	
    	if (allProjectsCheckBox.isSelected()) {
			// Iterate through and search all projects.
			for (Object object : ProjectManager.getAllProjects()) {
				if (object instanceof Project) {
					Project project = (Project) object;
					
					TaskList taskList =
							CurrentStorage.get().openTaskList(project);
					TemplateList templateList =
							CurrentStorage.
							get().
							openTemplateList(project);
					ProcessList processList =
							CurrentStorage.
							get().
							openProcessList(project);
					NoteList noteList =
							CurrentStorage.
							get().
							openNoteList(project);
					
					taskResults.put(
							project,
							Search.searchTasks(
									this.searchText.getText(),
									taskList));
					templateResults.put(
							project,
							Search.searchTemplates(
									this.searchText.getText(),
									templateList));
					processResults.put(
							project,
							Search.searchProcesses(
									this.searchText.getText(),
									processList));
					noteResults.put(
							project,
							Search.searchNotes(
									this.searchText.getText(),
									noteList));
				}
			} 
		} else {
			Project project = CurrentProject.get();
			TaskList taskList = CurrentProject.getTaskList();
			TemplateList templateList = CurrentProject.getTemplateList();
			ProcessList processList = CurrentProject.getProcessList();
			NoteList noteList = CurrentProject.getNoteList();
			
			taskResults.put(
					project,
					Search.searchTasks(
							this.searchText.getText(),
							taskList));
			templateResults.put(
					project,
					Search.searchTemplates(
							this.searchText.getText(),
							templateList));
			processResults.put(
					project,
					Search.searchProcesses(
							this.searchText.getText(),
							processList));
			noteResults.put(
					project,
					Search.searchNotes(
							this.searchText.getText(),
							noteList));
		}
	}
	
	/*
	 * END SEARCH METHODS
	 */
	
	/**
	 * Handler method to bring up the chosen search result.
	 * @param target	The target search result
	 * @param project	The project the target belongs to
	 * @param event		The event provided from the initial action
	 */
	private void goTo_actionPerformed(Object target, Project project, ActionEvent event) {
		if (project.getID() != CurrentProject.get().getID()) {
			CurrentProject.set(project);
		}
		
		WorkPanel workPanel = ((AppFrame) frame).workPanel;
		this.dispose();
		
		if (target instanceof Task) {
			// switch to the TaskPanel on the Task's start date
			Task task = (Task) target;
			workPanel.tasksB.doClick();
			CurrentDate.set(task.getStartDate());
		} else if (target instanceof Template) {
			// switch to the TaskPanel and open the templates dialog
			workPanel.tasksB.doClick();
			workPanel.dailyItemsPanel.tasksPanel.editTemplateB.doClick();
		} else if (target instanceof Process) {
			// switch to the TaskPanel on the Process' start date
			Process process = (Process) target;
			workPanel.tasksB.doClick();
			CurrentDate.set(process.getStartDate());
		} else if (target instanceof Note) {
			// switch to the Notes window on the day of the note
			Note note = (Note) target;
			workPanel.notesB.doClick();
			CurrentDate.set(note.getDate());
		}
	}
}