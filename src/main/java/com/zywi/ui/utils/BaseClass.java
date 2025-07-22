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
			File propFile = new File(System.getProperty(USER_DIR) + "//src//test//resources//Config//config.properties");
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
	 * ChromeOptions with support for headless execution and CI/CD environments.
	 * Adds common flags to stabilize test runs and improve compatibility.
	 *
	 * @return ChromeOptions configured for local or CI execution
	 */
	private ChromeOptions getChromeOptions() {
	    ChromeOptions options = new ChromeOptions();
	    // Fetch headless mode value from config.properties file
	    String headless = prop.getProperty("headless.mode", "false");
	 // Enable headless mode and common CI flags if either headless mode is true or tests are running on CI
	    if (headless.equalsIgnoreCase("true") || isRunningOnCI()) {
	        // Headless mode for CI (no browser UI)
	        options.addArguments("--headless=new"); // modern headless mode (recommended)
	        options.addArguments("--disable-gpu"); // disables GPU hardware acceleration
	        options.addArguments("--no-sandbox"); // needed for Linux-based CI systems
	        options.addArguments("--disable-dev-shm-usage"); // prevent crashes due to small /dev/shm
	    }

	    // Explanation: These two lines help reduce detection by websites 
	    // or avoid issues in headless test environments and make ChromeDriver less detectable and avoid conflicts
	    
	    // Removes the "Chrome is being controlled by automated test software" message
	    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

	    // Disables the automation extension used by older versions of ChromeDriver
	    // Helps avoid detection and also fixes random automation issues
	    options.setExperimentalOption("useAutomationExtension", false);

	    return options;
	}


	

	/**
	 * FirefoxOptions with headless toggle and CI/CD-friendly settings.
	 *
	 * @return FirefoxOptions
	 */
	private FirefoxOptions getFirefoxOptions() {
	    FirefoxOptions options = new FirefoxOptions();
	    String headless = prop.getProperty("headless.mode", "false");

	    if (headless.equalsIgnoreCase("true")) {
	        // Headless mode for CI environments (no GUI)
	        options.addArguments("-headless");
	        options.addArguments("--disable-gpu");
	        options.addArguments("--no-sandbox");
	        options.addArguments("--disable-dev-shm-usage");
	    }

	    return options;
	}

	
	
	/**
	 * EdgeOptions with headless toggle and CI/CD-safe flags.
	 *
	 * @return EdgeOptions
	 */
	private EdgeOptions getEdgeOptions() {
	    EdgeOptions options = new EdgeOptions();
	    String headless = prop.getProperty("headless.mode", "false");

	    if (headless.equalsIgnoreCase("true")) {
	        // Note: Edge uses Chromium, so we use similar headless args
	        options.addArguments("headless");
	        options.addArguments("--disable-gpu");
	        options.addArguments("--no-sandbox");
	        options.addArguments("--disable-dev-shm-usage");
	    }

	    return options;
	}

}
