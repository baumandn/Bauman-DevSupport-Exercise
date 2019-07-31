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
			
			System.out.println("\nLoop #" + (i+1));
			callMaker.getVersions();		
		
		}
		
		//Scheduler initiated
		ScheduledExecutorService stayAlive = callMaker.vaultAuthKeepAlive();
		
		//Wait 30 mins
		int thirtyMins = 1800;
		Thread.sleep(thirtyMins*1000);
		
		//Make API request
		callMaker.getVersions();
		
		//Wait 30 mins
		Thread.sleep(thirtyMins*1000);
		
		//Make API request
		callMaker.getVersions();
		
		//Stop scheduler process
		callMaker.vaultStopKeepAlive(stayAlive);
		
		
	}

}