package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A {@link Token} implementation representing a literal text segment in a
 * formatter pattern.
 *
 * <p>
 * This token appends a fixed string exactly as it appears in the formatter
 * pattern. It is used for portions of the pattern that are not interpreted as
 * special formatting tokens.
 *
 * <p>
 * For example, in the pattern:
 * 
 * <pre>
 * [%l] %m
 * </pre>
 * 
 * the characters {@code "["}, {@code "] "} are treated as literal text and
 * represented by {@code LiteralToken} instances.
 */
public class LiteralToken implements Token {

	/** The literal text to append to the output. */
	private final String text;

	/**
	 * Creates a {@code LiteralToken} with the given text.
	 *
	 * @param text the literal text that will be appended to the output
	 */
	public LiteralToken(String text) {
		this.text = text;
	}

	/**
	 * Appends the literal text to the provided {@link StringBuilder}.
	 *
	 * @param sb     the target {@code StringBuilder} receiving the formatted output
	 * @param level  the log level (unused by this token)
	 * @param prefix the logger prefix (unused by this token)
	 * @param msg    the log message (unused by this token)
	 */
	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
		sb.append(text);
	}

}