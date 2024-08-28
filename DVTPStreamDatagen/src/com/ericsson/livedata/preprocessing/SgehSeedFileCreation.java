package com.ericsson.livedata.preprocessing;
import java.io.*;

class Header{
	public String date;
	public String time;
	public String FFV;
	public String FIV;
	
	public String createCSVRecord(){
		String record= date+","+time+","+FFV+","+FIV;
		record=record.replace("undefined", "");
		record=record.replace("null", "");
		//System.out.println(record);
		
		return record;
	}
}

class AttachEvent{
	static final String recType="1";
	static final String event_id="0";
	public String event_result;
	public String time_hour;
	public String time_minute;
	public String time_second;
	public String attach_type;
	public String rat;
	public String cause_code;
	public String sub_cause_code;
	public String mcc;
	public String mnc;
	public String lac;
	public String rac;
	public String sac;
	public String imsi;
	public String ptmsi;
	public String imeisv;
	public String hlr;
	public String msisdn;
	public String cause_prot_type;
	public String time_millisecond;
	public String duration;
	public String request_retries;
	
	public String createCSVRecord(){
		String record= recType+","+event_id+","+event_result+","+time_hour+","+time_minute+","+time_second+","+attach_type+","+rat+","+cause_code+","+sub_cause_code+","+mcc+","+mnc+","+lac+","+rac+","+sac+","+imsi+","+ptmsi+","+imeisv+","+hlr+","+msisdn+","+cause_prot_type+","+","+time_millisecond+","+duration+","+request_retries;
		record=record.replace("undefined", "");
		record=record.replace("null", "");
		//System.out.println(record);
		
		return record;
	}
}


class ActivateEvent{
	static final String recType="1";
	static final String event_id="1";
	public String event_result;
	public String time_hour;
	public String time_minute;
	public String time_second;
	public String activation_type;
	public String rat;
	public String cause_prot_type;
	public String cause_code;
	public String sub_cause_code;
	public String mcc;
	public String mnc;
	public String lac;
	public String rac;
	public String ci;
	public String sac;
	public String imsi;
	public String imeisv;
	public String ggsn;
	public String apn;
	public String homezone_identity;
	public String msisdn;
	public String nsapi;
  //  public String linked_nsapi;
	public String ms_address_ipv4;
	public String ms_address_ipv6;
	public String time_millisecond;
	public String duration;
	public String request_retries;
	public String ptmsi;
	public String ms_requested_apn;
	
	public String qos_requested_reliability_class;
	public String qos_requested_delay_class;
	public String qos_requested_precedence_class;
	public String qos_requested_peak_throughput;
	public String qos_requested_mean_throughput;
	public String qos_requested_delivery_of_err_sdu;
	public String qos_requested_delivery_order;
	public String qos_requested_traffic_class;
	public String qos_requested_max_sdu_size;
	public String qos_requested_mbr_ul;
	public String qos_requested_mbr_dl;
	public String qos_requested_sdu_error_ratio;
	public String qos_requested_residual_ber;
	public String qos_requested_traffic_handling_priority;
	public String qos_requested_transfer_delay;
	public String qos_requested_gbr_ul;
	public String qos_requested_gbr_dl;
	public String qos_requested_source_statistics_descriptor;
	public String qos_requested_signalling_indication;
	
	public String qos_negotiated_reliability_class;
	public String qos_negotiated_delay_class;
	public String qos_negotiated_precedence_class;
	public String qos_negotiated_peak_throughput;
	public String qos_negotiated_mean_throughput;
	public String qos_negotiated_delivery_of_err_sdu;
	public String qos_negotiated_delivery_order;
	public String qos_negotiated_traffic_class;
	public String qos_negotiated_max_sdu_size;
	public String qos_negotiated_mbr_ul;
	public String qos_negotiated_mbr_dl;
	public String qos_negotiated_sdu_error_ratio;
	public String qos_negotiated_residual_ber;
	public String qos_negotiated_traffic_handling_priority;
	public String qos_negotiated_transfer_delay;
	public String qos_negotiated_gbr_ul;
	public String qos_negotiated_gbr_dl;
	public String qos_negotiated_source_statistics_descriptor;
	public String qos_negotiated_signalling_indication;
	public String qos_negotiated_arp_pl;
	public String qos_negotiated_arp_pci;
	public String qos_negotiated_arp_pvi;
	
//	public String qos_requested;
//	public String qos_negotiated;

