package com.expleoautomation.commons;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import com.expleoautomation.utils.ApiUtils;
import com.expleoautomation.utils.DatabaseUtils;
import com.expleoautomation.utils.Excel;
import com.expleoautomation.utils.ResourceMap;
import com.expleoautomation.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

@Log4j2
public
class ApiBase extends reflectionBase {

	
	// CLEAR default data
	public void clear() {
		clear("resource", "table");
	}

	public void clear(String... exceptionArray) {
		List<String> exceptionList = Arrays.asList(exceptionArray);
		for (Field field : this.getClass().getFields()) {
			String name = field.getName();
			if (!exceptionList.contains(name)) {
				setPropertyByName(field.getName(), "");
			}
		}
	}

	// execute POST api method
	public String post() {

		// execute POST request
		Response result = ApiUtils.post(getResource(), toJson(true));

		// re-create this object from response data - including unique record keys (for possible delete later)
		fromResponse(result);

		// add key values to report
		buildKeyValueReport();
		
		// status
		return result.getStatusLine();
	}
	
	public void buildKeyValueReport() {
		// add key values to report
		String className = this.getClassName();
		for (Field field : this.getClass().getFields()) {
			String fieldName = field.getName();
			String propertyName = className + "." + fieldName;
			if (ResourceMap.keyValueReportData.contains(propertyName)) {
				String propertyValue = getPropertyByName(field.getName());
				StaticData.keyValueReport.put(propertyName, propertyValue);
				log.debug("buildKeyValueReport(): " + propertyName + " = " + propertyValue);
			}
		}
	}
	
	// execute PUT api method
	public String put() {

		// execute POST request
		Response result = ApiUtils.put(getResource(), toJson(true));

		// re-create this object from response data - including unique record keys (for
		// possible delete later)
		fromResponse(result);

		// status
		return result.getStatusLine();
	}

	
	// execute GET api method
	public String get() {
		// untested
		Response result = ApiUtils.get(getResource());
		return result.getStatusLine();
	}
	// get SWAGGER doc
	public void swagger() {
		String apiName = getApiName();
		String swaggerUrl = "meta/swaggers/multifonds-gi-" + apiName + "-screen-swagger-v1.0.0.json";
		ApiUtils.get(swaggerUrl);
	}

	
	
	// execute DELETE api method
	public String deleteById(String id) {
		return deleteByResource(getResource() + "/" + id);
	}
	public String deleteByResource(String resource) {
		return deleteByResource(resource, false);
	}
	public String deleteByResource(String resource, boolean includeBodyWithRequest) {	
		// delete
		String body = (includeBodyWithRequest ? toJson(true) : "");
		Response result = ApiUtils.delete(resource, body);
		// check DB
		int rowCount = DatabaseUtils.select(this.getSelectSQL());
		String msg = "AssertDelete(): API data has NOT been removed form the database";
		if (Behaviour.assertFailure) {
			Assert.assertEquals(msg, 0, rowCount);
		} else if (rowCount != 0) {
			log.error(msg);
		}
		// return
		return result.getStatusLine();
	}

	
	
	
	
	
	
	
	
