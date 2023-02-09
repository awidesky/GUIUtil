package logging;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.function.Consumer;

public abstract class TaskLogger implements Logger {

	private DateFormat datePrefix = null;
	private String prefix = null;
	
	public volatile boolean isStop = false;
	private boolean verbose;
	
	public TaskLogger(boolean verbose) {
		this.verbose = verbose;
	}
	
	/**
	 * Set date information prefix for this <code>Logger</code> instance.
	 * if argument is <code>null</code>, no date information prefix is appended,
	 * */
	@Override
	public void setDatePrefix(DateFormat datePrefix) {
		this.datePrefix = datePrefix;
	}

	/**
	 * Set additional prefix for this <code>Logger</code> instance.
	 * if argument is <code>null</code>, no date information prefix is appended,
	 * */
	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * queue a logging task.
	 * implementation would be like queuing <code>logTask</code> to another 
	 * */
	abstract void queueLogTask(Consumer<PrintWriter> logTask);
	/**
	 * 
	 * */
	abstract boolean runLogTask(Consumer<PrintWriter> logTask);
	
	@Override
	public void newLine() {
		queueLogTask((logTo) -> {
			logTo.println();
		});
	}

	@Override
	public void log(String data) {
		queueLogTask(getLogTask(data));
	}

	@Override
	public void log(Exception e) {
		queueLogTask(getLogTask(e));
	}

	@Override
	public void log(Object... objs) {
		queueLogTask((logTo) -> {
			for(Object o : objs) getLogTask(o).accept(logTo);
		});
	}
	
	
	public boolean logNow(String data) {
		return runLogTask(getLogTask(data));
	}
	
	public boolean logNow(Exception e) {
		return runLogTask(getLogTask(e));
	}
	
	public boolean logNow(Object... objs) {
		return runLogTask((logTo) -> {
			for(Object o : objs) getLogTask(o).accept(logTo);
		});
	}
	
	@Override
	public void logVerbose(String data) {
		if(verbose) log(data);
	}
	
	@Override
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