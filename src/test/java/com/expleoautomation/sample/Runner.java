package com.expleoautomation.sample;

import org.junit.runner.RunWith;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;


@RunWith(Cucumber.class)


@CucumberOptions (
		//dryRun=true,
		monochrome = true,
		publish = true,
	    features = {"classpath:features/Sample.feature" },
   	    glue = {"com.expleoautomation.sample"}, 
	    tags=""
	 )


public class Runner {

}
