/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.ericsson.cac.ecds.utility.streaming.PgwEventsStream;
import com.ericsson.cac.ecds.utility.streaming.SgwEventsStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimeZone;
// import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class CpgStream implements Runnable {

	public static final String HEADER = "Header";
	public static final String FOOTER = "Footer";
	
	public static volatile int MAX_MS_BEFORE_NEXT_EVENT = 50;
	public static volatile int MAX_MS_BETWEEN_ACTIVE_IDLE = 5 * 1000;
	public static volatile int MAX_MS_BETWEEN_IDLE_ACTIVE = 8 * 1000;
	public static volatile int MIN_MS_BETWEEN_ACTIVE_IDLE = 3 * 1000;
	public static volatile int MIN_MS_BETWEEN_IDLE_ACTIVE = 5 * 1000;
	public static volatile int SLEEP_TIMER = 50;
	public static volatile int MEASUREMENT_EVENTS_INTERVAL = 1280;
    
    private int mmegi = 32768;
    private int mmec = 12;
    private int mcc = 311;
    private int mnc = 480;
    
    private long enbs1apid;
    private long mmes1apid;
    private String gummei;
    private String plmnIdentity;
    
    private String imsi = "311480000095283";
    private String imeisv = "9900004550292400";
    
    protected boolean delayHeaderTransmission;
    private boolean noHeader = false;
    
    private SimpleDateFormat tFormYearMilli;
    private SimpleDateFormat tFormYearSecT;
    private SimpleDateFormat tFormHourMilli;
    
    private Random random;
    
    private String cpgId;
    
    private SgwEventsStream sgwStreamer = new SgwEventsStream();
    private PgwEventsStream pgwStreamer = new PgwEventsStream();
    private Calendar localStartTime;
    private SimpleDateFormat utcTimeFormatter;
    private Socket socket;
    private OutputStream os;
    
    private int destPort;
    
    protected boolean stopStreaming;
    
    public long eventCount;
    
    
    private void CpgStreamCommon () {
    	
    	tFormYearMilli = new SimpleDateFormat (Utilities.tFstrYearMilli);
    	tFormYearMilli.setTimeZone (Utilities.tzGmt);
    	tFormYearSecT  = new SimpleDateFormat (Utilities.tFstrYearSecT);
    	tFormYearSecT.setTimeZone  (Utilities.tzGmt);
    	tFormHourMilli = new SimpleDateFormat (Utilities.tFstrHourMilli);
    	tFormHourMilli.setTimeZone (Utilities.tzGmt);   	
    }

    public CpgStream(String cpgId, int destPort) {
        eventCount = 0;
        enbs1apid = 0;
        mmes1apid = 0;
        plmnIdentity = "" + mcc + "" + mnc;
        
        CpgStreamCommon ();
        gummei = Utilities.generateGummei(plmnIdentity, mmegi, mmec);

        localStartTime = Calendar.getInstance();
        utcTimeFormatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss:SSS");
        utcTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        random = new Random(localStartTime.getTimeInMillis());

        this.cpgId = cpgId;
        this.destPort = destPort;
        this.stopStreaming = false;
    }

    public CpgStream(String cpgId, String destIp, int destPort) throws SocketTimeoutException {
        this(cpgId, destPort);
        boolean connected = false;

        System.out.println ("init BEG");
        try {
            InetAddress destAddr = InetAddress.getByName(destIp);
            
            SocketAddress destSockAddr = new InetSocketAddress (destAddr, destPort);
            socket = new Socket();
            
            socket.connect (destSockAddr, 20000);
            os = new DataOutputStream (socket.getOutputStream());
            connected = true;
        } catch (UnknownHostException ex) {
        	System.err.println ("Unknown host when connecting to " + destIp + ":" + destPort);
            Logger.getLogger (CtumStream.class.getName ()).log(Level.SEVERE, null, ex);
            connected = false;
        } catch (SocketTimeoutException ex) {
        	System.err.println ("Timeout when connecting to " + destIp + ":" + destPort);
            throw ex;
        } catch (IOException ex) {
        	System.err.println ("IO exception when connecting to " + destIp + ":" + destPort);
            Logger.getLogger (CtumStream.class.getName ()).log(Level.SEVERE, null, ex);
            connected = false;
        }

        if (connected == false) {
            System.err.println("Unable to connect to " + destIp + ":" + destPort);
            System.exit(1);
        }
        System.out.println ("init END");
    }

    public CpgStream (String cpgId, InetAddress bindAddr, String destIp, int destPort) throws SocketTimeoutException {
        this(cpgId, destPort);
        boolean connected = false;

        System.out.println ("init BEG");
        try {
            InetAddress destAddr = InetAddress.getByName (destIp);

            SocketAddress destSockAddr = new InetSocketAddress (destAddr, destPort);
            SocketAddress bindSock = new InetSocketAddress (bindAddr, 0);

            socket = new Socket();
            socket.bind (bindSock);

            socket.connect(destSockAddr, 20000);
            os = new DataOutputStream (socket.getOutputStream());
            connected = true;
        } catch (UnknownHostException ex) {
        	System.err.println ("Unknown host when connecting to " + destIp + ":" + destPort);
            Logger.getLogger (CtumStream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        } catch (SocketTimeoutException ex) {
        	System.err.println ("Timeout when connecting to " + destIp + ":" + destPort);
            throw ex;
        } catch (IOException ex) {
        	System.err.println ("IO exception when connecting to " + destIp + ":" + destPort);
            Logger.getLogger (CtumStream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }

        if (connected == false) {
            System.err.println("Unable to connect to " + destIp + ":" + destPort);
            System.exit(1);
        }
        System.out.println ("init END");
    }

    public final void streamEvent(byte[] ba, String type) {
        eventCount++;
        try {
            os.write(ba);
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(CtumStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void close() {
        try {
            os.flush();
            os.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(CtumStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void updateUeTemporaryIds() {
        enbs1apid++;
        mmes1apid++;
    }


// - - - - - - - - - - - - - - - - - - - //
//  HEADER RECORD						 //
// - - - - - - - - - - - - - - - - - - - //
    private byte[] generateCpgHeader() {

        // RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,TIME_YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,TIME_ZONE,
        // CAUSE_OF_HEADER,NODE_ID
    	
        return (byte[]) sgwStreamer.processSeedString (
        	"0,4,1,4," + tFormYearSecT.format (System.currentTimeMillis ()) + ",0," + getCpgId(), "a").get(0);
    }


// - - - - - - - - - - - - - - - - - - - //
//  SESSION EVENTS						 //
// - - - - - - - - - - - - - - - - - - - //
    public byte[] generateSessionCreation (int callMode) {
//        int callMode = Network.CALL_MODE_INET;

        //RECORD_TYPE,EVENT_ID,EVENT_RESULT,TIME_HOUR,TIME_MINUTE,TIME_SECOND,TIME_MILLISECOND,DURATION,CAUSE_PROTOCOL,CAUSE_CODE,
        // SUB_CAUSE_CODE,EVENT_TRIGGER,ORIGINATING_NODE,DEFAULT_BEARER_ID,APN,PGW_ADDRESS_V4,PGW_ADDRESS_V6,PAA_ADDRESS_V4,
        // PAA_ADDRESS_V6,APN_AMBR_UL,APN_AMBR_DL,IMSI,IMEISV,MCC,MNC,TAC,ECI,BEARER_INFO,MME_OR_SGSN_V4,MME_OR_SGSN_V6
        
        return (byte[]) sgwStreamer.processSeedString (
        		"1,2,0," + tFormHourMilli.format (System.currentTimeMillis ()) +
        		",1,0,0,0,0,0," +
        		(callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
        		"," +
        		(callMode == Network.CALL_MODE_VOICE ? Network.getVoicePdn() : Network.getInetPdn()) +
        		",3232252937,,3232252935,,0,0," +
                imsi + "," + imeisv + "," + Network.mcc + "," + Network.mnc + ",102," + Network.eci + ",[" +
        		(callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
        		";0;8;0;0],3232252931, ,", "a").get(0);
    }

    public byte[] generateSessionDeletion (int callMode) {
//      int callMode = Network.CALL_MODE_INET;

      // RECORD_TYPE,EVENT_ID,EVENT_RESULT,TIME_HOUR,TIME_MINUTE,TIME_SECOND,TIME_MILLISECOND,
      // DURATION,CAUSE_PROTOCOL,CAUSE_CODE,SUB_CAUSE_CODE,EVENT_TRIGGER,ORIGINATING_NODE,
      // DEFAULT_BEARER_ID,APN,PGW_ADDRESS_V4,PGW_ADDRESS_V6,PAA_ADDRESS_V4,PAA_ADDRESS_V6,
      // APN_AMBR_UL,APN_AMBR_DL,IMSI,IMEISV,MCC,MNC,TAC,ECI,BEARER_INFO,MME_OR_SGSN_V4,MME_OR_SGSN_V6],,,,,,,

      return (byte[]) sgwStreamer.processSeedString (
      		"1,0,0," + tFormHourMilli.format (System.currentTimeMillis ()) +
      		",35,0,0,0,0,0," +
      		Network.getdefaultBearerIdbyCallMode(callMode) + "," +
      		(callMode == Network.CALL_MODE_VOICE ? Network.getVoicePdn() : Network.getInetPdn()) +
      		",3232252937,,3232252935,,0,0,"
              + imsi + "," + imeisv + "," + Network.mcc + "," + Network.mnc + ",102,"
              + Network.eci + ",[" +
              (callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
              ";0;0;0;0;;;;;;],3232252931, ,,,,,,,", "a").get(0);
  }

    
// - - - - - - - - - - - - - - - - - - - //
//  BEARER EVENTS						 //
// - - - - - - - - - - - - - - - - - - - //
    public byte[] generateBearerCreation (int callMode, int bearerId) {
//      int bearerId = 5;
//      int callMode = Network.CALL_MODE_INET;

      // RECORD_TYPE,EVENT_ID,EVENT_RESULT,TIME_HOUR,TIME_MINUTE,TIME_SECOND,TIME_MILLISECOND,
      // DURATION,CAUSE_PROTOCOL,CAUSE_CODE,SUB_CAUSE_CODE,EVENT_TRIGGER,ORIGINATING_NODE,DEFAULT_BEARER_ID,APN,
      // PGW_ADDRESS_V4,PGW_ADDRESS_V6,UE_ADDRESS_V4,UE_ADDRESS_V6,APN_AMBR_UL,APN_AMBR_DL,IMSI,IMEISV,MCC,MNC,TAC,
      // ECI,BEARER_INFO,MME_OR_SGSN_V4,MME_OR_SGSN_V6
      return (byte[]) sgwStreamer.processSeedString (
      		"1,3,0," + tFormHourMilli.format (System.currentTimeMillis ()) + ",0,0,0,0,3,0," +
      		(callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
      		"," +
      		(callMode == Network.CALL_MODE_VOICE ? Network.getVoicePdn() : Network.getInetPdn()) +
      		",3232252937,,3232252935,,0,0," + imsi + "," + imeisv + "," + Network.mcc + "," + Network.mnc + ",102," +
      		Network.eci + ",[" + bearerId + ";0;8;0;0],3232252931,   ,,,,,,,,,,,,,", "a").get(0);
  }

    public byte[] generateBearerModification (int callMode, String pdnName, List<Bearer> bearerList) {

        // RECORD_TYPE,EVENT_ID,EVENT_RESULT,TIME_HOUR,TIME_MINUTE,TIME_SECOND,TIME_MILLISECOND,
        // DURATION,CAUSE_PROTOCOL,CAUSE_CODE,SUB_CAUSE_CODE,EVENT_TRIGGER,ORIGINATING_NODE,DEFAULT_BEARER_ID,APN,
        // PGW_ADDRESS_V4,PGW_ADDRESS_V6,PAA_ADDRESS_V4,PAA_ADDRESS_V6,APN_AMBR_UL,APN_AMBR_DL,IMSI,IMEISV,MCC,MNC,
        // TAC,ECI,BEARER_INFO,MME_OR_SGSN_V4,MME_OR_SGSN_V6]

        return (byte[]) sgwStreamer.processSeedString (
        		"1,4,0," + tFormHourMilli.format (System.currentTimeMillis ()) + ",595,0,0,0,0,0," +
        		bearerList.get(0).getDefaultBearerId() + "," + pdnName +
        		",3232252937,,3232252935,,0,0," + imsi + "," + imeisv + ","
                + Network.mcc + "," + Network.mnc + ",102," + Network.eci + "," +
        		Bearer.stringList (bearerList) + 
        		",3232252931, ,", "a").get(0);

    }

    public byte[] generateBearerDeletion (int callMode, int bearerId) {
//        int bearerId = 5;
//        int callMode = Network.CALL_MODE_INET;

        // RECORD_TYPE,EVENT_ID,EVENT_RESULT,TIME_HOUR,TIME_MINUTE,TIME_SECOND,TIME_MILLISECOND,
        // DURATION,CAUSE_PROTOCOL,CAUSE_CODE,SUB_CAUSE_CODE,EVENT_TRIGGER,ORIGINATING_NODE,
        // DEFAULT_BEARER_ID,APN,PGW_ADDRESS_V4,PGW_ADDRESS_V6,PAA_ADDRESS_V4,PAA_ADDRESS_V6,
        // APN_AMBR_UL,APN_AMBR_DL,IMSI,IMEISV,MCC,MNC,TAC,ECI,
        // BEARER_INFO,MME_OR_SGSN_V4,MME_OR_SGSN_V6],,,,,,,
        
        return (byte[]) sgwStreamer.processSeedString (
        		"1,1,0," + tFormHourMilli.format (System.currentTimeMillis ()) +
        		",1,0,0,0,4,0," +
        		(callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
        		"," +
        		(callMode == Network.CALL_MODE_VOICE ? Network.getVoicePdn() : Network.getInetPdn()) +
        		",3232252937,,3232252935,,0,0," +
        		imsi + "," + imeisv + "," + Network.mcc + "," + Network.mnc + ",102," +
        		Network.eci + ",[" + bearerId +
        		";0;8;0;0;10;200;20;3000;1;2],3232252931, ,,,,,,,", "a").get(0);
    }


// - - - - - - - - - - - - - - - - - - - //
//  MAIN EVENT GENERATOR LOOP			 //
// - - - - - - - - - - - - - - - - - - - //
    @Override
    public void run() {
    	int bearerId = 5;
    	Bearer bear = new Bearer (bearerId, bearerId);
    	List<Bearer> bearList = new ArrayList<Bearer>();
    	bearList.add(bear);

        int callMode = Network.CALL_MODE_INET;

        System.out.println("Starting CPG streaming for cpg id = " + getCpgId());

        if (noHeader == false) {
            streamEvent(generateCpgHeader(), HEADER);
        }

        while (stopStreaming == false) {
        	Utilities.Sleep (EventGenerate.delayBetweenEvents);  streamEvent (generateBearerCreation     (callMode, bearerId),        "CPG");
//        	Utilities.Sleep (EventGenerate.delayBetweenEvents);  streamEvent (generateBearerModification (callMode, "xxx", bearList), "CPG");
        	Utilities.Sleep (EventGenerate.delayBetweenEvents);  streamEvent (generateBearerDeletion     (callMode, bearerId),        "CPG");
//        	Utilities.Sleep (EventGenerate.delayBetweenEvents);  streamEvent (generateSessionCreation    (callMode),                  "CPG");
        	Utilities.Sleep (EventGenerate.delayBetweenEvents);  streamEvent (generateSessionDeletion    (callMode),                  "CPG");
            updateUeTemporaryIds ();
            if (callMode == Network.CALL_MODE_INET) {
            	callMode = Network.CALL_MODE_VOICE;
            } else {
            	callMode = Network.CALL_MODE_INET;
            }
        }
        close();
    }

    public long getEventCount () { return eventCount;    }
    public boolean isDelayHeaderTransmission () { return delayHeaderTransmission;    }
    public void setDelayHeaderTransmission (boolean delayHeaderTransmission) { this.delayHeaderTransmission = delayHeaderTransmission; }
    public synchronized void setStopStreaming (boolean stopStreaming) { this.stopStreaming = stopStreaming; }
    public boolean isNoHeader () { return noHeader; }
    public void setNoHeader (boolean noHeader) { this.noHeader = noHeader; }
    public String getCpgId () { return cpgId; }
    public void setCpgId (String cpgId) { this.cpgId = cpgId; }
}
