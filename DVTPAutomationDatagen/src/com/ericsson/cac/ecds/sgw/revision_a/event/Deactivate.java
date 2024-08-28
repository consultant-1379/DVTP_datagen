package com.ericsson.cac.ecds.sgw.revision_a.event;

import com.ericsson.cac.ecds.sgw.base.SgwEvent;
import com.ericsson.cac.ecds.sgw.base.SgwEventFormat;
import com.ericsson.cac.ecds.sgw.eventparameter.base.BearerStruct;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Apn;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ApnAmbrDl;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ApnAmbrUl;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.BearerId;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.CauseCode;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.CauseProtocol;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Duration;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Eci;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventId;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventResult;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventTrigger;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Imeisv;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Imsi;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv4;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv6;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Mcc;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Mnc;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.OriginatingNode;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SubCauseCode;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Tac;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeHour;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMillisecond;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMinute;
import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeSecond;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;


public class Deactivate extends SgwEvent{


    public Deactivate()
        throws InvocationTargetException
    {
    }

    public Deactivate(byte source[])
        throws InvocationTargetException
    {
        super(source);
    }

    public Deactivate(Properties p)
        throws InvocationTargetException
    {
    	super(p);
    	
        
    }

    protected ArrayList getEventFormat()
    {
        return eventFormat;
    }

    public int getEventId()
    {
        return eventId;
    }

    public String getName()
    {
        return name;
    }

    private static ArrayList eventFormat;
    private static int eventId = 4;
    private static String name = "DEACTIVATE";

    static 
    {
        eventFormat = new ArrayList();
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.RecordType.class, "HEADER_RECORD_TYPE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventId.class, "HEADER_EVENT_ID", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventResult.class, "HEADER_EVENT_RESULT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeHour.class, "HEADER_TIME_HOUR", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMinute.class, "HEADER_TIME_MINUTE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeSecond.class, "HEADER_TIME_SECOND", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Rat.class, "RAT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DeactivationTrigger.class, "DEACTIVATION_TRIGGER", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.CauseCode.class, "CAUSE_CODE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SubCauseCode.class, "SUB_CAUSE_CODE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Mcc.class, "LOCATION_MCC", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Mnc.class, "LOCATION_MNC", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Lac.class, "LOCATION_LAC", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Rac.class, "LOCATION_RAC", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ci.class, "LOCATION_CI", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Sac.class, "LOCATION_SAC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Imsi.class, "IMSI", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Imeisv.class, "IMEISV", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ggsn.class, "GGSN", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Apn.class, "APN", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.HomezoneIdentity.class, "HOMEZONE_IDENTITY", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Msisdn.class, "MSISDN", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Nsapi.class, "NSAPI", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv4.class, "MS_ADDRESS_IPV4", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv6.class, "MS_ADDRESS_IPV6", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.CauseProtType.class, "CAUSE_PROT_TYPE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMillisecond.class, "TIME_MILLISECOND", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Duration.class, "DURATION", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ptmsi.class, "PTMSI", true));
        
        
    }


}
