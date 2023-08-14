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
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


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
 * {@code LoggerThread#kill(int)} is called. {@code LoggerThread#kill(int)} must be called before the main application terminates.
 * */
public class LoggerThread extends Thread {

	private PrintWriter logTo = null;
	private	LinkedBlockingQueue<Consumer<PrintWriter>> loggerQueue = new LinkedBlockingQueue<>();
	private Set<TaskLogger> children = Collections.synchronizedSet(new HashSet<TaskLogger>());
	
	public volatile boolean isStop = false;
	private boolean verbose = false;
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
		logTo = new PrintWriter(new OutputStreamWriter(os, cs), autoFlush);
	}
	
	/**
	 * Returns a new {@code TaskLogger} with no initial prefix.
	 * Returned {@code TaskLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskLogger getLogger() {
		return getLogger(verbose, null);
	} 
	/**
	 * Returns a new {@code TaskLogger} with given prefix.
	 * Returned {@code TaskLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskLogger getLogger(String prefix) {
		return getLogger(verbose, prefix);
	}
	/**
	 * Returns a new {@code TaskLogger} with given prefix and verbosity.
	 * */
	public TaskLogger getLogger(boolean verbose, String prefix) {
		TaskLogger newLogger = new TaskLogger(verbose, prefix) {

			@Override
			public void queueLogTask(Consumer<PrintWriter> logTask) {
				try {
					loggerQueue.put(logTask);
				} catch (InterruptedException e) {
					if (!isStop) log(e);
				}
			}

			@Override
			public boolean runLogTask(Consumer<PrintWriter> logTask) {
				return loggerQueue.offer(logTask);
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
		return getBufferedLogger(verbose, null);
	} 
	/**
	 * Returns a new {@code TaskBufferedLogger} with given prefix.
	 * Returned {@code TaskBufferedLogger}'s verbosity is same as value of {@code LoggerThread#isVerbose()}
	 * */
	public TaskBufferedLogger getBufferedLogger(String prefix) {
		return getBufferedLogger(verbose, prefix);
	}
	/**
	 * Returns a new {@code TaskBufferedLogger} with given prefix and verbosity.
	 * */
	public TaskBufferedLogger getBufferedLogger(boolean verbose, String prefix) {
		TaskBufferedLogger newLogger = new TaskBufferedLogger(verbose, prefix) {

			@Override
			public void queueLogTask(Consumer<PrintWriter> logTask) {
				try {
					loggerQueue.put(logTask);
				} catch (InterruptedException e) {
					loggerQueue.offer(logTask);
					if (!isStop) log(e);
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
	 * Set verbosity of this {@code LoggerThread}.
	 * Verbosity of child {@code TaskLogger} generated after this call will set to {@code verbose}.
	 * Children {@code TaskLogger}s who created previously is not effected.
	 * */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	/**
	 * Set verbosity of this {@code LoggerThread} and all existing children {@code TaskLogger}s.
	 * Verbosity of child {@code TaskLogger} generated after this call will set to {@code verbose}.
	 * Children {@code TaskLogger}s who created previously is also effected.
	 * */
	public void setVerboseAllChildren(boolean verbose) {
		this.verbose = verbose;
		children.stream().forEach(l -> l.setVerbose(verbose));
	}
	/**
	 * Get verbosity of this {@code LoggerThread}.
	 * It means verbosity of child {@code TaskLogger} generated in this point will have.
	 * */
	public boolean isVerbose() {
		return verbose;
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
	 * date prefix of child {@code TaskLogger} generated after this call will set to {@code datePrefix}.
	 * Children {@code TaskLogger}s who created previously is also effected.
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
	public void kill(int timeOut) { //TODO : Rename to shutdown
		
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
