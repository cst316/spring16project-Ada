package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicArrowButton;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Task;
import net.sf.memoranda.util.CurrentStorage;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

/**
 * UI for sorting tasks within a process
 * following confirmation of task addition
 * 
 * @author sean
 *
 */
public class ProcessTaskSortDialog extends JDialog {
	
	public boolean CANCELLED = true;
	String processId = null;
	ArrayList<String> taskIds;
	
	Border defaultBorder;
	
	// Header
	JPanel headerPanel;
	JLabel headerLabel;
	
	// Tasks
	JScrollPane taskScrollPane;
	JPanel taskPanel;
	JPanel taskInnerPanel;
	
	// Buttons
	JButton okButton;
	JPanel buttonPanel;
	
	// Header panel UI
	void drawHeaderPanel() {
		headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.setBackground(Color.WHITE);
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		
		headerLabel = new JLabel();
		headerLabel.setFont(new Font("Dialog", 0, 20));
		headerLabel.setForeground(new Color(0,0,124));
		headerLabel.setText(Local.getString("Rearrange Tasks as necessary"));
		headerPanel.add(headerLabel);
		this.getContentPane().add(headerPanel, BorderLayout.NORTH);
	}
	
	// Sort
	void sortTasks(ArrayList<String> ids) {
		String[] array = new String[ids.size()];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = ids.get(i);
		}
		
		CurrentProject.getProcessList().getProcess(processId).setTaskOrder(array);
		CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
		
		this.getContentPane().remove(taskPanel);
		drawTaskPanel();
		pack();
	}
	
	// Tasks UI
	void drawTaskPanel() {
		if (processId != null) {
			Collection<Task> tasks = CurrentProject.getProcessList().getProcess(processId).getTasks();
			taskIds = new ArrayList<>();
			List<Task> taskList = new ArrayList<>(tasks);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			GridBagConstraints gc = new GridBagConstraints();
			gc.ipadx = 3;
			gc.ipady = 3;
			gc.gridy = 0;
			gc.weightx = 1;
			gc.weighty = 1;
			gc.anchor = GridBagConstraints.WEST;
			
			taskInnerPanel = new JPanel(new GridBagLayout());
			
			{
				JLabel title = new JLabel(Local.getString("Task"));
				JLabel startDate = new JLabel(Local.getString("Start Date"));
				JLabel endDate = new JLabel(Local.getString("End Date"));
				
				gc.gridx = 1;
				taskInnerPanel.add(title, gc);
				gc.gridx = 2;
				taskInnerPanel.add(startDate, gc);
				gc.gridx = 3;
				taskInnerPanel.add(endDate, gc);
			}
			
			for (Task task : taskList) {
				Util.debug("Adding task: " + task.getText());
				
				BasicArrowButton up = new BasicArrowButton(BasicArrowButton.NORTH);
				BasicArrowButton dn = new BasicArrowButton(BasicArrowButton.SOUTH);
				JLabel title = new JLabel(task.getText());
				JLabel startDate = new JLabel(df.format(task.getStartDate().getDate()));
				JLabel endDate = new JLabel(df.format(task.getEndDate().getDate()));
				
				gc.gridy = gc.gridy + 1;
				gc.gridx = 0;
				gc.gridx = 1;
				gc.anchor = GridBagConstraints.WEST;
				taskInnerPanel.add(title, gc);
				gc.gridx = 2;
				taskInnerPanel.add(startDate, gc);
				gc.gridx = 3;
				taskInnerPanel.add(endDate, gc);
				gc.gridx = 4;
				gc.anchor = GridBagConstraints.CENTER;
				taskInnerPanel.add(up, gc);
				gc.gridx = 5;
				gc.anchor = GridBagConstraints.CENTER;
				taskInnerPanel.add(dn, gc);
				
				up.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent action) {
						int index = taskList.indexOf(task);
						
						if (index > 0) {
							Collections.swap(taskList, index, index - 1);
							Collections.swap(taskIds, index, index - 1);
							
							sortTasks(taskIds);
						}
					}
				});
				
				dn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent action) {
						int index = taskList.indexOf(task);
						
						if (index < taskList.size() - 1) {
							Collections.swap(taskList, index, index + 1);
							Collections.swap(taskIds, index, index + 1);
							
							sortTasks(taskIds);
						}
					}
				});
				
				taskIds.add(task.getID());
			}
			
			taskScrollPane = new JScrollPane(taskInnerPanel);
			
			taskPanel = new JPanel(new BorderLayout());
			taskPanel.setPreferredSize(new Dimension(400, 300));
			taskPanel.setBorder(defaultBorder);
			taskPanel.add(taskScrollPane, BorderLayout.CENTER);
			this.getContentPane().add(taskPanel, BorderLayout.CENTER);
		}
	}
	
	// Button UI
	void drawButtonsPanel() {
		
		// ***** OK BUTTON *****
		okButton = new JButton();
		okButton.setMaximumSize(new Dimension(100, 26));
		okButton.setMinimumSize(new Dimension(100, 26));
		okButton.setPreferredSize(new Dimension(100, 26));
		okButton.setText(Local.getString("Ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		
		// ***** PANEL *****
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(defaultBorder);
		buttonPanel.add(okButton);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	// Constructor
	public ProcessTaskSortDialog(Frame frame, String title, String processId) {
		super(frame, title, true);
		this.processId = processId;
		try {
			jbInit();
			pack();
		} catch (Exception e) {
			new ExceptionDialog(e);
			e.printStackTrace();
		}
	}
	
	// Draw it
	void jbInit() {
		this.setResizable(false);
		defaultBorder = BorderFactory.createEmptyBorder(3,3,3,3);
		
		drawHeaderPanel();
		drawTaskPanel();
		drawButtonsPanel();
	}
	
	// OK Button pressed
	private void okButton_actionPerformed(ActionEvent e) {
		CANCELLED = false;
		this.dispose();
	}
}
