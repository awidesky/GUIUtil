package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

public interface Token {
    void append(StringBuilder sb, Level level, String prefix, CharSequence msg);
}
