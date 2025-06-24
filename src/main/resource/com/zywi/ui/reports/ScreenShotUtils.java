package com.zywi.ui.reports;



import org.openqa.selenium.*;



import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShotUtils {
    private static final String SCREENSHOT_DIR = "target/screenshots/";

    static {
        new File(SCREENSHOT_DIR).mkdirs();
    }

    public static String captureScreenshot(WebDriver driver, String scenarioName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
        String screenshotPath = SCREENSHOT_DIR + fileName;
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get(screenshotPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return screenshotPath;
    }
    
    public class FileUtils {

        public static void clearFolder(String folderPath) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                for (File file : folder.listFiles()) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }
        }
    }
}
