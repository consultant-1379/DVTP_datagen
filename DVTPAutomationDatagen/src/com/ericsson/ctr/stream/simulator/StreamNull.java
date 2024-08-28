package com.ericsson.ctr.stream.simulator;

public class StreamNull extends StreamRaw {

	public void showHelp () {
		System.out.println(
				"Stream : (not loaded)\n");
	}
	public boolean processArgs (String par, String val) { return false; }
	
	public StreamNull () { }
	
	public void startTraffic (String objId, ConnectorItem connItem) { }

}
