/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator.engine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ericsson.cac.ecds.utility.streaming.PgwEventsStream;
import com.ericsson.cac.ecds.utility.streaming.SgwEventsStream;
import com.ericsson.ctr.stream.simulator.ConnectorItem;
import com.ericsson.ctr.stream.simulator.StreamRaw;
import com.ericsson.ctr.stream.simulator.Utilities;

/**
 * 
 * @author ejactho
 */
public class Pgw extends StreamRaw {

	int bytecount = 0;
	private static String imsi = null;
	private static String apn = null;
	private static String pdnId = null;

	protected static final boolean sendHeader_default = true;
	protected static final boolean delayHeader_default = false;
	protected static final int eventDelay_default = 100;
	String[] eventTime = new String[4];
	String[] ropFileOpenTime = new String[4];
	String[] ropFileCloseTime = new String[4];
	final int ropFileDuration = 1; // in minutes.
	HashMap<String, ArrayList<byte[]>> eventRopFile = new HashMap<String, ArrayList<byte[]>>(
			1440);

	protected volatile boolean sendHeader;
	protected volatile boolean delayHeader;
	protected volatile int eventDelay;
	protected volatile int eventRate;
	private OutputStream out;

	public void showHelp() {
		System.out.println("Stream : Pgw\n"
				+ "    [--header=true/false/1/0] [--[no]delayheader]\n"
				+ "    [--delay=<milliSecs>] [--eventrate=<event/sec>]");
		super.showHelp();
	}

	private void calcDelay() {
		this.eventDelay = (this.simCount * 1000) / this.eventRate;
	}

	private void calcRate() {
		this.eventRate = (this.simCount * 1000) / this.eventDelay;
	}

	public Pgw() {

		super();

		this.sendHeader = Pgw.sendHeader_default;
		this.delayHeader = Pgw.delayHeader_default;
		this.eventDelay = Pgw.eventDelay_default;
		calcRate();

	}

	public boolean processArgs(String par, String val) {

		if (par.equals("--header")) {
			this.sendHeader = Utilities.boolStr(val);
			return true;
		}
		if (par.equals("--noheader")) {
			this.sendHeader = false;
			return true;
		}
		if (par.equals("--delayheader")) {
			this.delayHeader = Utilities.boolStr(val);
			return true;
		}
		if (par.equals("--nodelayheader")) {
			this.delayHeader = false;
			return true;
		}
		if (par.equals("--delay")) {
			this.eventDelay = Integer.parseInt(val);
			calcRate();
			return true;
		}
		if (par.equals("--eventrate")) {
			this.eventRate = Integer.parseInt(val);
			calcDelay();
			return true;
		}
		return super.processArgs(par, val);
	}

	public void startMsg() {
		System.out.println("Simulators started..");
	}

	private void putEvent(String objId, String rec, ConnectorItem connItem,
			PgwEventsStream pgwStreamer) {

		if (this.debug) {
			System.out.println("D '" + objId + "' rec '" + rec + "'");
		}
		connItem.put(pgwStreamer.processSeedString(rec, "a").get(0));
	}

	// - - - - - - - - - - - - - - - - - - - //
	// HEADER RECORD //
	// - - - - - - - - - - - - - - - - - - - //
	private String generatePgwHeader(String objId,
			SimpleDateFormat tFormYearSecT) {

		// RECORD_TYPE,EVENT_ID,FILE_FORMAT_VERSION,FILE_INFORMATION_VERSION,TIME_YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,TIME_ZONE,
		// CAUSE_OF_HEADER,NODE_ID
		String record = "";
		// if(time==null)
		record = "0,4,2,1," + tFormYearSecT.format(System.currentTimeMillis())
				+ ",1," + objId;
		// else
		// {

		// }
		return record;

	}

	// - - - - - - - - - - - - - - - - - - - //
	// MAIN EVENT GENERATOR LOOP //
	// - - - - - - - - - - - - - - - - - - - //
	public void startTraffic(String objId, ConnectorItem connItem) {

		if (this.debug) {
			System.out.println("D '" + objId + "' CONF: send header:'"
					+ this.sendHeader + "' delay header:'" + this.delayHeader
					+ "' event delay (ms):'" + this.eventDelay
					+ "' event rate (events/sec):'" + this.eventRate + "'");
		}

		PgwEventsStream pgwStreamer = new PgwEventsStream();

		SimpleDateFormat tFormYearMilli = new SimpleDateFormat(
				Utilities.tFstrYearMilli);
		SimpleDateFormat tFormYearSecT = new SimpleDateFormat(
				Utilities.tFstrYearSecT);
		SimpleDateFormat tFormHourMilli = new SimpleDateFormat(
				Utilities.tFstrHourMilli);

		tFormYearMilli.setTimeZone(Utilities.tzGmt);
		tFormYearSecT.setTimeZone(Utilities.tzGmt);
		tFormHourMilli.setTimeZone(Utilities.tzGmt);

		if (this.sendHeader) {

			// putEvent(objId, generatePgwHeader(objId, tFormYearSecT),
			// connItem,pgwStreamer);
			// Utilities.Sleep (1000);
		}

		putEvent(objId, generateSessionInfo(tFormHourMilli, objId), connItem,
				pgwStreamer);
		Utilities.Sleep(1000);

		while (stopStreaming == false) {

			putEvent(objId, generateDataUsage(tFormHourMilli, objId), connItem,
					pgwStreamer);
			Utilities.Sleep(5 * 60 * 1000);

		}

		close();
	}

	private String generateSessionInfo(SimpleDateFormat tFormHourMilli,
			String objId) {

		return "1,6,0,"
				+ tFormHourMilli.format(System.currentTimeMillis())
				+ ",0,11122330000002,,,,,,,,,,,,apn1.com,,,11.1.0.2,,,,4800017,RS229";

	}

	public String generateDataUsage(SimpleDateFormat tFormHourMilli,
			String objId) {

		return "1,7,0,"
				+ tFormHourMilli.format(System.currentTimeMillis())
				+ ",19000,11122330000002,,,,,,,,,,,,apn1.com,,,,,,,4800017,RS229,[2;;;;10240;20480],[]";

	}

	public synchronized void setStopStreaming(boolean stopStreaming) {
		this.stopStreaming = stopStreaming;
	}
}
