package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
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
import net.sf.memoranda.Template;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.CurrentStorage;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

/*$Id: TaskDialog.java,v 1.25 2005/12/01 08:12:26 alexeya Exp $*/
public class TaskDialog extends JDialog {
	
	// Booleans
    public boolean CANCELLED = true;
    boolean ignoreStartChanged = false;
    boolean ignoreEndChanged = false;
    
    // Strings
    String[] priority = {Local.getString("Lowest"), Local.getString("Low"),
            Local.getString("Normal"), Local.getString("High"),
            Local.getString("Highest")};
    
    String processId = null;
	
	// Format Date
	SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT);
	
	// Forbid to set dates outside the bounds
	CalendarDate startDateMin = CurrentProject.get().getStartDate();
	CalendarDate startDateMax = CurrentProject.get().getEndDate();
	CalendarDate endDateMin = startDateMin;
	CalendarDate endDateMax = startDateMax;
	
	// Panels
    JPanel mPanel = new JPanel(new BorderLayout());
    JPanel areaPanel = new JPanel(new BorderLayout());
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel dialogTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jPanel8 = new JPanel(new GridBagLayout());
    JPanel jPanel2 = new JPanel(new GridLayout(3, 2));
    JPanel jPanel4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel jPanel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel jPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
    // Borders
    Border mBorder;
    Border areaBorder;
    Border border3;
    Border border4;
    Border border8;
    
	// Header
    JLabel header = new JLabel();
    
    // Name
    JLabel jLabelName = new JLabel();
    JTextField jTextFieldName = new JTextField();
    
    // Type
    JLabel jLabelType = new JLabel();
    JTextField jTextFieldType = new JTextField();
    JList<String> jListTypeSuggestion = new JList<>();
    
    // Description
    JLabel jLabelDescription = new JLabel();
    JTextArea descriptionField = new JTextArea();
    JScrollPane descriptionScrollPane = new JScrollPane(descriptionField);
    
    // Start Date
    JLabel jLabelStartDate = new JLabel();
    JSpinner jSpinnerStartDate;
    JButton jButtonStartDate = new JButton();
    CalendarFrame startCalFrame = new CalendarFrame();
    
    // End Date
    JLabel jLabelEndDate = new JLabel();
    JSpinner jSpinnerEndDate;
    JButton jButtonEndDate = new JButton();
    CalendarFrame endCalFrame = new CalendarFrame();
	JCheckBox jCheckBoxEndDate = new JCheckBox();
    
    // Effort
    JPanel jPanelEffort = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel jLabelEffort = new JLabel();
    JTextField effortField = new JTextField();
    
    // Priority
    JLabel jLabelPriority = new JLabel();
    JComboBox<String> jComboBoxPriority = new JComboBox<>(priority);
    
    // Progress
	JPanel jPanelProgress = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	JLabel jLabelProgress = new JLabel();
	JSpinner jSpinnerProgress = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
    
	// Notification
    JButton jButtonNotification = new JButton();
	
    // Buttons
    JButton jButtonCancel = new JButton();
    JButton jButtonSaveTask = new JButton();
    JButton jButtonSaveTemplate = new JButton();
    JButton jButtonOpenTemplate = new JButton();
	
	/**
	 * Draws the UI for the Header
	 * SS: This seems to be redundant from a UI standpoint so I am taking it out.
	 */
    /*
	private void drawHeader() {
        header.setFont(new java.awt.Font("Dialog", 0, 20));
        header.setForeground(new Color(0, 0, 124));
        header.setText(Local.getString("To-do"));
        header.setIcon(new ImageIcon(net.sf.memoranda.ui.TaskDialog.class.getResource(
            "resources/icons/task48.png")));
	}
	*/
	/**
	 * Draws the UI for the Cancel button
	 */
	private void drawCancel() {
        jButtonCancel.setMaximumSize(new Dimension(150, 26));
        jButtonCancel.setMinimumSize(new Dimension(150, 26));
        jButtonCancel.setPreferredSize(new Dimension(150, 26));
        jButtonCancel.setText(Local.getString("Cancel"));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelB_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the UI for the Save Task Button
	 */
	private void drawSaveTask() {
        jButtonSaveTask.setMaximumSize(new Dimension(150, 26));
        jButtonSaveTask.setMinimumSize(new Dimension(150, 26));
        jButtonSaveTask.setPreferredSize(new Dimension(150, 26));
        jButtonSaveTask.setText(Local.getString("Save Task"));
        jButtonSaveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTask_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the UI for the Save Template Button
	 */
	private void drawSaveTemplate() {
        jButtonSaveTemplate.setMaximumSize(new Dimension(150, 26));
        jButtonSaveTemplate.setMinimumSize(new Dimension(150, 26));
        jButtonSaveTemplate.setPreferredSize(new Dimension(150, 26));
        jButtonSaveTemplate.setText(Local.getString("Save as Template"));
        jButtonSaveTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTemplate_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the UI for the Open Template Button
	 */
	private void drawOpenTemplate() {
        jButtonOpenTemplate.setMaximumSize(new Dimension(150, 26));
        jButtonOpenTemplate.setMinimumSize(new Dimension(150, 26));
        jButtonOpenTemplate.setPreferredSize(new Dimension(150, 26));
        jButtonOpenTemplate.setText(Local.getString("Open Template"));
        jButtonOpenTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openTemplate_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the UI for the Name field
	 */
	private void drawName() {
		// Label
        jLabelName.setMaximumSize(new Dimension(100, 16));
        jLabelName.setMinimumSize(new Dimension(60, 16));
        jLabelName.setText(Local.getString("Name"));
		
		// Field
        jTextFieldName.setBorder(border8);
        jTextFieldName.setPreferredSize(new Dimension(375, 24));
	}
	
	/**
	 * Draws the UI for Type field
	 */
	private void drawType() {
		
		// Label
        jLabelType.setMaximumSize(new Dimension(100, 16));
        jLabelType.setMinimumSize(new Dimension(60, 16));
        jLabelType.setText("Type");
        
        // Field
        jTextFieldType.setBorder(border8);
        jTextFieldType.setPreferredSize(new Dimension(375,24));
        
        // Update jTextFieldType upon selection
        jListTypeSuggestion.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				try {
					jTextFieldType.setText(jListTypeSuggestion.getSelectedValue().toString());
				} catch (Exception e) {
					System.out.println("[DEBUG] " + e.getMessage());
				}
				jListTypeSuggestion.setVisible(false);
			}
        });
        
        // Display suggestions with each key press
        try {
            final Collection<String> currentTypes = CurrentProject.getTaskList().getTaskTypes();
            final int size = currentTypes.size();
            if (size > 0) {
                jTextFieldType.addKeyListener(new KeyAdapter() {
                	public void keyTyped(KeyEvent k) {
                        String[] suggestions = new String[size];
                        
                        String currentText = "";
                        
                        if (k.getKeyChar() != KeyEvent.VK_BACK_SPACE) { // exempts backspace
                    		currentText = jTextFieldType.getText() + k.getKeyChar();
                        } else {
                        	currentText = jTextFieldType.getText();
                        }
                		
                		int count = 0;
                		
                		for (String type : currentTypes) {
                			if (count < size) {
                				if (type.length() >= currentText.length() && currentText.length() > 0) { // prevents Type text field from limiting characters
                        			if (type.substring(0, currentText.length()).equalsIgnoreCase(currentText)) {
                        				suggestions[count] = type;
                        				count++;
                        				
                        				jListTypeSuggestion.setVisible(true);
                        			}
                				}
                			}
                		}
                		
                		if (count == 0) {
                			jListTypeSuggestion.setVisible(false);
                		}
                		
            			jListTypeSuggestion.setListData(suggestions);
                	}
                });
            }
        } catch (Exception e) {
        	System.out.println("[DEBUG] No types found");
        }
	}
	
	/**
	 * Draws the UI for the Description field
	 */
	private void drawDescription() {
		// Description label
        jLabelDescription.setMaximumSize(new Dimension(100, 16));
        jLabelDescription.setMinimumSize(new Dimension(60, 16));
        jLabelDescription.setText(Local.getString("Description"));

        // Description field
        descriptionField.setBorder(border8);
        descriptionField.setPreferredSize(new Dimension(375, 387)); // 3 additional pixels from 384 so that the last line is not cut off
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionScrollPane.setPreferredSize(new Dimension(375,96));
	}
	
	/**
	 * Sets grid constraints for the Name, Type, and Description UI elements
	 */
	private void setConstraints() {
        GridBagLayout gbLayout = (GridBagLayout) jPanel8.getLayout();
        
        GridBagConstraints gbCon = new GridBagConstraints();
        
        // Set constraints for Name label
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 1;
        gbCon.anchor = GridBagConstraints.WEST;
        gbLayout.setConstraints(jLabelName,gbCon);
        
        // Set constraints for Name field
        gbCon = new GridBagConstraints();
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 1;
        gbLayout.setConstraints(jTextFieldName,gbCon);
        
        // Set constraints for Type label
        gbCon = new GridBagConstraints();
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 1;
        gbCon.anchor = GridBagConstraints.WEST;
        gbLayout.setConstraints(jLabelType,gbCon);
        
        // Set constraints for Type field
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 1;
        gbLayout.setConstraints(jTextFieldType,gbCon);
        
        // Set constraints for Type suggestion list
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 1;
        gbLayout.setConstraints(jListTypeSuggestion,gbCon);
        
        // Set constraints for Description label
        gbCon = new GridBagConstraints();
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 1;
        gbCon.anchor = GridBagConstraints.WEST;
        gbLayout.setConstraints(jLabelDescription,gbCon);
        
        // Set constraints for Description field
        gbCon = new GridBagConstraints();
        gbCon.gridwidth = GridBagConstraints.REMAINDER;
        gbCon.weighty = 3;
        gbLayout.setConstraints(descriptionScrollPane,gbCon);
		
	}
	
	/**
	 * Draws the Effort UI
	 */
	private void drawEffort() {
		// Label
        jLabelEffort.setMaximumSize(new Dimension(100, 16));
        jLabelEffort.setMinimumSize(new Dimension(60, 16));
        jLabelEffort.setText(Local.getString("Est Effort(hrs)"));
        
        // Field
        effortField.setBorder(border8);
        effortField.setPreferredSize(new Dimension(30, 24));
        
        // Panel to tie it together
        jPanelEffort.add(jLabelEffort, null);
        jPanelEffort.add(effortField, null);
	}
	
	/**
	 * Draws the Start Date UI
	 */
	private void drawStartDate() {
        jSpinnerStartDate = new JSpinner(new SpinnerDateModel(new Date(),null,null,Calendar.DAY_OF_WEEK));
        
		jSpinnerStartDate.setBorder(border8);
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
                if (sd.after(ed) && jCheckBoxEndDate.isSelected()) {
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
	}
	
	/**
	 * Draws the End Date UI
	 */
	private void drawEndDate() {
        jSpinnerEndDate = new JSpinner(new SpinnerDateModel(new Date(),null,null,Calendar.DAY_OF_WEEK));
        
        jLabelEndDate.setMaximumSize(new Dimension(270, 16));
        jLabelEndDate.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabelEndDate.setText(Local.getString("End date"));
        jSpinnerEndDate.setBorder(border8);
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
                    jSpinnerEndDate.getModel().setValue(ed);
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
        
        // Check box - unselected
        jCheckBoxEndDate.setSelected(false);
		chkEndDate_actionPerformed(null);
		jCheckBoxEndDate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chkEndDate_actionPerformed(e);
			}
		});
		
		
        endCalFrame.cal.addSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ignoreEndChanged)
                    return;
                jSpinnerEndDate.getModel().setValue(endCalFrame.cal.get().getCalendar().getTime());
            }
        });
	}
    
	/**
	 * Draws the Notification button
	 */
	private void drawNotification() {
        jButtonNotification.setText(Local.getString("Set notification"));
        jButtonNotification.setIcon(
            new ImageIcon(net.sf.memoranda.ui.AppFrame.class.getResource("resources/icons/notify.png")));
        jButtonNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setNotifB_actionPerformed(e);
            }
        });
	}
	
	/**
	 * Draws the Priority UI
	 */
	private void drawPriority() {
        // Label
        jLabelPriority.setMaximumSize(new Dimension(100, 16));
        jLabelPriority.setMinimumSize(new Dimension(60, 16));
        jLabelPriority.setText(Local.getString("Priority"));
		
        // Combo Box
        jComboBoxPriority.setFont(new java.awt.Font("Dialog", 0, 11));
        jComboBoxPriority.setSelectedItem(Local.getString("Normal"));
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
	
    public TaskDialog(Frame frame, String title, String processId) {
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
        //areaBorder = BorderFactory.createEtchedBorder(Color.white, 
        //    new Color(142, 142, 142));
        //border4 = BorderFactory.createEmptyBorder(0, 5, 0, 5);
        border8 = BorderFactory.createEtchedBorder(Color.white, 
            new Color(178, 178, 178));
        
        drawCancel();
		drawSaveTask();
		drawSaveTemplate();
		drawOpenTemplate();
        
        this.getRootPane().setDefaultButton(jButtonSaveTask);
        mPanel.setBorder(mBorder);
        //areaPanel.setBorder(areaBorder);
        //dialogTitlePanel.setBackground(Color.WHITE);
        //dialogTitlePanel.setBorder(border4);
        
        //drawHeader();
        drawName();
        drawType();   
        drawDescription();
        drawStartDate();
        drawEndDate();
        drawEffort();
        drawNotification();
        drawPriority();
        drawProgress();
        
        setConstraints();
        
        if (processId == null) { // Create New Task, not process task
            jPanel6.add(jLabelStartDate, null);
            jPanel6.add(jSpinnerStartDate, null);
            jPanel6.add(jButtonStartDate, null);

    		jPanel1.add(jCheckBoxEndDate, null);
            jPanel1.add(jLabelEndDate, null);
            jPanel1.add(jSpinnerEndDate, null);
            jPanel1.add(jButtonEndDate, null);
        }

        jPanel4.add(jLabelPriority, null);
        getContentPane().add(mPanel);
        mPanel.add(areaPanel, BorderLayout.CENTER);
        mPanel.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.add(jButtonOpenTemplate, null);
        buttonsPanel.add(jButtonSaveTemplate, null);
        buttonsPanel.add(jButtonSaveTask, null);
        buttonsPanel.add(jButtonCancel, null);
        this.getContentPane().add(dialogTitlePanel, BorderLayout.NORTH);
        dialogTitlePanel.add(header, null);
        areaPanel.add(jPanel8, BorderLayout.NORTH);
        jPanel8.add(jLabelName, null);
        jPanel8.add(jTextFieldName, null);
        jPanel8.add(jLabelType, null);
        jPanel8.add(jTextFieldType, null);
        jPanel8.add(jListTypeSuggestion, null);
        jPanel8.add(jLabelDescription);
        jPanel8.add(descriptionScrollPane, null);
        areaPanel.add(jPanel2, BorderLayout.CENTER);
        jPanel2.add(jPanel6, null);
        jPanel2.add(jPanel1, null);
        // added by rawsushi
        jPanel2.add(jPanelEffort, null);

        jPanel2.add(jPanel4, null);
        jPanel4.add(jComboBoxPriority, null);
        jPanel2.add(jPanel3, null);
        
        jPanel3.add(jButtonNotification, null);
        
        jPanel2.add(jPanelProgress);
    }

	public void setStartDate(CalendarDate d) {
		this.jSpinnerStartDate.getModel().setValue(d.getDate());
	}
	
	public void setEndDate(CalendarDate d) {		
		if (d != null) 
			this.jSpinnerEndDate.getModel().setValue(d.getDate());
	}
	
	public void setStartDateLimit(CalendarDate min, CalendarDate max) {
		this.startDateMin = min;
		this.startDateMax = max;
	}
	
	public void setEndDateLimit(CalendarDate min, CalendarDate max) {
		this.endDateMin = min;
		this.endDateMax = max;
	}
	
    void saveTask_actionPerformed(ActionEvent e) {
    	CANCELLED = false;
        this.dispose();
    }

    void cancelB_actionPerformed(ActionEvent e) {
        this.dispose();
    }
	

    void saveTemplate_actionPerformed(ActionEvent e) {
    	NameTaskTemplateDialog dlg = new NameTaskTemplateDialog(App.getFrame(), Local.getString("Name Task Template"));
    	Dimension frmSize = App.getFrame().getSize();
    	Point loc = App.getFrame().getLocation();
    	dlg.setLocation((frmSize.width - dlg.getSize().width) / 2 + loc.x, (frmSize.height - dlg.getSize().height) / 2 + loc.y);
    	dlg.setVisible(true);
    	if (!dlg.CANCELLED) {
    		CurrentProject.getTemplateList().createTemplate(
    				new CalendarDate((Date) this.jSpinnerStartDate.getModel().getValue()), 
    				getEndDate(), 
    				dlg.getName(), 
    				this.jTextFieldType.getText(), 
    				this.jComboBoxPriority.getSelectedIndex(), 
    				Util.getMillisFromHours(this.effortField.getText()), 
    				this.descriptionField.getText());
    		CurrentStorage.get().storeTemplateList(CurrentProject.getTemplateList(), CurrentProject.get());
    	}
    }

    /**
     * Helper method for getting the end date or a null value of there is no end date.
     */
    private CalendarDate getEndDate() {
    	CalendarDate endDate = null;
    	
    	if (jCheckBoxEndDate.isSelected()) {
    		endDate = new CalendarDate((Date) this.jSpinnerEndDate.getModel().getValue());
    	}
    	
    	return endDate;
    }
    
    void openTemplate_actionPerformed(ActionEvent e) {
    	// Open template list and handle as necessary.
    	TemplateSelectDialog dialog = new TemplateSelectDialog(App.getFrame(), "Select Template");
    	dialog.setLocationRelativeTo(this);
    	dialog.setVisible(true);
    	
    	if (!dialog.isCancelled()) {
    		Template template = dialog.getTemplate();
    		
    		if (template != null) {
    			int[] dateDifference = template.getDateDifference();
    			int priority = template.getPriority();
    			long effort = template.getEffort();
    			float effortInHours = ((float) effort) / 1000 / 60 / 60;
    			String description = template.getDescription();
    			String type = template.getType();
    			
    			// A negative date difference means there is no end date.
    			if (dateDifference[0] < 0) {
    				Util.debug("No end date");
    		        jCheckBoxEndDate.setSelected(false);
    				chkEndDate_actionPerformed(null);
    			} else {
    		        jCheckBoxEndDate.setSelected(true);
    				chkEndDate_actionPerformed(null);
    				
    				// apply the date difference to the start date to get the end date
    				Date startDate = (Date) jSpinnerStartDate.getModel().getValue();
    				Calendar endDate = new CalendarDate(startDate).getCalendar();
    				
    				endDate.roll(Calendar.YEAR, dateDifference[2]);
    				endDate.roll(Calendar.MONTH, dateDifference[1]);
    				endDate.roll(Calendar.DAY_OF_MONTH, dateDifference[0]);
    				
    				jSpinnerEndDate.getModel().setValue(
    						new CalendarDate(endDate).getDate());
    			}
    			
    			// only assign values in the task if the values were defined in the template
    			if (description != "") {
    				descriptionField.setText(template.getDescription());
    			}
    			if (type != "") {
    				jTextFieldType.setText(type);
    			} 
    			
				effortField.setText(effortInHours + "");
				jComboBoxPriority.setSelectedIndex(priority);
    		}
    	}
    }
    
	void chkEndDate_actionPerformed(ActionEvent e) {
		jSpinnerEndDate.setEnabled(jCheckBoxEndDate.isSelected());
		jButtonEndDate.setEnabled(jCheckBoxEndDate.isSelected());
		jLabelEndDate.setEnabled(jCheckBoxEndDate.isSelected());
		if(jCheckBoxEndDate.isSelected()) {
			Date currentEndDate = (Date) jSpinnerEndDate.getModel().getValue();
			Date currentStartDate = (Date) jSpinnerStartDate.getModel().getValue();
			if(currentEndDate.getTime() < currentStartDate.getTime()) {
				jSpinnerEndDate.getModel().setValue(currentStartDate);
			}
		}
	}

    void setStartDateB_actionPerformed(ActionEvent e) {
        startCalFrame.setLocation(jButtonStartDate.getLocation());
        startCalFrame.setSize(200, 200);
        this.getLayeredPane().add(startCalFrame);
        startCalFrame.show();

    }

    void setEndDateB_actionPerformed(ActionEvent e) {
        endCalFrame.setLocation(jButtonEndDate.getLocation());
        endCalFrame.setSize(200, 200);
        this.getLayeredPane().add(endCalFrame);
        endCalFrame.show();
    }
    
    void setNotifB_actionPerformed(ActionEvent e) {
    	((AppFrame)App.getFrame()).workPanel.dailyItemsPanel.eventsPanel.newEventB_actionPerformed(e, 
			this.jTextFieldName.getText(), (Date)jSpinnerStartDate.getModel().getValue(),(Date)jSpinnerEndDate.getModel().getValue());
    }

}