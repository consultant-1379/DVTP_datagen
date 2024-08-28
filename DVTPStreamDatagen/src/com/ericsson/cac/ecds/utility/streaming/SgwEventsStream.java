/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.utility.streaming;

import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.sgw.revision_a.event.*;
import com.ericsson.cac.ecds.utility.Main;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

// Referenced classes of package com.ericsson.cac.ecds.utility.streaming:
//            EcdsEventsStreamUtilityCommon, EcdsEventsStreamUtilityInterface

public class SgwEventsStream extends EcdsEventsStreamUtilityCommon
    implements EcdsEventsStreamUtilityInterface
{

    public SgwEventsStream()
    {
        eventRecordBuffer = new StringBuffer();
    }

    public String getEventLog()
    {
        return null;
    }
    
    
    

    protected byte[] processErrorRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("Sgw Stream Error");
        byte byteArray[] = new byte[12];
        byteArray[0] = 0;
        byteArray[1] = 12;
        byteArray[2] = 5;
        int fieldIndex = 0;
        setByte(byteArray, 2, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 1;
        setByte(byteArray, 3, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 2;
        setByte(byteArray, 4, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 3;
        setByte(byteArray, 5, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 4;
        setByte(byteArray, 6, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 5;
        int numDrops = Integer.parseInt(elements[fieldIndex]);
        byteArray[7] = (byte)(numDrops >> 24);
        byteArray[8] = (byte)(numDrops >> 16);
        byteArray[9] = (byte)(numDrops >> 8);
        byteArray[10] = (byte)numDrops;
        System.out.println((new StringBuilder("5 = ")).append(elements[fieldIndex]).toString());
        byteArray[11] = 0;
        return byteArray;
    }

    protected byte[] processFooterRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("Sgw Footer");
        byte byteArray[] = new byte[4];
        byteArray[0] = 0;
        byteArray[1] = 4;
        int fieldIndex = 0;
        setByte(byteArray, 2, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 1;
        setByte(byteArray, 3, elements[fieldIndex], file, lineNo, fieldIndex);
        return byteArray;
    }

    protected byte[] processEventRecord(String elements[], int lineNo, String file, String version)
    {
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
        case 0: // '\0'
            eventBA = SessionDeletionEvent(elements, lineNo, file, eventId, version);
            break;

        case 1: // '\001'
            eventBA = BearerDeletionEvent(elements, lineNo, file, eventId, version);
            break;

        case 2: // '\002'
            eventBA = SessionCreationEvent(elements, lineNo, file, eventId, version);
            break;

        case 3: // '\003'
            eventBA = BearerCreationEvent(elements, lineNo, file, eventId, version);
            break;

        case 4: // '\004'
            eventBA = BearerModificationEvent(elements, lineNo, file, eventId, version);
            break;

        case 5: // '\005'
            eventBA = BearerUpdateEvent(elements, lineNo, file, eventId, version);
            break;
      
            
        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
    }

    

	private byte[] BearerUpdateEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "BEARER_UPDATE";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_STRUCT", 27, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV4", 28, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV6", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new BearerUpdate(p);
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

    private byte[] BearerModificationEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "BEARER_MODIFICATION";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_STRUCT", 27, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV4", 28, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV6", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new BearerModification(p);
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

    private byte[] BearerCreationEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "BEARER_CREATION";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_STRUCT", 27, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV4", 28, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV6", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new BearerCreation(p);
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

    private byte[] SessionCreationEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "SESSION_CREATION";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_STRUCT", 27, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV4", 28, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV6", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new SessionCreation(p);
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

    private byte[] BearerDeletionEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "BEARER_DELETION";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_STRUCT", 27, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV4", 28, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV6", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new BearerDeletion(p);
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

    private byte[] SessionDeletionEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "SESSION_DELETION";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("SGW Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "BEARER_STRUCT", 27, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV4", 28, lineNo, file, eventId);
            setParam(p, elements, "MME_OR_SGSN__IPV6", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event = new SessionDeletion(p);
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

    protected byte[] processHeaderRecord(String elements[], int lineNo, String file, String version)
    {
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

    private void processCommonFields(Properties p, String elements[], int lineNo, String file, int eventId)
        throws ArrayIndexOutOfBoundsException
    {
        setParam(p, elements, "HEADER__EVENT_RESULT", 2, lineNo, file, eventId);
        setParam(p, elements, "HEADER__TIME_HOUR", 3, lineNo, file, eventId);
        setParam(p, elements, "HEADER__TIME_MINUTE", 4, lineNo, file, eventId);
        setParam(p, elements, "HEADER__TIME_SECOND", 5, lineNo, file, eventId);
        setParam(p, elements, "HEADER__TIME_MILLISECOND", 6, lineNo, file, eventId);
        setParam(p, elements, "HEADER__DURATION", 7, lineNo, file, eventId);
        setParam(p, elements, "CAUSE_PROTOCOL", 8, lineNo, file, eventId);
        setParam(p, elements, "CAUSE_CODE", 9, lineNo, file, eventId);
        setParam(p, elements, "SUB_CAUSE_CODE", 10, lineNo, file, eventId);
        setParam(p, elements, "EVENT_TRIGGER", 11, lineNo, file, eventId);
        setParam(p, elements, "ORIGINATING_NODE", 12, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__DEFAULT_BEARER_ID", 13, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__APN", 14, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__PGW_ADDRESS__IPV4", 15, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__PGW_ADDRESS__IPV6", 16, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__ALLOCATED_UE_ADDRESS__IPV4", 17, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__ALLOCATED_UE_ADDRESS__IPV6", 18, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__APN_AMBR__APN_AMBR_UL", 19, lineNo, file, eventId);
        setParam(p, elements, "PDN_INFO__APN_AMBR__APN_AMBR_DL", 20, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO__IMSI", 21, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO__IMEISV", 22, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO__TAI__MCC", 23, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO__TAI__MNC", 24, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO__TAI__TAC", 25, lineNo, file, eventId);
        setParam(p, elements, "UE_INFO__ECI", 26, lineNo, file, eventId);
    }

    private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events_Utility.jar
	Total time: 58 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/