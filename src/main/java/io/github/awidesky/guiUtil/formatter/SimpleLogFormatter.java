package io.github.awidesky.guiUtil.formatter;

import java.util.ArrayList;
import java.util.List;

import io.github.awidesky.guiUtil.formatter.token.DateToken;
import io.github.awidesky.guiUtil.formatter.token.LevelToken;
import io.github.awidesky.guiUtil.formatter.token.LiteralToken;
import io.github.awidesky.guiUtil.formatter.token.MessageToken;
import io.github.awidesky.guiUtil.formatter.token.PrefixToken;
import io.github.awidesky.guiUtil.formatter.token.ThreadToken;
import io.github.awidesky.guiUtil.formatter.token.Token;
import io.github.awidesky.guiUtil.level.Level;

/**
 * A simple pattern-based implementation of {@link LogFormatter}.
 *
 * <p>This formatter interprets a pattern string containing special tokens
 * and converts it into a sequence of {@link Token} objects. During formatting,
 * each token appends its corresponding content to the output.
 *
 * <p>The pattern is parsed once when {@link #setPattern(String)} is called,
 * allowing repeated calls to {@link #format(Level, String, CharSequence)} to
 * execute efficiently without repeated pattern parsing.
 *
 * <p>Supported pattern elements include:
 * <pre>
 * %l  - log level name
 * %t  - thread name
 * %d  - date/time using default format
 * %d{pattern} - date/time using a custom {@link java.time.format.DateTimeFormatter} pattern
 * %p  - logger prefix
 * %m  - log message
 * %%  - literal '%' character
 * </pre>
 *
 */
public class SimpleLogFormatter extends LogFormatter {
	
	/** Thread-local {@link StringBuilder} used to build formatted log messages. */
	private static final ThreadLocal<StringBuilder> sbLocal = ThreadLocal.withInitial(() -> new StringBuilder(256));
	
	/** Parsed tokens representing the current formatting pattern. */
	private List<Token> tokens;
	private String pattern;
	
	/**
	 * Creates a formatter with the default pattern:
	 * <pre>
	 * [%l] [%t] [%d] %p%m
	 * </pre>
	 */
	public SimpleLogFormatter() {
		this("[%l] [%t] [%d] %p%m");
	}
	
	/**
	 * Creates a formatter with a custom pattern.
	 *
	 * @param pattern the formatting pattern string
	 */
	public SimpleLogFormatter(String pattern) {
		setPattern(pattern);
	}
	
	/**
	 * Sets the pattern used by this formatter and parses it into tokens.
	 *
	 * @param pattern the new formatting pattern
	 * @return this formatter instance
	 */
	public SimpleLogFormatter setPattern(String pattern) {
	    this.pattern = pattern;
	    this.tokens = parsePattern(pattern);
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
	 * Parses a pattern string into a sequence of {@link Token} objects.
	 *
	 * <p>Literal text is collected and stored as {@link LiteralToken}
	 * instances, while recognized pattern elements are converted into
	 * their corresponding token implementations.
	 *
	 * @param pattern the pattern string to parse
	 * @return a list of parsed tokens
	 */
	private List<Token> parsePattern(String pattern) {
	    List<Token> tokens = new ArrayList<>();
	    StringBuilder literal = new StringBuilder();

	    for (int i = 0; i < pattern.length(); i++) {
	        char c = pattern.charAt(i);

	        if (c != '%') {
	            literal.append(c);
	            continue;
	        }

	        // flush literal
	        if (literal.length() > 0) {
	            tokens.add(new LiteralToken(literal.toString()));
	            literal.setLength(0);
	        }

	        if (i + 1 >= pattern.length()) {
	            literal.append('%');
	            break;
	        }

	        char type = pattern.charAt(++i);

	        switch (type) {

	        case '%':
	            literal.append('%');
	            break;
	        case 'l':
	            tokens.add(LevelToken.instance());
	            break;
	        case 't':
	            tokens.add(ThreadToken.instance());
	            break;
	        case 'p':
	            tokens.add(PrefixToken.instance());
	            break;
	        case 'm':
	            tokens.add(MessageToken.instance());
	            break;
	        case 'd':
	            i = parseDateToken(pattern, i, tokens);
	            break;
	        default:
	            literal.append('%').append(type);
	        }
	    }

	    if (literal.length() > 0)
	        tokens.add(new LiteralToken(literal.toString()));

	    return tokens;
	}
	
	/**
	 * Parses a date token beginning with {@code %d}.
	 *
	 * <p>If a custom pattern is specified using braces (e.g. {@code %d{HH:mm:ss}}),
	 * the pattern inside the braces is used to construct a {@link DateToken}.
	 * Otherwise, a {@link DateToken} with the default format is created.
	 *
	 * @param pattern the full formatter pattern
	 * @param i the current index in the pattern
	 * @param tokens the list receiving the parsed token
	 * @return the index position after the parsed token
	 */
	private int parseDateToken(String pattern, int i, List<Token> tokens) {
	    if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '{') {
	        int end = pattern.indexOf('}', i + 2);

	        if (end != -1) {
	            String fmt = pattern.substring(i + 2, end);
	            tokens.add(new DateToken(fmt));
	            return end;
	        }
	    }

	    // use default date, if token was %d
	    tokens.add(new DateToken());
	    return i;
	}
	
	/**
	 * Formats a log message using the previously parsed token sequence.
	 *
	 * <p>Each token appends its content to a thread-local {@link StringBuilder},
	 * which is then converted into the final log string.
	 *
	 * @param level the log level of the message
	 * @param prefix the logger prefix
	 * @param msg the log message
	 * @return the formatted log string
	 */
	@Override
	public String format(Level level, String prefix, CharSequence msg) {
	    StringBuilder sb = sbLocal.get();
	    sb.setLength(0);

	    for(Token t : tokens) {
	        t.append(sb, level, prefix, msg);
	    }

	    return sb.toString();
	}
	
	/**
	 * Creates a copy of this formatter using the same pattern.
	 *
	 * @return a new {@code SimpleLogFormatter} with the same pattern
	 */
	@Override
	public SimpleLogFormatter clone() {
		return new SimpleLogFormatter(pattern);
	}

	@Override
	public String toString() {
		return "SimpleLogFormatter with pattern : \"" + pattern + "\"]";
	}

}