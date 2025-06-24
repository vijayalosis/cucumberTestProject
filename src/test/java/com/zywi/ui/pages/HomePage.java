
package com.zywi.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.zywi.ui.utils.SeleniumUtils;

public class HomePage extends SeleniumUtils {

	public HomePage(WebDriver driver) {
		super(driver);
	}

	private static final By SIGN_IN_LINK = By.xpath("//a[contains(text(), 'Sign in')]");

	public LoginPage clickOnSignInlLink() {

		clickOn(SIGN_IN_LINK);
		return new LoginPage(driver);
	}

}
