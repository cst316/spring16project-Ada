package net.sf.memoranda.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
	private boolean timerLogged;
	
	public TimerDialog(Task task) {
		super(App.getFrame(), Local.getString("Timer"), false);
		this.task = task;
		timeDelta = 0;
		timer = new Timer();
		
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
		
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent event) {
				// do nothing
			}

			@Override
			public void windowClosed(WindowEvent event) {
				// do nothing
			}

			@Override
			public void windowClosing(WindowEvent event) {
				checkIfLoggedToContinue();
			}

			@Override
			public void windowDeactivated(WindowEvent event) {
				// do nothing
			}

			@Override
			public void windowDeiconified(WindowEvent event) {
				// do nothing
			}

			@Override
			public void windowIconified(WindowEvent event) {
				// do nothing
			}

			@Override
			public void windowOpened(WindowEvent event) {
				// do nothing
			}
		});
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
		logButton.setEnabled(false);
		
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
		
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				logButton_actionPerformed(event);
			}
		});
                
		buttonsPanel.add(startStopButton);
		buttonsPanel.add(resetButton);
		buttonsPanel.add(logButton);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	private void reset() {
		timeDelta = 0;
		timerLogged = true;
		
		logButton.setEnabled(false);
		
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
        if (timerLogged) {
        	reset();
        	timerLogged = false;
        }
		
		if (timer.isRunning) {
			timer.stop();
			startStopButton.setText(Local.getString("Start"));
		} else {
			timer.start();
			startStopButton.setText(Local.getString("Stop"));
		}
		pack();
	}
	
	/**
	 * Logs current time and appends it to current Task logs.
	 */
	private void logTime() {
		long length = timeDelta;
		
		task.addLoggedTime(date.toString(), length);
		timerLogged = true;
		logButton.setEnabled(false);
	}
	
	/**
	 * Action handler for logging the current time.
	 */
	private void logButton_actionPerformed(ActionEvent event) {
		if (timer.isRunning) {
			timer.stop();
		}
		
		logTime();
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
			
			logButton.setEnabled(true);
			
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
			Util.debug("Starting timer for task: " + task.getID());
			isRunning = true;
			new Thread(this).start();
		}
		
		/**
		 * Stop the timer.
		 */
		public void stop() {
			Util.debug("Stopping timer for task: " + task.getID());
			isRunning = false;
		}
	}
        private void resetButton_actionPerformed(ActionEvent event) {
            boolean toContinue = checkIfLoggedToContinue();
            if(toContinue){
                this.timer.stop();
                reset();
                startStopButton.setText(Local.getString("Start"));
            }
        }

		/**
		 * Check if the timer has been logged. If not checks if the user wants
		 * to log. Will return false if the user cancels the dialog.
		 */
		private boolean checkIfLoggedToContinue() {
			boolean toContinue = true;
			if(!timerLogged && timeDelta > 0L){
                int dialogSelection = JOptionPane.showConfirmDialog(
                            App.getFrame(),
                            "Log time before continuing?",
                            Local.getString("Timer"),
                            JOptionPane.YES_NO_OPTION);
                
                if (dialogSelection == JOptionPane.YES_OPTION){
                   logTime();
                } else if (dialogSelection == JOptionPane.CLOSED_OPTION){
                	toContinue = false;
                }
            }
			return toContinue;
		}
}