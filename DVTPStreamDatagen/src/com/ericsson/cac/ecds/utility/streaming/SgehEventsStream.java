package com.ericsson.cac.ecds.utility.streaming;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.sgw.revision_a.event.Activate;
import com.ericsson.cac.ecds.sgw.revision_a.event.DataUsage;
import com.ericsson.cac.ecds.sgw.revision_a.event.Deactivate;
import com.ericsson.cac.ecds.sgw.revision_a.event.SessionInfo;
import com.ericsson.cac.ecds.utility.Main;

public class SgehEventsStream extends EcdsEventsStreamUtilityCommon
implements EcdsEventsStreamUtilityInterface{

	
	
	
	public List processSeedString(String seedString, String version)
    {
        List<byte[]> eventsStream = new ArrayList<byte[]>();
        String elements[];
        int recordType;
        elements = seedString.split(",",-1);
        for(int i = 0; i < elements.length; i++)
            elements[i] = elements[i].trim();

        try
        {
            recordType = Integer.parseInt(elements[0]);
        }
        catch(NumberFormatException e)
        {
            log.error((new StringBuilder("Failed to parse \"")).append(elements[0]).append("\" into an integer. seed string:").append(seedString).toString());
            return eventsStream;
        }
        try
        {
            switch(recordType)
            {
            case 0: // '\0'
                eventsStream.add(processHeaderRecord(elements, 1, "SeedString", version));
                break;

            case 1: // '\001'
                eventsStream.add(processEventRecord(elements, 1, "SeedString", version));
                break;

            case 3: // '\003'
                eventsStream.add(processFooterRecord(elements, 1, "SeedString", version));
                break;

            case 5: // '\005'
                if(version == Main.SGW_A_VERSION)
                    eventsStream.add(processErrorRecord(elements, 1, "SeedString", version));
                else
                    eventsStream.add(processFooterRecord(elements, 1, "SeedString", version));
                break;

            case 2: // '\002'
            case 4: // '\004'
            default:
                log.error((new StringBuilder("Unknown record type ")).append(recordType).append(" on seeString, ignoring").toString());
                break;
            }
        }
        catch(Exception e)
        {
            log.error(e);
        }
        return eventsStream;
    }
	
	
	private byte[] activateEvent(String[] elements, int lineNo, String file,int eventId, String version) 
    {
    	String eventName = "ACTIVATE";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGEH Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        
        try
        {
        	setParam(p, elements, "HEADER_RECORD_TYPE", 0, lineNo, file, eventId);
    		setParam(p, elements, "HEADER_EVENT_ID", 1, lineNo, file, eventId);
    		setParam(p, elements, "HEADER_EVENT_RESULT", 2, lineNo, file, eventId);
            setParam(p, elements, "HEADER_TIME_HOUR", 3, lineNo, file, eventId);
            setParam(p, elements, "HEADER_TIME_MINUTE", 4, lineNo, file, eventId);
            setParam(p, elements, "HEADER_TIME_SECOND", 5, lineNo, file, eventId);
            setParam(p, elements, "ACTIVATION_TYPE", 6, lineNo, file, eventId);
            setParam(p, elements, "RAT", 7, lineNo, file, eventId);
    		setParam(p, elements, "CAUSE_PROT_TYPE", 8, lineNo, file, eventId);
            setParam(p, elements, "CAUSE_CODE", 9, lineNo, file, eventId);
    		setParam(p, elements, "SUB_CAUSE_CODE", 10, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_MCC", 11, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_MNC", 12, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_LAC", 13, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_RAC", 14, lineNo, file, eventId);
    		setParam(p, elements, "LOCATION_CI", 15, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_SAC", 16, lineNo, file, eventId);
    		setParam(p, elements, "IMSI", 17, lineNo, file, eventId);
            setParam(p, elements, "IMEISV", 18, lineNo, file, eventId);
            setParam(p, elements, "GGSN", 19, lineNo, file, eventId);
            setParam(p, elements, "APN", 20, lineNo, file, eventId);
            setParam(p, elements, "HOMEZONE_IDENTITY", 21, lineNo, file, eventId);
            setParam(p, elements, "MSISDN", 22, lineNo, file, eventId);
            setParam(p, elements, "NSAPI", 23, lineNo, file, eventId);
            setParam(p, elements, "MS_ADDRESS_IPV4", 24, lineNo, file, eventId);
            setParam(p, elements, "MS_ADDRESS_IPV6", 25, lineNo, file, eventId);
            setParam(p, elements, "TIME_MILLISECOND", 26, lineNo, file, eventId);
            setParam(p, elements, "DURATION", 27, lineNo, file, eventId);
            setParam(p, elements, "REQUEST_RETRIES", 28, lineNo, file, eventId);
            setParam(p, elements, "PTMSI", 29, lineNo, file, eventId);
    		setParam(p, elements, "MS_REQUESTED_APN", 30, lineNo, file, eventId);
    		
    		setParam(p, elements, "QOS_REQ_RELIABILITY_CLASS", 31, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_DELAY_CLASS", 32, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_PRECEDENCE_CLASS", 33, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_PEAK_THROUGHPUT", 34, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_MEAN_THROUGHPUT", 35, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_DELIVERY_OF_ERR_SDU", 36, lineNo, file, eventId);
    		setParam(p, elements, "QOS_REQ_DELIVERY_ORDER", 37, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_TRAFFIC_CLASS", 38, lineNo, file, eventId);
    		setParam(p, elements, "QOS_REQ_MAX_SDU_SIZE", 39, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_MBR_UL", 40, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_MBR_DL", 41, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_SDU_ERROR_RATIO", 42, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_RESIDUAL_BER", 43, lineNo, file, eventId);
    		setParam(p, elements, "QOS_REQ_TRAFFIC_HANDLING_PRIORITY", 44, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_TRANSFER_DELAY", 45, lineNo, file, eventId);
    		setParam(p, elements, "QOS_REQ_GBR_UL", 46, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_GBR_DL", 47, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_SOURCE_STATISTICS_DESCRIPTOR", 48, lineNo, file, eventId);
            setParam(p, elements, "QOS_REQ_SIGNALLING_INDICATION", 49, lineNo, file, eventId);
            
            setParam(p, elements, "QOS_NEG_RELIABILITY_CLASS", 50, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_DELAY_CLASS", 51, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_PRECEDENCE_CLASS", 52, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_PEAK_THROUGHPUT", 53, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_MEAN_THROUGHPUT", 54, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_DELIVERY_OF_ERR_SDU", 55, lineNo, file, eventId);
    		setParam(p, elements, "QOS_NEG_DELIVERY_ORDER", 56, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_TRAFFIC_CLASS", 57, lineNo, file, eventId);
    		setParam(p, elements, "QOS_NEG_MAX_SDU_SIZE", 58, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_MBR_UL", 59, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_MBR_DL", 60, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_SDU_ERROR_RATIO", 61, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_RESIDUAL_BER", 62, lineNo, file, eventId);
    		setParam(p, elements, "QOS_NEG_TRAFFIC_HANDLING_PRIORITY", 63, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_TRANSFER_DELAY", 64, lineNo, file, eventId);
    		setParam(p, elements, "QOS_NEG_GBR_UL", 65, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_GBR_DL", 66, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_SOURCE_STATISTICS_DESCRIPTOR", 67, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_SIGNALLING_INDICATION", 68, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_ARP_PL", 69, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_ARP_PCI", 70, lineNo, file, eventId);
            setParam(p, elements, "QOS_NEG_ARP_PVI", 71, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new Activate(p);
            eventBA = event.encode();
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return eventBA;
        }
        return eventBA;
	}
	
	private byte[] deactivateEvent(String[] elements, int lineNo, String file,int eventId, String version) 
    {
    	String eventName = "DEACTIVATE";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGEH Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        
        try
        {
        	setParam(p, elements, "HEADER_RECORD_TYPE", 0, lineNo, file, eventId);
    		setParam(p, elements, "HEADER_EVENT_ID", 1, lineNo, file, eventId);
    		setParam(p, elements, "HEADER_EVENT_RESULT", 2, lineNo, file, eventId);
            setParam(p, elements, "HEADER_TIME_HOUR", 3, lineNo, file, eventId);
            setParam(p, elements, "HEADER_TIME_MINUTE", 4, lineNo, file, eventId);
            setParam(p, elements, "HEADER_TIME_SECOND", 5, lineNo, file, eventId);
            setParam(p, elements, "RAT", 6, lineNo, file, eventId);
            setParam(p, elements, "DEACTIVATION_TRIGGER", 7, lineNo, file, eventId);
    		setParam(p, elements, "CAUSE_CODE", 8, lineNo, file, eventId);
            setParam(p, elements, "SUB_CAUSE_CODE", 9, lineNo, file, eventId);
    		setParam(p, elements, "LOCATION_MCC", 10, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_MNC", 11, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_LAC", 12, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_RAC", 13, lineNo, file, eventId);
            setParam(p, elements, "LOCATION_CI", 14, lineNo, file, eventId);
    		setParam(p, elements, "LOCATION_SAC", 15, lineNo, file, eventId);
            setParam(p, elements, "IMSI", 16, lineNo, file, eventId);
    		setParam(p, elements, "IMEISV", 17, lineNo, file, eventId);
            setParam(p, elements, "GGSN", 18, lineNo, file, eventId);
            setParam(p, elements, "APN", 19, lineNo, file, eventId);
            setParam(p, elements, "HOMEZONE_IDENTITY", 20, lineNo, file, eventId);
            setParam(p, elements, "MSISDN", 21, lineNo, file, eventId);
            setParam(p, elements, "NSAPI", 22, lineNo, file, eventId);
            setParam(p, elements, "MS_ADDRESS_IPV4", 23, lineNo, file, eventId);
            setParam(p, elements, "MS_ADDRESS_IPV6", 24, lineNo, file, eventId);
            setParam(p, elements, "CAUSE_PROT_TYPE", 25, lineNo, file, eventId);
            setParam(p, elements, "TIME_MILLISECOND", 26, lineNo, file, eventId);
            setParam(p, elements, "DURATION", 27, lineNo, file, eventId);
            setParam(p, elements, "PTMSI", 28, lineNo, file, eventId);
            
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new Deactivate(p);
            eventBA = event.encode();
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return eventBA;
        }
        return eventBA;
	}
	

	private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;
	@Override
	public String getEventLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] processHeaderRecord(String elements[], int lineNo, String file, String version) {

        log.debug("Sgw Stream Header");
        eventRecordBuffer.append("Sgw Stream Header\n");
        log.debug("Sgw Stream Header");
        if(elements.length < 12)
        {
            byte byteArray[] = new byte[12];
            byteArray[0] = 0;
            byteArray[1] = 12;
            int fieldIndex = 0;
            setByte(byteArray, 2, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 1;
            setByte(byteArray, 3, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 2;
            setByte(byteArray, 4, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 3;
            short year = Short.parseShort(elements[fieldIndex]);
            byteArray[5] = (byte)(year >> 8);
            byteArray[6] = (byte)year;
            fieldIndex = 4;
            setByte(byteArray, 7, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 5;
            setByte(byteArray, 8, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 6;
            setByte(byteArray, 9, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 7;
            setByte(byteArray, 10, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 8;
            setByte(byteArray, 11, elements[fieldIndex], file, lineNo, fieldIndex);
            return byteArray;
        } else
        {
            byte recTypeToTimeZoneLenBA[] = new byte[11];
            int fieldIndex = 1;
            setByte(recTypeToTimeZoneLenBA, 0, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 1, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 2, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            short year = Short.parseShort(elements[fieldIndex]);
            recTypeToTimeZoneLenBA[3] = (byte)(year >> 8);
            recTypeToTimeZoneLenBA[4] = (byte)year;
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 5, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 6, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 7, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 8, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(recTypeToTimeZoneLenBA, 9, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            String timeZone = elements[fieldIndex];
            String timeZoneLen = String.valueOf(timeZone.length());
            setByte(recTypeToTimeZoneLenBA, 10, timeZoneLen, file, lineNo, fieldIndex);
            byte timeZoneBA[] = timeZone.getBytes();
            byte causeHeaderNodeIdLenBA[] = new byte[2];
            fieldIndex++;
            setByte(causeHeaderNodeIdLenBA, 0, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            String nodeId = elements[fieldIndex];
            String nodeIdLen = String.valueOf(nodeId.length());
            setByte(causeHeaderNodeIdLenBA, 1, nodeIdLen, file, lineNo, fieldIndex);
            byte nodeIdBA[] = nodeId.getBytes();
            int len = 13 + timeZoneBA.length + 2 + nodeIdBA.length;
            len += (32 - len % 32) % 32;
            byte recLenBA[] = new byte[2];
            short lenShort = Short.parseShort(Integer.toString(len));
            recLenBA[0] = (byte)(lenShort >> 8);
            recLenBA[1] = (byte)lenShort;
            byte streamHeaderBA[] = new byte[len];
            System.arraycopy(recLenBA, 0, streamHeaderBA, 0, recLenBA.length);
            System.arraycopy(recTypeToTimeZoneLenBA, 0, streamHeaderBA, 2, 11);
            System.arraycopy(timeZoneBA, 0, streamHeaderBA, 13, timeZoneBA.length);
            System.arraycopy(causeHeaderNodeIdLenBA, 0, streamHeaderBA, 13 + timeZoneBA.length, 2);
            System.arraycopy(nodeIdBA, 0, streamHeaderBA, 13 + timeZoneBA.length + 2, nodeIdBA.length);
            return streamHeaderBA;
        }
	}

	@Override
	protected byte[] processEventRecord(String elements[], int lineNo, String file, String version) {

        byte eventBA[] = (byte[])null;
        if(elements == null)
            return eventBA;
        int fieldIdx = 1;
        String eventIdString = elements[fieldIdx];
        int eventId;
        try
        {
            eventId = Integer.parseInt(eventIdString);
        }
        catch(NumberFormatException e)
        {
            log.error(formatMsg((new StringBuilder("Failed to parse \"")).append(eventIdString).append("\" to an integer").toString(), file, lineNo, fieldIdx));
            return eventBA;
        }
        switch(eventId)
        {
        case 1:
        	eventBA = activateEvent(elements, lineNo, file, eventId, version);
        	break;
        	
        
        	
        case 4:
        	eventBA = deactivateEvent(elements, lineNo, file, eventId, version);
        	break;   
            
        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
    
	}

	@Override
	protected byte[] processErrorRecord(String[] as, int i, String s, String s1) {
		// TODO Auto-generated method stub
		return null;
	}



}
