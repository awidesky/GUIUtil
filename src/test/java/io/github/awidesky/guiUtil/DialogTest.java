package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled /** ignore on default since it blocks during the build */
class DialogTest {

	@Test
	void alwaysOntopTest() {
		SwingDialogs.error("always on top", "This error dialog must be on top of every windows!", null, true);
		SwingDialogs.setAlwaysOnTop(false);
		SwingDialogs.error("not always on top", "This error dialog must NOT be on top of every windows!", null, true);
		assertTrue(SwingDialogs.confirm("Result?", "result as intended?"));
	}

	
	
	@Test
	void inputDialog() {
		String init = "Hello, World!";
		String str = SwingDialogs.input("Input test", "input something!", init);
		assertTrue(SwingDialogs.confirm("Result?", "Was initial value : \"" + init + "\"" + " and input value \"" + str + "\"" + "?"));
	}
	
	
}
