package logging;

import java.text.DateFormat;


/**
 * An Abstract class for a Logger object.
 * 
 * @author Eugene Hong
 * */
public abstract class AbstractLogger {

	protected boolean verbose = false;
	protected DateFormat datePrefix = null;
	protected String prefix = null;
	
	/**
	 * Set date information prefix for this <code>Logger</code> instance.
	 * if argument is <code>null</code>, no date information prefix is appended,
	 * */
	public void setDatePrefix(DateFormat datePrefix) {
		this.datePrefix = datePrefix;
	}

	/**
	 * Set additional prefix for this <code>Logger</code> instance.
	 * if argument is <code>null</code>, no date information prefix is appended,
	 * */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Print a newLine character. 
	 * */
	public abstract void newLine();

	/**
	 * Logs String <code>data</code>
	 * */
	public abstract void log(String data);
	/**
	 * Logs an <code>Exception</code> 
	 * */
	public abstract void log(Exception e);
	/**
	 * Logs an array of <code>Object</code>s
	 * */
	public abstract void log(Object... objs);
	
	
	/**
	 * Set verbosity of this <code>Logger</code> object.
	 * */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	/**
	 * Log in verbose mode.
	 * If <code>this.verbose</code> is <code>true</code>, argument <code>data</code> is logged, otherwise it doesn't 
	 * */
	public void logVerbose(String data) {
		if(verbose) log(data);
	}
	
}
