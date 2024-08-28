package com.ericsson.ctr.stream.simulator;

public class ConnectorNull extends Connector {

	public void showHelp () {
		System.out.println(
				"Connector : (not loaded)\n");
		}
	public boolean processArgs (String par, String val) { return false; }
	
	public ConnectorNull () { }
	
	public ConnectorItem create() { return null; }

}
