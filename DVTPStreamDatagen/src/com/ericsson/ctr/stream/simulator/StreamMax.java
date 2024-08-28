/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 *
 * @author ejactho
 */
public class StreamMax extends Stream {

    public StreamMax(String eNodeBId, String destIp, int port, int scanner) throws SocketTimeoutException {
        super(eNodeBId, destIp, port, scanner);
    }

    public StreamMax(String eNodeBId, InetAddress addr, String destIp, int port, int scanner) throws SocketTimeoutException {
        super(eNodeBId, addr, destIp, port, scanner);
    }

    @Override
    public void run() {
        System.out.println("Starting eNodeB id = " + eNodeBId+", scanner = "+getScannerId());
        if(isNoHeader() == false)  {
            byte[] header = generateGoodEnbHeader();
            streamEvent(header, HEADER);
        }
        byte[] ueTrafficRepForMaxThroughput = generateUeTrafficRepEvent();

        while (stopStreaming == false) {
            streamEvent(ueTrafficRepForMaxThroughput, null);

        }


        close();

    }
}
