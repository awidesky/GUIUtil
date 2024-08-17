package io.github.awidesky.guiUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.awidesky.guiUtil.level.Level;

/**
 * Gather logs in a internal buffer.
 * {@code StringLogger#getString()} will return the logged data as {@code String}.
 * <br>
 * Field {@code printLogLevel} is set to {@code false} initially, since {@code StringLogger} 
 * is normally used to gobble output, like {@code java.io.StringWriter}.
 */
public class StringLogger extends AbstractLogger {

	private List<String> list = Collections.synchronizedList(new LinkedList<>());
	
	/**
	 * Creates a new StringLogger, without automatic line flushing,
	 * and with {@code printLogLevel} set to {@code false}.
	 */
	public StringLogger() {
		this(false);
	}
	/**
	 * Creates a new StringLogger,
	 * with {@code printLogLevel} set to {@code false}. 
	 */
	public StringLogger(boolean autoFlush) {
		setPrintLogLevel(false);
	}
	
	/**
	 * Returns the log string that were collected
	 * After this call, the log buffer will cleared.
	 * 
	 * @return Texts that are logged since last call of this method.
	 */
	public String getString() {
		String ret = list.stream().collect(Collectors.joining("\n"));
		clear();
		return ret;
	}
	
	/**
	 * Removes all of the logs from this logger.
	 * {@code getString()} will return empty String
	 * after this call returns.
	 */
	public void clear() {
		list.clear();
	}
	
	@Override
	public void newLine() {
		list.add("\n");
	}
	
	/**
	 * Does not do anything.<p>
	 * If clearing inner buffer of the {@code StringLogger} is needed,
	 * call {@code StringLogger#clear()}.
	 */
	@Override
	public void close() {}
	
	@Override
	protected void writeString(Level level, CharSequence str) {
		list.add(getPrefix(level) + str);
	}
}
