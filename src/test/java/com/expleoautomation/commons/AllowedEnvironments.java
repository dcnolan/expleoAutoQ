package com.expleoautomation.commons;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

/**
 * Environments where tests allowed to run on. Mainly used to facilitate the usage of {@linkEnvironmentHandler}
 */
public class AllowedEnvironments {

  public static final String ENVIRONMENT_DEFAULT =  ConstantsProvider.getENVIRONMENT();
  public static final String ENVIRONMENT_DEV = "dev";
  public static final String ENVIRONMENT_UAT = "uat";
  public static final String ENVIRONMENT_CENTEST = "centest";
  public static final String ENVIRONMENT_IMAP_SCAN = "imap_scan";

  /**
   * Environment is read from the test executer to be in as an example: DEVC or CENTEST-DEPT. 
   * This happens when Bamboo executes the test as a result of a deploy to a certain environment.
   *
   * @param environment the environment as given from the caller.
   * @return the environment which current test should run on. Returns the default environment if
   * called with NULL or empty String
   */
  public static String getAllowedEnvironment(String environment) {
    if (Strings.isNullOrEmpty(environment)) {
      //log.debug("environment for test is passed as null or empty. Set "
         // + "to default: " + ENVIRONMENT_DEFAULT);
      return ENVIRONMENT_DEFAULT;
    }
    String x = parseTestEnvironmentFromString(environment);
    if (x != null) {
      return x;
    }
    return ENVIRONMENT_DEFAULT;
  }

  /**
   * @see #getAllowedEnvironment(String)
   */
  public static String parseTestEnvironmentFromString(String environment) {
    if (StringUtils.containsIgnoreCase(environment, ENVIRONMENT_DEV)) {
      return ENVIRONMENT_DEV;
    }
    if (StringUtils.containsIgnoreCase(environment, ENVIRONMENT_CENTEST)) {
      return ENVIRONMENT_CENTEST;
    }
    if (StringUtils.containsIgnoreCase(environment, ENVIRONMENT_UAT)) {
      return ENVIRONMENT_UAT;
    }
    if (StringUtils.containsIgnoreCase(environment, ENVIRONMENT_IMAP_SCAN)) {
      return ENVIRONMENT_IMAP_SCAN;
    }
    return null;
  }
}