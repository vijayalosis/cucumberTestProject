package com.zywi.ui.reports;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;



public class CustomHtmlReportGenerator {
	
	
	private static final String REPORT_FOLDER = "target/reports/";
	private static final String LOG_FILE_PATH = "target/logs/automation.log";
	private static final String REPORT_FILE_NAME = "CustomReport.html";
	private static List<String> reportContent = new ArrayList<>();
	private static int scenarioCount = 0;
	private static int passCount = 0;
	private static int failCount = 0;
	private static final String TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

	static {
		new File(REPORT_FOLDER).mkdirs();
	}

	public static void startReport() {
		reportContent.clear();
		reportContent.add("<html><head><title>Automation Test Report</title>");
		reportContent.add("<style> img { border: 1px solid #ccc; border-radius: 4px; }");
		reportContent.add("body { font-family: Arial; background: #f8f9fa; padding: 20px; }");
		reportContent.add("h2 { color: #343a40; }");
		reportContent.add("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
		reportContent.add("th, td { border: 1px solid #dee2e6; padding: 8px; text-align: left; }");
		reportContent.add("th { background-color: #343a40; color: white; }");
		reportContent.add(".pass { background-color: #d4edda; color: #155724; }");
		reportContent.add(".fail { background-color: #f8d7da; color: #721c24; }");
		reportContent.add(".header { background-color: #e2e3e5; font-weight: bold; }");
		reportContent.add("</style></head><body>");
		reportContent.add("<h2>Custom Automation Report</h2>");
		reportContent.add("<p>Generated on: " + TIMESTAMP + "</p>");
		reportContent.add("<table>");
		reportContent.add(
				"<tr><th>S.No</th><th>Feature</th><th>Scenario</th><th>Steps</th><th>Status</th><th>Duration</th></tr>");
	}

	public static void logScenario(String feature, String scenario, String stepsHtml, boolean isPassed,
			String duration, String screenshotPath) {
		scenarioCount++;
		String statusClass = isPassed ? "pass" : "fail";
		String statusText = isPassed ? "Passed" : "Failed";
		if (isPassed)
			passCount++;
		else
			failCount++;
		reportContent.add("<tr>");
		reportContent.add("<td>" + scenarioCount + "</td>");
		reportContent.add("<td>" + feature + "</td>");
		reportContent.add("<td>" + scenario + "</td>");
		reportContent.add("<td>" + stepsHtml + "</td>");
		reportContent.add("<td class=\"" + statusClass + "\">" + (isPassed ? "Passed" : "Failed") + "</td>");
		reportContent.add("<td>" + duration + "</td>");
		reportContent.add("</tr>");
		
		reportContent.add("<tr>");
		reportContent.add("<td>" + scenarioCount + "</td>");
		reportContent.add("<td>" + feature + "</td>");
		reportContent.add("<td>" + scenario + "</td>");
		reportContent.add("<td>" + stepsHtml + "</td>");

		String screenshotTag = (screenshotPath != null)
		        ? "<a href=\"../" + screenshotPath + "\" target=\"_blank\"><img src=\"../" + screenshotPath + "\" width=\"100\" height=\"60\"/></a>"
		        : "N/A";

		reportContent.add("<td class=\"" + statusClass + "\">" + statusText + "<br/>" + screenshotTag + "</td>");
		reportContent.add("<td>" + duration + "</td>");
		reportContent.add("</tr>");

	}
	
	public static void addSummary() {
		reportContent.add("</table>");
		reportContent.add("<h3>Execution Summary</h3>");
		reportContent.add("<ul>");
		reportContent.add("<li>Total Scenarios: " + scenarioCount + "</li>");
		reportContent.add("<li>Passed: " + passCount + "</li>");
		reportContent.add("<li>Failed: " + failCount + "</li>");
		reportContent.add("</ul>");
	}

	public static void addLogs() {
		try {
			reportContent.add("<h3>Execution Logs</h3><pre>");
			List<String> logLines = Files.readAllLines(Paths.get(LOG_FILE_PATH));
			for (String line : logLines) {
				reportContent.add(line);
			}
			reportContent.add("</pre>");
		} catch (IOException e) {
			reportContent.add("<p>Error loading logs.</p>");
		}
	}

	public static void addRecommendations() {
		reportContent.add("<h3>Recommendations</h3><ul>");
		if (failCount > 0) {
			reportContent.add("<li>Investigate the failed scenarios listed above.</li>");
		} else {
			reportContent.add("<li>All scenarios passed successfully. Well done!</li>");
		}
		reportContent.add("<li>Check logs below for detailed debug info.</li>");
		reportContent.add("</ul>");
	}

	public static void generateReport() {
		addSummary();
		addRecommendations();
		addLogs();
		reportContent.add("</body></html>");

		try {
			Files.write(Paths.get(REPORT_FOLDER + REPORT_FILE_NAME), reportContent);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
}
