package com.ericsson.ctr.stream.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ejactho
 */
public class Utilities {

	public static final String tFstrYearMilli  = "yyyy,M,d,H,m,s,S";
	public static final String tFstrYearSecT   = "yyyy,M,d,H,m,s,z";
	public static final String tFstrHourMilli  = "H,m,s,S";
	public static final String tFStrDisp       = "EEE MMM dd HH:mm:ss zzz yyyy";
	public static final String fileNameTimeFrmt  = "HH.mm";
	public static final String tFStrDate       = "yyyy,MM,dd"; 
	
	public static final TimeZone tzGmt         = TimeZone.getTimeZone("GMT");
	public static final String  SEED_BIN_OP_FILE="resources/seed-binary/";
	public static final String  TEMP_DIR="resources/tmp/";
	public static final String SEED_DIR = "resources/seed";
	public static final int SEEDFILE_DURATION = 2 ;
	
	
	public static  String  NODE_TYPE;

	
	public static boolean boolStr (String val) {
		
		if (val == null) {
			return true;
		} else if (val.length () == 0) {
			return true;
		} else if ((val.equals("false")) || (val.equals("0"))) {
			return false;
		} else {
			return true;
		}
	}
	
	
    public static String generateGummei(String plmnIdentity, int mmegi, int mmec) {
        if (plmnIdentity != null && plmnIdentity.length() == 6) {
            return
                plmnIdentity.charAt(1) +
                plmnIdentity.charAt(0) +
                plmnIdentity.charAt(3) +
                plmnIdentity.charAt(2) +
                plmnIdentity.charAt(5) +
                plmnIdentity.charAt(4) +
                String.format("%x", mmegi) +
                String.format("%02x", mmec);
        } else {
            return "";
        }
    }

    
    public static void Sleep (int milliSeconds) {
        if (milliSeconds > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep (milliSeconds);
            } catch (InterruptedException ex) {
                Logger.getLogger (Stream.class.getName ()).log (Level.SEVERE, null, ex);
            }
        }
    	
    }
   
    private static final DecimalFormat itemIdDf = new DecimalFormat("000");
    
    public static String itemId (int i) {
    	
    	if(Utilities.NODE_TYPE.equals("Pgw"))
			return "pgw_" + itemIdDf.format (i);
		else if(Utilities.NODE_TYPE.equals("Sgeh"))
			return "sgeh_" + itemIdDf.format (i);
		else return null;
    }
   
    public static void cleanDirectory(File location)throws IOException
    {
    	
    		for(File file: location.listFiles()){
    			if (file.isDirectory()) cleanDirectory(file);
    			file.delete();
    		}
    }
    
    public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
}
