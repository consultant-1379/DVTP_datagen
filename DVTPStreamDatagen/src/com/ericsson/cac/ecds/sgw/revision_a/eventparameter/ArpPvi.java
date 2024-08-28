package com.ericsson.cac.ecds.sgw.revision_a.eventparameter;

import com.ericsson.cac.ecds.sgw.eventparameter.base.SgwEnum;
import java.math.BigInteger;
import java.util.Properties;

public class ArpPvi extends SgwEnum{


    public ArpPvi()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public ArpPvi(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ArpPvi(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ArpPvi(byte data[], int bitOffset, boolean useValidityBit)
    {
        super(data, bitOffset, useValidityBit);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ArpPvi(long newLong, boolean useValidityBit)
    {
        super(newLong);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public ArpPvi(String newString, boolean useValidityBit)
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
        return "Apr_Pvi";
    }

    public String getDescription()
    {
        return "Provides information on Pre-emption-Vulnerability of Allocation and Retention Priority for each bearer in a bearer setup list";
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
        return "Apr_Pvi";
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
    private static final String name = "Apr_Pci";
    private static final String shortName = "Apr_Pci";
    private static final String description = "Provides information on Pre-emption-Vulnerability of Allocation and Retention Priority for each bearer in a bearer setup list";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 0;
    public static final int ENABLED = 0;
    public static final int DISABLED = 1;
    private boolean hasValidityBit;
    private boolean isValid;





}
