package com.zywi.ui.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CleanLogsUtil {

    public static void cleanLogs() {
        File logFile = new File("target/logs/automation.log");

        if (logFile.exists()) {
            try {
                FileUtils.forceDelete(logFile);
                System.out.println("Old log file deleted: target/logs/automation.log");
            } catch (IOException e) {
                System.err.println("Failed to delete log file: " + e.getMessage());
            }
        }
    }
}
