
package net.sf.memoranda.ui;

import net.sf.memoranda.Process;
import net.sf.memoranda.Project;
import net.sf.memoranda.Task;
import net.sf.memoranda.date.CurrentDate;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import net.sf.memoranda.util.Local;

/**
 * 
 */
public class TaskTreeTableCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer, TableCellRenderer {
    static ImageIcon PR_HIGHEST_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/pr_highest.png"));
    static ImageIcon PR_HIGH_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/pr_high.png"));
    static ImageIcon PR_NORMAL_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/pr_normal.png"));
    static ImageIcon PR_LOW_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/pr_low.png"));
    static ImageIcon PR_LOWEST_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/pr_lowest.png"));
    static ImageIcon TASK_ACTIVE_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/task_active.png"));
    static ImageIcon TASK_SCHEDULED_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/task_scheduled.png"));
    static ImageIcon TASK_DEADLINE_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/task_deadline.png"));
    static ImageIcon TASK_FAILED_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/task_failed.png"));
    static ImageIcon TASK_COMPLETED_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
            .getResource("resources/icons/task_completed.png"));
    static ImageIcon PROCESS_ICON = new ImageIcon(net.sf.memoranda.ui.AppFrame.class
    		.getResource("resources/icons/process.png"));
    // reusable cellrenderers
    JLabel label = new JLabel();
    JButton jButtonAddTime = new JButton();
    //JLabel tree_label = new JLabel();
    ProgressLabel progressLabel;
    JPanel empty_panel = new JPanel();
    // get Task objects via table (maybe not most elegant solution)
    TaskTable table;
    
    //SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy");
    //  use localized date format, modified from default locale's short format if possible
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);//createModifiedShortFormat();

    public TaskTreeTableCellRenderer(TaskTable table) {
        super();
        this.table = table;
        progressLabel = new ProgressLabel(table);
        label.setOpaque(true);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        // if root then just return some component
        // it is not shown anyway
        super.getTreeCellRendererComponent(
                tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        if (value instanceof Project) {
        	return empty_panel;
        }
        else if (value instanceof Task) {
            Task t = (Task) value; 
            setText(t.getText());
            setToolTipText(t.getDescription());
            setIcon(getStatusIcon(t));
            applyFont(t, this);
            //return getTaskTreeCellRenderer(t, selected, hasFocus);
        }
        else if (value instanceof Process) {
        	Process p = (Process) value;
        	setFont(getFont().deriveFont(Font.BOLD));
        	setForeground(Color.BLUE);
        	setIcon(PROCESS_ICON);
        	setText(p.getName());
        }
        else {
        	return empty_panel;
        }

        return this;
    }

    public Component getTableCellRendererComponent(JTable ignore, Object value, boolean selected,
            boolean hasFocus, int row, int column) {
    	
		if (column == 1) {
			// this never happens because
			// column 1 contains TreeTableModel
			// and default renderer for it
			// is JTree directly            
			return table.getTree();
		}
    	
    	Object o = table.getModel().getValueAt(row, TaskTable.TASK);
        if (o instanceof Task) {
			Task t = (Task) o;

			// default values
			// label.setOpaque(true);
			label.setForeground(Color.BLACK);
			label.setIcon(null);
			// label.setToolTipText(t.getDescription()); //XXX Disabled because of bug 1596966
			applyFont(t, label);
			applySelectionStyle(selected, label);
			applyFocus(hasFocus, label);
			// if( column_name.equals("% " + Local.getString("done")) ){
			if (column == 8) {
				return getProgressCellRenderer(t, selected, hasFocus, column);
			}
			// if( column_name.equals("") ){
			if (column == 0) {
				return getPriorityIconCellRenderer(t, selected, hasFocus);
			}  
			// if( column_name.equals(Local.getString("Start date")) ||
			// column_name.equals(Local.getString("End date")) ){
			if ((column == 4) || (column == 5)) {
				label.setText(dateFormat.format((Date) value));
				return label;
			}
			// if( column_name.equals( Local.getString("Status") ) ){
			if (column == 7) {
				label.setText(value.toString());
				label.setForeground(getColorForTaskStatus(t, false));
				return label;
			}
			label.setText(value.toString());
		}
        else if (o instanceof Process) {
        	Process p = (Process) o;
        	
			applySelectionStyle(selected, label);
			applyFocus(hasFocus, label);
			label.setIcon(null);
			
			if (column == 2) { // actual effort column 
				label.setText(value.toString());
                                drawAddTime();
			} else if (column == 4 || column == 5) { // date columns
				label.setText(dateFormat.format((Date) value));
			} else if (column == 8) { // progress column
				return getProgressCellRenderer(p, selected, hasFocus, column);
			} else {
				label.setText("");
			}
        }
		return label;
    }

    /**
     * Component used to render tree cells in treetable
     */
    private Component getTaskTreeCellRenderer(Task t, boolean selected, boolean hasFocus) {
        JLabel tree_label = new JLabel();       
        tree_label.setText(t.getText());
        // XXX [alexeya] Disabled coz a bug with tooltips in TreeTables:
        //tree_label.setToolTipText(t.getDescription());
        tree_label.setIcon(getStatusIcon(t));
        applyFont(t, tree_label);
        return tree_label;        
    }

    /**
     * Component showing task progress
     */
    private Component getProgressCellRenderer(Task t, boolean selected, boolean hasFocus, int column) {
        progressLabel.setVal(t.getProgress());
        progressLabel.setColumn(column);
        progressLabel.setColor(getColorForTaskStatus(t, true));
        applyFocus(hasFocus, progressLabel);
        return progressLabel;
    }

    /**
     * Component showing task progress
     */
    private Component getProgressCellRenderer(Process p, boolean selected, boolean hasFocus, int column) {
        Collection<Task> tasks = p.getTasks();
    	
    	progressLabel.setVal(p.getProgress());
        progressLabel.setColumn(column);
        
        if (tasks.isEmpty()) {
        	progressLabel.setColor(new Color(255, 255, 255));
        }
        else {
        	progressLabel.setColor(getColorForTaskStatus((Task)tasks.toArray()[tasks.size()-1], true));
        }
        
        applyFocus(hasFocus, progressLabel);
        return progressLabel;
    }

    private Component getPriorityIconCellRenderer(Task t, boolean selected, boolean hasFocus) {
        applyFocus(false, label); // disable focus borders
        label.setIcon(getPriorityIcon(t));
        label.setToolTipText(t.getDescription());
        return label;
    }

    // some convenience methods
    private void applySelectionStyle(boolean selected, JComponent c) {
        if (selected)
            c.setBackground(table.getSelectionBackground());
        else
            c.setBackground(table.getBackground());
    }

    private void applyFocus(boolean hasFocus, JComponent c) {
        if (hasFocus) {
            c.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, table.getSelectionBackground()
                    .darker()));
        } else {
            if (c.getBorder() != null) {
                c.setBorder(null);
            }
        }
    }

    private void applyFont(Task t, JComponent c) {
        if ((t.getStatus(CurrentDate.get()) == Task.ACTIVE)
                || (t.getStatus(CurrentDate.get()) == Task.DEADLINE))
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        else
            c.setFont(c.getFont().deriveFont(Font.PLAIN));
    }

    /**
     * Color representing task status, "light" color is useful for backgrounds
     * and other for text
     */
    public static Color getColorForTaskStatus(Task t, boolean light) {
        if (light) {
            switch (t.getStatus(CurrentDate.get())) {
            case Task.ACTIVE:
                return new Color(192, 255, 192);
            case Task.SCHEDULED:
                return new Color(192, 230, 255);
            case Task.DEADLINE:
                return new Color(255, 240, 160);
            case Task.FAILED:
                return new Color(255, 192, 192);
            case Task.COMPLETED:
                return new Color(230, 255, 230);
            }
        } else {
            switch (t.getStatus(CurrentDate.get())) {
            case Task.ACTIVE:
                return new Color(0, 180, 0);
            case Task.SCHEDULED:
                return new Color(0, 120, 255);
            case Task.DEADLINE:
                return new Color(160, 90, 0);
            case Task.FAILED:
                return new Color(255, 0, 0);
            case Task.COMPLETED:
                return new Color(0, 120, 0);
            }
        }
        System.err.println("Problem finding color for task status");
        return null;
    }

    public static ImageIcon getStatusIcon(Task t) {
        switch (t.getStatus(CurrentDate.get())) {
        case Task.ACTIVE:
            return TASK_ACTIVE_ICON;
        case Task.SCHEDULED:
            return TASK_SCHEDULED_ICON;
        case Task.DEADLINE:
            return TASK_DEADLINE_ICON;
        case Task.FAILED:
            return TASK_FAILED_ICON;
        case Task.COMPLETED:
            return TASK_COMPLETED_ICON;
        }
        System.err.println("Problem finding status icon");
        return null;
    }

    public static ImageIcon getPriorityIcon(Task t) {
        switch (t.getPriority()) {
        case Task.PRIORITY_NORMAL:
            return PR_NORMAL_ICON;
        case Task.PRIORITY_HIGHEST:
            return PR_HIGHEST_ICON;
        case Task.PRIORITY_HIGH:
            return PR_HIGH_ICON;
        case Task.PRIORITY_LOW:
            return PR_LOW_ICON;
        case Task.PRIORITY_LOWEST:
            return PR_LOWEST_ICON;
        }
        System.err.println("Problem finding priority icon");
        return null;
    }
    /**
	 * Draws the UI for the Add Time Button
	 */
	private void drawAddTime() {
        jButtonAddTime.setMaximumSize(new Dimension(100, 26));
        jButtonAddTime.setMinimumSize(new Dimension(100, 26));
        jButtonAddTime.setPreferredSize(new Dimension(100, 26));
        jButtonAddTime.setText(Local.getString("Add Time"));
        jButtonAddTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddTime_actionPerformed(e);
            }

            private void AddTime_actionPerformed(ActionEvent e) {
                //TODO open the log time dialong
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
	}
    
    
}
