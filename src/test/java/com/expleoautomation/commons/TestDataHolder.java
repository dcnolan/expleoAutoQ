package com.expleoautomation.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Keeps track of test data throughout the test session. This has two main uses:
 * the first is to share the data between the steps and the other is to make
 * test data relevant for each test scenario available for the test report.
 *
 * @author youssef.abdalla
 */
@Log4j2
public class TestDataHolder {

	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String ACCESS_TOKEN_EXPIRY = "ACCESS_TOKEN_EXPIRY";
	public static final String RESPONSE_STATUS_CODE = "RESPONSE_STATUS_CODE";
	public static final String RESPONSE_STATUS = "RESPONSE_STATUS";
	public static final String RESPONSE_BODY = "RESPONSE_BODY";
	public static final String RESPONSE = "RESPONSE";
	public static final String RESOURCE = "PREVIOUS_RESOURCE";
	public static final String RECORD_ID = "RECORD_ID";
	public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
	
	
	
	public static final String CASE_ID = "CASE_ID";
	public static final String NON_FARM_CASE_ID = "NON_FARM_CASE_ID";
	public static final String BASE_URL = "BASE_URL";
	public static final String API_PARAMETER = "API_PARAMETER";
	public static final String ENVIRONMENT = "ENVIRONMENT";
	public static final String NUM_OF_ITEMS = "NUM_OF_ITEMS";
	public static final String ITEM_ID = "ITEM_ID";
	public static final String ORDER_ID = "ORDER_ID";
	public static final String SUSPECT_REPORT_ID = "SUSPECT_REPORT_ID";
	public static final String SUSPECT_PREMISES_ID = "SUSPECT_PREMISES_ID";
	public static final String HOLDING_ID = "HOLDING_ID";
	public static final String VERSION = "VERSION";
	public static final String VISIT_ID = "VISIT_ID";
	public static final String KEY_VALUE = "KEY_VALUE";
	public static final String PARCEL_ID = "PARCEL_ID";
	public static final String PARCEL_LABEL = "PARCEL_LABEL";
	public static final String CONTRACT_FILTERS = "CONTRACT_FILTERS";
	public static final String AAP_FILTERS = "AAP_FILTERS";
	public static final String PARCEL_LIST = "PARCEL_LIST";
	public static final String TARGET_URL = "TARGET_URL";
	public static final String REQUEST_BODY = "REQUEST_BODY";
	public static final String PROJECT_SUB_FEATURE_ID = "PROJECT_SUB_FEATURE_ID";
	public static final String PROJECT_PARCEL_ID = "PROJECT_PARCEL_ID";
	public static final String SUB_FEATURE_LABEL = "SUB_FEATURE_LABEL";
	public static final String SUB_FEATURE_ID = "SUB_FEATURE_ID";
	public static final String PROJECT_ID = "PROJECT_ID";
	public static final String REQUEST = "REQUEST";
	public static final String EXPECTED_PARCEL_ID = "EXPECTED_PARCEL_ID";
	public static final String EXPECTED_PARCEL_LABEL = "EXPECTED_PARCEL_LABEL";
	
