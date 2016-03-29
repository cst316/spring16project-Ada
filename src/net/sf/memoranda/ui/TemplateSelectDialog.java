package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Template;
import net.sf.memoranda.TemplateList;
import net.sf.memoranda.TemplateListImpl;
import net.sf.memoranda.util.Local;

/**
 * Dialog for selecting one template.
 * @author James Smith (jsmit106)
 *
 */
public class TemplateSelectDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private boolean cancelled = true;
        public boolean remove = false;
	
	private Border defaultBorder;
	
	/**
	 * This map pairs each template to it's radio button. This allows for a simple retrieval of
	 * the selected template given only the radio button that's been selected.
	 */
	private Map<JRadioButton, Template> templatesMap;
	private JRadioButton selectedRadioButton;
	
	// Header
	private JPanel headerPanel;
	private JLabel headerLabel;
    
	// Template Selection
	private ButtonGroup templateButtonGroup;
	private JScrollPane templateScrollPane;
	private JPanel templatePanel;
	private JPanel templateInnerPanel;
    
	// OK, Delete, and Cancel Buttons
        private JButton deleteButton;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel buttonPanel;
	
	public TemplateSelectDialog(Frame frame, String title) {
		super(frame, title, true);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
            new ExceptionDialog(ex);
            ex.printStackTrace();
        }
	}
	
	/**
	 * Check if dialog is cancelled.
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/**
	 * Get the template selected by the user. Returns null if no template is selected.
	 */
	public Template getTemplate() {
		Template template = null;
		
		if (selectedRadioButton != null && templatesMap.containsKey(selectedRadioButton)) {
			template = templatesMap.get(selectedRadioButton);
		}
		
		return template;
	}
	
	/**
	 * Initialize components of the dialog.
	 */
	private void jbInit() {
		this.setResizable(false);
		defaultBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		templatesMap = new HashMap<JRadioButton, Template>();
		
		drawHeader();
		drawTemplateSelection();
		drawButtons();
	}
	
	/**
	 * Sets up and defines the components and layout of the header.
	 */
	private void drawHeader() {
		headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        headerLabel = new JLabel();
        headerLabel.setFont(new java.awt.Font("Dialog", 0, 20));
        headerLabel.setForeground(new Color(0, 0, 124));
        headerLabel.setText(Local.getString("Select Template"));
        headerPanel.add(headerLabel);
        this.getContentPane().add(headerPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Sets up and defines the components and layout of the templates list.
	 */
	private void drawTemplateSelection() {
		TemplateList templateList = CurrentProject.getTemplateList();
		Collection<String> templateIds = templateList.getIds();
		GridBagConstraints gc = new GridBagConstraints();
		templateButtonGroup = new ButtonGroup();
		gc.ipadx = 3;
		gc.ipady = 3;
		gc.gridy = -1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.WEST;
		
		templateInnerPanel = new JPanel(new GridBagLayout());
		
		for (String templateId : templateIds) {
			Template template = templateList.getTemplate(templateId);
			JRadioButton radioButton = new JRadioButton();
			JLabel title = new JLabel(Local.getString(template.getTitle()));
			
			gc.gridy = gc.gridy + 1;
			gc.gridx = 0;
			gc.anchor = GridBagConstraints.CENTER;
			templateInnerPanel.add(radioButton, gc);
			gc.gridx = 1;
			gc.anchor = GridBagConstraints.WEST;
			templateInnerPanel.add(title, gc);
			
			templatesMap.put(radioButton, template);
			templateButtonGroup.add(radioButton);
			
			// The ok button needs to be enabled once a template is selected.
			radioButton.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					okButton.setEnabled(true);
                                        deleteButton.setEnabled(true);
			        TemplateSelectDialog.this.getRootPane().setDefaultButton(okButton);
			        selectedRadioButton = (JRadioButton) event.getSource();
				}
			});
		}
		
		templateScrollPane = new JScrollPane(templateInnerPanel);
		
		templatePanel = new JPanel(new BorderLayout());
		templatePanel.setPreferredSize(new Dimension(400, 300));
		templatePanel.setBorder(defaultBorder);
		templatePanel.add(templateScrollPane, BorderLayout.CENTER);
		this.getContentPane().add(templatePanel, BorderLayout.CENTER);
	}
	
	/**
	 * Sets up and defines the components and layout of the ok, delete, and cancel buttons.
	 */
	private void drawButtons() {
		okButton = new JButton();
		okButton.setMaximumSize(new Dimension(100, 26));
		okButton.setMinimumSize(new Dimension(100, 26));
		okButton.setPreferredSize(new Dimension(100, 26));
		okButton.setText(Local.getString("Ok"));
		okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
        		cancelled = false;
        		TemplateSelectDialog.this.dispose();
            }
        });
		okButton.setEnabled(false);
                
                deleteButton = new JButton();
		deleteButton.setMaximumSize(new Dimension(100, 26));
		deleteButton.setMinimumSize(new Dimension(100, 26));
		deleteButton.setPreferredSize(new Dimension(100, 26));
		deleteButton.setText(Local.getString("Delete"));
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
        		cancelled = false;
                        int n = JOptionPane.showConfirmDialog(
                            App.getFrame(),
                            "this cannot be undone",
                            Local.getString("Remove Template?"),
                            JOptionPane.YES_NO_OPTION);
                            if (n == JOptionPane.YES_OPTION){
                                remove = true;
                                TemplateSelectDialog.this.dispose();
                                return;
                            }
                            if (n == JOptionPane.NO_OPTION){
                                remove = false;
                                cancelled = true;
                                return;
                            }
        		//TemplateSelectDialog.this.dispose();
            }
        });
		deleteButton.setEnabled(false);
        
        cancelButton = new JButton();
        cancelButton.setMaximumSize(new Dimension(100, 26));
        cancelButton.setMinimumSize(new Dimension(100, 26));
        cancelButton.setPreferredSize(new Dimension(100, 26));
        cancelButton.setText(Local.getString("Cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                TemplateSelectDialog.this.dispose();
            }
        });
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(defaultBorder);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
}