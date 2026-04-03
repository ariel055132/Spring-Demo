package com.example.foundation.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for LocalDate and LocalDateTime operations.
 * Provides conversion between date/time objects and strings, plus common date operations.
 */
public class LocalDateTimeUtil {

    // Common date format patterns
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTERN_DATETIME_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_DATE_SHORT = "yyyyMMdd";
    public static final String PATTERN_DATETIME_SHORT = "yyyyMMddHHmmss";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_DATETIME_CN = "yyyy年MM月dd日 HH:mm:ss";
    public static final String PATTERN_DATE_CN = "yyyy年MM月dd日";
    
    // ROC (Taiwan Minguo calendar) patterns
    public static final String PATTERN_ROC_DATE = "yyy/MM/dd";  // e.g., 115/04/03
    public static final String PATTERN_ROC_DATE_DASH = "yyy-MM-dd";  // e.g., 115-04-03
    public static final String PATTERN_ROC_DATE_SHORT = "yyyMMdd";  // e.g., 1150403
    public static final String PATTERN_ROC_DATETIME = "yyy/MM/dd HH:mm:ss";  // e.g., 115/04/03 14:30:00
    public static final String PATTERN_ROC_DATETIME_DASH = "yyy-MM-dd HH:mm:ss";  // e.g., 115-04-03 14:30:00
    
    // ROC Calendar constant
    private static final int ROC_BASE_YEAR = 1911;  // ROC year 1 = Gregorian year 1912