	// searalize SUB-CLASS to JSON string
	public String toJson() {
		return toJson(false);
	}
	public String toJson(boolean removeSystemProperties) {
		Gson gson = new GsonBuilder().create();
		JSONObject jsonObj = new JSONObject(gson.toJson(this));
		if (removeSystemProperties) {
			jsonObj.remove("resource");
			
			// special case for FundPaRorParameters that extends parent object to get access to SQL resource (FundPaRorParameters extends FundPaRateOfReturnParameters)
			String listPropertyName = getListProperty();
			if (listPropertyName != null) {
				JSONArray jsonArray = jsonObj.getJSONArray(listPropertyName);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject nestedJsonObj = jsonArray.getJSONObject(index);
					nestedJsonObj.remove("resource");
					nestedJsonObj.remove(listPropertyName);
				}
			}
		}
		return jsonObj.toString();
	}

	
	// de-searalize SUB-CLASS from JSON string
	public void fromJson(String json) {
		
		if (json == null) {
			Assert.fail("fromJson(): json string is null");
		}

		// create new object from json
		Gson gson = new GsonBuilder().create();
		Object me = gson.fromJson(json, this.getClass());

		// clone to self
		for (Field field : me.getClass().getFields()) {
			String name = field.getName();
			// get property object
			Object obj = null;
			try {
				obj = field.get(me);
			} catch (Exception e) {
				log.error("Failed to get value for property '" + name + "'" + System.lineSeparator() + e.getMessage());
			}
			if (obj == null) {
				continue;
			} else {
				setPropertyByName(name, obj);
			}
		}
	}



	// de-searalize SUB-CLASS from JSON string found in API response
	public void fromResponse(Response response) {
		
		// ignore if XML response
		String responseData = response.asString();
		if (StringUtils.isXml(responseData)) { return; }

		// check sucess/failure
		JSONObject jsonObj = new JSONObject(responseData);
		if (jsonObj == null) { return; };
		if (jsonObj.getString("status").equals("ERROR")) { return; };
		
		// get json body
		jsonObj = jsonObj.getJSONObject("data");

		// extract json string
		String json = jsonObj.toString();

		// set property values
		fromJson(json);
	}
	
	

	
	// populate SUB-CLASS from XLS datasheet
	public void fromXls(String reference) {


		// use reference & className to identify workbook, sheet & range of json property names
		String xlsWorkbook = ResourceMap.getXlsWorkbook(reference);
		String[] xlsWorksheetRange = ResourceMap.getXlsWorksheetRangeForApi(getClassName()).split("[!]");
		String xlsWorksheet = xlsWorksheetRange[0];
		String rangeJsonPropertyNames = xlsWorksheetRange[1];

		// open excel
		Excel xls = new Excel();
		xls.openWorkbook(xlsWorkbook);
		xls.openWorksheet(xlsWorksheet);

		// get map of properties & values
		Map<String, String> jsonData = xls.getDataMap(rangeJsonPropertyNames, reference);
		xls.close();
		
		// populate this object
		for (Map.Entry<String, String> jsonProperty : jsonData.entrySet()) {
			// see webBase.setPropertyByName() for matching algorithm
			setPropertyByName(jsonProperty.getKey(), jsonProperty.getValue());
		}
	}

	
	// populate SUB-CLASS from CUCUMBER data-table
	public void fromDataTable(io.cucumber.datatable.DataTable dataTable) {
		fromDataTable(dataTable, this);
	}
	public void fromDataTable(io.cucumber.datatable.DataTable dataTable, Object obj) {
		log.info(System.lineSeparator());
		log.info("fromDataTable()");
		
		
		// convert to a key-value pair map/list
		Map<String, String> map = null;
		try {
			map = dataTable.asMap();
		} catch (Exception e) {
			Assert.fail("fromDataTable(): " + e.getMessage());
		}

		// loop through keys
		for (String key : map.keySet()) {
			// get value
			String value = map.get(key);
			if (value != null) {
				// set property value
				value = StringUtils.TransformData(value);
				log.info("   setting " + key + " = " + value);
				setPropertyByName(key, value, obj);
			}
		}
	}

	
	



	// build SQL using SUB-CLASS properties & values to VERIFY the existence of API data in the DATABASE
	public String getSelectSQL() {
		return getSelectSQL(this);
	}
	public String getSelectSQL(Object objItem) {
		String whereClause = buildWhereClause(objItem);
		if (whereClause.length()==0) {
			String msg = "getSelectSQL(): Failed to generate sql WHERE clause. Hint - try passing a child object!";
			log.error(msg);
			Assert.fail(msg);
		}
		String query = "select * from " + ResourceMap.getDbTable(getResource()) + " where " + whereClause;

		// return query
		log.info("getSelectSQL(): " + query);
		return query;
	}

	private String buildWhereClause(Object objItem) {

		// list of clauses
		List<String> query =  new ArrayList<String>();

		// loop properties
		Field[] fields = objItem.getClass().getFields();
		for (Field field : fields) {

			// get property name
			String name = field.getName();
			// get property object
			Object obj = null;
			try {
				obj = field.get(objItem);
			} catch (Exception e) {
				log.error("Failed to get value for property '" + name + "'" + System.lineSeparator() + e.getMessage());
			}
			if (obj == null) {
				continue;
			}

			// get corresponding DB field
			if (obj.getClass().getTypeName().equals("java.lang.String")) {
				String dbField = ResourceMap.getDbFieldFromJson(getResource(), name);
				if (dbField == null) {
					continue;
				}
				// append field
				String[] dbFieldSplits = dbField.split("[.]"); // regex for single .
				String value = obj.toString();
				if(!value.isEmpty()) {
					query.add(dbFieldSplits[dbFieldSplits.length-1] + " = '" + value + "'");
				} 

			// recursive class
			} else {
				log.debug("recursive call to " + objItem.getClass().getName() + " -> " + obj.getClass().getName());
				String nestedWhereClause = buildWhereClause(obj).trim();
				if (!nestedWhereClause.isEmpty() ) {
					query.add(nestedWhereClause);
				}
			}
		}

		// return
		String returnString = String.join(" and ", query); 
		return returnString;
	}
	
	
	
	// using the SUB-CLASS properties, try and FIND a matching entity in the DATABASE
	public boolean findFirst() {
		
		// build WHERE clause using current properties
		String whereClause = buildWhereClause(this);
		
		// append clause to ensure stale data is omitted (data created prior to latest patch) 
		if (Behaviour.checkForStaleData) {
			whereClause += " and DCREATED > (select DCREATED from (select DCREATED from hsm.patch ORDER BY DCREATED DESC) where ROWNUM = 1)";
		}

		// find most recent matching record (older records may be in incorrect format)		
		String sql = "select * from (select * from " + ResourceMap.getDbTable(getResource()) + " where " + whereClause + " order by DCREATED desc) where ROWNUM = 1";
		
		// execute query
		log.info("findFirst(): " + sql);
		ResultSet dbRs = DatabaseUtils.getResultSet(sql);
		
		// populate
		int rowCount = DatabaseUtils.rowCount(dbRs);
		log.info("findFirst(): rowCount = " + rowCount);
		if (rowCount == 1) {
			fromResultSet(dbRs);
			buildKeyValueReport();
		}
		
		// close resultset
		DatabaseUtils.close(dbRs);
		
		// return
		return (rowCount == 1);		
	}
	
	// populate SUB-CLASS from SQL result-set
	public void fromResultSet(ResultSet dbRs) {

		// move to 1st record
		try {
			dbRs.first();
		} catch (SQLException e1) {
			Assert.fail("fromResultSet(): ZERO records returned");
		}

		// loop through object properties
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			// get DB field matching obect property
			String propertyName = field.getName();
			String dbField = ResourceMap.getDbFieldFromJson(getResource(), propertyName);
			if (dbField == null) {
				log.debug("   " + propertyName + " -> ?");
				continue;
			}
			dbField = dbField.split("[.]")[2];

			// get dbField value
			String value = null;
			try {
				value = dbRs.getString(dbField);
			} catch (SQLException e) {
				log.debug("   " + propertyName + " (" + dbField + ") : " + e.getMessage());
				continue;
			}

			// set property value
			log.debug("   " + propertyName + " (" + dbField + ") = " + value);
			setPropertyByName(propertyName, value);

		}
	}
	
	


	// GET property by name from the SUB-CLASS using reflection
	public String getResource() {
		return getPropertyByName("resource");
	}
	public String getApiName() {
		String resource = getResource();
		return resource.substring(resource.lastIndexOf('/') + 1);
	}

	
}
