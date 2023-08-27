/*
 * Copyright (c) 2023 Eugene Hong
 *
 * This software is distributed under license. Use of this software
 * implies agreement with all terms and conditions of the accompanying
 * software license.
 * Please refer to LICENSE
 * */

package io.github.awidesky.guiUtil;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;


/**
 * An utility class that has static methods for various dialog functionality like 
 * error({@code JOptionPane#ERROR_MESSAGE}), warning({@code JOptionPane#WARNING_MESSAGE}), 
 * information({@code JOptionPane#INFORMATION_MESSAGE}) and confirmation
 * ({@code JOptionPane#showConfirmDialog(java.awt.Component, Object)}.
 * Each utility methods can called outside of {@code Event Dispatch Thread}, and whether wait for user to 
 * close the dialog or not can be set via {@code waitTillClosed} parameter in each methods.
 * 
 * <p>{@code SwingDialogs} has it's own {@code static Logger}, which is {@code Logger#consoleLogger} in default.
 * It is changeable via {@code SwingDialogs#setLogger(AbstractLogger)}.
 * */
public class SwingDialogs {


	private static Logger logger = Logger.consoleLogger;

	/**
	 * Set Logger for all SwingDialog operation.
	 * */
	public static void setLogger(Logger newLogger) {
		logger = newLogger;
	}
	
	/**
	 * Show an error dialog.
	 * String <code>"%e%"</code> in <code>content</code> will replaced by error message({@code Exception#getMessage()}) 
	 * of given <code>Exception</code> if it's not <code>null</code>
	 * 
	 * @param e Return value of {@code e.getMessage()} will replaced with {@code %e%} of {@code content}.
	 * 			If {@code e} or {@code e.getMessage()} is {code null}, {@code %e%} will be replaced to {@code "null"}
	 * @param waitTillClosed If {@code true}, this method will return after user closes the dialog.
	 * */
	public static void error(String title, String content, Exception e, boolean waitTillClosed) {

		String co = content.replace("%e%", (e == null || e.getMessage() == null) ? "null" : e.getMessage());
		
		logger.log("[SwingDialogs.error] " + title + "\n\t" + co);
		if(e != null) logger.log(e);
		
		if (waitTillClosed) {
			showErrorDialog(title, co);
		} else {
			SwingUtilities.invokeLater(() -> {
				showErrorDialog(title, co);
			});
		}
		
	}
	
