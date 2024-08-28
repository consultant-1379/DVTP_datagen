/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class EventGenerate implements EventGenerateMBean {

    public static volatile int delayCtrMilliSeconds = 0;
    public static volatile String counter = "0";
    public static volatile int delayBetweenEvents = 100;

    private MultipleInterface mi;

    public EventGenerate(MultipleInterface mi) {
        this.mi = mi;
    }

    @Override
    public String getCounter() {
        return counter;
    }

    @Override
    public int getDelayBetweenEvents() {
        return delayBetweenEvents;
    }

    @Override
    public void setDelayBetweenEvents(int delayBetweenEvents) {
        EventGenerate.delayBetweenEvents = delayBetweenEvents;
    }

    @Override
    public void stopStreaming(String eNodeBId) {
        
        List<Stream> stoppedStreams = new ArrayList<Stream>();
        List<Stream> streams = mi.getENodeBList();
        for (Stream s : streams) {
            if (s.getENodeBId().equals(eNodeBId)) {
                System.out.println("Stopping "+eNodeBId+", Scanner = "+s.getScannerId());
                s.setStopStreaming(true);
                stoppedStreams.add(s);

            }
        }
        for (Stream stoppedStream : stoppedStreams) {
            if (stoppedStream != null) {
                streams.remove(stoppedStream);
                mi.getTimeList().remove(stoppedStream.getENodeBId() + "-" + stoppedStream.getScannerId());
            }
        }
    }

    @Override
    public void startStreaming(String eNodeBId, Long droppedEvents) {
        for (Stream s : mi.getENodeBList()) {
            if (s.getENodeBId().equals(eNodeBId)) {
                throw new IllegalArgumentException("This eNodeB " + eNodeBId + " is currently streaming. Please stop it or try another eNodeB");
            }
        }

        InetAddress addr = getNetworkInterfaceFromId(eNodeBId);
        if (addr != null) {
            for (int scanner : mi.getScanners()) {
                try {
                    Stream s = new StreamScenario(eNodeBId, addr, mi.getHost(), mi.getPort(), scanner);
                    if (droppedEvents != null && droppedEvents > 0) {
                        s.setDroppedEvents(droppedEvents);
                    }
                    mi.getENodeBList().add(s);
                    mi.getTimeList().put(s.getENodeBId() + "-" + s.getScannerId(), 0l);
                    Thread t = new Thread(s);
                    t.start();
                    System.out.println("Starting "+eNodeBId+", Scanner = "+s.getScannerId());
                } catch (SocketTimeoutException ex) {
                    Logger.getLogger(EventGenerate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            throw new IllegalArgumentException("This eNodeB " + eNodeBId + " does not have a corresponding interface");
        }
    }

    private InetAddress getNetworkInterfaceFromId(String id) {
        InetAddress address = null;

        List<InetAddress> addrList = new ArrayList<InetAddress>();

        try {
            boolean breakFromNicLoop = false;
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                List<InterfaceAddress> iAddresses = nic.getInterfaceAddresses();
                int addrCount = 0;
                for (InterfaceAddress ia : iAddresses) {
                    InetAddress addr = ia.getAddress();
                    if (addr.getHostAddress().indexOf("192.168.40") == 0
                            || addr.getHostAddress().indexOf("192.168.41") == 0
                            || addr.getHostAddress().indexOf("192.168.42") == 0
                            || addr.getHostAddress().indexOf("192.168.43") == 0
                            || addr.getHostAddress().indexOf("192.168.44") == 0
                            || addr.getHostAddress().indexOf("192.168.45") == 0
                            || addr.getHostAddress().indexOf("192.168.46") == 0
                            || addr.getHostAddress().indexOf("192.168.47") == 0
                            || addr.getHostAddress().indexOf("192.168.48") == 0
                            || addr.getHostAddress().indexOf("192.168.49") == 0) {

                        addrList.add(addr);
                        addrCount++;
                    }
                }
                if (breakFromNicLoop == true) {
                    break;
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(EventGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (InetAddress addr : addrList) {
            String addrStr = addr.getHostAddress();
            String[] octets = addrStr.split("\\.");
            DecimalFormat df = new DecimalFormat("000");

            String eNodeBId = df.format(Integer.parseInt(octets[2])) + "" + df.format(Integer.parseInt(octets[3]));
            if (eNodeBId.equals(id)) {
                address = addr;
            }
        }

        return address;
    }
}
