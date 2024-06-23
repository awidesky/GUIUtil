package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.level.Level;

class LoggerOutputStreamTest {

	private static final String msg = "Hel\u1F61lo, World!\u1F60@안녕하세요";
	private static final Charset charset = StandardCharsets.UTF_16LE;
	private static final Level level = Level.INFO;
	
	
	@Test
	void testOutputStream() throws IOException {
		StringWriter sw = new StringWriter();
		try(Logger l = new SimpleLogger(sw, true);
			LoggerOutputStream os = l.toOutputStream(level, charset);) {
			byte[] buf = msg.getBytes(charset);
			os.write(buf, 0, 7);
			os.write(buf, 7, buf.length - 7);
			os.close();
		}
		assertEquals(level.getPrefixText() + msg, sw.toString().strip());
	}
	
	@Test
	void testOutputStream_byteBybyte() throws IOException {
		StringWriter sw = new StringWriter();
		try(Logger l = new SimpleLogger(sw, true);
				LoggerOutputStream os = l.toOutputStream(level, charset);) {
			byte[] buf = msg.getBytes(charset);
			for(int i = 0; i < buf.length; i++) {
				os.write(buf, i, 1);
			}
			os.close();
		}
		assertEquals(level.getPrefixText() + msg, sw.toString().strip());
	}
	
	@Test
	void testPrintStream() throws IOException {
		StringWriter sw = new StringWriter();
		try(Logger l = new SimpleLogger(sw, true);
			PrintStream ps = l.toOutputStream(level, charset).toPrintStream(true);) {
			ps.print(msg);
		}
		assertEquals(level.getPrefixText() + msg, sw.toString().strip());
	}
}
