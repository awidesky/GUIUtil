package com.awidesky.util;

import java.io.Flushable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

/**
 * A <code>Logger</code> that buffer all logs to <code>StringWriter</code> and does not actually prints it
 * before <code>flush()</code> is called.
 * */
public abstract class TaskBufferedLogger extends TaskLogger implements Flushable {

	private StringWriter buffer = new StringWriter();

	public TaskBufferedLogger(boolean verbose) {
		super(verbose);
	}

	/**
	 * A <code>TaskBufferedLogger</code> does not support immediate logging. 
	 * */
	@Override
	final boolean runLogTask(Consumer<PrintWriter> logTask) { throw new UnsupportedOperationException(); }
	/**
	 * A <code>TaskBufferedLogger</code> does not support immediate logging.
	 * Do same as <code>log</code> method.
	 * */
	@Override
	public boolean logNow(String data) {
		log(data);
		return true;
	}

	@Override
	public void newLine() {
		buffer.append(System.lineSeparator());
	}
	
	@Override
	public void log(String data) {
		Consumer<PrintWriter> task = getLogTask(data);
		PrintWriter pw = new PrintWriter(buffer);
		task.accept(pw);
	}
	
	@Override
	public void flush() {
		queueLogTask(getLogTask(buffer.toString()));
	}

}
