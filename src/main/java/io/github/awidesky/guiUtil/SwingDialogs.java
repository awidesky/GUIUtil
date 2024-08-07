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
 * <p>{@code SwingDialogs} has it's own {@code static Logger}, which is {@code Logger#nullLogger} in default.
 * It is changeable via {@code SwingDialogs#setLogger(AbstractLogger)}.
 * */
public class SwingDialogs {


	private static Logger logger = Logger.nullLogger;
	private volatile static boolean alwaysOnTop = true;

	/**
	 * Set Logger for all SwingDialog operation.
	 * */
	public static void setLogger(Logger newLogger) {
		logger = newLogger;
	}
	
	/**
	 * Return if the dialogs will always on top of other windows.
	 * */
	public static boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	/**
	 * Global setting of whether the dialogs will always on top of other windows.
	 * @param alwaysOnTop
	 */
	public static void setAlwaysOnTop(boolean alwaysOnTop) {
		SwingDialogs.alwaysOnTop = alwaysOnTop;
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
	public static void error(String title, String content, Throwable e, boolean waitTillClosed) {

		String co = content.replace("%e%", (e == null || e.getMessage() == null) ? "null" : e.getMessage());
		
		logger.error("[SwingDialogs.error] " + title + "\n\t" + co);
		if(e != null) logger.error(e);
		
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

		final JDialog dialog = setDialog(new JDialog(), "[SwingDialogs.error]");
		
		if (EventQueue.isDispatchThread()) {

			JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.ERROR_MESSAGE);
			dialog.dispose();
			
		} else {
			
			try {
				SwingUtilities.invokeAndWait(() -> {
					JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.ERROR_MESSAGE);
					dialog.dispose();
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
	public static void warning(String title, String content, Throwable e, boolean waitTillClosed) {

		String co = content.replace("%e%", (e == null || e.getMessage() == null) ? "null" : e.getMessage());
		
		logger.warning("[SwingDialogs.warning] " + title + "\n\t" + co);
		if(e != null) logger.warning(e);
		
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
		
		final JDialog dialog = setDialog(new JDialog(), "[SwingDialogs.warning]");
		
		if (EventQueue.isDispatchThread()) {

			JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.WARNING_MESSAGE);
			dialog.dispose();
			
		} else {
			
			try {
				SwingUtilities.invokeAndWait(() -> {
					JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.WARNING_MESSAGE);
					dialog.dispose();
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
		
		logger.info("[SwingDialogs.info] " + title + "\n\t" + content);

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
		
		final JDialog dialog = setDialog(new JDialog(), "[SwingDialogs.info]");
		
		if (EventQueue.isDispatchThread()) {

			JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.INFORMATION_MESSAGE);
			dialog.dispose();
			
		} else {
			
			try {
				SwingUtilities.invokeAndWait(() -> {
					JOptionPane.showMessageDialog(dialog, content.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.INFORMATION_MESSAGE);
					dialog.dispose();
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

		logger.info("[SwingDialogs.confirm] " + title + "\n\t" + message);

		
		if (EventQueue.isDispatchThread()) {

			return showConfirmDialog(title, message);
			
		} else {
			
			final AtomicReference<Boolean> result = new AtomicReference<>();

			try {

				SwingUtilities.invokeAndWait(() -> {
					result.set(showConfirmDialog(title, message));
				});
				
				return result.get();

			} catch (Exception e) {
				error("Exception in Thread working(SwingWorker)",
						e.getClass().getName() + "-%e%\nI'll consider you chose \"no\"", (e instanceof InvocationTargetException) ? (Exception)e.getCause() : e, false);
			}

			return false;

		}

	}
	
	private static boolean showConfirmDialog(String title, String message) {
		final JDialog dialog = setDialog(new JDialog(), "[SwingDialogs.confirm]");
		boolean result = JOptionPane.showConfirmDialog(dialog, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		dialog.dispose();
		logger.info("[SwingDialogs.confirm] response was : " + (result ? " Yes" : "No"));
		return result;
	}
	
	
	/**
	 * Show an input dialog.
	 * 
	 * @return User's answer. If user canceled, {@code null}.
	 * */
	public static String input(String title, String prompt) {
		return input(title, prompt, null);
	}
	
	/**
	 * Show an input dialog with given initial value.
	 * 
	 * @return User's answer. If user canceled, {@code null}.
	 * */
	public static String input(String title, String prompt, Object initialValue) {
		logger.info("[SwingDialogs.input] " + title + "\n\t" + prompt);
		String ret = showInputDialog(title, prompt, initialValue);
		logger.info("[SwingDialogs.input] Input was : " + ret);
		return ret;
	}
	
	/**
	 * Show input dialog.
	 * this method returns after the dialog closed.
	 * */
	private static String showInputDialog(String title, String prompt, Object initialValue) {

		final JDialog dialog = setDialog(new JDialog(), "[SwingDialogs.input]");
		
		if (EventQueue.isDispatchThread()) {

			String str = (String)JOptionPane.showInputDialog(dialog, prompt.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
			dialog.dispose();
			return str;
			
		} else {
			AtomicReference<String> ret = new AtomicReference<>();
			try {
				SwingUtilities.invokeAndWait(() -> {
					ret.set((String)JOptionPane.showInputDialog(dialog, prompt.replace("\n", System.lineSeparator()), title.replace("\n", System.lineSeparator()), JOptionPane.QUESTION_MESSAGE, null, null, initialValue));
					dialog.dispose();
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
		logger.info("[SwingDialogs.inputPassword] " + title + "\n\t" + prompt);
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

		JDialog dlg = setDialog(op.createDialog(title.replace("\n", System.lineSeparator())), "[SwingDialogs.inputPassword]");

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
	
	private static JDialog setDialog(JDialog dialog, String name) {
		dialog.setName(name);
		dialog.setAlwaysOnTop(alwaysOnTop);
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