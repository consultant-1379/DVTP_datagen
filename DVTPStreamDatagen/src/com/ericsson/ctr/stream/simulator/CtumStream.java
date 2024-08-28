/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import com.ericsson.cac.ecds.utility.streaming.CtumEventsStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class CtumStream implements Runnable {

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

    private String macroENodebId;
    private String homeENodebId;
    
    private Random random;

    private String mmeId;
    
    
    private CtumEventsStream ctumStreamer;
    private Calendar localStartTime;
    private SimpleDateFormat utcTimeFormatter;
    private Socket socket;
    private OutputStream os;
    
    private int port;
    
    protected boolean stopStreaming;
    
    public long eventCount;
    
    private String imsi = "311480000095283";
    private String imeisv = "9900004550292400";
    
    protected boolean delayHeaderTransmission;
    private boolean noHeader = false;

    public CtumStream(String mmeId, int port) {
        eventCount = 0;
        enbs1apid = 0;
        mmes1apid = 0;
        plmnIdentity = "" + mcc + "" + mnc;
        gummei = Utilities.generateGummei(plmnIdentity, mmegi, mmec);

        localStartTime = Calendar.getInstance();
        utcTimeFormatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss:SSS");
        utcTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        ctumStreamer = new CtumEventsStream();

        random = new Random(localStartTime.getTimeInMillis());

        this.mmeId = mmeId;
        macroENodebId = ""+mmeId;
        homeENodebId = ""+mmeId;
        

        this.port = port;
        this.stopStreaming = false;

    }

    public CtumStream(String mmeId, String destIp, int port) throws SocketTimeoutException {
        this(mmeId, port);
        boolean connected = false;

        try {

            InetAddress addr = InetAddress.getByName(destIp);
            SocketAddress sockaddr = new InetSocketAddress(addr, port);
            socket = new Socket();
            socket.connect(sockaddr, 2000);
            os = new DataOutputStream(socket.getOutputStream());
            connected = true;

        } catch (UnknownHostException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        } catch (SocketTimeoutException ex) {
            throw ex;
        } catch (IOException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }

        if (connected == false) {
            System.err.println("Unable to connect to " + destIp + ":" + port);
            System.exit(1);
        }
    }

    public CtumStream(String mmeId, InetAddress bindaddr, String destIp, int port) throws SocketTimeoutException {
        this(mmeId, port);
        boolean connected = false;

        try {

            InetAddress addr = InetAddress.getByName(destIp);

            SocketAddress sockaddr = new InetSocketAddress(addr, port);
            SocketAddress bindaddrsock = new InetSocketAddress(bindaddr, 0);

            socket = new Socket();
            socket.bind(bindaddrsock);

            socket.connect(sockaddr, 2000);
            os = new DataOutputStream(socket.getOutputStream());
            connected = true;

        } catch (UnknownHostException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        } catch (SocketTimeoutException ex) {
            throw ex;
        } catch (IOException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }

        if (connected == false) {
            System.err.println("Unable to connect to " + destIp + ":" + port);
            System.exit(1);
        }
    }

    public final void streamEvent(byte[] ba, String type) {
        eventCount++;
        try {
            os.write(ba);
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void close() {
        try {
            os.flush();
            os.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param stopStreaming the stopStreaming to set
     */
    public synchronized void setStopStreaming(boolean stopStreaming) {
        this.stopStreaming = stopStreaming;
    }

    private Calendar getTimeinUTC() {
        Calendar utcTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date utcTimeDate = null;
        try {
            utcTimeDate = utcTimeFormatter.parse(utcTimeFormatter.format(localStartTime.getTime()));
            utcTime.setTime(utcTimeDate);
        } catch (ParseException ex) {
            Logger.getLogger(CpgStream.class.getName()).log(Level.SEVERE, null, ex);
            //if failed for some reason return start of UNIX time
            utcTime.setTimeInMillis(0);
        }
        return utcTime;
    }

//    private String generateGummei() {
//        String tmpgummei = "";
//        if (plmnIdentity != null && plmnIdentity.length() == 6) {
//            tmpgummei = tmpgummei + plmnIdentity.charAt(1);
//            tmpgummei = tmpgummei + plmnIdentity.charAt(0);
//            tmpgummei = tmpgummei + plmnIdentity.charAt(3);
//            tmpgummei = tmpgummei + plmnIdentity.charAt(2);
//            tmpgummei = tmpgummei + plmnIdentity.charAt(5);
//            tmpgummei = tmpgummei + plmnIdentity.charAt(4);
//            tmpgummei = tmpgummei + String.format("%x", mmegi);
//            tmpgummei = tmpgummei + String.format("%02x", mmec);
//        }
//
//        return tmpgummei;
//    }

    private void updateTime() {
        localStartTime.setTimeInMillis(System.currentTimeMillis());
    }

    protected void updateUeTemporaryIds() {
        enbs1apid++;
        mmes1apid++;
    }


    public long getEventCount() {
        return eventCount;
    }

    /**
     * @return the delayHeaderTransmission
     */
    public boolean isDelayHeaderTransmission() {
        return delayHeaderTransmission;
    }

    /**
     * @param delayHeaderTransmission the delayHeaderTransmission to set
     */
    public void setDelayHeaderTransmission(boolean delayHeaderTransmission) {
        this.delayHeaderTransmission = delayHeaderTransmission;
    }

    public byte[] generateCtumHeader() {
        int ctumHour = localStartTime.get(Calendar.HOUR_OF_DAY);
        int ctumMinute = localStartTime.get(Calendar.MINUTE);
        int ctumSecond = localStartTime.get(Calendar.SECOND);

        int ctumYear = localStartTime.get(Calendar.YEAR);
        int ctumMonth = localStartTime.get(Calendar.MONTH) + 1;
        int ctumDay = localStartTime.get(Calendar.DATE);

        //RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,TIME_YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,CAUSE_OF_HEADER,NODE_ID
        String ctumHeader = "0,4,2,2," + ctumYear + "," + ctumMonth + "," + ctumDay + "," + ctumHour + "," + ctumMinute + "," + ctumSecond + ",0,MME01";

        List<byte[]> baList = ctumStreamer.processSeedString(ctumHeader, "b");

        return baList.get(0);
    }

    public byte[] generateCtumEvent() {
        updateTime();
        int ctumHour = localStartTime.get(Calendar.HOUR_OF_DAY);
        int ctumMinute = localStartTime.get(Calendar.MINUTE);
        int ctumSecond = localStartTime.get(Calendar.SECOND);
        int ctumMSecond = localStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,MacroEnodebId,HomeEnodebId,IMSI,IMEISV,PlmnIdentity,Mmegi,Mmec,MmeUeS1apId,EnbUeS1apId
        String ctumRecord = "1,0," + ctumHour + "," + ctumMinute + "," + ctumSecond + "," + ctumMSecond + "," + macroENodebId + "," + homeENodebId + "," + imsi + "," + imeisv + "," + plmnIdentity + "," + mmegi + "," + mmec + "," + mmes1apid + "," + enbs1apid;

        return (byte[]) ctumStreamer.processSeedString(ctumRecord, "b").get(0);
    }
    
        @Override
    public void run() {
        System.out.println("Starting CTUM streaming for mme id = " + getMmeId());
        
        if(noHeader == false) {
            streamEvent(generateCtumHeader(), HEADER);
        }

        long delay = EventGenerate.delayBetweenEvents;

        while (stopStreaming == false) {
            delay = EventGenerate.delayBetweenEvents;

            if (delay > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            streamEvent(generateCtumEvent(), "CTUM");
            updateUeTemporaryIds();


        }

        close();

    }

    /**
     * @return the mmeId
     */
    public String getMmeId() {
        return mmeId;
    }

    /**
     * @param mmeId the mmeId to set
     */
    public void setMmeId(String mmeId) {
        this.mmeId = mmeId;
    }

    /**
     * @return the noHeader
     */
    public boolean isNoHeader() {
        return noHeader;
    }

    /**
     * @param noHeader the noHeader to set
     */
    public void setNoHeader(boolean noHeader) {
        this.noHeader = noHeader;
    }
}
