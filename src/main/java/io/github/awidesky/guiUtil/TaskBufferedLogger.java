/*
 * Copyright (c) 2023 Eugene Hong
 *
 * This software is distributed under license. Use of this software
 * implies agreement with all terms and conditions of the accompanying
 * software license.
 * Please refer to LICENSE
 * */

package io.github.awidesky.guiUtil;

import java.io.Flushable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A <code>TaskLogger</code> that buffer all logs to <code>StringWriter</code> and does not actually prints it
 * before {@code TaskBufferedLogger#flush()} is called.
 * {@code TaskBufferedLogger#runLogTask(Consumer)} is not supported; this class is for buffered operation only.
 * */
public abstract class TaskBufferedLogger extends TaskLogger implements Flushable {

	private StringWriter buffer = new StringWriter();

	/**
	 * Creates a task based buffered logger.
	 * */
	public TaskBufferedLogger(String prefix, Level level) {
		super(prefix, level);
	}

	/**
	 * A <code>TaskBufferedLogger</code> does not support immediate logging.
	 * */
	@Override
	protected final boolean runLogTask(Consumer<PrintWriter> logTask) {
		throw new UnsupportedOperationException("TaskBufferedLogger supports buffered operation only!");
	}
	
	/**
	 * A <code>TaskBufferedLogger</code> does not support immediate logging.
	 * */
	@Override
	public boolean logNow(Level level, String data) {
		throw new UnsupportedOperationException("TaskBufferedLogger supports buffered operation only!");
	}

	/**
	 * Log empty newLine.
	 * */
	@Override
	public void newLine() {
		buffer.append(System.lineSeparator());
	}
	
	/**
	 * Logs a String.
	 * */
	@Override
	public void writeString(Level level, CharSequence data) {
		buffer.append(getPrefix(level) + data);
		newLine();
	}
	
	/**
	 * Empty the buffer and put logs into the queue. 
	 * */
	@Override
	public void flush() {
		if(buffer.getBuffer().length() != 0) {
			String str = buffer.toString();
			buffer.getBuffer().setLength(0);
			queueLogTask((logTo) -> {
				logTo.print(str);
			});
		}
	}

}
