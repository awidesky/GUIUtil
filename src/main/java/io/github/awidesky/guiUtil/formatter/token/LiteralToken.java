package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

public class LiteralToken implements Token {

    private final String text;

    public LiteralToken(String text) {
        this.text = text;
    }

    @Override
    public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
        sb.append(text);
    }

}
