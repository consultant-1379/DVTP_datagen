package com.ericsson.livedata.preprocessing;

import java.io.*;
import java.text.SimpleDateFormat;

import com.ericsson.ctr.stream.simulator.Utilities;



class StreamHeader{
	// RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,TIME_YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,TIME_ZONE,
    // CAUSE_OF_HEADER,NODE_ID
	static final String recType="0";
	static final String eventId="4";
	public String date;
	public String utcTime;
	public String utcOffset;
	public String fileFrmtVrsn;
	public String fileInfoVrsn;
	public String nodeid;
	public String cause_header;
	
public String createCSVRecord(){
	SimpleDateFormat tFormDate = new SimpleDateFormat (Utilities.tFStrDate);
	
	String[] dateTemp=date.split("-");
	date=dateTemp[0]+","+dateTemp[1]+","+dateTemp[2];
	
	String[] timeTemp=utcTime.split(":");
	utcTime=timeTemp[0]+","+timeTemp[1]+","+timeTemp[2];
	
	String record= recType+","+eventId+","+fileFrmtVrsn+","+fileInfoVrsn+","+date+","+utcTime+",GMT,"+cause_header+","+nodeid;
	record=record.replace("undefined", "");
	
	//System.out.println(record);
	
	return record;
}

}

class SessionInfoEvent{
	//HEADER__EVENT_RESULT,HEADER__TIME_HOUR,HEADER__TIME_MINUTE,HEADER__TIME_SECOND,HEADER__TIME_MILLISECOND,HEADER__DURATION,
	//UE_INFO__IMSI,UE_INFO__IMSI_VALIDATION,UE_INFO__IMEISV,UE_INFO__TAI__MCC,UE_INFO__TAI__MNC,UE_INFO__TAI__TAC,
	//UE_INFO__ECI,UE_INFO__LAC,UE_INFO__RAC,UE_INFO__CI,UE_INFO__SAC,
	//PDN_INFO__DEFAULT_BEARER_ID,
	//PDN_INFO__APN,PDN_INFO__PGW_ADDRESS__IPV4,PDN_INFO__PGW_ADDRESS__IPV6,PDN_INFO__ALLOCATED_UE_ADDRESS__IPV4,PDN_INFO__ALLOCATED_UE_ADDRESS__IPV6,
	//PDN_INFO__APN_AMBR__APN_AMBR_UL,PDN_INFO__APN_AMBR__APN_AMBR_DL,PDN_INFO__PDN_ID,PDN_INFO__RULE_SPACE
	
	static final String recType="1";
	static final String eventId="6";
	public String event_result;
	
	public String time_hour,time_minute ;
	public String time_second ,time_millisecond ;
	public String duration,	default_bearer_id,apn,pgw_ipv4,pgw_ipv6,ue_ipv4,ue_ipv6,apn_ambr_ul,
		apn_ambr_dl,pdn_id,rule_space,imsi ,imsi_validation,imeisv,mcc,mnc,tac,eci,lac,rac,ci,sac;
	
public String createCSVRecord(){
	String record= recType+","+eventId+","+event_result+","+time_hour+","+time_minute+","+time_second+","+time_millisecond+","+duration+","+imsi+","+imsi_validation+","+imeisv+
					","+mcc+","+mnc+","+tac+","+eci+","+lac+","+rac+","+ci+","+sac+","+default_bearer_id+","+apn+","+pgw_ipv4+","+pgw_ipv6+","+ue_ipv4+","+ue_ipv6+
					","+apn_ambr_ul+","+apn_ambr_dl+","+pdn_id+","+rule_space;
	record=record.replace("undefined", "");
	//System.out.println(record);
	
	return record;
}

}

class DataUsageEvent{
	
	static final String recType="1";
	static final String eventId="7";
	public String event_result;
	
