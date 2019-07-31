package com.bauman.bestPractices;

import java.util.concurrent.ScheduledExecutorService;
import com.bauman.httpComms.httpConnector;

/**
 * 
 * Main class to demonstrate Best Practices for Developing with Rate Limits. 
 * 
 * @author danbauman
 *
 */

public class rateLimitBestPractices {

	public static void main(String[] args) throws Exception {
		
		httpConnector callMaker = new httpConnector();
		
		callMaker.vaultAuthGen();
		
		//Monitoring and Regulating API Request Rates demonstration 
		for (int i=0; i<2000; i++) {
			
			callMaker.getVersions();
			System.out.println("Loop #" + (i+1));
		
		}
		
		//Schedule 
		ScheduledExecutorService stayAlive = callMaker.vaultAuthKeepAlive();
		
		//wait 30 mins
		int thirtyMins = 1800;
		Thread.sleep(thirtyMins*1000);
		
		//create object metadata file
		callMaker.getVersions();
		
		//wait 30 mins
		Thread.sleep(thirtyMins*1000);
		
		//create document field metadata file
		callMaker.getVersions();
		
		//Stop vaultAuthKeepAlive process
		callMaker.vaultStopKeepAlive(stayAlive);
		
		
	}

}