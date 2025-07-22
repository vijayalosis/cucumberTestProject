package com.zywi.ui.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import io.cucumber.java.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks extends BaseClass {

    private static final Logger logger = LogManager.getLogger(Hooks.class);
    
    // Suite level tracking
    private static String suiteTagName = "";
    private static boolean isSuiteStarted = false;
    private static LocalDateTime suiteStartTime;
    
    // Scenario counters
    private static int totalScenarios = 0;
    private static int passedScenarios = 0;
    private static int failedScenarios = 0;
    
    // Scenario start time
    private long scenarioStartTime;

    // ============================================================================
    //                          Helper Methods 
    // ============================================================================

    private String getFeatureName(Scenario scenario) {
        String rawURI = scenario.getUri().toString();
        return rawURI.substring(rawURI.lastIndexOf('/') + 1);
    }
    
    /**
     * Formats LocalDateTime to a readable string.
     */
    private static String formatDateTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Logs the start of a scenario.
     */
    private void logScenarioStart(Scenario scenario) {
        logger.info("---- Starting Feature : " + getFeatureName(scenario));
        logger.info("---- Starting Scenario: " + scenario.getName());
        logger.info("\n=======================================================================================");
    }
    

    /**
     * Logs the end of a scenario.
     */
    private void logScenarioEnd(Scenario scenario) {
        logger.info("------- Ending Scenario: " + scenario.getName() + " | Status: " + scenario.getStatus() + " -----");
        logger.info("-----------------------------------------------------------------------------\n");
    }

    // ============================================================================
    //                          Cucumber Hooks 
    // ============================================================================


    @Before
    public void setUp(Scenario scenario) {
        scenarioStartTime = System.currentTimeMillis();
        System.out.println("Test Automation Scenario: " + scenario.getName() + " started at " + scenarioStartTime);
        
        // Log once when suite begins
        if (!isSuiteStarted) {
            isSuiteStarted = true;
            suiteStartTime = LocalDateTime.now();

            List<String> tags = scenario.getSourceTagNames().stream().toList();
            if (!tags.isEmpty()) {
            	// Take first tag as suite name
                suiteTagName = tags.get(0);
            }

            logger.info("\n====================== TEST SUITE STARTED AT: " + formatDateTime(suiteStartTime) + " ======================");
            if (!suiteTagName.isEmpty()) {
                logger.info("===== SUITE TAG: " + suiteTagName + " =====");
            }
        }

        totalScenarios++;
        logScenarioStart(scenario);
    }
    
    /**
     * Executes after each scenario: handles screenshot for failures, closes browser, and logs status.
     */
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.getStatus() == Status.PASSED) {
            passedScenarios++;
        } else if (scenario.getStatus() == Status.FAILED) {
            failedScenarios++;

            if (driver != null) {
                try {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", scenario.getName());
                } catch (Exception e) {
                    logger.warn("Could not capture screenshot: " + e.getMessage());
                }
            }
        }
        
        // Clean up driver
        if (driver != null) {
            driver.quit();
        }

        logScenarioEnd(scenario);
    }

    /**
     * Executes after all scenarios: prints and logs execution summary.
     */
    @AfterAll
    public static void afterAll() {
        LocalDateTime suiteEndTime = LocalDateTime.now();
        Duration duration = Duration.between(suiteStartTime, suiteEndTime);
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();

        // Summary in Console
        System.out.println("\u001B[34m===== TEST EXECUTION SUMMARY =====\u001B[0m");
        System.out.println("\u001B[36mTest Suite Name: \u001B[0m" + suiteTagName);
        System.out.println("\u001B[36mTest Start Time: \u001B[0m" + formatDateTime(suiteStartTime));
        System.out.println("\u001B[36mTest End Time: \u001B[0m" + formatDateTime(suiteEndTime));
        System.out.println("\u001B[36mTest Duration: \u001B[0m" + minutes + " minutes " + seconds + " seconds");

        String scenarioSummary = String.format(
            "Scenarios: Total: %d | \u001B[32mPassed: %d\u001B[0m | \u001B[31mFailed: %d\u001B[0m",
            totalScenarios, passedScenarios, failedScenarios
        );
        System.out.println(scenarioSummary);
        System.out.println("\u001B[34m==================================\u001B[0m\n");

        // ---------------- Log File Summary ----------------
        try (PrintWriter writer = new PrintWriter(new FileWriter("target/logs/automation.log", true))) {
            writer.println();
            writer.println("===== TEST EXECUTION SUMMARY ===============");
            writer.println("Test Suite Name: "       + suiteTagName);
            writer.println("Test Start Time: "       + formatDateTime(suiteStartTime));
            writer.println("Test End Time:   "         + formatDateTime(suiteEndTime));
            writer.println("Test Duration:   "         + minutes + " minutes " + seconds + " seconds");
            writer.println("Test Scenarios: Total: " + totalScenarios + " | Passed: " + passedScenarios + " | Failed: " + failedScenarios);
            writer.println("=============================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
