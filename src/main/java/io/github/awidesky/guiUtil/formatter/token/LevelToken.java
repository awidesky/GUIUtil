package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A {@link Token} implementation that appends the name of the log level.
 *
 * <p>This token corresponds to the {@code %l} pattern in a log formatter
 * pattern string. When appended, it writes {@link Level#name()} to the
 * output {@link StringBuilder}.
 *
 * <p>This class is implemented as a singleton because it is stateless and
 * can be safely reused across multiple formatter instances.
 */
public class LevelToken implements Token {
	
	/** Singleton instance of {@code LevelToken}. */
	private static final LevelToken instance = new LevelToken();
	private LevelToken() {}
	
	/**
	 * Returns the singleton instance of {@code LevelToken}.
	 *
	 * @return the shared {@code LevelToken} instance
	 */
	public static LevelToken instance() { return instance; }

	/**
	 * Appends the log level name to the provided {@link StringBuilder}.
	 *
	 * @param sb the target {@code StringBuilder} receiving the formatted output
	 * @param level the log level whose name will be appended
	 * @param prefix the logger prefix (unused by this token)
	 * @param msg the log message (unused by this token)
	 */
	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
		sb.append(level.name());
	}

}