	public String createCSVRecord(){
		String record= recType+","+event_id+","+event_result+","+time_hour+","+time_minute+","+time_second+","+activation_type+","+rat+","+cause_prot_type+","+cause_code+","+sub_cause_code+
			","+mcc+","+mnc+","+lac+","+rac+","+ci+","+sac+","+imsi+","+imeisv+","+ggsn+","+apn+","+homezone_identity+","+msisdn+","+nsapi+","+/*linked_nsapi+","+*/ms_address_ipv4+
		    ","+ms_address_ipv6+","+time_millisecond+","+duration+","+request_retries+","+ptmsi+","+ms_requested_apn+","+qos_requested_reliability_class+","+qos_requested_delay_class+","+qos_requested_precedence_class+","+qos_requested_peak_throughput+","+
	           qos_requested_mean_throughput+","+qos_requested_delivery_of_err_sdu+","+qos_requested_delivery_order+","+qos_requested_traffic_class+","+
	           qos_requested_max_sdu_size+","+qos_requested_mbr_ul+","+qos_requested_mbr_dl+","+qos_requested_sdu_error_ratio+","+qos_requested_residual_ber+","+
	           qos_requested_traffic_handling_priority+","+qos_requested_transfer_delay+","+qos_requested_gbr_ul+","+qos_requested_gbr_dl+","+
	           qos_requested_source_statistics_descriptor+","+qos_requested_signalling_indication+","+qos_negotiated_reliability_class+","+qos_negotiated_delay_class+","+qos_negotiated_precedence_class+","+qos_negotiated_peak_throughput+","+
				qos_negotiated_mean_throughput+","+qos_negotiated_delivery_of_err_sdu+","+qos_negotiated_delivery_order+","+qos_negotiated_traffic_class+","+
		           qos_negotiated_max_sdu_size+","+qos_negotiated_mbr_ul+","+qos_negotiated_mbr_dl+","+qos_negotiated_sdu_error_ratio+","+qos_negotiated_residual_ber+","+
		           qos_negotiated_traffic_handling_priority+","+qos_negotiated_transfer_delay+","+qos_negotiated_gbr_ul+","+qos_negotiated_gbr_dl+","+
		           qos_negotiated_source_statistics_descriptor+","+qos_negotiated_signalling_indication+","+qos_negotiated_arp_pl+","+qos_negotiated_arp_pci+","+qos_negotiated_arp_pvi;
		
       record=record.replace("undefined", "");
       record=record.replace("null", "");
     //System.out.println(record);

     return record;
	}
}


class DeactivateEvent{
	static final String recType="1";
	static final String event_id="4";
	
	public String event_result;
	public String time_hour;
	public String time_minute;
	public String time_second;
	public String rat;
	public String deactivation_trigger;
	public String cause_code;
	public String sub_cause_code;
	public String mcc;
	public String mnc;
	public String lac;
	public String rac;
	public String sac;
	public String ci;
	public String imsi;
	public String imeisv;
	public String ggsn;
	public String apn;
	public String homezone_identity;
	public String msisdn;
	public String nsapi;
	public String ms_address_ipv4;
	public String ms_address_ipv6;
	public String cause_prot_type;
	public String time_millisecond;
	public String duration;
	public String ptmsi;
	public String createCSVRecord(){
	  String record= recType+","+event_id+","+event_result+","+time_hour+","+time_minute+","+time_second+","+rat+","+deactivation_trigger+","+cause_code+","+sub_cause_code+","+mcc+
				","+mnc+","+lac+","+rac+","+ci+","+sac+","+imsi+","+imeisv+","+ggsn+","+apn+","+homezone_identity+","+msisdn+","+nsapi+","+ms_address_ipv4+","+ms_address_ipv6+
				","+cause_prot_type+","+time_millisecond+","+duration+","+ptmsi;
   record=record.replace("undefined", "");
   record=record.replace("null", "");
   //System.out.println(record);

   return record;
	}
}


class DetachEvent{
	static final String recType="1";
	static final String event_id="14";
	
	public String event_result;
	public String time_hour;
	public String time_minute;
	public String time_second;
	public String time_millisecond;
	public String duration;
	public String rat;
	public String detach_trigger;
	public String detach_type;
	public String cause_prot_type;
	public String cause_code;
	public String sub_cause_code;
	public String mcc;
	public String mnc;
	public String lac;
	public String rac;
	public String sac;
	public String imsi;
	public String ptmsi;
	public String imeisv;
	public String msisdn;
	public String hlr;
	
	public String createCSVRecord(){
		String record= recType+","+event_id+","+event_result+","+time_hour+","+time_minute+","+time_second+","+time_millisecond+","+duration+","+rat+","+detach_trigger+","+detach_type+","+cause_prot_type+","+cause_code+","+sub_cause_code+","+mcc+","+mnc+","+lac+","+rac+","+sac+","+imsi+","+ptmsi+","+imeisv+","+msisdn+","+hlr;
		record=record.replace("undefined", "");
		record=record.replace("null", "");
		//System.out.println(record);
		
		return record;
	}
}



public class SgehSeedFileCreation {
	
	private static final String SCRIPT_ASCII_OP_FILE = "test.txt";
	private static final String SEED_OP_FILE = "resources/seed/sgehseedfile1.csv";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

