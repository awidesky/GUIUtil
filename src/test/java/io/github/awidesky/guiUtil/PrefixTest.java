package io.github.awidesky.guiUtil;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.simple.ConsoleLogger;

class PrefixTest {

	private String dateFormat = " [yyyy-MM-dd kk:mm:ss] ";
	private String prefix = " [additional prefix] ";

	@Test
	void test() {
		System.out.println("\n==========================PrefixTest==========================");
		try(ConsoleLogger logger = new ConsoleLogger();) {
			logger.setPrintLogLevel(true);
			logger.setDatePrefix(new SimpleDateFormat(dateFormat ));
			logger.setPrintThreadName(true);
			logger.setPrefix(prefix);
			
			System.out.printf("Prefix : \"%s\"\n", prefix);
			System.out.printf("Date format : \"%s\"\n", dateFormat);
			System.out.println();
			
			logger.info("Test logging");
		}
		System.out.println("==========================PrefixTest==========================");
	}

}
