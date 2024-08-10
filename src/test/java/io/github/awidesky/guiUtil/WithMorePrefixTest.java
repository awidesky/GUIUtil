package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

class WithMorePrefixTest {

	@Test
	void withMorePrefixTest() {
		StringLogger s1 = new StringLogger();
		s1.info("Hello, World!");
		Logger s2 = s1.withMorePrefix("[additional prefix]", false);
		s2.info("Hello, World!");
		s1.close();
		String[] arr = s1.getString().split("\n");
		assertEquals("[additional prefix]" + arr[0], arr[1]);
	}

	@Test
	void parentCloseTest() throws IOException {
		assertThrows(IOException.class, () -> closeChild(true));
		assertDoesNotThrow(() -> closeChild(false));
	}
	
	private void closeChild(boolean closeChildIfParentClosed) throws IOException {
		OutputStream os = OutputStream.nullOutputStream();
		SimpleLogger parent = new SimpleLogger(os);
		try {
			parent.withMorePrefix("[additional prefix]", closeChildIfParentClosed).close();
		} catch(IOException e) { parent.close(); throw new RuntimeException(e); }
		
		/*
		 * This should throw new IOException("Stream closed")
		 * when closeChildIfParentClosed is true. 
		 */
		os.write(0);
		parent.close();
	}
}
