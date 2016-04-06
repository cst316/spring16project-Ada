package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.memoranda.Task;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Local;

/**
 * Dialog for displaying a timer to log time for a Task.
 * @author James Smith
 *
 */
public class TimerDialog extends JDialog {

	private static final long serialVersionUID = 3961666401256470067L;

	private long timeDelta;
	
	private CalendarDate date;
	private Task task;
	
	private JLabel timerLabel;
	private JLabel dateLabel;
	
	private JButton startStopButton;
	private JButton resetButton;
	private JButton logButton;
	
	public TimerDialog(Task task) {
		super(App.getFrame(), Local.getString("Timer"), false);
		this.task = task;
		timeDelta = 0;
		
		try {
			jbInit();
			reset();
		} catch(Exception e) {
			new ExceptionDialog(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize dialog.
	 */
	private void jbInit() {
		drawHeader();
		drawTimer();
		drawButtons();
	}
	
	private void drawHeader() {
		JLabel header = new JLabel(task.getText());
		
		header.setAlignmentX(1f);
		this.getContentPane().add(header, BorderLayout.NORTH);
	}
	
	private void drawTimer() {
		JPanel timerPanel = new JPanel();
		timerLabel = new JLabel();
		dateLabel = new JLabel();
		
		timerLabel.setAlignmentX(.5f);
		timerLabel.setFont(new Font("Dialog", 0, 30));
		dateLabel.setAlignmentX(.5f);
		dateLabel.setFont(new Font("Dialog", 0, 20));
		
		timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
		timerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		timerPanel.add(timerLabel);
		timerPanel.add(dateLabel);
		this.getContentPane().add(timerPanel, BorderLayout.CENTER);
	}
	
	private void drawButtons() {
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		startStopButton = new JButton(Local.getString("Start"));
		resetButton = new JButton(Local.getString("Reset"));
		logButton = new JButton(Local.getString("Log"));
		
		buttonsPanel.add(startStopButton);
		buttonsPanel.add(resetButton);
		buttonsPanel.add(logButton);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	private void reset() {
		timeDelta = 0;
		date = CalendarDate.today();
		dateLabel.setText(CalendarDate.getSimpleDateFormat().format(date.getDate()));
		updateTimerLabel();
	}
	
	/**
	 * Updates the timer label from the timer.
	 */
	private void updateTimerLabel() {
		timerLabel.setText(timeToString(timeDelta));
		pack();
	}
	
	/**
	 * Formats a time value to a String.
	 * @param time	time in milliseconds
	 * @return	formatted String
	 */
	private static String timeToString(long time) {
		StringBuilder timeString = new StringBuilder();
		long hours = time / 1000 / 60 / 60;
		time = time % (1000 * 60 * 60);
		long minutes = time / 1000 / 60;
		time = time % (1000 * 60);
		long seconds = time / 1000;
		
		timeString.append(hours < 10 ? "0" + hours : hours);
		timeString.append(':');
		timeString.append(minutes < 10 ? "0" + minutes : minutes);
		timeString.append(':');
		timeString.append(seconds < 10 ? "0" + seconds : seconds);
		
		return timeString.toString();
	}
}