package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.level.Level;

class LogTest {
	
	private static SimpleLogger debugLogger = new SimpleLogger(System.out);
	static {
		debugLogger.setPrefix("[JUnit test debug output]");
		debugLogger.setLogLevel(Level.DEBUG); // delete this line or set higher level to omit test logs
	}

	@Test
	void testLevel() {
		levelIterateTest(SimpleLoggerTestAggregate::new);
	}

	void levelIterateTest(Supplier<LoggerTestAggregate> logGenerator) {
		debugLogger.debug("Level Iterate Test for " + logGenerator.get().getLogger().getClass().getName());
		debugLogger.debug();
		
		Arrays.stream(Level.values())
			.forEach(eachLevel -> {
				LoggerTestAggregate logGobbler = logGenerator.get();
				Logger logger = logGobbler.getLogger();
				logger.setLogLevel(eachLevel);
				Map.of(
					Level.INFO, logger.isInfoEnabled(),
					Level.DEBUG, logger.isDebugEnabled(),
					Level.TRACE, logger.isTraceEnabled(),
					Level.WARNING, logger.isWarningEnabled(),
					Level.ERROR, logger.isErrorEnabled(),
					Level.FATAL, logger.isFatalEnabled()
				).entrySet().stream()
				.forEach(e -> {
					assertEquals(eachLevel.includes(e.getKey()), e.getValue().booleanValue(),
							() -> "level " + e.getKey() + " must be " + eachLevel.includes(e.getKey()) + ", since set level is : " + eachLevel);
					assertEquals(e.getKey().ordinal() <= eachLevel.ordinal(), e.getValue().booleanValue(),
							() -> "level " + e.getKey().ordinal() + "(" + e.getKey() + ") <= " + "level " + eachLevel.ordinal() + "(" + eachLevel
								+ ") must be true, since set level is : " + eachLevel);
				});
				
				Arrays.stream(Level.values()).forEach(le -> logger.logInLevel(le, le.name()));
				
				String output = logGobbler.getOutput();
				Stream<String> outputStream = output.lines().map(str -> str.split(" ")[1]);
				String nonmatch = outputStream.map(Level::valueOf).filter(l -> !eachLevel.includes(l)).map(Level::name).collect(Collectors.joining(", "));
				
				debugLogger.debug("Test for " + logger.toString());
				output.lines().forEach(debugLogger::debug);
				debugLogger.debug("========================================");
				debugLogger.debug();
				
				
				if(!"".equals(nonmatch)) {
					System.out.println("Logger \"" + logger.toString() + "\"'s level is " + logger.getLogLevel());
					System.out.println("========================================");
					System.out.println(output);
					System.out.println("========================================");
					System.out.println("\"" + nonmatch + "\" should not printed!");
					fail("\"" + nonmatch + "\" should not printed since logger level is " + logger.getLogLevel());;
				}
			});
	}

}


interface LoggerTestAggregate {
	public Logger getLogger();
	public String getOutput();
}

class SimpleLoggerTestAggregate implements LoggerTestAggregate {
	StringWriter sw = new StringWriter();
	
	@Override
	public Logger getLogger() {
		return new SimpleLogger(sw, true);
	}
	
	@Override
	public String getOutput() {
		return sw.toString();
	}
}
