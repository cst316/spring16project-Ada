package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import net.sf.memoranda.Task;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.LogPair;

/**
 * Displays the logged time for a provided Task. Allows the user to select
 * a log and edit or delete.
 * @author James Smith
 */
public class LoggedTimeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private boolean cancelled = true;
	
	private Border defaultBorder;
	private HashMap<JRadioButton, LogPair> logsMap;
	private JRadioButton selectedRadioButton;
	private Task task;
	
	// Header
	private JPanel headerPanel;
	private JLabel headerLabel;
    
	// Logs
	private ButtonGroup logButtonGroup;
	private JScrollPane logScrollPane;
	private JPanel logPanel;
	private JPanel logInnerPanel;
    
	// Buttons
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	private JPanel buttonPanel;
	
	public LoggedTimeDialog(Frame frame, String title, Task task) {
		super(frame, title, true);
		this.task = task;
		this.logsMap = new HashMap<JRadioButton, LogPair>();
		
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
            new ExceptionDialog(ex);
            ex.printStackTrace();
        }
	}
	
	/**
	 * Check if dialog is cancelled.
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/**
	 * Initialize components of the dialog.
	 */
	private void jbInit() {
		this.setResizable(false);
		defaultBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		
		drawHeader();
		drawLogPanel();
		drawButtons();
	}
	
	/**
	 * Sets up and defines the components and layout of the header.
	 */
	private void drawHeader() {
		headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        headerLabel = new JLabel();
        headerLabel.setFont(new java.awt.Font("Dialog", 0, 20));
        headerLabel.setForeground(new Color(0, 0, 124));
        headerLabel.setText(Local.getString(task.getText() + " logged time"));
        headerPanel.add(headerLabel);
        this.getContentPane().add(headerPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Sets up and defines the components and layout of the logs list.
	 */
	private void drawLogPanel() {
		GridBagConstraints gc = new GridBagConstraints();
		TreeSet<LogPair> logsTreeSet = new TreeSet<LogPair>(new Comparator<LogPair>() {

			@Override
			public int compare(LogPair lp1, LogPair lp2) {
				int compare = new CalendarDate(lp1.getDate()).getDate().compareTo(
						new CalendarDate(lp2.getDate()).getDate());
				
				if (compare == 0) {
					compare = lp1.getIndex() - lp2.getIndex();
				}
				
				return compare;
			}
		});
		
		logsMap.clear();
		selectedRadioButton = null;
		if (editButton != null) {
			editButton.setEnabled(false);
		}
		if (deleteButton != null) {
			deleteButton.setEnabled(false);
		}
		if (logPanel != null) {
			this.getContentPane().remove(logPanel);
		}
		logButtonGroup = new ButtonGroup();
		
		gc.ipadx = 3;
		gc.ipady = 3;
		gc.gridy = -1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.WEST;
		
		logInnerPanel = new JPanel(new GridBagLayout());
		
		logsTreeSet.addAll(task.getLoggedTimes());
		
		for (LogPair logPair : logsTreeSet) {
			JRadioButton radioButton = new JRadioButton();
			float effortInHours = (float)logPair.getLength() / 1000f / 60f / 60f;
			JLabel hours = new JLabel(effortInHours + "");
			JLabel date = new JLabel(logPair.getDate());
			
			gc.gridy = gc.gridy + 1;
			gc.gridx = 0;
			gc.anchor = GridBagConstraints.CENTER;
			logInnerPanel.add(radioButton, gc);
			gc.gridx = 1;
			gc.anchor = GridBagConstraints.WEST;
			logInnerPanel.add(hours, gc);
			gc.gridx = 2;
			logInnerPanel.add(date, gc);
			
			logButtonGroup.add(radioButton);			
			logsMap.put(radioButton, logPair);
			
			// The edit and delete buttons need to be enabled once a log is selected.
			radioButton.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					if (selectedRadioButton == null) {
						editButton.setEnabled(true);
						deleteButton.setEnabled(true);
				        getRootPane().setDefaultButton(editButton);
					}
					
					selectedRadioButton = radioButton;
				}
			});
		}
		
		logScrollPane = new JScrollPane(logInnerPanel);
		
		logPanel = new JPanel(new BorderLayout());
		logPanel.setPreferredSize(new Dimension(400, 300));
		logPanel.setBorder(defaultBorder);
		logPanel.add(logScrollPane, BorderLayout.CENTER);
		this.getContentPane().add(logPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Sets up and defines the components and layout of the ok and cancel buttons.
	 */
	private void drawButtons() {
		addButton = new JButton();
		addButton.setMaximumSize(new Dimension(100, 26));
		addButton.setMinimumSize(new Dimension(100, 26));
		addButton.setPreferredSize(new Dimension(100, 26));
		addButton.setText(Local.getString("Add"));
		addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	openLogEffortDialog(false);
            }
        });
		
		editButton = new JButton();
		editButton.setMaximumSize(new Dimension(100, 26));
		editButton.setMinimumSize(new Dimension(100, 26));
		editButton.setPreferredSize(new Dimension(100, 26));
		editButton.setText(Local.getString("Edit"));
		editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	openLogEffortDialog(true);
            }
        });
		editButton.setEnabled(false);
        
        deleteButton = new JButton();
        deleteButton.setMaximumSize(new Dimension(100, 26));
        deleteButton.setMinimumSize(new Dimension(100, 26));
        deleteButton.setPreferredSize(new Dimension(100, 26));
        deleteButton.setText(Local.getString("Delete"));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                deleteSelectedLog();
            }
        });
        deleteButton.setEnabled(false);
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(defaultBorder);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void openLogEffortDialog(boolean isEdit) {
		LogPair logPair = null;
		LogEffortDialog dialog = new LogEffortDialog(
    			App.getFrame(),
    			"Time log",
    			"Time log");
		
		if (isEdit) {
			logPair = logsMap.get(selectedRadioButton);
			long millis = logPair.getLength();
			float hours = ((float)millis / 1000f / 60f / 60f);
			dialog.timeField.setText(hours + "");
			
			CalendarDate date = new CalendarDate(logPair.getDate());
			dialog.jSpinnerLogDate.getModel().setValue(date.getDate());
			//dialog.jSpinnerLogDate.getModel().setValue("");
		}
		
		dialog.jSpinnerProgress.getModel().setValue(task.getProgress());
    	dialog.setLocationRelativeTo(App.getFrame());
    	dialog.setVisible(true);
    	
    	if (!dialog.CANCELLED) {
    		CalendarDate date = new CalendarDate(
    				(Date) dialog.jSpinnerLogDate.getModel().getValue());
    		int progress = ((Integer)dialog.jSpinnerProgress.getValue());
    		try {
				float hours = Float.parseFloat(dialog.timeField.getText());
				long millis = (long) (hours * 1000f * 60f * 60f);
				
				if (isEdit) {
					task.editLoggedTime(
							logPair.getIndex(),
							date.toString(),
							millis);
				} else {
					task.addLoggedTime(CalendarDate.getSimpleDateFormat().format(date.getDate()), millis);
				}
				
				task.setProgress(progress);
				drawLogPanel();
				pack();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(
						this,
						"Invalid hours: " + dialog.timeField.getText());
			}
    	}
	}
	
	private void deleteSelectedLog() {
		int selection = JOptionPane.showConfirmDialog(
				this,
				"Are you sure you wish to delete the log?");
		
		if (selection == JOptionPane.YES_OPTION) {
			LogPair logPair = logsMap.get(selectedRadioButton);
			task.removeLoggedTime(logPair.getIndex());
			drawLogPanel();
			pack();
		}
	}
}