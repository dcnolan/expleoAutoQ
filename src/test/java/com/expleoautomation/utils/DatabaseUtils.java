package com.expleoautomation.utils;



import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.expleoautomation.commons.Behaviour;
import com.expleoautomation.commons.ConstantsProvider;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseUtils {

	public static final int numericKey = 0;
	public static final int alphaKey = 1;
	public static final int alphaNumericKey = 3;


	public static Map<String, Connection> connectionPool = new HashMap<String, Connection>();

	private static Connection GetConnection() {
		String uid = ConstantsProvider.getDB_UID();
		String pwd = Encryption.decrypt(ConstantsProvider.getDB_PWD());
		return GetConnection(uid, pwd);
	}

	private static Connection GetConnection(String uid, String pwd) {

		// get config settings
		String con = ConstantsProvider.getDB_CONNECTION();
		String connectionKey = con + "; " + uid;

		Connection dbConn = null;

		// check cache
		if (connectionPool.containsKey(connectionKey)) {
			dbConn = connectionPool.get(connectionKey);
		} else {
			log.debug("DB connection: " + con + ", UID: " + uid);
				
			try {
				// open connection
				dbConn = DriverManager.getConnection(con, uid, pwd);

				// store in cache
				if (Behaviour.connectionPooling) {
					connectionPool.put(connectionKey, dbConn);
				}
			} catch (SQLException e) {
				Assert.fail("GetConnection(): Failed to get DB connection" + System.lineSeparator() + e.getMessage());
			}
		}

		return dbConn;
	}

	public static ResultSet getResultSet(String query) {
		return getResultSet(query, false);
	}

	public static void close(ResultSet dbRs) {
		try {
			if (dbRs != null) {
				dbRs.close();
			}
		} catch (SQLException e) {
			Assert.fail("close(): FAILED to close ResultSet");
		}
	}

	public static ResultSet getResultSet(String query, boolean display) {

		// connect
		Connection dbConn = GetConnection();


		// execute
		ResultSet dbRs = null;
		Statement dbStmnt = null;
		ResultSetMetaData dbRsmd = null;

		try {
			log.debug(query);
			dbStmnt = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			dbRs = dbStmnt.executeQuery(query);
			dbRsmd = dbRs.getMetaData();
			
			// loop rows
			if (display) {
				while (dbRs.next()) {

					// loop columns
					log.info("Row: " + String.valueOf(dbRs.getRow()) + ", " + dbRsmd.getColumnCount() + "columns");

					for (int index = 1; index <= dbRsmd.getColumnCount(); index++) {
						switch (dbRsmd.getColumnType(index)) {
						case Types.NUMERIC:
							log.info("Column " + String.valueOf(index) + ": " + dbRsmd.getColumnName(index) + " : "
									+ dbRs.getLong(index));
							break;
						case Types.VARCHAR:
							log.info("Column " + String.valueOf(index) + ": " + dbRsmd.getColumnName(index) + " : "
									+ dbRs.getString(index));
							break;
						case Types.TIMESTAMP:
							log.info("Column " + String.valueOf(index) + ": " + dbRsmd.getColumnName(index) + " : "
									+ dbRs.getTimestamp(index));
							break;
						case Types.ARRAY:
							String[] data = (String[]) dbRs.getArray(index).getArray();
							log.info("Column " + String.valueOf(index) + ": " + dbRsmd.getColumnName(index) + "["
									+ data.length + "]");
							for (int i = 0; i < data.length; i++) {
								log.info("   " + data[i]);
								break;
							}
							break;

						default:
							log.error("Column " + String.valueOf(index) + ": " + dbRsmd.getColumnName(index)
									+ " : Unknown Type (" + dbRsmd.getColumnType(index) + ")");
							break;
						}
					}
				}
				// back to start
				dbRs.first();
			}

		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		} finally {

			if (!Behaviour.connectionPooling) {
				log.info("Closing db Connection");
				try {
					dbConn.close();
				} catch (SQLException e) {
					/* ignore */ }
			}
		}

		// return

		return dbRs;
	}

	public static int select(String query) {
		return select(query, false);
	}

	public static int select(String query, boolean display) {

		log.info("Executing SQL query " + query);

		ResultSet dbRs = getResultSet(query, display);
		int rowCount = rowCount(dbRs);
		close(dbRs);
		return rowCount;
	}

	public static int rowCount(ResultSet dbRs) {

		Integer rowCount = 0;
		try {
			dbRs.last();
			rowCount = dbRs.getRow();
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}

		// return
		log.debug("rowCount(): " + String.valueOf(rowCount));
		return rowCount;
	}

	public static String getString(String sql, String name) {
		ResultSet dbRs = getResultSet(sql);
		try {
			dbRs.next();
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		String value = getString(dbRs, name);
		close (dbRs);
		return value;
	}
	public static String getString(ResultSet dbRs, String name) {

		String value = null;
		try {
			value = dbRs.getString(name);
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}

		// return
		return value;
	}
	public static Date getDateTime(String sql, String name) {
		ResultSet dbRs = getResultSet(sql);
		try {
			dbRs.next();
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		Date value = getDateTime(dbRs, name);
		close (dbRs);
		return value;
	}
	public static Date getDateTime(ResultSet dbRs, String name) {

		Date value = null;
		try {
			value = dbRs.getDate(name);
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}

		// return
		return value;
	}
	

	public static String getUniqueKey(String resource, int codeLength, String keyFieldName, int keyType) {
		Map<String, String> keyList = new HashMap<String, String>() {
			{
				put(keyFieldName, "");
			}
		};
		return getUniqueKey(resource, codeLength, keyList, keyType);
	}

	public static String getUniqueKey(String resource, int codeLength, Map<String,String> keyList, int keyType) {

		StopWatch timer = new StopWatch();
		timer.start();

		log.debug("getUniqueId(): start");
		
		// find wanted key field (missing value)
		String keyField = null;
		for (String key : keyList.keySet()) {
			if (keyList.get(key) == "") {
				keyList.put(key, "{KEY}");
				keyField =key;
				break;
			}
		}
		
		// add where clause
		StringBuilder whereClase = new StringBuilder();
		for (String key : keyList.keySet()) {
			whereClase.append(whereClase.length() == 0 ? " where " : " and ");
			whereClase.append(ResourceMap.getDbFieldFromJson(resource, key)).append(" = '").append(keyList.get(key)).append("'");
		}

		// build base query
		String sqlMask = new StringBuilder("select 1 from ").append(ResourceMap.getDbTable(resource)).append(whereClase.toString()).toString();

		// check list
		log.debug("getUniqueId(): [" + timer.toString() + "] find new key");		
		String sql = null;
		String pk = null;
		do {
			switch (keyType) {
				case alphaKey 	: pk = RandomStringUtils.randomAlphabetic(codeLength); break;
				case numericKey : pk = RandomStringUtils.randomNumeric(codeLength); break;
				default			: pk = RandomStringUtils.randomAlphanumeric(codeLength); break;
			}
			sql = sqlMask.replace("{KEY}", pk);

		} while (DatabaseUtils.select(sql)>0 && !isTimeout(timer)); 
		
		
		if (isTimeout(timer)) {
			Assert.fail("getUniqueId(): Failed to get unique key within timeout period");
		} else {
			log.info("getUniqueId(): [" + timer.toString() + "]");
		}

		// return
		return pk;
	}
	
	public static String getUniqueKey_fundId(String resource, int codeLength, String keyFieldName, int keyType, String currency) {

		StopWatch timer = new StopWatch();
		timer.start();

		log.debug("getUniqueKey_fundId(): start");

		// build base query
		String sqlMask = "select NPTF from GTA.TA_FUND_PARAM where NPTF = concat('{KEY}', '" + currency + "')";
				sqlMask += " UNION select NPTF from GTA.TA_ASSET_FUND_PARAM where NPTF = concat('{KEY}', '" + currency + "')";
				sqlMask += " UNION select INTERNAL_ID from gta.api_reference where ENTITY_TYPE = '0004' and INTERNAL_ID = '{KEY}'";

		// check list
		log.debug("getUniqueKey_fundId(): [" + timer.toString() + "] find new key");		
		String sql = null;
		String pk = null;
		do {
			pk = getUniqueKey(resource, codeLength, keyFieldName,  keyType);
			sql = sqlMask.replace("{KEY}", pk);
		} while (DatabaseUtils.select(sql)>0 && !isTimeout(timer)); 
		
		if (isTimeout(timer)) {
			Assert.fail("getUniqueKey_fundId(): Failed to get unique key within timeout period");
		} else {
			log.info("getUniqueKey_fundId(): [" + timer.toString() + "]");
		}

		// return
		return pk;
	}	
	
	
	// append control fields
	public static void assertControlFields(ResultSet dbRs) {
		String uid = ConstantsProvider.getAPI_UID();
		Map<String, String> controlFields = new HashMap<String, String>() {
		{
				put("CREATED_BY", uid);
				put("MAKER", uid);
				put("INIT_MAKER", uid);
				put("AUD_CREATED_BY", uid);
				put("DCREATED", LocalDate.now().toString());
				put("MAKE_DATE", LocalDate.now().toString());
				put("INIT_MAKE_DATE", LocalDate.now().toString());
				put("AUD_DCREATED", LocalDate.now().toString());
			}
		};
		for (String columnName : controlFields.keySet()) {
			if (controlFields.containsKey(columnName)) {
				try {
					if (controlFields.get(columnName) != dbRs.getString(columnName)) {
						log.warn(columnName);
					}
				} catch (SQLException e) { /* ignore */ }
			}
		}
	}
	



	
	private static boolean isTimeout(StopWatch timeout) {
		return timeout.getTime() > 60 * 1000 * 2; // 5s
	}
	
	@Test
	public void dbConnectionTest() {
		Connection conn = GetConnection("browser", "browser");
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
