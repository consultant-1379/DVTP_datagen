/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.sgw.eventparameter.base;

import com.ericsson.cac.ecds.enodeb.eventparameter.base.EnodebEventParameter;
import com.ericsson.cac.ecds.interfaces.EventParameterInterface;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class SgwEventParameter
    implements EventParameterInterface
{

    public SgwEventParameter()
    {
        data = BigInteger.ZERO;
        originalValue = "";
        Properties p = new Properties();
        init(p);
    }

    public SgwEventParameter(Properties p)
    {
        data = BigInteger.ZERO;
        originalValue = "";
        init(p);
    }

    public SgwEventParameter(byte binaryData[], int bitOffset, boolean useValidityBit)
    {
        data = BigInteger.ZERO;
        originalValue = "";
        int beginningByte = bitOffset / 8;
        int bitSize = getNumBits();
        if(useValidityBit)
            bitSize++;
        int lsbBit = bitOffset + bitSize;
        int endingByte = (lsbBit - 1) / 8;
        int tmpByteArrayLength = (endingByte - beginningByte) + 2;
        byte tmpByteArray[] = new byte[tmpByteArrayLength];
        tmpByteArray[0] = 0;
        System.arraycopy(binaryData, beginningByte, tmpByteArray, 1, tmpByteArrayLength - 1);
        BigInteger newData = new BigInteger(tmpByteArray);
        int shiftSize = (8 - lsbBit % 8) % 8;
        newData = newData.shiftRight(shiftSize);
        BigInteger mask = BigInteger.ONE.shiftLeft(bitSize);
        mask = mask.subtract(BigInteger.ONE);
        newData = newData.and(mask);
        data = newData;
    }

    public SgwEventParameter(long newValue)
    {
        data = BigInteger.ZERO;
        originalValue = "";
        setValue(newValue);
    }

    public SgwEventParameter(String newString)
    {
        data = BigInteger.ZERO;
        originalValue = "";
        originalValue = newString;
        setValue(newString);
    }

    public BigInteger asBigInteger()
    {
        return data;
    }

    public long asLong()
    {
        return data.longValue();
    }

    public String asString()
    {
        return data.toString();
    }

    public BigInteger encodeToBigInteger(BigInteger bigInteger)
    {
        int numBits = getNumBits();
        
        if(hasValidityBit())
            if(getOriginalValue().length() == 0)
            {
                numBits = 1;
                data = BigInteger.ONE;
            } else
            {
                numBits++;
            }
        //System.out.println(getShortName()+"--->"+ getOriginalValue()+"--->"+ getOriginalValue().length()+"--->"+ numBits);
        bigInteger = bigInteger.shiftLeft(numBits);
        bigInteger = bigInteger.or(data);
        return bigInteger;
    }

    public abstract String getComment();

    public abstract String getDescription();

    public abstract String getName();

    public abstract int getNumBits();

    public int getNumBytes()
    {
        int bits = getNumBits();
        if(hasValidityBit())
            if(getOriginalValue().length() == 0)
                bits = 1;
            else
                bits++;
        bits += (8 - bits % 8) % 8;
        return bits / 8;
    }

    public abstract String getShortName();

    public abstract boolean hasValidityBit();

    public abstract boolean isValid();

    public void setValue(long newLong)
    {
        data = BigInteger.valueOf(newLong);
    }

    public void setValue(String newString)
    {
        byte byteArray[] = (byte[])null;
        if(getShortName().equalsIgnoreCase("APN"))
        {
            if(newString.length() != 0)
            {
                byteArray = new byte[newString.length() + 1 + 1];
                String substrings[] = newString.split("[.]");
                int index = 1;
                for(int i = 0; i < substrings.length; i++)
                {
                    byteArray[index] = (byte)substrings[i].length();
                    System.arraycopy(substrings[i].getBytes(), 0, byteArray, index + 1, substrings[i].length());
                    index += substrings[i].length() + 1;
                }

            } else
            {
                return;
            }
        } else
        {
            byteArray = new byte[newString.length() + 1];
            byteArray[0] = 0;
            System.arraycopy(newString.getBytes(), 0, byteArray, 1, newString.length());
        }
        data = new BigInteger(byteArray);
    }

    private void init(Properties p)
    {
        String shortName = getShortName();
        String value = p.getProperty(shortName);
        if(value == null || value.length() == 0)
        {
            value = localProperties.getProperty(getShortName());
            if(value == null)
                value = "";
            else
            if(log.isDebugEnabled())
                log.debug((new StringBuilder("Using default property value of ")).append(value).append(" for ").append(shortName).append(".").toString());
        } else{
        if(log.isDebugEnabled())
            log.debug((new StringBuilder("Using property value of ")).append(value).append(" for ").append(shortName).append(".").toString());
        log.debug((new StringBuilder("sgwEventParameter: init() - Using value of \"")).append(value).append("\" for ").append(shortName).toString());
        originalValue = value;
        setValue(value);
        }
    }

    protected abstract BigInteger getMinValue();

    protected abstract BigInteger getMaxValue();

    protected abstract boolean getCheckMinValue();

    protected abstract boolean getCheckMaxValue();

    protected abstract int getLengthBits();

    protected abstract int getValidLow();

    protected abstract int getValidHigh();

    protected BigInteger getData()
    {
        return data;
    }

    protected void setData(BigInteger data)
    {
        this.data = data;
    }

    public abstract void setHasValidityBit(boolean flag);

    public int calculatePaddingBits(int currentBitsize)
    {
        return 0;
    }

    public String getOriginalValue()
    {
        return originalValue;
    }

    private BigInteger data;
    private String originalValue;
    private static Properties defaultProperties;
    private static String defaultValuesPropertyFile;
    private static Properties localProperties;
    private static String localValuesPropertyFile;
    private static Log log;

    static 
    {
        defaultValuesPropertyFile = "sgw_parameter_defaults.properties";
        localValuesPropertyFile = "sgw_parameter.properties";
        log = LogFactory.getLog("fileLogger");
        defaultProperties = new Properties();
        URL url = ClassLoader.getSystemResource(defaultValuesPropertyFile);
        if(url == null)
        {
            url = com.ericsson.cac.ecds.sgw.eventparameter.base.SgwEventParameter.class.getClassLoader().getResource(defaultValuesPropertyFile);
            log.error((new StringBuilder(String.valueOf(defaultValuesPropertyFile))).append(" found @").append(url).toString());
        }
        if(url == null)
            log.error((new StringBuilder("Default Values Property File ")).append(defaultValuesPropertyFile).append(" not found in classpath.").toString());
        else
            try
            {
                defaultProperties.load(url.openStream());
            }
            catch(IOException e)
            {
                log.error((new StringBuilder("Failed to load the defaults property file ")).append(url.toString()).toString(), e);
            }
        localProperties = new Properties(defaultProperties);
        url = ClassLoader.getSystemResource(localValuesPropertyFile);
        if(url == null)
        {
            url = com.ericsson.cac.ecds.enodeb.eventparameter.base.EnodebEventParameter.class.getClassLoader().getResource(localValuesPropertyFile);
            log.warn((new StringBuilder(String.valueOf(localValuesPropertyFile))).append(" not found @").append(url).toString());
        }
        if(url == null)
        {
            if(log.isInfoEnabled())
                log.info((new StringBuilder("No local property file ")).append(localValuesPropertyFile).append(" was found on classpath.").toString());
        } else
        {
            try
            {
                localProperties.load(url.openStream());
            }
            catch(IOException e)
            {
                log.error((new StringBuilder("Failed to load the local property file ")).append(url.toString()).toString(), e);
            }
        }
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 89 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/