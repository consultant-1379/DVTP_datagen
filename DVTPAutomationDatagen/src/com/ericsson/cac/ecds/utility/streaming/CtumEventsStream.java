/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.utility.streaming;

import com.ericsson.cac.ecds.ctum.revision_b.event.Ctum;
import com.ericsson.cac.ecds.interfaces.EventInterface;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.log4j.Logger;

// Referenced classes of package com.ericsson.cac.ecds.utility.streaming:
//            EcdsEventsStreamUtilityCommon, EcdsEventsStreamUtilityInterface

public class CtumEventsStream extends EcdsEventsStreamUtilityCommon
    implements EcdsEventsStreamUtilityInterface
{

    public CtumEventsStream()
    {
        eventRecordBuffer = new StringBuffer();
    }

    public String getEventLog()
    {
        if(eventRecordBuffer != null)
            return eventRecordBuffer.toString();
        else
            return "";
    }

    protected byte[] processErrorRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("CTUM Error Header");
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
        setByte(byteArray, 5, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 4;
        setByte(byteArray, 6, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 5;
        int droppedEvents = Integer.parseInt(elements[fieldIndex]);
        byteArray[7] = (byte)(droppedEvents >> 24);
        byteArray[8] = (byte)(droppedEvents >> 16);
        byteArray[9] = (byte)(droppedEvents >> 8);
        byteArray[10] = (byte)droppedEvents;
        return byteArray;
    }

    protected byte[] processFooterRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("Ctum Footer");
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
            eventBA = CtumEvent(elements, lineNo, file, eventId);
            break;

        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
    }

    private byte[] CtumEvent(String elements[], int lineNo, String file, int eventId)
    {
        String eventName = "CTUM Stream";
        log.debug((new StringBuilder("Ctum Stream Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            setParam(p, elements, "TIMESTAMP__BYTE_HOUR", 2, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP__BYTE_MINUTE", 3, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP__BYTE_SECOND", 4, lineNo, file, eventId);
            setParam(p, elements, "TIME_MILLISECOND", 5, lineNo, file, eventId);
            setParam(p, elements, "ENODEB_ID__MACRO_ENODEB_ID", 6, lineNo, file, eventId);
            setParam(p, elements, "ENODEB_ID__HOME_ENODEB_ID", 7, lineNo, file, eventId);
            setParam(p, elements, "IMSI", 8, lineNo, file, eventId);
            setParam(p, elements, "IMEISV", 9, lineNo, file, eventId);
            setParam(p, elements, "GUMMEI__PLMN_IDENTITY", 10, lineNo, file, eventId);
            setParam(p, elements, "GUMMEI__MME_GROUP_ID", 11, lineNo, file, eventId);
            setParam(p, elements, "GUMMEI__MME_CODE", 12, lineNo, file, eventId);
            setParam(p, elements, "MME_UE_S1AP_ID", 13, lineNo, file, eventId);
            setParam(p, elements, "ENB_UE_S1AP_ID", 14, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event = new Ctum(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
            return event.encode();
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
        }
        return null;
    }

    protected byte[] processHeaderRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("Ctum Stream Header");
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
            String HEADER_RECORD_TYPE = "4";
            byte byteArray[] = new byte[36];
            byteArray[0] = 0;
            byteArray[1] = 36;
            int fieldIndex = 1;
            setByte(byteArray, 2, "4", file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 3, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 4, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            short year = Short.parseShort(elements[fieldIndex]);
            byteArray[5] = (byte)(year >> 8);
            byteArray[6] = (byte)year;
            fieldIndex++;
            setByte(byteArray, 7, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 8, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 9, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 10, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 11, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 12, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            byte stringBytes[] = elements[fieldIndex].getBytes();
            int len = elements[fieldIndex].length();
            len <<= 3;
            byte nodeIdLen = (byte)len;
            byteArray[13] = nodeIdLen;
            System.arraycopy(stringBytes, 0, byteArray, 14, stringBytes.length);
            return byteArray;
        }
    }

    private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events_Utility.jar
	Total time: 67 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/