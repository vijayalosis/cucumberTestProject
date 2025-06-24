package com.zywi.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;


public class BaseClass {
	
	public static WebDriver driver;
    public static Properties prop;

    public void loadConfig() {
        try {
            prop = new Properties();
            File propFile = new File(System.getProperty("user.dir") + "//src//test//resource//Config//config.properties");	
            System.out.println(propFile);
            FileInputStream filePath = new FileInputStream(propFile);
            prop.load(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchApplication(String browser) {
        try {
            loadConfig();
            String executionMode = prop.getProperty("execution.mode");

            if (executionMode.equalsIgnoreCase("remote")) {
                URL gridUrl = new URL(prop.getProperty("grid.url"));

                if (browser.equalsIgnoreCase("chrome")) {
                	ChromeOptions options = new ChromeOptions();
                	options.addArguments("--headless");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");

                    driver = new ChromeDriver(options);
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                    driver = new RemoteWebDriver(gridUrl, options);
                } else if (browser.equalsIgnoreCase("edge")) {
                    EdgeOptions options = new EdgeOptions();
                    driver = new RemoteWebDriver(gridUrl, options);
                }
            } else {
            	if(executionMode.equalsIgnoreCase("local")) {
                if (browser.equalsIgnoreCase("chrome")) {
                    driver = new ChromeDriver();
                } else if (browser.equalsIgnoreCase("edge")) {
                    driver = new EdgeDriver();
                }
            }
            }
            driver.manage().window().maximize();
            driver.get(prop.getProperty("app.url"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
