package com.ericsson.ctr.stream.simulator;

public abstract class StreamRaw {

    protected boolean debug;
    protected long    eventCount;
    protected boolean stopStreaming;
    protected int     simCount = 1;
    
	public void showHelp () {
		System.out.println("    [--stdebug[=true/false/1/0]] [--nostdebug]\n");
	}
	

	public boolean processArgs (String par, String val) { 
		
		if (par.equals ("--stdebug")) {
			this.debug = Utilities.boolStr (val);
			return true;
		} else if (par.equals ("--nostdebug")) {
			this.debug = false;
			return true;
		}
		return false;
	}
	
	public void startMsg () { }
	
    public StreamRaw () {
	    	this.debug = false;
	    	this.stopStreaming = false;
	        this.eventCount = 0;
	    }
	    
    public abstract void startTraffic (String objId, ConnectorItem connItem);
	    
	protected final void close() {
		//System.out.println ("No of events: " + eventCount);
	}

	public void setSimCount (int simCount) { this.simCount = simCount; }
	public long getEventCount () { return eventCount; }
	public synchronized void setStopStreaming (boolean stopStreaming) { this.stopStreaming = stopStreaming; }
	    
}