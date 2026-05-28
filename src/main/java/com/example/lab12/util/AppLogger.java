package com.example.lab12.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AppLogger {
    private static boolean configured = false;

    public static void setup() {
        if (configured) {
            return;
        }

        try {
            Files.createDirectories(Path.of("logs"));

            Logger rootLogger = Logger.getLogger("");

            for (var handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            Formatter formatter = createFormatter();

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(formatter);

            FileHandler fileHandler = new FileHandler("logs/Lab12.log", true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(formatter);

            rootLogger.addHandler(consoleHandler);
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);

            configured = true;
            Logger.getLogger(AppLogger.class.getName()).info("Logging is enabled");
        } catch (IOException e) {
            System.err.println("Cannot create log file: " + e.getMessage());
        }
    }

    private static Formatter createFormatter() {
        return new Formatter() {
            private final DateTimeFormatter dateFormat =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord record) {
                LocalDateTime time = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(record.getMillis()),
                        ZoneId.systemDefault()
                );

                return dateFormat.format(time)
                        + " "
                        + record.getLevel()
                        + " "
                        + record.getLoggerName()
                        + " - "
                        + formatMessage(record)
                        + System.lineSeparator();
            }
        };
    }
}