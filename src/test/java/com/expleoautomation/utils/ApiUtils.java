package com.expleoautomation.utils;

import static io.restassured.RestAssured.given;
import java.util.ArrayList;
import org.apache.commons.lang.time.StopWatch;
import org.w3c.dom.Document;
import org.joda.time.DateTime;
import org.json.JSONObject;
import com.expleoautomation.commons.Behaviour;
import com.expleoautomation.commons.ConstantsProvider;
import com.expleoautomation.commons.TestDataHolder;
import com.expleoautomation.utils.ApiUtils.MyResponse.Detail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;


@Log4j2
public class ApiUtils {

	public static boolean assertSuccessfulResponse = false;
	

	

	/*********************************************************************************************
	 * Extracting the access token for accessing the APIs. This is for grant type
	 * "password" only
	 *********************************************************************************************/
	public static void authenticate() {
		if (!IsAuthenticated()) {

			// timer - start
			StopWatch timer = new StopWatch();
			timer.start();

			Response response = null;
			try {
				// POST request for access_token
				log.info("authenticate()");
				// WARNING  .log().all() will display uid & password
				response = given().auth().preemptive()
						.basic(ConstantsProvider.getCLIENTID(), Encryption.decrypt(ConstantsProvider.getCLIENTSECRET()))
						.formParam("grant_type", ConstantsProvider.getGRANT_TYPE())
						.formParam("username", ConstantsProvider.getAPI_UID())
						.formParam("password", Encryption.decrypt(ConstantsProvider.getAPI_PWD()))
						.formParam("scope", "openid").contentType(ContentType.URLENC)
						.header(new Header("X-OAUTH-IDENTITY-DOMAIN-NAME", ConstantsProvider.getAUTH_DOMAIN()))
						.log().uri()
						.when().post(ConstantsProvider.getACCESS_TOKEN_URL());

				// success?
				String accessToken = "";
				if (response.getStatusCode() == 200) {

					// read response
					JSONObject jsonObject = new JSONObject(response.getBody().asString());
					accessToken = jsonObject.get("access_token").toString();
					DateTime tokenCreatedAt = DateTime.now();
					DateTime tokenExpiresAt = tokenCreatedAt.plusSeconds(Integer.parseInt(jsonObject.get("expires_in").toString()));

					// store in test data holder
					TestDataHolder.addTestDataRecord(TestDataHolder.ACCESS_TOKEN, accessToken, true, false);
					TestDataHolder.addTestDataRecord(TestDataHolder.ACCESS_TOKEN_EXPIRY, tokenExpiresAt.toString(), true, false);

					// log
					log.debug("authenticate(): Sucessfully authenticated @ " + tokenCreatedAt.toString() + " [" + timer.toString() + "]");
				} else {
					String msg = "authenticate(): Failed to authenticated with response : " + response.statusLine() + System.lineSeparator() + response.asPrettyString();
					log.error(msg);
					Assert.fail(msg);
				}

			} catch (Exception e) {
				String msg = "authenticate(): Failed to authenticated with exception : " + e.getMessage();
				log.error(msg);
				Assert.fail(msg);
			} finally {
				timer.stop();
			}

		}

		// return
		log.debug("");
	}
	public static Boolean IsAuthenticated() {
		// get token data from static TestDataHolder class
		String accessToken = TestDataHolder.getTestDataRecord(TestDataHolder.ACCESS_TOKEN);
		String expiryDateString = TestDataHolder.getTestDataRecord(TestDataHolder.ACCESS_TOKEN_EXPIRY);
		DateTime tokenExpiresAt = DateTime.now().minusDays(1);

		// got token & expiry datestamp
		if (accessToken != "" && expiryDateString != "") {
			tokenExpiresAt = DateTime.parse(expiryDateString);
		}

		// expiring in less than 1 minute?
		if (tokenExpiresAt.isAfter(DateTime.now().plusMinutes(1).toInstant())) {
			log.debug("Authenticated until @ " + tokenExpiresAt.toString() + System.lineSeparator());
			return true;
		} else {
			// clear token
			TestDataHolder.addTestDataRecord(TestDataHolder.ACCESS_TOKEN, "", true, false);
			TestDataHolder.addTestDataRecord(TestDataHolder.ACCESS_TOKEN_EXPIRY, "", true, false);
			// return
			return false;
		}
	}
	
	

