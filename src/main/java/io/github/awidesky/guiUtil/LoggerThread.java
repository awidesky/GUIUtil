/*
 * Copyright (c) 2023 Eugene Hong
 *
 * This software is distributed under license. Use of this software
 * implies agreement with all terms and conditions of the accompanying
 * software license.
 * Please refer to LICENSE
 * */

package io.github.awidesky.guiUtil;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import io.github.awidesky.guiUtil.level.Level;
import io.github.awidesky.guiUtil.level.Leveled;


/**
 * A Thread that manages a set of {@code TaskLogger}s that external log destination is the same.
 * It's purpose is to provide a thread-safe logging mechanism where tasks from multiple threads produce logs,
 * but all of them should saved into single destination({@code OutputStream} connected to file, console, web socket... etc.)
 * 
 * <p>Each Thread or task can have their {@code TaskLogger} created by <code>LoggerThread#getLogger</code> or
 * <code>LoggerThread#getBufferedLogger</code> methods, and queued log tasks are executed in the {@code LoggerThread}.
 * {@code LoggerThread} constantly checks the queue and executes log tasks. If <code>autoFlush</code> in 
 * {@code LoggerThread#setLogDestination(OutputStream, boolean, Charset)} is {@code true}, backing {@code PrintWriter}
 * of this logger thread's <code>autoFlush</code> will also be true.
 * 
 * <p>{@code LoggerThread#getLogger()} method family will create a normal {@code TaskLogger}; logs will queued every time you call 
 * {@code log} methods. On the other hand, {@code LoggerThread#getBufferedLogger()} method family will create {@code TaskBufferedLogger}; 
 * logs will not queued to {@code LoggerThread} unless {@code TaskBufferedLogger#flush()} is called.
 * 
 * <p>{@code LoggerThread} is not a Daemon Thread; since external output destination({@code OutputStream}) is not closed until
 * {@code LoggerThread#shutdown(int)} is called. {@code LoggerThread#shutdown(int)} must be called before the main application terminates.
 * */
public class LoggerThread extends Thread implements Leveled {

	private PrintWriter logTo = null;
	private	LinkedBlockingQueue<Consumer<PrintWriter>> loggerQueue = new LinkedBlockingQueue<>();
	private Set<TaskLogger> children = Collections.synchronizedSet(new HashSet<TaskLogger>());
	
	public volatile boolean isStop = false;
	private Level level = Level.getRootLogLevel();
	private DateFormat datePrefix = null;
	
	/** Creates a new logger thread. */
	public LoggerThread() { super("LoggerThread"); }
	
	/**
	 * Set destination of log to given {@code OutputStream}. Log Strings will be encoded in {@code Charset#defaultCharset()}.
	 * Auto flush is set to {@code true}.
	 * */
	public void setLogDestination(OutputStream os) throws IllegalArgumentException {
		setLogDestination(os, true, Charset.defaultCharset());
	}
	/**
	 * Set destination of log to given {@code OutputStream}. Log Strings will be encoded in given {@code Charset}.
	 * Auto flush is set to {@code true}.
	 * */
	public void setLogDestination(OutputStream os, Charset cs) throws IllegalArgumentException {
		setLogDestination(os, true, cs);
	}
	/**
	 * Set destination of log to given {@code OutputStream}. Log Strings will be encoded in {@code Charset#defaultCharset()}.
	 * 
	 * @param  autoFlush  A boolean; if true, the logs will flushed to the output buffer when printed.
	 * */
	public void setLogDestination(OutputStream os, boolean autoFlush) throws IllegalArgumentException {
		setLogDestination(os, autoFlush, Charset.defaultCharset());
	}
	/**
	 * Set destination of log to given {@code OutputStream}. Log Strings will be encoded in given {@code Charset}.
	 * 
	 * @param  autoFlush  A boolean; if true, the logs will flushed to the output buffer when printed.
	 * */
	public void setLogDestination(OutputStream os, boolean autoFlush, Charset cs) throws IllegalArgumentException {
		if(logTo != null) {
			throw new IllegalArgumentException("log output stream is already set, cannot modify!");
		}
		setLogDestination(new OutputStreamWriter(os, cs), autoFlush);
	}
	/**
	 * Set destination of log to given {@code Writer}.
	 * 
	 * @param  autoFlush  A boolean; if true, the logs will flushed to the output buffer when printed.
	 * */
	public void setLogDestination(Writer wt, boolean autoFlush) {
		logTo = new PrintWriter(wt, autoFlush);
	}
	
	
	/**
	 * Returns a new {@code TaskLogger} with no initial prefix.
	 * Returned {@code TaskLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskLogger getLogger() {
		return getLogger(null, level);
	} 
	/**
	 * Returns a new {@code TaskLogger} with given prefix.
	 * Returned {@code TaskLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskLogger getLogger(String prefix) {
		return getLogger(prefix, level);
	}
	/**
	 * Returns a new {@code TaskLogger} with given prefix and verbosity.
	 * */
	public TaskLogger getLogger(String prefix, Level level) {
		TaskLogger newLogger = new TaskLogger(prefix, level) {

			@Override
			public void queueLogTask(Consumer<PrintWriter> logTask) {
				try {
					loggerQueue.put(logTask);
				} catch (InterruptedException e) {
					if (!isStop) error(e);
				}
			}

			@Override
			public void close() {
				children.remove(this);
			}
			
		};
		newLogger.setDatePrefix(datePrefix);
		children.add(newLogger);
		return newLogger;
	}
	
