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

public class SimpleLogFormatter extends LogFormatter {
	
	private List<Token> tokens;
	
	public SimpleLogFormatter() {
		this("[%l] [%t] [%d] %p%m");
	}
	public SimpleLogFormatter(String pattern) {
		setPattern(pattern);
	}
	
	@Override
	public SimpleLogFormatter setPattern(String pattern) {
	    this.pattern = pattern;
	    this.tokens = parsePattern(pattern);
		return this;
	}
	
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
	            tokens.add(new LevelToken());
	            break;
	        case 't':
	            tokens.add(new ThreadToken());
	            break;
	        case 'p':
	            tokens.add(new PrefixToken());
	            break;
	        case 'm':
	            tokens.add(new MessageToken());
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
	
	@Override
	public String format(Level level, String prefix, CharSequence msg) {
	    StringBuilder sb = new StringBuilder(pattern.length() + 32);

	    for(Token t : tokens) {
	        t.append(sb, level, prefix, msg);
	    }

	    return sb.toString();
	}
	
	@Override
	public SimpleLogFormatter clone() {
		return new SimpleLogFormatter(pattern);
	}

}
