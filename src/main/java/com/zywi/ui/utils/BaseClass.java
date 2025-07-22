package com.zywi.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;

import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseClass {

	private static final String USER_DIR = "user.dir";
	protected static WebDriver driver;
	protected static Properties prop;

	private static final Logger logger = LogManager.getLogger(BaseClass.class);
	
	/**
	 * Loads configuration properties from config.properties file
	 */

	public void loadConfig() {
		try {
			prop = new Properties();
			File propFile = new File(System.getProperty(USER_DIR) + "//src//test//resource//Config//config.properties");
			System.out.println("The properties file is: " + propFile);
			FileInputStream filePath = new FileInputStream(propFile);
			prop.load(filePath);
		} catch (IOException e) {
			logger.error("Failed to load config.properties", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Launches the application based on the execution mode (local/remote) and browser
	 * 
	 * @param browser - browser type like Chrome, Firefox, edge
	 */

	protected void launchApplication(String browser) {
		try {
			// Load the config before starting
			loadConfig();
			String executionMode = prop.getProperty("execution.mode", "local").toLowerCase();
			String gridUrl = prop.getProperty("grid.url");
			
			// CI check to skip Edge on Linux/CI runners
			if (isRunningOnCI() && browser.equalsIgnoreCase("edge")) {
				logger.warn("Skipping Edge browser on CI (Ubuntu does not support Edge)");
				return;
			}
			
			// Remote Execution using Selenium Grid
			if (executionMode.equals("remote")) {
				URL remoteUrl = new URL(gridUrl);
				logger.info("Running in Remote Grid: " + remoteUrl);

				if (browser.equalsIgnoreCase("chrome")) {
					driver = new RemoteWebDriver(remoteUrl, getChromeOptions());
				} else if (browser.equalsIgnoreCase("firefox")) {
					driver = new RemoteWebDriver(remoteUrl, getFirefoxOptions());
				} else if (browser.equalsIgnoreCase("edge")) {
					driver = new RemoteWebDriver(remoteUrl, getEdgeOptions());
				} else {
					throw new IllegalArgumentException("Unsupported remote browser: " + browser);
				}
				
				// Local Execution
			} else if (executionMode.equals("local")) {
				logger.info("Running Locally");

				if (browser.equalsIgnoreCase("chrome")) {
					driver = new ChromeDriver(getChromeOptions());
				} else if (browser.equalsIgnoreCase("firefox")) {
					driver = new FirefoxDriver(getFirefoxOptions());
				} else if (browser.equalsIgnoreCase("edge")) {
					driver = new EdgeDriver(getEdgeOptions());
				} else {
					throw new IllegalArgumentException("Unsupported local browser: " + browser);
				}

			} else {
				throw new IllegalArgumentException("Invalid execution mode in config.properties: " + executionMode);
			}
			
			// Common setup after driver is initialized
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			driver.manage().window().maximize();
			driver.get(prop.getProperty("app.url"));

		} catch (Exception e) {
			logger.error("Error initializing WebDriver for browser: " + browser, e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Determines if the tests are running on a CI/CD environment (e.g., GitHub Actions)
	 * 
	 * @return true if running on CI (Linux or GITHUB_ACTIONS env var set)
	 */
	private boolean isRunningOnCI() {
		String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		return os.contains("linux") || System.getenv("GITHUB_ACTIONS") != null;
	}
	
	/**
	 * ChromeOptions with headless toggle based on config
	 * 
	 * @return ChromeOptions
	 */
	private ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		String headless = prop.getProperty("headless.mode", "false");

		if (headless.equalsIgnoreCase("true")) {
			options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
		}

		return options;
	}
	
	/**
	 * FirefoxOptions with headless toggle
	 * 
	 * @return FirefoxOptions
	 */
	private FirefoxOptions getFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();
		String headless = prop.getProperty("headless.mode", "false");

		if (headless.equalsIgnoreCase("true")) {
			options.addArguments("-headless");
		}

		return options;
	}
	
	/**
	 * EdgeOptions with headless toggle
	 * 
	 * @return EdgeOptions
	 */
	private EdgeOptions getEdgeOptions() {
		EdgeOptions options = new EdgeOptions();
		String headless = prop.getProperty("headless.mode", "false");

		if (headless.equalsIgnoreCase("true")) {
			options.addArguments("headless");
		}

		return options;
	}
}
