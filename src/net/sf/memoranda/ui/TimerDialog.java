package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.memoranda.Task;
import net.sf.memoranda.date.CalendarDate;
import net.sf.memoranda.util.Local;
import net.sf.memoranda.util.Util;

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
	private Timer timer;
	
	private JLabel timerLabel;
	private JLabel dateLabel;
	
	private JButton startStopButton;
	private JButton resetButton;
	private JButton logButton;
        private boolean ResetCancelled;
        private boolean TimerLogged;
	
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
		
		startStopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				startStopButton_actionPerformed(event);
			}
		});
            
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				resetButton_actionPerformed(event);
			}
		});
                
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
		pack();
	}
	
	/**
	 * Updates the timer label from the timer.
	 */
	private void updateTimerLabel() {
		timerLabel.setText(timeToString(timeDelta));
	}
	
	/**
	 * Action handler for starting and stopping the timer.
	 */
	private void startStopButton_actionPerformed(ActionEvent event) {
                TimerLogged = false;
		if (timer == null) {
			timer = new Timer();
		}
		
		if (timer.isRunning) {
			Util.debug("Stopping timer for task: " + task.getID());
			timer.stop();
			startStopButton.setText(Local.getString("Start"));
		} else {
			Util.debug("Starting timer for task: " + task.getID());
			timer.start();
			startStopButton.setText(Local.getString("Stop"));
		}
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
	
	/**
	 * Timer runs on a separate thread to update the timeDelta.
	 * @author James Smith
	 */
	private class Timer implements Runnable {
		
		boolean isRunning;
		long lastTimeDelta;
		long lastSecond;
		
		Timer() {
			isRunning = false;
		}

		@Override
		public void run() {
			lastTimeDelta = System.currentTimeMillis();
			lastSecond = TimerDialog.this.timeDelta / 1000;
			
			while (isRunning) {
				long newTimeDelta = System.currentTimeMillis();
				TimerDialog.this.timeDelta += newTimeDelta - lastTimeDelta;
				long newSecond = TimerDialog.this.timeDelta / 1000;
				
				if (newSecond > lastSecond) {
					lastSecond = newSecond;
					updateTimerLabel();
					pack();
				}
				lastTimeDelta = newTimeDelta;
			}
		}
		
		/**
		 * Start the timer.
		 */
		public void start() {
			isRunning = true;
			new Thread(this).start();
		}
		
		/**
		 * Stop the timer.
		 */
		public void stop() {
			isRunning = false;
		}
	}
        private void resetButton_actionPerformed(ActionEvent event) {
            ResetCancelled = false;
            if(!TimerLogged){
                int n = JOptionPane.showConfirmDialog(
                            App.getFrame(),
                            "do you want to log time before reset?",
                            Local.getString("resetting timer"),
                            JOptionPane.YES_NO_OPTION);
                            if (n == JOptionPane.YES_OPTION){
                               //TODO
                               //log time here!
                            } else if(n == JOptionPane.CLOSED_OPTION){
                                ResetCancelled = true;
                            }
            }
            if(!ResetCancelled){
                this.timer.stop();
                reset();
                startStopButton.setText(Local.getString("Start"));
            }
        }
}