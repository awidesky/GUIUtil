package io.github.awidesky.guiUtil.formatter.token;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.awidesky.guiUtil.level.Level;

public class DateToken implements Token {

    private final DateFormat format;

    public DateToken() {
        this.format = new SimpleDateFormat();
    }
    public DateToken(String pattern) {
    	this.format = new SimpleDateFormat(pattern);
    }

    @Override
    public void append(StringBuilder sb, Level level, String prefix, CharSequence msg) {
        sb.append(format.format(new Date()));
    }
}
