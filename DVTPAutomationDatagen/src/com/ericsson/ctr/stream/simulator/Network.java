package com.ericsson.ctr.stream.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Network {

    public static int mcc = 311;
    public static int mnc = 480;
    public static String plmnIdentity = "" + mcc + "" + mnc;
    public static int eci = 256522;
    private static String voicePdn = "ims.verizon.com";
    private static String inetPdn = "inet.verizon.com";
    public static final String ATTR_CALLMODE = "callMode";
    public static final int CALL_MODE_VOICE = 0;
    public static final int CALL_MODE_INET = 1;
    public static final String ATTR_CALLMODE_MIX = "CallModeMix";

    public static String getVoicePdn() {
        return voicePdn;
    }

    public static String getInetPdn() {
        return inetPdn;
    }
    // first element is the default bearer ID. And the array must be in a sorted order
    private static int[] voiceBearers = {5, 6};
    private static int[] inetBearers = {7, 8};

    public static List<Bearer> getAllVoiceBearers() {
        List<Bearer> blist = new ArrayList<Bearer>();

        for (int i = 0; i < voiceBearers.length; i++) {
            blist.add(new Bearer(voiceBearers[i], voiceBearers[0]));
        }
        return blist;
    }

    public static List<Bearer> getAllInetBearers() {
        List<Bearer> blist = new ArrayList<Bearer>();

        for (int i = 0; i < inetBearers.length; i++) {
            blist.add(new Bearer(inetBearers[i], inetBearers[0]));
        }
        return blist;
    }

    public static List<Bearer> getAllInetBearersPlusDefaultVoiceBearer() {
        List<Bearer> blist = new ArrayList<Bearer>();

        blist.add(new Bearer(voiceBearers[0], voiceBearers[0]));

        for (int i = 0; i < inetBearers.length; i++) {
            blist.add(new Bearer(inetBearers[i], inetBearers[0]));
        }
        return blist;
    }

    public static List<Bearer> getDefaultVoiceBearer() {
        List<Bearer> blist = new ArrayList<Bearer>();

        blist.add(new Bearer(voiceBearers[0], voiceBearers[0]));

        return blist;
    }

    public static List<Bearer> getDefaultInetBearer() {
        List<Bearer> blist = new ArrayList<Bearer>();

        blist.add(new Bearer(inetBearers[0], inetBearers[0]));

        return blist;
    }

    public static List<Bearer> getAllBearersByCallMode(int mode) {
        if (mode == CALL_MODE_INET) {
            return getAllInetBearers();
        } else {
            return getAllVoiceBearers();
        }
    }

    public static List<Bearer> getDefaultBearerByCallMode(int callMode) {
        if (callMode == CALL_MODE_INET) {
            return getDefaultInetBearer();
        } else {
            return getDefaultVoiceBearer();
        }
    }

    public static int getdefaultBearerIdbyCallMode(int callMode) {
        return getDefaultBearerByCallMode(callMode).get(0).getId();
    }

    /**
     * It will search for both voice bearer list and inet bearer list to get the default id. 
     * If the input is a default bearer id, the same id will be used as the output
     * @param d - dedicated bearer Id
     * @return - default bearer Id. "0" is used if the default bearer id is not found.
     */
    public static int getDefaultIdByDedicateBearer(int d) {

        int r = 0;

        r = Arrays.binarySearch(voiceBearers, d);

        if (r >= 0) {
            return voiceBearers[r];
        } else {

            r = Arrays.binarySearch(inetBearers, d);

            if (r >= 0) {
                return inetBearers[r];
            } else {
                return 0;
            }
        }

    }

    public static int getCallModeByBearerId(int d) {

        int r = Arrays.binarySearch(inetBearers, d);

        if (r >= 0) {
            return CALL_MODE_INET;
        } else {
            return CALL_MODE_VOICE;
        }
    }

    public static String getPdnByCallMode(int mode) {

        if (mode == CALL_MODE_VOICE) {
            return getVoicePdn();
        } else {
            return getInetPdn();
        }

    }

    public static String getPdnByBearerId(int d) {

        int mode = getCallModeByBearerId(d);
        return getPdnByCallMode(mode);
    }

    public static int getVoiceBearerDedicate() {
        return voiceBearers[1];
    }

    public static int getVoiceBearerDefault() {
        return voiceBearers[0];
    }

    public static int getInetBearerDedicate() {
        return inetBearers[1];
    }

    public static int getInetBearerDefault() {
        return inetBearers[0];
    }
}
