package com.zywi.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.zywi.ui.utils.SeleniumUtils;



public final class MyAccountPage extends SeleniumUtils{
	
	private static final By USER_NAME_LOCATOR = By.xpath("//a[@title='View my customer account']/span");

	public MyAccountPage(WebDriver driver) {
		super(driver);
		
	}
	
	public String getUserName() {
		return getText(USER_NAME_LOCATOR);
	}

}
