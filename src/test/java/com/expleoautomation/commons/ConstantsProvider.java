package com.expleoautomation.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Assume;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Constants as from the properties file.
 *
 * @author youssef.abdalla
 */
@Log4j2
public class ConstantsProvider {

    private static Properties allProperties;
    private static Properties envProperties;

    static {
        Properties initProperties = new Properties();
        try (InputStream propertyFileInputStream = new FileInputStream(ConstantsNames.CONSTANTS_FILE_NAME)) {
            initProperties.load(propertyFileInputStream);
            allProperties = initProperties;
        } catch (final IOException e) {
            log.fatal("failed to load the constants file from: " + ConstantsNames.CONSTANTS_FILE_NAME);
            Assume.assumeNoException(e);
        }
    }
    
    static {
        Properties envConfigProperties = new Properties();
        try (InputStream configPropertyFileInputStream = new FileInputStream(ConstantsNames.ENV_PROPERTIES_FILE_NAME)) {
        	envConfigProperties.load(configPropertyFileInputStream);
        	envProperties = envConfigProperties;
        } catch (final IOException e) {
            log.fatal("failed to load the config properties file from: " + ConstantsNames.ENV_PROPERTIES_FILE_NAME);
            Assume.assumeNoException(e);
        }
    }
    
    public static final String EXECUTION_STATUS_PASS = "1";
    public static final String EXECUTION_STATUS_FAIL = "2";
    public static final String EXECUTION_STATUS_WIP = "3";
    public static final String EXECUTION_STATUS_UNEXECUTED = "-1";
    public static final String EXECUTION_STATUS_BLOCKED = "4";
    @Getter private static final String MYBATIS_CONFIG_FILE_PATH = "mybatis-config.xml";
    
    @Getter private static final String ENVIRONMENT = envProperties.getProperty(ConstantsNames.ENVIRONMENT);   
    @Getter private static final String CLIENTID = envProperties.getProperty(ConstantsNames.CLIENTID);
    @Getter private static final String CLIENTSECRET = envProperties.getProperty(ConstantsNames.CLIENTSECRET);
    @Getter private static final String GRANT_TYPE = envProperties.getProperty(ConstantsNames.GRANT_TYPE);
    @Getter private static final String ACCESS_TOKEN_URL = envProperties.getProperty(ConstantsNames.ACCESS_TOKEN_URL);
    @Getter private static final String BASE_URL = envProperties.getProperty(ConstantsNames.BASE_URL);
    @Getter private static final String API_UID = envProperties.getProperty(ConstantsNames.API_UID);
    @Getter private static final String API_PWD = envProperties.getProperty(ConstantsNames.API_PWD);
    @Getter private static final String AUTH_DOMAIN = envProperties.getProperty(ConstantsNames.AUTH_DOMAIN);  
    @Getter private static final String DB_CONNECTION = envProperties.getProperty(ConstantsNames.DB_CONNECTION);  
    @Getter private static final String DB_UID = envProperties.getProperty(ConstantsNames.DB_UID);  
    @Getter private static final String DB_PWD = envProperties.getProperty(ConstantsNames.DB_PWD);  
    @Getter private static final String WEB_UID_1 = envProperties.getProperty(ConstantsNames.WEB_UID_1);
    @Getter private static final String WEB_PWD_1 = envProperties.getProperty(ConstantsNames.WEB_PWD_1);
    @Getter private static final String WEB_UID_2 = envProperties.getProperty(ConstantsNames.WEB_UID_2);
    @Getter private static final String WEB_PWD_2 = envProperties.getProperty(ConstantsNames.WEB_PWD_2);
    @Getter private static final String WEB_UID_10 = envProperties.getProperty(ConstantsNames.WEB_UID_10);
    @Getter private static final String WEB_PWD_10 = envProperties.getProperty(ConstantsNames.WEB_PWD_10);
    @Getter private static final String WEB_URL = envProperties.getProperty(ConstantsNames.WEB_URL);
    
    
    
