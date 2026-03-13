package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

public class MessageToken implements Token {

	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
		sb.append(msg);
	}

}
