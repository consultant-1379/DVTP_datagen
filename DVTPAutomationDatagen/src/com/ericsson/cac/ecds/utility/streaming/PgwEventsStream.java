package com.ericsson.cac.ecds.utility.streaming;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.sgw.revision_a.event.DataUsage;
import com.ericsson.cac.ecds.sgw.revision_a.event.SessionInfo;
import com.ericsson.cac.ecds.utility.Main;

public class PgwEventsStream extends EcdsEventsStreamUtilityCommon
implements EcdsEventsStreamUtilityInterface{
	
	
	
	public List <byte[]> processSeedString(String seedString, String version)
    {
        List<byte[]> eventsStream = new ArrayList<byte[]>();
        String elements[];
        int recordType;
        elements = seedString.split(",");
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
                
            case 5: // '\005'
                
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
	
	
	private byte[] dataUsageEvent(String[] elements, int lineNo, String file,int eventId, String version) 
    {
    	String eventName = "DATA_USAGE";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("PGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_USAGE_STRUCT", 29, lineNo, file, eventId);
            
            setParam(p, elements, "SESSION_USAGE_STRUCT", 30, lineNo, file, eventId);
            
           }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new DataUsage(p);
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
	
	
	private byte[] sessionInfoEvent(String[] elements, int lineNo, String file,int eventId, String version) 
    {
    	String eventName = "SESSION_INFO";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("PGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new SessionInfo(p);
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
	
	private void processCommonFields(Properties p, String elements[], int lineNo, String file, int eventId)
	        throws ArrayIndexOutOfBoundsException
	    {
		setParam(p, elements, "HEADER_RECORD_TYPE", 0, lineNo, file, eventId);
		setParam(p, elements, "HEADER_EVENT_ID", 1, lineNo, file, eventId);
		setParam(p, elements, "HEADER_EVENT_RESULT", 2, lineNo, file, eventId);
        setParam(p, elements, "HEADER_TIME_HOUR", 3, lineNo, file, eventId);
        setParam(p, elements, "HEADER_TIME_MINUTE", 4, lineNo, file, eventId);
        setParam(p, elements, "HEADER_TIME_SECOND", 5, lineNo, file, eventId);
        setParam(p, elements, "HEADER_TIME_MILLISECOND", 6, lineNo, file, eventId);
        setParam(p, elements, "HEADER_DURATION", 7, lineNo, file, eventId);
		setParam(p, elements, "UE_INFO_IMSI", 8, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_IMSI_VALIDATION", 9, lineNo, file, eventId);
		setParam(p, elements, "UE_INFO_IMEISV", 10, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_TAI_MCC", 11, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_TAI_MNC", 12, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_TAI_TAC", 13, lineNo, file, eventId);
		setParam(p, elements, "UE_INFO_ECI", 14, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_LAC", 15, lineNo, file, eventId);
		setParam(p, elements, "UE_INFO_RAC", 16, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_CI", 17, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO_SAC", 18, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_DEFAULT_BEARER_ID", 19, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_APN", 20, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_PGW_ADDRESS_IPV4", 21, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_PGW_ADDRESS_IPV6", 22, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_ALLOCATED_UE_ADDRESS_IPV4", 23, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_ALLOCATED_UE_ADDRESS_IPV6", 24, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_APN_AMBR_APN_AMBR_UL", 25, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_APN_AMBR_APN_AMBR_DL", 26, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO_PDN_ID", 27, lineNo, file, eventId);
		setParam(p, elements, "PDN_INFO_RULE_SPACE", 28, lineNo, file, eventId);
        
	    }
	
	
	
	private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;
	@Override
	public String getEventLog() {
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
           
        case 6:
        	eventBA = sessionInfoEvent(elements, lineNo, file, eventId, version);
        	break;
        	
        case 7:
        	eventBA = dataUsageEvent(elements, lineNo, file, eventId, version);
        	break;
            
            
        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
    
	}

	@Override
	protected byte[] processErrorRecord(String[] as, int i, String s, String s1) {
		return null;
	}

}
