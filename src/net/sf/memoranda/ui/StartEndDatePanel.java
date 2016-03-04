package net.sf.memoranda.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Local;

/**
 * StartEndDatePanel contains a selector for setting the start and end dates.
 * Designed to be run in a JDialog.
 * The purpose is to be able to more readily use the same options in different
 * dialogs/panels.
 * The vast majority of the code in this class was copied from TaskDialog on 2/24/2016
 * @author James
 *
 */
public class StartEndDatePanel extends JPanel {

    boolean ignoreStartChanged;
    boolean ignoreEndChanged;
    
	private Border border;
	private SimpleDateFormat sdf;
	
	private CalendarDate startDateMin;
	private CalendarDate startDateMax;
	private CalendarDate endDateMin;
	private CalendarDate endDateMax;
	
	private JDialog dialog;
	
    // Start Date
    private JLabel jLabelStartDate = new JLabel();
    private JSpinner jSpinnerStartDate;
    private JButton jButtonStartDate = new JButton();
    CalendarFrame startCalFrame = new CalendarFrame();
    
    // End Date
    private JLabel jLabelEndDate = new JLabel();
    private JSpinner jSpinnerEndDate;
    private JButton jButtonEndDate = new JButton();
    private CalendarFrame endCalFrame = new CalendarFrame();
	
    JPanel startPanel;
    JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	public StartEndDatePanel(JDialog dialog) {
		ignoreStartChanged = false;
		ignoreEndChanged = false;
		startDateMin = CurrentProject.get().getStartDate();
		startDateMax = CurrentProject.get().getEndDate();
		endDateMin = startDateMin;
		endDateMax = startDateMax;
		sdf = CalendarDate.getSimpleDateFormat();
		this.dialog = dialog;
		
		border = BorderFactory.createEtchedBorder(Color.white, 
	            new Color(178, 178, 178));
		
		init();
	}
	
	public Date getStartDate() {
		return (Date)jSpinnerStartDate.getModel().getValue();
	}

	public void setStartDate(CalendarDate d) {
		this.jSpinnerStartDate.getModel().setValue(d.getDate());
	}
	
	public void setEndDate(CalendarDate d) {		
		if (d != null) { 
			this.jSpinnerEndDate.getModel().setValue(d.getDate());
		}
	}
	
	public Date getEndDate() {
		return (Date)jSpinnerEndDate.getModel().getValue();
	}
	
	public void setStartDateLimit(CalendarDate min, CalendarDate max) {
		this.startDateMin = min;
		this.startDateMax = max;
	}
	
	public void setEndDateLimit(CalendarDate min, CalendarDate max) {
		this.endDateMin = min;
		this.endDateMax = max;
	}
	
	private void init() {
		startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		endPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		drawStartDate();
		drawEndDate();
		
		this.add(startPanel);
		this.add(endPanel);
	}
	
