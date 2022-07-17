package logging;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.concurrent.LinkedBlockingQueue;


public class LoggerThread extends Thread { //TODO : add prefix

	private PrintWriter logTo;
	private LinkedBlockingQueue<Runnable> loggerQueue = new LinkedBlockingQueue<>();
	
	private SimpleDateFormat datePrefix = null;
	private String prefix = null;
	
	public volatile boolean isStop = false;
	
	public LoggerThread(OutputStream os) { //TODO : default construct that use sysout
		this(os, true, Charset.defaultCharset());
	}
	
	public LoggerThread(OutputStream os, Charset cs) {
		this(os, true, cs);
	}
	
	public LoggerThread(OutputStream os, boolean autoFlush) {
		this(os, autoFlush, Charset.defaultCharset());
	}
	
	public LoggerThread(OutputStream os, boolean autoFlush, Charset cs) {
		logTo = new PrintWriter(os, autoFlush, cs);
	}
	
	@Override
	public void run() {

		while (true) {

			if (loggerQueue.isEmpty() && isStop) {
				break;
			}

			try {
				loggerQueue.take().run();
			} catch (InterruptedException e) {
				logTo.println("LoggerThread Interrupted! : " + e.getMessage());
				logTo.println("Closing logger..");
				break;
			}
		}
		
		logTo.close();

	}
	
	public void log(String data) {

		loggerQueue.offer(getLogTask(data));
		
	}
	
	public void log(Exception e) {
		
		loggerQueue.offer(getLogTask(e));
		
	}
	
	public void log(Object... objs) {
		loggerQueue.offer(() -> {
			for(Object o : objs) getLogTask(o).run();
		});
	}
	
	
	private Runnable getLogTask(String data) {
		return () -> {
			logTo.println(data.replaceAll("\\R", System.lineSeparator()));
		};
	}
	
	private Runnable getLogTask(Exception e) {
		return () -> {
			e.printStackTrace(logTo);
		};
	}
	
	private Runnable getLogTask(Object obj) {
		return () -> {
			logTo.println(obj.toString().replaceAll("\\R", System.lineSeparator()));
		};
	}
	

	
	
	/**
	 * Kill LoggerThread in <code>timeOut</code> ms.
	 * */
	public void kill(int timeOut) {
		
		isStop = true;
		
		try {
			this.join(timeOut);
		} catch (InterruptedException e) {
			logTo.println("Failed to join logger thread!");
			e.printStackTrace(logTo);
		}
		
		logTo.close();
		
	}
}