	/**
	 * Show error dialog.
	 * this method returns after the dialog closed.
	 * */
	private static void showErrorDialog(String title, String content) {

		final JDialog dialog = createDialog();
		
		if (EventQueue.isDispatchThread()) {

			JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.ERROR_MESSAGE);
			
		} else {
			
			try {
				SwingUtilities.invokeAndWait(() -> {
					JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.ERROR_MESSAGE);
				});
			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)", "%e%", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
			}

		}
		
	}
	
	
	
	
	/**
	 * Show a warning dialog.
	 * String <code>"%e%"</code> in <code>content</code> will replaced by warning message of 
	 * given <code>Exception</code> if it's not <code>null</code>
	 * 
	 * @param e Return value of {@code e.getMessage()} will replaced with {@code %e%} of {@code content}.
	 * 			If {@code e} or {@code e.getMessage()} is {code null}, {@code %e%} will be replaced to {@code "null"}
	 * @param waitTillClosed If {@code true}, this method will return after user closes the dialog.
	 * */
	public static void warning(String title, String content, Exception e, boolean waitTillClosed) {

		String co = content.replace("%e%", (e == null || e.getMessage() == null) ? "null" : e.getMessage());
		
		logger.log("[SwingDialogs.warning] " + title + "\n\t" + co);
		if(e != null) logger.log(e);
		
		if (waitTillClosed) {
			showWarningDialog(title, co);
		} else {
			SwingUtilities.invokeLater(() -> {
				showWarningDialog(title, co);
			});
		}
		
	}
	
	/**
	 * Show warning dialog.
	 * this method returns after the dialog closed.
	 * */
	private static void showWarningDialog(String title, String content) {
		
		final JDialog dialog = createDialog();
		
		if (EventQueue.isDispatchThread()) {

			JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.WARNING_MESSAGE);
			
		} else {
			
			try {
				SwingUtilities.invokeAndWait(() -> {
					JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.WARNING_MESSAGE);
				});
			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)", "%e%", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
			}

		}
		
	}
	
	
	
	
	/**
	 * Show an information dialog.
	 * 
	 * @param waitTillClosed If {@code true}, this method will return after user closes the dialog.
	 * */
	public static void information(String title, String content, boolean waitTillClosed) {
		
		logger.log("[SwingDialogs.info] " + title + "\n\t" + content);

		if (waitTillClosed) {
			showInfoDialog(title, content);
		} else {
			SwingUtilities.invokeLater(() -> {
				showInfoDialog(title, content);
			});
		}
		
	}
	
	/**
	 * Show information dialog.
	 * this method returns after the dialog closed.
	 * */
	private static void showInfoDialog(String title, String content) {
		
		final JDialog dialog = createDialog();
		
		if (EventQueue.isDispatchThread()) {

			JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.INFORMATION_MESSAGE);
			
		} else {
			
			try {
				SwingUtilities.invokeAndWait(() -> {
					JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.INFORMATION_MESSAGE);
				});
			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)", "%e%", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
			}

		}
		
	}
	
	

	/**
	 * Show a confirmation dialog.
	 * This method returns after user closed the dialog.
	 * 
	 * @return {@code true} if user chose {@code yes}, otherwise {@code false}.
	 * */
	public static boolean confirm(String title, String message) {

		logger.log("[SwingDialogs.confirm] " + title + "\n\t" + message);

		final JDialog dialog = createDialog();
		
		if (EventQueue.isDispatchThread()) {

			return showConfirmDialog(title, message, dialog);
			
		} else {
			
			final AtomicReference<Boolean> result = new AtomicReference<>();

			try {

				SwingUtilities.invokeAndWait(() -> {
					result.set(showConfirmDialog(title, message, dialog));
				});
				
				return result.get();

			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)",
						e.getClass().getName() + "-%e%\nI'll consider you chose \"no\"", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
			}

			return false;

		}

	}
	
	private static boolean showConfirmDialog(String title, String message, JDialog dialog) {
		boolean result = JOptionPane.showConfirmDialog(dialog, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; 
		logger.log("[SwingDialogs.confirm] response was : " + (result ? " Yes" : "No"));
		return result;
	}
	
	
	/**
	 * Show an input dialog.
	 * 
	 * @return User's answer. If user canceled, {@code null}.
	 * */
	public static String input(String title, String prompt) {
		logger.log("[SwingDialogs.input] " + title + "\n\t" + prompt);
		String ret = showInputDialog(title, prompt);
		logger.log("[SwingDialogs.input] Input was : " + ret);
		return ret;
	}
	
	/**
	 * Show input dialog.
	 * this method returns after the dialog closed.
	 * */
	private static String showInputDialog(String title, String prompt) {

		final JDialog dialog = createDialog();
		
		if (EventQueue.isDispatchThread()) {

			return JOptionPane.showInputDialog(dialog, prompt.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.QUESTION_MESSAGE);
			
		} else {
			AtomicReference<String> ret = new AtomicReference<>();
			try {
				SwingUtilities.invokeAndWait(() -> {
					ret.set(JOptionPane.showInputDialog(dialog, prompt.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.QUESTION_MESSAGE));
				});
				return ret.getAcquire();
			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)", "%e%", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
				return "";
			}

		}
		
	}
	
	/**
	 * Show a password input dialog.
	 * 
	 * @return Received password. If user canceled, {@code null}.
	 * */
	public static char[] inputPassword(String title, String prompt) {
		logger.log("[SwingDialogs.inputPassword] " + title + "\n\t" + prompt);
		return showInputPasswordDialog(title, prompt);
	}
	
	/**
	 * Show password input dialog.
	 * this method returns after the dialog closed.
	 * 
	 * code from https://stackoverflow.com/a/8881370
	 * */
	private static char[] showInputPasswordDialog(String title, String prompt) {
		
		PasswordPanel pPnl = new PasswordPanel(prompt.replace("\n", System.lineSeparator()));
		JOptionPane op = new JOptionPane(pPnl, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		JDialog dlg = op.createDialog(title.replace("\n", System.lineSeparator()));

		// Wire up FocusListener to ensure JPasswordField is able to request focus when
		// the dialog is first shown.
		dlg.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				pPnl.gainedFocus();
			}
		});
		
		Supplier<char[]> getPassword = () -> {
			dlg.setVisible(true);
			dlg.dispose();
			if (op.getValue() != null && op.getValue().equals(JOptionPane.OK_OPTION)) 
				return pPnl.getPassword();
			else return null;
		};
		
		if (EventQueue.isDispatchThread()) {
			return getPassword.get();
		} else {
			AtomicReference<char[]> ret = new AtomicReference<>();
			try {
				SwingUtilities.invokeAndWait(() -> {
					ret.set(getPassword.get());
				});
				return ret.getAcquire();
			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)", "%e%", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
				return null;
			}
			
		}
		
	}
	
	private static JDialog createDialog() {
		JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return dialog;
	}
}


/**
 * Auto-focused password input JPanel with prompt from 
 * <a href=https://stackoverflow.com/a/8881370>https://stackoverflow.com/a/8881370</a>
 *  */
class PasswordPanel extends JPanel {
	
	private static final long serialVersionUID = 3580352895097705874L;
	private final JPasswordField passwordField = new JPasswordField();
	private boolean gainedFocusBefore;

	
	public PasswordPanel(String prompt) {
		super(new BorderLayout(0, 5));
		add(new JLabel(prompt), BorderLayout.NORTH);
		add(passwordField, BorderLayout.SOUTH);
	}
	/**
	 * "Hook" method that causes the JPasswordField to request focus the first time
	 * this method is called.
	 */
	void gainedFocus() {
		if (!gainedFocusBefore) {
			gainedFocusBefore = true;
			passwordField.requestFocusInWindow();
		}
	}
	public char[] getPassword() {
		return passwordField.getPassword();
	}

}