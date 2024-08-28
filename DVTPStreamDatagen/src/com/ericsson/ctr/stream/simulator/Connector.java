package com.ericsson.ctr.stream.simulator;


public abstract class Connector {

    protected boolean debug;
	
	public void showHelp () {
		System.out.println("    [--codebug[=true/false/1/0]] [--nocodebug]\n");
	}
	

	public boolean processArgs (String par, String val) { 
		
		if (par.equals ("--codebug")) {
			this.debug = Utilities.boolStr (val);
			return true;
		} else if (par.equals ("--nocodebug")) {
			this.debug = false;
			return true;
		}
		return false;
	}
	
	
	public abstract ConnectorItem create ();
	
}