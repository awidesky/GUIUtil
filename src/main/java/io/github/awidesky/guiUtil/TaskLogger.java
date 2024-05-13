/*
 * Copyright (c) 2023 Eugene Hong
 *
 * This software is distributed under license. Use of this software
 * implies agreement with all terms and conditions of the accompanying
 * software license.
 * Please refer to LICENSE
 * */

package io.github.awidesky.guiUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.function.Consumer;

import io.github.awidesky.guiUtil.level.Level;

/**
 * An abstract Logger class for one-consumer, multi-provider model that manages each logs as "task"
 * (whose type is {@code Consumer<PrintWriter>})
 * <p>log() method family call does not actually write the content to external log destination.
 * Instead, log content will be packed in a task. The content is actually written 
 * when the task is executed.
 * 
 * <p><code>TaskLogger</code> provides abstract methods that queue({@code TaskLogger#queueLogTask(Consumer)}) 
 * or run({@code TaskLogger#runLogTask(Consumer)}) the task. The queue(if exists) should be generated and managed in 
 * another class or subclass. <code>TaskLogger</code> does not know or care about the queue.
 * */
public abstract class TaskLogger extends AbstractLogger {

	/**
	 * Creates a task based logger.
	 * */
	public TaskLogger(String prefix, Level level) {
		setLogLevel(level);;
		this.prefix = prefix;
	}

	/**
	 * Queue a log task.
	 * Implementation may queue given <code>logTask</code> to a worker thread. 
	 * */
	protected abstract void queueLogTask(Consumer<PrintWriter> logTask);
	/**
	 * Try to run a log task <i><b>right away</b></i>.
	 * 
	 * @return <code>true</code> if succeed to run the <code>logTask</code> right away
	 * */
	protected abstract boolean runLogTask(Consumer<PrintWriter> logTask);
	
	/**
	 * Logs a empty line without any prefix.
	 * */
	@Override
	public void newLine() {
		queueLogTask((logTo) -> {
			logTo.println();
		});
	}

	@Override
	public void writeString(Level level, CharSequence str) {
		queueLogTask(getLogTask(level, str));
	}

	/**
	 * Try to log a String <i><b>right away</b></i>.
	 * 
	 * @return <code>true</code> if succeed to log a String right away.
	 * */
	public boolean logNow(Level level, String data) {//TODO : remove?
		return runLogTask(getLogTask(level, data));
	}
	/**
	 * Try to log an Throwable <i><b>right away</b></i>.
	 * 
	 * @return <code>true</code> if succeed to log an Exception right away.
	 * */
	public boolean logNow(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return logNow(sw.toString());
	}
	/**
	 * Try to log Objects(by calling {@code Object#toString()}) <i><b>right away</b></i>.
	 * 
	 * @return <code>true</code> if succeed to log all Objects right away.
	 * */
	public boolean logNow(Object... objs) {
		return Arrays.stream(objs).map(Object::toString).allMatch(this::logNow);
	}
	
	/**
	 * Prefix will printed only once even if the String is multiple line.
	 * */
	protected Consumer<PrintWriter> getLogTask(Level level, CharSequence data) {
		return (logTo) -> {
			logTo.println(getPrefix(level) + data);
		};
	}
	
	/** Close the logger. */
	@Override
	public abstract void close();
}