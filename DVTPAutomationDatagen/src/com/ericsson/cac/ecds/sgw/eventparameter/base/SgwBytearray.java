/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.ericsson.cac.ecds.sgw.eventparameter.base;

import java.math.BigInteger;
import java.util.Properties;

// Referenced classes of package com.ericsson.cac.ecds.sgw.eventparameter.base:
//            SgwEventParameter

public abstract class SgwBytearray extends SgwEventParameter
{

    public SgwBytearray()
    {
        payloadBits = 0;
        paddingBits = 0;
        calculatePayloadBits();
    }

    public SgwBytearray(Properties p)
    {
        super(p);
        payloadBits = 0;
        paddingBits = 0;
        calculatePayloadBits();
    }

    public SgwBytearray(byte binaryData[], int bitOffset, boolean useValidityBit)
    {
        payloadBits = 0;
        paddingBits = 0;
        int lengthBits = getLengthBits();
        int lsbBitOffset = bitOffset + lengthBits;
        int startByte = bitOffset / 8;
        int endByte = (lsbBitOffset - 1) / 8;
        int tmpByteArrayLength = (endByte - startByte) + 2;
        byte tmpByteArray[] = new byte[tmpByteArrayLength];
        tmpByteArray[0] = 0;
        System.arraycopy(binaryData, startByte, tmpByteArray, 1, tmpByteArrayLength - 1);
        BigInteger fieldLength = new BigInteger(tmpByteArray);
        int shiftSize = (8 - lsbBitOffset % 8) % 8;
        fieldLength = fieldLength.shiftRight(shiftSize);
        BigInteger mask = BigInteger.ONE.shiftLeft(lengthBits);
        mask = mask.subtract(BigInteger.ONE);
        fieldLength = fieldLength.and(mask);
        int fieldBits = fieldLength.intValue() * 8;
        payloadBits = fieldBits;
        paddingBits = _calculatePaddingBits(bitOffset + lengthBits);
        startByte = (bitOffset + lengthBits + paddingBits) / 8;
        lsbBitOffset = bitOffset + lengthBits + paddingBits + payloadBits;
        endByte = (lsbBitOffset - 1) / 8;
        int bitSize = fieldBits;
        if(useValidityBit)
            bitSize++;
        tmpByteArrayLength = (endByte - startByte) + 2;
        tmpByteArray = new byte[tmpByteArrayLength];
        tmpByteArray[0] = 0;
        System.arraycopy(binaryData, startByte, tmpByteArray, 1, tmpByteArrayLength - 1);
        BigInteger newData = new BigInteger(tmpByteArray);
        mask = BigInteger.ONE.shiftLeft(bitSize);
        mask = mask.subtract(BigInteger.ONE);
        newData = newData.and(mask);
        setData(newData);
        setNumBits(lengthBits + payloadBits + paddingBits);
    }

    public SgwBytearray(long newValue)
    {
        super(newValue);
        payloadBits = 0;
        paddingBits = 0;
        calculatePayloadBits();
    }

    private void calculatePayloadBits()
    {
        payloadBits = getData().bitLength();
        payloadBits += (8 - payloadBits % 8) % 8;
        setNumBits(7 + paddingBits + payloadBits);
    }

    public SgwBytearray(String newString)
    {
        super(newString);
        payloadBits = 0;
        paddingBits = 0;
        calculatePayloadBits();
    }

    protected abstract boolean getCheckMaxValue();

    protected abstract boolean getCheckMinValue();

    public abstract String getComment();

    public abstract String getDescription();

    protected abstract int getLengthBits();

    protected abstract BigInteger getMaxValue();

    protected abstract BigInteger getMinValue();

    public abstract String getName();

    public abstract int getNumBits();

    public abstract String getShortName();

    protected abstract int getValidHigh();

    protected abstract int getValidLow();

    public abstract boolean hasValidityBit();

    public abstract boolean isValid();

    protected abstract void setNumBits(int i);

    public BigInteger encodeToBigInteger(BigInteger binaryEvent)
    {
        int lengthBits = getLengthBits();
        if(hasValidityBit())
        	//System.out.println(getShortName()+"--->"+ getOriginalValue());
            if(getOriginalValue().length() == 0)
            {
                binaryEvent = binaryEvent.shiftLeft(1);
                binaryEvent = binaryEvent.or(BigInteger.ONE);
                lengthBits = 1;
            } else
            {
                lengthBits++;
            }
        BigInteger data = getData();
        int payloadSize = data.bitLength();
        payloadSize += (8 - payloadSize % 8) % 8;
        int byteCount = payloadSize / 8;
        paddingBits = calculatePaddingBits(lengthBits, binaryEvent);
        //System.out.println(paddingBits);
        binaryEvent = binaryEvent.shiftLeft(lengthBits);
        binaryEvent = binaryEvent.or(BigInteger.valueOf(byteCount));
        binaryEvent = binaryEvent.shiftLeft(paddingBits); ////check this later
        binaryEvent = binaryEvent.shiftLeft(byteCount * 8);
        binaryEvent = binaryEvent.or(data);
        return binaryEvent;
    }

    private int calculatePaddingBits(int numBits, BigInteger binaryEvent)
    {
        int payloadSizeLsb = binaryEvent.bitLength() + numBits;
        return _calculatePaddingBits(payloadSizeLsb);
    }

    public int calculatePaddingBits(int currentBitsize)
    {
        int payloadSizeLsb = currentBitsize + getLengthBits();
        return _calculatePaddingBits(payloadSizeLsb);
    }

    private int _calculatePaddingBits(int payloadSizeLsb)
    {
        int paddingBits = (8 - payloadSizeLsb % 8) % 8;
        return paddingBits;
    }

    public String asString()
    {
        BigInteger data = getData();
        return (new StringBuilder("(length=")).append(payloadBits / 8).append("), \"").append(new String(data.toByteArray())).append("\"").toString();
    }

    private int payloadBits;
    private int paddingBits;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 58 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/