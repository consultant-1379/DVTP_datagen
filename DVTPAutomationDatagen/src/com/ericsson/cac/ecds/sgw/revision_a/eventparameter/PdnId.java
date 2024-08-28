/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwUint;

import java.math.BigInteger;
import java.util.Properties;

public class PdnId extends SgwUint
{

    public PdnId()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public PdnId(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PdnId(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PdnId(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PdnId(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PdnId(String newString, boolean useValidityBit)
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
        return "PdnId";
    }

    public String getDescription()
    {
        return "PdnId";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return 32;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "PdnId";
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

    private static final int bitSize = 32;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf((long)(Math.pow(2,bitSize)-1));
    private static final boolean checkMinValue = true;
    private static final boolean checkMaxValue = true;
    private static final String name = "PdnId";
    private static final String shortName = "PdnId";
    private static final String description = "The PDN identifier is based on the Charging ID.";
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
	Total time: 50 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/