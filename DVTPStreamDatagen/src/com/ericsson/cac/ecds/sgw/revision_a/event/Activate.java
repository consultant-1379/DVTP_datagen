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

public class Activate extends SgwEvent{

    public Activate()
        throws InvocationTargetException
    {
    }

    public Activate(byte source[])
        throws InvocationTargetException
    {
        super(source);
    }

    public Activate(Properties p)
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
    private static int eventId = 1;
    private static String name = "ACTIVATE";

    static 
    {
        eventFormat = new ArrayList();
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.RecordType.class, "HEADER_RECORD_TYPE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventId.class, "HEADER_EVENT_ID", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.EventResult.class, "HEADER_EVENT_RESULT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeHour.class, "HEADER_TIME_HOUR", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMinute.class, "HEADER_TIME_MINUTE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeSecond.class, "HEADER_TIME_SECOND", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ActivationType.class, "ACTIVATION_TYPE", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Rat.class, "RAT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.CauseProtType.class, "CAUSE_PROT_TYPE", false));
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
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TimeMillisecond.class, "TIME_MILLISECOND", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Duration.class, "DURATION", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.RequestRetries.class, "REQUEST_RETRIES", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.Ptmsi.class, "PTMSI", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MsRequestedApn.class, "MS_REQUESTED_APN", true));
        
        //QOS_REQUESTED
        
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ReliabilityClass.class, "QOS_REQ_RELIABILITY_CLASS", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DelayClass.class, "QOS_REQ_DELAY_CLASS", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.PrecedenceClass.class, "QOS_REQ_PRECEDENCE_CLASS", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.PeakThroughput.class, "QOS_REQ_PEAK_THROUGHPUT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MeanThroughput.class, "QOS_REQ_MEAN_THROUGHPUT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DeliveryOfErrSdu.class, "QOS_REQ_DELIVERY_OF_ERR_SDU", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DeliveryOrder.class, "QOS_REQ_DELIVERY_ORDER", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TrafficClass.class, "QOS_REQ_TRAFFIC_CLASS", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MaxSduSize.class, "QOS_REQ_MAX_SDU_SIZE", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MbrUl.class, "QOS_REQ_MBR_UL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MbrDl.class, "QOS_REQ_MBR_DL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SduErrorRatio.class, "QOS_REQ_SDU_ERROR_RATIO", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ResidualBer.class, "QOS_REQ_RESIDUAL_BER", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TrafficHandlingPriority.class, "QOS_REQ_TRAFFIC_HANDLING_PRIORITY", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TransferDelay.class, "QOS_REQ_TRANSFER_DELAY", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.GbrUl.class, "QOS_REQ_GBR_UL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.GbrDl.class, "QOS_REQ_GBR_DL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SourceStatisticsDescriptor.class, "QOS_REQ_SOURCE_STATISTICS_DESCRIPTOR", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SignallingIndication.class, "QOS_REQ_SIGNALLING_INDICATION", true));
     
        
        //QOS_NEGOTIATED
        
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ReliabilityClass.class, "QOS_NEG_RELIABILITY_CLASS", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DelayClass.class, "QOS_NEG_DELAY_CLASS", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.PrecedenceClass.class, "QOS_NEG_PRECEDENCE_CLASS", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.PeakThroughput.class, "QOS_NEG_PEAK_THROUGHPUT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MeanThroughput.class, "QOS_NEG_MEAN_THROUGHPUT", false));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DeliveryOfErrSdu.class, "QOS_NEG_DELIVERY_OF_ERR_SDU", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.DeliveryOrder.class, "QOS_NEG_DELIVERY_ORDER", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TrafficClass.class, "QOS_NEG_TRAFFIC_CLASS", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MaxSduSize.class, "QOS_NEG_MAX_SDU_SIZE", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MbrUl.class, "QOS_NEG_MBR_UL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.MbrDl.class, "QOS_NEG_MBR_DL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SduErrorRatio.class, "QOS_NEG_SDU_ERROR_RATIO", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ResidualBer.class, "QOS_NEG_RESIDUAL_BER", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TrafficHandlingPriority.class, "QOS_NEG_TRAFFIC_HANDLING_PRIORITY", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.TransferDelay.class, "QOS_NEG_TRANSFER_DELAY", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.GbrUl.class, "QOS_NEG_GBR_UL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.GbrDl.class, "QOS_NEG_GBR_DL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SourceStatisticsDescriptor.class, "QOS_NEG_SOURCE_STATISTICS_DESCRIPTOR", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.SignallingIndication.class, "QOS_NEG_SIGNALLING_INDICATION", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ArpPl.class, "QOS_NEG_ARP_PL", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ArpPci.class, "QOS_NEG_ARP_PCI", true));
        eventFormat.add(new SgwEventFormat(com.ericsson.cac.ecds.sgw.revision_a.eventparameter.ArpPvi.class, "QOS_NEG_ARP_PVI", true));
        
    }



}
