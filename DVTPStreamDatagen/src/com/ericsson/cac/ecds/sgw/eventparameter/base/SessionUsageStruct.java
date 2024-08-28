
package com.ericsson.cac.ecds.sgw.eventparameter.base;

import com.ericsson.cac.ecds.sgw.revision_a.eventparameter.*;
import java.math.BigInteger;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package com.ericsson.cac.ecds.sgw.eventparameter.base:
//            SgwEventParameter

public class SessionUsageStruct extends SgwEventParameter
{

    public SessionUsageStruct()
    {
        hasValidityBit = false;
        isValid = true;
    }

    public SessionUsageStruct(boolean useValidityBit)
    {
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public SessionUsageStruct(Properties p, boolean useValidityBit)
    {
        super(p);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public SessionUsageStruct(String newString, boolean useValidityBit)
    {
        super(newString);
        hasValidityBit = false;
        isValid = true;
        hasValidityBit = useValidityBit;
    }

    public void setValue(String bearerUsageStruct)
    {
        BigInteger encodeData = BigInteger.ZERO;
        String subStrings[] = bearerUsageStruct.trim().split("\\|");
        encodeData = BigInteger.valueOf(subStrings.length);
        int totalBits = 8;  //length
        if(bearerUsageStruct.trim().length() == 0)// || bearerUsageStruct.trim().length() == 2)
        {
            encodeData = BigInteger.valueOf(0L);
            setData(encodeData);
            setNumBits(totalBits);
            return;
        }
        if(bearerUsageStruct != null && bearerUsageStruct.startsWith("[") && bearerUsageStruct.endsWith("]"))
        {
            int ind = bearerUsageStruct.indexOf("]");
            String newSessionUsageStruct = bearerUsageStruct.substring(1, ind);
            
                String bearers[] = newSessionUsageStruct.split("\\|");
                if(bearers != null)
                {
                    for(int k = 0; k < bearers.length; k++)
                    {
                        String commaSepBearer = bearers[k];
                        String eachBearer[] = commaSepBearer.split(";", 6);
                        if(eachBearer != null && eachBearer.length >= 6 && (eachBearer.length%6) == 0)
                        {
                        	RatingGroup rg = new RatingGroup(eachBearer[0], true);
                        	ServiceIdentifier si = new ServiceIdentifier(eachBearer[1], true);
                        	UriName uri_name = new UriName(eachBearer[2], true);
                        	UriId uri_id = new UriId(eachBearer[3], true);
                        	ULBytes ul_bytes = new ULBytes(eachBearer[4], true);
                            DLBytes dl_bytes = new DLBytes(eachBearer[5], true);
                            encodeData = rg.encodeToBigInteger(encodeData);
                            encodeData = si.encodeToBigInteger(encodeData);
                            encodeData = uri_name.encodeToBigInteger(encodeData);
                            encodeData = uri_id.encodeToBigInteger(encodeData);
                            encodeData = ul_bytes.encodeToBigInteger(encodeData);
                            encodeData = dl_bytes.encodeToBigInteger(encodeData);
                            totalBits += 6 + (rg.getOriginalValue().length() <= 0 ? 0 : rg.getNumBits()) 
                            			+ (si.getOriginalValue().length() <= 0 ? 0 : si.getNumBits()) 
                            			+ (uri_name.getOriginalValue().length() <= 0 ? 0 : uri_name.getNumBits()) 
                            			+ (uri_id.getOriginalValue().length() <= 0 ? 0 : uri_id.getNumBits()) 
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

