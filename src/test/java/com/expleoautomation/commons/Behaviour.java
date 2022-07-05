package com.expleoautomation.commons;

// behavioural characteristics
public class Behaviour {
	public static boolean assertFailure = true;				// for negative testing
	public static boolean runSlow = false;					// wait 1s on web click for display
	public static boolean assertFailedSetValue = false;		// fail when set value does not stick
	public static boolean maximizeBrowserWindow = true;		// full screen browser
	public static boolean clearCookiesOnStartup = false;	// clear cookies & other junk 
	public static boolean connectionPooling = true;			// database connection pooling
	public static boolean checkForStaleData = true;			// check patch history when calling findFirst to omit old data
	

}
