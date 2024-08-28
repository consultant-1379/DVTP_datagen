package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwEnum;
import java.math.BigInteger;
import java.util.Properties;

public class DeactivationTrigger extends SgwEnum{


    public DeactivationTrigger()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public DeactivationTrigger(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public DeactivationTrigger(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public DeactivationTrigger(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public DeactivationTrigger(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public DeactivationTrigger(String newString, boolean useValidityBit)
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
        return "Deactivation_Trigger";
    }

    public String getDescription()
    {
        return "Deactivation Trigger. The value represents the initiated origin for deactivation";
    }

    public String getComment()
    {
        return "";
    }

    public int getNumBits()
    {
        return 3;
    }

    public boolean hasValidityBit()
    {
        return hasValidityBit;
    }

    public String getName()
    {
        return "Deactivation_Trigger";
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

    private static final int bitSize = 3;
    private static final BigInteger minValue = BigInteger.valueOf(0L);
    private static final BigInteger maxValue = BigInteger.valueOf((long)(Math.pow(2,bitSize)-1));
    private static final boolean checkMinValue = false;
    private static final boolean checkMaxValue = false;
    private static final String name = "Deactivation_Trigger";
    private static final String shortName = "Deactivation_Trigger";
    private static final String description = "Deactivation Trigger. The value represents the initiated origin for deactivation";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 0;
    public static final int SGSN = 0;
    public static final int MS = 1;
    public static final int GGSN= 2;
    public static final int CAMEL = 3;
    public static final int HLR_OR_HSS = 4;
    public static final int RNC = 5;
    public static final int SGW = 6;
    private boolean hasValidityBit;
    private boolean isValid;


}
