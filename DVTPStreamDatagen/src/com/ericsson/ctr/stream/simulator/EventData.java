/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

/**
 *
 * @author ejactho
 */
public class EventData {

    private long delay;
    private byte[] ba;
    private String type;

    public EventData(long delay, byte[] ba, String type) {
        this.delay = delay;
        this.ba = ba;
        this.type = type;
    }

//    public EventData(byte[] ba, String type) {
//        this.type = type;
//        this.delay = 0;
//        this.ba = ba;
//    }

    /**
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return the ba
     */
    public byte[] getBa() {
        return ba;
    }

    /**
     * @param ba the ba to set
     */
    public void setBa(byte[] ba) {
        this.ba = ba;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
