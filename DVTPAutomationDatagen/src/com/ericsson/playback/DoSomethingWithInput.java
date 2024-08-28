package com.ericsson.playback;

import java.io.*;
import java.net.*;

public class DoSomethingWithInput implements Runnable {
	   private final Socket clientSocket; //initialize in const'r
	   public DoSomethingWithInput(Socket clientSocket2) {
		   this.clientSocket=clientSocket2;
	}
	public void run() {

	        
	        String nextline;
	        int temp,length=0;
	        /*try {
	        	BufferedInputStream  in = new BufferedInputStream (clientSocket.getInputStream());
				while ((temp = in.read()) != -1 ) {   
			    length+=1;
				}
					
			    in.close();
			    System.out.println("total size= "+length);
			    
			} catch (IOException e) {
				e.printStackTrace();
			} */
	    }
	}
