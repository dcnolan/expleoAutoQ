package com.expleoautomation.utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import com.expleoautomation.commons.Behaviour;


public class ResourceMap {

	private static String client = null;
	
	
	public static List<String> keyValueReportData = Arrays.asList(
			"Item.itemId",
			"Order.orderId"
	);


	
	public static boolean isExcelReference(String xlsRef) {
		if (xlsRef == null) {
			return false;
		} else {
			boolean assertFailure = Behaviour.assertFailure;
			Behaviour.assertFailure=false;
			String xlsWorkbook = getXlsWorkbook(xlsRef);
			Behaviour.assertFailure=assertFailure;
			return (xlsWorkbook != null);
		}
	}
		
	public static String getXlsWorkbook(String reference) {
		// identify client
		if (reference.startsWith("employee")) {
			client = "Sample";
		}
		
		else {
			if (Behaviour.assertFailure) {
				Assert.fail("getXlsWorkbook(): Failed to identify client from reference '" + reference + "'");
			} else {
				return null;
			}
		}
		
		// identify workbook
		String name = xlsWorkbookMap.get(client);
		if (name == null) {
			if (Behaviour.assertFailure) {
				Assert.fail("getXlsWorkbook(): Failed to identify Excel workbook from reference '" + reference + "'. Check the data in ResourceMap!");
			} else {
				return null;
			}
		}
		
		// check file exists
		String filePath = "resources\\xlsData\\" + name;
		File f = new File(filePath);
		if(!f.exists()) { 
			if (Behaviour.assertFailure) {
				Assert.fail("getXlsWorkbook(): Excel workbook cannot be found '" + filePath + "'");
			} else {
				return null;
			}
		}
		
		// return
		return filePath;
	}
	
	private static Map<String, String> xlsWorkbookMap = new HashMap<String, String>() {
	{
			// sample
			put("Sample", "Sample.xlsx");
			
			
			// ...more data here... //
		}
	};
	
	public static String getXlsWorksheetRangeForApi(String className) {
		
		String name = xlsWorksheetMapForApi.get(client + "¬" + className);
		if (name==null) {			
			// try wildcard for GL
			name = xlsWorksheetMapForApi.get("*¬" + className);
		
			// failed
			if (name==null) {			
				Assert.fail("getXlsWorksheetRangeForApi(): Failed to identify worksheet for class '" + className + "'. Check the data in ResourceMap!"); 
			} 
		}
		return name;
	}
	
	
	
	
	private static Map<String, String> xlsWorksheetMapForApi = new HashMap<String, String>() {
		{
			// sample
			put("Sample¬Employee", "Sample.Employees!B4:B6");

			
			
			
			
			
		}
	};
	
	
	public static String getXlsWorksheetRangeForWeb(String className) {
		
		String name = xlsWorksheetMapForWeb.get(client + "¬" + className);
		
		// failed
		if (name==null) {			
			Assert.fail("getXlsWorksheetRangeForWeb(): Failed to identify worksheet for class '" + className + "'. Check the data in ResourceMap!"); 
		} 
		return name;
	}
	private static Map<String, String> xlsWorksheetMapForWeb = new HashMap<String, String>() {
		{		
			put("Sample¬Item", "Sample.Items!B4:B7");

	}
	};
	
	
	
	
	
	
	public static String getDbTable(String resource) {
		String dbTable = tableToResourceMap.get(resource);
		if (dbTable == null) {
			Assert.fail("getDbTable(): Failed to identify DB table for resource '" + resource + "'");
		} 
		return dbTable;
	}
	
	public static String getDbFieldFromJson(String resource, String jsonField) {
		String dbTable = getDbTable(resource);
		String key = dbTable + "." + jsonField;
		return jsonToColumnMap.get(key);
	}

	// data scraped from apis-ITRGI580-20211203.xls
	private static Map<String, String> tableToResourceMap = new HashMap<String, String>() {
	{
		put("employees", "LIST_EMPLOYEES");
		put("create", "LIST_EMPLOYEES");
			
		}
	};

	public static Map<String, String> jsonToColumnMap = new HashMap<String, String>() {
		{
			put("LIST_EMPLOYEES.id",				"LIST_EMPLOYEES.ID");
			put("LIST_EMPLOYEES.employee_name",		"LIST_EMPLOYEES.NAME");
			put("LIST_EMPLOYEES.employee_salary",	"LIST_EMPLOYEES.SALARY");
			put("LIST_EMPLOYEES.employee_age",		"LIST_EMPLOYEES.AGE");
			put("LIST_EMPLOYEES.profile_image", 	"LIST_EMPLOYEES.PROFILE_PIC");
		    

		}
	};

}
