/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class StreamScenario extends Stream {

    public StreamScenario(String eNodeBId, String destIp, int port, int scanner) throws SocketTimeoutException {
        super(eNodeBId, destIp, port, scanner);
    }

    public StreamScenario(String eNodeBId, InetAddress addr, String destIp, int port, int scanner) throws SocketTimeoutException {
        super(eNodeBId, addr, destIp, port, scanner);
    }

    @Override
    public void run() {
        System.out.println("Starting eNodeB id = " + eNodeBId + ", scanner = " + getScannerId());
        byte[] header = generateGoodEnbHeader();
        
        if(droppedEvents != null) {
            header = generateDroppedEnbHeader(droppedEvents);
        }

        if (delayHeaderTransmission == true) {
            System.out.println("eNodeB " + eNodeBId + " - Delaying header transmission.");
            for (int c = 0; c < 5; c++) {
                List<EventData> measurements = getMeasurements();
                for (EventData ed : measurements) {
                    streamEvent(ed.getBa(), MEASUREMENT);
                }
            }
        }

        if (isNoHeader() == false) {
            streamEvent(header, HEADER);
        }

        long delay = EventGenerate.delayBetweenEvents;

        while (stopStreaming == false) {
            delay = EventGenerate.delayBetweenEvents;

            if (delay > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Stream.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

//            Code below needs work, because eNodeBId in Stream Class was changed to String type
//            if (EventGenerate.stream_dropped_events_header_enodeb_id == eNodeBId) {
//                System.out.println("Streaming Dropped Event headers for eNodeB : " + eNodeBId);
//                int numberOfDroppedEvents = EventGenerate.stream_dropped_events_header_dropped_events;
//                streamEvent(generateDroppedEnbHeader(numberOfDroppedEvents), HEADER);
//                EventGenerate.stream_dropped_events_header_enodeb_id = -1;
//            }

//            List<EventData> attachProcedure = getUeInitialAttachProcedure();
//            for (EventData ed : attachProcedure) {
//                streamEvent(ed.getBa(), "Attach");
//            }

            EventData idlePrcedure = getUeIdleProcedure();
            streamEvent(idlePrcedure.getBa(), "Idle");
            updateEnbS1ApId();

//            List<EventData> activeProcedure = getUeActiveProcedure();
//            for (EventData ed : activeProcedure) {
//                streamEvent(ed.getBa(), "Active");
//            }
//            List<EventData> measurements = getMeasurements();
//            for (EventData ed : measurements) {
//                streamEvent(ed.getBa(), "Measurement");
//            }

        }

        close();

    }
}
