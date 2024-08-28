/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   SgwEventFormat.java

package com.ericsson.cac.ecds.sgw.base;


public class SgwEventFormat
{

    public SgwEventFormat(Class eventParameter, String label, boolean useValidBit)
    {
        this.eventParameter = eventParameter;
        this.label = label;
        this.useValidBit = useValidBit;
    }

    public Class getEventParameter()
    {
        return eventParameter;
    }

    public String getLabel()
    {
        return label;
    }

    public boolean getUseValidBit()
    {
        return useValidBit;
    }

    private Class eventParameter;
    private String label;
    private boolean useValidBit;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\xpandee\CTRStreamSimulator\lib\ECDS_Events.jar
	Total time: 53 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/