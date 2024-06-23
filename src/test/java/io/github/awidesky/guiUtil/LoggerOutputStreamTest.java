package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.level.Level;

class LoggerPrintStreamTest {

	private static final String msg = "Hel\u1F61lo, World!\u1F60@안녕하세요";
	private static final Charset charset = StandardCharsets.UTF_16LE;
	private static final Level level = Level.INFO;
	
	@Test
	void testPrintStream() throws IOException {
		StringWriter sw = new StringWriter();
		try(Logger l = new SimpleLogger(sw, true);
			PrintStream ps = l.toPrintStream(level, true, charset);) {
			ps.print(msg);
		}
		assertEquals(level.getPrefixText() + msg, sw.toString().strip());
	}
}
