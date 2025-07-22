package com.zywi.ui.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;





import java.io.File;

public class ScreenshotCaptureUtil  {

    public static void captureCucumberHtmlReportScreenshot() {
        WebDriver reportDriver = null;
        try {
            // Launch a fresh headless browser just for screenshot
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1280,900");
            reportDriver = new ChromeDriver(options);

            // Load local Cucumber report
            File reportFile = new File("target/CucumberReports/Cucumber-reports.html");
            String reportUrl = reportFile.toURI().toString();

            reportDriver.get(reportUrl);
            Thread.sleep(2000); // Give time to fully render

            File screenshot = ((TakesScreenshot) reportDriver).getScreenshotAs(OutputType.FILE);
            File destination = new File("C:/Reports/Cucumber-report-screenshot.png");
            FileUtils.copyFile(screenshot, destination);

            System.out.println("✅ Report screenshot saved: " + destination.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Failed to capture report screenshot: " + e.getMessage());
        } finally {
            if (reportDriver != null) {
                reportDriver.quit();
            }
        }
    }
}


