package com.expleoautomation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class CommonUtils {

	/**
	   * 
	   * @param numofDays
	   * @param strdateFormat
	   * @return a date in the given format with the number of days added or subtracted
	   */
	  public static String requiredDate(int numofDays,String strdateFormat)
	  {
	 	 DateFormat dateFormat = new SimpleDateFormat(strdateFormat);
	 	 Calendar c = Calendar.getInstance();
	 	 c.add(Calendar.DATE,numofDays);
	 	 Date newDate = c.getTime();
	 	 return dateFormat.format(newDate).toString();
	 		 
	  }
	  
	  public Properties loadPropertiesFile(String filePath) {

	      Properties prop = new Properties();
	      try (InputStream resourceAsStream = new FileInputStream(filePath) ){
	    		  //getClass().getClassLoader().getResourceAsStream(filePath)) {
	          prop.load(resourceAsStream);
	      } catch (IOException e) {
	          System.err.println("Unable to load properties file : " + filePath);
	      }
	      return prop;

	  }
	  
	  
	  public static String getUserDirectory()
	  {
		   String userDirectory = Paths.get("").toAbsolutePath().toString();
		   return userDirectory;
		  
		  
	  }
	 
}
