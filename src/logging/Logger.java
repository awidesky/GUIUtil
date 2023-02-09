package logging;

import java.text.DateFormat;


/**
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
	
	public void newLine();

	public void log(String data);
	public void log(Exception e);
	public void log(Object... objs);
	
	public void logVerbose(String data);
	public void setVerbose(boolean verbose);
	
}
