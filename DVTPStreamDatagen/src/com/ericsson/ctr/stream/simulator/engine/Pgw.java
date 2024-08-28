/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator.engine;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.cac.ecds.utility.streaming.PgwEventsStream;
import com.ericsson.cac.ecds.utility.streaming.SgwEventsStream;
import com.ericsson.ctr.stream.simulator.ConnectorItem;
import com.ericsson.ctr.stream.simulator.StreamRaw;
import com.ericsson.ctr.stream.simulator.Network;
import com.ericsson.ctr.stream.simulator.Utilities;
import com.ericsson.ctr.stream.simulator.Bearer;

/**
 *
 * @author ejactho
 */
public class Pgw extends StreamRaw {
    
	int bytecount=0;
    private static String imsi = null;
    private static final String imeisv = "9900004550292400";
    private static String apn = null;
    private static String pdnId = null;
    
    protected static final boolean sendHeader_default  = true;
    protected static final boolean delayHeader_default = false;
    protected static final int     eventDelay_default  = 100;
    String[] eventTime = new String[4];
    String[] ropFileOpenTime = new String[4];
    String[] ropFileCloseTime = new String[4];
    final int ropFileDuration=1; //in minutes.
    
    
    
    
    protected volatile boolean sendHeader;
    protected volatile boolean delayHeader;
    protected volatile int     eventDelay;
    protected volatile int     eventRate;
    private OutputStream out;
	private int count=0;
	
    

	public void showHelp () {
		System.out.println(
				"Stream : Cpg\n" +
				"    [--header=true/false/1/0] [--[no]delayheader]\n" +
				"    [--delay=<milliSecs>] [--eventrate=<event/sec>]");
		super.showHelp ();
	}
	
	
	private void calcDelay () {
		this.eventDelay = (this.simCount * 1000) / this.eventRate;  
	}
	
	
	private void calcRate () {
		this.eventRate  = (this.simCount * 1000) / this.eventDelay;
	}
	
	
    public Pgw () {
    	
    	super();
    	
    	this.sendHeader  = Pgw.sendHeader_default;
    	this.delayHeader = Pgw.delayHeader_default;
    	this.eventDelay  = Pgw.eventDelay_default;
    	calcRate ();
           	
    }
    
    
	public boolean processArgs (String par, String val) {
		
		if (par.equals("--header"))        { this.sendHeader  = Utilities.boolStr (val); return true; }
		if (par.equals("--noheader"))      { this.sendHeader  = false;                   return true; }
		if (par.equals("--delayheader"))   { this.delayHeader = Utilities.boolStr (val); return true; }
		if (par.equals("--nodelayheader")) { this.delayHeader = false;                   return true; }
		if (par.equals("--delay"))         { this.eventDelay  = Integer.parseInt  (val); calcRate  (); return true; }
		if (par.equals("--eventrate"))     { this.eventRate   = Integer.parseInt  (val); calcDelay (); return true; }
		return super.processArgs (par,val);
	}
	