    // Common formatters
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);
    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    public static final DateTimeFormatter FORMATTER_DATETIME_ISO = DateTimeFormatter.ofPattern(PATTERN_DATETIME_ISO);
    public static final DateTimeFormatter FORMATTER_DATETIME_MILLIS = DateTimeFormatter.ofPattern(PATTERN_DATETIME_MILLIS);
    public static final DateTimeFormatter FORMATTER_DATE_SHORT = DateTimeFormatter.ofPattern(PATTERN_DATE_SHORT);
    public static final DateTimeFormatter FORMATTER_DATETIME_SHORT = DateTimeFormatter.ofPattern(PATTERN_DATETIME_SHORT);
    public static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(PATTERN_TIME);
    
    // ROC formatters (Note: These use 'yyy' for 3-digit year, not standard Gregorian 'yyyy')
    public static final DateTimeFormatter FORMATTER_ROC_DATE = DateTimeFormatter.ofPattern(PATTERN_ROC_DATE);
    public static final DateTimeFormatter FORMATTER_ROC_DATE_DASH = DateTimeFormatter.ofPattern(PATTERN_ROC_DATE_DASH);
    public static final DateTimeFormatter FORMATTER_ROC_DATE_SHORT = DateTimeFormatter.ofPattern(PATTERN_ROC_DATE_SHORT);
    public static final DateTimeFormatter FORMATTER_ROC_DATETIME = DateTimeFormatter.ofPattern(PATTERN_ROC_DATETIME);
    public static final DateTimeFormatter FORMATTER_ROC_DATETIME_DASH = DateTimeFormatter.ofPattern(PATTERN_ROC_DATETIME_DASH);

    private LocalDateTimeUtil() {
        // Private constructor to prevent instantiation
    }

    // ========== Current Date/Time Methods ==========

    /**
     * Get current LocalDate
     */
    public static LocalDate now() {
        return LocalDate.now();
    }

    /**
     * Get current LocalDateTime
     */
    public static LocalDateTime nowTime() {
        return LocalDateTime.now();
    }

    /**
     * Get current date as string (yyyy-MM-dd)
     */
    public static String nowDateStr() {
        return LocalDate.now().format(FORMATTER_DATE);
    }

    /**
     * Get current datetime as string (yyyy-MM-dd HH:mm:ss)
     */
    public static String nowDateTimeStr() {
        return LocalDateTime.now().format(FORMATTER_DATETIME);
    }

    // ========== LocalDate to String Conversion ==========

    /**
     * Convert LocalDate to string with default pattern (yyyy-MM-dd)
     */
    public static String dateToStr(LocalDate date) {
        return date == null ? null : date.format(FORMATTER_DATE);
    }

    /**
     * Convert LocalDate to string with custom pattern
     */
    public static String dateToStr(LocalDate date, String pattern) {
        if (date == null || StringUtil.isBlank(pattern)) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Convert LocalDate to string with custom formatter
     */
    public static String dateToStr(LocalDate date, DateTimeFormatter formatter) {
        return date == null || formatter == null ? null : date.format(formatter);
    }

    // ========== LocalDateTime to String Conversion ==========

    /**
     * Convert LocalDateTime to string with default pattern (yyyy-MM-dd HH:mm:ss)
     */
    public static String dateTimeToStr(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(FORMATTER_DATETIME);
    }

    /**
     * Convert LocalDateTime to string with custom pattern
     */
    public static String dateTimeToStr(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || StringUtil.isBlank(pattern)) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Convert LocalDateTime to string with custom formatter
     */
    public static String dateTimeToStr(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime == null || formatter == null ? null : dateTime.format(formatter);
    }

    // ========== String to LocalDate Conversion ==========

    /**
     * Parse string to LocalDate with default pattern (yyyy-MM-dd)
     */
    public static LocalDate strToDate(String dateStr) {
        if (StringUtil.isBlank(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, FORMATTER_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse string to LocalDate with custom pattern
     */
    public static LocalDate strToDate(String dateStr, String pattern) {
        if (StringUtil.isBlank(dateStr) || StringUtil.isBlank(pattern)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse string to LocalDate with custom formatter
     */
    public static LocalDate strToDate(String dateStr, DateTimeFormatter formatter) {
        if (StringUtil.isBlank(dateStr) || formatter == null) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ========== String to LocalDateTime Conversion ==========

    /**
     * Parse string to LocalDateTime with default pattern (yyyy-MM-dd HH:mm:ss)
     */
    public static LocalDateTime strToDateTime(String dateTimeStr) {
        if (StringUtil.isBlank(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, FORMATTER_DATETIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse string to LocalDateTime with custom pattern
     */
    public static LocalDateTime strToDateTime(String dateTimeStr, String pattern) {
        if (StringUtil.isBlank(dateTimeStr) || StringUtil.isBlank(pattern)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse string to LocalDateTime with custom formatter
     */
    public static LocalDateTime strToDateTime(String dateTimeStr, DateTimeFormatter formatter) {
        if (StringUtil.isBlank(dateTimeStr) || formatter == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ========== LocalDate and LocalDateTime Conversion ==========

    /**
     * Convert LocalDate to LocalDateTime (at start of day 00:00:00)
     */
    public static LocalDateTime dateToDateTime(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    /**
     * Convert LocalDate to LocalDateTime with specific time
     */
    public static LocalDateTime dateToDateTime(LocalDate date, int hour, int minute, int second) {
        return date == null ? null : date.atTime(hour, minute, second);
    }

    /**
     * Convert LocalDate to LocalDateTime with LocalTime
     */
    public static LocalDateTime dateToDateTime(LocalDate date, LocalTime time) {
        return date == null || time == null ? null : LocalDateTime.of(date, time);
    }

    /**
     * Convert LocalDateTime to LocalDate
     */
    public static LocalDate dateTimeToDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    // ========== Date Calculation Methods ==========

    /**
     * Add days to LocalDate
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date == null ? null : date.plusDays(days);
    }

    /**
     * Add days to LocalDateTime
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime == null ? null : dateTime.plusDays(days);
    }

    /**
     * Add months to LocalDate
     */
    public static LocalDate plusMonths(LocalDate date, long months) {
        return date == null ? null : date.plusMonths(months);
    }

    /**
     * Add months to LocalDateTime
     */
    public static LocalDateTime plusMonths(LocalDateTime dateTime, long months) {
        return dateTime == null ? null : dateTime.plusMonths(months);
    }

    /**
     * Add years to LocalDate
     */
    public static LocalDate plusYears(LocalDate date, long years) {
        return date == null ? null : date.plusYears(years);
    }

    /**
     * Add years to LocalDateTime
     */
    public static LocalDateTime plusYears(LocalDateTime dateTime, long years) {
        return dateTime == null ? null : dateTime.plusYears(years);
    }

    /**
     * Add hours to LocalDateTime
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        return dateTime == null ? null : dateTime.plusHours(hours);
    }

    /**
     * Add minutes to LocalDateTime
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime == null ? null : dateTime.plusMinutes(minutes);
    }

    /**
     * Add seconds to LocalDateTime
     */
    public static LocalDateTime plusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime == null ? null : dateTime.plusSeconds(seconds);
    }

    /**
     * Subtract days from LocalDate
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        return date == null ? null : date.minusDays(days);
    }

    /**
     * Subtract days from LocalDateTime
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime == null ? null : dateTime.minusDays(days);
    }

    // ========== Date Comparison Methods ==========

    /**
     * Check if date1 is before date2
     */
    public static boolean isBefore(LocalDate date1, LocalDate date2) {
        return date1 != null && date2 != null && date1.isBefore(date2);
    }

    /**
     * Check if dateTime1 is before dateTime2
     */
    public static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1 != null && dateTime2 != null && dateTime1.isBefore(dateTime2);
    }

    /**
     * Check if date1 is after date2
     */
    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        return date1 != null && date2 != null && date1.isAfter(date2);
    }

    /**
     * Check if dateTime1 is after dateTime2
     */
    public static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1 != null && dateTime2 != null && dateTime1.isAfter(dateTime2);
    }

    /**
     * Calculate days between two dates
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate days between two datetimes
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate hours between two datetimes
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calculate minutes between two datetimes
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculate seconds between two datetimes
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(start, end);
    }

    // ========== Timestamp Conversion ==========

    /**
     * Convert timestamp (milliseconds) to LocalDateTime
     */
    public static LocalDateTime timestampToDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * Convert timestamp (milliseconds) to LocalDate
     */
    public static LocalDate timestampToDate(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Convert LocalDateTime to timestamp (milliseconds)
     */
    public static long dateTimeToTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Convert LocalDate to timestamp (milliseconds) at start of day
     */
    public static long dateToTimestamp(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // ========== Utility Methods ==========

    /**
     * Get start of day for a LocalDate
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    /**
     * Get end of day for a LocalDate (23:59:59.999999999)
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.atTime(LocalTime.MAX);
    }

    /**
     * Check if a date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Check if a datetime is today
     */
    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime != null && dateTime.toLocalDate().equals(LocalDate.now());
    }

    /**
     * Get the first day of the month for a given date
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date == null ? null : date.withDayOfMonth(1);
    }

    /**
     * Get the last day of the month for a given date
     */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date == null ? null : date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * Format string from one pattern to another
     */
    public static String reformatDateStr(String dateStr, String fromPattern, String toPattern) {
        if (StringUtil.isBlank(dateStr) || StringUtil.isBlank(fromPattern) || StringUtil.isBlank(toPattern)) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(fromPattern));
            return date.format(DateTimeFormatter.ofPattern(toPattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Format datetime string from one pattern to another
     */
    public static String reformatDateTimeStr(String dateTimeStr, String fromPattern, String toPattern) {
        if (StringUtil.isBlank(dateTimeStr) || StringUtil.isBlank(fromPattern) || StringUtil.isBlank(toPattern)) {
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(fromPattern));
            return dateTime.format(DateTimeFormatter.ofPattern(toPattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ========== ROC (Taiwan Minguo Calendar) Conversion ==========

    /**
     * Convert Gregorian year to ROC year
     * @param gregorianYear Gregorian year (e.g., 2026)
     * @return ROC year (e.g., 115)
     */
    public static int gregorianToRocYear(int gregorianYear) {
        return gregorianYear - ROC_BASE_YEAR;
    }

    /**
     * Convert ROC year to Gregorian year
     * @param rocYear ROC year (e.g., 115)
     * @return Gregorian year (e.g., 2026)
     */
    public static int rocToGregorianYear(int rocYear) {
        return rocYear + ROC_BASE_YEAR;
    }

    /**
     * Convert LocalDate to ROC date string with default pattern (yyy/MM/dd)
     * @param date LocalDate to convert
     * @return ROC date string (e.g., "115/04/03")
     */
    public static String dateToRocStr(LocalDate date) {
        return dateToRocStr(date, PATTERN_ROC_DATE);
    }

    /**
     * Convert LocalDate to ROC date string with custom pattern
     * @param date LocalDate to convert
     * @param pattern ROC date pattern (use 'yyy' for ROC year)
     * @return ROC date string
     */
    public static String dateToRocStr(LocalDate date, String pattern) {
        if (date == null || StringUtil.isBlank(pattern)) {
            return null;
        }
        try {
            int rocYear = gregorianToRocYear(date.getYear());
            String rocYearStr = String.valueOf(rocYear);
            
            // Replace the year part with ROC year
            String result = pattern.replace("yyy", "%03d");
            result = String.format(result, rocYear);
            
            // Format the rest (month, day)
            String monthDay = date.format(DateTimeFormatter.ofPattern(pattern.replace("yyy", "yyyy")));
            // Replace year in formatted string with ROC year
            result = monthDay.replaceFirst("\\d{4}", rocYearStr);
            
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert LocalDateTime to ROC datetime string with default pattern (yyy/MM/dd HH:mm:ss)
     * @param dateTime LocalDateTime to convert
     * @return ROC datetime string (e.g., "115/04/03 14:30:00")
     */
    public static String dateTimeToRocStr(LocalDateTime dateTime) {
        return dateTimeToRocStr(dateTime, PATTERN_ROC_DATETIME);
    }

    /**
     * Convert LocalDateTime to ROC datetime string with custom pattern
     * @param dateTime LocalDateTime to convert
     * @param pattern ROC datetime pattern (use 'yyy' for ROC year)
     * @return ROC datetime string
     */
    public static String dateTimeToRocStr(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || StringUtil.isBlank(pattern)) {
            return null;
        }
        try {
            int rocYear = gregorianToRocYear(dateTime.getYear());
            String rocYearStr = String.valueOf(rocYear);
            
            // Format with Gregorian year first
            String formatted = dateTime.format(DateTimeFormatter.ofPattern(pattern.replace("yyy", "yyyy")));
            // Replace year with ROC year
            return formatted.replaceFirst("\\d{4}", rocYearStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse ROC date string to LocalDate with default pattern (yyy/MM/dd)
     * @param rocDateStr ROC date string (e.g., "115/04/03")
     * @return LocalDate
     */
    public static LocalDate rocStrToDate(String rocDateStr) {
        return rocStrToDate(rocDateStr, PATTERN_ROC_DATE);
    }

    /**
     * Parse ROC date string to LocalDate with custom pattern
     * @param rocDateStr ROC date string
     * @param pattern ROC date pattern (use 'yyy' for ROC year)
     * @return LocalDate
     */
    public static LocalDate rocStrToDate(String rocDateStr, String pattern) {
        if (StringUtil.isBlank(rocDateStr) || StringUtil.isBlank(pattern)) {
            return null;
        }
        try {
            // Extract ROC year from the string
            String[] parts = rocDateStr.split("[/-]");
            if (parts.length < 3) {
                // Try short format without separators
                if (rocDateStr.length() >= 7) {
                    int rocYear = Integer.parseInt(rocDateStr.substring(0, 3));
                    int month = Integer.parseInt(rocDateStr.substring(3, 5));
                    int day = Integer.parseInt(rocDateStr.substring(5, 7));
                    int gregorianYear = rocToGregorianYear(rocYear);
                    return LocalDate.of(gregorianYear, month, day);
                }
                return null;
            }
            
            int rocYear = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            
            int gregorianYear = rocToGregorianYear(rocYear);
            return LocalDate.of(gregorianYear, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse ROC datetime string to LocalDateTime with default pattern (yyy/MM/dd HH:mm:ss)
     * @param rocDateTimeStr ROC datetime string (e.g., "115/04/03 14:30:00")
     * @return LocalDateTime
     */
    public static LocalDateTime rocStrToDateTime(String rocDateTimeStr) {
        return rocStrToDateTime(rocDateTimeStr, PATTERN_ROC_DATETIME);
    }

    /**
     * Parse ROC datetime string to LocalDateTime with custom pattern
     * @param rocDateTimeStr ROC datetime string
     * @param pattern ROC datetime pattern (use 'yyy' for ROC year)
     * @return LocalDateTime
     */
    public static LocalDateTime rocStrToDateTime(String rocDateTimeStr, String pattern) {
        if (StringUtil.isBlank(rocDateTimeStr) || StringUtil.isBlank(pattern)) {
            return null;
        }
        try {
            // Split date and time parts
            String[] dateTimeParts = rocDateTimeStr.split(" ");
            if (dateTimeParts.length < 2) {
                return null;
            }
            
            String datePart = dateTimeParts[0];
            String timePart = dateTimeParts[1];
            
            // Extract date components
            String[] dateParts = datePart.split("[/-]");
            if (dateParts.length < 3) {
                return null;
            }
            
            int rocYear = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            
            // Extract time components
            String[] timeParts = timePart.split(":");
            if (timeParts.length < 3) {
                return null;
            }
            
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            int second = Integer.parseInt(timeParts[2]);
            
            int gregorianYear = rocToGregorianYear(rocYear);
            return LocalDateTime.of(gregorianYear, month, day, hour, minute, second);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert ROC date string from one pattern to another
     * @param rocDateStr ROC date string
     * @param fromPattern Source ROC pattern
     * @param toPattern Target ROC pattern
     * @return Reformatted ROC date string
     */
    public static String reformatRocDateStr(String rocDateStr, String fromPattern, String toPattern) {
        LocalDate date = rocStrToDate(rocDateStr, fromPattern);
        return dateToRocStr(date, toPattern);
    }

    /**
     * Get current date as ROC string with default pattern (yyy/MM/dd)
     * @return Current ROC date string (e.g., "115/04/03")
     */
    public static String nowRocDateStr() {
        return dateToRocStr(LocalDate.now());
    }

    /**
     * Get current datetime as ROC string with default pattern (yyy/MM/dd HH:mm:ss)
     * @return Current ROC datetime string (e.g., "115/04/03 14:30:00")
     */
    public static String nowRocDateTimeStr() {
        return dateTimeToRocStr(LocalDateTime.now());
    }
}