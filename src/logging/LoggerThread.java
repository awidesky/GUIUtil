package logging;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;


public class LoggerThread extends Thread {

	private PrintWriter logTo;
	private LinkedBlockingQueue<Runnable> loggerQueue = new LinkedBlockingQueue<>();
	
	public volatile boolean isStop = false;
	
	public LoggerThread(OutputStream os) {
		logTo = new PrintWriter(os);
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

		loggerQueue.offer(() -> {
			logTo.println(data.replaceAll("\\R", System.lineSeparator()));
		});
		
	}
	
	public void log(Exception e) {
		
		loggerQueue.offer(() -> {
			e.printStackTrace(logTo);
		});
		
	}
}
