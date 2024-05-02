package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DialogTest {

	@Test
	void alwaysOntopTest() {
		SwingDialogs.setAlwaysOnTop(true);
		SwingDialogs.error("always on top", "This error dialog must be on top of every windows!", null, true);
		SwingDialogs.setAlwaysOnTop(false);
		SwingDialogs.error("not always on top", "This error dialog must NOT be on top of every windows!", null, true);
		assertTrue(SwingDialogs.confirm("alwaysOntopTest", "result as intended?"));
	}

	
	@Test
	void inputDialogTest() {
		String init = "Hello, World!";
		String str = SwingDialogs.input("Title", "Prompt", init);
		assertTrue(SwingDialogs.confirm("Result?", "Was initial value : \"" + init + "\"" + " and input value \"" + str + "\"" + "?"));
		str = String.valueOf(SwingDialogs.inputPassword("Title", "Prompt"));
		assertTrue(SwingDialogs.confirm("inputDialogTest", "Was input value \"" + str + "\"" + "?"));
	}
	
	@Test
	void waitTillClosedTest() {
		SwingDialogs.information("waitTillClosed", "Content", true);
		SwingDialogs.warning("waitTillClosed", "Content", new Exception("Example Exception"), true);
		SwingDialogs.error("waitTillClosed", "Content", new Exception("Example Exception"), true);
		assertTrue(SwingDialogs.confirm("waitTillClosedTest", "Was waitTillClosed \"" + true + "\"" + "?"));
		
		/*
		SwingDialogs.information("Title", "Content", false);
		SwingDialogs.warning("Title", "Content", new Exception("Example Exception"), false);
		SwingDialogs.error("Title", "Content", new Exception("Example Exception"), false);
		assertTrue(SwingDialogs.confirm("Result?", "Was waitTillClosed \"" + false + "\"" + "?"));
		*/
	}
	
	
}
