package io.github.awidesky.guiUtil.formatter;

import io.github.awidesky.guiUtil.level.Level;
import io.github.awidesky.guiUtil.simple.StringLogger;

/**
 * A {@code NullLogFormatter} does not have any additional prefixes, including 
 * level, date, thread name, etc.
 * {@link NullLogFormatter#format(Level, String, CharSequence)} will always return
 * the log message(third parameter) only.<br>
 * This object can be used when you don't need to print any prefixes at all.
 * (e.g. {@link StringLogger}).
 * 
 * @see StringLogger
 */
public class NullLogFormatter extends LogFormatter {
	
	private static final NullLogFormatter instance = new NullLogFormatter();
	
	/**
	 * Returns a {@code NullLogFormatter} instance.
	 * There is only one instance that initiated since
	 * there is no field specifiable.
	 * 
	 * @return the {@code NullLogFormatter} instance
	 */
	public static NullLogFormatter instance() { return instance; }
	
	/**
	 * The pattern is {@code null}.
	 */
	private NullLogFormatter() { pattern = null; }
	
	/**
	 * Does not set pattern field, just returns itself.
	 * pattern is always {@code null}.
	 */
	@Override
	public LogFormatter setPattern(String pattern) {
		return this;
	}
	
	/**
	 * Just returns the {@code logstr}.
	 * @return the parameter {@code logstr}.
	 */
	@Override
	public String format(Level level, String prefix, CharSequence msg) {
		return msg.toString();
	}

	/**
	 * Return self, since there is no need to duplicate a {@code NullLogFormatter} instance
	 */
	@Override
	public NullLogFormatter clone() {
		return this;
	}
}
