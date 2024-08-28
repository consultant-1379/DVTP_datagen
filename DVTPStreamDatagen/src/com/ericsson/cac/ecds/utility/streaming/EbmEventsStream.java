/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.utility.streaming;

import com.ericsson.cac.ecds.ebm.revision_11.event.LAttach;
import com.ericsson.cac.ecds.ebm.revision_11.event.LDedicatedBearerActivate;
import com.ericsson.cac.ecds.ebm.revision_11.event.LDedicatedBearerDeactivate;
import com.ericsson.cac.ecds.ebm.revision_11.event.LHandover;
import com.ericsson.cac.ecds.ebm.revision_11.event.LPdnConnect;
import com.ericsson.cac.ecds.ebm.revision_11.event.LServiceRequest;
import com.ericsson.cac.ecds.ebm.revision_11.event.LTau;
import com.ericsson.cac.ecds.ebm.revision_d.event.LDetach;
import com.ericsson.cac.ecds.ebm.revision_d.event.LPdnDisconnect;
import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.utility.Main;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.log4j.Logger;

// Referenced classes of package com.ericsson.cac.ecds.utility.streaming:
//            EcdsEventsStreamUtilityCommon, EcdsEventsStreamUtilityInterface

public class EbmEventsStream extends EcdsEventsStreamUtilityCommon
    implements EcdsEventsStreamUtilityInterface
{

    public EbmEventsStream()
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
        log.debug("Ebm Error Header");
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
        log.debug("Ebm Footer");
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
        log.debug("Ebm Stream Event");
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
        case 5: // '\005'
            eventBA = LAttachEvent(elements, lineNo, file, eventId, version);
            break;

        case 6: // '\006'
            eventBA = LDetachEvent(elements, lineNo, file, eventId, version);
            break;

        case 7: // '\007'
            eventBA = LHandoverEvent(elements, lineNo, file, eventId, version);
            break;

        case 8: // '\b'
            eventBA = LTauEvent(elements, lineNo, file, eventId, version);
            break;

        case 9: // '\t'
            eventBA = LDedicatedBearerActivateEvent(elements, lineNo, file, eventId, version);
            break;

        case 10: // '\n'
            eventBA = LDedicatedBearerDeactivateEvent(elements, lineNo, file, eventId, version);
            break;

        case 11: // '\013'
            eventBA = LPdnConnect(elements, lineNo, file, eventId, version);
            break;

        case 12: // '\f'
            eventBA = LPdnDisconnect(elements, lineNo, file, eventId, version);
            break;

        case 13: // '\r'
            eventBA = LServiceRequest(elements, lineNo, file, eventId, version);
            break;

        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
    }

    private byte[] LHandoverEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_HANDOVER";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        int idx = 19;
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "HANDOVER_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "UEID__MTMSI", idx++, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "BEARER_STRUCT_11", idx++, lineNo, file, eventId);
            else
                setParam(p, elements, "BEARER_STRUCT", idx++, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", idx++, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO_STRUCT", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_TAI__OLD_MCC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_TAI__OLD_MNC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_TAI__OLD_TAC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_ECI", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_MME__MMEGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_MME__MMEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_MTMSI", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_S_GW__IPV4", idx++, lineNo, file, eventId);
            if(version.equals("4_15"))
            {
                setParam(p, elements, "L_HO_RAI__MCC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO_RAI__MNC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO_RAI__LAC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO_RAI__RAC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO__CI", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO_SGSN__IPV4", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO_OLD_SGSN__IPV4", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO__NODE_ROLE", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO__RAT_CHANGE_TYPE", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_HO__SGW_CHANGE_TYPE", idx++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION))
                event = new LHandover(p);
            else
            if(version.equals("4_15"))
                event = new com.ericsson.cac.ecds.ebm.revision_4_15.event.LHandover(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LHandover(p);
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

    private byte[] LTauEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_TAU";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        int idx = 19;
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "TAU_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "UEID__MTMSI", idx++, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "BEARER_STRUCT_11", idx++, lineNo, file, eventId);
            else
                setParam(p, elements, "BEARER_STRUCT", idx++, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", idx++, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO_STRUCT", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_TAI__OLD_MCC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_TAI__OLD_MNC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_TAI__OLD_TAC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_MME__MMEGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_MME__MMEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_MTMSI", idx++, lineNo, file, eventId);
            setParam(p, elements, "OLD_S_GW__IPV4", idx++, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
            {
                setParam(p, elements, "REQUEST_RETRIES", idx++, lineNo, file, eventId);
                setParam(p, elements, "COMBINED_TAU_TYPE", idx++, lineNo, file, eventId);
                setParam(p, elements, "SMS_ONLY", idx++, lineNo, file, eventId);
            }
            if(version.equals("4_15"))
            {
                setParam(p, elements, "L_TAU_OLD_RAI_MCC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_TAU_OLD_RAI_MNC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_TAU_OLD_RAI_LAC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_TAU_OLD_RAI_RAC", idx++, lineNo, file, eventId);
                setParam(p, elements, "L_TAU_OLD_SGSN__IPV4", idx++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION))
                event = new LTau(p);
            else
            if(version.equals("4_15"))
                event = new com.ericsson.cac.ecds.ebm.revision_4_15.event.LTau(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LTau(p);
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

    private byte[] LServiceRequest(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_SERVICE_REQUEST";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "PAGING_ATTEMPTS", 19, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 20, lineNo, file, eventId);
            if(version.equals(Main.EBM_E_VERSION))
            {
                setParam(p, elements, "BEARER_STRUCT", 21, lineNo, file, eventId);
                setParam(p, elements, "CS_FALLBACK_SERVICE_TYPE", 22, lineNo, file, eventId);
                setParam(p, elements, "SERVICE_REQUEST_TYPE", 23, lineNo, file, eventId);
            } else
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
            {
                setParam(p, elements, "BEARER_STRUCT_11", 21, lineNo, file, eventId);
                setParam(p, elements, "CS_FALLBACK_SERVICE_TYPE", 22, lineNo, file, eventId);
                setParam(p, elements, "SERVICE_REQUEST_TYPE", 23, lineNo, file, eventId);
                setParam(p, elements, "SERVICE_REQUEST_TRIGGER", 24, lineNo, file, eventId);
                setParam(p, elements, "REQUEST_RETRIES", 25, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                event = new LServiceRequest(p);
            else
            if(version.equals(Main.EBM_E_VERSION))
                event = new com.ericsson.cac.ecds.ebm.revision_e.event.LServiceRequest(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LServiceRequest(p);
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

    private byte[] LPdnDisconnect(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_PDN_DISCONNECT";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "DECONNECT_PDN_TYPE", 19, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 20, lineNo, file, eventId);
            setParam(p, elements, "DEFAULT_BEARER_ID", 21, lineNo, file, eventId);
            setParam(p, elements, "APN", 22, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event = new LPdnDisconnect(p);
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

    private byte[] LPdnConnect(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_PDN_CONNECT";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "BEARER_STRUCT_11", 19, lineNo, file, eventId);
            else
                setParam(p, elements, "BEARER_STRUCT", 19, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 20, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO__DEFAULT_BEARER_ID", 21, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO__PAA__IPV4", 22, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO__P_GW__IPV4", 23, lineNo, file, eventId);
            setParam(p, elements, "APN", 24, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "REQUEST_RETRIES", 25, lineNo, file, eventId);
            if(version.equals("4_15"))
                setParam(p, elements, "REQUEST_TYPE", 26, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION))
                event = new LPdnConnect(p);
            else
            if(version.equals("4_15"))
                event = new com.ericsson.cac.ecds.ebm.revision_4_15.event.LPdnConnect(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LPdnConnect(p);
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

    private byte[] LDedicatedBearerDeactivateEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_DEDICATED_BEARER_DEACTIVATE";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "DEACTIVATION_TRIGGER", 19, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "BEARER_STRUCT_11", 20, lineNo, file, eventId);
            else
                setParam(p, elements, "BEARER_STRUCT", 20, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 21, lineNo, file, eventId);
            setParam(p, elements, "APN", 22, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                event = new LDedicatedBearerDeactivate(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LDedicatedBearerDeactivate(p);
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

    private byte[] LDedicatedBearerActivateEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_DEDICATED_BEARER_ACTIVATE";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "BEARER_STRUCT_11", 19, lineNo, file, eventId);
            else
                setParam(p, elements, "BEARER_STRUCT", 19, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 20, lineNo, file, eventId);
            setParam(p, elements, "APN", 21, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                event = new LDedicatedBearerActivate(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LDedicatedBearerActivate(p);
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

    private byte[] LDetachEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_DETACH";
        log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "DETACH_TRIGGER", 19, lineNo, file, eventId);
            setParam(p, elements, "DETACH_TYPE", 20, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 21, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        try
        {
            EventInterface event = new LDetach(p);
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

    private byte[] LAttachEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "L_ATTACH";
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("Ebm Event ")).append(eventName).toString());
        byte eventBA[] = (byte[])null;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId);
            setParam(p, elements, "ATTACH_TYPE", 19, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                setParam(p, elements, "BEARER_STRUCT_11", 20, lineNo, file, eventId);
            else
                setParam(p, elements, "BEARER_STRUCT", 20, lineNo, file, eventId);
            setParam(p, elements, "S_GW__IPV4", 21, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO__DEFAULT_BEARER_ID", 22, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO__PAA__IPV4", 23, lineNo, file, eventId);
            setParam(p, elements, "PDN_INFO__P_GW__IPV4", 24, lineNo, file, eventId);
            setParam(p, elements, "APN", 25, lineNo, file, eventId);
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
            {
                setParam(p, elements, "REQUEST_RETRIES", 26, lineNo, file, eventId);
                setParam(p, elements, "SMS_ONLY", 27, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return eventBA;
        }
        try
        {
            EventInterface event;
            if(version.equals(Main.EBM_11_VERSION) || version.equals("4_15"))
                event = new LAttach(p);
            else
                event = new com.ericsson.cac.ecds.ebm.revision_d.event.LAttach(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
            return event.encode();
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
        }
        return eventBA;
    }

    private void processCommonFields(Properties p, String elements[], int lineNo, String file, int eventId)
        throws ArrayIndexOutOfBoundsException
    {
        setParam(p, elements, "L_HEADER__HEADER__EVENT_RESULT", 2, lineNo, file, eventId);
        setParam(p, elements, "L_HEADER__HEADER__TIME_HOUR", 3, lineNo, file, eventId);
        setParam(p, elements, "L_HEADER__HEADER__TIME_MINUTE", 4, lineNo, file, eventId);
        setParam(p, elements, "L_HEADER__HEADER__TIME_SECOND", 5, lineNo, file, eventId);
        setParam(p, elements, "L_HEADER__TIME_MILLISECOND", 6, lineNo, file, eventId);
        setParam(p, elements, "L_HEADER__DURATION", 7, lineNo, file, eventId);
        setParam(p, elements, "L_CAUSE_PROT_TYPE", 8, lineNo, file, eventId);
        setParam(p, elements, "CAUSE_CODE", 9, lineNo, file, eventId);
        setParam(p, elements, "SUB_CAUSE_CODE", 10, lineNo, file, eventId);
        setParam(p, elements, "TAI__TAC", 11, lineNo, file, eventId);
        setParam(p, elements, "ECI", 12, lineNo, file, eventId);
        setParam(p, elements, "MMEI__MMEGI", 13, lineNo, file, eventId);
        setParam(p, elements, "MMEI__MMEC", 14, lineNo, file, eventId);
        setParam(p, elements, "UEID__IMSI", 15, lineNo, file, eventId);
        setParam(p, elements, "UEID__IMEISV", 16, lineNo, file, eventId);
        setParam(p, elements, "MCC", 17, lineNo, file, eventId);
        setParam(p, elements, "MNC", 18, lineNo, file, eventId);
    }

    protected byte[] processHeaderRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("EBM Stream Header");
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
            int len = nodeId.length();
            len <<= 3;
            byte nodeIdLen = (byte)len;
            causeHeaderNodeIdLenBA[1] = nodeIdLen;
            byte nodeIdBA[] = nodeId.getBytes();
            len = 13 + timeZoneBA.length + 2 + nodeIdBA.length;
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

    private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events_Utility.jar
	Total time: 68 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/