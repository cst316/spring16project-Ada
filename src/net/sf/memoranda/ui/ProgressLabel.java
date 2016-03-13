package net.sf.memoranda.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

import net.sf.memoranda.Task;

/**
 * Shows progress as a colored bar.
 * A more generic ProgressLabel than TaskProgressLabel to allow a broader use
 * than just for Tasks.
 * 
 * Code primarily taken from TaskProgressLabel on 2/25/2016
 * 
 * @author James
 *
 */
public class ProgressLabel extends JLabel {
    private Color color;
	private TaskTable table;
    private int column;
    private int val;
    
    public ProgressLabel( TaskTable table ){
        this.table = table;
        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    }
    
    public void setVal(int val) {
    	if (val <= 100 && val >= 0) {
    		this.val = val;
    	}
    }
    
    public void setColumn(int col) {
    	column = col;
    }
    
    public void setColor(Color color) {
    	this.color = color;
    }
    
    public void paintComponent(Graphics g) {
        int width = table.getColumnModel().getColumn(column).getWidth();
        int height = table.getRowHeight();
        int p = width * val / 100;
        
        g.setColor(Color.WHITE);
        g.fillRect(0,0,width, height);

        g.setColor( color );
        g.fillRect(1, 1, p, height - 2);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(1, 1, width, height - 2);
        
        setText(val + "%");
        setBounds(0, 0, width, height);
        
        super.paintComponent(g);
    }
}