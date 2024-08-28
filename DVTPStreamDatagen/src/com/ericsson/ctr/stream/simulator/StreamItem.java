package com.ericsson.ctr.stream.simulator;

public class StreamItem extends Thread {
// public class StreamItem implements Runnable {
    private String objId;
    private ConnectorItem connItem;
    private StreamRaw stream;

	public StreamItem (String objId, ConnectorItem connItem, StreamRaw stream) {
     	this.objId = objId;
    	this.connItem = connItem;
    	this.stream = stream;
	}
	
	public String getObjId            () { return this.objId; }
	public ConnectorItem getConnector () { return this.connItem; }
	
	public void run() {
		
		stream.startTraffic (this.objId, this.connItem);
	}

}
