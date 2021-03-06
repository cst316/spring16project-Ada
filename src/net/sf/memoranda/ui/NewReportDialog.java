package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Report;
import net.sf.memoranda.ReportImpl;
import net.sf.memoranda.Task;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

public class NewReportDialog extends JDialog {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2975505696367632812L;
	
	public boolean CANCELLED = true;
	private Report report;

	Border defaultBorder;
	
	// Header
    JPanel headerPanel;
    JLabel headerLabel;
    
    // Style Selection
    JLabel styleLabel;
    JRadioButton minimumRadioButton;
    JRadioButton mediumRadioButton;
    JRadioButton maximumRadioButton;
    JPanel stylePanel;
    
    // Task Selection
    int numTasksSelected;
    int numTotalTasks;
    Queue<JCheckBox> taskCheckBoxesQueue;
    Queue<Task> tasksQueue;
    JScrollPane taskScrollPane;
    JPanel taskPanel;
    JPanel taskInnerPanel;
    
    // OK and Cancel Buttons
    JCheckBox selectAllCheckBox;
	JButton okButton;
	JButton cancelButton;
	JPanel buttonPanel;
	
	public NewReportDialog(Frame frame, String title) {
		super(frame, title, true);
		try {
			jbInit();
			pack();
		}
        catch (Exception ex) {
            new ExceptionDialog(ex);
            ex.printStackTrace();
        }
	}
	
	public Report getReport() {
		return this.report;
	}
	
	void jbInit() {
		this.setResizable(false);
		defaultBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        
		drawHeaderPanel();
		drawStylePanel();
		drawTaskPanel();
		drawButtonsPanel();
	}
	
	void drawHeaderPanel() {
		headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        headerLabel = new JLabel();
        headerLabel.setFont(new java.awt.Font("Dialog", 0, 20));
        headerLabel.setForeground(new Color(0, 0, 124));
        headerLabel.setText(Local.getString("New Report"));
        headerLabel.setIcon(new ImageIcon(net.sf.memoranda.ui.AddResourceDialog.class.getResource(
            "resources/icons/project48.png")));
        headerPanel.add(headerLabel);
        this.getContentPane().add(headerPanel, BorderLayout.NORTH);
	}
	
	void drawStylePanel() {
		styleLabel = new JLabel();
		styleLabel.setBorder(defaultBorder);
		styleLabel.setText(Local.getString("Report Style"));
		
	    minimumRadioButton = new JRadioButton();
	    minimumRadioButton.setText(Local.getString("Brief"));
	    
	    mediumRadioButton = new JRadioButton();
	    mediumRadioButton.setText(Local.getString("Standard"));
	    mediumRadioButton.setSelected(true);
	    
	    maximumRadioButton = new JRadioButton();
	    maximumRadioButton.setText(Local.getString("Detailed"));
	    
	    ButtonGroup styleButtonGroup = new ButtonGroup();
	    styleButtonGroup.add(minimumRadioButton);
	    styleButtonGroup.add(mediumRadioButton);
	    styleButtonGroup.add(maximumRadioButton);
	    
	    stylePanel = new JPanel();
	    stylePanel.setLayout(new BoxLayout(stylePanel, BoxLayout.Y_AXIS));
	    stylePanel.setBorder(defaultBorder);
	    stylePanel.add(styleLabel);
	    stylePanel.add(minimumRadioButton);
	    stylePanel.add(mediumRadioButton);
	    stylePanel.add(maximumRadioButton);
	    this.getContentPane().add(stylePanel, BorderLayout.WEST);
	}
	
