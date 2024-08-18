package io.github.awidesky.guiUtil;

import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import io.github.awidesky.guiUtil.level.Level;

/**
 * Logs each line written to a pre-defined level. Can also be configured with a Marker. This class provides an interface
 * that follows the {@link java.io.OutputStream} methods in spirit, but doesn't require output to any external stream.
 * This class should <em>not</em> be used as a stream for an underlying logger unless it's being used as a bridge.
 * Otherwise, infinite loops may occur!
 * <p>
 * Heavily inspired by 
 * <a href="https://github.com/apache/logging-log4j2/blob/2.x/log4j-iostreams/src/main/java/org/apache/logging/log4j/io/LoggerOutputStream.java">log4j</a>
 */
public class LoggerOutputStream extends OutputStream {

    private static final int BUFFER_SIZE = 1024;
    private final Logger logger;
    private final Level level;
    private final InputStreamReader reader;
    private final char[] msgBuf = new char[BUFFER_SIZE];
    private final StringBuilder msg = new StringBuilder();
    private boolean closed;
    private boolean closeExternalLogger;
	private final Charset charset;

    private final ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);


    public LoggerOutputStream(Logger logger, Level level, Charset charset) {
    	this(logger, level, charset, true);
    }
    public LoggerOutputStream(Logger logger, Level level, Charset charset, boolean closeExternalLogger) {
        this.logger = logger;
        this.level = level == null ? logger.getLogLevel() : level;
        this.closed = false;
        this.closeExternalLogger = closeExternalLogger;
        this.charset = charset == null ? Charset.defaultCharset() : charset;
		this.reader = new InputStreamReader(new InputStream() {
			@Override
			public int read() {
				buf.flip();
				int result = -1;
				if (buf.limit() > 0) {
					result = buf.get() & 0xFF;
				}
				buf.compact();
				return result;
			}

			@Override
			public int read(final byte[] bytes, final int off, final int len) {
				buf.flip();
				int result = -1;
				if (buf.limit() > 0) {
					result = Math.min(len, buf.limit());
					buf.get(bytes, off, result);
				}
				buf.compact();
				return result;
			}
		}, this.charset);
	}

    @Override
    public void close() throws IOException {
        synchronized (msg) {
            closed = true;
            if (msg.length() > 0) log();
            if(closeExternalLogger) logger.close();
        }
    }

    private void extractMessages() throws IOException {
        if (closed) {
            return;
        }
        int read = reader.read(msgBuf);
        while (read > 0) {
            int off = 0;
            for (int pos = 0; pos < read; pos++) {
                switch (msgBuf[pos]) {
                case '\r':
                    msg.append(msgBuf, off, pos - off);
                    off = pos + 1;
                    break;
                case '\n':
                    msg.append(msgBuf, off, pos - off);
                    off = pos + 1;
                    log();
                    break;
                }
            }
            msg.append(msgBuf, off, read - off);
            read = reader.read(msgBuf);
        }
    }

    private void log() {
        logger.logInLevel(level, msg.toString());
        msg.setLength(0);
    }

	@Override
	public void flush() throws IOException {
		if (logger instanceof Flushable flushableLogger) {
        	flushableLogger.flush();
        }		
	}

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int curOff = off;
        int curLen = len;
		synchronized (msg) {
			while (curLen > buf.remaining()) {
				int remaining = buf.remaining();
				buf.put(b, curOff, remaining);
				curLen -= remaining;
				curOff += remaining;
				extractMessages();
			}
			buf.put(b, curOff, curLen);
			extractMessages();
		}
    }

    @Override
    public void write(int b) throws IOException {
		synchronized (msg) {
			buf.put((byte)(b & 0xFF));
			extractMessages();
		}
    }

    public void setCloseExternalLogger(boolean closeExternalLogger) {
		this.closeExternalLogger = closeExternalLogger;
	}

}

