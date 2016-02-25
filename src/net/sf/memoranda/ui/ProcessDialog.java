package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.ui.htmleditor.util.Local;

/**
 * Dialog allowing the user to set a name for a process.
 * @author james
 *
 */
public class ProcessDialog extends JDialog {

	public boolean CANCELLED = true;
	
	CalendarDate minDate;
	CalendarDate maxDate;
	CalendarDate startDate;
	CalendarDate endDate;
	
	Border defaultBorder;
	
	JPanel namePanel;
	JTextField nameTextField;
	
	StartEndDatePanel datesPanel;
	
	JPanel buttonPanel;
	JButton okButton;
	JButton cancelButton;
	
	public ProcessDialog(Frame frame, String title, CalendarDate startDate, CalendarDate endDate) {
		super(frame, title, true);
		try {
			jbInit(startDate, endDate);
			pack();
		}
        catch (Exception ex) {
            new ExceptionDialog(ex);
            ex.printStackTrace();
        }
	}
	
	/**
	 * Returns the name entered into the text field.
	 */
	public String getName() {
		return nameTextField.getText().trim();
	}
	
	public Date getStartDate() {
		return datesPanel.getStartDate();
	}
	
	public Date getEndDate() {
		return datesPanel.getEndDate();
	}
	
	private void jbInit(CalendarDate startDate, CalendarDate endDate) {
		this.setResizable(false);
		defaultBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		
		drawNamePanel();
		
		datesPanel = new StartEndDatePanel(this);
		datesPanel.setStartDate(startDate);
		datesPanel.setEndDate(endDate);
		this.getContentPane().add(datesPanel, BorderLayout.CENTER);
		
		drawButtonPanel();
	}

	private void drawButtonPanel() {
		okButton = new JButton();
		okButton.setMaximumSize(new Dimension(100, 26));
		okButton.setMinimumSize(new Dimension(100, 26));
		okButton.setPreferredSize(new Dimension(100, 26));
		okButton.setText(Local.getString("Ok"));
		okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerormed();
            }
        });
        
        cancelButton = new JButton();
        cancelButton.setMaximumSize(new Dimension(100, 26));
        cancelButton.setMinimumSize(new Dimension(100, 26));
        cancelButton.setPreferredSize(new Dimension(100, 26));
        cancelButton.setText(Local.getString("Cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProcessDialog.this.dispose();
            }
        });
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(defaultBorder);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	private void drawNamePanel() {
		nameTextField = new JTextField();
		nameTextField.setPreferredSize(new Dimension(200, 26));
		
		namePanel = new JPanel();
		namePanel.setBorder(defaultBorder);
		namePanel.add(nameTextField);
		
		this.getContentPane().add(namePanel, BorderLayout.NORTH);
	}
	
	private void okButton_actionPerormed() {
		CANCELLED = false;
		this.dispose();
	}
}
