package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
	private Task task;
	
	// Header
	private JPanel headerPanel;
	private JLabel headerLabel;
    
	// Logs
	private ButtonGroup logButtonGroup;
	private JScrollPane logScrollPane;
	private JPanel logPanel;
	private JPanel logInnerPanel;
    
	// Edit and Delete Buttons
	private JButton editButton;
	private JButton deleteButton;
	private JPanel buttonPanel;
	
	public LoggedTimeDialog(Frame frame, String title, Task task) {
		super(frame, title, true);
		this.task = task;
		
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
		logButtonGroup = new ButtonGroup();
		gc.ipadx = 3;
		gc.ipady = 3;
		gc.gridy = -1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.WEST;
		
		logInnerPanel = new JPanel(new GridBagLayout());
		
		List<LogPair> list = task.getLoggedTimes();
		
		for (int i = 0; i < list.size(); i++) {
			JRadioButton radioButton = new JRadioButton();
			DateFormat dateFormat = CalendarDate.getSimpleDateFormat();
			CalendarDate entryDate = new CalendarDate(list.get(i).getDate());
			long effortInHours = list.get(i).getLength() / 1000 / 60 / 60;
			JLabel hours = new JLabel(effortInHours + "");
			JLabel date = new JLabel(dateFormat.format(entryDate.getDate()));
			
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
			
			// The edit and delete buttons need to be enabled once a log is selected.
			radioButton.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					editButton.setEnabled(true);
					deleteButton.setEnabled(true);
			        LoggedTimeDialog.this.getRootPane().setDefaultButton(editButton);
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
		editButton = new JButton();
		editButton.setMaximumSize(new Dimension(100, 26));
		editButton.setMinimumSize(new Dimension(100, 26));
		editButton.setPreferredSize(new Dimension(100, 26));
		editButton.setText(Local.getString("Edit"));
		editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	// TODO edit
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
                // TODO delete
            }
        });
        deleteButton.setEnabled(false);
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(defaultBorder);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
}