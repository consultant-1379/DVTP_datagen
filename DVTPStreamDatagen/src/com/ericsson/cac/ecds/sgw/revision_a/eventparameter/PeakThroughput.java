package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwUint;
import java.math.BigInteger;
import java.util.Properties;

public class PeakThroughput extends SgwUint{

    public PeakThroughput()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public PeakThroughput(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PeakThroughput(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PeakThroughput(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PeakThroughput(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public PeakThroughput(String newString, boolean useValidityBit)
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
        return "Peak_Throughput";
    }

    public String getDescription()
    {
        return "Peak Throughput in R97/98 QoS profile, represent the Peak Throughput Class 1 to 9.";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return 4;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "Peak_Throughput";
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
        return 15;
    }

    public void setHasValidityBit(boolean hasValidityBit)
    {
        this.hasValidityBit = hasValidityBit;
    }

    private static final int bitSize = 4;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf((long)(Math.pow(2,bitSize)-1));
    private static final boolean checkMinValue = true;
    private static final boolean checkMaxValue = true;
    private static final String name = "Peak_Throughput";
    private static final String shortName = "Peak_Throughput";
    private static final String description = "Peak Throughput in R97/98 QoS profile, represent the Peak Throughput Class 1 to 9.";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 15;
    private boolean hasValidityBit;
    private boolean isValid;


}
