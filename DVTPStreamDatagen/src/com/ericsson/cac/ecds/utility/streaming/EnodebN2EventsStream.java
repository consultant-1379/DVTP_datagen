/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.utility.streaming;

import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalEventBestCellEval;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalPerRadioCellMeasurement;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcErabRelease;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoExecS1In;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoExecX2In;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcRrcConnSetup;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcS1SigConnSetup;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcUeCtxtReleaseEnb;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalEventUeMobilityEval;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalPerRadioUeMeasurement;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalPerUeRbTrafficRep;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalPerUeTrafficRep;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcErabSetup;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcHoExecS1Out;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcHoExecX2Out;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcHoPrepS1In;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcHoPrepS1Out;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcHoPrepX2In;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcHoPrepX2Out;
import com.ericsson.cac.ecds.enodeb.revision_t3.event.InternalProcInitialCtxtSetup;
import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.utility.Main;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.log4j.Logger;

// Referenced classes of package com.ericsson.cac.ecds.utility.streaming:
//            EcdsEventsStreamUtilityCommon, EcdsEventsStreamUtilityInterface

public class EnodebN2EventsStream extends EcdsEventsStreamUtilityCommon
    implements EcdsEventsStreamUtilityInterface
{

    public EnodebN2EventsStream()
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

    protected byte[] processErrorRecord(String elements[], int lineNo, String seedFile, String s)
    {
        return null;
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
            return null;
        }
        switch(eventId)
        {
        case 3075: 
            eventBA = internalPerRadioUeMeasurementEvent(elements, lineNo, file, eventId, version);
            break;

        case 3081: 
            eventBA = internalPerRadioCellMeasurementEvent(elements, lineNo, file, eventId, version);
            break;

        case 3076: 
            eventBA = internalPerUeTrafficRepEvent(elements, lineNo, file, eventId, version);
            break;

        case 3077: 
            eventBA = internalPerUeRbTrafficRepEvent(elements, lineNo, file, eventId, version);
            break;

        case 4097: 
            eventBA = internalProcRrcConnSetupEvent(elements, lineNo, file, eventId, version);
            break;

        case 4098: 
            eventBA = internalProcS1SigConnSetupEvent(elements, lineNo, file, eventId, version);
            break;

        case 4099: 
            eventBA = internalProcErabSetupEvent(elements, lineNo, file, eventId, version);
            break;

        case 4100: 
            eventBA = internalProcUeCtxtReleaseEnbEvent(elements, lineNo, file, eventId, version);
            break;

        case 4106: 
            eventBA = internalProcInitialCtxtSetupEvent(elements, lineNo, file, eventId, version);
            break;

        case 4114: 
            eventBA = internalProcErabReleaseEvent(elements, lineNo, file, eventId, version);
            break;

        case 4102: 
            eventBA = internalProcHoPrepS1OutEvent(elements, lineNo, file, eventId, version);
            break;

        case 4103: 
            eventBA = internalProcHoPrepS1InEvent(elements, lineNo, file, eventId, version);
            break;

        case 4104: 
            eventBA = internalProcHoExecS1OutEvent(elements, lineNo, file, eventId, version);
            break;

        case 4105: 
            eventBA = internalProcHoExecS1InEvent(elements, lineNo, file, eventId, version);
            break;

        case 4110: 
            eventBA = internalProcHoPrepX2OutEvent(elements, lineNo, file, eventId, version);
            break;

        case 4111: 
            eventBA = internalProcHoPrepX2InEvent(elements, lineNo, file, eventId, version);
            break;

        case 4112: 
            eventBA = internalProcHoExecX2OutEvent(elements, lineNo, file, eventId, version);
            break;

        case 4113: 
            eventBA = internalProcHoExecX2InEvent(elements, lineNo, file, eventId, version);
            break;

        case 5135: 
            eventBA = internalEventBestCellEvalEvent(elements, lineNo, file, eventId, version);
            break;

        case 5193: 
            eventBA = internalEventUeMobilityEvalEvent(elements, lineNo, file, eventId, version);
            break;

        case 4125: 
            eventBA = internalProcUeCtxtReleaseEnbEvent(elements, lineNo, file, eventId, version);
            break;

        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
    }

    private byte[] internalEventUeMobilityEvalEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalEventUeMobilityEval";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "MOBILITY_TRIGGER", idx++, lineNo, file, eventId);
            setParam(p, elements, "TRIGGERING_MEAS_ID", idx++, lineNo, file, eventId);
            setParam(p, elements, "MOBILITY_TRIGGER", idx++, lineNo, file, eventId);
            setParam(p, elements, "MEAS_ID_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "REDIRECT_RELEASE_INFO", idx++, lineNo, file, eventId);
            setParam(p, elements, "RELEASE_WITH_REDIRECT_REASON", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalEventUeMobilityEval(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoExecX2InEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoExecX2InEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "RANDOM_ACCESS_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_PACKET_FORWARD", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_EXEC_IN_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcHoExecX2In(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoExecX2OutEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoExecX2OutEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_PACKET_FORWARD", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_EXEC_OUT_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_EXEC_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_EXEC_ERAB_FAIL_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "SOURCE_OR_TARGET_TYPE", idx++, lineNo, file, eventId);
                setParam(p, elements, "TARGET_SELECTION_TYPE", idx++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcHoExecX2Out(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoExecX2Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoPrepX2InEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoPrepX2InEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            if(version.equals(Main.ENB_T3_VERSION))
                setParam(p, elements, "SOURCE_OR_TARGET_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
                setParam(p, elements, "PROC_HO_PREP_IN_RESULT_ARRAY", idx++, lineNo, file, eventId);
            else
                setParam(p, elements, "PROC_HO_PREP_IN_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "RANDOM_ACCESS_TYPE", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "ERAB_ID_SETUP_REQ_ARRAY", idx++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_ARP_ARRAY", idx++, lineNo, file, eventId);
            } else
            {
                setParam(p, elements, "HO_IN_PREP_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
                setParam(p, elements, "HO_IN_PREP_ERAB_SUCC_BITMAP", idx++, lineNo, file, eventId);
            }
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcHoPrepX2In(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoPrepX2In(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoPrepX2OutEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoPrepX2OutEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_PREP_OUT_ATTEMPT_CAUSE", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_PREP_OUT_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_PREP_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_PREP_ERAB_FAIL_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "SOURCE_OR_TARGET_TYPE", idx++, lineNo, file, eventId);
                setParam(p, elements, "TARGET_SELECTION_TYPE", idx++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcHoPrepX2Out(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoPrepX2Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoExecS1InEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoExecS1InEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_EXEC_IN_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "RANDOM_ACCESS_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcHoExecS1In(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoExecS1OutEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoExecS1OutEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_EXEC_OUT_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_EXEC_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_EXEC_ERAB_FAIL_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "SOURCE_OR_TARGET_TYPE", idx++, lineNo, file, eventId);
                setParam(p, elements, "TARGET_SELECTION_TYPE", idx++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcHoExecS1Out(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoExecS1Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoPrepS1InEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoPrepS1InEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            if(version.equals(Main.ENB_T3_VERSION))
                setParam(p, elements, "SOURCE_OR_TARGET_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
                setParam(p, elements, "PROC_HO_PREP_IN_RESULT_ARRAY", idx++, lineNo, file, eventId);
            else
                setParam(p, elements, "PROC_HO_PREP_IN_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "RANDOM_ACCESS_TYPE", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "ERAB_ID_SETUP_REQ_ARRAY", idx++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_ARP_ARRAY", idx++, lineNo, file, eventId);
            } else
            {
                setParam(p, elements, "HO_IN_PREP_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
                setParam(p, elements, "HO_IN_PREP_ERAB_SUCC_BITMAP", idx++, lineNo, file, eventId);
            }
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcHoPrepS1In(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoPrepS1In(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcHoPrepS1OutEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoPrepS1OutEvent";
        log.debug((new StringBuilder("EnodeB N2 Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_PREP_OUT_ATTEMPT_CAUSE", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_PREP_OUT_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_PREP_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_PREP_ERAB_FAIL_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", idx++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "SOURCE_OR_TARGET_TYPE", idx++, lineNo, file, eventId);
                setParam(p, elements, "TARGET_SELECTION_TYPE", idx++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcHoPrepS1Out(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoPrepS1Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalEventBestCellEvalEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalEventBestCellEvalEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int idx = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "SERVING_RSRP", idx++, lineNo, file, eventId);
            setParam(p, elements, "SERVING_RSRQ", idx++, lineNo, file, eventId);
            setParam(p, elements, "NEIGHBOR_PCI", idx++, lineNo, file, eventId);
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "NEIGHBOR_RSRP", idx++, lineNo, file, eventId);
            setParam(p, elements, "NEIGHBOR_RSRQ", idx++, lineNo, file, eventId);
            setParam(p, elements, "BEST_CELL_DECISION", idx++, lineNo, file, eventId);
            setParam(p, elements, "MEAS_REPORT_RANK", idx++, lineNo, file, eventId);
            setParam(p, elements, "BEST_CELL_MEAS_TYPE", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalEventBestCellEval(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcErabReleaseEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcErabReleaseEvent";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "TRIGGERING_NODE", 14, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_RESULT", 15, lineNo, file, eventId);
            setParam(p, elements, "ERAB_DATA_LOST_BITMAP", 16, lineNo, file, eventId);
            setParam(p, elements, "ERAB_DATA_LOST", 17, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_REQ_BITMAP", 18, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_REQ", 19, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_SUCC_BITMAP", 20, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_SUCC", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 22, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 23, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 24, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 25, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 26, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 27, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 28, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcErabRelease(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcInitialCtxtSetupEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcInitialCtxtSetup";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int row = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "ERAB_SETUP_RESULT_ARRAY", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_QCI_ARRAY", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_ARP_ARRAY", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_ATT_PA", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_SUCC_PA", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_UL_REQUESTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_DL_REQUESTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_UL_ADMITTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_DL_ADMITTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "INITIAL_CTXT_RESULT", row++, lineNo, file, eventId);
            } else
            {
                setParam(p, elements, "INITIAL_CTXT_RESULT", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL_BITMAP", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_REQ_BITMAP", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_REQ", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_SUCC_BITMAP", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_SUCC", row++, lineNo, file, eventId);
            }
            setParam(p, elements, "TIMESTAMP_START_HOUR", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", row++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcInitialCtxtSetup(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcInitialCtxtSetup(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcUeCtxtReleaseEnbEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcUeCtxtReleaseEnb";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "TRIGGERING_NODE", 14, lineNo, file, eventId);
            setParam(p, elements, "UE_RELEASE_CAUSE", 15, lineNo, file, eventId);
            setParam(p, elements, "ERAB_DATA_LOST_BITMAP", 16, lineNo, file, eventId);
            setParam(p, elements, "ERAB_DATA_LOST", 17, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_SUCC", 18, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_PREP_ERAB_FAIL_BITMAP", 19, lineNo, file, eventId);
            setParam(p, elements, "HO_OUT_PREP_ERAB_FAIL", 20, lineNo, file, eventId);
            setParam(p, elements, "ERAB_HO_PREP_DATA_LOST", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 22, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 23, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 24, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 25, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 26, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 27, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 28, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 29, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcUeCtxtReleaseEnb(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcErabSetupEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcErabSetup";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int row = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "ERAB_SETUP_RESULT_ARRAY", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_QCI_ARRAY", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_ARP_ARRAY", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_ATT_PA", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_SUCC_PA", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_UL_REQUESTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_DL_REQUESTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_UL_ADMITTED_GBR", row++, lineNo, file, eventId);
                setParam(p, elements, "ACCUMULATED_DL_ADMITTED_GBR", row++, lineNo, file, eventId);
            } else
            {
                setParam(p, elements, "ERAB_SETUP_RESULT", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL_BITMAP", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_REQ_BITMAP", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_REQ", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_SUCC_BITMAP", row++, lineNo, file, eventId);
                setParam(p, elements, "ERAB_SETUP_SUCC", row++, lineNo, file, eventId);
            }
            setParam(p, elements, "TIMESTAMP_START_HOUR", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", row++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", row++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalProcErabSetup(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcErabSetup(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcS1SigConnSetupEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcS1SigConnSetup";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "S1_SIG_CONN_RESULT", 14, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 15, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 16, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 17, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 18, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 19, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 20, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 22, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcS1SigConnSetup(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalProcRrcConnSetupEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcRrcConnSetup";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "INITIAL_UE_IDENTITY_TYPE", 14, lineNo, file, eventId);
            setParam(p, elements, "INITIAL_UE_IDENTITY", 15, lineNo, file, eventId);
            setParam(p, elements, "RRC_ESTABL_CAUSE", 16, lineNo, file, eventId);
            setParam(p, elements, "RRC_RESULT", 17, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 18, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 19, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 20, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 22, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 23, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 24, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 25, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcRrcConnSetup(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalPerUeRbTrafficRepEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalPerUeRbTrafficRep";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        int row = 14;
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "BEARER_ID", row++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
                setParam(p, elements, "BEARER_TYPE", row++, lineNo, file, eventId);
            setParam(p, elements, "ERAB_ID", row++, lineNo, file, eventId);
            if(version.equals(Main.ENB_N2_VERSION))
            {
                setParam(p, elements, "PER_PDCPVOL_DL_RB", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_PDCPVOL_UL_RB", row++, lineNo, file, eventId);
            }
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "PER_DRB_PDCP_ACKVOL_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_DRB_PDCP_TRANSVOL_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_DRB_PDCP_RECVOL_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_DRB_DL_RLC_DELAY", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_DRB_DL_RLC_DELAY_SAMPL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_DRB_LAT_TIME_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_DRB_LAT_SAMPL_DL", row++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalPerUeRbTrafficRep(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalPerUeRbTrafficRep(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalPerUeTrafficRepEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalPerUeTrafficRep";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "PER_UE_THP_PDCPVOL_TRUNK_DL", 14, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_THP_TIME_DL", 15, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_LAT_TIME_DL", 16, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_LAT_SAMPL_DL", 17, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_THP_PDCPVOL_TRUNK_UL", 18, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_THP_TIME_UL", 19, lineNo, file, eventId);
            setParam(p, elements, "PER_SCHED_RESTRICT_UE_CAT_UL", 20, lineNo, file, eventId);
            setParam(p, elements, "PER_SCHED_RESTRICT_UE_CAT_DL", 21, lineNo, file, eventId);
            setParam(p, elements, "PER_SCHED_ACTIVITY_UE_UL", 22, lineNo, file, eventId);
            setParam(p, elements, "PER_SCHED_ACTIVITY_UE_DL", 23, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_TR_DL", 24, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_REC_DL", 25, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_LOST_HO_DL", 26, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_LOST_PELR_DL", 27, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_REC_UL", 28, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_LOST_UL", 29, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PDCP_SRB_ACKVOL_DL", 30, lineNo, file, eventId);
            int row = 31;
            if(version.equals(Main.ENB_N2_VERSION))
                setParam(p, elements, "PER_UE_PDCP_DRB_ACKVOL_DL", row++, lineNo, file, eventId);
            else
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "PER_UE_PDCP_SRB_TRANSVOL_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_UE_PDCP_SRB_RECVOL_UL", row++, lineNo, file, eventId);
            }
            setParam(p, elements, "PER_UE_RLC_ACK_DL", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_NACK_DL", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_ACK_UL", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_NACK_UL", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_LOST_PELR_UU_DL", row++, lineNo, file, eventId);
            if(version.equals(Main.ENB_N2_VERSION))
                setParam(p, elements, "PER_UE_DL_RLC_DELAY", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_DL_MAC_DELAY", row++, lineNo, file, eventId);
            if(version.equals(Main.ENB_N2_VERSION))
                setParam(p, elements, "PER_UE_DL_RLC_DELAY_SAMPL_DL", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_DL_ACK_QPSK", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_DL_NACK_QPSK", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_UL_SUCC_QPSK", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_UL_FAIL_QPSK", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_DL_ACK_16QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_DL_NACK_16QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_UL_SUCC_16QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_UL_FAIL_16QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_DL_ACK_64QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "UE_HARQ_DL_NACK_64QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_MAC_DTX_UL_QPSK", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_MAC_DTX_DL_QPSK", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_MAC_DTX_DL_16QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_MAC_DTX_UL_16QAM", row++, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_MAC_DTX_DL_64QAM", row++, lineNo, file, eventId);
            if(version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "RADIOTHP_VOL_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "RADIOTHP_RES_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "RADIOTHP_VOL_UL", row++, lineNo, file, eventId);
                setParam(p, elements, "RADIOTHP_RES_UL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_UE_PACKET_FWD_DL", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_UE_PACKET_LOST_PELR_UU_DL_RLCUM", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_UE_THP_DL_DRB", row++, lineNo, file, eventId);
                setParam(p, elements, "PER_UE_THP_UL_DRB", row++, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version.equals(Main.ENB_T3_VERSION))
                event = new InternalPerUeTrafficRep(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalPerUeTrafficRep(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalPerRadioUeMeasurementEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalPerRadioUeMeasurement";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "RANK_TX_0", 14, lineNo, file, eventId);
            setParam(p, elements, "RANK_TX_1", 15, lineNo, file, eventId);
            setParam(p, elements, "RANK_TX_2", 16, lineNo, file, eventId);
            setParam(p, elements, "RANK_TX_3", 17, lineNo, file, eventId);
            setParam(p, elements, "RANK_TX_4", 18, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_0", 19, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_1", 20, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_2", 21, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_3", 22, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_4", 23, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_5", 24, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_6", 25, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_7", 26, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_8", 27, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_9", 28, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_10", 29, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_11", 30, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_12", 31, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_13", 32, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_14", 33, lineNo, file, eventId);
            setParam(p, elements, "CQI_REPORTED_15", 34, lineNo, file, eventId);
            setParam(p, elements, "RANK_REPORTED_0", 35, lineNo, file, eventId);
            setParam(p, elements, "RANK_REPORTED_1", 36, lineNo, file, eventId);
            setParam(p, elements, "TBSPWRRESTRICTED", 37, lineNo, file, eventId);
            setParam(p, elements, "TBSPWRUNRESTRICTED", 38, lineNo, file, eventId);
            if(!version.equals(Main.ENB_T3_VERSION))
            {
                setParam(p, elements, "RADIOTHP_VOL_DL", 39, lineNo, file, eventId);
                setParam(p, elements, "RADIOTHP_RES_DL", 40, lineNo, file, eventId);
                setParam(p, elements, "RADIOTHP_VOL_UL", 41, lineNo, file, eventId);
                setParam(p, elements, "RADIOTHP_RES_UL", 42, lineNo, file, eventId);
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            if(version == Main.ENB_T3_VERSION)
                event = new InternalPerRadioUeMeasurement(p);
            else
                event = new com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalPerRadioUeMeasurement(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private byte[] internalPerRadioCellMeasurementEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalPerRadioCellMeasurement";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            int idx = 2;
            setParam(p, elements, "TIMESTAMP_HOUR", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_MINUTE", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_SECOND", idx++, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_MILLISEC", idx++, lineNo, file, eventId);
            setParam(p, elements, "SCANNER_ID", idx++, lineNo, file, eventId);
            setParam(p, elements, "RBS_MODULE_ID", idx++, lineNo, file, eventId);
            setParam(p, elements, "GLOBAL_CELL_ID", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_0", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_1", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_2", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_3", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_4", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_5", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_6", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_7", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_8", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_9", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_10", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_11", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_12", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_13", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_14", idx++, lineNo, file, eventId);
            setParam(p, elements, "NOISEINTERF_MEAS_15", idx++, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalPerRadioCellMeasurement(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode(eventId);
    }

    private void processCommonFields(Properties p, String elements[], int lineNo, String file, int eventId, String version)
        throws ArrayIndexOutOfBoundsException
    {
        setParam(p, elements, "TIMESTAMP_HOUR", 2, lineNo, file, eventId);
        setParam(p, elements, "TIMESTAMP_MINUTE", 3, lineNo, file, eventId);
        setParam(p, elements, "TIMESTAMP_SECOND", 4, lineNo, file, eventId);
        setParam(p, elements, "TIMESTAMP_MILLISEC", 5, lineNo, file, eventId);
        setParam(p, elements, "SCANNER_ID", 6, lineNo, file, eventId);
        setParam(p, elements, "RBS_MODULE_ID", 7, lineNo, file, eventId);
        setParam(p, elements, "GLOBAL_CELL_ID", 8, lineNo, file, eventId);
        setParam(p, elements, "ENBS1APID", 9, lineNo, file, eventId);
        setParam(p, elements, "MMES1APID", 10, lineNo, file, eventId);
        setParam(p, elements, "GUMMEI", 11, lineNo, file, eventId);
        setParam(p, elements, "RAC_UE_REF", 12, lineNo, file, eventId);
        setParam(p, elements, "TRACE_RECORDING_SESSION_REFERENCE", 13, lineNo, file, eventId);
    }

    protected byte[] processHeaderRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("EnodeB Stream Header");
        if(elements.length < 14)
        {
            byte byteArray[] = new byte[404];
            byteArray[0] = 1;
            byteArray[1] = -108;
            int fieldIndex = 0;
            setByte(byteArray, 3, elements[fieldIndex], file, lineNo, fieldIndex);
            byte stringBytes[] = elements[1].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 4, Math.min(stringBytes.length, 5));
            stringBytes = elements[2].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 9, Math.min(stringBytes.length, 5));
            fieldIndex = 3;
            short year = Short.parseShort(elements[fieldIndex]);
            byteArray[14] = (byte)(year >> 8);
            byteArray[15] = (byte)year;
            fieldIndex = 4;
            setByte(byteArray, 16, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 5;
            setByte(byteArray, 17, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 6;
            setByte(byteArray, 18, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 7;
            setByte(byteArray, 19, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex = 8;
            setByte(byteArray, 20, elements[fieldIndex], file, lineNo, fieldIndex);
            stringBytes = elements[9].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 21, Math.min(stringBytes.length, 128));
            stringBytes = elements[10].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 149, Math.min(stringBytes.length, 255));
            return byteArray;
        } else
        {
            byte byteArray[] = new byte[412];
            byteArray[0] = 1;
            byteArray[1] = -100;
            int fieldIndex = 1;
            setByte(byteArray, 3, "1", file, lineNo, fieldIndex);
            fieldIndex++;
            byte stringBytes[] = elements[fieldIndex].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 4, Math.min(stringBytes.length, 5));
            fieldIndex++;
            stringBytes = elements[fieldIndex].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 9, Math.min(stringBytes.length, 5));
            fieldIndex++;
            short year = Short.parseShort(elements[fieldIndex]);
            byteArray[14] = (byte)(year >> 8);
            byteArray[15] = (byte)year;
            fieldIndex++;
            setByte(byteArray, 16, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 17, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 18, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 19, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            setByte(byteArray, 20, elements[fieldIndex], file, lineNo, fieldIndex);
            fieldIndex++;
            stringBytes = elements[fieldIndex].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 21, Math.min(stringBytes.length, 128));
            fieldIndex++;
            stringBytes = elements[fieldIndex].getBytes();
            System.arraycopy(stringBytes, 0, byteArray, 149, Math.min(stringBytes.length, 255));
            fieldIndex++;
            short scanner = Short.parseShort(elements[fieldIndex]);
            byteArray[404] = (byte)(scanner >> 8);
            byteArray[405] = (byte)scanner;
            fieldIndex++;
            short causeOfheader = Short.parseShort(elements[fieldIndex]);
            byteArray[406] = (byte)(causeOfheader >> 8);
            byteArray[407] = (byte)causeOfheader;
            fieldIndex++;
            int droppedEvents = Integer.parseInt(elements[fieldIndex]);
            byteArray[408] = (byte)(droppedEvents >> 24);
            byteArray[409] = (byte)(droppedEvents >> 16);
            byteArray[410] = (byte)(droppedEvents >> 8);
            byteArray[411] = (byte)droppedEvents;
            return byteArray;
        }
    }

    protected byte[] processFooterRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("EnodeB Footer");
        byte byteArray[] = new byte[12];
        byteArray[0] = 0;
        byteArray[1] = 12;
        int fieldIndex = 0;
        setByte(byteArray, 3, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 1;
        short year = Short.parseShort(elements[fieldIndex]);
        byteArray[4] = (byte)(year >> 8);
        byteArray[5] = (byte)year;
        fieldIndex = 2;
        setByte(byteArray, 6, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 3;
        setByte(byteArray, 7, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 4;
        setByte(byteArray, 8, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 5;
        setByte(byteArray, 9, elements[fieldIndex], file, lineNo, fieldIndex);
        fieldIndex = 6;
        setByte(byteArray, 10, elements[fieldIndex], file, lineNo, fieldIndex);
        return byteArray;
    }

    private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events_Utility.jar
	Total time: 99 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/