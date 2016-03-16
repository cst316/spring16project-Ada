package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.History;
import net.sf.memoranda.NoteList;
import net.sf.memoranda.Process;
import net.sf.memoranda.Project;
import net.sf.memoranda.ProjectListener;
import net.sf.memoranda.ResourcesList;
import net.sf.memoranda.Task;
import net.sf.memoranda.TaskList;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.date.CurrentDate;
import net.sf.memoranda.date.DateListener;
import net.sf.memoranda.util.Context;
import net.sf.memoranda.util.CurrentStorage;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

/*$Id: TaskPanel.java,v 1.27 2007/01/17 20:49:12 killerjoe Exp $*/
public class TaskPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    JButton historyBackB = new JButton();
    JToolBar tasksToolBar = new JToolBar();
    JButton historyForwardB = new JButton();
    JButton newTaskB = new JButton();
    JButton subTaskB = new JButton();
    JButton editTaskB = new JButton();
    JButton removeTaskB = new JButton();
    JButton completeTaskB = new JButton();
    JButton editTemplateB = new JButton();
    JButton newProcessB = new JButton();
    JButton editProcessB = new JButton();
    JButton addProcessTaskB = new JButton();
    
	JCheckBoxMenuItem ppShowActiveOnlyChB = new JCheckBoxMenuItem();
		
    JScrollPane scrollPane = new JScrollPane();
    TaskTable taskTable = new TaskTable();
	JMenuItem ppEditTask = new JMenuItem();
	JPopupMenu taskPPMenu = new JPopupMenu();
	JMenuItem ppRemoveTask = new JMenuItem();
	JMenuItem ppNewTask = new JMenuItem();
	JMenuItem ppCompleteTask = new JMenuItem();
	//JMenuItem ppSubTasks = new JMenuItem();
	//JMenuItem ppParentTask = new JMenuItem();
	JMenuItem ppAddSubTask = new JMenuItem();
	JMenuItem ppCalcTask = new JMenuItem();
	DailyItemsPanel parentPanel = null;

    public TaskPanel(DailyItemsPanel _parentPanel) {
        try {
            parentPanel = _parentPanel;
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    void jbInit() throws Exception {
        tasksToolBar.setFloatable(false);

        historyBackB.setAction(History.historyBackAction);
        historyBackB.setFocusable(false);
        historyBackB.setBorderPainted(false);
        historyBackB.setToolTipText(Local.getString("History back"));
        historyBackB.setRequestFocusEnabled(false);
        historyBackB.setPreferredSize(new Dimension(24, 24));
        historyBackB.setMinimumSize(new Dimension(24, 24));
        historyBackB.setMaximumSize(new Dimension(24, 24));
        historyBackB.setText("");

        historyForwardB.setAction(History.historyForwardAction);
        historyForwardB.setBorderPainted(false);
        historyForwardB.setFocusable(false);
        historyForwardB.setPreferredSize(new Dimension(24, 24));
        historyForwardB.setRequestFocusEnabled(false);
        historyForwardB.setToolTipText(Local.getString("History forward"));
        historyForwardB.setMinimumSize(new Dimension(24, 24));
        historyForwardB.setMaximumSize(new Dimension(24, 24));
        historyForwardB.setText("");

        newTaskB.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new.png")));
        newTaskB.setEnabled(true);
        newTaskB.setMaximumSize(new Dimension(24, 24));
        newTaskB.setMinimumSize(new Dimension(24, 24));
        newTaskB.setToolTipText(Local.getString("Create new task"));
        newTaskB.setRequestFocusEnabled(false);
        newTaskB.setPreferredSize(new Dimension(24, 24));
        newTaskB.setFocusable(false);
        newTaskB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newTaskB_actionPerformed(e);
            }
        });
        newTaskB.setBorderPainted(false);
        
        subTaskB.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new_sub.png")));
        subTaskB.setEnabled(true);
        subTaskB.setMaximumSize(new Dimension(24, 24));
        subTaskB.setMinimumSize(new Dimension(24, 24));
        subTaskB.setToolTipText(Local.getString("Add subtask"));
        subTaskB.setRequestFocusEnabled(false);
        subTaskB.setPreferredSize(new Dimension(24, 24));
        subTaskB.setFocusable(false);
        subTaskB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSubTask_actionPerformed(e);
            }
        });
        subTaskB.setBorderPainted(false);

        editTaskB.setBorderPainted(false);
        editTaskB.setFocusable(false);
        editTaskB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editTaskB_actionPerformed(e);
            }
        });
        editTaskB.setPreferredSize(new Dimension(24, 24));
        editTaskB.setRequestFocusEnabled(false);
        editTaskB.setToolTipText(Local.getString("Edit task"));
        editTaskB.setMinimumSize(new Dimension(24, 24));
        editTaskB.setMaximumSize(new Dimension(24, 24));
