package io.github.awidesky.guiUtil.simple;

import java.util.Collections;
import java.util.LinkedList;

public class SynchronizedStringLogger extends StringLogger {

	/**
	 * Creates a new synchronized Thread-safe StringLogger with {@code printLogLevel}
	 * set to {@code false}.
	 */
	public SynchronizedStringLogger() {
		super(Collections.synchronizedList(new LinkedList<>()));
	}

}