	public static Response put(String resource, String body)  {
		return request("PUT", resource, body);
	}
	public static Response post(String resource, String body)  {
		return request("POST", resource, body);
	}
	public static Response get(String resource)  {
		return request("GET", resource, "");
	}
	public static Response delete(String resource)  {
		return delete(resource, "");
	}
	public static Response delete(String resource, String body)  {
		return request("DELETE", resource, body);
	}


	public static Response request(String action, String resource, String body)  {
		// timer - start
		StopWatch timer = new StopWatch();
		timer.start();

		// action
		action = action.toUpperCase();

		// request
		RequestSpecification requestSpec = buildRequestSpecification(body);
		ResponseSpecification responseSpec = buildResponseSpecification(action);

		// request
		Response response = null;
		try {
			switch (action) {
				case "PUT":		response = requestSpec.when().put(resource).then().spec(responseSpec).extract().response();		break;
				case "POST":	response = requestSpec.when().post(resource).then().spec(responseSpec).extract().response();	break;
				case "GET":		response = requestSpec.when().get(resource).then().spec(responseSpec).extract().response();		break;
				case "DELETE":	response = requestSpec.when().delete(resource).then().spec(responseSpec).extract().response();	break;
			}
		} catch (Exception ex) {
			Assert.fail("request(): FAILED to execute api request with error - " + ex.getMessage());
		}
		
		// response
		String responseString = response.asPrettyString().replace(TestDataHolder.getTestDataRecord(TestDataHolder.ACCESS_TOKEN), "*************");
		log.debug(responseString);
		if (StringUtils.isJson(responseString)) {
			MyResponse responseObj = new ApiUtils().new MyResponse().fromJsonString(responseString);
			// success
			if (responseObj.header.status.message.equals("SUCCESS"))
			{
				TestDataHolder.addTestDataRecord(TestDataHolder.REQUEST_BODY, body, true, false);
				TestDataHolder.addTestDataRecord(TestDataHolder.RESPONSE, response.asString(), true, false);
				TestDataHolder.addTestDataRecord(TestDataHolder.RESPONSE_STATUS, response.statusLine(), true, false);
				TestDataHolder.addTestDataRecord(TestDataHolder.RESPONSE_STATUS_CODE, String.valueOf(response.statusCode()), true, false);
			
			// JSON error from API
			} else {		
			
				StringBuilder errorMessage = new StringBuilder("Failed to execute " + action.toUpperCase() + " call on API " + resource).append(System.lineSeparator());
				errorMessage.append(response.statusLine()).append(System.lineSeparator());
				errorMessage.append(responseObj.header.status.message).append(System.lineSeparator());
				errorMessage.append(responseObj.header.status.description).append(System.lineSeparator());
				for(Detail detail : responseObj.header.status.details)
				{
					if (detail.type.toUpperCase().equals("ERROR"))
					{
						errorMessage.append(detail.message).append(System.lineSeparator());
					}
				}
				TestDataHolder.addTestDataRecord(TestDataHolder.ERROR_MESSAGE, errorMessage.toString(), true, false);
				
				log.error(errorMessage.toString());
				if (Behaviour.assertFailure) {
					Assert.fail(errorMessage.toString());
				}
			}

		// XML error from server (404)
		} else {
			Document xmlResponse = StringUtils.getXmlDocument(responseString);

			StringBuilder errorMessage = new StringBuilder("Failed to execute " + action.toUpperCase() + " call on API " + resource).append(System.lineSeparator());
			errorMessage.append(response.statusLine()).append(System.lineSeparator());
			errorMessage.append(xmlResponse.getElementsByTagName("title").item(0).getTextContent().trim()).append(System.lineSeparator());
			errorMessage.append(xmlResponse.getElementsByTagName("p").item(0).getTextContent().trim()).append(System.lineSeparator());
			TestDataHolder.addTestDataRecord(TestDataHolder.ERROR_MESSAGE, errorMessage.toString(), true, false);

			log.error(errorMessage.toString());
			if (Behaviour.assertFailure) {
				Assert.fail(errorMessage.toString());
			}
		
		}
		// timer - stop
		timer.stop();

		// logging
		log.info(action + " request to '" + resource + "' with response '" + response.statusLine() + "' [" + timer.toString() + "]");

		// return
		return response;
	}
	

