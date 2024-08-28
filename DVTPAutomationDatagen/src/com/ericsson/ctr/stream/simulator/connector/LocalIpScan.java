package com.ericsson.ctr.stream.simulator.connector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.ctr.stream.simulator.Connector;
import com.ericsson.ctr.stream.simulator.ConnectorItem;
import com.ericsson.ctr.stream.simulator.CtumStream;
import com.ericsson.ctr.stream.simulator.Utilities;

public class LocalIpScan extends Connector {

	private String		host;
	private int			port;
	private boolean		ipv4p = true;
	private boolean		ipv6p = true;
    private List<InetAddress> bindAddrs = new ArrayList<InetAddress>();
	private int			bindAddrsIx;

    
	public void showHelp () {
		System.out.println(
				"Connector : LocalIpScan\n" +
				"    --host=<host> --port=<port> [--ipv4=true/false/1/0] [--ipv6=true/false/1/0]");
		super.showHelp ();
	}
	
	
	public boolean processArgs (String par, String val) {
		
		if (par.equals("--host")) {
			this.host = val;
			return true;
		} else if (par.equals("--port")) {
			this.port = Integer.parseInt(val);
			return true;
		} else if (par.equals("--ipv4")) {
			this.ipv4p = Utilities.boolStr (val);
			if (!this.ipv4p) { this.ipv6p = true; }
			return true;
		} else if (par.equals("--noipv4")) {
			this.ipv4p = false;
			this.ipv6p = true;
			return true;			
		} else if (par.equals("--ipv6")) {
			this.ipv6p = Utilities.boolStr (val);
			if (!this.ipv6p) { this.ipv4p = true; }
			return true;
		} else if (par.equals("--noipv6")) {
			this.ipv6p = false;
			this.ipv4p = true;
			return true;			
		}
		return super.processArgs (par,val);
	}


	
	public void scanIf () {
		
		this.bindAddrs = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> nics;
		
		try {
			nics = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException ex) {
			nics = null;
		}
        
		while (nics.hasMoreElements()) {
            NetworkInterface nic = nics.nextElement();
            List<InterfaceAddress> iAddresses = nic.getInterfaceAddresses();
            if (this.debug) { System.out.println ("D nic '" + nic.getName() + "'"); }
            for (InterfaceAddress ia : iAddresses) {
               InetAddress addr = ia.getAddress();
               if (addr instanceof Inet4Address) {
                   if (!ipv4p) {
                	   if (this.debug) { System.out.println ("D skip addr = '" + addr + "', IPV4"); }
                	   continue;
                   }
                   if (addr.getHostAddress().equals ("127.0.0.1")) {
                	   if (this.debug) { System.out.println ("D skip addr = '" + addr + "', loopback"); }
                       continue;
                   }
                   if (addr.getHostAddress().equals ("0.0.0.0")) {
                	   if (this.debug) { System.out.println ("D skip addr = '" + addr + "', invalid address"); }
                       continue;
                   }
//				   if (addr.getHostAddress().startsWith ("192.168" /* ".57" */)) {
//					   if (this.debug) { System.out.println ("D skip addr = '" + addr + "', outside range"); }
//                	   continue;                	 
//				   }
               }
               if (addr instanceof Inet6Address) {
            	   if (!ipv6p) {
            		   if (this.debug) { System.out.println ("D skip addr = '" + addr + "', IPV6"); }
            		   continue;
            	   }
                   if (addr.getHostAddress().equals ("0:0:0:0:0:0:0:1")) {
                	   if (this.debug) { System.out.println ("D skip addr = '" + addr + "', loopback"); }
                       continue;
                   }
               }
               if (this.debug) { System.out.println ("D addr = '" + addr + "' added"); }
               this.bindAddrs.add(addr);
            }
        }
	}
	
	
	private boolean ifScanned;
	
	
	public ConnectorItem create () {
		Socket socket = null;
		OutputStream outputStream = null;
		boolean connected;
		
		if (!ifScanned) {
			scanIf ();
			ifScanned = true;
		}
        try {
            InetAddress destAddr = InetAddress.getByName (this.host);

            SocketAddress destSockAddr = new InetSocketAddress (destAddr, this.port);
            SocketAddress bindSock = new InetSocketAddress (this.bindAddrs.get (this.bindAddrsIx), 0);
            
            socket = new Socket();
            socket.bind (bindSock);

            socket.connect(destSockAddr, 20000);
            outputStream = new DataOutputStream (socket.getOutputStream());
            
            if (this.debug) {
            	System.out.println ("D Connecting to ip '" + destAddr.toString () + "' port '" + this.port + "'");
            	System.out.println ("D bind address '" + this.bindAddrs.get (this.bindAddrsIx).toString() + "'");
            }
            
            if (this.bindAddrsIx >= this.bindAddrs.size()) {
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