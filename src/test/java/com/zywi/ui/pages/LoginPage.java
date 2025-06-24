
 package com.zywi.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.zywi.ui.utils.SeleniumUtils;

public class LoginPage extends SeleniumUtils{
	
	 public LoginPage(WebDriver driver) {
	        super(driver);
	    }

	 private static final By EMAIL_TEXT_BOX = By.id("email");
	 private static final By PASSWORD_TEXT_BOX = By.id("passwd");
	 private static final By SUBMIT_LOGIN_BUTTON = By.id("SubmitLogin");
	    

	    public void enterUsername(String username) {
	    	enterText(EMAIL_TEXT_BOX, username);
	    }

	    public void enterPassword(String password) {
	    	enterValue(PASSWORD_TEXT_BOX, password);
	    }

	    public MyAccountPage clickLogin() {
	        click(SUBMIT_LOGIN_BUTTON);
	        return new MyAccountPage(driver);
	    }

		
}
