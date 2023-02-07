package test;

import java.util.ArrayList;

import logging.LogMaster;

public class Test {

	public static void main(String[] args) {

		LogMaster l = new LogMaster(System.out);
		l.start();
		l.log(new ArrayList<String>(), " dfsdf", new Exception("dfasdfa"));
		l.log("ttttt");
		l.log(new Object());
		l.kill(1000);
		
	}

}