			String line = null;
			String record="";
	        try {
	            FileReader fileReader = new FileReader(SCRIPT_ASCII_OP_FILE);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            
	            FileWriter fileWriter = new FileWriter(SEED_OP_FILE);
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	            while((line = bufferedReader.readLine()) != null) {
	               // System.out.println(line + " __");
	                record=processLine(line,bufferedReader);
	                if(record!=null && !record.equals(""))
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
	        System.exit(0);
		}
		
		
		private static String processLine(String line,BufferedReader bufferedReader) {
			String currentRecord;
			String record="";
			
			if(line.contains("###FILE###"))	
				currentRecord="FILE";
			else if(line.contains("###HEADER###"))	
				{currentRecord="HEADER";
				record=processHeader(bufferedReader);
				return null;}
			else if(line.contains("======EVENT======"))	
				{currentRecord="EVENT";
				record=processEvent(bufferedReader);}
			else if(line.contains("###FOOTER###"))	
			{currentRecord="FOOTER";
			processFooter(bufferedReader);
			return null;}
			else if(line.contains("###STATS###"))	
			{currentRecord="STATS";
			processStats(bufferedReader);
			return null;}
			return record;
		}
		
		
		private static String processHeader(BufferedReader bufferedReader){
			Header header= new Header();
			String temp[] = new String[2];
			for (int i=0;i<4;i++){
			String currentLine;
			try {
				currentLine = bufferedReader.readLine();
				//System.out.println(currentLine);
				temp = currentLine.trim().split("=");
				temp[0]=temp[0].trim();
				temp[1]=temp[1].trim();
				if(temp[0].equals("date") ) 
					header.date=temp[1];
				else if(temp[0].equals("time") ) 
					header.time=temp[1];
				else if(temp[0].equals("FFV") )
					header.FFV=temp[1];
				else if(temp[0].equals("FIV") )
					header.FIV=temp[1];
				
				} catch (IOException e) {
					e.printStackTrace();  }
			
			}
			String csv=header.createCSVRecord();
			return csv;
			//System.out.println(csv);
			
		}

		
		private static String processEvent(BufferedReader bufferedReader) {
			String currentLine;
			String record="";
				try {
					currentLine = bufferedReader.readLine();
					if(!currentLine.equals("header:") && !currentLine.equals("l_header:")){
							    System.out.println("error");
						
					}
					else {
						currentLine = bufferedReader.readLine();
						if(currentLine.trim().equals("header:"))  currentLine = bufferedReader.readLine();
						String temp[] = currentLine.trim().split("=");
						temp[0]=temp[0].trim();
						temp[1]=temp[1].trim();
						
						if(temp[0].equals("event_id")  && temp[1].equals("attach") ) 
							record=processAttachEvent(bufferedReader);
						else if(temp[0].equals("event_id")  && temp[1].equals("activate") ) 
							record=processActivateEvent(bufferedReader);
						else if(temp[0].equals("event_id")  && temp[1].equals("deactivate") )
							record=processDeactivateEvent(bufferedReader);
						else if(temp[0].equals("event_id")  && temp[1].equals("detach") ) 
							record=processDetachEvent(bufferedReader);
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return record;
		}

		
		

	private static String processAttachEvent(BufferedReader bufferedReader){
			AttachEvent attachEvent=new AttachEvent();
			String temp[] = new String[2];
			//String currentSection="";
			for (int i=0;i<24;i++){
				String currentLine;
				try {
					currentLine = bufferedReader.readLine();
					//System.out.println(currentLine);
					temp = currentLine.trim().split("=");
					temp[0]=temp[0].trim();
					if(temp.length==2) temp[1]=temp[1].trim();
					
					 if(temp[0].equals("event_result") ) {
							if(temp[1].equals("success"))
								attachEvent.event_result="0";
							else if(temp[1].equals("reject"))
								attachEvent.event_result="1";
							else if(temp[1].equals("abort"))
								attachEvent.event_result="2";
							else if(temp[1].equals("ignore"))
								attachEvent.event_result="3";
						}
						
						else if(temp[0].equals("time_hour") ) 
							attachEvent.time_hour=temp[1];
						else if(temp[0].equals("time_minute") )
							attachEvent.time_minute=temp[1];
						else if(temp[0].equals("time_second") )
							attachEvent.time_second=temp[1];
						else if(temp[0].equals("attach_type")  )
							attachEvent.attach_type=temp[1];
						else if(temp[0].equals("rat")  ){
							if(temp[1].equals("gsm"))
								attachEvent.event_result="0";
							else if(temp[1].equals("wcdma"))
								attachEvent.event_result="1";
						}
					 
							//attachEvent.rat=temp[1];
						else if(temp[0].equals("cause_code")  )
							attachEvent.cause_code=temp[1];
						else if(temp[0].equals("sub_cause_code") ) 
							attachEvent.sub_cause_code=temp[1];
						else if(temp[0].equals("mcc") ) 
							attachEvent.mcc=temp[1];
						else if(temp[0].equals("mnc") )
							attachEvent.mnc=temp[1];
						else if(temp[0].equals("lac") )
							attachEvent.lac=temp[1];
						else if(temp[0].equals("rac")  )
							attachEvent.rac=temp[1];
						else if(temp[0].equals("sac")  )
							attachEvent.sac=temp[1];
						else if(temp[0].equals("imsi")  )
							attachEvent.imsi=temp[1];
						else if(temp[0].equals("ptmsi")  )
							attachEvent.ptmsi=temp[1];
						else if(temp[0].equals("imeisv") ) 
							attachEvent.imeisv=temp[1];
						else if(temp[0].equals("hlr") )
							attachEvent.hlr=temp[1];
						else if(temp[0].equals("msisdn")  )
							attachEvent.msisdn=temp[1];
						else if(temp[0].equals("cause_prot_type")  )
							attachEvent.cause_prot_type=temp[1];
						else if(temp[0].equals("time_millisecond")  )
							attachEvent.time_millisecond=temp[1];
						else if(temp[0].equals("duration")  )
							attachEvent.duration=temp[1];
						else if(temp[0].equals("request_retries")  )
							attachEvent.request_retries=temp[1];
	
						} catch (IOException e) {
						
							e.printStackTrace();  }
					
					}
					String csv=attachEvent.createCSVRecord();
					return csv;
				// System.out.println(csv);
		}

		
		private static String processActivateEvent(BufferedReader bufferedReader){ 
			 ActivateEvent activateEvent=new  ActivateEvent();
			 String temp[] = new String[2];
				String currentSection="";
			/*	String qos_requested_reliability_class="", qos_requested_delay_class="", qos_requested_precedence_class="", qos_requested_peak_throughput="",
			           qos_requested_mean_throughput="", qos_requested_delivery_of_err_sdu="", qos_requested_delivery_order="", qos_requested_traffic_class="",
			           qos_requested_max_sdu_size="", qos_requested_mbr_ul="", qos_requested_mbr_dl="", qos_requested_sdu_error_ratio="", qos_requested_residual_ber="",
			           qos_requested_traffic_handling_priority="", qos_requested_transfer_delay="", qos_requested_gbr_ul="", qos_requested_gbr_dl="",
			           qos_requested_source_statistics_descriptor="", qos_requested_signalling_indication=""; 
				
				String qos_negotiated_reliability_class="", qos_negotiated_delay_class="", qos_negotiated_precedence_class="", qos_negotiated_peak_throughput="",
					   qos_negotiated_mean_throughput="", qos_negotiated_delivery_of_err_sdu="", qos_negotiated_delivery_order="", qos_negotiated_traffic_class="",
					   qos_negotiated_max_sdu_size="", qos_negotiated_mbr_ul="", qos_negotiated_mbr_dl="", qos_negotiated_sdu_error_ratio="", qos_negotiated_residual_ber="",
					   qos_negotiated_traffic_handling_priority="", qos_negotiated_transfer_delay="", qos_negotiated_gbr_ul="", qos_negotiated_gbr_dl="",
					   qos_negotiated_source_statistics_descriptor="", qos_negotiated_signalling_indication="", qos_negotiated_arp_pl="", qos_negotiated_arp_pci="", qos_negotiated_arp_pvi="";
				
				String qosRequestedStruct="";
				String qosNegotiatedStruct="";     */
			//	boolean firstQosRequested=true;
		    //		boolean firstQosNegotiated=true;
				for (int i=0;;i++){
					String currentLine;
					
					try {
						currentLine = bufferedReader.readLine();
						if (currentLine.equals("") ) 
						{ break;}
						//System.out.println(currentLine);
						temp = currentLine.trim().split("=");
						temp[0]=temp[0].trim();
						if(temp.length==2) temp[1]=temp[1].trim();
						 if(temp[0].equals("event_result") ) {
							if(temp[1].equals("success"))
								activateEvent.event_result="0";
							else if(temp[1].equals("reject"))
								activateEvent.event_result="1";
							else if(temp[1].equals("abort"))
								activateEvent.event_result="2";
							else if(temp[1].equals("ignore"))
								activateEvent.event_result="3";
						}
						else if(temp[0].equals("time_hour") ) 
							activateEvent.time_hour=temp[1];
						else if(temp[0].equals("time_minute") )
							activateEvent.time_minute=temp[1];
						else if(temp[0].equals("time_second") )
							activateEvent.time_second=temp[1];
						else if(temp[0].equals("activation_type") ) 
						{
							if(temp[1].equals("gprs_primary"))
								activateEvent.activation_type="0";
							else if(temp[1].equals("gprs_ms_secondary"))
								activateEvent.activation_type="1";
							else if(temp[1].equals("gprs_nw_secondary"))
								activateEvent.activation_type="2";
						}
						//	activateEvent.activation_type=temp[1];
						else if(temp[0].equals("rat") )
						{
							if(temp[1].equals("gsm"))
								activateEvent.rat="0";
							else if(temp[1].equals("wcdma"))
								activateEvent.rat="1";
						}
							//activateEvent.rat=temp[1];
						else if(temp[0].equals("cause_prot_type") )
						{
							if(temp[1].equals("ril3_cause"))
								activateEvent.cause_prot_type="0";
							else if(temp[1].equals("gtp_cause"))
								activateEvent.cause_prot_type="1";
							else if(temp[1].equals("ril3_cause2"))
								activateEvent.cause_prot_type="2";
							else if(temp[1].equals("gtpv2_cause"))
								activateEvent.cause_prot_type="3";
						}
						//	activateEvent.cause_prot_type=temp[1];
						else if(temp[0].equals("cause_code")  )
							activateEvent.cause_code=temp[1];
						else if(temp[0].equals("sub_cause_code") ) 
							activateEvent.sub_cause_code=temp[1];
						else if(temp[0].equals("mcc") ) 
							activateEvent.mcc=temp[1];
						else if(temp[0].equals("mnc") )
							activateEvent.mnc=temp[1];
						else if(temp[0].equals("lac") )
							activateEvent.lac=temp[1];
						else if(temp[0].equals("rac")  )
							activateEvent.rac=temp[1];
						else if(temp[0].equals("ci")  )
							activateEvent.ci=temp[1];
						else if(temp[0].equals("sac") )
							activateEvent.sac=temp[1];
						else if(temp[0].equals("imsi")  )
							activateEvent.imsi=temp[1];
						else if(temp[0].equals("imeisv") ) 
							activateEvent.imeisv=temp[1];
						else if(temp[0].equals("ggsn") )
							activateEvent.ggsn=temp[1];
						else if(temp[0].equals("apn")  )
							activateEvent.apn=temp[1];
						else if(temp[0].equals("homezone_identity")  )
						{if(temp.length==2)	
							activateEvent.homezone_identity=temp[1];}
							//activateEvent.homezone_identity=temp[1];
						else if(temp[0].equals("msisdn")  )
							activateEvent.msisdn=temp[1];
						else if(temp[0].equals("nsapi") ) 
							activateEvent.nsapi=temp[1];
					//	else if(temp[0].equals("linked_nsapi") ) 
						//	activateEvent.linked_nsapi=temp[1];
						else if(temp[0].equals("ipv4") )
							activateEvent.ms_address_ipv4=temp[1];
						else if(temp[0].equals("ipv6")  )
							activateEvent.ms_address_ipv6=temp[1];
						else if(temp[0].equals("time_millisecond") )
							activateEvent.time_millisecond=temp[1];
						else if(temp[0].equals("duration") )
							activateEvent.duration=temp[1];
						else if(temp[0].equals("request_retries") ) 
							activateEvent.request_retries=temp[1];
						else if(temp[0].equals("ptmsi") )
							activateEvent.ptmsi=temp[1];
						else if(temp[0].equals("ms_requested_apn") )
						{if(temp.length==2)	
							activateEvent.ms_requested_apn=temp[1];
						}
							//activateEvent.ms_requested_apn=temp[1];
				/*		else if(temp[0].equals("qos_requested:"))
						{ 
						for(int j=0;j<19;j++){
							currentLine = bufferedReader.readLine();
							//System.out.println(currentLine);
							temp = currentLine.trim().split("=");
							
							temp[0]=temp[0].trim();
							if(temp.length==2) temp[1]=temp[1].trim();*/
							
						   else	if(temp[0].equals("reliability_class") )
							   activateEvent.qos_requested_reliability_class=temp[1];
							else if(temp[0].equals("delay_class") )
								activateEvent.qos_requested_delay_class=temp[1];
							else if(temp[0].equals("precedence_class") ) 
								activateEvent.qos_requested_precedence_class=temp[1];
							else if(temp[0].equals("peak_throughput") )
								activateEvent.qos_requested_peak_throughput=temp[1];
							else if(temp[0].equals("mean_throughput") ) 
								activateEvent.qos_requested_mean_throughput=temp[1];
							else if(temp[0].equals("delivery_of_err_sdu") ) 
								activateEvent.qos_requested_delivery_of_err_sdu=temp[1];
							else if(temp[0].equals("delivery_order") )
								activateEvent.qos_requested_delivery_order=temp[1];
							else if(temp[0].equals("traffic_class") ) 
								activateEvent.qos_requested_traffic_class=temp[1];
							else if(temp[0].equals("max_sdu_size") )
								activateEvent.qos_requested_max_sdu_size=temp[1];
							else if(temp[0].equals("mbr_ul") ) 
								activateEvent.qos_requested_mbr_ul=temp[1];
							else if(temp[0].equals("mbr_dl") ) 
								activateEvent.qos_requested_mbr_dl=temp[1];
							else if(temp[0].equals("sdu_error_ratio") )
								activateEvent.qos_requested_sdu_error_ratio=temp[1];
							else if(temp[0].equals("residual_ber") ) 
								activateEvent.qos_requested_residual_ber=temp[1];
							else if(temp[0].equals("traffic_handling_priority") )
								activateEvent.qos_requested_traffic_handling_priority=temp[1];
							else if(temp[0].equals("transfer_delay") ) 
								activateEvent.qos_requested_transfer_delay=temp[1];
							else if(temp[0].equals("gbr_ul") ) 
								activateEvent.qos_requested_gbr_ul=temp[1];
							else if(temp[0].equals("gbr_dl") )
								activateEvent.qos_requested_gbr_dl=temp[1];
							else if(temp[0].equals("source_statistics_descriptor") ) 
								activateEvent.qos_requested_source_statistics_descriptor=temp[1];
							else if(temp[0].equals("signalling_indication") )
								activateEvent.qos_requested_signalling_indication=temp[1];
							
					//	}

					/*	if(!firstQosRequested)
						 qosRequestedStruct=qosRequestedStruct+"|"+qos_requested_reliability_class+";"+qos_requested_delay_class+";"+qos_requested_precedence_class+";"+qos_requested_peak_throughput+";"+
				           qos_requested_mean_throughput+";"+qos_requested_delivery_of_err_sdu+";"+qos_requested_delivery_order+";"+qos_requested_traffic_class+";"+
				           qos_requested_max_sdu_size+";"+qos_requested_mbr_ul+";"+qos_requested_mbr_dl+";"+qos_requested_sdu_error_ratio+";"+qos_requested_residual_ber+";"+
				           qos_requested_traffic_handling_priority+";"+qos_requested_transfer_delay+";"+qos_requested_gbr_ul+";"+qos_requested_gbr_dl+";"+
				           qos_requested_source_statistics_descriptor+";"+qos_requested_signalling_indication;
						
						else{
							qosRequestedStruct=qos_requested_reliability_class+";"+qos_requested_delay_class+";"+qos_requested_precedence_class+";"+qos_requested_peak_throughput+";"+
							           qos_requested_mean_throughput+";"+qos_requested_delivery_of_err_sdu+";"+qos_requested_delivery_order+";"+qos_requested_traffic_class+";"+
							           qos_requested_max_sdu_size+";"+qos_requested_mbr_ul+";"+qos_requested_mbr_dl+";"+qos_requested_sdu_error_ratio+";"+qos_requested_residual_ber+";"+
							           qos_requested_traffic_handling_priority+";"+qos_requested_transfer_delay+";"+qos_requested_gbr_ul+";"+qos_requested_gbr_dl+";"+
							           qos_requested_source_statistics_descriptor+";"+qos_requested_signalling_indication;	
							firstQosRequested=false;
						}
						}
						
						else if(temp[0].equals("qos_negotiated:"))
						{ 
						for(int j=0;j<22;j++){
							currentLine = bufferedReader.readLine();
							//System.out.println(currentLine);
							temp = currentLine.trim().split("=");
							
							temp[0]=temp[0].trim();
							if(temp.length==2) temp[1]=temp[1].trim(); */
							
							 if(temp[0].equals("reliability_class") )
								activateEvent.qos_negotiated_reliability_class=temp[1];
							else if(temp[0].equals("delay_class") )
								activateEvent.qos_negotiated_delay_class=temp[1];
							else if(temp[0].equals("precedence_class") ) 
								activateEvent.qos_negotiated_precedence_class=temp[1];
							else if(temp[0].equals("peak_throughput") )
								activateEvent.qos_negotiated_peak_throughput=temp[1];
							else if(temp[0].equals("mean_throughput") ) 
								activateEvent.qos_negotiated_mean_throughput=temp[1];
							else if(temp[0].equals("delivery_of_err_sdu") ) 
								activateEvent.qos_negotiated_delivery_of_err_sdu=temp[1];
							else if(temp[0].equals("delivery_order") )
								activateEvent.qos_negotiated_delivery_order=temp[1];
							else if(temp[0].equals("traffic_class") ) 
								activateEvent.qos_negotiated_traffic_class=temp[1];
							else if(temp[0].equals("max_sdu_size") )
								activateEvent.qos_negotiated_max_sdu_size=temp[1];
							else if(temp[0].equals("mbr_ul") ) 
								activateEvent.qos_negotiated_mbr_ul=temp[1];
							else if(temp[0].equals("mbr_dl") ) 
								activateEvent.qos_negotiated_mbr_dl=temp[1];
							else if(temp[0].equals("sdu_error_ratio") )
								activateEvent.qos_negotiated_sdu_error_ratio=temp[1];
							else if(temp[0].equals("residual_ber") ) 
								activateEvent.qos_negotiated_residual_ber=temp[1];
							else if(temp[0].equals("traffic_handling_priority") )
								activateEvent.qos_negotiated_traffic_handling_priority=temp[1];
							else if(temp[0].equals("transfer_delay") ) 
								activateEvent.qos_negotiated_transfer_delay=temp[1];
							else if(temp[0].equals("gbr_ul") ) 
								activateEvent.qos_negotiated_gbr_ul=temp[1];
							else if(temp[0].equals("gbr_dl") )
								activateEvent.qos_negotiated_gbr_dl=temp[1];
							else if(temp[0].equals("source_statistics_descriptor") ) 
								activateEvent.qos_negotiated_source_statistics_descriptor=temp[1];
							else if(temp[0].equals("signalling_indication") )
								activateEvent.qos_negotiated_signalling_indication=temp[1];
							else if(temp[0].equals("arp_pl") )
								activateEvent.qos_negotiated_arp_pl=temp[1];
							else if(temp[0].equals("arp_pci") ) 
							{
								if(temp[1].equals("enabled"))
									activateEvent.qos_negotiated_arp_pci="0";
								else if(temp[1].equals("disabled"))
									activateEvent.qos_negotiated_arp_pci="1";
							}
							//	qos_negotiated_arp_pci=temp[1];
							else if(temp[0].equals("arp_pvi") )
							{
								if(temp[1].equals("enabled"))
									activateEvent.qos_negotiated_arp_pvi="0";
								else if(temp[1].equals("disabled"))
									activateEvent.qos_negotiated_arp_pvi="1";
							}
							//	qos_negotiated_arp_pvi=temp[1];
							
					//	}
						
				/*		if(!firstQosNegotiated)
						 qosNegotiatedStruct=qosNegotiatedStruct+"|"+qos_negotiated_reliability_class+";"+qos_negotiated_delay_class+";"+qos_negotiated_precedence_class+";"+qos_negotiated_peak_throughput+";"+
								 qos_negotiated_mean_throughput+";"+qos_negotiated_delivery_of_err_sdu+";"+qos_negotiated_delivery_order+";"+qos_negotiated_traffic_class+";"+
								 qos_negotiated_max_sdu_size+";"+qos_negotiated_mbr_ul+";"+qos_negotiated_mbr_dl+";"+qos_negotiated_sdu_error_ratio+";"+qos_negotiated_residual_ber+";"+
								 qos_negotiated_traffic_handling_priority+";"+qos_negotiated_transfer_delay+";"+qos_negotiated_gbr_ul+";"+qos_negotiated_gbr_dl+";"+
								 qos_negotiated_source_statistics_descriptor+";"+qos_negotiated_signalling_indication+";"+qos_negotiated_arp_pl+";"+qos_negotiated_arp_pci+";"+qos_negotiated_arp_pvi;
						
						else{
							qosNegotiatedStruct=qos_negotiated_reliability_class+";"+qos_negotiated_delay_class+";"+qos_negotiated_precedence_class+";"+qos_negotiated_peak_throughput+";"+
									qos_negotiated_mean_throughput+";"+qos_negotiated_delivery_of_err_sdu+";"+qos_negotiated_delivery_order+";"+qos_negotiated_traffic_class+";"+
							           qos_negotiated_max_sdu_size+";"+qos_negotiated_mbr_ul+";"+qos_negotiated_mbr_dl+";"+qos_negotiated_sdu_error_ratio+";"+qos_negotiated_residual_ber+";"+
							           qos_negotiated_traffic_handling_priority+";"+qos_negotiated_transfer_delay+";"+qos_negotiated_gbr_ul+";"+qos_negotiated_gbr_dl+";"+
							           qos_negotiated_source_statistics_descriptor+";"+qos_negotiated_signalling_indication+";"+qos_negotiated_arp_pl+";"+qos_negotiated_arp_pci+";"+qos_negotiated_arp_pvi;	
							firstQosNegotiated=false;
						}
						*/
					//}
				}catch (IOException e) {
						
						e.printStackTrace(); 
						}
		}
				
			//	activateEvent.qos_requested="["+qosRequestedStruct+"]";
			//	activateEvent.qos_negotiated="["+qosNegotiatedStruct+"]";
				String csv=activateEvent.createCSVRecord();
				
				return csv;
				//System.out.println(csv);
		}	
     

		
		
	
		private static String processDeactivateEvent(BufferedReader bufferedReader) {
			DeactivateEvent deactivateEvent=new DeactivateEvent();
			String temp[] = new String[2];
			//String currentSection="";
			for (int i=0;i<28;i++){
				String currentLine;
				try {
					currentLine = bufferedReader.readLine();
					//System.out.println(currentLine+"--");
					temp = currentLine.trim().split("=");
					temp[0]=temp[0].trim();
					if(temp.length==2) temp[1]=temp[1].trim();
					 if(temp[0].equals("event_result") ) {
						if(temp[1].equals("success"))
							deactivateEvent.event_result="0";
						else if(temp[1].equals("reject"))
							deactivateEvent.event_result="1";
						else if(temp[1].equals("abort"))
							deactivateEvent.event_result="2";
						else if(temp[1].equals("ignore"))
							deactivateEvent.event_result="3";
					}
					
					else if(temp[0].equals("time_hour") ) 
						deactivateEvent.time_hour=temp[1];
					else if(temp[0].equals("time_minute") )
						deactivateEvent.time_minute=temp[1];
					else if(temp[0].equals("time_second") )
						deactivateEvent.time_second=temp[1];
					else if(temp[0].equals("rat")  )
					{
						if(temp[1].equals("gsm"))
							deactivateEvent.rat="0";
						else if(temp[1].equals("wcdma"))
							deactivateEvent.rat="1";
					}
						//deactivateEvent.rat=temp[1];
					else if(temp[0].equals("deactivation_trigger")  )
					{
						if(temp[1].equals("sgsn"))
							deactivateEvent.deactivation_trigger="0";
						else if(temp[1].equals("ms"))
							deactivateEvent.deactivation_trigger="1";
						else if(temp[1].equals("ggsn"))
							deactivateEvent.deactivation_trigger="2";
						else if(temp[1].equals("camel"))
							deactivateEvent.deactivation_trigger="3";
						else if(temp[1].equals("hlr_or_hss"))
							deactivateEvent.deactivation_trigger="4";
						else if(temp[1].equals("rnc"))
							deactivateEvent.deactivation_trigger="5";
						else if(temp[1].equals("sgw"))
							deactivateEvent.deactivation_trigger="6";
					}
						//deactivateEvent.deactivation_trigger=temp[1];
					else if(temp[0].equals("cause_code")  )
						deactivateEvent.cause_code=temp[1];
					else if(temp[0].equals("sub_cause_code") ) 
						deactivateEvent.sub_cause_code=temp[1];
					else if(temp[0].equals("mcc") ) 
						deactivateEvent.mcc=temp[1];
					else if(temp[0].equals("mnc") )
						deactivateEvent.mnc=temp[1];
					else if(temp[0].equals("lac") )
						deactivateEvent.lac=temp[1];
					else if(temp[0].equals("rac")  )
						deactivateEvent.rac=temp[1];
					else if(temp[0].equals("ci") )
						deactivateEvent.ci=temp[1];
					else if(temp[0].equals("sac") )
						deactivateEvent.sac=temp[1];
					else if(temp[0].equals("imsi")  )
						deactivateEvent.imsi=temp[1];
					else if(temp[0].equals("imeisv") ) 
						deactivateEvent.imeisv=temp[1];
					else if(temp[0].equals("ggsn") )
						deactivateEvent.ggsn=temp[1];
					else if(temp[0].equals("apn")  )
						deactivateEvent.apn=temp[1];
					else if(temp[0].equals("homezone_identity")  )
					{if(temp.length==2)	
						deactivateEvent.homezone_identity=temp[1];}
						//deactivateEvent.homezone_identity=temp[1];
					else if(temp[0].equals("msisdn")  )
						deactivateEvent.msisdn=temp[1];
					else if(temp[0].equals("nsapi") ) 
						deactivateEvent.nsapi=temp[1];
					else if(temp[0].equals("ipv4") )
						deactivateEvent.ms_address_ipv4=temp[1];
					else if(temp[0].equals("ipv6") )
						deactivateEvent.ms_address_ipv6=temp[1];
					else if(temp[0].equals("cause_prot_type")  )
					{
						if(temp[1].equals("ril3_cause"))
							deactivateEvent.cause_prot_type="0";
						else if(temp[1].equals("gtp_cause"))
							deactivateEvent.cause_prot_type="1";
						else if(temp[1].equals("ril3_cause2"))
							deactivateEvent.cause_prot_type="2";
						else if(temp[1].equals("gtpv2_cause"))
							deactivateEvent.cause_prot_type="3";
					}
					//	deactivateEvent.cause_prot_type=temp[1];
					else if(temp[0].equals("time_millisecond")  )
						deactivateEvent.time_millisecond=temp[1];
					else if(temp[0].equals("duration")  )
						deactivateEvent.duration=temp[1];
					else if(temp[0].equals("ptmsi")  )
						deactivateEvent.ptmsi=temp[1];
					} catch (IOException e) {
					
						e.printStackTrace();  }
				
				}
				String csv=deactivateEvent.createCSVRecord();
				return csv;
			// System.out.println(csv);
		}
		
		

 private static String processDetachEvent(BufferedReader bufferedReader){
	  DetachEvent detachEvent=new DetachEvent();
		String temp[] = new String[2];
		//String currentSection="";
		for (int i=0;i<26;i++){
			String currentLine;
			try {
				currentLine = bufferedReader.readLine();
				//System.out.println(currentLine);
				temp = currentLine.trim().split("=");
				temp[0]=temp[0].trim();
				if(temp.length==2) temp[1]=temp[1].trim();
				
				 if(temp[0].equals("event_result") ) {
						if(temp[1].equals("success"))
							 detachEvent.event_result="0";
						else if(temp[1].equals("reject"))
							 detachEvent.event_result="1";
						else if(temp[1].equals("abort"))
							 detachEvent.event_result="2";
						else if(temp[1].equals("ignore"))
							 detachEvent.event_result="3";
					}
					
					else if(temp[0].equals("time_hour") ) 
						 detachEvent.time_hour=temp[1];
					else if(temp[0].equals("time_minute") )
						 detachEvent.time_minute=temp[1];
					else if(temp[0].equals("time_second") )
						 detachEvent.time_second=temp[1];
					else if(temp[0].equals("time_millisecond")  )
						 detachEvent.time_millisecond=temp[1];
					else if(temp[0].equals("duration")  )
						 detachEvent.duration=temp[1];
					else if(temp[0].equals("rat")  )
						 detachEvent.rat=temp[1];
					else if(temp[0].equals("detach_trigger")  )
						 detachEvent.detach_trigger=temp[1];
					else if(temp[0].equals("detach_type")  )
						detachEvent.detach_type=temp[1];
					else if(temp[0].equals("cause_prot_type")  )
						detachEvent.cause_prot_type=temp[1];
					else if(temp[0].equals("cause_code")  )
						detachEvent.cause_code=temp[1];
					else if(temp[0].equals("sub_cause_code") ) 
						detachEvent.sub_cause_code=temp[1];
					else if(temp[0].equals("mcc") ) 
						detachEvent.mcc=temp[1];
					else if(temp[0].equals("mnc") )
						detachEvent.mnc=temp[1];
					else if(temp[0].equals("lac") )
						detachEvent.lac=temp[1];
					else if(temp[0].equals("rac")  )
						detachEvent.rac=temp[1];
					else if(temp[0].equals("sac")  )
						detachEvent.sac=temp[1];
					else if(temp[0].equals("imsi")  )
						detachEvent.imsi=temp[1];
					else if(temp[0].equals("ptmsi")  )
						detachEvent.ptmsi=temp[1];
					else if(temp[0].equals("imeisv") ) 
						detachEvent.imeisv=temp[1];
					else if(temp[0].equals("msisdn")  )
						detachEvent.msisdn=temp[1];
					else if(temp[0].equals("hlr") )
						detachEvent.hlr=temp[1];
					
					} catch (IOException e) {
					
						e.printStackTrace();  }
				
				}
				String csv=detachEvent.createCSVRecord();
				return csv;
			// System.out.println(csv);
		}

	
    	private static void processFooter(BufferedReader bufferedReader){
    		try {
				String currentLine=  bufferedReader.readLine();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	private static void processStats(BufferedReader bufferedReader){
    		try {
				String currentLine=  bufferedReader.readLine();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

}