    @Getter private static final String URL = allProperties.getProperty(ConstantsNames.URL);
    @Getter private static final String EIRCODE = allProperties.getProperty(ConstantsNames.EIRCODE);
    @Getter private static final String ADDRESS = allProperties.getProperty(ConstantsNames.ADDRESS);
    @Getter private static final String CONTACTNUMBER = allProperties.getProperty(ConstantsNames.CONTACTNUMBER);
    @Getter private static final String SENIORPROFILE_USERNAME = allProperties.getProperty(ConstantsNames.SENIORPROFILE_USERNAME);
    @Getter private static final String DATETIMESTAMP = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    @Getter private static final String REST_ASSURED_BASE_URLS_START = allProperties.getProperty(ConstantsNames.REST_ASSURED_BASE_URLS_START);
    @Getter private static final String REST_ASSURED_INET_BASE_URLS_START = allProperties.getProperty(ConstantsNames.REST_ASSURED_INET_BASE_URLS_START);
    @Getter private static final String REST_ASSURED_INET_BASE_PATH = allProperties.getProperty(ConstantsNames.REST_ASSURED_INET_BASE_PATH);
    @Getter private static final String REST_ASSURED_BASE_URLS_END = allProperties.getProperty(ConstantsNames.REST_ASSURED_BASE_URLS_END);
    @Getter private static final String BASE_PATH = allProperties.getProperty(ConstantsNames.BASE_PATH);
    @Getter private static final String JIRA_URL = allProperties.getProperty(ConstantsNames.JIRA_URL);
    @Getter private static final String ZAPI_PATH = allProperties.getProperty(ConstantsNames.ZAPI_PATH);
    @Getter private static final String TEST_CYCLE_NAME = allProperties.getProperty(ConstantsNames.TEST_CYCLE_NAME);
    @Getter private static final boolean SELENIUM_GRID_ACTIVE = Boolean.parseBoolean(allProperties.getProperty(ConstantsNames.USE_SELENIUM_GRID));
    @Getter private static final String SELENIUM_HUB_URL = "http://" + allProperties.getProperty(ConstantsNames.SELENIUM_VM_IP) + ":4444/wd/hub";
    @Getter private static final String SELENIUM_VM_IP = allProperties.getProperty(ConstantsNames.SELENIUM_VM_IP);
    @Getter private static final int SELENIUM_HUB_PORT = Integer.parseInt(allProperties.getProperty(ConstantsNames.SELENIUM_HUB_PORT));
    @Getter private static final String SELENIUM_WEB_BROWSER_NAME = allProperties.getProperty(ConstantsNames.WEB_BROWSER);     /* This is browser name after the folder of the driver (eg. resources/webdriver/chrome in case of chrome)    */
    @Getter private static final String SELENIUM_DRIVER_PATH = allProperties.getProperty(ConstantsNames.SELENIUM_DRIVER_PATH);
    @Getter private static final String LOCAL_FIREFOX_BINARY_ABSOLUTE_PATH = allProperties.getProperty(ConstantsNames.LOCAL_FIREFOX_BINARY_ABSOLUTE_PATH);
    @Getter private static final String SSO_LOGIN_URL = allProperties.getProperty(ConstantsNames.SSO_LOGIN_URL);
    @Getter private static final String SSO_AGENT_LOGIN_URL = allProperties.getProperty(ConstantsNames.SSO_AGENT_LOGIN_URL);
    @Getter private static final String SSO_LOGIN_USERNAME = allProperties.getProperty(ConstantsNames.SSO_LOGIN_USERNAME);
    @Getter private static final String SSO_AGENT_LOGIN_USERNAME = allProperties.getProperty(ConstantsNames.SSO_AGENT_LOGIN_USERNAME);
    @Getter private static final String SSO_LOGIN_PASSWORD_DEV = allProperties.getProperty(ConstantsNames.SSO_LOGIN_PASSWORD_DEV);
    @Getter private static final String SSO_LOGIN_PASSWORD_UAT = allProperties.getProperty(ConstantsNames.SSO_LOGIN_PASSWORD_UAT);
    @Getter private static final String SSO_LOGIN_PASSWORD_CENTEST = allProperties.getProperty(ConstantsNames.SSO_LOGIN_PASSWORD_CENTEST);
    @Getter private static final String SSO_LOGIN_PASSWORD_PRELIVE = allProperties.getProperty(ConstantsNames.SSO_LOGIN_PASSWORD_PRELIVE);
    @Getter private static final String PROJECT_PARCEL_DELETE_PATH = allProperties.getProperty(ConstantsNames.PROJECT_PARCEL_DELETE_PATH);
    @Getter private static final String WINDOWS_USER_NAME = allProperties.getProperty(ConstantsNames.WINDOWS_USER_NAME);
    @Getter private static final String WINDOWS_PASSWORD = allProperties.getProperty(ConstantsNames.WINDOWS_PASSWORD);
    @Getter private static final String JIRA_USER_NAME = allProperties.getProperty(ConstantsNames.JIRA_USER_NAME);
    @Getter private static final String JIRA_PASSWORD = allProperties.getProperty(ConstantsNames.JIRA_PASSWORD);
    @Getter private static final String JIRA_ASSIGNEE_NAME = allProperties.getProperty(ConstantsNames.JIRA_ASSIGNEE_NAME);
    @Getter private static final long GLOBAL_TIMEOUT = Long.parseLong(allProperties.getProperty(ConstantsNames.GLOBAL_TIMEOUT_IN_SECONDS));
   
    /**
     * @param environment
     *            {@link AllowedEnvironments#ENVIRONMENT_DEV} or {@link AllowedEnvironments#ENVIRONMENT_CENTEST}
     * @return the time frame of the Jira creation batch job to be triggered on the given environment
     */
    public static long getJiraCreationTimeFrame(String environment) {
        return Long.parseLong(allProperties.getProperty("jiraCreationTimeFrame_" + environment));
    }

}
