/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
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
public class MultipleInterfaceCpg {

    private int count = 1;
    private String host = "atrcxb1023.athtem.eei.ericsson.se";
    private int port = 4020;
    private boolean max = false;
    private boolean delayAllHeaders = false;
    private boolean delaySomeHeaders = false;
    private boolean noHeader = false;
    private List<CpgStream> cpgList = new ArrayList<CpgStream>();
    private int eps = 10;

    public void displayUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage : java -jar CTRStreamSimulator [--host=<host>] [--port=<port>] [--count=<count>] [--max] [--eps=<eps>] [--delayallheaders] [--delaysomeheaders] [--help]\n");
        sb.append("         where host = Stream termination host. Default host = atrcxb1023.athtem.eei.ericsson.se\n");
        sb.append("               port = Stream termination port number. Default port number = 4020\n");
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

            if (parameter.equals("--host")) {
                host = value;
            } else if (parameter.equals("--port")) {
                port = Integer.parseInt(value);
            } else if (parameter.equals("--count")) {
            	count = Integer.parseInt(value);
            } else if (parameter.equals("--eps")) {
                eps = Integer.parseInt(value);
            } else if (parameter.equals("--max")) {
                max = true;
            } else if (parameter.equals("--delayallheaders")) {
                delayAllHeaders = true;
            } else if (parameter.equals("--delaysomeheaders")) {
                delaySomeHeaders = true;
            } else if (parameter.equals("--help")) {
                displayUsage();
            } else if (parameter.equals("--noheader")) {
                noHeader = true;
            } else {
            	System.out.println ("arg: '" + arg + "'");
//                System.err.println("Invalid argument : " + arg);
//                displayUsage();
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

//        EventGenerateAgent mbsAgent = new EventGenerateAgent(null);

        
        HashMap<String, Long> timeList = new HashMap<String, Long>();

        timeList.put("-1", 0l);


        List<InetAddress> addrList = new ArrayList<InetAddress>();
        System.out.println ("Step 1");
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                List<InterfaceAddress> iAddresses = nic.getInterfaceAddresses();
                System.out.println ("Step 2, nic = '" + nic.getName() + "'");
                for (InterfaceAddress ia : iAddresses) {
                   InetAddress addr = ia.getAddress();
                   System.out.println ("Step 3, addr = '" + addr + "'");
//                    if ((addr.getHostAddress().indexOf("192.168" /* ".57" */) == 0) &&
//                    		(count-- > 0)) {
                   if (addr.getHostAddress().equals ("127.0.0.1")) {
                       System.out.println ("Step 3.1, DO NOT ADD addr = '" + addr + "', loopback");
                   } else if (addr instanceof Inet6Address) {
                	   System.out.println ("Step 3.1, DO NOT ADD addr = '" + addr + "', IPV6");
                   } else {
                       System.out.println ("Step 3.1, ADD addr = '" + addr + "' count = '" + count + "'");
                       addrList.add(addr);
                   }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(MultipleInterfaceCpg.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println ("Step 4");

        int i = 0;
        for (InetAddress addr : addrList) {
            String addrStr = addr.getHostAddress();
            String[] octets = addrStr.split("\\.");
            DecimalFormat df = new DecimalFormat("000");

            String cpgId = df.format (Integer.parseInt(octets[2])) + "" + df.format (Integer.parseInt(octets[3]));
            
            System.out.println ("Step 5, addr = '" + addr + "', id = '" + cpgId + "'");
            CpgStream s = null;
            try {
                s = new CpgStream (cpgId, addr, host, port);
                s.setNoHeader (noHeader);
            } catch (SocketTimeoutException ex) {
                System.out.println("Waiting 10 seconds because of timeout");

                try {
                    TimeUnit.SECONDS.sleep (10);
                } catch (InterruptedException ex1) {
                    Logger.getLogger (MultipleInterfaceCpg.class.getName ()).log (Level.SEVERE, null, ex1);
                }
                continue;

            }

            System.out.println ("Step 6");
            Thread t = new Thread(s);
            cpgList.add(s);
            t.start();

            timeList.put(s.getCpgId(), 0l);

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
                    Logger.getLogger(MultipleInterfaceCpg.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            i++;
        }
        System.out.println ("Step 7");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat df = new DecimalFormat("###,###,###,###,###");

        boolean zeroDelaySet = false;
        while (true) {

            System.out.println ("Step 8");
            long totalEventCount = 0;
            String time = sdf.format(Calendar.getInstance().getTime());
            for (CpgStream s : cpgList) {
                long lastCount = timeList.get(s.getCpgId());
                long c = s.getEventCount();
                long delta = c - lastCount;
                timeList.put(s.getCpgId(), c);
                System.out.println(time + " - CPG " + s.getCpgId() + ", Total : " + df.format(c) + "; Last Minute : " + df.format(delta) + "; events/sec : " + df.format((delta / 60)));
                totalEventCount += c;
            }
            long lastCount = timeList.get("-1");
            long delta = totalEventCount - lastCount;
            timeList.put("-1", totalEventCount);
            System.out.println("\n" + time + " - Total Count : " + df.format(totalEventCount) + "; Last Minute : " + df.format(delta) + "; events/sec : " + df.format((delta / 60)) +
            		"\n\n========================================================================\n\n");
            EventGenerate.counter = df.format(totalEventCount);
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException ex) {
                Logger.getLogger(MultipleInterfaceCpg.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (max == true && zeroDelaySet == false) {
                EventGenerate.delayBetweenEvents = 0;
                zeroDelaySet = true;
            }
        }
    }

    public static void main(String[] args) {
        new MultipleInterfaceCpg().runSimulator(args);
    }
}