	public static final String NUMBER_OF_RECORDS_BEFORE_CALL = "NUMBER_OF_RECORDS_BEFORE_CALL";
	public static final String NUMBER_OF_RECORDS_AFTER_CALL = "NUMBER_OF_RECORDS_AFTER_CALL";
	public static final String ZEPHYR_TEST_ID = "ZEPHYR_TEST_ID";
	public static final String JIRA_TASK_TYPE = "JIRA_TASK_TYPE";
	public static final String JIRA_SUB_TASK_TYPE = "JIRA_SUB_TASK_TYPE";
	public static final String JIRA_TASK_KEY = "JIRA_TASK_KEY";
	public static final String JIRA_TASK_SUMMARY = "JIRA_TASK_SUMMARY";
	public static final String JIRA_TASK_DESCRIPTION = "JIRA_TASK_DESCRIPTION";
	public static final String JIRA_TASK_THIRD_PARTY_REFERENCE = "JIRA_TASK_THIRD_PARTY_REFERENCE";
	public static final String NUMBER_OF_PARCELS_IN_PRINT_QUEUE = "NUMBER_OF_PARCELS_IN_PRINT_QUEUE";
	public static final String SUPER_FEATURE_ID = "SUPER_FEATURE_ID";
	public static final String SECOND_SUPER_FEATURE_ID = "SECOND_SUPER_FEATURE_ID";
	public static final String SECOND_PARCEL_ID = "SECOND_PARCEL_ID";
	public static final String SECOND_PARCEL_LABEL = "SECOND_PARCEL_LABEL";
	public static final String SECOND_SUB_FEATURE_ID = "SECOND_SUB_FEATURE_ID";
	public static final String PARCEL_TOTAL_AREA = "PARCEL_TOTAL_AREA";
	public static final String PROJECTS_LABELS = "PROJECTS_LABELS";
	public static final String HISTORY_PARENT = "HISTORY_PARENT";
	public static final String BIF_ID = "BIF_ID_FROM_TABLE_TSLP_BPSINET_FEATURE_TYPE";
	public static final String BIP_ID = "BIP_ID_FROM_VIEW_VWLP_BPSINET_GEOM";
	public static final String BPS_INET_HASH_CODE = "BPS_INET_HASH_CODE";
	public static final String BPS_INET_MODE = "BPS_INET_MODE";
	public static final String FAD_AREA = "FAD_AREA";
	public static final String SUB_FEATURE_TYPE = "SUB_FEATURE_TYPE";
	public static final String SUB_FEATURE_DESC = "SUB_FEATURE_DESC";
	public static final String GPS_POINT_ID = "GPS_POINT_ID";
	public static final String NEIGHBOURING_PARCELS_NUMBER = "NEIGHBOURING_PARCELS_NUMBER";
	public static final String HERD_NUM_BUSINESS_ID_OR_CONTRACT_NUMBER = "HERD_NUM_BUSINESS_ID_OR_CONTRACT_NUMBER ";
	public static final String SECOND_HERD_NUM_BUSINESS_ID_OR_CONTRACT_NUMBER = "HERD_NUM_BUSINESS_ID_OR_CONTRACT_NUMBER";
	public static final String SCHEME_YEAR = "SCHEME YEAR";
	public static final String NUMBER_OF_NODES_IN_FEATURE_BEFORE_TEST = "NUMBER_OF_NODES_IN_FEATURE_BEFORE_TEST";
	public static final String NUMBER_OF_NODES_IN_FEATURE_AFTER_TEST = "NUMBER_OF_NODES_IN_FEATURE_AFTER_TEST";
	public static final String AVAILABLE_DIGITIZATION_QUERY_TYPES = "AVAILABLE_DIGITIZATION_QUERY_TYPES";
	public static final String NUMBER_OF_CREATED_JIRA_TASKS_IN_DATABASE = "NUMBER_OF_CREATED_JIRA_TASKS_IN_DATABASE";
	public static final String JIRA_CREATION_ROW_ID = "JIRA_CREATION_ROW_ID";
	public static final String PARTNERSHIP_HERD = "PARTNERSHIP_HERD";
	public static final String PARCEL_END_DATE = "PARCEL_END_DATE";
	public static final String SECOND_PARCEL_END_DATE = "SECOND_PARCEL_END_DATE";
	public static final String PARCEL_PROJECT_PAIRS = "PARCEL_PROJECT_PAIRS";
	public static final String QUERY_NOTE = "QUERY_NOTE";
	public static final String QUERY_TYPE = "QUERY_TYPE";
	private static Map<String, ArrayList<String>> testDataMapArry;
	@Getter	private static Map<String, String> testDataMap;

	public static String getTestDataRecord(String key) {

		// init
		if (testDataMap == null) {
			initDataMap();
		}

		// get key value
		String record = testDataMap.get(key);

		if (key.equals(ENVIRONMENT) && Strings.isNullOrEmpty(record)) {
			TestDataHolder.addTestDataRecord(TestDataHolder.ENVIRONMENT, EnvironmentHandler.getEnvironmentForTest(), true, true);
			record = testDataMap.get(key);
			log.debug("record: " + key + " = " + record);
		} else if (record == null) {
			log.debug("no record found for " + key + " . Returning empty string instead...");
			record = "";
		}

		// log & return
		return record;
	}

	private static void initDataMap() {
	    testDataMap = new HashMap<>();
	    testDataMapArry = new HashMap<>();
	}

