package logging;

import java.text.DateFormat;


/**
 * An Onterface for a Logger object.
 * 
 * @author Eugene Hong
 * */
public interface Logger {

	/**
	 * Set date information prefix for this <code>Logger</code> instance.
	 * if argument is <code>null</code>, no date information prefix is appended,
	 * */
	public void setDatePrefix(DateFormat datePrefix);

	/**
	 * Set additional prefix for this <code>Logger</code> instance.
	 * if argument is <code>null</code>, no date information prefix is appended,
	 * */
	public void setPrefix(String prefix);
	
	/**
	 * Print a newLine character. 
	 * */
	public void newLine();

	/**
	 * Logs String <code>data</code>
	 * */
	public void log(String data);
	/**
	 * Logs an <code>Exception</code> 
	 * */
	public void log(Exception e);
	/**
	 * Logs an array of <code>Object</code>s
	 * */
	public void log(Object... objs);
	
	
	/**
	 * Set verbosity of this <code>Logger</code> object.
	 * */
	public void setVerbose(boolean verbose);
	/**
	 * Log in verbose mode.
	 * If <code>setVerbose</code> is <code>true</code>, argument <code>data</code> is logged, otherwise it doesn't 
	 * */
	public void logVerbose(String data);
}
