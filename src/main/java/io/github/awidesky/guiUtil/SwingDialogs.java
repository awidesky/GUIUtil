/*
 * Copyright (c) 2023 Eugene Hong
 *
 * This software is distributed under license. Use of this software
 * implies agreement with all terms and conditions of the accompanying
 * software license.
 * Please refer to LICENSE
 * */

package io.github.awidesky.guiUtil;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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

	
	public static void setLogger(AbstractLogger newLogger) {//TODO : Logger
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

		logger.log("\n");//TODO : no need?
		String co = content.replace("%e%", (e == null || e.getMessage() == null) ? "null" : e.getMessage());
		
		if (waitTillClosed) {
			showErrorDialog(title, co);
		} else {
			SwingUtilities.invokeLater(() -> {
				showErrorDialog(title, co);
			});
		}
		
		logger.log("[SwingDialogs.error] " + title + "\n\t" + co);
		if(e != null) logger.log(e);
		
	}
	
	/**
	 * Show error dialog.
	 * this method returns after the dialog closed.
	 * */
	private static void showErrorDialog(String title, String content) {

		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true); //TODO : dispose_on_close
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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

		logger.log("\n");
		String co = content.replace("%e%", (e == null || e.getMessage() == null) ? "null" : e.getMessage());
		
		if (waitTillClosed) {
			showWarningDialog(title, co);
		} else {
			SwingUtilities.invokeLater(() -> {
				showWarningDialog(title, co);
			});
		}
		
		logger.log("[SwingDialogs.warning] " + title + "\n\t" + co);
		if(e != null) logger.log(e);
		
	}
	
	/**
	 * Show warning dialog.
	 * this method returns after the dialog closed.
	 * */
	private static void showWarningDialog(String title, String content) {
		
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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

		logger.log("\n");

		if (waitTillClosed) {
			showInfoDialog(title, content);
		} else {
			SwingUtilities.invokeLater(() -> {
				showInfoDialog(title, content);
			});
		}
		
		logger.log("[SwingDialogs.info] " + title + "\n\t" + content);
		
	}
	
	/**
	 * Show information dialog.
	 * this method returns after the dialog closed.
	 * */
	private static void showInfoDialog(String title, String content) {
		
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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

		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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
		return JOptionPane.showConfirmDialog(dialog, message, title,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	} 
}