//        editTaskB.setEnabled(true);
        editTaskB.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_edit.png")));

        removeTaskB.setBorderPainted(false);
        removeTaskB.setFocusable(false);
        removeTaskB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeTaskB_actionPerformed(e);
            }
        });
        removeTaskB.setPreferredSize(new Dimension(24, 24));
        removeTaskB.setRequestFocusEnabled(false);
        removeTaskB.setToolTipText(Local.getString("Remove task"));
        removeTaskB.setMinimumSize(new Dimension(24, 24));
        removeTaskB.setMaximumSize(new Dimension(24, 24));
        removeTaskB.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_remove.png")));
        
        completeTaskB.setBorderPainted(false);
        completeTaskB.setFocusable(false);
        completeTaskB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppCompleteTask_actionPerformed(e);
            }
        });
        completeTaskB.setPreferredSize(new Dimension(24, 24));
        completeTaskB.setRequestFocusEnabled(false);
        completeTaskB.setToolTipText(Local.getString("Complete task"));
        completeTaskB.setMinimumSize(new Dimension(24, 24));
        completeTaskB.setMaximumSize(new Dimension(24, 24));
        completeTaskB.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_complete.png")));

        editTemplateB.setBorderPainted(false);
        editTemplateB.setFocusable(false);
        editTemplateB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                openEditTemplate();
            }
        });
        editTemplateB.setPreferredSize(new Dimension(24, 24));
        editTemplateB.setRequestFocusEnabled(true);
        editTemplateB.setToolTipText(Local.getString("Edit template"));
        editTemplateB.setMinimumSize(new Dimension(24, 24));
        editTemplateB.setMaximumSize(new Dimension(24, 24));
        editTemplateB.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource(
            		"resources/icons/template_edit.png")));

        newProcessB.setIcon(
                new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/process_new.png")));
        newProcessB.setEnabled(true);
        newProcessB.setMaximumSize(new Dimension(24, 24));
        newProcessB.setMinimumSize(new Dimension(24, 24));
        newProcessB.setToolTipText(Local.getString("Create new process"));
        newProcessB.setRequestFocusEnabled(false);
        newProcessB.setPreferredSize(new Dimension(24, 24));
        newProcessB.setFocusable(false);
        newProcessB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	newProcessB_actionPerformed(e);
            }
        });
        newProcessB.setBorderPainted(false);
        
        editProcessB.setIcon(
                new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_edit.png")));
        editProcessB.setEnabled(false);
        editProcessB.setMaximumSize(new Dimension(24, 24));
        editProcessB.setMinimumSize(new Dimension(24, 24));
        editProcessB.setToolTipText(Local.getString("Edit process"));
        editProcessB.setRequestFocusEnabled(false);
        editProcessB.setPreferredSize(new Dimension(24, 24));
        editProcessB.setFocusable(false);
        editProcessB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	editProcessB_actionPerformed(e);
            }
        });
        editProcessB.setBorderPainted(false);
        
        addProcessTaskB.setIcon(
                new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new.png")));
        addProcessTaskB.setEnabled(false);
        addProcessTaskB.setMaximumSize(new Dimension(24, 24));
        addProcessTaskB.setMinimumSize(new Dimension(24, 24));
        addProcessTaskB.setToolTipText(Local.getString("Add task to process"));
        addProcessTaskB.setRequestFocusEnabled(false);
        addProcessTaskB.setPreferredSize(new Dimension(24, 24));
        addProcessTaskB.setFocusable(false);
        addProcessTaskB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	addProcessTaskB_actionPerformed(e);
            }
        });
        addProcessTaskB.setBorderPainted(false);
            
		// added by rawsushi
