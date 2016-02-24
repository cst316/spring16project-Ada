package net.sf.memoranda.ui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import net.sf.memoranda.*;
import net.sf.memoranda.Process;
import net.sf.memoranda.util.*;
import net.sf.memoranda.date.*;

public class TaskTableSorter extends TaskTableModel{
	
	// -1 == no sorting
	int sorting_column = -1;
	
	// sort opposite direction
	boolean opposite = false;
	
	Comparator comparator = new Comparator(){
		public int compare(Object o1, Object o2){
			int compare = 0;
			if (sorting_column == -1) {
			}
			else if (o1 instanceof Task && o2 instanceof Task) {
				Task task1 = (Task) o1;
				Task task2 = (Task) o2;
				// based on TaskTableModel.columnNames
				switch (sorting_column) {
				case 1:
					compare = task1.getText().compareTo(task2.getText());
					break;
				case 2:
					compare = task1.getType().compareTo(task2.getType());
					break;
				case 3:
					compare = task1.getStartDate().getDate().compareTo(task2.getStartDate().getDate());
					break;
				case 4:
					compare = task1.getEndDate().getDate().compareTo(task2.getEndDate().getDate());
					break;
				case 0: // task priority, same as 4
				case 5:
					compare = task1.getPriority() - task2.getPriority();
					break;
				case 6:
					compare = task1.getStatus(CurrentDate.get()) - task2.getStatus(CurrentDate.get());
					break;
				case 7:
					compare = task1.getProgress() - task2.getProgress();
					break;
				}
			}
			else if (o1 instanceof Process && o2 instanceof Process) {
				Process p1 = (Process) o1;
				Process p2 = (Process) o2;
				compare = p1.getName().compareTo(p2.getName());
			}
			else {
				compare = (o1 instanceof Task ? 1 : -1);
			}
			return compare;
		}
	};
	
	public TaskTableSorter( TaskTable table ){
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.addMouseListener( new MouseHandler() );
		tableHeader.setDefaultRenderer( new SortableHeaderRenderer());
	}
	
	public Object getChild(Object parent, int index) {
		Collection c = null;
		
		if (parent instanceof Project){
			if( activeOnly() ) {
				if (opposite) {
					c = CurrentProject.getProcessList().getActiveProcesses();
					c.addAll(CurrentProject.getTaskList().getActiveSubTasks(null, CurrentDate.get()));
				}
				else {
					c = CurrentProject.getTaskList().getActiveSubTasks(null, CurrentDate.get());
					c.addAll(CurrentProject.getProcessList().getActiveProcesses());
				}
			}
			else  {
				if (opposite) {
					c = CurrentProject.getProcessList().getActiveProcesses();
					c.addAll(CurrentProject.getTaskList().getTopLevelTasks());
				}
				else {
					c = CurrentProject.getTaskList().getTopLevelTasks();
					c.addAll(CurrentProject.getProcessList().getActiveProcesses());
				}
			}
		}
		else if (parent instanceof Process) {
			Process p = (Process) parent;
			if (activeOnly()) {
				c = p.getActiveTasks(CurrentDate.get());
			}
			else {
				c = p.getTasks();
			}
		}
		else{
			Task t = (Task) parent;
			if(activeOnly()) c = CurrentProject.getTaskList().getActiveSubTasks(t.getID(), CurrentDate.get());
			else c = t.getSubTasks();
		}
		
		Object array[] = c.toArray();
		if(opposite){
			Arrays.sort(array, comparator);
			return array[ array.length - index - 1];
		}
		return array[index];
	}

	
    
    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1) {
		sorting_column = column;
		
		// 0 == priority icon column
		// 4 == priority text column
		if(column == 0) sorting_column = 4;
		
		if(e.isControlDown()) sorting_column = -1;
		else opposite = !opposite;
		
		TaskTable treetable = ( (TaskTable) h.getTable());
		
		//java.util.Collection expanded = treetable.getExpandedTreeNodes();
		
		treetable.tableChanged();
		//treetable.setExpandedTreeNodes(expanded);
		//h.updateUI();
		h.resizeAndRepaint();
            }
        }
    }
    
	/**
	* Render sorting header differently
	*/
	private class SortableHeaderRenderer implements TableCellRenderer {
	    
	    
	    
		public Component getTableCellRendererComponent(JTable table, 
							       Object value,
							       boolean isSelected, 
							       boolean hasFocus,
							       int row, 
							       int column) {
			JComponent c = new JLabel(value.toString());
			if(column == sorting_column){
				c.setFont(c.getFont().deriveFont(Font.BOLD));
				//c.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
			}
			else c.setFont(c.getFont().deriveFont(Font.PLAIN));
			return c;
		}
	}
	
}
