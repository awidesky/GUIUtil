package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

public class PrefixToken implements Token {

	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
        if (prefix != null) sb.append(prefix);
	}

}
