package com.awidesky.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * An Simple Logger class that prints log to a <code>PrintWriter</code>.
 * 
 * @author Eugene Hong
 * */
public class SimpleLogger extends AbstractLogger {
	
	private PrintWriter logTo;
	
	public SimpleLogger() { 
		this(System.out, true, Charset.defaultCharset());
	}
	
	public SimpleLogger(OutputStream os) {
		this(os, true, Charset.defaultCharset());
	}
	
	public SimpleLogger(OutputStream os, Charset cs) {
		this(os, true, cs);
	}
	
	public SimpleLogger(OutputStream os, boolean autoFlush) {
		this(os, autoFlush, Charset.defaultCharset());
	}
	
	public SimpleLogger(OutputStream os, boolean autoFlush, Charset cs) {
		logTo = new PrintWriter(os, autoFlush, cs);
	}

	@Override
	public void newLine() {
		logTo.println();
	}

	@Override
	public void log(String data) {
		logTo.println(data);
	}

	@Override
	public void log(Exception e) {
		e.printStackTrace(logTo);
	}

	@Override
	public void log(Object... objs) {
		Arrays.stream(objs).forEach(logTo::println);
	}

}
