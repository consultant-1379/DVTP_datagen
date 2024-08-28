/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   SgwIpaddress.java

package com.ericsson.cac.ecds.sgw.eventparameter.base;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package com.ericsson.cac.ecds.sgw.eventparameter.base:
//            SgwEventParameter

public abstract class SgwIpaddress extends SgwEventParameter
{

    public SgwIpaddress()
    {
        processIpAddress();
    }

    public SgwIpaddress(Properties p)
    {
        super(p);
        processIpAddress();
    }

    public SgwIpaddress(byte binaryData[], int bitOffset, boolean useValidityBit)
    {
        super(binaryData, bitOffset, useValidityBit);
    }

    public SgwIpaddress(long newValue)
    {
        super(newValue);
        processIpAddress();
    }

    public SgwIpaddress(String newString)
    {
        super(newString);
        processIpAddress();
    }

    private void processIpAddress()
    {
        BigInteger data = getData();
        byte dataByteArray[] = data.toByteArray();
        String dataString = new String(dataByteArray);
        if(data ==BigInteger.ZERO || dataString.length() == 0 )
            dataString = "0.0.0.0";
        try
        {
            InetAddress ipAddr = InetAddress.getByName(dataString);
            byte ipAddrByteArray[] = ipAddr.getAddress();
            byte ipAddrByteArray2[] = new byte[ipAddrByteArray.length + 1];
            ipAddrByteArray2[0] = 0;
            System.arraycopy(ipAddrByteArray, 0, ipAddrByteArray2, 1, ipAddrByteArray.length);
            BigInteger ipAddrBigInt = new BigInteger(ipAddrByteArray2);
            setData(ipAddrBigInt);
        }
        catch(UnknownHostException e)
        {
        	e.printStackTrace();
            //log.error((new StringBuilder("UnknownHost when resolving IPV4 \"")).append(dataString).append("\"").toString(), e);
        }
    }

    public String asString()
    {
        BigInteger data = getData();
        byte dataByteArray[] = data.toByteArray();
        byte rawIpAddr[] = new byte[4];
        int sourceStartIdx = Math.max(0, dataByteArray.length - 4);
        int targetStartIdx = Math.max(0, 4 - dataByteArray.length);
        System.arraycopy(dataByteArray, sourceStartIdx, rawIpAddr, targetStartIdx, dataByteArray.length - sourceStartIdx);
        try
        {
            InetAddress ipAddress = InetAddress.getByAddress(rawIpAddr);
            return ipAddress.toString().substring(1);
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
            return "ERROR";
        }
    }

    protected abstract boolean getCheckMaxValue();

    protected abstract boolean getCheckMinValue();

    public abstract String getComment();

    public abstract String getDescription();

    protected abstract BigInteger getMaxValue();

    protected abstract BigInteger getMinValue();

    public abstract String getName();

    public abstract int getNumBits();

    public abstract String getShortName();

    public abstract boolean hasValidityBit();

    public abstract boolean isValid();

    protected abstract int getLengthBits();

    protected abstract int getValidHigh();

    protected abstract int getValidLow();

    
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\DVTP_datagen\temp\DVTP_datagen\DVTPAutomationDatagen\lib\ECDS_Events.jar
	Total time: 60 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/