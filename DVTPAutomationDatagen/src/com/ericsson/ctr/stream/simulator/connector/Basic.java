
package com.ericsson.ctr.stream.simulator.connector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.ctr.stream.simulator.Connector;
import com.ericsson.ctr.stream.simulator.ConnectorItem;
import com.ericsson.ctr.stream.simulator.CtumStream;

public class Basic extends Connector {
	
	private String		host;
	private int			port;
	
	
	public void showHelp () {
		System.out.println(
				"Connector : Basic\n" +
				"    --host=<host> --port=<port>");
		super.showHelp ();
	}
	
	
	public boolean processArgs (String par, String val) {
		
		if (par.equals("--host")) {
			this.host = val;
			if (this.debug) { System.out.println ("D host:'" + this.host + "'"); }
			return true;
		} else if (par.equals("--port")) {
			this.port = Integer.parseInt(val);
			if (this.debug) { System.out.println ("D port:'" + this.port + "'"); }
			return true;
		}
		return super.processArgs (par,val);
	}

	
	public ConnectorItem create () {
		Socket socket = null;
		OutputStream outputStream = null;
		boolean connected;
		
        try {
            InetAddress destAddr = InetAddress.getByName(this.host);
            if (this.debug) { System.out.println ("D name  '" + this.host + "' to '" + destAddr.toString () + "'"); }
            SocketAddress destSockAddr = new InetSocketAddress (destAddr, this.port);
            socket = new Socket();
            
            socket.connect (destSockAddr, 20000);
            outputStream = new DataOutputStream (socket.getOutputStream());
            if (this.debug) { System.out.println ("D Connecting to ip '" + destAddr.toString () + "' port '" + this.port + "'"); }
            connected = true;
        } catch (UnknownHostException ex) {
        	System.err.println ("Unknown host when connecting to " + this.host + ":" + this.port);
            Logger.getLogger (CtumStream.class.getName ()).log(Level.SEVERE, null, ex);
            connected = false;
            System.exit (-1);
        } catch (ConnectException ex) {
        	System.err.println ("Failure connecting to " + this.host + ":" + this.port);
            Logger.getLogger (CtumStream.class.getName ()).log(Level.SEVERE, null, ex);
            connected = false;
            System.exit (-1);
        } catch (SocketTimeoutException ex) {
        	System.err.println ("Timeout when connecting to " + this.host + ":" + this.port);
            connected = false;
            System.exit (-1);
        } catch (IOException ex) {
        	System.err.println ("IO exception when connecting to " + this.host + ":" + this.port);
            Logger.getLogger (CtumStream.class.getName ()).log(Level.SEVERE, null, ex);
            connected = false;
            System.exit (-1);
        }
		return new ConnectorItem (socket, outputStream, connected);
	}
}