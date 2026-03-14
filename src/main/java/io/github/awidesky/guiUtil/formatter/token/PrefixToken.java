package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A {@link Token} implementation that appends the logger prefix.
 *
 * <p>This token corresponds to the {@code %p} pattern in a log formatter
 * pattern string. When appended, it writes the provided prefix string
 * to the output {@link StringBuilder}.
 *
 * <p>If the prefix is {@code null}, nothing is appended.
 *
 * <p>This class is implemented as a singleton because it is stateless and
 * can be safely reused across multiple formatter instances.
 */
public class PrefixToken implements Token {

	private static final PrefixToken instance = new PrefixToken();
	private PrefixToken() {}
	
	/**
	 * Returns the singleton instance of {@code PrefixToken}.
	 *
	 * @return the shared {@code PrefixToken} instance
	 */
	public static PrefixToken instance() { return instance; }
	
	/**
	 * Appends the logger prefix to the provided {@link StringBuilder}.
	 *
	 * @param sb the target {@code StringBuilder} receiving the formatted output
	 * @param level the log level (unused by this token)
	 * @param prefix the logger prefix to append; may be {@code null}
	 * @param msg the log message (unused by this token)
	 */
	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
        if (prefix != null) sb.append(prefix);
	}

}