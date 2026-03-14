package io.github.awidesky.guiUtil.formatter;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A helper class for specifying log format(pattern).
 */
public abstract class LogFormatter implements Cloneable {
	
	/**
	 * Generate a log string with given level, prefix, and message.
	 * 
	 * @param level
	 * @param prefix
	 * @param msg
	 * @return
	 */
	public abstract String format(Level level, String prefix, CharSequence msg);
	
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
