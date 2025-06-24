package com.zywi.ui.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.zywi.ui.reports.CustomHtmlReportGenerator;
import com.zywi.ui.reports.CustomHtmlReportWriter;
import com.zywi.ui.reports.ScreenShotUtils;
import com.zywi.ui.reports.ScreenShotUtils.FileUtils;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;

public class Hooks extends BaseClass {

	private static CustomHtmlReportWriter reportWriter;
	private List<String> stepLogs;
	private boolean isScenarioPassed;
	private String currentFeature;
	private String currentScenario;

	// private static final Logger logger = LogManager.getLogger(Hooks.class);
	private long startTime;

	private static final Logger logger = LogManager.getLogger(Hooks.class);

	private static LocalDateTime suiteStartTime;
	private static int totalScenarios = 0;
	private static int passedScenarios = 0;
	private static int failedScenarios = 0;
	private static boolean isSuiteStarted = false;
	private static String suiteTagName = "";

	private static String formatDateTime(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	@BeforeAll
	public static void beforeAll() {
		FileUtils.clearFolder("target/screenshots/");
		CustomHtmlReportGenerator.startReport();
	}

	@Before
	public void setUp(Scenario scenario) {
		// CleanLogsUtil.cleanLogs();
		// FileUtils.clearFolder("target/screenshots/");
		// logger.info("Starting scenario: " + scenario.getName());
		startTime = System.currentTimeMillis();
		System.out.println("Test Automation Scenario Started: " + scenario.getName() + startTime);

		if (!isSuiteStarted) {
			suiteStartTime = LocalDateTime.now();
			isSuiteStarted = true;

			// Capture the first tag of the first scenario
			List<String> tags = scenario.getSourceTagNames().stream().toList();
			if (!tags.isEmpty()) {
				suiteTagName = tags.get(0); // Take the first tag as the suite indicator
			}

			logger.info("===== TEST SUITE STARTED AT: " + formatDateTime(suiteStartTime) + " =====");
			if (!suiteTagName.isEmpty()) {
				logger.info("===== SUITE TAG: " + suiteTagName + " =====");
			}
		}

		totalScenarios++;
		logger.info("----- STARTING SCENARIO: " + scenario.getName() + " -----");

		if (reportWriter == null) {
			reportWriter = new CustomHtmlReportWriter();
		}

		stepLogs = new ArrayList<>();
		isScenarioPassed = true;
		currentScenario = scenario.getName();
		currentFeature = scenario.getUri().toString(); // Optional: parse better if needed
	}

	@After
	public void tearDown(Scenario scenario) {

		/*
		 * long duration = System.currentTimeMillis() - startTime; String durationStr =
		 * duration / 1000.0 + "s"; StringBuilder steps = new StringBuilder();
		 * scenario.getSourceTagNames().forEach(tag ->
		 * steps.append(tag).append("<br>"));
		 * 
		 * CustomHtmlReportGenerator.logScenario(scenario.getUri().toString(),
		 * scenario.getName(), steps.toString(), !scenario.isFailed(), durationStr);
		 */

		if (scenario.getStatus() == Status.PASSED) {
			passedScenarios++;
		} else if (scenario.getStatus() == Status.FAILED) {
			failedScenarios++;
		}

		logger.info("----- ENDING SCENARIO: " + scenario.getName() + " | Status: " + scenario.getStatus() + " -----");
		logger.info("-------------------------------------------------------------------------------\n");

		long duration = System.currentTimeMillis() - startTime;
		String durationStr = duration / 1000.0 + "s";
		StringBuilder steps = new StringBuilder();
		scenario.getSourceTagNames().forEach(tag -> steps.append(tag).append("<br>"));

		boolean isPassed = !scenario.isFailed();
		String screenshotPath = null;

		if (!isPassed) {
			screenshotPath = ScreenShotUtils.captureScreenshot(driver, scenario.getName());
		}

		CustomHtmlReportGenerator.logScenario(scenario.getUri().toString(), scenario.getName(), steps.toString(),
				isPassed, durationStr, screenshotPath);

		if (driver != null) {
			driver.quit();
		}

		/*
		 * if (scenario.isFailed()) {
		 * 
		 * 
		 * byte[] screenshot = ((TakesScreenshot)
		 * driver).getScreenshotAs(OutputType.BYTES); scenario.attach(screenshot,
		 * "image/png", scenario.getName());
		 * 
		 * }
		 */
		/*
		 * if (driver != null) { driver.quit(); }
		 */
	}

	@AfterAll
	public static void afterAll() {

		LocalDateTime suiteEndTime = LocalDateTime.now();
		Duration duration = Duration.between(suiteStartTime, suiteEndTime);
		long minutes = duration.toMinutes();
		long seconds = duration.minusMinutes(minutes).getSeconds();

		// Color-coded summary for console
		System.out.println("\u001B[34m===== TEST EXECUTION SUMMARY =================\u001B[0m");
		System.out.println("\u001B[36mSuite Tag:\u001B[0m   " + suiteTagName);
		System.out.println("\u001B[36mStart Time:\u001B[0m  " + formatDateTime(suiteStartTime));
		System.out.println("\u001B[36mEnd Time:\u001B[0m    " + formatDateTime(suiteEndTime));
		System.out.println("\u001B[36mDuration:\u001B[0m    " + minutes + " minutes " + seconds + " seconds");

		String scenarioSummary = String.format(
				"Scenarios:   Total: %d | \u001B[32mPassed: %d\u001B[0m | \u001B[31mFailed: %d\u001B[0m",
				totalScenarios, passedScenarios, failedScenarios);
		System.out.println(scenarioSummary);
		System.out.println("\u001B[34m===============================================\u001B[0m\n");

		// Write Clean Version to automation.log (without ANSI colors)
		try (PrintWriter writer = new PrintWriter(new FileWriter("target/logs/automation.log", true))) {
			writer.println(); // extra line between runs
			writer.println("===== TEST EXECUTION SUMMARY ================");
			writer.println("Suite Tag:   " + suiteTagName);
			writer.println("Start Time:  " + suiteStartTime);
			writer.println("End Time:    " + suiteEndTime);
			writer.println("Duration:    " + minutes + " minutes " + seconds + " seconds");
			writer.println("Scenarios:   Total: " + totalScenarios + " | Passed: " + passedScenarios + " | Failed: "
					+ failedScenarios);
			writer.println("==============================================");
		} catch (IOException e) {
			e.printStackTrace();
		}


		CustomHtmlReportGenerator.generateReport();
	}

}
