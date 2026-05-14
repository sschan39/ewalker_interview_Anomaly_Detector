package com.ewalker.anomaly.server;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SimpleLogger {
    private final String logFile;

    public SimpleLogger(String logFile) {
        this.logFile = logFile;
    }

    public void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = "[" + timestamp + "] " + message;
        
        System.out.println(logMessage);
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logMessage + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public void error(String message, Exception e) {
        log("ERROR: " + message + " - " + e.getMessage());
    }
}
