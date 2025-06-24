package com.zywi.ui.stepdefinition;

import com.zywi.ui.utils.BaseClass;


import io.cucumber.java.en.*;



import static org.testng.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zywi.ui.pages.HomePage;
import com.zywi.ui.pages.LoginPage;
import com.zywi.ui.pages.MyAccountPage;



public class LoginStepDefinition extends BaseClass {
	
	private static final Logger logger = LogManager.getLogger(LoginStepDefinition.class);
    LoginPage loginPage;
    HomePage homePage;
    MyAccountPage myAccountPage;
    
    
    @Given("User launches {string} browser and opens the Login Page")
    public void user_launches_browser_and_opens_login_page(String browser) {
        logger.info("Launching browser: " + browser);
        launchApplication(browser);
        //loginPage = new LoginPage(driver);
        //Assert.assertTrue(loginPage.isLoginPageLoaded());
        //ReportUtil.logPass("Login page loaded successfully on browser: " + browser);
        
        
    }
    
   
    @When("User enters username {string} and password {string}")
    public void user_enters_credentials(String username, String password) {
    	loginPage = new HomePage(driver).clickOnSignInlLink();
        logger.info("Entering credentials");
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        //ReportUtil.logInfo("Entered username and password");
    }

    
    @And("Clicks on Login button")
    public void clicks_on_login_button() {
        logger.info("Clicking login button");
        myAccountPage= loginPage.clickLogin();
        //ReportUtil.logInfo("Clicked on login button");
    }

    
    @Then("User should be navigated to Accounts Page")
    public void user_should_be_navigated_to_accounts_page() {
       // boolean isLoggedIn = loginPage.isHomePageDisplayed();
        logger.info("Navigating to the Accounts Page");
       // Assert.assertTrue("Home page not displayed", isLoggedIn);
        
        String userName = myAccountPage.getUserName();
        logger.info("Logged in sucessfully with the user name " + userName);
		System.out.println("Logged in sucessfully with the user name " + userName);
		assertEquals(userName, "Ajay Devgan");
        //ReportUtil.logPass("Successfully logged in and navigated to Home Page");
    }

}