	void drawTaskPanel() {
		Collection<Task> tasksCollection = CurrentProject.getTaskList().getTopLevelTasks();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		GridBagConstraints gc = new GridBagConstraints();
		gc.ipadx = 3;
		gc.ipady = 3;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.WEST;
		numTotalTasks = 0;
		
		taskInnerPanel = new JPanel(new GridBagLayout());
		taskCheckBoxesQueue = new ArrayDeque<JCheckBox>();
		tasksQueue = new ArrayDeque<Task>();
		
		{
			JLabel title = new JLabel(Local.getString("Task"));
			JLabel startDate = new JLabel(Local.getString("Start Date"));
			JLabel endDate = new JLabel(Local.getString("End Date"));;

			gc.gridx = 1;
			taskInnerPanel.add(title, gc);
			gc.gridx = 2;
			taskInnerPanel.add(startDate, gc);
			gc.gridx = 3;
			taskInnerPanel.add(endDate, gc);
		}
		
		for (Task task : tasksCollection) {
			JCheckBox cb = new JCheckBox();
			JLabel title = new JLabel(Local.getString(task.getText()));
			JLabel startDate = new JLabel(dateFormat.format(task.getStartDate().getDate()));
			JLabel endDate = new JLabel(dateFormat.format(task.getEndDate().getDate()));
			
			gc.gridy = gc.gridy + 1;
			gc.gridx = 0;
			gc.anchor = GridBagConstraints.CENTER;
			taskInnerPanel.add(cb, gc);
			gc.gridx = 1;
			gc.anchor = GridBagConstraints.WEST;
			taskInnerPanel.add(title, gc);
			gc.gridx = 2;
			taskInnerPanel.add(startDate, gc);
			gc.gridx = 3;
			taskInnerPanel.add(endDate, gc);
			
			cb.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					taskCheckBox_itemStateChanged(arg0);
				}
			});
			
			tasksQueue.add(task);
			taskCheckBoxesQueue.add(cb);
			numTotalTasks++;
		}
		
		taskScrollPane = new JScrollPane(taskInnerPanel);
		
		taskPanel = new JPanel(new BorderLayout());
		taskPanel.setPreferredSize(new Dimension(400, 300));
		taskPanel.setBorder(defaultBorder);
		taskPanel.add(taskScrollPane, BorderLayout.CENTER);
		this.getContentPane().add(taskPanel, BorderLayout.CENTER);
	}
	
	void drawButtonsPanel() {
		selectAllCheckBox = new JCheckBox("Select all");
		selectAllCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectAllCheckBox_actionPerformed(arg0);
			}
		});
		
		okButton = new JButton();
		okButton.setMaximumSize(new Dimension(100, 26));
		okButton.setMinimumSize(new Dimension(100, 26));
		okButton.setPreferredSize(new Dimension(100, 26));
		okButton.setText(Local.getString("Ok"));
		okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerformed(e);
            }
        });
		okButton.setEnabled(false);
        
        cancelButton = new JButton();
        cancelButton.setMaximumSize(new Dimension(100, 26));
        cancelButton.setMinimumSize(new Dimension(100, 26));
        cancelButton.setPreferredSize(new Dimension(100, 26));
        cancelButton.setText(Local.getString("Cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NewReportDialog.this.dispose();
            }
        });
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(defaultBorder);
        buttonPanel.add(selectAllCheckBox);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void okButton_actionPerformed(ActionEvent e) {
		String[] tasksArray;
		int numTasks = 0;
		
		while (!taskCheckBoxesQueue.isEmpty()) {
			Task task = tasksQueue.remove();
			JCheckBox cb = taskCheckBoxesQueue.remove();
			
			if (cb.isSelected()) {
				numTasks++;
				tasksQueue.add(task);
			}
		}
		
		tasksArray = new String[numTasks];
		
		for (int i=0; i<numTasks; i++) {
			tasksArray[i] = tasksQueue.remove().getID();
		}
		
		report = new ReportImpl();
		if (minimumRadioButton.isSelected()) {
			report.setStyle(Report.STYLE_MINIMUM);
		}
		else if (mediumRadioButton.isSelected()) {
			report.setStyle(Report.STYLE_MEDIUM);
		}
		else  {// maximumRadioButton.isSelected()
			report.setStyle(Report.STYLE_MAXIMUM);
		}
		report.setTasks(tasksArray);
		
		CANCELLED = false;
		this.dispose();
	}
	
	private void taskCheckBox_itemStateChanged(ItemEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		
		if (cb.isSelected()) {
			numTasksSelected++;
		}
		else {
			numTasksSelected--;
		}
		
		if (numTasksSelected == 0) {
			this.getRootPane().setDefaultButton(null);
			okButton.setEnabled(false);
		}
		else {
			okButton.setEnabled(true);
	        this.getRootPane().setDefaultButton(okButton);
		}
		
		if (numTasksSelected == numTotalTasks) {
			selectAllCheckBox.setSelected(true);
		}
		else {
			selectAllCheckBox.setSelected(false);
		}
	}
	
	private void selectAllCheckBox_actionPerformed(ActionEvent e) {
		if (selectAllCheckBox.isSelected()) {
			for (JCheckBox cb : taskCheckBoxesQueue) {
				cb.setSelected(true);
			}
		}
		else {
			for (JCheckBox cb : taskCheckBoxesQueue) {
				cb.setSelected(false);
			}
		}
	}
}