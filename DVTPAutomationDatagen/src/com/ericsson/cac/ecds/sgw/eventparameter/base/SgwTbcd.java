/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.sgw.eventparameter.base;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package com.ericsson.cac.ecds.sgw.eventparameter.base:
//            SgwEventParameter

public abstract class SgwTbcd extends SgwEventParameter
{

    public SgwTbcd()
    {
        swapNybbles = true;
        reverseBcd = false;
        tbcdEncode();
    }

    public SgwTbcd(Properties p)
    {
        super(p);
        swapNybbles = true;
        reverseBcd = false;
        tbcdEncode();
    }

    public SgwTbcd(byte binaryData[], int bitOffset, boolean useValidityBit)
    {
        super(binaryData, bitOffset, useValidityBit);
        swapNybbles = true;
        reverseBcd = false;
        BigInteger data = getData();
        if(getSwapNybbles())
            data = swapNybbles(data);
        boolean reverse = getReverseBcd();
        if(reverse)
            data = reverseBcd(data);
        setData(data);
    }

    public SgwTbcd(long newValue)
    {
        super(newValue);
        swapNybbles = true;
        reverseBcd = false;
        tbcdEncode();
    }

    public SgwTbcd(String newString)
    {
        super(newString);
        swapNybbles = true;
        reverseBcd = false;
        tbcdEncode();
    }

    protected abstract boolean getCheckMaxValue();

    protected abstract boolean getCheckMinValue();

    public abstract String getComment();

    public abstract String getDescription();

    protected abstract int getLengthBits();

    protected abstract BigInteger getMaxValue();

    protected abstract BigInteger getMinValue();

    public abstract String getName();

    public abstract int getNumBits();

    public abstract String getShortName();

    protected abstract int getValidHigh();

    protected abstract int getValidLow();

    public abstract boolean hasValidityBit();

    public abstract boolean isValid();

    private void tbcdEncode()
    {
        if(getOriginalValue().length() == 0)
            return;
        BigInteger data = getData();
        String value = data.toString();
        int maxDigits = getNumBits() / 4;
        BigInteger result = BigInteger.ZERO;
        char valueArray[] = value.toCharArray();
        int i = 0;
        for(i = 0; i < Math.min(valueArray.length, maxDigits); i++)
        {
            Integer digit = (Integer)digitMap.get(Character.valueOf(valueArray[i]));
            if(digit == null)
            {
                if(log.isWarnEnabled())
                    log.warn((new StringBuilder("Invalid TBCD digit of '")).append(valueArray[i]).append("'").toString());
                digit = (Integer)digitMap.get("P");
            }
            result = result.shiftLeft(4);
            result = result.or(BigInteger.valueOf(digit.intValue()));
        }

        for(; i < maxDigits; i++)
        {
            result = result.shiftLeft(4);
            result = result.or(BigInteger.valueOf(padding));
        }

        setData(result);
    }

    public BigInteger encodeToBigInteger(BigInteger bigInteger)
    {
        int numBits = getNumBits();
        BigInteger data = getData();
        //System.out.println(getShortName()+"--->"+ getOriginalValue()+"--->"+ getOriginalValue().length());
        if(hasValidityBit())
        {
            if(getOriginalValue().length() == 0)
            {
                numBits = 1;
                data = BigInteger.ONE;
                bigInteger = bigInteger.shiftLeft(numBits);
                //System.out.println(getShortName()+"--->"+ getOriginalValue()+"--->"+ getOriginalValue().length()+"--->"+ numBits);
                return bigInteger.or(data);
            }
            numBits++;
            
        	
        }
        //System.out.println(getShortName()+"--->"+ getOriginalValue()+"--->"+ getOriginalValue().length()+"--->"+ numBits);
        
        bigInteger = bigInteger.shiftLeft(numBits);
        if(getSwapNybbles())
            data = swapNybbles(data);
        if(getReverseBcd())
            data = reverseBcd(data);
        bigInteger = bigInteger.or(data);
        return bigInteger;
    }

    private BigInteger swapNybbles(BigInteger source)
    {
        byte byteArray[] = source.toByteArray();
        byte targetByteArray[] = new byte[byteArray.length + 1];
        targetByteArray[0] = 0;
        for(int i = 0; i < byteArray.length; i++)
        {
            targetByteArray[i + 1] = (byte)((byteArray[i] & 240) / 16);
            targetByteArray[i + 1] += (byte)((byteArray[i] & 15) * 16);
        }

        return new BigInteger(targetByteArray);
    }

    private BigInteger reverseBcd(BigInteger source)
    {
        BigInteger mask = BigInteger.ONE.shiftLeft(4).subtract(BigInteger.ONE);
        BigInteger result = BigInteger.ZERO;
        for(; source.bitLength() > 0; source = source.shiftRight(4))
        {
            BigInteger digit = source.and(mask);
            result = result.shiftLeft(4);
            result = result.or(digit);
        }

        return result;
    }

    public void setValue(String newString)
    {
        if(getOriginalValue().length() == 0)
            return;
        if(newString.endsWith("L"))
            newString = newString.substring(0, newString.length() - 1);
        long longValue = Long.parseLong(newString);
        setData(BigInteger.valueOf(longValue));
    }

    public String asString()
    {
        return getData().toString(16);
    }

    public boolean getSwapNybbles()
    {
        return swapNybbles;
    }

    public boolean getReverseBcd()
    {
        return reverseBcd;
    }

    private boolean swapNybbles;
    private boolean reverseBcd;
    private static HashMap digitMap;
    static Log log = LogFactory.getLog(com.ericsson.cac.ecds.sgw.eventparameter.base.SgwTbcd.class);
    private static char padding;

    static 
    {
        digitMap = null;
        padding = '\017';
        digitMap = new HashMap(48);
        digitMap.put(Character.valueOf('0'), Integer.valueOf(0));
        digitMap.put(Character.valueOf('1'), Integer.valueOf(1));
        digitMap.put(Character.valueOf('2'), Integer.valueOf(2));
        digitMap.put(Character.valueOf('3'), Integer.valueOf(3));
        digitMap.put(Character.valueOf('4'), Integer.valueOf(4));
        digitMap.put(Character.valueOf('5'), Integer.valueOf(5));
        digitMap.put(Character.valueOf('6'), Integer.valueOf(6));
        digitMap.put(Character.valueOf('7'), Integer.valueOf(7));
        digitMap.put(Character.valueOf('8'), Integer.valueOf(8));
        digitMap.put(Character.valueOf('9'), Integer.valueOf(9));
        digitMap.put(Character.valueOf('*'), Integer.valueOf(10));
        digitMap.put(Character.valueOf('#'), Integer.valueOf(11));
        digitMap.put(Character.valueOf('a'), Integer.valueOf(12));
        digitMap.put(Character.valueOf('b'), Integer.valueOf(13));
        digitMap.put(Character.valueOf('c'), Integer.valueOf(14));
        digitMap.put(Character.valueOf('A'), Integer.valueOf(12));
        digitMap.put(Character.valueOf('B'), Integer.valueOf(13));
        digitMap.put(Character.valueOf('C'), Integer.valueOf(14));
        digitMap.put(Character.valueOf('P'), new Integer(padding));
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 60 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/