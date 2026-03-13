package io.github.awidesky.guiUtil.simple;

import io.github.awidesky.guiUtil.level.Level;

/**
 * Writes log into system standard out({@code stdout}, {@code stderr}).
 * If the log level is above {@code Level#WARNING}, it'll logged to {@code System#err}
 */
public class SysoutLogger extends ConsoleLogger {

	/**
	 * Constructs a system logger with given auto flush option.
	 */
	public SysoutLogger(boolean autoFlush) {
		super(autoFlush);
	}
	
	@Override
	protected void writeString(Level level, CharSequence msg) {
		if(Level.WARNING.includes(level))
			System.err.println(formatter.format(level, prefix, msg));
		else 
			System.out.println(formatter.format(level, prefix, msg));
	}

	@Override
	protected void consumeLogString(String str) {
		// SysoutLogger prints log string in writeString, so consumeLogString should not be called
		throw new UnsupportedOperationException("This method should not be called!");
	}
	
	@Override
	public void close() {
		System.err.flush();
		System.out.flush();
	}

}
