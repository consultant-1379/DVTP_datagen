package com.ericsson.ctr.stream.simulator.connector;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

public class LocalIpFile extends Connector {
	
	private String		host;
	private int			port;
	private String[]	bindAddrs;
	private int			bindAddrsIx;
	
	
	public void showHelp () {
		System.out.println(
				"Connector : LocalIpFile\n" +
				"    --host=<host> --port=<port> --bindfile=<filename>");
		super.showHelp ();
	}
	
	
	private String[] readFileVals (String fname) {
		byte[] buffer = null;
		String bufStr = "";
		
		try {
			buffer = new byte [(int) new File (fname).length ()];
			BufferedInputStream fs = new BufferedInputStream (new FileInputStream (fname));
			fs.read (buffer);
			bufStr = new String (buffer);
		} catch (IOException ex) {
			System.err.println ("Cannot open file: '" + fname + "'");
			System.exit (-1);
		}
		if (this.debug) { System.out.println ("D buff '" + bufStr + "'"); }
	    return bufStr.split ("[\n\r\t ]+");
	}
	
	
	public boolean processArgs (String par, String val) {
		
		if (par.equals("--host")) {
			this.host = val;
			return true;
		} else if (par.equals("--port")) {
			this.port = Integer.parseInt(val);
			return true;
		} else if (par.equals("--bindfile")) {
			this.bindAddrs = readFileVals (val);
		    return true;
		}
		return super.processArgs (par,val);
	}

	
	public ConnectorItem create () {
		Socket socket = null;
		OutputStream outputStream = null;
		boolean connected;
		
        try {
            InetAddress destAddr = InetAddress.getByName (this.host);

            SocketAddress destSockAddr = new InetSocketAddress (destAddr, this.port);
            if (this.debug) { System.out.println ("D bindaddr '" + this.bindAddrs [this.bindAddrsIx] + "'"); }
            SocketAddress bindSock = new InetSocketAddress (this.bindAddrs [this.bindAddrsIx], 0);
            
            socket = new Socket();
            socket.bind (bindSock);

            socket.connect(destSockAddr, 20000);
            outputStream = new DataOutputStream (socket.getOutputStream());
            
            if (this.debug) {
            	System.out.println ("D Connecting to ip '" + destAddr.toString () + "' port '" + this.port + "'");
            	System.out.println ("D bind address '" + this.bindAddrs [this.bindAddrsIx] + "'");
            }
            
            if (this.bindAddrsIx >= this.bindAddrs.length) {
            	this.bindAddrsIx = 0;
            } else {
            	this.bindAddrsIx++;
            }

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