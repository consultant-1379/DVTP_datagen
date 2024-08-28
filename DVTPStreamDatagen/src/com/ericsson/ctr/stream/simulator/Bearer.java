package com.ericsson.ctr.stream.simulator;

import java.lang.StringBuffer;
import java.util.List;

public class Bearer {

    int id;
    int defaultBearerId;
    int qci = 8;

    public Bearer(int id, int defaultBearerId) {
        super();
        this.id = id;
        this.defaultBearerId = defaultBearerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDefaultBearerId() {
        return defaultBearerId;
    }

    public void setDefaultBearerId(int defaultBearerId) {
        this.defaultBearerId = defaultBearerId;
    }

    public int getQci() {
        return qci;
    }

    public void setQci(int qci) {
        this.qci = qci;
    }
    
    public static String stringList (List<Bearer> bearerList) {
    	StringBuffer retS = new StringBuffer (400);
    	
    	retS.append ("[");
        for (Bearer b : bearerList) {
        	retS.append (b.id);
        	retS.append (";0;");
        	retS.append (b.qci);
        	retS.append (";0;0|");
        }
        if (bearerList.size () > 0) {
        	retS.replace (retS.length () - 1, retS.length (), "]");
        } else {
        	retS.append ("]");
        }
    	return retS.toString ();
    }
}
