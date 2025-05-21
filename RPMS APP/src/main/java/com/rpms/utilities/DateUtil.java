package com.rpms.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time formatting operations.
 * Provides standardized date formatting across the application.
 */
public class DateUtil {
    /** Standard datetime format used throughout the application */
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Formats a LocalDateTime object using the standard application format.
     * 
     * @param dateTime The LocalDateTime to format
     * @return Formatted date-time string
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}