	/**
	 * only adds record if the key is not found in #testDataMap or if the key is
	 * found but the value is null
	 *
	 * @param key   for the data record. preferably one of the static fields.
	 * @param value to be stored as String
	 */
	public static void addTestDataRecord(String key, String value, boolean resetValuesIfAlreadyRecorded, boolean printValueInLog) {
		// init
		if (testDataMap == null) {
			initDataMap();
		}
		
		// refresh
		if (resetValuesIfAlreadyRecorded) {
			testDataMap.remove(key);
		}
		
		// existing?
		final boolean isKeyRecordedBefore = testDataMap.containsKey(key);
		if (isKeyRecordedBefore) {
			if (!Strings.isNullOrEmpty(testDataMap.get(key))) {
				key = key + "_i";
			}
		}
		
		// log
		if (printValueInLog) {
			log.debug("recording the test data key: " + key + " with the value: " + value);
		}
		
		// set
		testDataMap.put(key, value);
		if (key.equals(TestDataHolder.ENVIRONMENT)) {
			TestEnvironmentDataHolder.addTestEnvironment(value.toUpperCase());
		}
	}

	public static void appendTestDataRecordWithPipe(String key, String value, boolean printValueInLog) {
		// init
		if (testDataMap == null) {
			initDataMap();
		}
		
		// existing?
		final boolean isKeyRecordedBefore = testDataMap.containsKey(key);
		if (isKeyRecordedBefore) {
			if (!Strings.isNullOrEmpty(testDataMap.get(key))) {
				key = key + "_i";
			}
		}
		
		// log
		if (printValueInLog) {
			log.debug("recording the test data key: " + key + " with the value: " + value);
		}

		// set
		String existingRecord = getTestDataRecord(key);
		testDataMap.put(key, value + "|" + existingRecord);
		if (key.equals(TestDataHolder.ENVIRONMENT)) {
			TestEnvironmentDataHolder.addTestEnvironment(value.toUpperCase());
		}
	}
	

	/**
	 * reset a specific key in the map
	 */
	public static void resetTestDataRecord(String key) {
		if (testDataMap.containsKey(key)) {
			testDataMap.remove(key);
		} 

	}

	public static void resetAllDataValues() {
		testDataMap = null;
	}

	public static List<String> getTestDataRecordWithKeyStartsWith(String keyPrefix) {
		
		// init
		if (testDataMap == null) {
			initDataMap();
		}
		log.debug("getting the values for the keys starting with" + keyPrefix);
		
		// find key(s)
		Set<String> keysStartsWithKeyPrefixSet = testDataMap.keySet().stream().filter(k -> k.startsWith(keyPrefix)).collect(Collectors.toSet());

		// return list of value-items in key sequence(DN?)
		return keysStartsWithKeyPrefixSet.stream().map(k -> {
			String v = testDataMap.get(k);
			if (v == null) {
				return "";
			}
			return v;
		}).collect(Collectors.toList());
	}

	public static void addTestDataArryRecord(String key, ArrayList<String> values, boolean resetValuesIfAlreadyRecorded, boolean printValueInLog) {
		
		// init
		if (testDataMapArry == null) {
			initDataMap();
		}
		
		// remove
		if (resetValuesIfAlreadyRecorded) {
			testDataMapArry.remove(key);
		}
		
		// get
		final boolean isKeyRecordedBefore = testDataMapArry.containsKey(key);
		if (isKeyRecordedBefore) {
			if (values.isEmpty()) {
				key = key + "_i";
			}
		}
		
		// log
		if (printValueInLog) {
			log.debug("recording the test data key: " + key + " with the value: " + values);
		}
		
		// set
		testDataMapArry.put(key, values);

	}

	public static ArrayList<String> getTestDataArryRecord(String key) {
		
		// init
		if (testDataMapArry == null) {
			initDataMap();
		}
		
		// get
		ArrayList<String> record = testDataMapArry.get(key);
		if (key.equals(ENVIRONMENT) && record.isEmpty()) {
			TestDataHolder.addTestDataRecord(TestDataHolder.ENVIRONMENT, EnvironmentHandler.getEnvironmentForTest(),
					true, true);
			record = testDataMapArry.get(key);
		} else if (record == null) {
			log.debug("no record found for " + key + " . Returning empty string instead...");
			record = null;
		}

		// log &return
		log.debug("record: " + key + " = " + record);
		return record;
	}

}
