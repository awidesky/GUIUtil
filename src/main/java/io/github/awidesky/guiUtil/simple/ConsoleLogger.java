package io.github.awidesky.guiUtil;

/**
 * Logs to the console via {@code System.out}.
 */
public class ConsoleLogger extends SimpleLogger {

	/**
	 * Constructs a console logger with auto flush enabled.
	 */
	public ConsoleLogger() {
		this(true);
	}
	/**
	 * Constructs a console logger with given auto flush option.
	 */
	public ConsoleLogger(boolean autoFlush) {
		super(System.out, autoFlush);
	}
	
	/**
	 * Do noting because we shouldn't close {@code System.out}.
	 */
	@Override
	public void close() {
		System.out.flush();
	}

}
