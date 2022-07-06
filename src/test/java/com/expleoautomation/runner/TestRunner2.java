package com.expleoautomation.runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class)


@CucumberOptions (
		monochrome = true,
		publish = true,
				  features = {"classpath:features/Sample.feature" },
			   	    glue = {"com.expleoautomation.sample"},    
	    plugin = {"pretty", 
	    		  "json:target/jsonReports/cucumber-report.json",
				  "junit:target/cucumber.xml",
	    		   "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
		},
	    tags=""
	 )



public class TestRunner2 {

}