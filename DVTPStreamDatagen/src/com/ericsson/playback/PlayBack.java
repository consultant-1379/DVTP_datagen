package com.ericsson.playback;

import java.text.DecimalFormat;

import com.ericsson.ctr.stream.simulator.Connector;
import com.ericsson.ctr.stream.simulator.ConnectorNull;
import com.ericsson.ctr.stream.simulator.StreamItem;

public class PlayBack {

	private static Connector connector = new ConnectorNull ();
	private static final String connectorPath = "com.ericsson.ctr.stream.simulator.connector.Basic";
	private int pgwNodeCount;
	private int sgehNodeCount;
	private String nodeType;
	
	private static final DecimalFormat itemIdDf = new DecimalFormat("000");
	
	public PlayBack(int pgw, int sgeh, Connector conn)
	{
		pgwNodeCount=pgw;
		sgehNodeCount=sgeh;
		connector=conn;
		if(pgwNodeCount==0 && sgehNodeCount>0)
			nodeType="Sgeh";
		else if(pgwNodeCount>0 && sgehNodeCount==0)
			nodeType="Pgw";
	}
	
	public void simulateNodes()
	{
		NodeStreamSimulator[] pgwSimulators=new NodeStreamSimulator[pgwNodeCount];
		NodeStreamSimulator[] sgehSimulators=new NodeStreamSimulator[sgehNodeCount];
		for(int i=0;i<pgwNodeCount;i++)
		{
			pgwSimulators [i] = new NodeStreamSimulator (objId(i), connector.create ());
			pgwSimulators [i].start();
		}
		
		for(int i=0;i<sgehNodeCount;i++)
		{
			pgwSimulators [i] = new NodeStreamSimulator (objId(i), connector.create ());
			pgwSimulators [i].start();
		}
		
		
	}

	private String objId(int i) {
		if(nodeType.equals("Pgw"))
			return "pgw_" + itemIdDf.format (i);
		else if(nodeType.equals("Sgeh"))
			return "sgeh_" + itemIdDf.format (i);
		else return null;
	}
	
   
	
}
