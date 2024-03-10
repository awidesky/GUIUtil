package io.github.awidesky.guiUtil;

import org.junit.jupiter.api.Test;

class DialogTest {

	@Test
	void test() {
		SwingDialogs.error("always on top", "This error dialog must be on top of every windows!", null, true);
		SwingDialogs.setAlwaysOnTop(false);
		SwingDialogs.error("not always on top", "This error dialog must NOT be on top of every windows!", null, true);
	}

}