	public void startMsg () {
		System.out.println ("Starting " + this.simCount + " simulators with " + this.eventDelay +
				            " ms delay giving " + this.eventRate + " events/sec" );
	}
	
    
  
    
private void putEvent (String objId, String rec, PgwEventsStream pgwStreamer,OutputStream out) {
    	
    	if (this.debug) { System.out.println ("D '" + objId +
    							"' rec '" + rec + "'"); }
    	//connItem.put ((byte[]) pgwStreamer.processSeedString (rec, "a").get(0));
    	try {
    		byte[] bin=(byte[]) pgwStreamer.processSeedString (rec, "a").get(0);
			out.write(bin);
			bytecount+=bin.length;
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    }
    
    
// - - - - - - - - - - - - - - - - - - - //
//  HEADER RECORD						 //
// - - - - - - - - - - - - - - - - - - - //
    private String generateCpgHeader(String objId, SimpleDateFormat tFormYearSecT, String time) {

        // RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,TIME_YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,TIME_ZONE,
        // CAUSE_OF_HEADER,NODE_ID
    	//System.out.println(tFormYearSecT.format (System.currentTimeMillis ()));
    	String record="";
    	//if(time==null)
    	 record="0,4,1,4," + tFormYearSecT.format (System.currentTimeMillis ()) + ",0," + objId;
    	//else
    	//{
    		
    	//}
        return record;
        	
    }

   
// - - - - - - - - - - - - - - - - - - - //
//  MAIN EVENT GENERATOR LOOP			 //
// - - - - - - - - - - - - - - - - - - - //
    public void startTraffic (String objId, ConnectorItem connItem) {
    	
    	SgwEventsStream sgwStreamer = new SgwEventsStream();
    	PgwEventsStream pgwStreamer = new PgwEventsStream();
    	
        SimpleDateFormat tFormYearMilli = new SimpleDateFormat (Utilities.tFstrYearMilli);
        SimpleDateFormat tFormYearSecT  = new SimpleDateFormat (Utilities.tFstrYearSecT);
        SimpleDateFormat tFormHourMilli = new SimpleDateFormat (Utilities.tFstrHourMilli);
        SimpleDateFormat tFormDate = new SimpleDateFormat (Utilities.tFStrDate);

    	tFormYearMilli.setTimeZone (Utilities.tzGmt);
    	tFormYearSecT.setTimeZone  (Utilities.tzGmt);
    	tFormHourMilli.setTimeZone (Utilities.tzGmt);
    	tFormDate.setTimeZone      (Utilities.tzGmt);

    	if (this.debug) {
    		System.out.println ("D '" + objId +
    							"' CONF: send header:'"       + this.sendHeader  + 
    							"' delay header:'"            + this.delayHeader +
    							"' event delay (ms):'"        + this.eventDelay  +
    							"' event rate (events/sec):'" + this.eventRate   +
    							"'");
    	}
    	
       
        int bearerId = 5;
    	Bearer bear = new Bearer (bearerId, bearerId);
    	List<Bearer> bearList = new ArrayList<Bearer>();
    	bearList.add(bear);

    	int callMode = Network.CALL_MODE_INET;
    	final String SEED_FILE = "resources/tmp/"+Utilities.NODE_TYPE+"/"+objId+"/"+"seed6.csv";
    	final String OUTPUT_DIR=Utilities.SEED_BIN_OP_FILE+"/"+Utilities.NODE_TYPE+"/"+objId+"/";
    	
    	
    	
    	FileReader fileReader;
    	BufferedReader bufferedReader;
    	String currentLine;
    	String[] timeTemp;
    	String time=null;
    	int fileno=0;
		try {
			fileReader = new FileReader(SEED_FILE);
			bufferedReader = new BufferedReader(fileReader);
			
			System.out.println("Starting CPG streaming for cpg id = " + objId);
			
			currentLine=bufferedReader.readLine();
			
			String[] feilds=currentLine.split(",");
			if(feilds!=null){
				System.arraycopy( feilds, 3,ropFileOpenTime,0,4 );
				setRopFileCloseTime();
				File out_dir=new File(OUTPUT_DIR);
				if (!out_dir.exists()) {
					out_dir.mkdir();
		        }
				else Utilities.cleanDirectory(out_dir);
				out = new FileOutputStream(OUTPUT_DIR+newRopFilename());
				
			}
			
			
			
			
			if (this.sendHeader){
				if(Integer.parseInt(feilds[1])==0){
				timeTemp = feilds[5].split(":");
				time = timeTemp[0] + "," + timeTemp[1] + ","+ timeTemp[2] + "," + feilds[6];
				
				}
				//putEvent (objId, generateCpgHeader(objId, tFormYearSecT,time), connItem, pgwStreamer,out);
				
				
			}
			
				
			while(currentLine!=null){
			feilds=currentLine.split(",");
			if(feilds!=null){
				int recType=Integer.parseInt(feilds[0]);
				int eventid=Integer.parseInt(feilds[1]);
				System.arraycopy( feilds, 3,eventTime,0,4 );
				
				if(!compareTime(eventTime,ropFileCloseTime))
				{
					System.arraycopy( eventTime, 0,ropFileOpenTime,0,4 );
				setRopFileCloseTime();
				out.close();
				out = new FileOutputStream(OUTPUT_DIR+newRopFilename());
				System.out.println("file"+(++fileno)+" size in bytes-->"+bytecount);
				bytecount=0;
				}
				
				
				
				 if (stopStreaming == false && recType==1 ) {
					 
					if(eventid==6){ 
						//Utilities.Sleep (eventDelay);  
						putEvent (objId, generateSessionInfo(feilds,tFormHourMilli,objId),  pgwStreamer,out);
					}
					if(eventid==7){ 
						//Utilities.Sleep (eventDelay); 
						putEvent (objId, generateDataUsage(feilds,tFormHourMilli,objId),  pgwStreamer,out);
					}
            	}
        	}
			currentLine=bufferedReader.readLine();
			//System.out.println(++count);// +" "+currentLine+"\n");
			}//end while
			System.out.println("file"+(++fileno)+" size in bytes-->"+bytecount);
			//out.flush();
			out.close();
			
			// new BufferedWriter(new FileWriter("success.txt")).write("success12345");
			}//end try block
		 catch (Exception e) {
			e.printStackTrace();
		 }
        
        close();
    }

 
    private String enrichIMSI(String imsi, String objId) {
    	String firstTwoDigits=null;
    	firstTwoDigits=imsi.substring(0, 2);
		imsi=imsi.replaceFirst(".{5}", firstTwoDigits+objId.substring(4));
    	return imsi;
    }
    
    private String enrichAPN(String apn, String objId) {
    	StringBuilder sb = new StringBuilder (1024);
    	sb.append(objId.substring(5));
    	sb.append(".");
    	sb.append(apn);
    	return sb.toString();
    }
    
    private String enrichPDNId(String pdnId, String objId) {
    	String firstTwoDigits=null;
    	firstTwoDigits=pdnId.substring(0, 2);
    	//System.out.println("First five digits:- "+firstTwoDigits+objId.substring(4));
    	pdnId=pdnId.replaceFirst(".{5}", firstTwoDigits+objId.substring(4));
    	return pdnId;


    }
    
    
    private String newRopFilename() {
		String filename="";
		for(int i=0;i<4;i++){
		ropFileOpenTime[i]=String.format("%02d",Integer.parseInt(ropFileOpenTime[i]));
		ropFileCloseTime[i]=String.format("%02d",Integer.parseInt(ropFileCloseTime[i]));
		}
    	filename=ropFileOpenTime[0]+"."+ropFileOpenTime[1]+"-"+ ropFileCloseTime[0]+"."+ropFileCloseTime[1];
    	return filename;
	}


	private boolean compareTime(String[] time1, String[] time2) {
    	if(Integer.parseInt(time1[0])<Integer.parseInt(time2[0]))
			return true;
		else if(Integer.parseInt(time1[1])<Integer.parseInt(time2[1]))
			return true;
		else if(Integer.parseInt(time1[2])<Integer.parseInt(time2[2]))
			return true;
		else if(Integer.parseInt(time1[3])<Integer.parseInt(time2[3]))
			return true;
		
		else return false;
	}


	//private String generateSessionInfo(int callMode, int bearerId,SimpleDateFormat tFormHourMilli) {
    private String generateSessionInfo(String[] feilds,SimpleDateFormat tFormHourMilli, String objId) {
    	
    	if(!objId.equals("pgw_000")){
    	if (feilds[8]!=null && feilds[8]!="")
    		feilds[8]=enrichIMSI(feilds[8], objId);
		if (feilds[20]!=null && feilds[20]!="") 
			 feilds[20]=enrichAPN(feilds[20], objId);
		feilds[27]=enrichPDNId(feilds[27], objId);
    	}
		
		String record=feilds[0];
		for(int i=1;i<feilds.length;i++)
			record+=","+feilds[i];
		
		
		return record;
    	
    	//HEADER__EVENT_RESULT,HEADER__TIME_HOUR,HEADER__TIME_MINUTE,HEADER__TIME_SECOND,HEADER__TIME_MILLISECOND,HEADER__DURATION,
    	//UE_INFO__IMSI,UE_INFO__IMSI_VALIDATION,UE_INFO__IMEISV,UE_INFO__TAI__MCC,UE_INFO__TAI__MNC,UE_INFO__TAI__TAC,
    	//UE_INFO__ECI,UE_INFO__LAC,UE_INFO__RAC,UE_INFO__CI,UE_INFO__SAC,
    	//PDN_INFO__DEFAULT_BEARER_ID,
    	//PDN_INFO__APN,PDN_INFO__PGW_ADDRESS__IPV4,PDN_INFO__PGW_ADDRESS__IPV6,PDN_INFO__ALLOCATED_UE_ADDRESS__IPV4,PDN_INFO__ALLOCATED_UE_ADDRESS__IPV6,
    	//PDN_INFO__APN_AMBR__APN_AMBR_UL,PDN_INFO__APN_AMBR__APN_AMBR_DL,PDN_INFO__PDN_ID,PDN_INFO__RULE_SPACE
    	
      /*
      return 
      		"1,6,0," + tFormHourMilli.format (System.currentTimeMillis ()) +",1,"+
      		imsi + ",1," + imeisv + "," + Network.mcc + "," + Network.mnc + ",102," +
      		Network.eci + ",,,,," + 
      		(callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
    		"," +
    		(callMode == Network.CALL_MODE_VOICE ? Network.getVoicePdn() : Network.getInetPdn()) +
    		",3232252937,,3232252935,," +
      		"22,22,12345,rulespace";
      
      */
      
      
	
}
    
 //public String generateDataUsage (int callMode, int bearerId, SimpleDateFormat tFormHourMilli) {

   


	public String generateDataUsage (String[] feilds,SimpleDateFormat tFormHourMilli, String objId) {
    	
		if(!objId.equals("pgw_000")){
		if (feilds[8]!=null && feilds[8]!="")
    		feilds[8]=enrichIMSI(feilds[8], objId);
		if (feilds[20]!=null && feilds[20]!="") 
			 feilds[20]=enrichAPN(feilds[20], objId);
		feilds[27]=enrichPDNId(feilds[27], objId);
		}
		
		String record=feilds[0];
		for(int i=1;i<feilds.length;i++)
			record+=","+feilds[i];
		
		
		return record;
		
        //HEADER__EVENT_RESULT,HEADER__TIME_HOUR,HEADER__TIME_MINUTE,HEADER__TIME_SECOND,HEADER__TIME_MILLISECOND,HEADER__DURATION,
    	//UE_INFO__IMSI,UE_INFO__IMSI_VALIDATION,UE_INFO__IMEISV,UE_INFO__TAI__MCC,UE_INFO__TAI__MNC,UE_INFO__TAI__TAC,
    	//UE_INFO__ECI,UE_INFO__LAC,UE_INFO__RAC,UE_INFO__CI,UE_INFO__SAC,
    	//PDN_INFO__DEFAULT_BEARER_ID,
    	//PDN_INFO__APN,
	 	//PDN_INFO__PGW_ADDRESS__IPV4,PDN_INFO__PGW_ADDRESS__IPV6,PDN_INFO__ALLOCATED_UE_ADDRESS__IPV4,PDN_INFO__ALLOCATED_UE_ADDRESS__IPV6,
    	//PDN_INFO__APN_AMBR__APN_AMBR_UL,PDN_INFO__APN_AMBR__APN_AMBR_DL,PDN_INFO__PDN_ID,PDN_INFO__RULE_SPACE,
    	//BEARER_USAGE_INFO_EPS_BEARER_ID,BEARER_USAGE_INFO_BEARER_CAUSE,BEARER_USAGE_INFO_BEARER_UL_PACKETS,BEARER_USAGE_INFO_BEARER_DL_PACKETS,
    	//BEARER_USAGE_INFO_BEARER_UL_BYTES,BEARER_USAGE_INFO_BEARER_DL_BYTES,
	 	//SESSION_USAGE_INFO_RATING_GROUP,SESSION_USAGE_INFO_SERVICE_IDENTIFIER,SESSION_USAGE_INFO_URI_NAME,SESSION_USAGE_INFO_URI_ID,SESSION_USAGE_INFO_SERVICE_UL_BYTES,SESSION_USAGE_INFO_SERVICE_DL_BYTES
	
      /*
      return 
      		"1,7,0," + tFormHourMilli.format (System.currentTimeMillis ()) +",1,"+
      		imsi + ",," + imeisv + "," + Network.mcc + "," + Network.mnc + ",," +
      		Network.eci + 
      		",,,,," + 
      		(callMode == Network.CALL_MODE_VOICE ? Network.getVoiceBearerDefault() : Network.getInetBearerDefault()) +
    		"," +
    		(callMode == Network.CALL_MODE_VOICE ? Network.getVoicePdn() : Network.getInetPdn()) +
    		",3232252937,,3232252935,," +
      		"22,22,12345,rulespace,"
      		+ "["+bearerId+";0;4;4;22;22|"+(bearerId+1)+";0;5;5;29;29],"+
      		"[]";//,14,"+
      		//",abc,123,200,200";
      		 *
      		 */
  }

private void setRopFileCloseTime()
{
	System.arraycopy( ropFileOpenTime, 0,ropFileCloseTime,0,4 );
	int endtime=Integer.parseInt(ropFileOpenTime[1]) + ropFileDuration;
	if(endtime>=60)
	{
		ropFileCloseTime[0]=""+(Integer.parseInt(ropFileOpenTime[0]) + 1);
		ropFileCloseTime[1]=""+(endtime%60);
	}
	else 	ropFileCloseTime[1]=""+endtime;
}

private boolean checkEventTime(String[] eventTime,	SimpleDateFormat tFormHourMilli) {
	String[] currentTime=tFormHourMilli.format (System.currentTimeMillis ()).split(",");
	//System.out.println(tFormHourMilli.format (System.currentTimeMillis ()));
	if(Integer.parseInt(eventTime[0])<Integer.parseInt(currentTime[0]))
		return true;
	else if(Integer.parseInt(eventTime[1])<Integer.parseInt(currentTime[1]))
		return true;
	else if(Integer.parseInt(eventTime[2])<Integer.parseInt(currentTime[2]))
		return true;
	else if(Integer.parseInt(eventTime[3])<Integer.parseInt(currentTime[3]))
		return true;
	
	else return false;
}


	public synchronized void setStopStreaming (boolean stopStreaming) { this.stopStreaming = stopStreaming; }
}
