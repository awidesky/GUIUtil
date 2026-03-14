package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A {@link Token} implementation that appends the log message.
 *
 * <p>This token corresponds to the {@code %m} pattern in a log formatter
 * pattern string. When appended, it writes the provided log message
 * ({@code msg}) directly to the output {@link StringBuilder}.
 *
 * <p>This class is implemented as a singleton because it is stateless and
 * can be safely reused across multiple formatter instances.
 */
public class MessageToken implements Token {

	private static final MessageToken instance = new MessageToken();
	private MessageToken() {}
	
	/**
	 * Returns the singleton instance of {@code MessageToken}.
	 *
	 * @return the shared {@code MessageToken} instance
	 */
	public static MessageToken instance() { return instance; }

	/**
	 * Appends the log message to the provided {@link StringBuilder}.
	 *
	 * @param sb the target {@code StringBuilder} receiving the formatted output
	 * @param level the log level (unused by this token)
	 * @param prefix the logger prefix (unused by this token)
	 * @param msg the log message to append
	 */
	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
		sb.append(msg);
	}

}