	/**
	 * Returns a new {@code TaskBufferedLogger} with no initial prefix.
	 * Returned {@code TaskBufferedLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskBufferedLogger getBufferedLogger() {
		return getBufferedLogger(null, level);
	} 
	/**
	 * Returns a new {@code TaskBufferedLogger} with given prefix.
	 * Returned {@code TaskBufferedLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskBufferedLogger getBufferedLogger(String prefix) {
		return getBufferedLogger(prefix, level);
	}
	/**
	 * Returns a new {@code TaskBufferedLogger} with given prefix and verbosity.
	 * */
	public TaskBufferedLogger getBufferedLogger(String prefix, Level level) {
		TaskBufferedLogger newLogger = new TaskBufferedLogger(prefix, level) {

			@Override
			public void queueLogTask(Consumer<PrintWriter> logTask) {
				try {
					loggerQueue.put(logTask);
				} catch (InterruptedException e) {
					loggerQueue.offer(logTask);
					if (!isStop) error(e);
				}
			}

			@Override
			public void close() {
				flush();
				children.remove(this);
			}
			
		};
		newLogger.setDatePrefix(datePrefix);
		children.add(newLogger);
		return newLogger;
	}
	
	@Override
	public void run() {

		logTo.println("LoggerThread started at [" + new SimpleDateFormat("yyyy/MM/dd-kk:mm:ss").format(new Date()) + "]");
		
		while (true) {

			if (loggerQueue.isEmpty() && isStop) {
				break;	
			}

			try {
				loggerQueue.take().accept(logTo);
			} catch (InterruptedException e) {
				logTo.println("LoggerThread Interrupted! : " + e.getMessage());
				logTo.println("Closing LoggerThread..");
				break;
			}
		}
		
		logTo.close();

	}
	
	/**
	 * @return true if the child {@code TaskLogger} to be generated
	 * 			will be enabled for the INFO level.
	 */
	@Override
	public boolean isInfoEnabled() {
		return level.compareTo(Level.INFO) >= 0;
	}

	/**
	 * @return true if the child {@code TaskLogger} to be generated
	 * 			will be enabled for the DEBUG level.
	 */
	@Override
	public boolean isDebugEnabled() {
		return level.compareTo(Level.DEBUG) >= 0;
	}

	/**
	 * @return true if the child {@code TaskLogger} to be generated
	 * 			will be enabled for the TRACE level.
	 */
	@Override
	public boolean isTraceEnabled() {
		return level.compareTo(Level.TRACE) >= 0;
	}

	/**
	 * @return true if the child {@code TaskLogger} to be generated
	 * 			will be enabled for the WARNING level.
	 */
	@Override
	public boolean isWarningEnabled() {
		return level.compareTo(Level.WARNING) >= 0;
	}

	/**
	 * @return true if the child {@code TaskLogger} to be generated
	 * 			will be enabled for the ERROR level.
	 */
	@Override
	public boolean isErrorEnabled() {
		return level.compareTo(Level.ERROR) >= 0;
	}

	/**
	 * @return true if the child {@code TaskLogger} to be generated
	 * 			will be enabled for the FATAL level.
	 */
	@Override
	public boolean isFatalEnabled() {
		return level.compareTo(Level.FATAL) >= 0;
	}
	
	/**
	 * Set global log level of this {@code LoggerThread}.
	 * Log level of child {@code TaskLogger}s generated after this call will set to {@code level}.
	 * Children {@code TaskLogger}s who created previously are not effected.
	 * */
	@Override
	public void setLogLevel(Level newLevels) {
		this.level = newLevels;
	}
	
	/**
	 * Get global log level of this {@code LoggerThread}.
	 * Log level of child {@code TaskLogger}s generated will set to returning {@code level}.
	 * */
	@Override
	public Level getLogLevel() {
		return level;
	}
	
	
	/**
	 * Set global log level of this {@code LoggerThread} and all existing children {@code TaskLogger}s.
	 * Log level of child {@code TaskLogger}s generated after this call will set to {@code level}.
	 * Children {@code TaskLogger}s who created previously are also changed to new level.
	 * */
	public void setLogLevelAllChildren(Level newLevel) {
		setLogLevel(newLevel);
		children.stream().forEach(l -> l.setLogLevel(newLevel));
	}
	
	/**
	 * Set date prefix of this {@code LoggerThread}.
	 * date prefix of child {@code TaskLogger} generated after this call will set to {@code datePrefix}.
	 * Children {@code TaskLogger}s who created previously is not effected.
	 * */
	public void setDatePrefix(DateFormat datePrefix) {
		this.datePrefix = datePrefix;
	}
	/**
	 * Set date prefix of this {@code LoggerThread} and all existing children {@code TaskLogger}s.
	 * date prefix of child {@code TaskLogger}s generated after this call will set to {@code datePrefix}.
	 * Children {@code TaskLogger}s who created previously are also changed to new date format.
	 * */
	public void setDatePrefixAllChildren(DateFormat datePrefix) {
		this.datePrefix = datePrefix;
		children.stream().forEach(l -> l.setDatePrefix(datePrefix));
	}
	
	/**
	 * Waits at most <code>timeOut</code> milliseconds for this LoggerThread to die.
	 * A timeout of {@code 0} means to wait forever.
	 * If the time has past and this LoggerThread is not dead, interrupt.
	 * */
	public void shutdown(int timeOut) {
		
		isStop = true;
		
		new HashSet<>(children).stream().forEach(TaskLogger::close);
		
		try {
			this.join(timeOut);
		} catch (InterruptedException e) {
			logTo.println("Failed to join logger thread!");
			e.printStackTrace(logTo);
		}
		
		this.interrupt();
		if(logTo != null) logTo.close();
		
	}
	
	
}
