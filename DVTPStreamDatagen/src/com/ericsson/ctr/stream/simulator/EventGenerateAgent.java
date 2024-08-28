/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.ctr.stream.simulator;

import com.sun.jdmk.comm.HtmlAdaptorServer;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 *
 * @author ejactho
 */
public class EventGenerateAgent {

    private MBeanServer mbs;
    private HtmlAdaptorServer adapter;
    
    public EventGenerateAgent(MultipleInterface mi) {
        mbs = ManagementFactory.getPlatformMBeanServer();
        EventGenerate egBean = new EventGenerate(mi);

        adapter = new HtmlAdaptorServer();

        ObjectName egBeanObjectName = null;
        ObjectName adapterName = null;

        try {
            egBeanObjectName = new ObjectName("EventGenerate:name=EventGenerateAgent");
            mbs.registerMBean(egBean, egBeanObjectName);

            adapterName = new ObjectName("SimpleAgent:name=htmladapter,port=8000");
            adapter.setPort(8000);

            mbs.registerMBean(adapter, adapterName);
            adapter.start();



        } catch (InstanceAlreadyExistsException ex) {
            Logger.getLogger(EventGenerateAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MBeanRegistrationException ex) {
            Logger.getLogger(EventGenerateAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotCompliantMBeanException ex) {
            Logger.getLogger(EventGenerateAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(EventGenerateAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(EventGenerateAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stop() {
        adapter.stop();
        
    }
}
