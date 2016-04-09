/**
 * TaskTableModel.java         
 * -----------------------------------------------------------------------------
 * Project           Memoranda
 * Package           net.sf.memoranda.ui
 * Original author   Alex V. Alishevskikh
 *                   [alexeya@gmail.com]
 * Created           18.05.2005 15:16:11
 * Revision info     $RCSfile: TaskTableModel.java,v $ $Revision: 1.7 $ $State: Exp $  
 *
 * Last modified on  $Date: 2005/12/01 08:12:26 $
 *               by  $Author: alexeya $
 * 
 * @VERSION@ 
 *
 * @COPYRIGHT@
 * 
 * @LICENSE@ 
 */

package net.sf.memoranda.ui;

import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Process;
import net.sf.memoranda.Project;
import net.sf.memoranda.Task;
import net.sf.memoranda.date.CurrentDate;
import net.sf.memoranda.ui.treetable.AbstractTreeTableModel;
import net.sf.memoranda.ui.treetable.TreeTableModel;
import net.sf.memoranda.util.Context;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

/**
 * JAVADOC:
 * <h1>TaskTableModel</h1>
 * 
 * @version $Id: TaskTableModel.java,v 1.7 2005/12/01 08:12:26 alexeya Exp $
 * @author $Author: alexeya $
 */
public class TaskTableModel extends AbstractTreeTableModel implements TreeTableModel {

    String[] columnNames = {
    		"",
    		Local.getString("To-do"),
    		Local.getString(""),
    		Local.getString("Actual Effort"),
    		Local.getString("Type"),
            Local.getString("Start date"),
            Local.getString("End date"),
            Local.getString("Priority"),
            Local.getString("Status"),
            "% " + Local.getString("done") };

    protected EventListenerList listenerList = new EventListenerList();

    private boolean activeOnly = check_activeOnly();
    private boolean byDateOnly = check_byDateOnly();
        
    /**
     * JAVADOC: Constructor of <code>TaskTableModel</code>
     * 
     * @param root
     */
    public TaskTableModel(){
        super(CurrentProject.get());
    }

    /**
     * @see net.sf.memoranda.ui.treetable.TreeTableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * @see net.sf.memoranda.ui.treetable.TreeTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * @see net.sf.memoranda.ui.treetable.TreeTableModel#getValueAt(java.lang.Object,
     *      int)
     */
    public Object getValueAt(Object node, int column) {
        Object object = null;
    	if (node instanceof Project) {
    		object = null;
    	} else if (node instanceof Process) {
    		Process process = (Process) node;
    		switch (column) {
			case 0:
				object = "";
				break;
			case 1:
				object = process.getName();
				break;
			case 3:
				object = (float)process.getLoggedTime() / 1000f / 60f / 60f;
				break;
			case 5:
				object = process.getStartDate().getDate();
				break;
			case 6:
				object = process.getEndDate().getDate();
				break;
			case 9:
				object = process;
				break;
			case TaskTable.TASK_ID:
				object = process.getID();
				break;
			case TaskTable.TASK:
				object = process;
				break;
			default:
				object = "";
				break;
			}
    	} else if (node instanceof Task) {
			Task task = (Task) node;
			switch (column) {
			case 0:
				object = "";
				break;
			case 1:
				object = task;
				break;
			case 2:
				object = "";
				break;
			case 3:
				object = (float)task.getLoggedTime() / 1000f / 60f / 60f;
				break;
			case 4:
				object = task.getType();
				break;
			case 5:
				object = task.getStartDate().getDate();
				break;
			case 6:
				if (task.getEndDate() == null) {
					object = null;
				} else {
					object = task.getEndDate().getDate();
				}
				break;
			case 7:
				object = getPriorityString(task.getPriority());
				break;
			case 8:
				object = getStatusString(task.getStatus(CurrentDate.get()));
				break;
			case 9:
				//return new Integer(t.getProgress());
				object = task;
				break;
			case TaskTable.TASK_ID:
				object = task.getID();
				break;
			case TaskTable.TASK:
				object = task;
				break;
			default:
				object = "";
				break;
			}
		}
		return object;
    }

    String getStatusString(int status) {
        switch (status) {
        case Task.ACTIVE:
            return Local.getString("Active");
        case Task.DEADLINE:
            return Local.getString("Deadline");
        case Task.COMPLETED:
            return Local.getString("Completed");
        case Task.FAILED:
            return Local.getString("Failed");
        case Task.FROZEN:
            return Local.getString("Frozen");
        case Task.LOCKED:
            return Local.getString("Locked");
        case Task.SCHEDULED:
            return Local.getString("Scheduled");
        }
        return "";
    }

