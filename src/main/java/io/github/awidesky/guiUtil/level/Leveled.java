package io.github.awidesky.guiUtil.level;

public interface Leveled {
	
	/**
	 * @return true if this instance is enabled for the INFO level.
	 */
	public boolean isInfoEnabled();
	
	/**
	 * @return true if this instance is enabled for the DEBUG level.
	 */
	public boolean isDebugEnabled();
	
	/**
	 * @return true if this instance is enabled for the TRACE level.
	 */
	public boolean isTraceEnabled();
	
	/**
	 * @return true if this instance is enabled for the WARNING level.
	 */
	public boolean isWarningEnabled();
	
	/**
	 * @return true if this instance is enabled for the ERROR level.
	 */
	public boolean isErrorEnabled();
    
	/**
	 * @return true if this instance is enabled for the FATAL level.
	 */
	public boolean isFatalEnabled();

	/**
	 * Set log level of this instance.
	 * */
	public void setLogLevel(Level level);
	
	/**
	 * Get log level of this instance.
	 * */
	public Level getLogLevel();
	
}
