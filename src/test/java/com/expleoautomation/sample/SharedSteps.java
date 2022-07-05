package com.expleoautomation.sample;

import static com.codeborne.selenide.Selenide.webdriver;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.codeborne.selenide.Configuration;
import com.expleoautomation.commons.Behaviour;
import com.expleoautomation.commons.ConstantsNames;
import com.expleoautomation.commons.StaticData;
import com.expleoautomation.pom.northerntrust.ErrorMsg;
import com.expleoautomation.pom.northerntrust.Footer;
import com.expleoautomation.pom.northerntrust.MultifondsMenu;
import com.expleoautomation.pom.northerntrust.PopUp;
import com.expleoautomation.stepdefinitions.StaticDataSteps;
import com.expleoautomation.stepdefinitions.UiSteps;
import com.expleoautomation.utils.DatabaseUtils;
import com.expleoautomation.utils.WebUI;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class SharedSteps {
	
	@Before
	public void beforeEveryTest(Scenario scenario) {
		
		

		// welcome!
		log.info(System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
		log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		log.info("START test scenario " + scenario.getName() + " (" + scenario.getLine() + ")");
		log.info(scenario.getUri());
		log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + System.lineSeparator() + System.lineSeparator());
		
		
	    // machine name
		String machineName = "";
		try {
			machineName = InetAddress.getLocalHost().getHostName().toUpperCase();
		} catch (UnknownHostException e) { /* ignore */ }
		log.info("Machine: " + machineName);
		System.setProperty("host.name", machineName);
	    
	    // OS info
	    log.info("OS: " +  SystemUtils.OS_NAME + " (v" + SystemUtils.OS_VERSION + ")");		
	    log.info("Java: " + System.getProperty("java.version"));
	    
	    
		// disk space
		long gbFree = new File("C:").getUsableSpace() /1024 /1024 /1024;
		log.info("C:\\ " + gbFree + " GB free");
		if (gbFree<1) {
			log.warn("* LOW DISK SPACE *");
		}
		
		
		// define default behaviour
		Behaviour.assertFailure = true;			
		Behaviour.runSlow = false; 
		Behaviour.assertFailedSetValue = true;
		Behaviour.maximizeBrowserWindow = true;
		Behaviour.clearCookiesOnStartup = false;
		log.info("Behaviour settings:");
		log.info("   runSlow:" + Behaviour.runSlow);
		log.info("   assertFailure:" + Behaviour.assertFailure);
		log.info("   assertFailedSetValue:" + Behaviour.assertFailedSetValue);
		log.info("   maximizeBrowserWindow:" + Behaviour.maximizeBrowserWindow);
		log.info("   clearCookiesOnStartup:" + Behaviour.clearCookiesOnStartup);
		
		// set log level
		Level logLevel = Level.DEBUG;
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.getConfiguration().getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(logLevel); 
		log.info("   log4j.level: " + logLevel.toString());

		
	
	}
	
	@Before("not @ShareDataBetweenTests")
	public void clearDataBetweenTests(Scenario scenario){

		// clear property bag
		StaticDataSteps.properties.clear();
		UiSteps.properties.clear();
		StaticData.keyValueReport.clear();
		// preserve UiSteps.browserTabs to leave tabs open 

	}

	
	@Before("@Web")
	public void beforeWebTest(Scenario scenario){
		

		// configuration (https://selenide.org/javadoc/5.6.0/com/codeborne/selenide/Configuration.html)
		Configuration.headless = System.getProperty("host.name").startsWith("ILRC");
		Configuration.screenshots = true;
		Configuration.fastSetValue = true;
		Configuration.selectorMode = Configuration.selectorMode.CSS;
		Configuration.pageLoadStrategy = "normal";
		Configuration.pageLoadTimeout = 600 * 1000;
		Configuration.timeout  = 10 * 1000; // 10s

		// log
		log.info("Selenide settings:");
		log.info("   headless:" + Configuration.headless);
		log.info("   screenshots:" + Configuration.screenshots);
		log.info("   fastSetValue:" + Configuration.fastSetValue);
		log.info("   selectorMode:" + Configuration.selectorMode);
		log.info("   pageLoadStrategy:" + Configuration.pageLoadStrategy);
		log.info("   pageLoadTimeout:" + Configuration.pageLoadTimeout);
		log.info("   timeout:" + Configuration.timeout);
		
		
	}
	

	@After
	public void afterEveryTest(Scenario scenario) {

		// close db connections
		for (Entry<String, Connection> conn : DatabaseUtils.connectionPool.entrySet()) {

			try {
				log.debug("Closing db connection " + conn.getKey());
				Connection closeMe = conn.getValue();
				if (!closeMe.getAutoCommit()) {
					closeMe.rollback();
				}
				closeMe.close();
			} catch (SQLException e) {	
				log.error("afterEveryTest(): FAILED to close DB connection: " + e.getMessage());
			}
		}
		DatabaseUtils.connectionPool.clear();

		// rest assured
		RestAssured.reset();
		
		
		// Save key-value report
		if (!StaticData.keyValueReport.isEmpty()) {
			log.info("Attaching 'Key-value Report' to test scenario");
			StringBuilder builder = new StringBuilder();
			for (Entry<String, String> entry : StaticData.keyValueReport.entrySet()) {
				String info = entry.getKey() + " = " + entry.getValue();
				builder.append(info).append(System.lineSeparator());
		    }
			scenario.attach(builder.toString(), "text", "Key-value Report");
		}
		

		// goodbye
		log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		log.info("END test scenario " + scenario.getName() + " (" + scenario.getLine() + ") with status "
				+ scenario.getStatus());
		log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		log.info(System.lineSeparator() + System.lineSeparator() + System.lineSeparator());


	}
	
	
	
    /**
     * Takes screenshot after scenario ends regardless of status
     */
    @After("@Web")
    public void afterWebTest(Scenario scenario) {
    	if (!scenario.getStatus().name().equals("PASSED")) {
    		
    		// take screenshot
	        try {
	        	TakesScreenshot shooter = ((TakesScreenshot) webdriver().driver().getWebDriver());
			    final byte[] screenshot = shooter.getScreenshotAs(OutputType.BYTES);
			    scenario.attach(screenshot, "image/jpeg", scenario.getName());
	    		log.debug("afterWebTest(): " + scenario.getName());
			} catch (Exception e) {
				log.error("afterWebTest(): FAILED to take screenshot - '" + e.getMessage() + "'");
			}
	        
	        // navigate back to home screen
	        log.debug(" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        log.debug("afterWebTest(): close-all and return to home screen");
	        
	        // loop round open tabs
	        for (String windowHandle : Steps.browserTabs.values()) {
	        	// switch tab
	        	WebUI.switchTab(windowHandle);
	        	WebUI.close();
	        }

    	}
    	

    }

}
