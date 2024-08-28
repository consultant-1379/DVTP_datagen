/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   Duration.java

package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwUint;

import java.math.BigInteger;
import java.util.Properties;

public class Duration extends SgwUint
{

    public Duration()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public Duration(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public Duration(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public Duration(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public Duration(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public Duration(String newString, boolean useValidityBit)
    {
        super(newString);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    protected boolean getCheckMaxValue()
    {
        return true;
    }

    protected boolean getCheckMinValue()
    {
        return true;
    }

    protected BigInteger getMaxValue()
    {
        return maxValue;
    }

    protected BigInteger getMinValue()
    {
        return minValue;
    }

    public String getShortName()
    {
        return "DURATION";
    }

    public String getDescription()
    {
        return "Duration provides timing information with millisecond granularity for the signaling procedure.";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return 24;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "DURATION";
    }

    public boolean isValid()
    {
        return !hasValidityBit || isValid;
    }

    protected int getLengthBits()
    {
        return 0;
    }

    protected int getValidLow()
    {
        return 0;
    }

    protected int getValidHigh()
    {
        return 0;
    }

    public void setHasValidityBit(boolean hasValidityBit)
    {
        this.hasValidityBit = hasValidityBit;
    }

    private static final int bitSize = 24;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf((long)(Math.pow(2,bitSize)-1));
    private static final boolean checkMinValue = true;
    private static final boolean checkMaxValue = true;
    private static final String name = "DURATION";
    private static final String shortName = "DURATION";
    private static final String description = "Duration provides timing information with millisecond granularity for the signaling procedure.";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 0;
    private boolean hasValidityBit;
    private boolean isValid;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 54 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/