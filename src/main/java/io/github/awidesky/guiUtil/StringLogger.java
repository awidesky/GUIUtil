package io.github.awidesky.guiUtil;

import java.io.StringWriter;

/**
 * Logs in a {@code StringWriter}, so that every log output can be collected into a buffer.
 * {@code StringLogger#getString()} will return the logged data as {@code String}.
 */
public class StringLogger extends SimpleLogger {

	private StringWriter sw;
	
	/**
	 * Creates a new StringLogger, without automatic line flushing.
	 */
	public StringLogger() {
		this(false);
	}
	/**
	 * Creates a new StringLogger.
	 */
	public StringLogger(boolean autoFlush) {
		this(new StringWriter(), autoFlush);
	}
	
	private StringLogger(StringWriter s, boolean autoFlush) {
		super(s, true);
		sw = s;
	}
	
	/**
	 * Returns the log string that were collected
	 * (line separator is {@code System#lineSeparator()}).
	 * After this call, the log buffer will cleared.
	 * 
	 * @return Texts that are logged since last call of this method.
	 */
	public String getString() {
		String ret = sw.toString();
		StringBuffer sb = sw.getBuffer();
		sb.setLength(0);
		sb.trimToSize();
		return ret;
	}
}
