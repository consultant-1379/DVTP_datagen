

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

public class DataUsage extends SgwEvent
{

    public DataUsage()
        throws InvocationTargetException
    {
    }

    public DataUsage(byte source[])
        throws InvocationTargetException
    {
        super(source);
    }

    public DataUsage(Properties p)
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
    private static int eventId = 7;
    private static String name = "DATA_USAGE";

    static 
    {
        eventFormat = new ArrayList();
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.RecordType.class, "HEADER_RECORD_TYPE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventId.class, "HEADER_EVENT_ID", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventResult.class, "HEADER_EVENT_RESULT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeHour.class, "HEADER_TIME_HOUR", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMinute.class, "HEADER_TIME_MINUTE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeSecond.class, "HEADER_TIME_SECOND", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMillisecond.class, "HEADER_TIME_MILLISECOND", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Duration.class, "HEADER_DURATION", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Imsi.class, "UE_INFO_IMSI", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ImsiValidation.class, "UE_INFO_IMSI_VALIDATION", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Imeisv.class, "UE_INFO_IMEISV", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Mcc.class, "UE_INFO_TAI_MCC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Mnc.class, "UE_INFO_TAI_MNC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Tac.class, "UE_INFO_TAI_TAC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Eci.class, "UE_INFO_ECI", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Lac.class, "UE_INFO_LAC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Rac.class, "UE_INFO_RAC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ci.class, "UE_INFO_CI", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Sac.class, "UE_INFO_SAC", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.BearerId.class, "PDN_INFO_DEFAULT_BEARER_ID", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Apn.class, "PDN_INFO_APN", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv4.class, "PDN_INFO_PGW_ADDRESS_IPV4", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv6.class, "PDN_INFO_PGW_ADDRESS_IPV6", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv4.class, "PDN_INFO_ALLOCATED_UE_ADDRESS_IPV4", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ipv6.class, "PDN_INFO_ALLOCATED_UE_ADDRESS_IPV6", true));
        
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ApnAmbrUl.class, "PDN_INFO_APN_AMBR_APN_AMBR_UL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ApnAmbrDl.class, "PDN_INFO_APN_AMBR_APN_AMBR_DL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.PdnId.class, "PDN_INFO_PDN_ID", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.RuleSpace.class, "PDN_INFO_RULE_SPACE", true));
        
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.eventparameter.base.BearerUsageStruct.class, "BEARER_USAGE_STRUCT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.eventparameter.base.SessionUsageStruct.class, "SESSION_USAGE_STRUCT", false));
        /*
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.BearerId.class, "BEARER_USAGE_INFO_EPS_BEARER_ID", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.BearerCause.class, "BEARER_USAGE_INFO_BEARER_CAUSE", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ULPackets.class, "BEARER_USAGE_INFO_BEARER_UL_PACKETS", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DLPackets.class, "BEARER_USAGE_INFO_BEARER_DL_PACKETS", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ULBytes.class, "BEARER_USAGE_INFO_BEARER_UL_BYTES", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DLBytes.class, "BEARER_USAGE_INFO_BEARER_DL_BYTES", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.RatingGroup.class, "SESSION_USAGE_INFO_RATING_GROUP", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ServiceIdentifier.class, "SESSION_USAGE_INFO_SERVICE_IDENTIFIER", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.UriName.class, "SESSION_USAGE_INFO_URI_NAME", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.UriId.class, "SESSION_USAGE_INFO_URI_ID", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ULBytes.class, "SESSION_USAGE_INFO_SERVICE_UL_BYTES", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DLBytes.class, "SESSION_USAGE_INFO_SERVICE_DL_BYTES", true));
        */
        
        
    }
}

