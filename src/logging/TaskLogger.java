package logging;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.function.Consumer;

public abstract class TaskLogger extends AbstractLogger {

	private DateFormat datePrefix = null;
	private String prefix = null;
	
	public volatile boolean isStop = false;
	
	public TaskLogger(boolean verbose) {
		this.verbose = verbose;
	}
	

	/**
	 * queue a logging task.
	 * implementation would queue <code>logTask</code> to a worker thread 
	 * */
	abstract void queueLogTask(Consumer<PrintWriter> logTask);
	/**
	 * try to run a logging task <i><b>right away</b></i>.
	 * @return <code>true</code> if succeed to run the <code>logTask</code> right away
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