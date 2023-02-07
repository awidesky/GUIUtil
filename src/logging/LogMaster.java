package logging;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


public class LogMaster extends Thread {

	private PrintWriter logTo;
	private	LinkedBlockingQueue<Consumer<PrintWriter>> loggerQueue = new LinkedBlockingQueue<>();
	private LinkedList<LogMaster.Logger> children = new LinkedList<>();
	
	public volatile boolean isStop = false;
	private boolean verbose;
	
	
	public static final String version = "v1.2.0";
	
	public LogMaster() { 
		this(System.out, true, Charset.defaultCharset());
	}
	
	public LogMaster(OutputStream os) { 
		this(os, true, Charset.defaultCharset());
	}
	
	public LogMaster(OutputStream os, Charset cs) {
		this(os, true, cs);
	}
	
	public LogMaster(OutputStream os, boolean autoFlush) {
		this(os, autoFlush, Charset.defaultCharset());
	}
	
	public LogMaster(OutputStream os, boolean autoFlush, Charset cs) {
		logTo = new PrintWriter(os, autoFlush, cs);
	}
	
	public LogMaster.Logger getLogger() {
		LogMaster.Logger newLogger = this.new Logger(verbose);
		children.add(newLogger);
		return newLogger;
	}
	
	@Override
	public void run() {

		logTo.println("LoggerThread " + version + " started at [" + new SimpleDateFormat("yyyy/MM/dd-kk:mm:ss").format(new Date()) + "]");
		
		while (true) {

			if (loggerQueue.isEmpty() && isStop) {
				break;	
			}

			try {
				loggerQueue.take().accept(logTo);
			} catch (InterruptedException e) {
				logTo.println("LoggerThread Interrupted! : " + e.getMessage());
				logTo.println("Closing logger..");
				break;
			}
		}
		
		logTo.close();

	}
	
	
	public void setVerboseAllChildren(boolean verbose) {
		this.verbose = verbose;
		children.parallelStream().forEach(l -> l.verbose = verbose);
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
		
		this.interrupt();
		logTo.close();
		
	}
	
	public class Logger {

		private DateFormat datePrefix = null;
		private String prefix = null;
		
		private boolean verbose;
		
		public Logger(boolean verbose) {
			this.verbose = verbose;
		}
		
		public void setDatePrefix(DateFormat datePrefix) {
			this.datePrefix = datePrefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		public void newLine() {
			try {
				loggerQueue.put((logTo) -> {
					logTo.println();
				});
			} catch (InterruptedException e) {
				if (!isStop) log(e);
			}
		}

		public void log(String data) {
			try {
				loggerQueue.put(getLogTask(data));
			} catch (InterruptedException e) {
				if (!isStop) log(e);
			}
		}

		public void log(Exception e) {
			try {
				loggerQueue.put(getLogTask(e));
			} catch (InterruptedException e1) {
				if (!isStop) log(e1);
			}
		}

		public void log(Object... objs) {
			try {
				loggerQueue.put((logTo) -> {
					for(Object o : objs) getLogTask(o).accept(logTo);
				});
			} catch (InterruptedException e) {
				if(!isStop) log(e);
			}
		}
		
		
		public boolean logNow(String data) {
			return loggerQueue.offer(getLogTask(data));
		}
		
		public boolean logNow(Exception e) {
			return loggerQueue.offer(getLogTask(e));
		}
		
		public boolean logNow(Object... objs) {
			return loggerQueue.offer((logTo) -> {
				for(Object o : objs) getLogTask(o).accept(logTo);
			});
		}
		
		public void logVerbose(String data) {
			if(verbose) log(data);
		}
		
		public void setVerbose(boolean verbose) {
			this.verbose = verbose;
		}
		
		
		private Consumer<PrintWriter> getLogTask(String data) {
			return (logTo) -> {
				printPrefix(logTo);
				logTo.println(data.replaceAll("\\R", System.lineSeparator()));
			};
		}
		
		private Consumer<PrintWriter> getLogTask(Exception e) {
			return (logTo) -> {
				printPrefix(logTo);
				e.printStackTrace(logTo);
			};
		}
		
		private Consumer<PrintWriter> getLogTask(Object obj) {
			return getLogTask(obj.toString());
		}
		
		private void printPrefix(PrintWriter logTo) {
			if(datePrefix != null) logTo.print("[" + datePrefix.format(new Date()) + "] ");
			if(prefix != null) logTo.print(prefix);
		}
		
		

	}
}
