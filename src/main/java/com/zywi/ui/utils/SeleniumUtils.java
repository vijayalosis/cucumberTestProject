package com.zywi.ui.utils;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumUtils {

	protected WebDriver driver;
	protected WebDriverWait wait;
	protected Alert alert;
	protected WebElement element;

	public SeleniumUtils(WebDriver driver) {
		this.driver = driver;
	}

	public By waitForWebElement(By locator) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(CommonUtils.EXPLICIT_WAIT_TIME));
			wait.until(ExpectedConditions.elementToBeClickable(locator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return locator;

	}

	public void enterValue(By locator, String value) {
		driver.findElement(locator).clear();
		driver.findElement(locator).sendKeys(value);
	}

	public void enterText(By locator, String value) {
		waitForWebElement(locator);
		flashElement(locator);
		element = driver.findElement(locator);
		element.clear();
		element.sendKeys(value);
	}

	public void clickOn(By locator) {
		waitForWebElement(locator);
		flashElement(locator);
		element = driver.findElement(locator);
		element.click();
	}

	public String getText(By locator) {
		waitForWebElement(locator);
		flashElement(locator);
		element = driver.findElement(locator);
		return element.getText();
	}

	public void selectOptionInDropDown(By locator, String dropDownOption) {
		waitForWebElement(locator);
		element =  driver.findElement(locator);
		Select select = new Select(element);
		select.selectByVisibleText(dropDownOption);
		select.selectByValue(dropDownOption);
	}

	public void click(By locator) {
		driver.findElement(locator).click();
	}

	public boolean isDisplayed(By locator) {
		return driver.findElement(locator).isDisplayed();
	}

	// Flash highlighter before interaction
	private void flashElement(By locator) {
		
		element = driver.findElement(locator);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			for (int i = 0; i < 2; i++) {
				js.executeScript("arguments[0].style.border='3px solid yellow'", element);
				Thread.sleep(150);
				js.executeScript("arguments[0].style.border=''", element);
				Thread.sleep(150);
			}
			js.executeScript("arguments[0].style.border='3px solid yellow'", element);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
