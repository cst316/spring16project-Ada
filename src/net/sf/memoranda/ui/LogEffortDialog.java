package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
//import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JCheckBox;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

public class LogEffortDialog extends JDialog {
	
	// Booleans
    public boolean CANCELLED = true;
	
    // Strings
    String processId = null;
    
	// Format Date
	SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT);
	
	// Forbid to set dates outside the bounds
	CalendarDate logDateMin = CurrentProject.get().getStartDate();
	CalendarDate logDateMax = CurrentProject.get().getEndDate();
	
	// Panels
    JPanel mPanel = new JPanel(new BorderLayout());
    JPanel areaPanel = new JPanel(new BorderLayout());
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel dialogTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jPanel2 = new JPanel(new GridLayout(2, 2));
    JPanel jPanel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
    // Borders
    Border mBorder;
    Border areaBorder;
    Border border4;
    Border border8;
    
	// Header
    JLabel header = new JLabel();

    // Start Date
    JLabel jLabelLogDate = new JLabel();
    JSpinner jSpinnerLogDate;
    JButton jButtonLogDate = new JButton();
    CalendarFrame startCalFrame = new CalendarFrame();
    
    // Time
    JPanel jPanelTime = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel jLabelTime = new JLabel();
    JTextField timeField = new JTextField();
        
    // Progress
	JPanel jPanelProgress = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	JLabel jLabelProgress = new JLabel();
	JSpinner jSpinnerProgress = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
    
	// Notification
    JButton jButtonNotification = new JButton();
	
    // Buttons
    JButton jButtonCancel = new JButton();
    JButton jButtonOk = new JButton();
	
	/**
	 * Draws the UI for the Header
	 */
	private void drawHeader() {
        header.setFont(new java.awt.Font("Dialog", 0, 20));
        header.setForeground(new Color(0, 0, 124));
        header.setText(Local.getString("Log Time"));
        header.setIcon(new ImageIcon(net.sf.memoranda.ui.TaskDialog.class.getResource(
            "resources/icons/task48.png")));
	}
	
	/**
	 * Draws the UI for the Cancel button
	 */
	private void drawCancel() {
        jButtonCancel.setMaximumSize(new Dimension(100, 26));
        jButtonCancel.setMinimumSize(new Dimension(100, 26));
        jButtonCancel.setPreferredSize(new Dimension(100, 26));
        jButtonCancel.setText(Local.getString("Cancel"));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelB_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the UI for the OK Button
	 */
	private void drawOk() {
        jButtonOk.setMaximumSize(new Dimension(100, 26));
        jButtonOk.setMinimumSize(new Dimension(100, 26));
        jButtonOk.setPreferredSize(new Dimension(100, 26));
        jButtonOk.setText(Local.getString("Ok"));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okB_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the Time UI
	 */
	private void drawTime() {
		// Label
        jLabelTime.setMaximumSize(new Dimension(100, 16));
        jLabelTime.setMinimumSize(new Dimension(60, 16));
        jLabelTime.setText(Local.getString("Time(hrs)"));
        
        // Field
        timeField.setBorder(border8);
        timeField.setPreferredSize(new Dimension(30, 24));
        
        // Panel to tie it together
        jPanelTime.add(jLabelTime, null);
        jPanelTime.add(timeField, null);
	}
	
	/**
	 * Draws the Start Date UI
	 */
	private void drawLogDate() {
        jSpinnerLogDate = new JSpinner(new SpinnerDateModel(new Date(),null,null,Calendar.DAY_OF_WEEK));
        
		jSpinnerLogDate.setBorder(border8);
        jSpinnerLogDate.setPreferredSize(new Dimension(80, 24));
		jSpinnerLogDate.setEditor(new JSpinner.DateEditor(jSpinnerLogDate, sdf.toPattern()));

        jSpinnerLogDate.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	// it's an ugly hack so that the spinner can increase day by day
            	SpinnerDateModel sdm = new SpinnerDateModel((Date)jSpinnerLogDate.getModel().getValue(),null,null,Calendar.DAY_OF_WEEK);
            	jSpinnerLogDate.setModel(sdm);

                Date sd = (Date) jSpinnerLogDate.getModel().getValue();
                startCalFrame.cal.set(new CalendarDate(sd));
            }
        });

        jLabelLogDate.setText(Local.getString("Log Date"));
        jLabelLogDate.setMinimumSize(new Dimension(60, 16));
        jLabelLogDate.setMaximumSize(new Dimension(100, 16));
        
        jButtonLogDate.setMinimumSize(new Dimension(24, 24));
        jButtonLogDate.setPreferredSize(new Dimension(24, 24));
        jButtonLogDate.setText("");
        jButtonLogDate.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/calendar.png")));
        jButtonLogDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLogDateB_actionPerformed(e);
            }
        });
        
        startCalFrame.cal.addSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jSpinnerLogDate.getModel().setValue(startCalFrame.cal.get().getCalendar().getTime());
            }
        });
	}
	
	/**
	 * Draws the Notification button
	 */
	private void drawNotification() {
        jButtonNotification.setText(Local.getString("View Logged Time"));
        jButtonNotification.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/notify.png")));
        jButtonNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setNotifB_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the Progress UI
	 */
	private void drawProgress() {
		// Label
        jLabelProgress.setText(Local.getString("Progress"));

        // Panel to tie it together
        jPanelProgress.add(jLabelProgress, null);
        jPanelProgress.add(jSpinnerProgress, null);
	}
	
	public LogEffortDialog(Frame frame, String title, String processId) {
        super(frame, title, true);
    	this.processId = processId;
        try {
            jbInit();            
            pack();
        }
        catch (Exception ex) {
            new ExceptionDialog(ex);
        }
    }
    
    void jbInit() throws Exception {
	this.setResizable(false);
	this.setSize(new Dimension(430,300));
        mBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        areaBorder = BorderFactory.createEtchedBorder(Color.white, 
            new Color(142, 142, 142));
        border4 = BorderFactory.createEmptyBorder(0, 5, 0, 5);
        border8 = BorderFactory.createEtchedBorder(Color.white, 
            new Color(178, 178, 178));
        
        drawCancel();
		drawOk();
        
        this.getRootPane().setDefaultButton(jButtonOk);
        mPanel.setBorder(mBorder);
        areaPanel.setBorder(areaBorder);
        dialogTitlePanel.setBackground(Color.WHITE);
        dialogTitlePanel.setBorder(border4);
        
        drawHeader();
        drawLogDate();
        drawTime();
        drawNotification();
        drawProgress();
            
        jPanel6.add(jLabelLogDate, null);
        jPanel6.add(jSpinnerLogDate, null);
        jPanel6.add(jButtonLogDate, null);

        getContentPane().add(mPanel);
        mPanel.add(areaPanel, BorderLayout.CENTER);
        mPanel.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.add(jButtonOk, null);
        buttonsPanel.add(jButtonCancel, null);
        this.getContentPane().add(dialogTitlePanel, BorderLayout.NORTH);
        dialogTitlePanel.add(header, null);
        areaPanel.add(jPanel2, BorderLayout.CENTER);
        jPanel2.add(jPanel6, null);
        // added by rawsushi
        jPanel2.add(jPanelTime, null);

        jPanel2.add(new JPanel(), null);
        
        jPanel2.add(jPanelProgress);
    }

	public void setLogDate(CalendarDate d) {
		this.jSpinnerLogDate.getModel().setValue(d.getDate());
	}
	
	public void setLogDateLimit(CalendarDate min, CalendarDate max) {
		this.logDateMin = min;
		this.logDateMax = max;
	}
	
    void okB_actionPerformed(ActionEvent e) {
    	CANCELLED = false;
        this.dispose();
    }

    void cancelB_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    void setLogDateB_actionPerformed(ActionEvent e) {
        startCalFrame.setLocation(jButtonLogDate.getLocation());
        startCalFrame.setSize(200, 200);
        this.getLayeredPane().add(startCalFrame);
        startCalFrame.show();
    }

    void setNotifB_actionPerformed(ActionEvent e) {
    }

}