	public String time_hour,time_minute ;
	public String time_second ,time_millisecond ;
	public String duration,	default_bearer_id,apn,pgw_ipv4,pgw_ipv6,ue_ipv4,ue_ipv6,apn_ambr_ul,
		apn_ambr_dl,pdn_id,rule_space,imsi ,imsi_validation,imeisv,mcc,mnc,tac,eci,lac,rac,ci,sac;
	public String bearer_usage_info;
	public String service_usage_info;
	
public String createCSVRecord(){
	String record= recType+","+eventId+","+event_result+","+time_hour+","+time_minute+","+time_second+","+time_millisecond+","+duration+","+imsi+","+imsi_validation+","+imeisv+
					","+mcc+","+mnc+","+tac+","+eci+","+lac+","+rac+","+ci+","+sac+","+default_bearer_id+","+apn+","+pgw_ipv4+","+pgw_ipv6+","+ue_ipv4+","+ue_ipv6+
					","+apn_ambr_ul+","+apn_ambr_dl+","+pdn_id+","+rule_space+","+bearer_usage_info+","+service_usage_info;
	record=record.replace("undefined", "");
	//System.out.println(record);
	
	return record;
}

}

public class SeedFileCreation {
	private static final String SCRIPT_ASCII_OP_FILE = "output.28.txt";
	private static final String SEED_OP_FILE = "resources/seed/seedfile.28.csv";
	