//		showActiveOnly.setBorderPainted(false);
//		showActiveOnly.setFocusable(false);
//		showActiveOnly.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				toggleShowActiveOnly_actionPerformed(e);
//			}
//		});
//		showActiveOnly.setPreferredSize(new Dimension(24, 24));
//		showActiveOnly.setRequestFocusEnabled(false);
//		if (taskTable.isShowActiveOnly()) {
//			showActiveOnly.setToolTipText(Local.getString("Show All"));			
//		}
//		else {
//			showActiveOnly.setToolTipText(Local.getString("Show Active Only"));			
//		}
//		showActiveOnly.setMinimumSize(new Dimension(24, 24));
//		showActiveOnly.setMaximumSize(new Dimension(24, 24));
//		showActiveOnly.setIcon(
//			new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_remove.png")));
		// added by rawsushi
		
		ppShowActiveOnlyChB.setFont(new java.awt.Font("Dialog", 1, 11));
		ppShowActiveOnlyChB.setText(
			Local.getString("Show Active only"));
		ppShowActiveOnlyChB
			.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleShowActiveOnly_actionPerformed(e);
			}
		});		
		boolean isShao =
			(Context.get("SHOW_ACTIVE_TASKS_ONLY") != null)
				&& (Context.get("SHOW_ACTIVE_TASKS_ONLY").equals("true"));
		ppShowActiveOnlyChB.setSelected(isShao);
		toggleShowActiveOnly_actionPerformed(null);

		/*showActiveOnly.setPreferredSize(new Dimension(24, 24));
		showActiveOnly.setRequestFocusEnabled(false);
		if (taskTable.isShowActiveOnly()) {
			showActiveOnly.setToolTipText(Local.getString("Show All"));			
		}
		else {
			showActiveOnly.setToolTipText(Local.getString("Show Active Only"));			
		}
		showActiveOnly.setMinimumSize(new Dimension(24, 24));
		showActiveOnly.setMaximumSize(new Dimension(24, 24));
		showActiveOnly.setIcon(
			new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_active.png")));*/
		// added by rawsushi


        this.setLayout(borderLayout1);
        scrollPane.getViewport().setBackground(Color.white);
        /*taskTable.setMaximumSize(new Dimension(32767, 32767));
        taskTable.setRowHeight(24);*/
        ppEditTask.setFont(new java.awt.Font("Dialog", 1, 11));
    ppEditTask.setText(Local.getString("Edit task")+"...");
    ppEditTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppEditTask_actionPerformed(e);
            }
        });
    ppEditTask.setEnabled(false);
    ppEditTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_edit.png")));
    taskPPMenu.setFont(new java.awt.Font("Dialog", 1, 10));
    ppRemoveTask.setFont(new java.awt.Font("Dialog", 1, 11));
    ppRemoveTask.setText(Local.getString("Remove task"));
    ppRemoveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppRemoveTask_actionPerformed(e);
            }
        });
    ppRemoveTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_remove.png")));
    ppRemoveTask.setEnabled(false);
    ppNewTask.setFont(new java.awt.Font("Dialog", 1, 11));
    ppNewTask.setText(Local.getString("New task")+"...");
    ppNewTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppNewTask_actionPerformed(e);
            }
        });
    ppNewTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new.png")));

    ppAddSubTask.setFont(new java.awt.Font("Dialog", 1, 11));
    ppAddSubTask.setText(Local.getString("Add subtask"));
    ppAddSubTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppAddSubTask_actionPerformed(e);
            }
        });
    ppAddSubTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new_sub.png")));

    /*
    ppSubTasks.setFont(new java.awt.Font("Dialog", 1, 11));
    ppSubTasks.setText(Local.getString("List sub tasks"));
    ppSubTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppListSubTasks_actionPerformed(e);
            }
        });
    ppSubTasks.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new.png")));

    ppParentTask.setFont(new java.awt.Font("Dialog", 1, 11));
    ppParentTask.setText(Local.getString("Parent Task"));
    ppParentTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ppParentTask_actionPerformed(e);
            }
        });
    ppParentTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_new.png")));
    */

	ppCompleteTask.setFont(new java.awt.Font("Dialog", 1, 11));
	ppCompleteTask.setText(Local.getString("Complete task"));
	ppCompleteTask.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ppCompleteTask_actionPerformed(e);
			}
		});
	ppCompleteTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_complete.png")));
	ppCompleteTask.setEnabled(false);

	ppCalcTask.setFont(new java.awt.Font("Dialog", 1, 11));
	ppCalcTask.setText(Local.getString("Calculate task data"));
	ppCalcTask.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ppCalcTask_actionPerformed(e);
			}
		});
	ppCalcTask.setIcon(new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/todo_complete.png")));
	ppCalcTask.setEnabled(false);

    scrollPane.getViewport().add(taskTable, null);
        this.add(scrollPane, BorderLayout.CENTER);
        tasksToolBar.add(historyBackB, null);
        tasksToolBar.add(historyForwardB, null);
        tasksToolBar.addSeparator(new Dimension(8, 24));

        tasksToolBar.add(newTaskB, null);
        tasksToolBar.add(subTaskB, null);
        tasksToolBar.add(removeTaskB, null);
        tasksToolBar.addSeparator(new Dimension(8, 24));
        tasksToolBar.add(editTaskB, null);
        tasksToolBar.add(completeTaskB, null);
        tasksToolBar.addSeparator(new Dimension(8, 24));
        tasksToolBar.add(editTemplateB, null);
        tasksToolBar.addSeparator(new Dimension(8, 24));
        tasksToolBar.add(newProcessB, null);
        tasksToolBar.add(editProcessB, null);
        tasksToolBar.add(addProcessTaskB, null);

		//tasksToolBar.add(showActiveOnly, null);
        

        this.add(tasksToolBar, BorderLayout.NORTH);

        PopupListener ppListener = new PopupListener();
        scrollPane.addMouseListener(ppListener);
        taskTable.addMouseListener(ppListener);



        CurrentDate.addDateListener(new DateListener() {
            public void dateChange(CalendarDate d) {
                newTaskB.setEnabled(d.inPeriod(CurrentProject.get().getStartDate(), CurrentProject.get().getEndDate()));
            }
        });
        CurrentProject.addProjectListener(new ProjectListener() {
            public void projectChange(Project p, NoteList nl, TaskList tl, ResourcesList rl) {
                newTaskB.setEnabled(
                    CurrentDate.get().inPeriod(p.getStartDate(), p.getEndDate()));
            }
            public void projectWasChanged() {
            	//taskTable.setCurrentRootTask(null); //XXX
            }
        });
        taskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                
            	boolean taskSelected = false;
            	boolean processSelected = false;
            	Object selectedItem;
            	
            	if (taskTable.getRowCount() > 0 && taskTable.getSelectedRowCount() == 1) {
            		selectedItem = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK);
                	taskSelected = (selectedItem instanceof Task);
                	processSelected = (selectedItem instanceof Process);
            	}
            	
                editTaskB.setEnabled(taskSelected);
                ppEditTask.setEnabled(taskSelected);
                removeTaskB.setEnabled(taskSelected);
                ppRemoveTask.setEnabled(taskSelected);
				
				ppCompleteTask.setEnabled(taskSelected);
				completeTaskB.setEnabled(taskSelected);
				ppAddSubTask.setEnabled(taskSelected);
				//ppSubTasks.setEnabled(enbl); // default value to be over-written later depending on whether it has sub tasks
				ppCalcTask.setEnabled(taskSelected); // default value to be over-written later depending on whether it has sub tasks
				
				editProcessB.setEnabled(processSelected);
				addProcessTaskB.setEnabled(processSelected);
				
				/*if (taskTable.getCurrentRootTask() == null) {
					ppParentTask.setEnabled(false);
				}
				else {
					ppParentTask.setEnabled(true);
				}XXX*/
				
                if (taskSelected) {   
    				String thisTaskId = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString();
    				
    				boolean hasSubTasks = CurrentProject.getTaskList().hasSubTasks(thisTaskId);
    				//ppSubTasks.setEnabled(hasSubTasks);
    				ppCalcTask.setEnabled(hasSubTasks);
    				Task t = CurrentProject.getTaskList().getTask(thisTaskId);
    				parentPanel.calendar.jnCalendar.renderer.setTask(t);
                    parentPanel.calendar.jnCalendar.updateUI();
                }
                else {
                    parentPanel.calendar.jnCalendar.renderer.setTask(null);
                    parentPanel.calendar.jnCalendar.updateUI();
                }
            }
        });
        editTaskB.setEnabled(false);
        removeTaskB.setEnabled(false);
		completeTaskB.setEnabled(false);
		ppAddSubTask.setEnabled(false);
		//ppSubTasks.setEnabled(false);
		//ppParentTask.setEnabled(false);
    taskPPMenu.add(ppEditTask);
    
    taskPPMenu.addSeparator();
    taskPPMenu.add(ppNewTask);
    taskPPMenu.add(ppAddSubTask);
    taskPPMenu.add(ppRemoveTask);
    
    taskPPMenu.addSeparator();
	taskPPMenu.add(ppCompleteTask);
	taskPPMenu.add(ppCalcTask);
	
    //taskPPMenu.addSeparator();
    
    //taskPPMenu.add(ppSubTasks);
    
    //taskPPMenu.addSeparator();
    //taskPPMenu.add(ppParentTask);
    
    taskPPMenu.addSeparator();
	taskPPMenu.add(ppShowActiveOnlyChB);

	
		// define key actions in TaskPanel:
		// - KEY:DELETE => delete tasks (recursivly).
		// - KEY:INTERT => insert new Subtask if another is selected.
		// - KEY:INSERT => insert new Task if nothing is selected.
		// - KEY:SPACE => finish Task.
		taskTable.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					// Only elect to delete a Process if it's the only item selected.
					if (taskTable.getSelectedRowCount() == 1 &&
							taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK) instanceof Process) {
						
						// TODO
					}
					else {
						ppRemoveTask_actionPerformed(null);
					}
				}
				
				else if(e.getKeyCode()==KeyEvent.VK_INSERT) {
					if(taskTable.getSelectedRows().length>0) {
						Object o = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK);
						if (o instanceof Task) {
							ppAddSubTask_actionPerformed(null);
						}
						else if (o instanceof Process) {
							addProcessTaskB_actionPerformed(null);							
						}
					}
					else {
						ppNewTask_actionPerformed(null);						
					}
				}
				
				else if(e.getKeyCode()==KeyEvent.VK_SPACE
						&& taskTable.getSelectedRows().length>0) {
					ppCompleteTask_actionPerformed(null);
				}
			}
			public void	keyReleased(KeyEvent e){}
			public void keyTyped(KeyEvent e){} 
		});	

    }

    void editTaskB_actionPerformed(ActionEvent e) {
        Task t =
            CurrentProject.getTaskList().getTask(
                taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString());
        Process p = t.getProcess();
        TaskDialog dlg;
        if (p == null) {
        	dlg = new TaskDialog(App.getFrame(), Local.getString("Edit task"), null);
        }
        else {
        	dlg = new TaskDialog(App.getFrame(), Local.getString("Edit task"), p.getID());
        }
        Dimension frmSize = App.getFrame().getSize();
        Point loc = App.getFrame().getLocation();
        dlg.setLocation((frmSize.width - dlg.getSize().width) / 2 + loc.x, (frmSize.height - dlg.getSize().height) / 2 + loc.y);
        dlg.jTextFieldName.setText(t.getText());
        dlg.jTextFieldType.setText(t.getType());
        dlg.descriptionField.setText(t.getDescription());
        dlg.jSpinnerStartDate.getModel().setValue(t.getStartDate().getDate());
        dlg.jSpinnerEndDate.getModel().setValue(t.getEndDate().getDate());
        dlg.jComboBoxPriority.setSelectedIndex(t.getPriority());                
        dlg.effortField.setText(Util.getHoursFromMillis(t.getEffort()));
	if((t.getStartDate().getDate()).after(t.getEndDate().getDate()))
		dlg.jCheckBoxEndDate.setSelected(false);
	else
		dlg.jCheckBoxEndDate.setSelected(true);
		dlg.jSpinnerProgress.setValue(new Integer(t.getProgress()));
 	dlg.chkEndDate_actionPerformed(null);	
        dlg.setVisible(true);
        if (dlg.CANCELLED)
            return;
        CalendarDate sd = new CalendarDate((Date) dlg.jSpinnerStartDate.getModel().getValue());
//        CalendarDate ed = new CalendarDate((Date) dlg.endDate.getModel().getValue());
         CalendarDate ed;
 		if(dlg.jCheckBoxEndDate.isSelected())
 			ed = new CalendarDate((Date) dlg.jSpinnerEndDate.getModel().getValue());
 		else
 			ed = null;
        t.setStartDate(sd);
        t.setEndDate(ed);
        t.setText(dlg.jTextFieldName.getText());
        t.setType(dlg.jTextFieldType.getText());
        t.setDescription(dlg.descriptionField.getText());
        t.setPriority(dlg.jComboBoxPriority.getSelectedIndex());
        t.setEffort(Util.getMillisFromHours(dlg.effortField.getText()));
        t.setProgress(((Integer)dlg.jSpinnerProgress.getValue()).intValue());
        
//		CurrentProject.getTaskList().adjustParentTasks(t);

        CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
        taskTable.tableChanged();
        parentPanel.updateIndicators();
        //taskTable.updateUI();
    }

    void newTaskB_actionPerformed(ActionEvent e) {
        TaskDialog dlg = new TaskDialog(App.getFrame(), Local.getString("New task"), null);
        
        //XXX String parentTaskId = taskTable.getCurrentRootTask();
        
        Dimension frmSize = App.getFrame().getSize();
        Point loc = App.getFrame().getLocation();
        dlg.jSpinnerStartDate.getModel().setValue(CurrentDate.get().getDate());
        dlg.jSpinnerEndDate.getModel().setValue(CurrentDate.get().getDate());
        dlg.setLocation((frmSize.width - dlg.getSize().width) / 2 + loc.x, (frmSize.height - dlg.getSize().height) / 2 + loc.y);
        dlg.setVisible(true);
        if (dlg.CANCELLED)
            return;
        CalendarDate sd = new CalendarDate((Date) dlg.jSpinnerStartDate.getModel().getValue());
//        CalendarDate ed = new CalendarDate((Date) dlg.endDate.getModel().getValue());
          CalendarDate ed;
 		if(dlg.jCheckBoxEndDate.isSelected())
 			ed = new CalendarDate((Date) dlg.jSpinnerEndDate.getModel().getValue());
 		else
 			ed = null;
        long effort = Util.getMillisFromHours(dlg.effortField.getText());
		//XXX Task newTask = CurrentProject.getTaskList().createTask(sd, ed, dlg.todoField.getText(), dlg.priorityCB.getSelectedIndex(),effort, dlg.descriptionField.getText(),parentTaskId);
		Task newTask = CurrentProject.getTaskList().createTask(sd, ed, dlg.jTextFieldName.getText(), dlg.jTextFieldType.getText(), dlg.jComboBoxPriority.getSelectedIndex(),effort, dlg.descriptionField.getText(),null);
//		CurrentProject.getTaskList().adjustParentTasks(newTask);
		newTask.setProgress(((Integer)dlg.jSpinnerProgress.getValue()).intValue());
        CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
        taskTable.tableChanged();
        parentPanel.updateIndicators();
        //taskTable.updateUI();
    }

    void addSubTask_actionPerformed(ActionEvent e) {
        String parentTaskId = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString();
        
//        Util.debug("Adding sub task under " + parentTaskId);
        
        Dimension frmSize = App.getFrame().getSize();
        Point loc = App.getFrame().getLocation();
		Task parent = CurrentProject.getTaskList().getTask(parentTaskId);
		Process parentProc = parent.getProcess();
		CalendarDate todayD = CurrentDate.get();
        TaskDialog dlg;
        if (parentProc == null) {
        	dlg = new TaskDialog(App.getFrame(), Local.getString("New Task"), null);
        }
        else {
        	dlg = new TaskDialog(App.getFrame(), Local.getString("New Task"), parentProc.getID());
        }
		if (todayD.after(parent.getStartDate()))
			dlg.setStartDate(todayD);
		else
			dlg.setStartDate(parent.getStartDate());
		if (parent.getEndDate() != null) 
			dlg.setEndDate(parent.getEndDate());
		else 
			dlg.setEndDate(CurrentProject.get().getEndDate());
		dlg.setStartDateLimit(parent.getStartDate(), parent.getEndDate());
		dlg.setEndDateLimit(parent.getStartDate(), parent.getEndDate());
        dlg.setLocation((frmSize.width - dlg.getSize().width) / 2 + loc.x, (frmSize.height - dlg.getSize().height) / 2 + loc.y);
        dlg.setVisible(true);
        if (dlg.CANCELLED)
            return;
        CalendarDate sd = new CalendarDate((Date) dlg.jSpinnerStartDate.getModel().getValue());
//        CalendarDate ed = new CalendarDate((Date) dlg.endDate.getModel().getValue());
          CalendarDate ed;
 		if(dlg.jCheckBoxEndDate.isSelected())
 			ed = new CalendarDate((Date) dlg.jSpinnerEndDate.getModel().getValue());
 		else
 			ed = null;
        long effort = Util.getMillisFromHours(dlg.effortField.getText());
		Task newTask = CurrentProject.getTaskList().createTask(sd, ed, dlg.jTextFieldName.getText(), dlg.jTextFieldType.getText(), dlg.jComboBoxPriority.getSelectedIndex(),effort, dlg.descriptionField.getText(),parentTaskId);
        newTask.setProgress(((Integer)dlg.jSpinnerProgress.getValue()).intValue());
//		CurrentProject.getTaskList().adjustParentTasks(newTask);

		CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
        taskTable.tableChanged();
        parentPanel.updateIndicators();
        //taskTable.updateUI();
    }

    void calcTask_actionPerformed(ActionEvent e) {
        TaskCalcDialog dlg = new TaskCalcDialog(App.getFrame());
        dlg.pack();
        Task t = CurrentProject.getTaskList().getTask(taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString());
        
        Dimension frmSize = App.getFrame().getSize();
        Point loc = App.getFrame().getLocation();
        
        dlg.setLocation((frmSize.width - dlg.getSize().width) / 2 + loc.x, (frmSize.height - dlg.getSize().height) / 2 + loc.y);
        dlg.setVisible(true);
        if (dlg.CANCELLED) {
            return;            
        }
        
        TaskList tl = CurrentProject.getTaskList();
        if(dlg.calcEffortChB.isSelected()) {
            t.setEffort(tl.calculateTotalEffortFromSubTasks(t));
        }
        
        if(dlg.compactDatesChB.isSelected()) {
            t.setStartDate(tl.getEarliestStartDateFromSubTasks(t));
            t.setEndDate(tl.getLatestEndDateFromSubTasks(t));
        }
        
        if(dlg.calcCompletionChB.isSelected()) {
            long[] res = tl.calculateCompletionFromSubTasks(t);
            int thisProgress = (int) Math.round((((double)res[0] / (double)res[1]) * 100));
            t.setProgress(thisProgress);
        }
        
//        CalendarDate sd = new CalendarDate((Date) dlg.startDate.getModel().getValue());
////        CalendarDate ed = new CalendarDate((Date) dlg.endDate.getModel().getValue());
//          CalendarDate ed;
// 		if(dlg.chkEndDate.isSelected())
// 			ed = new CalendarDate((Date) dlg.endDate.getModel().getValue());
// 		else
// 			ed = new CalendarDate(0,0,0);
//        long effort = Util.getMillisFromHours(dlg.effortField.getText());
//		Task newTask = CurrentProject.getTaskList().createTask(sd, ed, dlg.todoField.getText(), dlg.priorityCB.getSelectedIndex(),effort, dlg.descriptionField.getText(),parentTaskId);
//		
		
        CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
        taskTable.tableChanged();
//        parentPanel.updateIndicators();
        //taskTable.updateUI();
    }

    void listSubTasks_actionPerformed(ActionEvent e) {
        String parentTaskId = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString();
        
        //XXX taskTable.setCurrentRootTask(parentTaskId); 
		taskTable.tableChanged();

//        parentPanel.updateIndicators();
//        //taskTable.updateUI();
    }

    void parentTask_actionPerformed(ActionEvent e) {
//    	String taskId = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString();
//      
//    	Task t = CurrentProject.getTaskList().getTask(taskId);
    	/*XXX Task t2 = CurrentProject.getTaskList().getTask(taskTable.getCurrentRootTask());
    	
    	String parentTaskId = t2.getParent();
    	if((parentTaskId == null) || (parentTaskId.equals(""))) {
    		parentTaskId = null;
    	}
    	taskTable.setCurrentRootTask(parentTaskId); 
    	taskTable.tableChanged();*/

//      parentPanel.updateIndicators();
//      //taskTable.updateUI();
  }

    void removeTaskB_actionPerformed(ActionEvent e) {
    	boolean hasProcess = false;
        String msg;
        String thisTaskId = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK_ID).toString();
        
        if (taskTable.getSelectedRows().length > 1)
            msg = Local.getString("Remove")+" "+taskTable.getSelectedRows().length +" "+Local.getString("tasks")+"?"
             + "\n"+Local.getString("Are you sure?");
        else {        	
        	Task t = CurrentProject.getTaskList().getTask(thisTaskId);
        	Process p = t.getProcess();
        	// check if part of process
        	if (p != null) {
        		msg = Local.getString("Remove task")+"\n'" + t.getText() + Local.getString("' from process or project?");
        		hasProcess = true;
        	}
        	// check if there are subtasks
        	else if(CurrentProject.getTaskList().hasSubTasks(thisTaskId)) {
				msg = Local.getString("Remove task")+"\n'" + t.getText() + Local.getString("' and all subtasks") +"\n"+Local.getString("Are you sure?");
			}
			else {		            
				msg = Local.getString("Remove task")+"\n'" + t.getText() + "'\n"+Local.getString("Are you sure?");
			}
        }
        if (hasProcess) {
        	Task t = (Task) taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK);
    		int n  = JOptionPane.showOptionDialog(
    				App.getFrame(),
    				msg,
    				Local.getString("Remove Task"),
    				JOptionPane.DEFAULT_OPTION,
    				JOptionPane.QUESTION_MESSAGE,
    				null,
    				new String[] {"Project", "Process", "Cancel"},
    				0);
    		
    		if (n == 0) {
    			CurrentProject.getTaskList().removeTask(t);
    		}
    		else if (n == 1) {
    			t.getProcess().removeTask(t.getID());
    		}
        }
        else {
			int n = JOptionPane.showConfirmDialog(App.getFrame(), msg, Local.getString("Remove task"),
					JOptionPane.YES_NO_OPTION);
			if (n != JOptionPane.YES_OPTION)
				return;
			Vector<Task> toremove = new Vector<Task>();
			for (int i = 0; i < taskTable.getSelectedRows().length; i++) {
				Object o = taskTable.getModel().getValueAt(taskTable.getSelectedRows()[i], TaskTable.TASK);
				if (o instanceof Task) {
					toremove.add((Task) o);
				}
				/*Task t =
				CurrentProject.getTaskList().getTask(
				taskTable.getModel().getValueAt(taskTable.getSelectedRows()[i], TaskTable.TASK_ID).toString());
				if (t != null)
				toremove.add(t);*/
			}
			for (int i = 0; i < toremove.size(); i++) {
				CurrentProject.getTaskList().removeTask(toremove.get(i));
			}
		}
		
		taskTable.tableChanged();
		CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
		CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
		parentPanel.updateIndicators();
    }

	void ppCompleteTask_actionPerformed(ActionEvent e) {
		String msg;
		Vector<Task> tocomplete = new Vector<Task>();
		for (int i = 0; i < taskTable.getSelectedRows().length; i++) {
			Object o = taskTable.getModel().getValueAt(taskTable.getSelectedRows()[i], TaskTable.TASK);
			if (o instanceof Task) {
				tocomplete.add((Task) o);
			}
			/*(Task t =
			CurrentProject.getTaskList().getTask(
				taskTable.getModel().getValueAt(taskTable.getSelectedRows()[i], TaskTable.TASK_ID).toString());
			if (t != null)
				tocomplete.add(t);*/
		}
		for (int i = 0; i < tocomplete.size(); i++) {
			Task t = tocomplete.get(i);
			t.setProgress(100);
		}
		taskTable.tableChanged();
		CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
		parentPanel.updateIndicators();
		//taskTable.updateUI();
	}
	
	/**
	 * Controls the dialog for editing templates.
	 */
	void openEditTemplate() {
		TemplateSelectDialog dialog =
				new TemplateSelectDialog(App.getFrame(), "Select template");
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		
		if (!dialog.isCancelled() && dialog.getTemplate() != null) {
			// TODO: Open template to edit
		}
	}
	  
	void newProcessB_actionPerformed(ActionEvent e) {
		  ProcessDialog dialog = new ProcessDialog(App.getFrame(), "New Process", CurrentDate.get(), CurrentDate.get());
		  dialog.setLocationRelativeTo(this);
		  dialog.setVisible(true);
		  
		  if (!dialog.CANCELLED) {
			  String name = dialog.getName();
			  Date startDate = dialog.getStartDate();
			  Date endDate = dialog.getEndDate();
			  
			  CurrentProject.getProcessList().createProcess(name, new CalendarDate(startDate), new CalendarDate(endDate));
			  
			  taskTable.tableChanged();
			  CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
			  parentPanel.updateIndicators();
		  }
	}
	
	void editProcessB_actionPerformed(ActionEvent e) {
		Process selectedProcess = (Process) taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK);
		String processName = selectedProcess.getName();
		String processId = selectedProcess.getID();
		ProcessDialog dialog = new ProcessDialog(App.getFrame(), "Edit Process", selectedProcess.getStartDate(), selectedProcess.getEndDate());
		dialog.setLocationRelativeTo(this);
		dialog.nameTextField.setText(selectedProcess.getName());
		dialog.setVisible(true);
		
		if (!dialog.CANCELLED) {
			String name = dialog.getName();
			Date startDate = dialog.getStartDate();
			Date endDate = dialog.getEndDate();
			
			selectedProcess.setName(name);
			selectedProcess.setStartDate(new CalendarDate(startDate));
			selectedProcess.setEndDate(new CalendarDate(endDate));
			
			taskTable.tableChanged();
			CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
			parentPanel.updateIndicators();
	        
	        sortProcessTasks(processName, processId);
		}
	}
	
	// US-3 Task 49: Create add task wizard
	// Show sorting dialog following task addition to process
	void sortProcessTasks(String processName, String processId) {
		ProcessTaskSortDialog ptsd = new ProcessTaskSortDialog(App.getFrame(), Local.getString("Sort Tasks for \"" + processName + "\":"), processId);
		
        Dimension frmSize = App.getFrame().getSize();
        Point loc = App.getFrame().getLocation();
        ptsd.setLocation((frmSize.width - ptsd.getSize().width) / 2 + loc.x, (frmSize.height - ptsd.getSize().height) / 2 + loc.y);
        ptsd.setVisible(true);
        if (ptsd.CANCELLED)
            return;
        
		CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
        taskTable.tableChanged();
        parentPanel.updateIndicators();
	}
	
	// US-3 Task 49: Create add task wizard
	void addProcessTaskB_actionPerformed(ActionEvent e) {
		// Get process name
		Process selectedProcess = (Process) taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK);
		String processName = selectedProcess.getName();
		String processId = selectedProcess.getID();
		
		Util.debug("ID: " + processId);
		
		// Button text
		String[] options = new String[2];
		options[0] = new String(Local.getString("Create New Task"));
		options[1] = new String(Local.getString("Use Existing Task"));
		
		// Display option pane
		int selection = JOptionPane.showOptionDialog(App.getFrame(), Local.getString("Select how you would like to add your task to \"" + processName + "\":"), Local.getString("Add Process Task"), 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
		
		// Selection handle
		if (selection == 0) { // Create new task
			TaskDialog taskDialog = new TaskDialog(App.getFrame(), Local.getString("New Task for \"" + processName + "\""), processId);
			
	        Dimension frmSize = App.getFrame().getSize();
	        Point loc = App.getFrame().getLocation();
	        taskDialog.jSpinnerStartDate.getModel().setValue(CurrentDate.get().getDate());
	        taskDialog.jSpinnerEndDate.getModel().setValue(CurrentDate.get().getDate());
	        taskDialog.setLocation((frmSize.width - taskDialog.getSize().width) / 2 + loc.x, (frmSize.height - taskDialog.getSize().height) / 2 + loc.y);
	        taskDialog.setVisible(true);
	        if (taskDialog.CANCELLED)
	            return;
	        
	        CalendarDate sd = new CalendarDate(selectedProcess.getStartDate().getDate());
	        CalendarDate ed = new CalendarDate(selectedProcess.getEndDate().getDate());
	        long effort = Util.getMillisFromHours(taskDialog.effortField.getText());
			Task newTask = CurrentProject.getTaskList().createTask(sd, ed, taskDialog.jTextFieldName.getText(), taskDialog.jTextFieldType.getText(), taskDialog.jComboBoxPriority.getSelectedIndex(),effort, taskDialog.descriptionField.getText(),null);
			newTask.setProgress(((Integer)taskDialog.jSpinnerProgress.getValue()).intValue());
			CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());

			selectedProcess.addTask(newTask.getID());
			CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
			CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
	        taskTable.tableChanged();
	        parentPanel.updateIndicators();
	        
	        sortProcessTasks(processName, processId);
	        
			
		} else if (selection == 1) {
			// Use existing
			
			TaskSelectionDialog tsd = new TaskSelectionDialog(App.getFrame(), "Tasks");
			tsd.setLocationRelativeTo(this);
			tsd.setVisible(true);
			
			if (!tsd.CANCELLED) {
				String[] tasks = tsd.getSelectedTasks();
				
				for (int i = 0; i < tasks.length; i++) {
					selectedProcess.addTask(tasks[i]);
				}
				
				taskTable.tableChanged();
				CurrentStorage.get().storeProcessList(CurrentProject.getProcessList(), CurrentProject.get());
				CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());
				parentPanel.updateIndicators();
		        
		        sortProcessTasks(processName, processId);
			}
		}
	}

	// toggle "show active only"
	void toggleShowActiveOnly_actionPerformed(ActionEvent e) {
		Context.put(
			"SHOW_ACTIVE_TASKS_ONLY",
			new Boolean(ppShowActiveOnlyChB.isSelected()));
		taskTable.tableChanged();
	}

    class PopupListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
		if ((e.getClickCount() == 2) && (taskTable.getSelectedRow() > -1)){
			// ignore "tree" column
			//if(taskTable.getSelectedColumn() == 1) return;
			Object o = taskTable.getModel().getValueAt(taskTable.getSelectedRow(), TaskTable.TASK);
			
			if (o instanceof Task) {
				editTaskB_actionPerformed(null);
			}
			else if (o instanceof Process) {
				editProcessB_actionPerformed(null);
			}
		}
        }

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        taskPPMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }

    }

  void ppEditTask_actionPerformed(ActionEvent e) {
    editTaskB_actionPerformed(e);
  }
  void ppRemoveTask_actionPerformed(ActionEvent e) {
    removeTaskB_actionPerformed(e);
  }
  void ppNewTask_actionPerformed(ActionEvent e) {
    newTaskB_actionPerformed(e);
  }

  void ppAddSubTask_actionPerformed(ActionEvent e) {
  	addSubTask_actionPerformed(e);
  }

  void ppListSubTasks_actionPerformed(ActionEvent e) {
  	listSubTasks_actionPerformed(e);
  }

  void ppParentTask_actionPerformed(ActionEvent e) {
  	parentTask_actionPerformed(e);
  }

  void ppCalcTask_actionPerformed(ActionEvent e) {
      calcTask_actionPerformed(e);
  }
}