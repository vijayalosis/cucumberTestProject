package com.zywi.ui.reports;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomHtmlReportWriter {

    private final List<String> scenarioHtmlBlocks = new ArrayList<>();
    private final StringBuilder summaryStats = new StringBuilder();
    private int totalScenarios = 0;
    private int passedScenarios = 0;
    private int failedScenarios = 0;
    private final String timestamp;
    private final String screenshotsDir = "test-output/screenshots";
    private final String logoPath = "logo.png"; // Place logo.png in test-output folder

    public CustomHtmlReportWriter() {
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        cleanScreenshotsFolder();
    }

    public void logScenario(String feature, String scenario, List<String> stepsHtml, boolean isPassed, String screenshotPath) {
        totalScenarios++;
        if (isPassed) passedScenarios++;
        else failedScenarios++;

        String statusColor = isPassed ? "green" : "red";
        StringBuilder stepsBlock = new StringBuilder();

        for (String step : stepsHtml) {
            stepsBlock.append("<li class='step-item'>").append(escapeHtml(step)).append("</li>");
        }

        String screenshotTag = !isPassed && screenshotPath != null
            ? "<div class='screenshot'><img src='" + screenshotPath + "' alt='screenshot'></div>"
            : "";
        
        String scenarioBlock = 
        	    "<div class='scenario-card'>" +
        	        "<div class='scenario-header'>" +
        	            "<h3><span class='label'>Feature:</span> " + escapeHtml(feature) + "</h3>" +
        	            "<h4><span class='label'>Scenario:</span> " + escapeHtml(scenario) + "</h4>" +
        	        "</div>" +
        	        "<ul class='step-list'>" + stepsBlock + "</ul>" +
        	        "<div class='scenario-status' style='color:" + statusColor + ";'>Status: " + (isPassed ? "Passed" : "Failed") + "</div>" +
        	        screenshotTag +
        	    "</div>";

        	scenarioHtmlBlocks.add(scenarioBlock);
       
    }

    public void generateReport() {
        try {
            Path outputDir = Paths.get("test-output");
            if (!Files.exists(outputDir)) Files.createDirectories(outputDir);

            summaryStats.append("<p><strong>Total:</strong> ").append(totalScenarios)
                .append(" | <strong>Passed:</strong> <span style='color:green;'>")
                .append(passedScenarios).append("</span> | <strong>Failed:</strong> <span style='color:red;'>")
                .append(failedScenarios).append("</span></p>");

            String fullReportHtml = Files.readString(Paths.get("src/test/resources/report-template.html"))
                .replace("${LOGO}", logoPath)
                .replace("${SUMMARY}", summaryStats)
                .replace("${REPORT_ENTRIES}", String.join("\n", scenarioHtmlBlocks))
                .replace("${TIMESTAMP}", timestamp);

            BufferedWriter writer = new BufferedWriter(new FileWriter("test-output/CustomTestReport.html"));
            writer.write(fullReportHtml);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanScreenshotsFolder() {
        Path path = Paths.get(screenshotsDir);
        try {
            if (Files.exists(path)) {
                Files.walk(path).map(Path::toFile).forEach(File::delete);
            }
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
