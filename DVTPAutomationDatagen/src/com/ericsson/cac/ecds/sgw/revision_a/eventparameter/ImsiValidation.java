package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;



import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwUint;

import java.math.BigInteger;
import java.util.Properties;

public class ImsiValidation extends SgwUint
{

    public ImsiValidation()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public ImsiValidation(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ImsiValidation(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ImsiValidation(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ImsiValidation(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ImsiValidation(String newString, boolean useValidityBit)
    {
        super(newString);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    protected boolean getCheckMaxValue()
    {
        return false;
    }

    protected boolean getCheckMinValue()
    {
        return false;
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
        return "ImsiValidation";
    }

    public String getDescription()
    {
        return "ImsiValidation";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return 1;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "ImsiValidation";
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

    private static final int bitSize = 1;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf((long)(Math.pow(2,bitSize)-1));
    private static final boolean checkMinValue = false;
    private static final boolean checkMaxValue = false;
    private static final String name = "ImsiValidation";
    private static final String shortName = "ImsiValidation";
    private static final String description = "ImsiValidation";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 0;
    private boolean hasValidityBit;
    private boolean isValid;

}


