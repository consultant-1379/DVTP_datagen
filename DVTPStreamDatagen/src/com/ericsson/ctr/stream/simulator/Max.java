/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class Max {

    public static void main(String[] args) {
        String host = "atrcxb1863.athtem.eei.ericsson.se";
        int port = 1025;

        int count = 100;
        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            host = args[1];
        }
        try {
            for (int i = 0; i < count; i++) {

                try {
                    StreamMax max = new StreamMax(""+i, host, port, 10000);
                    Thread t = new Thread(max);
                    t.start();

                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (SocketTimeoutException ex) {

                    System.out.println("Waiting 10 seconds because of timeout");
                    TimeUnit.SECONDS.sleep(10);
                    i--;

                }

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Max.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