	public static void main(String[] args) {
		String line = null;
		String record="";
        try {
            FileReader fileReader = new FileReader(SCRIPT_ASCII_OP_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            FileWriter fileWriter = new FileWriter(SEED_OP_FILE);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line + " __");
                record=processLine(line,bufferedReader);
                if(!record.equals(""))
                	{System.out.println(record );
                	bufferedWriter.write(record+"\n");          	
                	}
            }	
            bufferedWriter.close();
            bufferedReader.close();			
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file");				
        }
        catch(IOException ex) {
            System.out.println("Error reading file '");					
        }
	}

	
	private static String processLine(String line,BufferedReader bufferedReader) {
		String currentRecord;
		String record="";
		
		if(line.contains("###FILE###"))	
			currentRecord="FILE";
		else if(line.contains("###HEADER###"))	
			{currentRecord="HEADER";processFileHeader(bufferedReader);}
		else if(line.contains("###STREAM HEADER###"))	
			{currentRecord="STREAM_HEADER";
			record=processStreamHeader(bufferedReader);}
		
		else if(line.contains("======EVENT======"))	
			{currentRecord="EVENT";
			record=processEvent(bufferedReader);}
		return record;
	}


	private static String processEvent(BufferedReader bufferedReader) {
		String currentLine;
		String record="";
			try {
				currentLine = bufferedReader.readLine();
				if(!currentLine.equals("header:"))	System.out.println("error");
				else {
					currentLine = bufferedReader.readLine();
					String temp[] = currentLine.trim().split("=");
					temp[0]=temp[0].trim();
					temp[1]=temp[1].trim();
					if(temp[0].equals("event_id")  && temp[1].equals("data_usage") ) 
						record=processDatausageEvent(bufferedReader);
					else if(temp[0].equals("event_id")  && temp[1].equals("session_info") )
						record=processSessionInfoEvent(bufferedReader);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return record;
		
	}


	private static String processDatausageEvent(BufferedReader bufferedReader) {
		// TODO Auto-generated method stub
		
		DataUsageEvent dataUsageEvent= new DataUsageEvent();
		String temp[] = new String[2];
		String currentSection="";
		String eps_bearer_id="" ,bearer_cause="",bearer_ul_packets="" ,bearer_dl_packets="",bearer_ul_bytes="",bearer_dl_bytes="";
		String bearerStruct="";
		
		String rating_group = "" ,service_identifier = "" ,uri_name = "" ,uri_id = "" ,service_ul_bytes = "" ,service_dl_bytes = "" ;
		String sessionStruct="";
		
		boolean firstBearer=true,firstSession=true;
		for (int i=0;;i++){
		String currentLine;
		try {
			currentLine = bufferedReader.readLine();
			if (currentLine.equals("") ) { break;}
			//System.out.println(currentLine);
			temp = currentLine.trim().split("=");
			
			temp[0]=temp[0].trim();
			if(temp.length==2) temp[1]=temp[1].trim();
			if(temp[0].equals("pgw_address:") )
				currentSection="pgw_address";
			else if(temp[0].equals("allocated_ue_address:") )
				currentSection="allocated_ue_address";
			else if(temp[0].equals("event_result") ) 
				dataUsageEvent.event_result=temp[1].equals("success")?"0":"1";
			else if(temp[0].equals("time_hour") ) 
				dataUsageEvent.time_hour=temp[1];
			else if(temp[0].equals("time_minute") )
				dataUsageEvent.time_minute=temp[1];
			else if(temp[0].equals("time_second") )
				dataUsageEvent.time_second=temp[1];
			else if(temp[0].equals("time_millisecond")  )
				dataUsageEvent.time_millisecond=temp[1];
			else if(temp[0].equals("duration")  )
				dataUsageEvent.duration=temp[1];
			else if(temp[0].equals("default_bearer_id")  )
				dataUsageEvent.default_bearer_id=temp[1];
			else if(temp[0].equals("apn") ) 
				dataUsageEvent.apn=temp[1];
			else if(temp[0].equals("ipv4")  && currentSection.equals("pgw_address") )
				dataUsageEvent.pgw_ipv4=temp[1];
			else if(temp[0].equals("ipv6")  && currentSection.equals("pgw_address") )
				dataUsageEvent.pgw_ipv6=temp[1];
			else if(temp[0].equals("ipv4")  && currentSection.equals("allocated_ue_address") )
				dataUsageEvent.ue_ipv4=temp[1];
			else if(temp[0].equals("ipv6")  && currentSection.equals("allocated_ue_address") )
				dataUsageEvent.ue_ipv6=temp[1];
			else if(temp[0].equals("apn_ambr_ul")  )
				dataUsageEvent.apn_ambr_ul=temp[1];
			else if(temp[0].equals("apn_ambr_dl")  )
				dataUsageEvent.apn_ambr_dl=temp[1];
			else if(temp[0].equals("pdn_id")  )
				dataUsageEvent.pdn_id=temp[1];
			else if(temp[0].equals("rule_space")  )
				dataUsageEvent.rule_space=temp[1];
			else if(temp[0].equals("imsi")  )
				dataUsageEvent.imsi=temp[1];
			else if(temp[0].equals("imsi_validation")  )
				dataUsageEvent.imsi_validation=temp[1];
			else if(temp[0].equals("imeisv")  )
				dataUsageEvent.imeisv=temp[1];
			else if(temp[0].equals("mcc")  )
				dataUsageEvent.mcc=temp[1];
			else if(temp[0].equals("mnc"))
				dataUsageEvent.mnc=temp[1];
			else if(temp[0].equals("tac"))
				dataUsageEvent.tac=temp[1];
			else if(temp[0].equals("eci"))
				dataUsageEvent.eci=temp[1];
			else if(temp[0].equals("lac"))
				dataUsageEvent.lac=temp[1];
			else if(temp[0].equals("rac"))
				dataUsageEvent.rac=temp[1];
			else if(temp[0].equals("ci"))
				dataUsageEvent.ci=temp[1];
			else if(temp[0].equals("sac"))
				dataUsageEvent.sac=temp[1];
			else if(temp[0].equals("bearer_usage_info:"))
				{ int lines=0;
				if(firstBearer) lines=9;
				else lines=8;
				
				for(int j=0;j<lines;j++){
				currentLine = bufferedReader.readLine();
				//System.out.println(currentLine);
				temp = currentLine.trim().split("=");
				
				temp[0]=temp[0].trim();
				if(temp.length==2) temp[1]=temp[1].trim();
				
				if(temp[0].equals("eps_bearer_id") )
					eps_bearer_id=temp[1];
				else if(temp[0].equals("bearer_cause") )
					bearer_cause=temp[1];
				else if(temp[0].equals("bearer_ul_packets") ) 
					bearer_ul_packets=temp[1];
				else if(temp[0].equals("bearer_dl_packets") )
					bearer_dl_packets=temp[1];
				else if(temp[0].equals("bearer_ul_bytes") ) 
					bearer_ul_bytes=temp[1];
				else if(temp[0].equals("bearer_dl_bytes") ) 
					bearer_dl_bytes=temp[1];
					
				}
				if(!firstBearer)
				bearerStruct=bearerStruct+ "|"+eps_bearer_id+";"+bearer_cause+";"+bearer_ul_packets+";"+bearer_dl_packets+";"+bearer_ul_bytes+";"+bearer_dl_bytes;
				else 
					{bearerStruct=eps_bearer_id+";"+bearer_cause+";"+bearer_ul_packets+";"+bearer_dl_packets+";"+bearer_ul_bytes+";"+bearer_dl_bytes;
					 firstBearer=false;
					}
				}
			
			else if(temp[0].equals("service_usage_info:"))
			{
				int lines=0;
				if(firstSession) lines=9;
				else lines=8;
			for(int j=0;j<lines;j++){
			currentLine = bufferedReader.readLine();
			//System.out.println(currentLine);
			temp = currentLine.trim().split("=");
			
			temp[0]=temp[0].trim();
			if(temp.length==2) temp[1]=temp[1].trim();
			
			if(temp[0].equals("rating_group") )
				rating_group=temp[1];
			else if(temp[0].equals("service_identifier") )
				service_identifier=temp[1];
			else if(temp[0].equals("uri_name") ) 
					{if(temp.length==2)	uri_name=temp[1];}
			else if(temp[0].equals("uri_id") )
				uri_id=temp[1];
			else if(temp[0].equals("service_ul_bytes") ) 
				service_ul_bytes=temp[1];
			else if(temp[0].equals("service_dl_bytes") ) 
				service_dl_bytes=temp[1];
				
			}
			if(!firstSession)
			sessionStruct=sessionStruct+ "|"+rating_group+";"+service_identifier+";"+uri_name+";"+uri_id+";"+service_ul_bytes+";"+service_dl_bytes;
			else 
				{sessionStruct=rating_group+";"+service_identifier+";"+uri_name+";"+uri_id+";"+service_ul_bytes+";"+service_dl_bytes;
				firstSession=false;
				}
			
			}
			} catch (IOException e) {
			
				e.printStackTrace();  }
		
		}
		
		dataUsageEvent.bearer_usage_info="["+bearerStruct+"]";
		dataUsageEvent.service_usage_info="["+sessionStruct+"]";
		
		String csv=dataUsageEvent.createCSVRecord();
		
		return csv;
		//System.out.println(csv);
		
		
	}


	


	private static String processSessionInfoEvent(BufferedReader bufferedReader) {
		// TODO Auto-generated method stub
		
		SessionInfoEvent sessInfoEvent= new SessionInfoEvent();
		String temp[] = new String[2];
		String currentSection="";
		for (int i=0;i<33;i++){
		String currentLine;
		try {
			currentLine = bufferedReader.readLine();
			//System.out.println(currentLine);
			temp = currentLine.trim().split("=");
			temp[0]=temp[0].trim();
			if(temp.length==2) temp[1]=temp[1].trim();
			if(temp[0].equals("pgw_address:") )
				currentSection="pgw_address";
			else if(temp[0].equals("allocated_ue_address:") )
				currentSection="allocated_ue_address";
			else if(temp[0].equals("event_result") ) 
				sessInfoEvent.event_result=temp[1].equals("success")?"0":"1";
			else if(temp[0].equals("time_hour") ) 
				sessInfoEvent.time_hour=temp[1];
			else if(temp[0].equals("time_minute") )
				sessInfoEvent.time_minute=temp[1];
			else if(temp[0].equals("time_second") )
				sessInfoEvent.time_second=temp[1];
			else if(temp[0].equals("time_millisecond")  )
				sessInfoEvent.time_millisecond=temp[1];
			else if(temp[0].equals("duration")  )
				sessInfoEvent.duration=temp[1];
			else if(temp[0].equals("default_bearer_id")  )
				sessInfoEvent.default_bearer_id=temp[1];
			else if(temp[0].equals("apn") ) 
				sessInfoEvent.apn=temp[1];
			else if(temp[0].equals("ipv4")  && currentSection.equals("pgw_address") )
				sessInfoEvent.pgw_ipv4=temp[1];
			else if(temp[0].equals("ipv6")  && currentSection.equals("pgw_address") )
				sessInfoEvent.pgw_ipv6=temp[1];
			else if(temp[0].equals("ipv4")  && currentSection.equals("allocated_ue_address") )
				sessInfoEvent.ue_ipv4=temp[1];
			else if(temp[0].equals("ipv6")  && currentSection.equals("allocated_ue_address") )
				sessInfoEvent.ue_ipv6=temp[1];
			else if(temp[0].equals("apn_ambr_ul")  )
				sessInfoEvent.apn_ambr_ul=temp[1];
			else if(temp[0].equals("apn_ambr_dl")  )
				sessInfoEvent.apn_ambr_dl=temp[1];
			else if(temp[0].equals("pdn_id")  )
				sessInfoEvent.pdn_id=temp[1];
			else if(temp[0].equals("rule_space")  )
				sessInfoEvent.rule_space=temp[1];
			else if(temp[0].equals("imsi")  )
				sessInfoEvent.imsi=temp[1];
			else if(temp[0].equals("imsi_validation")  )
				sessInfoEvent.imsi_validation=temp[1];
			else if(temp[0].equals("imeisv")  )
				sessInfoEvent.imeisv=temp[1];
			else if(temp[0].equals("mcc")  )
				sessInfoEvent.mcc=temp[1];
			else if(temp[0].equals("mnc")  )
				sessInfoEvent.mnc=temp[1];
			else if(temp[0].equals("tac")  )
				sessInfoEvent.tac=temp[1];
			else if(temp[0].equals("eci")  )
				sessInfoEvent.eci=temp[1];
			else if(temp[0].equals("lac")  )
				sessInfoEvent.lac=temp[1];
			else if(temp[0].equals("rac")  )
				sessInfoEvent.rac=temp[1];
			else if(temp[0].equals("ci")  )
				sessInfoEvent.ci=temp[1];
			else if(temp[0].equals("sac")  )
				sessInfoEvent.sac=temp[1];
			} catch (IOException e) {
			
				e.printStackTrace();  }
		
		}
		String csv=sessInfoEvent.createCSVRecord();
		return csv;
		//System.out.println(csv);
		
	}


	private static String processStreamHeader(BufferedReader bufferedReader)  {
		// TODO Auto-generated method stub
		
		StreamHeader strHeader= new StreamHeader();
		String temp[] = new String[2];
		for (int i=0;i<7;i++){
		String currentLine;
		try {
			currentLine = bufferedReader.readLine();
			//System.out.println(currentLine);
			temp = currentLine.trim().split("=");
			temp[0]=temp[0].trim();
			temp[1]=temp[1].trim();
			if(temp[0].equals("date") ) 
				strHeader.date=temp[1];
			else if(temp[0].equals("UTC time") ) 
				strHeader.utcTime=temp[1];
			else if(temp[0].equals("UTC offset") )
				strHeader.utcOffset=temp[1];
			else if(temp[0].equals("File format version") )
				strHeader.fileFrmtVrsn=temp[1];
			else if(temp[0].equals("File information version")  )
				strHeader.fileInfoVrsn=temp[1];
			else if(temp[0].equals("nodeid")  )
				strHeader.nodeid=temp[1];
			else if(temp[0].equals("cause_header")  )
				strHeader.cause_header=temp[1];
			} catch (IOException e) {
				e.printStackTrace();  }
		
		}
		String csv=strHeader.createCSVRecord();
		return csv;
		//System.out.println(csv);
		
		
	}


	private static void processFileHeader(BufferedReader bufferedReader)  {
		// TODO Auto-generated method stub
		try {
			String currentLine=  bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