	public static RequestSpecification buildRequestSpecification(String body) {
		RestAssured.baseURI = ConstantsProvider.getBASE_URL();
		RequestSpecification requestSpec = RestAssured.given().auth().preemptive()
				.oauth2(TestDataHolder.getTestDataRecord("ACCESS_TOKEN")).contentType(ContentType.JSON).log().uri().log().body();
		
		// add body json string
		if (body.length() > 0) {
			requestSpec = requestSpec.given().body(body);
		}

		/*
		 * LOGGING *
		 * 
		 * String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		 * PrintStream printStream = new
		 * PrintStream(CommonUtils.getUserDirectory()+"/resources/logs/"+"APILogs"+
		 * timeStamp+".txt"); reqSpec = new
		 * RequestSpecBuilder().setBaseUri(TestDataHolder.getTestDataRecord("BASE_URL"))
		 * // .addFilter(RequestLoggingFilter.logRequestTo(printStream)) //
		 * .addFilter(ResponseLoggingFilter.logResponseTo(printStream))
		 * .setContentType(ContentType.JSON) .build()
		 * .auth().oauth2(TestDataHolder.getTestDataRecord("ACCESS_TOKEN"));
		 */
		return requestSpec;

	}

	public static ResponseSpecification buildResponseSpecification(String action) {
		ResponseSpecBuilder responseSpec = new ResponseSpecBuilder();
		if (assertSuccessfulResponse) {
			responseSpec.expectContentType("application/json");
			switch (action) {
				case "POST":	responseSpec.expectStatusLine("HTTP/1.1 201 Created");	break;
				case "GET":		responseSpec.expectStatusLine("HTTP/1.1 200 OK");		break;
				case "DELETE":	responseSpec.expectStatusLine("HTTP/1.1 200 OK");		break;
				case "PUT":	    responseSpec.expectStatusLine("HTTP/1.1 200 OK");		break;
			}

		}
		return responseSpec.build();
	}

	/*********************************************************************************************
	 * For setting the BASE URL with an invalid token and creating API Logs
	 ********************************************************************************************
	 * public RequestSpecification invalidRequestSpecification() throws
	 * FileNotFoundException { String timeStamp = new
	 * SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); PrintStream
	 * printStream = new
	 * PrintStream(CommonUtils.getUserDirectory()+"/resources/logs/"+"APILogs"+timeStamp+".txt");
	 * reqSpec = new
	 * RequestSpecBuilder().setBaseUri(TestDataHolder.getTestDataRecord("BASE_URL"))
	 * .addFilter(RequestLoggingFilter.logRequestTo(printStream))
	 * .addFilter(ResponseLoggingFilter.logResponseTo(printStream)) .build()
	 * .auth().oauth2(RandomStringUtils.randomAlphanumeric(8));
	 * 
	 * return reqSpec;
	 * 
	 * }
	 */

	/*********************************************************************************************
	 * ResponseSpecification
	 ********************************************************************************************
	 * public ResponseSpecification responseSpecification() throws
	 * FileNotFoundException { //respSpec = new
	 * ResponseSpecBuilder().expectContentType(ContentType.JSON).build(); respSpec =
	 * new ResponseSpecBuilder().build(); return respSpec;
	 * 
	 * }
	 */

	/*********************************************************************************************
	 * Getting a value from from JSON Body
	 *********************************************************************************************/

	public String getJsonPath(Response response, String key) {
		String resp = response.asString();
		JsonPath js = new JsonPath(resp);
		return js.get(key).toString();
	}
	


	public class MyResponse {

		public MyResponse fromJsonString(String json) {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(json, this.getClass());
		}
		
		
		public String toJsonString() {
			Gson gson = new GsonBuilder().create();
			return gson.toJson(this);
		}
		
		public class Pagination {
			public int page;
			public int size;
			public int count;
			public int total;
		}

		public class Audit {
			public long startTime;
			public long endTime;
			public int procesTime;
		}

		public class AdditionalInfo {
		}

		public class Detail {
			public AdditionalInfo additionalInfo;
			public String code;
			public String message;
			public String type;
		}

		public class Status {
			public String hTTP_CODE;
			public String hTTP_MESSAGE;
			public String result;
			public String description;
			public String message;
			public ArrayList<Detail> details;
			public String requestId;
			public String responseId;
		}

		public class Header {
			public Pagination pagination;
			public Audit audit;
			public Status status;
			public String referenceId;
			public String token;
		}

		/*
		 * public class Record { public String recordId;
		 * 
		 * // various stuff here, recordId is all we want - to be able to remove the
		 * record later
		 * 
		 * }
		 * 
		 * public class Body { public ArrayList<Record> records; }
		 */

		public Header header;
		//public Body body;
	}

}
