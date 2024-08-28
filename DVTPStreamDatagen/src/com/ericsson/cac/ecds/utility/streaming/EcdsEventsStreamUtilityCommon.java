/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.utility.streaming;

import com.ericsson.cac.ecds.interfaces.EventInterface;
import com.ericsson.cac.ecds.utility.Main;
import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class EcdsEventsStreamUtilityCommon
{

    public EcdsEventsStreamUtilityCommon()
    {
    }

    protected void setByte(byte byteArray[], int byteIndex, String value, String file, int lineNo, int fieldIndex)
    {
        byte byteValue = 0;
        try
        {
            byteValue = Byte.parseByte(value);
        }
        catch(NumberFormatException e)
        {
            log.error((new StringBuilder("Failed to parse \"")).append(value).append("\" into a byte. File:").append(file).append(", line:").append(lineNo).append(", field:").append(fieldIndex).append(".").toString());
        }
        byteArray[byteIndex] = byteValue;
    }

    protected String formatMsg(String msg, String file, int lineNo, int field)
    {
        return (new StringBuilder(String.valueOf(msg))).append(", file:").append(file).append(", line:").append(lineNo).append(", field:").append(field).toString();
    }

    public List processSeedfile(String seedFile, String version)
    {
        List eventsStream = new ArrayList();
        BufferedReader in;
        try
        {
            in = new BufferedReader(new FileReader(seedFile));
        }
        catch(FileNotFoundException e)
        {
            log.error((new StringBuilder("Seed file ")).append(seedFile).append(" not found.").toString());
            return null;
        }
        try
        {
            int lineNo = 0;
            String line;
            while((line = in.readLine()) != null) 
            {
                lineNo++;
                String elements[] = line.split("[\t]*,[\t]*");
                if(elements.length == 1 && elements[0].equals(""))
                    continue;
                for(int i = 0; i < elements.length; i++)
                    elements[i] = elements[i].trim();

                if(elements[0].startsWith("#"))
                    continue;
                int recordType;
                try
                {
                    recordType = Integer.parseInt(elements[0]);
                }
                catch(NumberFormatException e)
                {
                    log.error((new StringBuilder("Failed to parse \"")).append(elements[0]).append("\" into an integer. File:").append(seedFile).append(", line:").append(lineNo).append(", field:1.").toString());
                    continue;
                }
                switch(recordType)
                {
                case 0: // '\0'
                    eventsStream.add(processHeaderRecord(elements, lineNo, seedFile, version));
                    break;

                case 1: // '\001'
                    eventsStream.add(processEventRecord(elements, lineNo, seedFile, version));
                    break;

                case 5: // '\005'
                    eventsStream.add(processErrorRecord(elements, lineNo, seedFile, version));
                    break;

                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                default:
                    log.error((new StringBuilder("Unknown record type ")).append(recordType).append(" on line ").append(lineNo).append(", ignoring").toString());
                    break;
                }
            }
            in.close();
        }
        catch(IOException e)
        {
            log.error(e);
        }
        return eventsStream;
    }

    public List processSeedString(String seedString, String version)
    {
        List<byte[]> eventsStream = new ArrayList<byte[]>();
        String elements[];
        int recordType;
        elements = seedString.split(",");
        for(int i = 0; i < elements.length; i++)
            elements[i] = elements[i].trim();

        try
        {
            recordType = Integer.parseInt(elements[0]);
        }
        catch(NumberFormatException e)
        {
            log.error((new StringBuilder("Failed to parse \"")).append(elements[0]).append("\" into an integer. seed string:").append(seedString).toString());
            return eventsStream;
        }
        try
        {
            switch(recordType)
            {
            case 0: // '\0'
                eventsStream.add(processHeaderRecord(elements, 1, "SeedString", version));
                break;

            case 1: // '\001'
                eventsStream.add(processEventRecord(elements, 1, "SeedString", version));
                break;

            case 3: // '\003'
                eventsStream.add(processFooterRecord(elements, 1, "SeedString", version));
                break;

            case 5: // '\005'
                if(version == Main.SGW_A_VERSION)
                    eventsStream.add(processErrorRecord(elements, 1, "SeedString", version));
                else
                    eventsStream.add(processFooterRecord(elements, 1, "SeedString", version));
                break;

            case 2: // '\002'
            case 4: // '\004'
            default:
                log.error((new StringBuilder("Unknown record type ")).append(recordType).append(" on seeString, ignoring").toString());
                break;
            }
        }
        catch(Exception e)
        {
            log.error(e);
        }
        return eventsStream;
    }

    protected void setParam(Properties p, String elements[], String key, int index, int lineNo, String file, int eventId)
        throws ArrayIndexOutOfBoundsException
    {
        try
        {
            p.setProperty(key, elements[index]);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            log.error(formatMsg((new StringBuilder("Insufficient fields for eventId:")).append(eventId).toString(), file, lineNo, index));
            throw e;
        }
    }

    protected void writeEvent(EventInterface event, OutputStream out)
    {
        try
        {
            out.write(event.encode());
        }
        catch(IOException e)
        {
            log.error(e);
        }
    }

    protected void writeEvent(EventInterface event, OutputStream out, int eventId)
    {
        try
        {
            out.write(event.encode(eventId));
        }
        catch(IOException e)
        {
            log.error(e);
        }
    }

    protected abstract byte[] processHeaderRecord(String as[], int i, String s, String s1);

    protected abstract byte[] processEventRecord(String as[], int i, String s, String s1);

    protected abstract byte[] processErrorRecord(String as[], int i, String s, String s1);

    protected byte[] processFooterRecord(String elements[], int lineNo, String seedFile, String version)
    {
        return new byte[0];
    }

    public String[] adjustSeedArray(String elements[], String version)
    {
        if(version.equals(Main.EBM_D_VERSION) || version.equals(Main.EBM_E_VERSION) || version.equals(Main.SGW_A_VERSION))
        {
            ArrayList elementAL = new ArrayList();
            for(int j = 0; j < elements.length; j++)
            {
                String item = elements[j];
                if(item != null && item.startsWith("[") && item.endsWith("]"))
                {
                    int start = item.indexOf("[");
                    int end = item.indexOf("]");
                    item = item.substring(start + 1, end);
                    String items[] = item.split(";");
                    for(int k = 0; k < items.length; k++)
                        elementAL.add(items[k]);

                } else
                {
                    elementAL.add(elements[j]);
                }
            }

            elements = (String[])elementAL.toArray(new String[elementAL.size()]);
        }
        return elements;
    }

    private static final Log log = LogFactory.getLog(com.ericsson.cac.ecds.utility.streaming.EcdsEventsStreamUtilityCommon.class);

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events_Utility.jar
	Total time: 55 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method processSeedString
	Exit status: 0
	Caught exceptions:
*/