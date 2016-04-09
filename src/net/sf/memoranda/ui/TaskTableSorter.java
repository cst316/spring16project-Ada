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
	int sortingColumn = -1;
	
	// sort opposite direction
	boolean opposite = false;
	
	Comparator comparator = new Comparator(){
		public int compare(Object o1, Object o2){
			int compare = 0;
			if (sortingColumn == -1) {
				// do nothing
			} else if (o1 instanceof Task && o2 instanceof Task) {
				Task task1 = (Task) o1;
				Task task2 = (Task) o2;
				// based on TaskTableModel.columnNames
				switch (sortingColumn) {
				case 1:
					compare = task1.getText().compareTo(task2.getText());
					break;
				case 2:
				case 3:
					compare = (int) (task1.getLoggedTime()
							- task2.getLoggedTime());
					break;
				case 4:
					compare = task1.getType().compareTo(task2.getType());
					break;
				case 5:
					compare = task1.getStartDate().getDate().compareTo(
							task2.getStartDate().getDate());
					break;
				case 6:
					compare = task1.getEndDate().getDate().compareTo(
							task2.getEndDate().getDate());
					break;
				case 0: // task priority, same as 4
				case 7:
					compare = task1.getPriority() - task2.getPriority();
					break;
				case 8:
					compare = task1.getStatus(CurrentDate.get())
							- task2.getStatus(CurrentDate.get());
					break;
				case 9:
					compare = task1.getProgress() - task2.getProgress();
					break;
				default:
					compare = task1.getText().compareTo(task2.getText());	
				}
			} else if (o1 instanceof Process && o2 instanceof Process) {
				Process p1 = (Process) o1;
				Process p2 = (Process) o2;
				
				switch (sortingColumn) {
				case 1:
					compare = p1.getName().compareTo(p2.getName());
					break;
				case 2:
				case 3:
					compare = (int) (p1.getLoggedTime() - p2.getLoggedTime());
					break;
				case 5:
					compare = p1.getStartDate().getDate().compareTo(
							p2.getStartDate().getDate());
					break;
				case 6:
					compare = p1.getEndDate().getDate().compareTo(
							p2.getEndDate().getDate());
					break;
				case 9:
					compare = p1.getProgress() - p2.getProgress();
					break;
				default:
					compare = p1.getName().compareTo(p2.getName());
					break;
				}
			} else {
				compare = (o1 instanceof Task ? -1 : 1);
				if (TaskTableSorter.this.opposite) {
					compare *= -1;
				}
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
		Collection collection = null;
		
		if (parent instanceof Project){
			if( activeOnly() ) {
				collection =
						CurrentProject.
						getTaskList().
						getActiveTopLevelNoProcessTasks(CurrentDate.get());
				collection.addAll(
						CurrentProject.getProcessList().getActiveProcesses(
								CurrentDate.get()));
			} else if ( byDateOnly() ) {
				collection =
						CurrentProject.getTaskList().getTasksByDate(
								CurrentDate.get(), false);
				collection.addAll(
						CurrentProject.getProcessList().getProcessesByDate(
								CurrentDate.get()));
			} else  {
				collection =
						CurrentProject.
						getTaskList().
						getTopLevelNoProcessTasks();
				collection.addAll(
						CurrentProject.getProcessList().getAllProcesses());
			}
		} else if (parent instanceof Process) {
			Process process = (Process) parent;
			if (activeOnly()) {
				collection = process.getActiveTasks(CurrentDate.get());
			} else {
				collection = process.getTasks();
			}
		} else {
			Task task = (Task) parent;
			if (activeOnly()) {
				collection = CurrentProject.getTaskList().getActiveSubTasks(
								task.getID(),
								CurrentDate.get());
			} else  {
				collection = task.getSubTasks();
			}
		}
		
		Object array[] = collection.toArray();
		if (!(parent instanceof Process)) {
			Arrays.sort(array, comparator);
			if(opposite){
				return array[ array.length - index - 1];
			}
		}		
		return array[index];
	}

	
    
    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent event) {
            JTableHeader header = (JTableHeader) event.getSource();
            TableColumnModel columnModel = header.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(event.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1) {
		sortingColumn = column;
		
		// 0 == priority icon column
		// 4 == priority text column
		if (column == 0) {
			sortingColumn = 4;
		}
		
		if (event.isControlDown()) {
			sortingColumn = -1;
		} else {
			opposite = !opposite;
		}
		
		TaskTable treetable = ( (TaskTable) header.getTable());
		
		//java.util.Collection expanded = treetable.getExpandedTreeNodes();
		
		treetable.tableChanged();
		//treetable.setExpandedTreeNodes(expanded);
		//h.updateUI();
		header.resizeAndRepaint();
            }
        }
    }
    
	/**
	* Render sorting header differently.
	*/
	private class SortableHeaderRenderer implements TableCellRenderer {
	    
	    
	    
		public Component getTableCellRendererComponent(JTable table, 
							       Object value,
							       boolean isSelected, 
							       boolean hasFocus,
							       int row, 
							       int column) {
			JComponent component = new JLabel(value.toString());
			if(column == sortingColumn){
				component.setFont(component.getFont().deriveFont(Font.BOLD));
				//c.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
			} else {
				component.setFont(component.getFont().deriveFont(Font.PLAIN));
			}
			return component;
		}
	}
	
}
