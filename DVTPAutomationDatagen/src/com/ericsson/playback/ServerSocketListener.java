package com.ericsson.playback;

import java.io.*;
import java.net.*;

import com.ericsson.ctr.stream.simulator.Utilities;

public class ServerSocketListener {

	public static void main(String[] args) {
		ServerSocket serverSocket =null;
		Socket clientSocket = null;
		
		try {
		    serverSocket = new ServerSocket(4444);
		    for (;;) {
			    
			    clientSocket = serverSocket.accept();
			    //delegate to new thread
			    new Thread(new DoSomethingWithInput(clientSocket)).start();
			}
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
