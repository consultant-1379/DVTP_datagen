package com.ericsson.ctr.stream.simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectorItem {
	protected boolean connected;
	protected Socket socket;
	protected OutputStream outputStream;
	protected long counter;

	public ConnectorItem (Socket socket, OutputStream outputStream, boolean connected) {
		synchronized (this) {
			this.socket       = socket;
			this.outputStream = outputStream;
			this.connected    = connected;
			this.counter      = 0;
		}
	}
	
	public void put (int outData) {
        try {
        	this.outputStream.write (outData);
        	this.outputStream.flush ();
        } catch (IOException ex) {
            Logger.getLogger(StreamRaw.class.getName()).log(Level.SEVERE, null, ex);
        }
        synchronized (this) {
        	this.counter++;
        }
	}
	
	public void put (byte[] outData) {
        try {
        	this.outputStream.write (outData);
        	this.outputStream.flush ();
        } catch (IOException ex) {
        	
            Logger.getLogger(StreamRaw.class.getName()).log(Level.SEVERE, null, ex);
        }		
        synchronized (this) {
        	this.counter++;
        }
	}

	public void close () {
        try {
        	synchronized (this) {
        		this.outputStream.flush();
        		this.outputStream.close();
        		this.socket.close();
        		this.connected = false;
        	}
        } catch (IOException ex) {
            Logger.getLogger(StreamRaw.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	
	public boolean      getConnected ()       { return this.connected; }
	public Socket       getSocket ()          { return this.socket; }
	public OutputStream getOutputStream ()    { return this.outputStream; }
	public long         getCounter ()         { synchronized (this) {return this.counter; } }
}
