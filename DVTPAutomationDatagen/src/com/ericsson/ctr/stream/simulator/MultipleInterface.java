/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
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
public class MultipleInterface {

    private int count = -1;
    private String host = "atrcxb1023.athtem.eei.ericsson.se";
    private int port = 4001;
    private boolean max = false;
    private boolean delayAllHeaders = false;
    private boolean delaySomeHeaders = false;
    private List<Stream> eNodeBList = new ArrayList<Stream>();
    private HashMap<String, Long> timeList = new HashMap<String, Long>();
    private int eci = 1000;
    private int eps = 10;
    private boolean noHeader = false;
    private List<Integer> scanners;
    private Long droppedEvents = null;
    private String bindAddressFileList = null;

    public List<Stream> getENodeBList() {
        return geteNodeBList();
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
            } else if (parameter.equals("--droppedevents")) {
                droppedEvents = Long.parseLong(value);
            } else if (parameter.equals("--initeci")) {
                eci = Integer.parseInt(value);
            } else if (parameter.equals("--bindfile")) {
                bindAddressFileList = value;
            } else if (parameter.equals("--help")) {
                displayUsage();
            } else if (parameter.equals("--noheader")) {
                noHeader = true;
            } else if (parameter.equals("--scanners")) {
                scanners = new ArrayList<Integer>();
                if (value.indexOf(",") != -1) {
                    StringTokenizer scnrs = new StringTokenizer(value, ",");
                    while (scnrs.hasMoreTokens()) {

                        String s = scnrs.nextToken();
                        System.out.println(s);
                        getScanners().add(Integer.parseInt(s));
                    }
                } else {
                    getScanners().add(Integer.parseInt(value));
                }
            } else {
                System.err.println("Invalid argument : " + arg);
                displayUsage();
            }

        }

        if (getScanners() == null) {
            scanners = new ArrayList<Integer>();
            getScanners().add(10000);
        }
    }

    public void calculateThreadSleepTime() {
        int threadSleepTime = 1000 / eps;
        EventGenerate.delayBetweenEvents = threadSleepTime;
    }

    public void runSimulator(String[] args) {
        processArguments(args);
        calculateThreadSleepTime();

        EventGenerateAgent mbsAgent = new EventGenerateAgent(this);

        int i = 0;


        getTimeList().put("-1", 0l);


        List<InetAddress> addrList = new ArrayList<InetAddress>();
        List<String> bindAddressList = new ArrayList<String>();
        if (bindAddressFileList != null) {
            try {
                File bindFile = new File(bindAddressFileList);
                if (bindFile.canRead()) {
                    BufferedReader br = new BufferedReader(new FileReader(bindFile));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        bindAddressList.add(line.trim());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MultipleInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (bindAddressList.isEmpty()) {
            bindAddressList.add("192.168");
        }

        try {

            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                List<InterfaceAddress> iAddresses = nic.getInterfaceAddresses();
                for (InterfaceAddress ia : iAddresses) {
                    InetAddress addr = ia.getAddress();
                    for (String bindAddr : bindAddressList) {
                        if (bindAddr.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
                            if (addr.getHostAddress().equals(bindAddr)) {
                                addrList.add(addr);
                            }
                        } else if (addr.getHostAddress().indexOf(bindAddr + ".") == 0) {
                            addrList.add(addr);
                        } else if (addr.getHostAddress().indexOf(bindAddr + ":") == 0) {
                            addrList.add(addr);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(MultipleInterface.class.getName()).log(Level.SEVERE, null, ex);
        }

        DecimalFormat eNodeBIpDf = new DecimalFormat("000");
        
        for (InetAddress addr : addrList) {
            String hostaddress = addr.getHostAddress();
            
            String eNodeBId = null;
            System.out.println(hostaddress);
            String [] octets = hostaddress.split("\\.");
            if(octets != null && octets.length == 4) {
                eNodeBId = eNodeBIpDf.format(Integer.parseInt(octets[2])) + "" + eNodeBIpDf.format(Integer.parseInt(octets[3]));
            } else {
                octets = hostaddress.split(":");
                eNodeBId = eNodeBIpDf.format(Long.parseLong(octets[octets.length-1].replaceAll("%.*$", ""), 16));
            }

            System.out.println("Using " + hostaddress+". Computed eNodeB Id = "+eNodeBId);            

            for (int scanner : getScanners()) {
                Stream s = null;
                try {

                    if (max == true) {
                        s = new StreamMax(eNodeBId, addr, getHost(), getPort(), scanner);
                    } else {
                        s = new StreamScenario(eNodeBId, addr, getHost(), getPort(), scanner);
                    }
                    s.setNoHeader(noHeader);
                    s.setDroppedEvents(droppedEvents);


                } catch (SocketTimeoutException ex) {
                    System.out.println("Waiting 10 seconds because of timeout");
                    i--;

                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(MultipleInterface.class.getName()).log(Level.SEVERE, null, ex1);
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
                geteNodeBList().add(s);
                t.start();

                getTimeList().put(s.getENodeBId() + "-" + s.getScannerId(), 0l);

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
                        Logger.getLogger(MultipleInterface.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                i++;
            }

        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat df = new DecimalFormat("###,###,###,###,###");

        boolean zeroDelaySet = false;
        while (true) {

            long totalEventCount = 0;
            String time = sdf.format(Calendar.getInstance().getTime());
            for (Stream s : geteNodeBList()) {
                long lastCount = getTimeList().get(s.getENodeBId() + "-" + s.getScannerId());
                long c = s.getEventCount();
                long delta = c - lastCount;
                getTimeList().put(s.getENodeBId() + "-" + s.getScannerId(), c);
                System.out.println(time + " - eNodeB " + s.getENodeBId() + ", scanner = " + s.getScannerId() + ", Total : " + df.format(c) + "; Last Minute : " + df.format(delta) + "; events/sec : " + df.format((delta / 60)));
                totalEventCount += c;
            }
            long lastCount = getTimeList().get("-1");
            long delta = totalEventCount - lastCount;
            getTimeList().put("-1", totalEventCount);
            System.out.println("\n" + time + " - Total Count : " + df.format(totalEventCount) + "; Last Minute : " + df.format(delta) + "; events/sec : " + df.format((delta / 60)) + "\n\n========================================================================\n\n");
            EventGenerate.counter = df.format(totalEventCount);
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException ex) {
                Logger.getLogger(MultipleInterface.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (max == true && zeroDelaySet == false) {
                EventGenerate.delayBetweenEvents = 0;
                zeroDelaySet = true;
            }
        }
    }

    public static void main(String[] args) {
        org.apache.log4j.Logger lg = org.apache.log4j.Logger.getLogger("com");
        lg.setLevel(org.apache.log4j.Level.OFF);
        new MultipleInterface().runSimulator(args);
    }

    /**
     * @return the eNodeBList
     */
    public List<Stream> geteNodeBList() {
        return eNodeBList;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the scanners
     */
    public List<Integer> getScanners() {
        return scanners;
    }

    /**
     * @return the timeList
     */
    public HashMap<String, Long> getTimeList() {
        return timeList;
    }
}
