package io.github.awidesky.guiUtil.formatter;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A helper class for specifying log format(pattern).
 */
public abstract class LogFormatter implements Cloneable {
	protected String pattern;
	
	/**
	 * Set pattern for the log format
	 * @param pattern
	 * @return this object
	 */
	public LogFormatter setPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}
	/**
	 * Get the pattern for the log format
	 * @return pattern
	 */
	public String getPattern() {
		return pattern;
	}
	
	/**
	 * Generate a log string with given level, prefix, and message.
	 * 
	 * @param level
	 * @param prefix
	 * @param msg
	 * @return
	 */
	public abstract String format(Level level, String prefix, CharSequence msg);
	
	@Override
	public String toString() {
		return getClass().getName() + " [pattern=\"" + pattern + "\"]";
	}
	
	/**
	 * Clone the {@code LogFormatter} instance.<br>
	 * Implementations can just return a new instance
	 * with same {@code pattern} field.<br>
	 * The {@code LogFormatter} instance is mutable since the pattern is
	 * changeable via {@link LogFormatter#setPattern(String)}.
	 */
	@Override
	public abstract LogFormatter clone();
}
