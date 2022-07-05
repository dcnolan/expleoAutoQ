package com.expleoautomation.runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class)


@CucumberOptions (
		monochrome = true,
		publish = true,
	    features = {
		    		  "classpath:features/complete-features/Framework.feature" 
		    		, "classpath:features/complete-features/GLSetup.feature"
		    		, "classpath:features/complete-features/StaticDataSetup.feature"
		    		, "classpath:features/complete-features/InvestorSetup.feature" 
		    		, "classpath:features/complete-features/Transaction.feature" 
		    		, "classpath:features/data-creation-features/DataCreation.feature"
		    		, "classpath:features/ui-features/FundsExplorer.feature" 
	    		},
	    
    	    glue = {"com.expleoautomation.stepdefinitions"},
	    
	    plugin = {"pretty", 
	    		  "json:target/jsonReports/cucumber-report.json",
				  "junit:target/cucumber.xml",
	    		   "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"

		},



	    tags="@Web"


	 )



public class TestRunner2 {

}