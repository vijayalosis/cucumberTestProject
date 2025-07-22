package com.zywi.ui.testrunner;



import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
				features = { "classpath:Features" }, 
				glue = { "com.zywi.ui.stepdefinition", "com.zywi.ui.utils" }, 
				publish = true, 
				plugin = { "pretty","html:target/CucumberReports/Cucumber-reports.html" }, 
				tags = "@ValidLogin", monochrome = true, dryRun = false)


public class TestRunner extends AbstractTestNGCucumberTests {
	
	

}