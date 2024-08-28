/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

/**
 *
 * @author ejactho
 */
public interface EventGenerateMBean {

    /**
     * Returns the total number of events streamed
     * @return
     */
    public String getCounter();

    /**
     * Returns the delay between event's streamed
     * @return
     */
    public int getDelayBetweenEvents();

    /**
     * Sets the delay in milliseconds between streaming of events. Adjust this parameter to change event throughput. 
     * @param delayBetweenEvents
     */
    public void setDelayBetweenEvents(int delayBetweenEvents);
    
    
    /**
     * Transmits a new header
     * @param eNodeBId   The eNodeBId for which the header is transmitted
     * @param droppedEvents number of dropped events
     */
    
    
    public void stopStreaming(String eNodeBId);
    
    public void startStreaming(String eNodeBId, Long droppedEvents);
       
}

