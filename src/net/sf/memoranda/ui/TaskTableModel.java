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

/**
 * JAVADOC:
 * <h1>TaskTableModel</h1>
 * 
 * @version $Id: TaskTableModel.java,v 1.7 2005/12/01 08:12:26 alexeya Exp $
 * @author $Author: alexeya $
 */
public class TaskTableModel extends AbstractTreeTableModel implements TreeTableModel {

    String[] columnNames = {"", Local.getString("To-do"), Local.getString("Type"),
            Local.getString("Start date"), Local.getString("End date"),
            Local.getString("Priority"), Local.getString("Status"),
            "% " + Local.getString("done") };

    protected EventListenerList listenerList = new EventListenerList();

    private boolean activeOnly = check_activeOnly();
        
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
        Object o = null;
    	if (node instanceof Project) {
    		o = null;
    	}
    	else if (node instanceof Process) {
    		Process p = (Process) node;
    		switch (column) {
			case 0:
				o = "";
				break;
			case 1:
				o = p.getName();
				break;
			case 3:
				// TODO
				o = "start date";
				break;
			case 4:
				// TODO
				o = "end date";
				break;
			case TaskTable.TASK_ID:
				o = p.getID();
				break;
			case TaskTable.TASK:
				o = p;
				break;
			default:
				o = "";
				break;
			}
    	}
    	else if (node instanceof Task) {
			Task t = (Task) node;
			switch (column) {
			case 0:
				o = "";
				break;
			case 1:
				o = t;
				break;
			case 2:
				o = t.getType();
				break;
			case 3:
				o = t.getStartDate().getDate();
				break;
			case 4:
				if (t.getEndDate() == null)
					o = null;
				else
					o = t.getEndDate().getDate();
				break;
			case 5:
				o = getPriorityString(t.getPriority());
				break;
			case 6:
				o = getStatusString(t.getStatus(CurrentDate.get()));
				break;
			case 7:
				//return new Integer(t.getProgress());
				o = t;
				break;
			case TaskTable.TASK_ID:
				o = t.getID();
				break;
			case TaskTable.TASK:
				o = t;
				break;
			}
		}
		return o;
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
				childCount = CurrentProject.getTaskList().getActiveTopLevelNoProcessTasks(CurrentDate.get()).size();
				childCount += CurrentProject.getProcessList().getActiveProcesses().size();
			}
			else {
				childCount = CurrentProject.getTaskList().getTopLevelNoProcessTasks().size();
				childCount += CurrentProject.getProcessList().getAllProcesses().size();
			}
        }
        else if (parent instanceof Process) {
        	Process p = (Process) parent;
        	if (activeOnly()) {
				childCount = p.getActiveTasks(CurrentDate.get()).size();
			}
        	else {
				childCount = p.getTasks().size();
			} 
        }
        else if (parent instanceof Task) {
			Task t = (Task) parent;
			if (activeOnly()) {
				childCount = CurrentProject.getTaskList().getActiveSubTasks(t.getID(), CurrentDate.get()).size();
			}
			else {
				childCount = t.getSubTasks().size();
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
        	if( activeOnly() ) {
        		Collection<Task> tasks = CurrentProject.getTaskList().getActiveSubTasks(null, CurrentDate.get());
        		
        		if (index < tasks.size()) {
        			child = tasks.toArray()[index];
        		}
        		else {
        			child = CurrentProject.getProcessList().getActiveProcesses().toArray()[index - tasks.size()];
        		}
        	}
        	else {
        		Collection<Task> tasks = CurrentProject.getTaskList().getTopLevelNoProcessTasks();
        		
        		if (index < tasks.size()) {
        			child = tasks.toArray()[index];
        		}
        		else {
        			child = CurrentProject.getProcessList().getAllProcesses().toArray()[index - tasks.size()];
        		}
        	}
        }
        else if (parent instanceof Process) {
        	Process p = (Process) parent;
        	if (activeOnly()) {
        		child = p.getActiveTasks(CurrentDate.get()).toArray()[index];
        	}
        	else {
        		child = p.getTasks().toArray()[index];
        	}
        }
        else if (parent instanceof Task) {
			Task t = (Task) parent;
			if (activeOnly()) {
				child = CurrentProject.getTaskList().getActiveSubTasks(t.getID(), CurrentDate.get()).toArray()[index];
			} else {
				child = t.getSubTasks().toArray()[index];
			} 
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
            case 5:
            case 6:
                return Class.forName("java.lang.String");
            case 3:
            case 4:
                return Class.forName("java.util.Date");
            case 7:
                return Class.forName("java.lang.Integer");
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
    }

    public static boolean check_activeOnly(){
		Object o = Context.get("SHOW_ACTIVE_TASKS_ONLY");
		if(o == null) return false;
		return o.toString().equals("true");
	}

    public boolean activeOnly(){
		return activeOnly;
    }
    
    public boolean isCellEditable(Object node, int column) {
		if(column == 6) return true; 
        return super.isCellEditable(node, column); 
    }

}