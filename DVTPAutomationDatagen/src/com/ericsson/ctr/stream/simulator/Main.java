/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class Main {

    private int count = 1;
    private String host = "atrcxb1863.athtem.eei.ericsson.se";
    private int port = 1025;
    private boolean max = false;
    private boolean delayAllHeaders = false;
    private boolean delaySomeHeaders = false;
    private List<Stream> eNodeBList = new ArrayList<Stream>();
    private int eci = 1000;
    private int eps = 10;

    public List<Stream> getENodeBList() {
        return eNodeBList;
    }

    public void displayUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage : java -jar CTRStreamSimulator [--initeci=<initeci>] [--host=<host>] [--port=<port>] [--count=<count>] [--max] [--eps=<eps>] [--delayallheaders] [--delaysomeheaders] [--help]\n");
        sb.append("         where initeci = Initial ECI value. Default eci value = 1000\n");
        sb.append("         where host = Stream termination host. Default host = atrcxb926.athtem.eei.ericsson.se\n");
        sb.append("               port = Stream termination port number. Default port number = 1068\n");
        sb.append("               count = Number of streams. Default number of streams = 1\n");
        sb.append("               max is used to simulate maximum event load. One event is streamed over and over at maximum rate possible. Do not use max if you like some scenarios of events generated\n");

        System.out.println(sb.toString());
        System.exit(1);
    }

    private void processArguments(String[] args) {
        for (String arg : args) {
            String parameter = null;
            String value = null;

            if (arg.indexOf("=") == -1) {
                parameter = arg;
            } else {
                StringTokenizer st = new StringTokenizer(arg, "=");
                parameter = st.nextToken();
                value = st.nextToken();
            }

            if (parameter.equals("--count")) {
                count = Integer.parseInt(value);
            } else if (parameter.equals("--host")) {
                host = value;
            } else if (parameter.equals("--port")) {
                port = Integer.parseInt(value);
            } else if (parameter.equals("--eps")) {
                eps = Integer.parseInt(value);
            } else if (parameter.equals("--max")) {
                max = true;
            } else if (parameter.equals("--delayallheaders")) {
                delayAllHeaders = true;
            } else if (parameter.equals("--delaysomeheaders")) {
                delaySomeHeaders = true;
            } else if (parameter.equals("--initeci")) {
                eci = Integer.parseInt(value);
            } else if (parameter.equals("--help")) {
                displayUsage();
            } else {
                System.err.println("Invalid argument : " + arg);
                displayUsage();
            }

        }

    }

    public void calculateThreadSleepTime() {
        int threadSleepTime = 1000 / eps;
        EventGenerate.delayBetweenEvents = threadSleepTime;
    }

    public void runSimulator(String[] args) {
        processArguments(args);
        calculateThreadSleepTime();

        EventGenerateAgent mbsAgent = new EventGenerateAgent(null);

        int i = 0;
        HashMap<String, Long> timeList = new HashMap<String, Long>();

        timeList.put("-1", 0l);
        while (i < count) {
            Stream s = null;
            try {
                if (max == true) {
                    s = new StreamMax(""+(eci++), host, port, 10000);
                } else {
                    s = new StreamScenario(""+(eci++), host, port, 10000);
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("Waiting 10 seconds because of timeout");
                i--;
                
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
                }
                continue;

            }

            if (delayAllHeaders) {
                s.setDelayHeaderTransmission(true);
            } else if (delaySomeHeaders) {
                int percent = 10;
                if (i % percent == 0) {
                    s.setDelayHeaderTransmission(true);
                }
            }

            Thread t = new Thread(s);
            eNodeBList.add(s);
            t.start();

            timeList.put(s.getENodeBId(), 0l);

            long sleep = 0;

            if ((i % 1000) == 0) {
                sleep = 3000;
            } else if ((i % 100) == 0) {
                sleep = 1000;
            } else if ((i % 10) == 0) {
                sleep = 100;
            }

            if (sleep > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(sleep);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            i++;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat df = new DecimalFormat("###,###,###,###,###");

        boolean zeroDelaySet = false;
        while (true) {

            long totalEventCount = 0;
            String time = sdf.format(Calendar.getInstance().getTime());
            for (Stream s : eNodeBList) {
                long lastCount = timeList.get(s.getENodeBId());
                long c = s.getEventCount();
                long delta = c - lastCount;
                timeList.put(s.getENodeBId(), c);
                System.out.println(time + " - eNodeB " + s.getENodeBId() + ", Total : " + df.format(c) + "; Last Minute : " + df.format(delta) + "; events/sec : " + df.format((delta / 60)));
                totalEventCount += c;
            }
            long lastCount = timeList.get("-1");
            long delta = totalEventCount - lastCount;
            timeList.put("-1", totalEventCount);
            System.out.println("\n" + time + " - Total Count : " + df.format(totalEventCount) + "; Last Minute : " + df.format(delta) + "; events/sec : " + df.format((delta / 60)) + "\n\n========================================================================\n\n");
            EventGenerate.counter = df.format(totalEventCount);
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (max == true && zeroDelaySet == false) {
                EventGenerate.delayBetweenEvents = 0;
                zeroDelaySet = true;
            }
        }
    }

    public static void main(String[] args) {
        new Main().runSimulator(args);
    }
}
