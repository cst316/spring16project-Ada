/**
 * 
 */
package net.sf.memoranda.ui;

import java.util.Calendar;
import java.util.Date;

import net.sf.memoranda.CurrentProject;
import net.sf.memoranda.Template;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.CurrentStorage;
import net.sf.memoranda.util.Util;

/**
 * Public interface for methods to control the dialogs involving templates.
 * @author james
 *
 */
public interface TemplateDialogInterface {
	
	/**
	 * Sets up and displays the TaskDialog configured for a template.
	 */
	static void openEditTemplate(Template template) {
		TaskDialog editDialog = new TaskDialog(
				App.getFrame(),
				"Edit template",
				null,
				true);
		
		int[] dateDifference = template.getDateDifference();
		int priority = template.getPriority();
		long effort = template.getEffort();
		float effortInHours = ((float) effort) / 1000 / 60 / 60;
		String description = template.getDescription();
		String name = template.getTitle();
		String type = template.getType();
		
		// A negative date difference means there is no end date.
		if (dateDifference[0] < 0) {
			Util.debug("No end date");
			editDialog.jCheckBoxEndDate.setSelected(false);
		    editDialog.chkEndDate_actionPerformed(null);
		} else {
			editDialog.jCheckBoxEndDate.setSelected(true);
		    editDialog.chkEndDate_actionPerformed(null);
			
			// apply the date difference to the start date to get the end date
			Date startDate = (Date) editDialog.jSpinnerStartDate.getModel().getValue();
			Calendar endDate = new CalendarDate(startDate).getCalendar();
			
			endDate.roll(Calendar.YEAR, dateDifference[2]);
			endDate.roll(Calendar.MONTH, dateDifference[1]);
			endDate.roll(Calendar.DAY_OF_MONTH, dateDifference[0]);
			
			editDialog.jSpinnerEndDate.getModel().setValue(
					new CalendarDate(endDate).getDate());
		}
		editDialog.descriptionField.setText(description);
		editDialog.jTextFieldName.setText(name);
		editDialog.jTextFieldType.setText(type);
		editDialog.effortField.setText(effortInHours + "");
		editDialog.jComboBoxPriority.setSelectedIndex(priority);
		
		editDialog.setLocationRelativeTo(App.getFrame());
		editDialog.setVisible(true);
		
		if (!editDialog.CANCELLED) {
			String newName = editDialog.jTextFieldName.getText();
			String newType = editDialog.jTextFieldType.getText();
			String newDescription = editDialog.descriptionField.getText();
			long neweffort =
					(long)
					(Float.parseFloat(editDialog.effortField.getText())
					* 1000 * 60 * 60);
			int newPriority = editDialog.jComboBoxPriority.getSelectedIndex();
			CalendarDate newStartDate =	new CalendarDate(
					(Date) editDialog.jSpinnerStartDate.getModel().getValue());
			CalendarDate newEndDate =
					(editDialog.jCheckBoxEndDate.isSelected()
					// assign the end date if end date is selected
					? new CalendarDate((Date)
							editDialog.
							jSpinnerEndDate.
							getModel().
							getValue())
					// if end date is not selected, return null
					: null);
			
			template.setDateDifference(newStartDate, newEndDate);
			template.setDescription(newDescription);
			template.setEffort(neweffort);
			template.setPriority(newPriority);
			template.setTitle(newName);
			template.setType(newType);
			
    		CurrentStorage.get().storeTemplateList(
    				CurrentProject.getTemplateList(), CurrentProject.get());
		}
	}
}
