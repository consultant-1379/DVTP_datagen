package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwUint;
import java.math.BigInteger;
import java.util.Properties;

public class MaxSduSize extends SgwUint{

    public MaxSduSize()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public MaxSduSize(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MaxSduSize(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MaxSduSize(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MaxSduSize(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public MaxSduSize(String newString, boolean useValidityBit)
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
        return "Max_Sdu_Size";
    }

    public String getDescription()
    {
        return "Maximum SDU size in R99 QoS profile.";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return 8;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "Max_Sdu_Size";
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
        return 255;
    }

    public void setHasValidityBit(boolean hasValidityBit)
    {
        this.hasValidityBit = hasValidityBit;
    }

    private static final int bitSize = 8;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf((long)(Math.pow(2,bitSize)-1));
    private static final boolean checkMinValue = true;
    private static final boolean checkMaxValue = true;
    private static final String name = "Max_Sdu_Size";
    private static final String shortName = "Max_Sdu_Size";
    private static final String description = "Maximum SDU size in R99 QoS profile.";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 255;
    private boolean hasValidityBit;
    private boolean isValid;




}
