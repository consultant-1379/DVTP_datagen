package com.ericsson.playback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

import com.ericsson.ctr.stream.simulator.ConnectorItem;

import com.ericsson.ctr.stream.simulator.Utilities;

public class NodeStreamSimulator extends Thread {

    private String objId;
    private ConnectorItem connItem;
    
    private String precookDirectory;

	public NodeStreamSimulator (String objId, ConnectorItem connItem) {
     	this.objId = objId;
    	this.connItem = connItem;
    	precookDirectory=Utilities.SEED_BIN_OP_FILE+Utilities.NODE_TYPE+"/"+this.objId+"/";
    	
	}
	
	
	
	public void run() {
		
		int count=0;
		File f = new File(precookDirectory);
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH.mm");

		String[] files = f.list();
		Arrays.sort(files);
		
		
		String[] currentTime=timeFormat.format (System.currentTimeMillis ()).split("\\.");
		currentTime="11.40".split("\\.");
		int firstFileIndex=-1;
		
		
		for(String currentFile : files) {
				String fileName[] = currentFile.split("-");
				String[] fileEndTime=fileName[1].split("\\.");
				if(checkWithCurrentTime(fileEndTime,currentTime))
					{firstFileIndex++;
					}
				else break;
			}
		
		if (firstFileIndex>files.length)
			return;
		
		
		
		for(int i=firstFileIndex;i<files.length;i++) {
		try {
			String fileTime[] = files[i].split("-");
			String[] fileCloseTime=fileTime[1].split("\\.");
			
			
		    //currentTime=timeFormat.format (System.currentTimeMillis ()).split("\\.");
			if(checkWithCurrentTime(fileCloseTime,currentTime)==false)
				break;
				
			while(checkWithCurrentTime(fileCloseTime,currentTime)==false)
	    		Utilities.Sleep (1000);//wait
				
			
			InputStream in = new FileInputStream(precookDirectory+files[i]);
			byte[] buf = new byte[1];
            int len;
            while ((len = in.read(buf)) > 0) {
            	connItem.put(buf);
            	count+=buf.length;
            }
            in.close();
            
            System.out.println("File: "+files[i]+" ObjId: "+objId+" Size: "+count);
            
        } catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		connItem.close();
		
	}
	
	
	private boolean checkWithCurrentTime(String[] eventTime,	String[] currentTime) {
		//String[] currentTime=currentTime.format (System.currentTimeMillis ()).split("\\.");
		if(Integer.parseInt(eventTime[0])< Integer.parseInt(currentTime[0]))
			return true;
		else if(Integer.parseInt(eventTime[0])== Integer.parseInt(currentTime[0]) &&
				Integer.parseInt(eventTime[1])< Integer.parseInt(currentTime[1]))
			return true;
		
		else return false;
	}
	
	
}