    String getPriorityString(int p) {
        switch (p) {
        case Task.PRIORITY_NORMAL:
            return Local.getString("Normal");
        case Task.PRIORITY_LOW:
            return Local.getString("Low");
        case Task.PRIORITY_LOWEST:
            return Local.getString("Lowest");
        case Task.PRIORITY_HIGH:
            return Local.getString("High");
        case Task.PRIORITY_HIGHEST:
            return Local.getString("Highest");
        }
        return "";
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        int childCount = 0;
    	
    	if (parent instanceof Project) {
			if( activeOnly() ){
				childCount = CurrentProject.
						getTaskList().
						getActiveTopLevelNoProcessTasks(CurrentDate.get()).
						size();
				childCount += CurrentProject.
						getProcessList().
						getActiveProcesses(CurrentDate.get()).
						size();
			} else if ( byDateOnly ) {
				childCount = CurrentProject.
						getTaskList().
						getTasksByDate(CurrentDate.get(), false).
						size();
				childCount += CurrentProject.
						getProcessList().
						getProcessesByDate(CurrentDate.get()).
						size();
			} else {
				childCount = CurrentProject.
						getTaskList().
						getTopLevelNoProcessTasks().
						size();
				childCount += CurrentProject.
						getProcessList().
						getAllProcesses().
						size();
			}
        } else if (parent instanceof Process) {
        	Process process = (Process) parent;
        	if (activeOnly()) {
				childCount = process.getActiveTasks(CurrentDate.get()).size();
			} else {
				childCount = process.getTasks().size();
			} 
        } else if (parent instanceof Task) {
			Task task = (Task) parent;
			if (activeOnly()) {
				childCount = CurrentProject.
						getTaskList().
						getActiveSubTasks(task.getID(), CurrentDate.get()).
						size();
			} else {
				childCount = task.getSubTasks().size();
			} 
		}
    	
    	return childCount;
    }

    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {
    	Object child = null;
        if (parent instanceof Project) {
        	child = getChildOfProject(index);
        } else if (parent instanceof Process) {
        	child = getChildOfProcess((Process) parent, index);
        } else if (parent instanceof Task) {
			child = getChildOfTask((Task) parent, index);
		}
        return child;
    }

    /**
     * @see net.sf.memoranda.ui.treetable.TreeTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int column) {
        try {
            switch (column) {
            case 1:
                return TreeTableModel.class;
            case 0:
                return TaskTable.class;
            case 2:
            case 3:
            case 4:
            case 7:
            case 8:
                return Class.forName("java.lang.String");
            case 5:
            case 6:
                return Class.forName("java.util.Date");
            case 9:
                return Class.forName("java.lang.Integer");
            default:
                return Object.class;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void fireTreeStructureChanged(){	    
	    fireTreeStructureChanged( this,
	    			new Object[]{getRoot()},
				new int[0],
				new Object[0]
				);
    }
    
    
    /**
     * Update cached data
     */
    public void fireUpdateCache(){
		activeOnly = check_activeOnly();
		byDateOnly = check_byDateOnly();
    }

    public static boolean check_activeOnly(){
		Object o = Context.get("SHOW_ACTIVE_TASKS_ONLY");
		if(o == null) return false;
		return o.toString().equals("true");
	}

    public boolean activeOnly(){
		return activeOnly;
    }
    
    public static boolean check_byDateOnly() {
    	boolean byDateOnly = false;
    	Object object = Context.get("SHOW_BY_DATE_ONLY");
    	if (object != null) {
    		byDateOnly = object.toString().equals("true");
    	}
    	Util.debug("byDateOnly: " + byDateOnly);
    	return byDateOnly;
    }
    
    public boolean byDateOnly() {
    	return byDateOnly;
    }
    
    public boolean isCellEditable(Object node, int column) {
		if(column == 9) {
			return true; 
		}
        return super.isCellEditable(node, column); 
    }

    private Object getChildOfProject(int index) {
    	Object child;
    	if( activeOnly() ) {
    		child = getChildOfProjectActiveOnly(index);
    	} else if (byDateOnly) {
    		child = getChildOfProjectDateOnly(index);
    	} else {
    		child = getChildOfProjectDefault(index);
    	}
    	
    	return child;
    }

	private Object getChildOfProjectActiveOnly(int index) {
		Object child;
		Collection<Task> tasks = CurrentProject.
				getTaskList().
				getActiveSubTasks(null, CurrentDate.get());
		
		if (index < tasks.size()) {
			child = tasks.toArray()[index];
		} else {
			child = CurrentProject.
					getProcessList().
					getActiveProcesses(CurrentDate.get()).
					toArray()[index - tasks.size()];
		}
		return child;
	}
	
	private Object getChildOfProjectDateOnly(int index) {
		Object child;
		Collection<Task> tasks = CurrentProject.
				getTaskList().
				getTasksByDate(CurrentDate.get(), false);
		
		if (index < tasks.size()) {
			child = tasks.toArray()[index];
		} else {
			child = CurrentProject.
					getProcessList().
					getProcessesByDate(CurrentDate.get()).
					toArray()[index - tasks.size()];
		}
		return child;
	}

	private Object getChildOfProjectDefault(int index) {
		Object child;
		Collection<Task> tasks = CurrentProject.getTaskList().getTopLevelNoProcessTasks();
		
		if (index < tasks.size()) {
			child = tasks.toArray()[index];
		} else {
			child = CurrentProject.
					getProcessList().
					getAllProcesses().
					toArray()[index - tasks.size()];
		}
		return child;
	}
    
    private Object getChildOfProcess(Process process, int index) {
    	Object child;
    	if (activeOnly()) {
    		child = process.getActiveTasks(CurrentDate.get()).toArray()[index];
    	} else {
    		child = process.getTasks().toArray()[index];
    	}
    	return child;
    }
    
    private Object getChildOfTask(Task task, int index) {
    	Object child;
		if (activeOnly()) {
			child = CurrentProject.
					getTaskList().
					getActiveSubTasks(task.getID(), CurrentDate.get()).
					toArray()[index];
		} else {
			child = task.getSubTasks().toArray()[index];
		} 
		return child;
    }
}