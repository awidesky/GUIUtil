package io.github.awidesky.guiUtil.formatter.token;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import io.github.awidesky.guiUtil.level.Level;

/**
 * A {@link Token} implementation for the {@code %d} pattern in a log formatter.
 *
 * <p>This token appends the current date/time to the log output.
 * The date/time is formatted using a {@link DateTimeFormatter}. A custom format can be
 * specified using a pattern compatible with {@link DateTimeFormatter#ofPattern(String)}.
 *
 *
 * <p>Examples:
 * <pre>
 * %d                 -> default {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} format
 * %d{HH:mm:ss}       -> time only
 * %d{yyyy-MM-dd}     -> date only
 * %d{yyyy-MM-dd HH:mm:ss.SSS}
 * </pre>
 *
 * <p>The formatter uses the system default time zone via
 * {@link ZoneId#systemDefault()}.
 *
 * <p>{@link DateTimeFormatter} is immutable and thread-safe, so instances of this
 * class can be safely shared between multiple threads.
 */
public class DateToken implements Token {

    /** Formatter used to render the current timestamp. */
    private final DateTimeFormatter formatter;

	/**
	 * Creates a {@code DateToken} using the default ISO local date-time format.
	 * The formatter uses {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} with the
	 * system default time zone.
	 */
	public DateToken() {
		formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());
	}

	/**
	 * Creates a {@code DateToken} using a custom date/time pattern.
	 *
	 * @param pattern a date/time pattern compatible with
	 *                {@link DateTimeFormatter#ofPattern(String)}
	 */
	public DateToken(String pattern) {
		formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
	}

    /**
     * Appends the formatted current timestamp to the provided {@link StringBuilder}.
     *
     * @param sb the target {@code StringBuilder} receiving the formatted output
     * @param level the log level of the message (unused by this token)
     * @param prefix the logger prefix (unused by this token)
     * @param msg the log message (unused by this token)
     */
    @Override
    public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
        sb.append(formatter.format(Instant.now()));
    }
}
