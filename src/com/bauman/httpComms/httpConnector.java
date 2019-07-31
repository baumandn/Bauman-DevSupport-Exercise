package com.bauman.httpComms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.net.ssl.HttpsURLConnection;

/**
 * 
 * Reusable HTTP calls to authenticate and interact with Vault environment.
 * 
 * @author Dan Bauman
 * 
 **/

public class httpConnector {

	//Enter your environment URL
	private String environment =  "{enter your Vault URL}";
	
	//Initiate Strings for storing authentication token and API version URL
	private String auth;
	private String versionURL;
	

	/*
	 * Generate Authentication token. Vault environment and user information collected and stored.
	 * 
	 * Enter your user information for connecting to Vault.
	 * 
	 */
	public void vaultAuthGen() throws Exception {
		
		//Enter user information
		String user = "{enter your Vault username}";
		String pass = "{enter your Vault password}";
		
		System.out.println("Starting Authentication");
		
		//Set URL for request
		String callUrl = environment + "/api/v19.2/auth";
		URL obj = new URL(callUrl);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//Set request method
		con.setRequestMethod("POST");
		
		//Add username and password parameters to URL for authentication
		String urlParameters = "username=" + user + "&password=" + pass;
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());		
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		//Record call made and process response
		int responseCode = con.getResponseCode();
		System.out.println("Sending request to URL : " + callUrl);
		System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				
			}
			
			//Check response status for success or failure
			JSONParser myJson = new JSONParser();
			JSONObject responseJson = (JSONObject) myJson.parse(response.toString());
			String responseStatus = (String) responseJson.get("responseStatus");
			System.out.println("Response Status: " + responseStatus);
			
			//Print failure string or store authentication token
			if (responseStatus.equalsIgnoreCase("success")) {
			
				auth = (String) responseJson.get("sessionId");
				System.out.println("Authentication Successful");
				
			}
			else if  (responseStatus.equalsIgnoreCase("failure")) {
				
				JSONArray failDetails = (JSONArray) responseJson.get("errors");
				String failure = failDetails.toJSONString();
				System.out.println("Failed to authenticate: " + failure);
				
			}
			
			else {
				System.out.println("Unaccounted for response status in authentication.");
			}
			
		}
	
	/*
	 * Calls the getVersions method every 19 minutes in order to keep current Authentication Token valid. 
	 *
	 * @return Scheduled Service started by method
	 * 
	 */
	 
	public ScheduledExecutorService vaultAuthKeepAlive() throws Exception {
		
		System.out.println("Keep Alive Initiated");
		Runnable keepAlive = new Runnable() {
			public void run() {
				try {
					System.out.println("Staying alive: " + LocalDateTime.now());
					getVersions();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		};
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(keepAlive , 0, 19, TimeUnit.MINUTES);
		return exec;

	}
	
	/*
	 * Ends the passed Scheduled Service  to stop from keeping current Authentication Token valid.
	 * 
	 * @param Schedule Service for shut down
	 * 
	 */
	public void vaultStopKeepAlive(ScheduledExecutorService exec) throws Exception {
		
		System.out.println("Keep Alive Halt");
		exec.shutdown();

	}
	
	/*
	 * Check current API limits from a request's returned HTTP headers.
	 * 
	 * @param current API call connection
	 * 
	 */
	public void checkAPILimits(HttpURLConnection connector) throws Exception {

		System.out.println("Checking API Usage");
		
		//Get headers from call response and store as doubles
		double dailyCount = Integer.parseInt(connector.getHeaderField("X-VaultAPI-DailyLimitRemaining"));
		double burstCount = Integer.parseInt(connector.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
		double dailyLimit = Integer.parseInt(connector.getHeaderField("X-VaultAPI-DailyLimit"));
		double burstLimit = Integer.parseInt(connector.getHeaderField("X-VaultAPI-BurstLimit"));
		
		//Write remaining limit as percentage reached
		double dailyPercentRemaining = (dailyCount * 100) / dailyLimit;
		double burstPrecentRemaining = (burstCount * 100) / burstLimit;
		
		//Set sleep delay timer depending on percentage of API limits remaining
	    if (burstPrecentRemaining < 50  || dailyPercentRemaining < 25) {
	    	
	    	//delay 1 second
		    System.out.println("Delaying 1 second\n");
	    	Thread.sleep(1000);
	
	    }
	    else if (burstPrecentRemaining < 75 || dailyPercentRemaining < 50) {
	    	
	    	//Delay 1/4 of a second
	    	System.out.println("Delaying 1/4 second\n");    
	    	Thread.sleep(250);
	    	
	    }
	    else {
	    	
	    	System.out.println("No delay\n");    
	
	    }
	    
	}
	
	/*
	 * Makes HTTP request for available API versions of current environment
	 * 
	 */
	public void getVersions() throws Exception {
		
		//Set URL for request
		String url = environment +"/api";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//Set request method
		con.setRequestMethod("GET");

		//Set request headers
		con.setRequestProperty("Authorization", auth);

		//Record call made and process response
		int responseCode = con.getResponseCode();
		System.out.println("Sending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//Check response status for success or failure
		JSONParser myJson = new JSONParser();
		JSONObject responseJson = (JSONObject) myJson.parse(response.toString());
		String responseStatus = (String) responseJson.get("responseStatus");
		System.out.println("Response Status: " + responseStatus);
		
		if (responseStatus.equalsIgnoreCase("success")){
			
			//Store version 19.2 from returned values for future requests
			JSONObject versionValues = (JSONObject) responseJson.get("values");
			versionURL = (String) versionValues.get("v19.2");
			
		}
		else {
			
			System.out.println("Error getting versions");
			
		}
		
		//Check current API usage to see if delay needed
		checkAPILimits(con);

	}
	
}