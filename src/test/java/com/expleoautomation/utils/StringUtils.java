package com.expleoautomation.utils;


import com.google.common.base.Splitter;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class StringUtils {


	 
	/**
	 * @param commaSeparatedExpectedLayersListAsString comma separated strings
	 * @return List containing the comma separated strings each as a separate
	 *         element
	 */
	public static List<String> commaSeparatedStringAsListOfStringsReplacingColonAsSpace(
			String commaSeparatedExpectedLayersListAsString) {
		List<String> stringList = Arrays.asList(org.apache.commons.lang3.StringUtils
				.splitByWholeSeparator(commaSeparatedExpectedLayersListAsString, ","));
		stringList = stringList.stream().map(string -> {
			string = string.replace(':', ' ');
			string = string.replace('"', ' ').trim();
			return string;
		}).collect(Collectors.toList());
		return stringList;
	}

	/**
	 * @param separatedString separated strings
	 * @param separator
	 * @return List containing the comma separarted strings each as a separate
	 *         element
	 */
	public static List<String> separatedStringAsListOfLowercaseStrings(String separatedString, String separator) {
		List<String> stringList = Splitter.on(separator).trimResults().splitToList(separatedString.toLowerCase());
		return stringList;
	}

	public static String removeAllNonAlphaNumericChars(String originalString) {
		String tmp = originalString.replaceAll("[^A-Za-z0-9 ]", "");
		return tmp;
	}

	public static String removeAllNonAlphaNumericCharsAndSpaces(String originalString) {
		String tmp = originalString.replaceAll("[^A-Za-z0-9]", "");
		return tmp;
	}

	// 22 365 86400 max 10 digits 
	public static String getJulianTimeStamp() {
		DateTime now = DateTime.now(); 
		String value = String.valueOf(now.getYear()).substring(2) + String.valueOf(now.getDayOfYear()) + String.valueOf(now.getMillisOfDay());
		log.debug("getJulianTimeStamp(): " + value);
		return value;
	}
	
	public static String getJulianTimeStamp(int length) {
		String timeStamp = getJulianTimeStamp();
		String value = timeStamp.substring(timeStamp.length()-length);
		log.debug("getJulianTimeStamp(" + length + "): " + value);
		return value;
	}
	
	
	public static String getCgroupCode() {
		DateTime now = DateTime.now(); 
		return String.valueOf(now.getSecondOfDay()).substring(1);
	}
	
	public static String getexggrpcode() {
		String value = getJulianTimeStamp();
		return "Y"+ value.substring(value.length()-2);

	}
	public static String getcommissiongrpcode() {
		String value = getJulianTimeStamp();
		return "Al"+ value.substring(value.length()-2);

	}
	
	public static String PeriodCode() {
		String value = getJulianTimeStamp();
		return "PC"+value.substring(value.length()-3);
	}
	
	public static String IndicesName() {
		String value = getJulianTimeStamp();
		return "IndicesName"+value.substring(value.length()-4);
	}
	public static String FundID() {
		String value = getJulianTimeStamp();
		return "F"+value.substring(value.length()-4);
	}

	public static String getuniquealpha() {
		return RandomUtils.generateRandomAlpha(true);
	}


	
	public static String generateRandomId() {
	 return "MF"+RandomStringUtils.randomNumeric(4);
	  }
	public static String fundpromoterexternalId() {
		 return "FPExt"+RandomStringUtils.randomNumeric(4);
		  }
	
	public static String legalEntityexternalId() {
		 return "TFC"+RandomStringUtils.randomNumeric(3);
		  }
	public static String getMonthEndDate() {
		return DateUtils.format(LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(1).toString(), "dd-MMM-yyyy");
	}
	public static String getMonthEndDateMinus() {
		return DateUtils.format(LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(5).toString(), "dd-MMM-yyyy");
	}
	public static String getaddress() {
		return System.getProperty("user.name") + "_" + generateRandomId();
	}
	public static String getelectronicaddress() {
		 return "Eaddress"+RandomStringUtils.randomNumeric(5);
		  }
	

	public static String TransformData(String key) {
		
		String value = key;
		switch (key) {


			case "$yesterday$":		value = DateUtils.format(LocalDate.now().minusDays(1).toString(), "dd-MM-yyyy"); break;		
			case "$tomorrow$":		value = DateUtils.format(LocalDate.now().plusDays(1).toString(), "dd-MM-yyyy"); break;		
			case "$today$":			value = DateUtils.format(LocalDate.now().toString(), "dd-MM-yyyy"); break;		
			case "$monthendminus$":	value = getMonthEndDateMinus(); break;		
			case "$monthend$":		value = getMonthEndDate(); break;		
			case "$generateRandomId$":	value = generateRandomId(); break;	
			case "$fundpromoterexternalId$":	value = fundpromoterexternalId(); break;	
			case "$legalEntityexternalId$":	value = legalEntityexternalId(); break;
			case "$getUniqueId$":	    value = getJulianTimeStamp(); break;
			case "$get5digitCode$": 	value = getJulianTimeStamp(5); break;

			
			/* depricated			
			case "$get8digitCode$": 	value = getJulianTimeStamp(8); break;
			case "$get9digitCode$": 	value = getJulianTimeStamp(9); break;
			case "$sixdigitcode$":		value = getJulianTimeStamp(6); break;
			case "$get5digitCode$": 	value = getJulianTimeStamp(5); break;
			case "$get4digitCode$": 	value = getJulianTimeStamp(4); break;
			case "$get3digitcode$":		value = getJulianTimeStamp(3);break;
			 */
			
			case "$getexggrpcode$":		value = getexggrpcode();break;
			case "$getCgroupCode$":		value = getCgroupCode();break;
			case "$getuniquealpha$":		value = getuniquealpha();break;
			case "$getaddress$":		value = getaddress();break;
			case "$getelectronicaddress$":		value = getelectronicaddress();break;
			case "$FromDate$": value = DateUtils.format(LocalDate.now().withDayOfMonth(1).toString(), "dd-MMM-yyyy"); break;
			case "$ToDate$": value = DateUtils.format(LocalDate.now().plusMonths(3).withDayOfMonth(1).minusDays(1).toString(), "dd-MMM-yyyy"); break;
			case "$TheoreticalDate$": value = DateUtils.format(LocalDate.now().plusMonths(6).withDayOfMonth(1).minusDays(1).toString(), "dd-MMM-yyyy"); break;
			

			
			
		}
		// return
		return value;
	}
	
	public static Boolean isJson(String data) {
		// keep it simple
		return data.trim().startsWith("{");
		/*try {
		 
			Gson gson = new GsonBuilder().create();
			gson.fromJson(data, this.getClass());
			return true;
		} catch (Exception e) {
			return false;
		} */
	}
	public static Boolean isXml(String data) {
		// keep it simple
		return data.trim().startsWith("<");
		/*try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(data));
			Document xml = builder.parse(source);
			return true;
		} catch (Exception e) {
			return false;
		}*/
	}
	
	public static Document getXmlDocument(String xmlString) {

	    try {		
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(xmlString));
			return builder.parse(source);
		} catch (Exception e) { /* ignore */}
	    
	    return null;
	}
}
