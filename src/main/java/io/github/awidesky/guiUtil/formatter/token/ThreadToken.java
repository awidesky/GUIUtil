package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

public class ThreadToken implements Token {

	@Override
	public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
		sb.append(Thread.currentThread().getName());
	}

}