	/**
	 * Draws the Start Date UI
	 */
	private void drawStartDate() {
        jSpinnerStartDate = new JSpinner(new SpinnerDateModel(new Date(),null,null,Calendar.DAY_OF_WEEK));
        
		jSpinnerStartDate.setBorder(border);
        jSpinnerStartDate.setPreferredSize(new Dimension(80, 24));
		jSpinnerStartDate.setEditor(new JSpinner.DateEditor(jSpinnerStartDate, sdf.toPattern()));

        jSpinnerStartDate.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	// it's an ugly hack so that the spinner can increase day by day
            	SpinnerDateModel sdm = new SpinnerDateModel((Date)jSpinnerStartDate.getModel().getValue(),null,null,Calendar.DAY_OF_WEEK);
            	jSpinnerStartDate.setModel(sdm);

                if (ignoreStartChanged)
                    return;
                ignoreStartChanged = true;
                Date sd = (Date) jSpinnerStartDate.getModel().getValue();
                Date ed = (Date) jSpinnerEndDate.getModel().getValue();
                if (sd.after(ed)) {
                    jSpinnerStartDate.getModel().setValue(ed);
                    sd = ed;
                }
				if ((startDateMax != null) && sd.after(startDateMax.getDate())) {
					jSpinnerStartDate.getModel().setValue(startDateMax.getDate());
                    sd = startDateMax.getDate();
				}
                if ((startDateMin != null) && sd.before(startDateMin.getDate())) {
                    jSpinnerStartDate.getModel().setValue(startDateMin.getDate());
                    sd = startDateMin.getDate();
                }
                startCalFrame.cal.set(new CalendarDate(sd));
                ignoreStartChanged = false;
            }
        });

        jLabelStartDate.setText(Local.getString("Start date"));
        jLabelStartDate.setMinimumSize(new Dimension(60, 16));
        jLabelStartDate.setMaximumSize(new Dimension(100, 16));
        
        jButtonStartDate.setMinimumSize(new Dimension(24, 24));
        jButtonStartDate.setPreferredSize(new Dimension(24, 24));
        jButtonStartDate.setText("");
        jButtonStartDate.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/calendar.png")));
        jButtonStartDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setStartDateB_actionPerformed(e);
            }
        });
        
        startCalFrame.cal.addSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ignoreStartChanged)
                    return;
                jSpinnerStartDate.getModel().setValue(startCalFrame.cal.get().getCalendar().getTime());
            }
        });
        
        startPanel.add(jLabelStartDate, null);
        startPanel.add(jSpinnerStartDate, null);
        startPanel.add(jButtonStartDate, null);
	}
	
	/**
	 * Draws the End Date UI
	 */
	private void drawEndDate() {
        jSpinnerEndDate = new JSpinner(new SpinnerDateModel(new Date(),null,null,Calendar.DAY_OF_WEEK));
        
        jLabelEndDate.setMaximumSize(new Dimension(270, 16));
        jLabelEndDate.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabelEndDate.setText(Local.getString("End date"));
        jSpinnerEndDate.setBorder(border);
        jSpinnerEndDate.setPreferredSize(new Dimension(80, 24));
        
		jSpinnerEndDate.setEditor(new JSpinner.DateEditor(jSpinnerEndDate, sdf.toPattern())); //Added by (jcscoobyrs) on
		//14-Nov-2003 at 10:45:16PM
        
        jSpinnerEndDate.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	// it's an ugly hack so that the spinner can increase day by day
            	SpinnerDateModel sdm = new SpinnerDateModel((Date)jSpinnerEndDate.getModel().getValue(),null,null,Calendar.DAY_OF_WEEK);
            	jSpinnerEndDate.setModel(sdm);
            	
                if (ignoreEndChanged)
                    return;
                ignoreEndChanged = true;
                Date sd = (Date) jSpinnerStartDate.getModel().getValue();
                Date ed = (Date) jSpinnerEndDate.getModel().getValue();				
				if (ed.before(sd)) {
                    jSpinnerEndDate.getModel().setValue(sd);
                    ed = sd;
                }
				if ((endDateMax != null) && ed.after(endDateMax.getDate())) {
					jSpinnerEndDate.getModel().setValue(endDateMax.getDate());
                    ed = endDateMax.getDate();
				}
                if ((endDateMin != null) && ed.before(endDateMin.getDate())) {
                    jSpinnerEndDate.getModel().setValue(endDateMin.getDate());
                    ed = endDateMin.getDate();
                }
				endCalFrame.cal.set(new CalendarDate(ed));
                ignoreEndChanged = false;
            }
        });
        jButtonEndDate.setMinimumSize(new Dimension(24, 24));
        jButtonEndDate.setPreferredSize(new Dimension(24, 24));
        jButtonEndDate.setText("");
        jButtonEndDate.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/calendar.png")));
        jButtonEndDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setEndDateB_actionPerformed(e);
            }
        });
		
		
        endCalFrame.cal.addSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ignoreEndChanged)
                    return;
                jSpinnerEndDate.getModel().setValue(endCalFrame.cal.get().getCalendar().getTime());
            }
        });
        
		endPanel.add(jLabelEndDate, null);
		endPanel.add(jSpinnerEndDate, null);
		endPanel.add(jButtonEndDate, null);
	}

    private void setStartDateB_actionPerformed(ActionEvent e) {
        startCalFrame.setLocation(jButtonStartDate.getLocation());
        startCalFrame.setSize(200, 200);
        dialog.getLayeredPane().add(startCalFrame);
        startCalFrame.show();

    }

    private void setEndDateB_actionPerformed(ActionEvent e) {
        endCalFrame.setLocation(jButtonEndDate.getLocation());
        endCalFrame.setSize(200, 200);
        dialog.getLayeredPane().add(endCalFrame);
        endCalFrame.show();
    }
}