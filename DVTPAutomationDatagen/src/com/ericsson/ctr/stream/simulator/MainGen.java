package com.ericsson.ctr.stream.simulator;

import java.io.*;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.playback.PlayBack;

public class MainGen {

	private static final String connectorPath	 = "com.ericsson.ctr.stream.simulator.connector.";
	private static final String streamPath   	 = "com.ericsson.ctr.stream.simulator.engine.";
	private static Connector 	connector        = new ConnectorNull ();
	private static StreamRaw 	stream           = new StreamNull ();
    private static int      	simCount         = 1;
    private static int      	logInterval      = 1;
    private static boolean 		precook			 =false;
	
	private static void setConnector (String connId) {
		try {
			connector = (Connector) Class.forName (connectorPath + connId) .newInstance ();
	    } catch (ClassNotFoundException x) {
			System.out.println ("ERR Connector '" + connId + "' not found");
			System.exit (-1);	
	    } catch (InstantiationException x) {
		    System.out.println ("ERR Connector '" + connId + "', error during instantiation");
	 	    System.exit (-1);
	    } catch (IllegalAccessException x) {
		    System.out.println ("ERR Connector '" + connId + "', access error");
		    System.exit (-1);	
	    }
	}
	
	private static void setStream (String streamId) {
		
		
		try {
			stream = (StreamRaw) Class.forName (streamPath + streamId) .newInstance ();
		} catch (ClassNotFoundException x) {
			System.out.println ("ERR Stream '" + streamId + "' not found");
			System.exit (-1);	
		} catch (InstantiationException x) {
			System.out.println ("ERR Stream '" + streamId + "', error during instantiation");
			System.exit (-1);
		} catch (IllegalAccessException x) {
			System.out.println ("ERR Stream '" + streamId + "', access error");
			System.exit (-1);	
		}
		
	}


    public static void displayUsage() {
		System.out.println(
                "Usage : java -jar CtrStreamSimulator [--connector=<connector>] [--stream=<stream>]\n" +
		        "    [--loginterval=<seconds>] [--simulators=<count>] [--help]\n");
        connector.showHelp ();
        stream.showHelp ();
        System.exit(1);
    }

    private static void processArguments(String[] args) {
    	
        for (String arg : args) {
            String parameter = null;
            String value = null;

            if (arg.indexOf("=") == -1) {
                parameter = arg;
            } else {
                StringTokenizer st = new StringTokenizer(arg, "=");
                parameter = st.nextToken();
                value = st.nextToken();
            }

            if (parameter.equals ("--help"))         { displayUsage ();                        continue; }
            if (parameter.equals ("--connector"))    { setConnector (value);                   continue; }
            if (parameter.equals ("--stream"))       { setStream (value);
            											Utilities.NODE_TYPE=value;               continue; }
            
            if (parameter.equals ("--simulators"))   { simCount    = Integer.parseInt (value); stream.setSimCount (simCount); continue; }
            if (parameter.equals ("--loginterval"))  { logInterval = Integer.parseInt (value); continue; }
            if (parameter.equals ("--precook")) 	 { precook=true; continue; }
            if (connector.processArgs (parameter, value)) { continue; }
            if (stream   .processArgs (parameter, value)) { continue; }
            
            System.err.println("Invalid argument : " + arg);
            displayUsage();
        }

    }
   
     






    
    private static void delay (int i) {
    	
        try {
            TimeUnit.MILLISECONDS.sleep (814);
        } catch (InterruptedException ex1) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    
    private static StreamItem[] streams;
    
    
    private static void traceIt () {
    	
//    	DecimalFormat counterForm = new DecimalFormat ("### ### ##0");
    	
    	while (true) {
    		StringBuilder sb = new StringBuilder (1024);
    		sb.append ("L ");
    		for (StreamItem si : streams) {
    			sb.append (" ");
    			sb.append (si.getObjId ());
    			sb.append (" ");
    			sb.append (String.format ("%9d", si.getConnector ().getCounter ()));
    		}
            System.out.println (sb.toString());
            try {
                TimeUnit.SECONDS.sleep (logInterval);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
    	}
    }
    
    
    public static void main(String[] args) {
        processArguments(args);
        
        
        streams = new StreamItem [simCount];
        stream.startMsg ();
        int i;
        for (i = 0; i < simCount; i++) {
        	
				streams [i] = new StreamItem (Utilities.itemId (i), connector.create (), stream);
	        	streams [i] .start ();
	        	delay (i);
			
        }
        //traceIt ();
    
        
    }
    
    
    
    
    
}