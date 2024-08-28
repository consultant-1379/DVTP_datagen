/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import com.ericsson.cac.ecds.utility.streaming.CtumEventsStream;
import com.ericsson.cac.ecds.utility.streaming.EnodebN2EventsStream;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public abstract class Stream implements Runnable {

    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inactive";
    public static final String MEASUREMENT = "Measurement";
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
    private long racueref;
    private String gummei;
    private String plmnIdentity;
    private String macroENodebId = "1002";
    private String homeENodebId = "1002";
    private String eNodeBCellId;
    protected String eNodeBId;
    private Random random;
    private EnodebN2EventsStream enbStreamer;
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
    private int scannerId = 10000;
    protected boolean delayHeaderTransmission;
    private boolean noHeader = false;
    protected Long droppedEvents;

    public Stream(String eNodeBId, int port, int scanner) {
        eventCount = 0;
        plmnIdentity = "" + mcc + "" + mnc;
        gummei = Utilities.generateGummei(plmnIdentity, mmegi, mmec);

        localStartTime = Calendar.getInstance();
        utcTimeFormatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss:SSS");
        utcTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        enbStreamer = new EnodebN2EventsStream();

        random = new Random(localStartTime.getTimeInMillis());

        this.eNodeBId = eNodeBId;
        macroENodebId = "" + eNodeBId;
        homeENodebId = macroENodebId;
        eNodeBCellId = eNodeBId;

        this.port = port;
        this.stopStreaming = false;
        this.scannerId = scanner;

        enbs1apid = 0;
        mmes1apid = scannerId;
        racueref = scannerId;

    }

    public Stream(String eNodeBId, String destIp, int port, int scanner) throws SocketTimeoutException {
        this(eNodeBId, port, scanner);
        boolean connected = false;

        try {

            InetAddress addr = InetAddress.getByName(destIp);
            SocketAddress sockaddr = new InetSocketAddress(addr, port);
            socket = new Socket();
            socket.connect(sockaddr, 2000);
            os = new DataOutputStream(socket.getOutputStream());
            connected = true;

        } catch (UnknownHostException ex) {
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        } catch (SocketTimeoutException ex) {
            throw ex;
        } catch (IOException ex) {
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }

        if (connected == false) {
            System.err.println("Unable to connect to " + destIp + ":" + port);
            System.exit(1);
        }
    }

    public Stream(String eNodeBId, InetAddress bindaddr, String destIp, int port, int scanner) throws SocketTimeoutException {
        this(eNodeBId, port, scanner);
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
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        } catch (SocketTimeoutException ex) {
            throw ex;
        } catch (IOException ex) {
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void close() {
        try {
            os.flush();
            os.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
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
//        mmes1apid++;
//        racueref++;
    }
    
    protected void updateEnbS1ApId() {
        enbs1apid++;
    }

    public byte[] generateGoodEnbHeader() {
        Calendar utcStartTime = getTimeinUTC();

        int eNodeBStartTimeYear = utcStartTime.get(Calendar.YEAR);
        int eNodeBStartTimeMonth = utcStartTime.get(Calendar.MONTH) + 1;
        int eNodeBStartTimeDay = utcStartTime.get(Calendar.DATE);
        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);

        //RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,NE User Label,NE Logical Label,SCANNER,CAUSE_OF_HEADER,NO_OF_DROPPED_EVENTS
        String eNodeBHeader = "0,4,J,N2," + eNodeBStartTimeYear + "," + eNodeBStartTimeMonth + "," + eNodeBStartTimeDay + "," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNodeBId + "," + getScannerId() + "," + getScannerId() + ",0,0";

        List<byte[]> baList = enbStreamer.processSeedString(eNodeBHeader, eNodeBHeader);
        return baList.get(0);
    }

    public byte[] generateDroppedEnbHeader(long droppedEvents) {
        Calendar utcStartTime = getTimeinUTC();

        int eNodeBStartTimeYear = utcStartTime.get(Calendar.YEAR);
        int eNodeBStartTimeMonth = utcStartTime.get(Calendar.MONTH) + 1;
        int eNodeBStartTimeDay = utcStartTime.get(Calendar.DATE);
        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);

        //RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,NE User Label,NE Logical Label,SCANNER,CAUSE_OF_HEADER,NO_OF_DROPPED_EVENTS
        String eNodeBHeader = "0,4,J,N2," + eNodeBStartTimeYear + "," + eNodeBStartTimeMonth + "," + eNodeBStartTimeDay + "," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNodeBId + "," + eNodeBId + "," + getScannerId() + ",1," + droppedEvents;

        List<byte[]> baList = enbStreamer.processSeedString(eNodeBHeader, eNodeBHeader);
        return baList.get(0);
    }

    public byte[] generateInitContextSetupEvent() {
        Calendar utcStartTime = getTimeinUTC();

        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eNbEventStopHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStopMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStopSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStopMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eventHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eventMinute = utcStartTime.get(Calendar.MINUTE);
        int eventSecond = utcStartTime.get(Calendar.SECOND);
        int eventMsecond = utcStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,InitialContextResult,ErabSetupLicenseFailBitmap,ErabSetupLicenseFail,ErabSetupReqBitmap,ErabSetupReq,ErabSetupSuccBitmap,ErabSetupSucc,StartHour,StartMinute,StartSecond,StartMilliSec,StopHour,StopMinute,StopSecond,StopMilliSec
        String initCtxtSetup = "1,4106," + eventHour + "," + eventMinute + "," + eventSecond + "," + eventMsecond + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,0,0,0,32,1,32,1," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + "," + eNbEventStopHour + "," + eNbEventStopMinute + "," + eNbEventStopSecond + "," + eNbEventStopMilliSec;

        return (byte[]) enbStreamer.processSeedString(initCtxtSetup, null).get(0);

    }

    public byte[] generateS1SignalConnectionSetupEvent() {
        Calendar utcStartTime = getTimeinUTC();

        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eNbEventStopHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStopMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStopSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStopMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eventHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eventMinute = utcStartTime.get(Calendar.MINUTE);
        int eventSecond = utcStartTime.get(Calendar.SECOND);
        int eventMsecond = utcStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,S1SigConnResult,StartHour,StartMinute,StartSecond,StartMilliSec,StopHour,StopMinute,StopSecond,StopMilliSec
        String s1SigConnSetup = "1,4098," + eventHour + "," + eventMinute + "," + eventSecond + "," + eventMsecond + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,0," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + "," + eNbEventStopHour + "," + eNbEventStopMinute + "," + eNbEventStopSecond + "," + eNbEventStopMilliSec;

        return (byte[]) enbStreamer.processSeedString(s1SigConnSetup, null).get(0);
    }

    public byte[] generateRrcConnectionSetupEvent() {
        Calendar utcStartTime = getTimeinUTC();

        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eNbEventStopHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStopMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStopSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStopMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eventHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eventMinute = utcStartTime.get(Calendar.MINUTE);
        int eventSecond = utcStartTime.get(Calendar.SECOND);
        int eventMsecond = utcStartTime.get(Calendar.MILLISECOND);


        //INT_PROC_RRC_CONN_SETUP_K7:RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,UeIdentityType,UeIdentity,RrcEstablishCause,RrcResult,StartHour,StartMinute,StartSecond,StartMilliSec,StopHour,StopMinute,StopSecond,StopMilliSec
        String rrcConnSetup = "1,4097," + eventHour + "," + eventMinute + "," + eventSecond + "," + eventMsecond + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,0,0,4,0," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + "," + eNbEventStopHour + "," + eNbEventStopMinute + "," + eNbEventStopSecond + "," + eNbEventStopMilliSec;

        return (byte[]) enbStreamer.processSeedString(rrcConnSetup, null).get(0);

    }

    public byte[] generateUeContextReleaseEvent() {
        Calendar utcStartTime = getTimeinUTC();

        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eNbEventStopHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStopMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStopSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStopMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        utcStartTime.add(Calendar.MILLISECOND, 12);

        int eventHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eventMinute = utcStartTime.get(Calendar.MINUTE);
        int eventSecond = utcStartTime.get(Calendar.SECOND);
        int eventMsecond = utcStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,TriggeringNode,UeReleaseCause,getErabDataLostBitmap,ErabDataLost,ErabReleaseSucc,HoOutPrepErabFailBitmap,HoOutPrepErabFail,HoOutPrepDataLost,StartHour,StartMinute,StartSecond,StartMilliSec,StopHour,StopMinute,StopSecond,StopMilliSec
        String ueCtxtRelease = "1,4100," + eventHour + "," + eventMinute + "," + eventSecond + "," + eventMsecond + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,0,10,32,3,0,0,0,0," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + "," + eNbEventStopHour + "," + eNbEventStopMinute + "," + eNbEventStopSecond + "," + eNbEventStopMilliSec;

        return (byte[]) enbStreamer.processSeedString(ueCtxtRelease, null).get(0);
    }

    public List<EventData> getUeInitialAttachProcedure() {
        List<EventData> list = new ArrayList<EventData>();
        updateUeTemporaryIds();

        localStartTime.add(Calendar.MILLISECOND, random.nextInt(MAX_MS_BEFORE_NEXT_EVENT));
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateRrcConnectionSetupEvent(), Stream.ACTIVE));

        localStartTime.add(Calendar.MILLISECOND, random.nextInt(MAX_MS_BEFORE_NEXT_EVENT));
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateS1SignalConnectionSetupEvent(), Stream.ACTIVE));

        localStartTime.add(Calendar.MILLISECOND, random.nextInt(MAX_MS_BEFORE_NEXT_EVENT));
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateInitContextSetupEvent(), Stream.ACTIVE));

        return list;
    }

    public byte[] generateUeMeasurementEvent() {
        Calendar utcStartTime = getTimeinUTC();
        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,RANK_TX_0,RANK_TX_1,RANK_TX_2,RANK_TX_3,RANK_TX_4,CQI_REPORTED_0,CQI_REPORTED_1,CQI_REPORTED_2,CQI_REPORTED_3,CQI_REPORTED_4,CQI_REPORTED_5,CQI_REPORTED_6,CQI_REPORTED_7,CQI_REPORTED_8,CQI_REPORTED_9,CQI_REPORTED_10,CQI_REPORTED_11,CQI_REPORTED_12,CQI_REPORTED_13,CQI_REPORTED_14,CQI_REPORTED_15,RANK_REPORTED_0,RANK_REPORTED_1,TBSPWR_RESTRICTED,TBSPWR_UNRESTRICTED,RADIOTHP_VOL_DL,RADIOTHP_RES_DL,RADIOTHP_VOL_UL,RADIOTHP_RES_UL
        String ueMeasurementEvent = "1,3075," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";


        return (byte[]) enbStreamer.processSeedString(ueMeasurementEvent, null).get(0);
    }

    public byte[] generateUeTrafficRepEvent() {
        Calendar utcStartTime = getTimeinUTC();
        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,UE_THP_PDCPVOL_TRUNK_DL,UE_THP_TIME_DL,UE_LAT_TIME_DL,UE_LAT_SAMPL_DL,UE_THP_PDCPVOL_TRUNK_UL,UE_THP_TIME_UL,SCHED_RESTRICT_UE_CAT_UL,SCHED_RESTRICT_UE_CAT_DL,SCHED_ACTIVITY_UE_UL,SCHED_ACTIVITY_UE_DL,UE_PACKET_TR_DL,UE_PACKET_REC_DL,UE_PACKET_LOST_HO_DL,UE_PACKET_LOST_PELR_DL,UE_PACKET_REC_UL,UE_PACKET_LOST_UL,UE_PDCP_SRB_ACKVOL_DL,UE_PDCP_DRB_ACKVOL_DL,UE_RLC_ACK_DL,UE_RLC_NACK_DL,UE_RLC_ACK_UL,UE_RLC_NACK_UL,PER_UE_PACKET_LOST_PELR_UU_DL,PER_UE_DL_RLC_DELAY,PER_UE_DL_MAC_DELAY,PER_UE_DL_RLC_DELAY_SAMPL_DL,UE_HARQ_DL_ACK_QPSK,getUE_HARQ_DL_NACK_QPSK,getUE_HARQ_UL_SUCC_QPSK,UE_HARQ_UL_FAIL_QPSK,UE_HARQ_DL_ACK_16QAM,UE_HARQ_DL_NACK_16QAM,UE_HARQ_UL_SUCC_16QAM,UE_HARQ_UL_FAIL_16QAM,UE_HARQ_DL_ACK_64QAM,UE_HARQ_DL_NACK_64QAM,PER_UE_MAC_DTX_UL_QPSK,PER_UE_MAC_DTX_DL_QPSK,PER_UE_MAC_DTX_DL_16QAM,PER_UE_MAC_DTX_UL_16QAM,PER_UE_MAC_DTX_DL_64QAM]
        String ueTrafficRepString = "1,3076," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";


        return (byte[]) enbStreamer.processSeedString(ueTrafficRepString, null).get(0);
    }

    public byte[] generateUeRbTrafficRepEvent() {
        Calendar utcStartTime = getTimeinUTC();
        int eNbEventStartHour = utcStartTime.get(Calendar.HOUR_OF_DAY);
        int eNbEventStartMinute = utcStartTime.get(Calendar.MINUTE);
        int eNbEventStartSecond = utcStartTime.get(Calendar.SECOND);
        int eNbEventStartMilliSec = utcStartTime.get(Calendar.MILLISECOND);


        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,SCANNER_ID,RBS_MODULE_ID,CELL_ID,EnbS1APId,MmeS1APId,Gummei,RacUERef,SessionRef,Bearer_ID,ErabId,PerPdcpvolDlRb,PerPdcpvolUlRb
        String ueRbTrafficRepString = "1,3077," + eNbEventStartHour + "," + eNbEventStartMinute + "," + eNbEventStartSecond + "," + eNbEventStartMilliSec + ",128,0," + eNodeBCellId + "," + enbs1apid + "," + mmes1apid + "," + gummei + "," + racueref + ",3,5,5,5937,228";

        return (byte[]) enbStreamer.processSeedString(ueRbTrafficRepString, null).get(0);
    }

    public EventData getUeIdleProcedure() {
        updateTime();
        return new EventData(EventGenerate.delayCtrMilliSeconds, generateUeContextReleaseEvent(), Stream.INACTIVE);

    }

    public EventData getUeIdleProcedure(long delay) {
        updateTime();
        return new EventData(delay, generateUeContextReleaseEvent(), Stream.INACTIVE);

    }

    public List<EventData> getUeActiveProcedure() {
        List<EventData> list = new ArrayList<EventData>();
        updateTime();
        updateUeTemporaryIds();

        localStartTime.add(Calendar.MILLISECOND, random.nextInt(MAX_MS_BEFORE_NEXT_EVENT));
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateRrcConnectionSetupEvent(), Stream.ACTIVE));

        localStartTime.add(Calendar.MILLISECOND, random.nextInt(MAX_MS_BEFORE_NEXT_EVENT));
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateS1SignalConnectionSetupEvent(), Stream.ACTIVE));

        localStartTime.add(Calendar.MILLISECOND, random.nextInt(MAX_MS_BEFORE_NEXT_EVENT));
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateInitContextSetupEvent(), Stream.ACTIVE));

        return list;

    }

    public List<EventData> getMeasurements() {
        List<EventData> list = new ArrayList<EventData>();
        updateTime();
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateUeMeasurementEvent(), Stream.MEASUREMENT));
        updateUeTemporaryIds();
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateUeRbTrafficRepEvent(), Stream.MEASUREMENT));
        updateUeTemporaryIds();
        list.add(new EventData(EventGenerate.delayCtrMilliSeconds, generateUeTrafficRepEvent(), Stream.MEASUREMENT));
        updateUeTemporaryIds();

        return list;
    }

    public String getENodeBId() {
        return eNodeBId;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void checkSize() {
        EventData idleProcedure = getUeIdleProcedure();
        List<EventData> measurements = getMeasurements();
        List<EventData> activeprocs = getUeActiveProcedure();
        List<EventData> initattach = getUeInitialAttachProcedure();
        for (EventData ed : measurements) {
            System.out.println("measurements = " + ed.getBa().length);
        }
        for (EventData ed : activeprocs) {
            System.out.println("activeprocs = " + ed.getBa().length);
        }
        for (EventData ed : initattach) {
            System.out.println("initattach = " + ed.getBa().length);
        }

        System.out.println("IDLE Size = " + idleProcedure.getBa().length);
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
        int ctumHour = localStartTime.get(Calendar.HOUR_OF_DAY);
        int ctumMinute = localStartTime.get(Calendar.MINUTE);
        int ctumSecond = localStartTime.get(Calendar.SECOND);
        int ctumMSecond = localStartTime.get(Calendar.MILLISECOND);

        //RECORD_TYPE,EVENT_ID,HOUR,MINUTE,SECOND,MILLI-SECOND,MacroEnodebId,HomeEnodebId,IMSI,IMEISV,PlmnIdentity,Mmegi,Mmec,MmeUeS1apId,EnbUeS1apId
        String ctumRecord = "1,0," + ctumHour + "," + ctumMinute + "," + ctumSecond + "," + ctumMSecond + "," + macroENodebId + "," + homeENodebId + "," + imsi + "," + imeisv + "," + plmnIdentity + "," + mmegi + "," + mmec + "," + mmes1apid + "," + enbs1apid;

        return (byte[]) ctumStreamer.processSeedString(ctumRecord, "b").get(0);
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

    /**
     * @return the scannerId
     */
    public int getScannerId() {
        return scannerId;
    }

    /**
     * @param scannerId the scannerId to set
     */
    public void setScannerId(int scannerId) {
        this.scannerId = scannerId;
    }

    /**
     * @return the droppedEvents
     */
    public Long getDroppedEvents() {
        return droppedEvents;
    }

    /**
     * @param droppedEvents the droppedEvents to set
     */
    public void setDroppedEvents(Long droppedEvents) {
        this.droppedEvents = droppedEvents;
    }
}
