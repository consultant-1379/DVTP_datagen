package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwDnsname;
import java.math.BigInteger;
import java.util.Properties;

public class MsRequestedApn extends SgwDnsname{

    public MsRequestedApn()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public MsRequestedApn(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MsRequestedApn(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MsRequestedApn(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MsRequestedApn(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MsRequestedApn(String newString, boolean useValidityBit)
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
        return "Ms_Requested_Apn";
    }

    public String getDescription()
    {
        return "Access Point Name";
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
        return "Ms_Requested_Apn";
    }

    public boolean isValid()
    {
        return !hasValidityBit || isValid;
    }

    protected int getLengthBits()
    {
        return 7;
    }

    protected int getValidLow()
    {
        return 0;
    }

    protected int getValidHigh()
    {
        return 100;
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
    private static final String name = "Ms_Requested_Apn";
    private static final String shortName = "Ms_Requested_Apn";
    private static final String description = "Access Point Name";
    private static final String comment = "";
    private static final int lengthBits = 7;
    private static final int validLow = 0;
    private static final int validHigh = 100;
    private boolean hasValidityBit;
    private boolean isValid;



}
