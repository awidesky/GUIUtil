package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.formatter.NullLogFormatter;
import io.github.awidesky.guiUtil.formatter.SimpleLogFormatter;
import io.github.awidesky.guiUtil.level.Level;
import io.github.awidesky.guiUtil.simple.ConsoleLogger;
import io.github.awidesky.guiUtil.simple.StringLogger;

class PrefixTest {

	private final String pattern = "[%l] [%t] [%d{yyyy-MM-dd kk:mm:ss}] [%p] %m";
	private final String prefix = "prefix";
	private final String additionalPrefix = " + additional";
	private final String additionalFormatter = "%%%m%%";

	@Test
	void test() {
		System.out.println("\n==========================PrefixTest==========================");
		try(ConsoleLogger logger = new ConsoleLogger();) {
			logger.setLogFormatter(new SimpleLogFormatter(pattern));
			logger.setPrefix(prefix);
			
			System.out.printf("Pattern : \"%s\"\n", pattern);
			System.out.printf("Prefix  : \"%s\"\n", prefix);
			
			logger.info("Test logging");
			
			System.out.println();
			System.out.printf("Additional prefix  : \"%s\"\n", additionalPrefix);
			Logger child1 = logger.withMorePrefix(additionalPrefix , false);
			child1.info("Test logging");
			
			System.out.println();
			System.out.printf("Additional formatter  : \"%s\"\n", additionalFormatter);
			Logger child2 = logger.getChildlogger(new SimpleLogFormatter(additionalFormatter) , false);
			child2.info("Test logging");
		}
		System.out.println("==========================PrefixTest==========================");
	}

	private final String sampleText = "Hello, World!";
	@Test
	void nullFormatterTest() {
		StringLogger s = new StringLogger();
		s.setLogLevel(Level.TRACE);
		s.setLogFormatter(NullLogFormatter.instance());
		NullLogFormatter other = NullLogFormatter.instance();
		other.setPattern("other pattern");
		for(Level l : Level.values()) {
			s.logInLevel(l, sampleText);
			assertEquals(sampleText, s.getString());
			assertEquals(sampleText, other.format(l, "pref", sampleText));
		}
		assertEquals(null, other.getPattern());
		assertTrue(other == s.getLogFormatter());
		
		s.close();
	}
}
