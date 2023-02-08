package test;

import java.util.ArrayList;

import logging.LoggerThread;

public class Test {

	public static void main(String[] args) {

		LoggerThread l = new LoggerThread(System.out);
		l.start();
		l.log(new ArrayList<String>(), " dfsdf", new Exception("dfasdfa"));
		l.log("ttttt");
		l.log(new Object());
		l.kill(1000);
		
	}

}
