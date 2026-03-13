/*
 * Copyright (c) 2023 Eugene Hong
 *
 * This software is distributed under license. Use of this software
 * implies agreement with all terms and conditions of the accompanying
 * software license.
 * Please refer to LICENSE
 * */

package io.github.awidesky.guiUtil.thread;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import io.github.awidesky.guiUtil.formatter.LogFormatter;
import io.github.awidesky.guiUtil.formatter.SimpleLogFormatter;
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
	private LogFormatter formatter = new SimpleLogFormatter();
	
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
	 * Get a new builder for a child logger of this {@code LoggerThread}.
	 * 
	 * @return generated {@code LoggerBuilder} instance.
	 * @see LoggerBuilder
	 */
	public LoggerBuilder getLoggerBuilder() {
		return new LoggerBuilder();
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
	 * Set main log formatter of this {@code LoggerThread}.
	 * formatter of child {@code TaskLogger} generated after this call will set to 
	 * the given {@code formatter}.<br>
	 * Children {@code TaskLogger}s who created previously is not effected.
	 * 
	 * @see LoggerThread#setFormatterAllChildren(UnaryOperator)
	 * */
	public void setLogFormatter(LogFormatter formatter) {
		this.formatter = formatter;
	}
	/**
	 * Set log formatter of this {@code LoggerThread} and all existing children {@code TaskLogger}s,
	 * using the given {@code UnaryOperator}.
	 * {@code LogFormatter} field of each {@code TaskLogger} will applied to given {@code UnaryOperator},
	 * and the return value will set to {@code LogFormatter} field of the {@code TaskLogger}.
	 * <br>
	 * The {@code LoggerThread}'s main log formatter will changed by the given {@code formatChanger},
	 * and used as the default log formatter of child {@code TaskLogger}s that generated after this call.
	 * <p>
	 * Children {@code TaskLogger}s who created previously are also changed to new format using the {@code UnaryOperator}.
	 * */
	public void setFormatterAllChildren(UnaryOperator<LogFormatter> formatterChanger) {
		this.formatter = formatterChanger.apply(formatter);
		children.stream().forEach(l -> l.setLogFormatter(formatterChanger.apply(l.getLogFormatter())));
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
	
	/**
	 * Builder for child {@code TaskLogger} instance of the {@code LoggerThread}.<br>
	 * A {@code TaskLogger} or {@code TaskBufferedLogger} instance can generated via
	 * {@code LoggerBuilder#getLogger()} and {@code LoggerBuilder#getLogger()} methods.
	 * Generated {@code TaskLogger} will be managed by outer {@code LoggerThread} object.
	 * <p>
	 * The {@code LogFormatter}, {@code Level}, {@code prefix} properties of 
	 * generated logger will set to {@code LoggerThread#formatter}, {@code LoggerThread#level},
	 * {@code null} in default, and can be specified via setter methods in {@code LoggerBuilder}.<br>
	 * {@code LoggerBuilder#setCloneLogFormatter(boolean)} can define whether 
	 * the log formatter should be cloned or not when generating the logger instance.
	 * If {@code false}(default), every loggers generated will share same log formatter instance,
	 * else, the log formatter will be cloned each time the logger is generated.
	 */
	public class LoggerBuilder {
		
		private LogFormatter childLogFormatter = LoggerThread.this.formatter;
		private Level childLevel = LoggerThread.this.level;
		private String childPrefix = null;
		private boolean cloneLogFormatter = false;
		
		/**
		 * Specifies log level.
		 * @param level the log level to use.
		 * @return This builder instance
		 */
		public LoggerBuilder setLevel(Level level) {
			childLevel = level;
			return this;
		}
		/**
		 * Specifies log formatter.<br>
		 * Given log formatter instance will be shared among generated loggers,
		 * hence, {@code LogFormatter#pattern(String)} will affect every child logger
		 * generated by this builder.
		 * Set {@code LoggerBuilder#setCloneLogFormatter(boolean)} to {@code true}
		 * to clone log formatter instead of share.
		 * 
		 * @param formatter the formatter to use.
		 * @return This builder instance
		 */
		public LoggerBuilder setLogFormatter(LogFormatter formatter) {
			childLogFormatter = formatter;
			return this;
		}
		/**
		 * Specifies additional prefix {@code String}.
		 * @param prefix the additional prefix {@code String}.
		 * @return This builder instance
		 */
		public LoggerBuilder setPrefix(String prefix) {
			this.childPrefix  = prefix;
			return this;
		}
		/**
		 * Specifies whether to clone {@code LogFormatter}
		 * @param cloneLogFormatter
		 * @return
		 */
		public LoggerBuilder setCloneLogFormatter(boolean cloneLogFormatter) {
			this.cloneLogFormatter  = cloneLogFormatter;
			return this;
		}
		private LogFormatter getLogFormatter() {
			return cloneLogFormatter ? childLogFormatter.clone() : childLogFormatter;
		}
		
		/**
		 * Returns a new {@code TaskLogger} that submits logs to the logger thread with specified properties.
		 * */
		public TaskLogger getLogger() {
			TaskLogger newLogger = new TaskLogger(getLogFormatter(), childLevel) {

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
					children.remove(this);
				}
				
			};
			newLogger.setPrefix(childPrefix);
			children.add(newLogger);
			return newLogger;
		}
		
		/**
		 * Returns a new {@code TaskBufferedLogger} that submits logs to the logger thread only if flushed
		 * with specified properties.
		 * 
		 * @see TaskBufferedLogger#flush()
		 * */
		public TaskBufferedLogger getBufferedLogger() {
			TaskBufferedLogger newLogger = new TaskBufferedLogger(getLogFormatter(), childLevel) {

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
			newLogger.setPrefix(childPrefix);
			children.add(newLogger);
			return newLogger;
		}
		
	}
	
}
