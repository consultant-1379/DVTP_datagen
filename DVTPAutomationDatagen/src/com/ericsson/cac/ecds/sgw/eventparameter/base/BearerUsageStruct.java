
package com.ericsson.cac.ecds.sgw.eventparameter.base;

import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.*;
import java.math.BigInteger;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package com.ericsson.cac.ecds.sgw.eventparameter.base:
//            SgwEventParameter

public class BearerUsageStruct extends SgwEventParameter
{

    public BearerUsageStruct()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public BearerUsageStruct(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public BearerUsageStruct(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public BearerUsageStruct(String newString, boolean useValidityBit)
    {
        super(newString);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public void setValue(String bearerUsageStruct)
    {
        BigInteger encodeData = BigInteger.ZERO;
        int ind = bearerUsageStruct.indexOf("]");
        String newBearerUsageStruct = bearerUsageStruct.substring(1, ind);
        String subStrings[] = newBearerUsageStruct.trim().split("\\|");
        if(subStrings.length>=1 && !subStrings[0].equalsIgnoreCase(""))
        encodeData = BigInteger.valueOf(subStrings.length);
        
        int totalBits = 8;  //length
        /*if(bearerUsageStruct.trim().length() == 0 || bearerUsageStruct.trim().length() == 2)
        {
            encodeData = BigInteger.valueOf(0L);
            setData(encodeData);
            setNumBits(totalBits);
            return;
        }*/
        if(bearerUsageStruct != null && bearerUsageStruct.startsWith("[") && bearerUsageStruct.endsWith("]"))
        {
            ind = bearerUsageStruct.indexOf("]");
            newBearerUsageStruct = bearerUsageStruct.substring(1, ind);
            
                String bearers[] = newBearerUsageStruct.split("\\|");
                if(bearers != null)
                {
                    for(int k = 0; k < bearers.length; k++)
                    {
                    	String eachBearer[];
                        String commaSepBearer = bearers[k];
                        if(commaSepBearer.equalsIgnoreCase("")){
                        		String[] temp= {"","","","","",""};
                        		eachBearer=temp;
                        }
                        else
                        	eachBearer = commaSepBearer.split(";", 6);
                        
                        if(eachBearer != null )
                        {
                            BearerId id = new BearerId(eachBearer[0], true);
                            BearerCause cause = new BearerCause(eachBearer[1], true);
                            ULPackets ul_packets = new ULPackets(eachBearer[2], true);
                            DLPackets dl_packets = new DLPackets(eachBearer[3], true);
                            ULBytes ul_bytes = new ULBytes(eachBearer[4], true);
                            DLBytes dl_bytes = new DLBytes(eachBearer[5], true);
                            encodeData = id.encodeToBigInteger(encodeData);
                            encodeData = cause.encodeToBigInteger(encodeData);
                            encodeData = ul_packets.encodeToBigInteger(encodeData);
                            encodeData = dl_packets.encodeToBigInteger(encodeData);
                            encodeData = ul_bytes.encodeToBigInteger(encodeData);
                            encodeData = dl_bytes.encodeToBigInteger(encodeData);
                            totalBits += 6 + (id.getOriginalValue().length() <= 0 ? 0 : id.getNumBits()) 
                            			+ (cause.getOriginalValue().length() <= 0 ? 0 : cause.getNumBits()) 
                            			+ (ul_packets.getOriginalValue().length() <= 0 ? 0 : ul_packets.getNumBits()) 
                            			+ (dl_packets.getOriginalValue().length() <= 0 ? 0 : dl_packets.getNumBits()) 
                            			+ (ul_bytes.getOriginalValue().length() <= 0 ? 0 : ul_bytes.getNumBits())
                            			+ (dl_bytes.getOriginalValue().length() <= 0 ? 0 : dl_bytes.getNumBits());
                            
                        }
                    }
                }                    
        	}else
                {
                    encodeData = BigInteger.valueOf(0L);
                    totalBits = 8;
                }
           
        //System.out.println("bearer bit count-->" + encodeData.bitLength()+"--->"+ encodeData);
        setData(encodeData);
        setNumBits(totalBits);
    }

    protected void setNumBits(int numBits)
    {
        bitSize = numBits;
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
        return "BEARER_STRUCT";
    }

    public String getDescription()
    {
        return "Bearer Structure";
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
        return "BEARER_STRUCT";
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

    private static Log log = LogFactory.getLog("fileLogger");
    private int bitSize;
    private static final BigInteger minValue = BigInteger.valueOf(1L);
    private static final BigInteger maxValue = BigInteger.valueOf(8L);
    private static final boolean checkMinValue = true;
    private static final boolean checkMaxValue = true;
    private static final String name = "BEARER_STRUCT";
    private static final String shortName = "BEARER_STRUCT";
    private static final String description = "Bearer Structure";
    private static final String comment = "";
    private static final int lengthBits = 0;
    private static final int validLow = 0;
    private static final int validHigh = 0;
    private boolean hasValidityBit;
    private boolean isValid;

}

