package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A {@link Token} implementation that appends the current thread name.
 *
 * <p>This token corresponds to the {@code %t} pattern in a log formatter
 * pattern string. When appended, it writes the name of the current thread
 * to the output {@link StringBuilder}.
 *
 * <p>The thread name is cached using a {@link ThreadLocal} to avoid repeated
 * calls to {@link Thread#currentThread()} and {@link Thread#getName()} during
 * logging, improving performance for frequently executed log statements.
 *
 * <p>This class is implemented as a singleton because it is stateless and
 * can be safely reused across multiple formatter instances.
 */
public class ThreadToken implements Token {

	private static final ThreadToken instance = new ThreadToken();
	private ThreadToken() {}
	
	/**
	 * Returns the singleton instance of {@code ThreadToken}.
	 *
	 * @return the shared {@code ThreadToken} instance
	 */
	public static ThreadToken instance() { return instance; }
	
	/**
	 * Thread-local cache of the current thread name used for log output.
	 */
	private static final ThreadLocal<String> threadName = ThreadLocal
			.withInitial(() -> Thread.currentThread().getName());

	/**
	 * Appends the current thread name to the provided {@link StringBuilder}.
	 *
	 * @param sb the target {@code StringBuilder} receiving the formatted output
	 * @param level the log level (unused by this token)
	 * @param prefix the logger prefix (unused by this token)
	 * @param msg the log message (unused by this token)
	 */
	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
		sb.append(threadName.get());
	}
}