/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.sgw.revision_a.event;

import com.ericsson.cac.ecds.sgw.base.SgwEvent;
import com.ericsson.cac.ecds.sgw.base.SgwEventFormat;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;

public class SessionInfo extends SgwEvent
{

    public SessionInfo()
        throws InvocationTargetException
    {
    }

    public SessionInfo(byte source[])
        throws InvocationTargetException
    {
        super(source);
    }

    public SessionInfo(Properties p)
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
    private static int eventId = 6;
    private static String name = "SESSION_INFO";

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
        
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 61 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/