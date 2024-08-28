package com.ericsson.ctr.stream.simulator.engine;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.cac.ecds.utility.streaming.PgwEventsStream;
import com.ericsson.cac.ecds.utility.streaming.SgwEventsStream;
import com.ericsson.cac.ecds.utility.streaming.SgehEventsStream;
import com.ericsson.ctr.stream.simulator.ConnectorItem;
import com.ericsson.ctr.stream.simulator.StreamRaw;
import com.ericsson.ctr.stream.simulator.Network;
import com.ericsson.ctr.stream.simulator.Utilities;
import com.ericsson.ctr.stream.simulator.Bearer;

public class Sgeh extends StreamRaw{
	
int bytecount=0;
private static String imsi = null;
private static final String imeisv = "9900004550292400";
private static String apn = null;

protected static final boolean sendHeader_default  = true;
protected static final boolean delayHeader_default = false;
protected static final int     eventDelay_default  = 100;
String[] eventTime = new String[3];
String[] ropFileOpenTime = new String[3];
String[] ropFileCloseTime = new String[3];
final int ropFileDuration=1; //in minutes.


protected volatile boolean sendHeader;
protected volatile boolean delayHeader;
protected volatile int     eventDelay;
protected volatile int     eventRate;
protected OutputStream out;
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


public Sgeh() {
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



private void putEvent (String objId, String rec, SgehEventsStream sgehStreamer, OutputStream out) {
	
	try {
		byte[] bin = (byte[])sgehStreamer.processSeedString (rec, "a").get(0);
		
		out.write(bin,0,bin.length);
		bytecount+=bin.length;
	} catch (IOException e) {
	
		e.printStackTrace();
	}
	
}



//- - - - - - - - - - - - - - - - - - - //
//HEADER RECORD						 //
//- - - - - - - - - - - - - - - - - - - //
private String generateCpgHeader(String objId, SimpleDateFormat tFormYearSecT, String time) {

    // RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,TIME_YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,TIME_ZONE,
    // CAUSE_OF_HEADER,NODE_ID
	//System.out.println(tFormYearSecT.format (System.currentTimeMillis ()));
	
    return
    	"0,4,1,4," + tFormYearSecT.format (System.currentTimeMillis ()) + ",0," + objId;
}




//- - - - - - - - - - - - - - - - - - - //
//MAIN EVENT GENERATOR LOOP			 //
//- - - - - - - - - - - - - - - - - - - //
public void startTraffic (String objId, ConnectorItem connItem) {
	SgwEventsStream sgwStreamer = new SgwEventsStream();
	SgehEventsStream sgehStreamer = new SgehEventsStream();
	
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
	final String SEED_FILE = "resources/tmp/"+Utilities.NODE_TYPE+"/"+objId+"/sgehseedfile1.csv";
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
		String[] feilds=currentLine.split(",",-1);
		//while(currentLine!=null){
		//String[] temp=currentLine.split(",");
		
		if(feilds!=null){
			System.arraycopy( feilds, 3,ropFileOpenTime,0,3 );
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
			//putEvent (objId, generateCpgHeader(objId, tFormYearSecT,time), connItem, sgehStreamer, out);
			
		
		}
		
		while(currentLine!=null){
		feilds=currentLine.split(",",-1);
		if(feilds!=null){
			int recType=Integer.parseInt(feilds[0]);
			int eventid=Integer.parseInt(feilds[1]);
			System.arraycopy( feilds, 3,eventTime,0,3 );
			
			if(!compareTime(eventTime,ropFileCloseTime))
			{
				System.arraycopy( eventTime, 0,ropFileOpenTime,0,3 );
			setRopFileCloseTime();
			out.close();
			out = new FileOutputStream(OUTPUT_DIR+newRopFilename());
			System.out.println("file"+(++fileno)+" size in bytes-->"+bytecount);
			bytecount=0;
			}
			

			/*if (this.sendHeader && recType==0){
				putEvent (objId, generateCpgHeader(objId, tFormYearSecT), connItem, sgwStreamer);
				if (this.delayHeader) {
					Utilities.Sleep (eventDelay);
					Utilities.Sleep (eventDelay);
				}
			}*/
			 if (stopStreaming == false && recType==1 ) {
				
				if(eventid==4){ //Utilities.Sleep (eventDelay); 
				putEvent (objId, generateDeactivate(feilds,tFormHourMilli, objId),  sgehStreamer, out);
				}
				
				if(eventid==1){ //Utilities.Sleep (eventDelay); 
				putEvent (objId, generateActivate(feilds,tFormHourMilli, objId),  sgehStreamer, out);
				}
        	}
    	}
		currentLine=bufferedReader.readLine();
		}//end while
		out.flush();
		out.close();
		}//end try block
	 catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	catch (IOException e) {
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

private String newRopFilename() {
	String filename="";
	for(int i=0;i<3;i++){
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
	
	
	else return false;
}


 public String generateActivate (String[] feilds,SimpleDateFormat tFormHourMilli, String objId) {
 
    if(!objId.equals("sgeh_000")){
	if (feilds[17]!=null || feilds[17]!="")
		feilds[17]=enrichIMSI(feilds[17], objId);
	if (feilds[20]!=null || feilds[20]!="") 
		 feilds[20]=enrichAPN(feilds[20], objId);
	
	}
	
	String record=feilds[0];
	for(int i=1;i<feilds.length;i++)
		record+=","+feilds[i];
	
	return record;
	/*String[] feilds=currentLine.split(",");
	String[] eventTime = new String[4];
	System.arraycopy( feilds, 3,eventTime,0,4 );
	
	//while(checkEventTime(eventTime,tFormHourMilli)==false)
	//	Utilities.Sleep (1000);//wait
		    	
	return currentLine;*/

}

public String generateDeactivate (String[] feilds,SimpleDateFormat tFormHourMilli, String objId) {

    if(!objId.equals("sgeh_000")){
	if (feilds[16]!=null || feilds[16]!="")
		feilds[16]=enrichIMSI(feilds[16], objId);
	if (feilds[19]!=null || feilds[19]!="") 
		 feilds[19]=enrichAPN(feilds[19], objId);
	
	}
	
	String record=feilds[0];
	for(int i=1;i<feilds.length;i++)
		record+=","+feilds[i];
	
	return record;
	
	/*String[] feilds=currentLine.split(",");
	String[] eventTime = new String[4];
	System.arraycopy( feilds, 3,eventTime,0,4 );
	
	//while(checkEventTime(eventTime,tFormHourMilli)==false)
	//	Utilities.Sleep (1000);//wait
		    	
	return currentLine;*/
	
}

private void setRopFileCloseTime()
{
System.arraycopy( ropFileOpenTime, 0,ropFileCloseTime,0,3 );
int endtime=Integer.parseInt(ropFileOpenTime[1]) + ropFileDuration;
if(endtime>=60)
{
	ropFileCloseTime[0]=""+(Integer.parseInt(ropFileOpenTime[0]) + 1);
	ropFileCloseTime[1]=""+(endtime%60);
}
else 	ropFileCloseTime[1]=""+endtime;
}





public synchronized void setStopStreaming (boolean stopStreaming) { this.stopStreaming = stopStreaming; }


}
