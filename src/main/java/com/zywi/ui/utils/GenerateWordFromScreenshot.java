package com.zywi.ui.utils;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class GenerateWordFromScreenshot extends BaseClass {

	public static void generateWordFromScreenshot() {
	    try {
	        // Load screenshot image
	        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	        File screenshotFile = new File("C:/Reports/Cucumber-report-screenshot.png");
	        FileUtils.copyFile(screenshot, screenshotFile);

	        // ✅ Check if the screenshot file exists
	        if (!screenshotFile.exists()) {
	            System.err.println("❌ Screenshot file not found after capture.");
	            return;
	        }

	        // Read automation.log content
	        String logContent = Files.readString(Paths.get("target/logs/automation.log"));
	        String testSummary = extractSummarySection(logContent);

	        // Create Word document
	        XWPFDocument doc = new XWPFDocument();

	        // Add header
	        XWPFParagraph header = doc.createParagraph();
	        header.setAlignment(ParagraphAlignment.CENTER);
	        XWPFRun headerRun = header.createRun();
	        headerRun.setText("📄 Automation Test Execution Report");
	        headerRun.setBold(true);
	        headerRun.setFontSize(16);

	        doc.createParagraph(); // Add spacing

	        // Add test summary
	        XWPFParagraph summaryPara = doc.createParagraph();
	        XWPFRun summaryRun = summaryPara.createRun();
	        summaryRun.setText(testSummary);
	        summaryRun.setFontFamily("Courier New");
	        summaryRun.setFontSize(11);

	        doc.createParagraph(); // Add spacing

	        // Embed image into Word doc
	        BufferedImage image = ImageIO.read(screenshotFile);
	        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
	        ImageIO.write(image, "png", imgBytes);

	        XWPFParagraph imgPara = doc.createParagraph();
	        imgPara.setAlignment(ParagraphAlignment.CENTER);
	        XWPFRun imgRun = imgPara.createRun();
	        imgRun.addPicture(
	            new ByteArrayInputStream(imgBytes.toByteArray()),
	            XWPFDocument.PICTURE_TYPE_PNG,
	            "Cucumber-report.png",
	            Units.toEMU(520),
	            Units.toEMU(420)
	        );

	        // Save docx
	        Files.createDirectories(Paths.get("C:/Reports"));
	        FileOutputStream out = new FileOutputStream("C:/Reports/Cucumber-reports.docx");
	        doc.write(out);
	        out.close();
	        doc.close();

	        System.out.println("✅ Word report created with screenshot and summary.");

	    } catch (Exception e) {
	        System.err.println("❌ Failed to create Word report: " + e.getMessage());
	    }
	}



	public static String extractSummarySection(String logContent) {
		int index = logContent.lastIndexOf("== TEST EXECUTION SUMMARY ==");
		if (index != -1) {
			return logContent.substring(index).trim();
		} else {
			return "❗ Test summary not found in automation.log";
		}
	}
}
