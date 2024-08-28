/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.utility.streaming;

import com.ericsson.cac.ecds.enodeb.revision_k7.event.*;
import com.ericsson.cac.ecds.enodeb.revision_n2.event.InternalProcHoPrepS1Out;
import com.ericsson.cac.ecds.interfaces.EventInterface;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.log4j.Logger;

// Referenced classes of package com.ericsson.cac.ecds.utility.streaming:
//            EcdsEventsStreamUtilityCommon, EcdsEventsStreamUtilityInterface

public class EnodebK7EventsStream extends EcdsEventsStreamUtilityCommon
    implements EcdsEventsStreamUtilityInterface
{

    public EnodebK7EventsStream()
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

        default:
            log.error(formatMsg((new StringBuilder("Unsupported event. EventId:")).append(eventId).toString(), file, lineNo, fieldIdx));
            break;
        }
        return eventBA;
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
        return event.encode();
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
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcHoExecX2Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_PREP_IN_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "RANDOM_ACCESS_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_IN_PREP_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_IN_PREP_ERAB_SUCC_BITMAP", idx++, lineNo, file, eventId);
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
            event = new InternalProcHoPrepX2In(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcHoPrepX2Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
        return event.encode();
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
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcHoExecS1Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
            setParam(p, elements, "NEIGHBOR_CGI", idx++, lineNo, file, eventId);
            setParam(p, elements, "PROC_HO_PREP_IN_RESULT", idx++, lineNo, file, eventId);
            setParam(p, elements, "RANDOM_ACCESS_TYPE", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_IN_PREP_ERAB_REQ_BITMAP", idx++, lineNo, file, eventId);
            setParam(p, elements, "HO_IN_PREP_ERAB_SUCC_BITMAP", idx++, lineNo, file, eventId);
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
            event = new InternalProcHoPrepS1In(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
    }

    private byte[] internalProcHoPrepS1OutEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcHoPrepS1OutEvent";
        log.debug((new StringBuilder("EnodeB K7 Event ")).append(eventName).toString());
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
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcHoPrepS1Out(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
        return event.encode();
    }

    private byte[] internalProcErabReleaseEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "internalProcErabRelease";
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
        return event.encode();
    }

    private byte[] internalProcInitialCtxtSetupEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcInitialCtxtSetup";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "INITIAL_CTXT_RESULT", 14, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL_BITMAP", 15, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL", 16, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_REQ_BITMAP", 17, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_REQ", 18, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_SUCC_BITMAP", 19, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_SUCC", 20, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 22, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 23, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 24, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 25, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 26, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 27, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 28, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcInitialCtxtSetup(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
            setParam(p, elements, "EUTRANCELLFDD_FROID", 15, lineNo, file, eventId);
            setParam(p, elements, "UE_RELEASE_CAUSE", 16, lineNo, file, eventId);
            setParam(p, elements, "ERAB_DATA_LOST_BITMAP", 17, lineNo, file, eventId);
            setParam(p, elements, "ERAB_DATA_LOST", 18, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_SUCC_BITMAP", 19, lineNo, file, eventId);
            setParam(p, elements, "ERAB_RELEASE_SUCC", 20, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 22, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 23, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 24, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 25, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 26, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 27, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 28, lineNo, file, eventId);
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
        return event.encode();
    }

    private byte[] internalProcErabSetupEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalProcErabSetup";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "ERAB_SETUP_RESULT", 14, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL_BITMAP", 15, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_LICENSE_FAIL", 16, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_REQ_BITMAP", 17, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_REQ", 18, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_SUCC_BITMAP", 19, lineNo, file, eventId);
            setParam(p, elements, "ERAB_SETUP_SUCC", 20, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_HOUR", 21, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MINUTE", 22, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_SECOND", 23, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_START_MILLISEC", 24, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_HOUR", 25, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MINUTE", 26, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_SECOND", 27, lineNo, file, eventId);
            setParam(p, elements, "TIMESTAMP_STOP_MILLISEC", 28, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalProcErabSetup(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
        return event.encode();
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
        return event.encode();
    }

    private byte[] internalPerUeRbTrafficRepEvent(String elements[], int lineNo, String file, int eventId, String version)
    {
        String eventName = "InternalPerUeRbTrafficRep";
        log.debug((new StringBuilder("EnodeB Event ")).append(eventName).toString());
        Properties p = new Properties();
        try
        {
            processCommonFields(p, elements, lineNo, file, eventId, version);
            setParam(p, elements, "BEARER_ID", 14, lineNo, file, eventId);
            setParam(p, elements, "ERAB_ID", 15, lineNo, file, eventId);
            setParam(p, elements, "PER_PDCPVOL_DL_RB", 16, lineNo, file, eventId);
            setParam(p, elements, "PER_PDCPVOL_UL_RB", 17, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalPerUeRbTrafficRep(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
            setParam(p, elements, "PER_UE_PDCP_DRB_ACKVOL_DL", 31, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_ACK_DL", 32, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_NACK_DL", 33, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_ACK_UL", 34, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_RLC_NACK_UL", 35, lineNo, file, eventId);
            setParam(p, elements, "PER_UE_PACKET_LOST_PELR_UU_DL", 36, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalPerUeTrafficRep(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
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
            setParam(p, elements, "RADIOTHP_VOL_DL", 39, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_RES_DL", 40, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_VOL_DL_SEL_TBS", 41, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_RES_DL_SEL_TBS", 42, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_VOL_UL", 43, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_RES_UL", 44, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_VOL_UL_SEL_TBS", 45, lineNo, file, eventId);
            setParam(p, elements, "RADIOTHP_RES_UL_SEL_TBS", 46, lineNo, file, eventId);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        EventInterface event;
        try
        {
            event = new InternalPerRadioUeMeasurement(p);
            if(log.isDebugEnabled())
                log.debug(event.toString());
        }
        catch(InvocationTargetException e)
        {
            log.error((new StringBuilder("Error creating ")).append(eventName).append(" event").toString(), e);
            return null;
        }
        return event.encode();
    }

    protected byte[] processHeaderRecord(String elements[], int lineNo, String file, String version)
    {
        log.debug("EnodeB Stream Header");
        eventRecordBuffer.append("EnodeB Stream Header\n");
        log.debug("EnodeB Stream Header");
        byte byteArray[] = new byte[412];
        byteArray[0] = 1;
        byteArray[1] = -100;
        byteArray[2] = 1;
        int fieldIndex = 2;
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

    private static final Logger log = Logger.getLogger("fileLogger");
    private StringBuffer eventRecordBuffer;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events_Utility.jar
	Total time: 81 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/