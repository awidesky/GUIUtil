package logging;

public interface Logger {

	public static final Logger nullLogger = new Logger() { //TODO : update
		@Override
		public void log(String data) {}
		@Override
		public void log(Exception e) {}
	};
	
	public void log(String data);
	public void log(Exception e);
	
}
