# Best Practices when working with Vault API Rate Limits - BestPractices-with-RateLimits
**Please see the [project wiki](https://github.com/baumandn/BestPractices-with-RateLimits/wiki) for detailed walkthroug**

This project demonstrates some best practices when developing programs to interact with a Vevea Vault environment using the API. To run this requires an IDE and API access to a Vault environment on General Release 19R2 or later.

This contains example of:
* Reducing Authentication Calls
* Monitoring and regulating API request rates

## How to import

1. Clone or Download the sample project from GitHub
2. Unzip the file in your desired location
   * If using **Eclipse**, select File -> Import -> Projects from Folder or Archive, Press the **Directory** button and navigate to the **BestPractices-with-RateLimits-master** folder, and press **Finish**
   * If using **IntelliJ**, select **Import Project** navigate into the **BestPractices-with-RateLimits-master** and select the **.Project** file, click through **Next** and then **Finish**
3. Open the `httpConnector` class
4. Enter your Vault environment URL as a String on line 28, replacing the text and brackets

```
	//Enter your environment URL
	private String environment =  "{enter your Vault URL}";
```
5. Enter your Vault username and password as Strings on lines 44 and 45 respectively, replacing the text and brackets

```
		//Enter user information
		String user = "{enter your Vault username}";
		String pass = "{enter your Vault password}";
```

The main method for this project is held in `com.bauman.bestPractices.rateLimitBestPractices`, which class calls methods in `com.bauman.httpComms.httpConnector` in order to connect to Vault using the API.
