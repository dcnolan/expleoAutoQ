package com.expleoautomation.commons;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;

import com.expleoautomation.utils.Excel;
import com.expleoautomation.utils.ResourceMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.extern.log4j.Log4j2;


@Log4j2
public
 class PomBase extends reflectionBase {


	// populate SUB-CLASS from XLS datasheet
	public void fromXls(String reference) {

		// use reference & className to identify workbook, sheet & range of json property names
		String xlsWorkbook = ResourceMap.getXlsWorkbook(reference);
		String[] xlsWorksheetRange = ResourceMap.getXlsWorksheetRangeForWeb(getClassName()).split("[!]");
		String xlsWorksheet = xlsWorksheetRange[0];
		String rangeUiPropertyNames = xlsWorksheetRange[1];
		
		// open excel
		Excel xls = new Excel();
		xls.openWorkbook(xlsWorkbook);
		xls.openWorksheet(xlsWorksheet);

		// get map of properties & values
		Map<String, String> jsonData = xls.getDataMap(rangeUiPropertyNames, reference);

		// populate this object
		for (Map.Entry<String, String> jsonProperty : jsonData.entrySet()) {
			setPropertyByName(jsonProperty.getKey(), jsonProperty.getValue());
		}

		// done
		xls.close();
	}

	
	protected void setPropertyByName(String propertyName, Object value) {

		// get list of properties (actually SET methods)
		Method[] methods = this.getClass().getMethods();
		
		// match property to set-method
		String methodName=matchObjectProperty(methods, propertyName);
		if (methodName == null) {
			log.warn("matchObjectProperty(): '" + propertyName + "' property value could not be matched to class '" + getClassName() + "'");
			return;
		} else {
			//log.debug("matchObjectProperty(): " + propertyName + " -> " + methodName);
		}
			
		// get SET method
		Method method = null;
		try {
			method = this.getClass().getMethod(methodName, String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			log.warn("setPropertyByName(): '" + propertyName + "' property value could not be matched to class '" + getClassName() + "'");
			return;
		}
				
		// execute invoke to SET property value
		try {
			log.info("   * setPropertyByName(): method.invoke before");
			method.invoke(this, value);
			log.info("   * setPropertyByName(): method.invoke after");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String msg = "setPropertyByName(): FAILED to set property value '" + getClassName() + "." + methodName + "' = '" + value + "' from Excel sheet";
			log.error("setPropertyByName(): Exception " + e.getMessage());
			Assert.fail(msg);
		}

	}
	
	private String matchObjectProperty(Method[] methods, String propertyName) {

		Map<String, String> methodNames = new HashMap<String, String>();
		for (Method method : methods) {
			methodNames.put(method.getName(), method.getName().toLowerCase().replace(" ", "").trim());
		}
		
		// NB using multiple loops because I don't want to find a partial match before I have checked all properties
		propertyName = propertyName.toLowerCase().replace(" ", "").replace("/", "").trim();

		// match on methodName.equals()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (method.getValue().equals(propertyName)) {
				return method.getKey();
			} 
		}

		// match on methodName.startsWith()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (method.getValue().startsWith(propertyName)) {				
				return method.getKey();
			} 
		}

		// match on methodName.endsWith()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (method.getValue().endsWith(propertyName)) {				
				return method.getKey();
			} 
		}

		// match on methodName.contains()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (method.getValue().contains(propertyName)) {				
				return method.getKey();
			} 
		}
		
		// match on propertyName.startsWith()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (propertyName.startsWith(method.getValue())) {				
				return method.getKey();
			} 
		}

		// match on propertyName.endsWith()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (propertyName.endsWith(method.getValue())) {				
				return method.getKey();
			} 
		}

		// match on propertyName.contains()
		for (Map.Entry<String, String> method : methodNames.entrySet()) {
			if (propertyName.contains(method.getValue())) {				
				return method.getKey();
			} 
		}

		// can't match
		return null;
	}

}
