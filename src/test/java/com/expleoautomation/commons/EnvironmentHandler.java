package com.expleoautomation.commons;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/** Handling environment usage */
@Log4j2
public class EnvironmentHandler {

  public static final String SSO_STAFF_USER_NAME_KEY = "SSO_STAFF_USER_NAME";
  public static final String SSO_AGENT_USER_NAME_KEY = "SSO_AGENT_USER_NAME";
  public static final String SSO_PASSWORD_KEY = "SSO_PASSWORD_KEY";

  /**
   * @param environment as returned from {@link #getEnvironmentForTest()}
   * @return The base URL need to configure RestAssured and browser tests based on the test environment.
   */
  public static String getEnvironmentBaseUrl(@NonNull String environment) {
    return getEnvironmentBaseUrl(environment, false);
  }

  /**
   * @param environment as returned from {@link #getEnvironmentForTest()}
   * @return The base URL need to configure RestAssured and browser tests based on the test environment.
   */
  public static String getEnvironmentBaseUrl(@NonNull String environment, boolean isForInet) {
    String rest_assured_base_urls_start;
    if (isForInet) {
      rest_assured_base_urls_start = ConstantsProvider.getREST_ASSURED_INET_BASE_URLS_START();
    }
    else {
    	rest_assured_base_urls_start = ConstantsProvider.getREST_ASSURED_BASE_URLS_START();
    }
    	
    String restAssuredBaseUrl = rest_assured_base_urls_start + environment + ConstantsProvider.getREST_ASSURED_BASE_URLS_END();
    log.debug("Base url= " + restAssuredBaseUrl);
    return restAssuredBaseUrl;
  }
  
  /**
   * Get the URL for Accessing the Application- Pradeep
   */
   public static String getUrl()
   {
	   String Url = ConstantsProvider.getURL();
	   log.debug("EDMS URL: " + Url);
	   return Url;
 	   
   }
  

  /**
   * @return the environment for this test as String. this works by examining the 'env' variable
   *     from the system. If the variable is null or empty this returns the default environment as
   *     configured in the properties constants.properties
   */
  static String getEnvironmentForTest() {
    String env = System.getProperty("env");
    return AllowedEnvironments.getAllowedEnvironment(env);
  }

  
  public static Map<String, String> getUserNameAndPasswordForUiTests(@NonNull String environment) {
    String staffUserName = ConstantsProvider.getSSO_LOGIN_USERNAME();
    String agentUserName = ConstantsProvider.getSSO_AGENT_LOGIN_USERNAME();
    String pwd;
    switch (environment.toLowerCase()) {
      case AllowedEnvironments.ENVIRONMENT_DEV:
        pwd = ConstantsProvider.getSSO_LOGIN_PASSWORD_DEV();
        break;
      case AllowedEnvironments.ENVIRONMENT_UAT:
        pwd = ConstantsProvider.getSSO_LOGIN_PASSWORD_UAT();
        break;
      case AllowedEnvironments.ENVIRONMENT_CENTEST:
        pwd = ConstantsProvider.getSSO_LOGIN_PASSWORD_CENTEST();
        break;
      default:
        throw new IllegalArgumentException(
            "SSO password for "
                + "environment "
                + environment
                + " not "
                + "configured in "
                + "properties file..");
    }
    Map<String, String> userNamePwdMap = new HashMap<>();
    userNamePwdMap.put(SSO_STAFF_USER_NAME_KEY, staffUserName);
    userNamePwdMap.put(SSO_AGENT_USER_NAME_KEY, agentUserName);
    userNamePwdMap.put(SSO_PASSWORD_KEY, pwd);
    return userNamePwdMap;
  }
}
