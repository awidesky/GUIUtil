package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.simple.SimpleLogger;

class ChildLoggerTest {

	@Test
	void parentCloseTest() throws IOException {
		assertThrows(IOException.class, () -> closeChild(true));
		assertDoesNotThrow(() -> closeChild(false));
	}
	
	private void closeChild(boolean closeChildIfParentClosed) throws IOException {
		OutputStream os = OutputStream.nullOutputStream();
		SimpleLogger parent = new SimpleLogger(os);
		try {
			parent.withMorePrefix("[additional formatter]", closeChildIfParentClosed).close();
		} catch(IOException e) { parent.close(); throw new RuntimeException(e); }
		
		/*
		 * This should throw new IOException("Stream closed")
		 * when closeChildIfParentClosed is true. 
		 */
		os.write(0);
		parent.close();
	}
}
