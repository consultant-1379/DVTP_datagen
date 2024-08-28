/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   SgwEvent.java

package com.ericsson.cac.ecds.sgw.base;

import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.interfaces.EventParameterInterface;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package com.ericsson.cac.ecds.sgw.base:
//            SgwEventFormat

public abstract class SgwEvent
    implements EventInterface
{

    public SgwEvent()
        throws InvocationTargetException
    {
        Properties p = new Properties();
        init(p);
    }

    public SgwEvent(byte binaryData[])
    {
        ArrayList eventFormatList = getEventFormat();
        ListIterator iterator = eventFormatList.listIterator();
        fields = new LinkedHashMap(initialCapacity, loadFactor);
        int bitOffset = 24;
        while(iterator.hasNext()) 
        {
            SgwEventFormat sgwEventFormat = (SgwEventFormat)iterator.next();
            Class eventParameterClass = sgwEventFormat.getEventParameter();
            try
            {
                Constructor eventParameterClassConstructor = eventParameterClass.getConstructor(new Class[] {
                		java.util.Properties.class, Integer.TYPE, Boolean.TYPE
                });
                EventParameterInterface eventParameter = (EventParameterInterface)eventParameterClassConstructor.newInstance(new Object[] {
                    binaryData, Integer.valueOf(bitOffset), Boolean.valueOf(sgwEventFormat.getUseValidBit())
                });
                fields.put(sgwEventFormat.getLabel(), eventParameter);
                bitOffset += eventParameter.getNumBits();
                if(sgwEventFormat.getUseValidBit())
                    bitOffset++;
            }
            catch(SecurityException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(NoSuchMethodException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(IllegalArgumentException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(IllegalAccessException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(InstantiationException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(InvocationTargetException e)
            {
                log.error("Exception while creating event\n", e);
            }
        }
    }

    public SgwEvent(Properties p)
        throws InvocationTargetException
    {
        init(p);
    }

    public byte[] encode()
    {
        byte result[] = encodeToBigInteger().toByteArray();
        byte newResult[] = Arrays.copyOfRange(result, 2, result.length);
        int eventSize = newResult.length;
        newResult[0] = (byte)((eventSize / 256) % 256);
        newResult[1] = (byte)((eventSize ) % 256);
        return newResult;
    }

    private BigInteger encodeToBigInteger()
    {
        Iterator iterator = fields.values().iterator();
        BigInteger data = BigInteger.ONE;
        
        //byte[] temp = BigInteger.value0f(229).toByteArray();
        
        //data = data.shiftLeft(31);
        data = data.shiftLeft(15);
        data = data.shiftLeft(8);
        //data = data.or(BigInteger.valueOf(eventRecordingType));
        EventParameterInterface eventParameter;
        for(; iterator.hasNext(); log.debug((new StringBuilder("base:SgwEvent encodeToBigInteger() - data=")).append(data.toString(2)).append("  ").append(eventParameter.getClass().getSimpleName()).toString()))
        {
            eventParameter = (EventParameterInterface)iterator.next();
            
            
            data = eventParameter.encodeToBigInteger(data);
            //System.out.println(data.bitLength());
        }

        int eventBitLength = data.bitLength();
        int eventPaddingBits = (32 - eventBitLength % 32) % 32;
        data = data.shiftLeft(eventPaddingBits);
        
        return data;
    }

    public OutputStream encodeToStream(OutputStream os)
    {
        return null;
    }

    protected int getBitSize()
    {
        Iterator iterator = fields.values().iterator();
        int bitSize = 0;
        EventParameterInterface eventParameter = null;
        while(iterator.hasNext()) 
        {
            eventParameter = (EventParameterInterface)iterator.next();
            int numBits = eventParameter.getNumBits();
            int paddingBits = eventParameter.calculatePaddingBits(bitSize);
            bitSize += numBits;
            bitSize += paddingBits;
            if(eventParameter.hasValidityBit())
                bitSize++;
        }
        bitSize = (bitSize += 24) + (32 - bitSize % 32) % 32;
        return bitSize;
    }

    protected abstract ArrayList getEventFormat();

    public abstract int getEventId();

    public abstract String getName();

    public int getEventSize()
    {
        return getBitSize() / 8;
    }

    public EventParameterInterface getField(String fieldName)
    {
        return (EventParameterInterface)fields.get(fieldName);
    }

    private void init(Properties p)
        throws InvocationTargetException
    {
        ArrayList eventFormatList = getEventFormat();
        ListIterator iterator = eventFormatList.listIterator();
        fields = new LinkedHashMap(initialCapacity, loadFactor);
        p.setProperty("EVENT_ID", Integer.toString(getEventId()));
        while(iterator.hasNext()) 
        {
            SgwEventFormat sgwEventFormat = (SgwEventFormat)iterator.next();
            Class eventParameterClass = sgwEventFormat.getEventParameter();
            Properties p1 = new Properties(p);
            try
            {
                Constructor c = eventParameterClass.getConstructor(new Class[] {
                		java.util.Properties.class, Boolean.TYPE
                });
                EventParameterInterface ep = (EventParameterInterface)c.newInstance(new Object[] {
                    p, Boolean.valueOf(false)
                });
                String shortName = ep.getShortName();
                String label = sgwEventFormat.getLabel();
                String value = p.getProperty(label);
                if(log.isDebugEnabled())
                    log.debug((new StringBuilder("shortName = ")).append(shortName).append(", label = ").append(label).append(", value = ").append(value).toString());
                if(value != null && !shortName.equals("EVENT_ID"))
                    p1.setProperty(shortName, value);
            }
            
            catch(SecurityException e)
            {
                log.error(e);
            }
            catch(NoSuchMethodException e)
            {
                log.error(e);
            }
            catch(IllegalArgumentException e)
            {
                log.error(e);
            }
            catch(IllegalAccessException e)
            {
                log.error(e);
            }
            catch(InstantiationException e)
            {
                log.error(e);
            }
            try
            {
                Constructor eventParameterClassConstructor = eventParameterClass.getConstructor(new Class[] {
                		java.util.Properties.class, Boolean.TYPE
                });
                EventParameterInterface eventParameter = (EventParameterInterface)eventParameterClassConstructor.newInstance(new Object[] {
                    p1, Boolean.valueOf(sgwEventFormat.getUseValidBit())
                });
                String label = sgwEventFormat.getLabel();
                fields.put(label, eventParameter);
            }
            catch(SecurityException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(NoSuchMethodException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(IllegalArgumentException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(IllegalAccessException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(InstantiationException e)
            {
                log.error("Exception while creating event\n", e);
            }
            catch(InvocationTargetException e)
            {
                log.error("Exception while creating event\n", e);
                throw e;
            }
        }
    }

    public String toString()
    {
        Iterator iterator = fields.values().iterator();
        StringBuffer sb = new StringBuffer(1024);
        int indent = 4;
        int nameFieldWidth = 40;
        String formatString = (new StringBuilder("%")).append(indent).append("s").toString();
        EventParameterInterface eventParameter;
        String epString;
        for(; iterator.hasNext(); sb.append((new StringBuilder(String.valueOf(String.format(formatString, new Object[] {
    ""
})))).append(String.format((new StringBuilder("%")).append(nameFieldWidth).append("s: %s\n").toString(), new Object[] {
    eventParameter.getShortName(), epString
})).toString()))
        {
            eventParameter = (EventParameterInterface)iterator.next();
            epString = eventParameter.asString();
        }

        return sb.toString();
    }

    public void setField(String fieldName, long newLong)
    {
        EventParameterInterface param = (EventParameterInterface)fields.get(fieldName);
        param.setValue(newLong);
    }

    public void setField(String fieldName, String newString)
    {
        EventParameterInterface param = (EventParameterInterface)fields.get(fieldName);
        param.setValue(newString);
    }

    protected String getVariableFieldName()
    {
        return "NONE";
    }

    protected String getVariableFieldLengthName()
    {
        return "NONE";
    }

    public byte[] encode(int eventId)
    {
        return null;
    }

    private static int eventRecordingType = 1;
    private static int initialCapacity = 64;
    private static float loadFactor = 0.75F;
    private static Log log = LogFactory.getLog("fileLogger");
    private LinkedHashMap fields;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 59 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/