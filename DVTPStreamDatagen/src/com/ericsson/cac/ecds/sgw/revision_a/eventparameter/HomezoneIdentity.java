package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwBytearray;
import java.math.BigInteger;
import java.util.Properties;

public class HomezoneIdentity extends SgwBytearray{


    public HomezoneIdentity()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public HomezoneIdentity(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public HomezoneIdentity(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public HomezoneIdentity(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public HomezoneIdentity(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public HomezoneIdentity(String newString, boolean useValidityBit)
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
        return "Homezone_Identity";
    }

    public String getDescription()
    {
        return "Homezone identity. A name for the current homezone. Home Zone charging differentiation is supported for GSM only.";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return bitSize;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "Homezone_Identity";
    }

    public boolean isValid()
    {
        return !hasValidityBit || isValid;
    }

    protected int getLengthBits()
    {
        return 5;
    }

    protected int getValidLow()
    {
        return 0;
    }

    protected int getValidHigh()
    {
        return 31;
    }

    public void setHasValidityBit(boolean hasValidityBit)
    {
        this.hasValidityBit = hasValidityBit;
    }

    protected void setNumBits(int numBits)
    {
        bitSize = numBits;
    }

    private int bitSize;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf(0L);
    private static final boolean checkMinValue = false;
    private static final boolean checkMaxValue = false;
    private static final String name = "Homezone_Identity";
    private static final String shortName = "Homezone_Identity";
    private static final String description = "Homezone identity. A name for the current homezone. Home Zone charging differentiation is supported for GSM only.";
    private static final String comment = "";
    private static final int lengthBits = 5;
    private static final int validLow = 0;
    private static final int validHigh = 31;
    private boolean hasValidityBit;
    private boolean isValid;


}
