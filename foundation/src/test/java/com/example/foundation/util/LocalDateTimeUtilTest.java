package com.example.foundation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LocalDateTimeUtil Test")
class LocalDateTimeUtilTest {

    // ========== Current Date/Time Tests ==========

    @Test
    @DisplayName("now returns current LocalDate")
    void testNow() {
        LocalDate now = LocalDateTimeUtil.now();
        assertNotNull(now);
        assertEquals(LocalDate.now(), now);
    }

    @Test
    @DisplayName("nowTime returns current LocalDateTime")
    void testNowTime() {
        LocalDateTime nowTime = LocalDateTimeUtil.nowTime();
        assertNotNull(nowTime);
    }

    @Test
    @DisplayName("nowDateStr returns current date string")
    void testNowDateStr() {
        String nowStr = LocalDateTimeUtil.nowDateStr();
        assertNotNull(nowStr);
        assertTrue(nowStr.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    @DisplayName("nowDateTimeStr returns current datetime string")
    void testNowDateTimeStr() {
        String nowStr = LocalDateTimeUtil.nowDateTimeStr();
        assertNotNull(nowStr);
        assertTrue(nowStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    // ========== LocalDate to String Conversion Tests ==========

    @Test
    @DisplayName("dateToStr converts LocalDate to string with default format")
    void testDateToStr() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        assertEquals("2026-04-03", LocalDateTimeUtil.dateToStr(date));
        assertNull(LocalDateTimeUtil.dateToStr(null));
    }

    @Test
    @DisplayName("dateToStr converts LocalDate to string with custom pattern")
    void testDateToStrWithPattern() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        assertEquals("20260403", LocalDateTimeUtil.dateToStr(date, "yyyyMMdd"));
        assertEquals("2026/04/03", LocalDateTimeUtil.dateToStr(date, "yyyy/MM/dd"));
        assertNull(LocalDateTimeUtil.dateToStr(null, "yyyy-MM-dd"));
        assertNull(LocalDateTimeUtil.dateToStr(date, (String) null));
    }

    @Test
    @DisplayName("dateToStr converts LocalDate to string with custom formatter")
    void testDateToStrWithFormatter() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        assertEquals("20260403", LocalDateTimeUtil.dateToStr(date, LocalDateTimeUtil.FORMATTER_DATE_SHORT));
        assertNull(LocalDateTimeUtil.dateToStr(null, LocalDateTimeUtil.FORMATTER_DATE));
        assertNull(LocalDateTimeUtil.dateToStr(date, (java.time.format.DateTimeFormatter) null));
    }

    // ========== LocalDateTime to String Conversion Tests ==========

    @Test
    @DisplayName("dateTimeToStr converts LocalDateTime to string with default format")
    void testDateTimeToStr() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals("2026-04-03 14:30:45", LocalDateTimeUtil.dateTimeToStr(dateTime));
        assertNull(LocalDateTimeUtil.dateTimeToStr(null));
    }

    @Test
    @DisplayName("dateTimeToStr converts LocalDateTime to string with custom pattern")
    void testDateTimeToStrWithPattern() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals("20260403143045", LocalDateTimeUtil.dateTimeToStr(dateTime, "yyyyMMddHHmmss"));
        assertEquals("2026-04-03T14:30:45", LocalDateTimeUtil.dateTimeToStr(dateTime, "yyyy-MM-dd'T'HH:mm:ss"));
        assertNull(LocalDateTimeUtil.dateTimeToStr(null, "yyyy-MM-dd HH:mm:ss"));
        assertNull(LocalDateTimeUtil.dateTimeToStr(dateTime, (String) null));
    }

    @Test
    @DisplayName("dateTimeToStr converts LocalDateTime to string with custom formatter")
    void testDateTimeToStrWithFormatter() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals("20260403143045", LocalDateTimeUtil.dateTimeToStr(dateTime, LocalDateTimeUtil.FORMATTER_DATETIME_SHORT));
        assertNull(LocalDateTimeUtil.dateTimeToStr(null, LocalDateTimeUtil.FORMATTER_DATETIME));
        assertNull(LocalDateTimeUtil.dateTimeToStr(dateTime, (java.time.format.DateTimeFormatter) null));
    }

    // ========== String to LocalDate Conversion Tests ==========

    @Test
    @DisplayName("strToDate converts string to LocalDate with default format")
    void testStrToDate() {
        assertEquals(LocalDate.of(2026, 4, 3), LocalDateTimeUtil.strToDate("2026-04-03"));
        assertNull(LocalDateTimeUtil.strToDate(null));
        assertNull(LocalDateTimeUtil.strToDate(""));
        assertNull(LocalDateTimeUtil.strToDate("invalid"));
    }

    @Test
    @DisplayName("strToDate converts string to LocalDate with custom pattern")
    void testStrToDateWithPattern() {
        assertEquals(LocalDate.of(2026, 4, 3), LocalDateTimeUtil.strToDate("20260403", "yyyyMMdd"));
        assertEquals(LocalDate.of(2026, 4, 3), LocalDateTimeUtil.strToDate("2026/04/03", "yyyy/MM/dd"));
        assertNull(LocalDateTimeUtil.strToDate(null, "yyyy-MM-dd"));
        assertNull(LocalDateTimeUtil.strToDate("2026-04-03", (String) null));
        assertNull(LocalDateTimeUtil.strToDate("invalid", "yyyy-MM-dd"));
    }

    @Test
    @DisplayName("strToDate converts string to LocalDate with custom formatter")
    void testStrToDateWithFormatter() {
        assertEquals(LocalDate.of(2026, 4, 3), LocalDateTimeUtil.strToDate("20260403", LocalDateTimeUtil.FORMATTER_DATE_SHORT));
        assertNull(LocalDateTimeUtil.strToDate(null, LocalDateTimeUtil.FORMATTER_DATE));
        assertNull(LocalDateTimeUtil.strToDate("2026-04-03", (java.time.format.DateTimeFormatter) null));
        assertNull(LocalDateTimeUtil.strToDate("invalid", LocalDateTimeUtil.FORMATTER_DATE));
    }

    // ========== String to LocalDateTime Conversion Tests ==========

    @Test
    @DisplayName("strToDateTime converts string to LocalDateTime with default format")
    void testStrToDateTime() {
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 45), 
                     LocalDateTimeUtil.strToDateTime("2026-04-03 14:30:45"));
        assertNull(LocalDateTimeUtil.strToDateTime(null));
        assertNull(LocalDateTimeUtil.strToDateTime(""));
        assertNull(LocalDateTimeUtil.strToDateTime("invalid"));
    }

    @Test
    @DisplayName("strToDateTime converts string to LocalDateTime with custom pattern")
    void testStrToDateTimeWithPattern() {
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 45), 
                     LocalDateTimeUtil.strToDateTime("20260403143045", "yyyyMMddHHmmss"));
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 45), 
                     LocalDateTimeUtil.strToDateTime("2026-04-03T14:30:45", "yyyy-MM-dd'T'HH:mm:ss"));
        assertNull(LocalDateTimeUtil.strToDateTime(null, "yyyy-MM-dd HH:mm:ss"));
        assertNull(LocalDateTimeUtil.strToDateTime("2026-04-03 14:30:45", (String) null));
        assertNull(LocalDateTimeUtil.strToDateTime("invalid", "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    @DisplayName("strToDateTime converts string to LocalDateTime with custom formatter")
    void testStrToDateTimeWithFormatter() {
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 45), 
                     LocalDateTimeUtil.strToDateTime("20260403143045", LocalDateTimeUtil.FORMATTER_DATETIME_SHORT));
        assertNull(LocalDateTimeUtil.strToDateTime(null, LocalDateTimeUtil.FORMATTER_DATETIME));
        assertNull(LocalDateTimeUtil.strToDateTime("2026-04-03 14:30:45", (java.time.format.DateTimeFormatter) null));
        assertNull(LocalDateTimeUtil.strToDateTime("invalid", LocalDateTimeUtil.FORMATTER_DATETIME));
    }

    // ========== LocalDate and LocalDateTime Conversion Tests ==========

    @Test
    @DisplayName("dateToDateTime converts LocalDate to LocalDateTime at start of day")
    void testDateToDateTime() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        LocalDateTime expected = LocalDateTime.of(2026, 4, 3, 0, 0, 0);
        assertEquals(expected, LocalDateTimeUtil.dateToDateTime(date));
        assertNull(LocalDateTimeUtil.dateToDateTime(null));
    }

    @Test
    @DisplayName("dateToDateTime converts LocalDate to LocalDateTime with specific time")
    void testDateToDateTimeWithTime() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        LocalDateTime expected = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(expected, LocalDateTimeUtil.dateToDateTime(date, 14, 30, 45));
        assertNull(LocalDateTimeUtil.dateToDateTime(null, 14, 30, 45));
    }

    @Test
    @DisplayName("dateToDateTime converts LocalDate to LocalDateTime with LocalTime")
    void testDateToDateTimeWithLocalTime() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        LocalTime time = LocalTime.of(14, 30, 45);
        LocalDateTime expected = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(expected, LocalDateTimeUtil.dateToDateTime(date, time));
        assertNull(LocalDateTimeUtil.dateToDateTime(null, time));
        assertNull(LocalDateTimeUtil.dateToDateTime(date, null));
    }

    @Test
    @DisplayName("dateTimeToDate converts LocalDateTime to LocalDate")
    void testDateTimeToDate() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDate expected = LocalDate.of(2026, 4, 3);
        assertEquals(expected, LocalDateTimeUtil.dateTimeToDate(dateTime));
        assertNull(LocalDateTimeUtil.dateTimeToDate(null));
    }

    // ========== Date Calculation Tests ==========

    @Test
    @DisplayName("plusDays adds days to LocalDate")
    void testPlusDaysForDate() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        assertEquals(LocalDate.of(2026, 4, 8), LocalDateTimeUtil.plusDays(date, 5));
        assertEquals(LocalDate.of(2026, 4, 1), LocalDateTimeUtil.plusDays(date, -2));
        assertNull(LocalDateTimeUtil.plusDays((LocalDate) null, 5));
    }

    @Test
    @DisplayName("plusDays adds days to LocalDateTime")
    void testPlusDaysForDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(LocalDateTime.of(2026, 4, 8, 14, 30, 45), LocalDateTimeUtil.plusDays(dateTime, 5));
        assertNull(LocalDateTimeUtil.plusDays((LocalDateTime) null, 5));
    }

    @Test
    @DisplayName("plusMonths adds months to LocalDate")
    void testPlusMonthsForDate() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        assertEquals(LocalDate.of(2026, 7, 3), LocalDateTimeUtil.plusMonths(date, 3));
        assertNull(LocalDateTimeUtil.plusMonths((LocalDate) null, 3));
    }

    @Test
    @DisplayName("plusMonths adds months to LocalDateTime")
    void testPlusMonthsForDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(LocalDateTime.of(2026, 7, 3, 14, 30, 45), LocalDateTimeUtil.plusMonths(dateTime, 3));
        assertNull(LocalDateTimeUtil.plusMonths((LocalDateTime) null, 3));
    }

    @Test
    @DisplayName("plusYears adds years to LocalDate")
    void testPlusYearsForDate() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        assertEquals(LocalDate.of(2028, 4, 3), LocalDateTimeUtil.plusYears(date, 2));
        assertNull(LocalDateTimeUtil.plusYears((LocalDate) null, 2));
    }

    @Test
    @DisplayName("plusYears adds years to LocalDateTime")
    void testPlusYearsForDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(LocalDateTime.of(2028, 4, 3, 14, 30, 45), LocalDateTimeUtil.plusYears(dateTime, 2));
        assertNull(LocalDateTimeUtil.plusYears((LocalDateTime) null, 2));
    }

    @Test
    @DisplayName("plusHours adds hours to LocalDateTime")
    void testPlusHours() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(LocalDateTime.of(2026, 4, 3, 17, 30, 45), LocalDateTimeUtil.plusHours(dateTime, 3));
        assertNull(LocalDateTimeUtil.plusHours(null, 3));
    }

    @Test
    @DisplayName("plusMinutes adds minutes to LocalDateTime")
    void testPlusMinutes() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 45, 45), LocalDateTimeUtil.plusMinutes(dateTime, 15));
        assertNull(LocalDateTimeUtil.plusMinutes(null, 15));
    }

    @Test
    @DisplayName("plusSeconds adds seconds to LocalDateTime")
    void testPlusSeconds() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 31, 15), LocalDateTimeUtil.plusSeconds(dateTime, 30));
        assertNull(LocalDateTimeUtil.plusSeconds(null, 30));
    }

    @Test
    @DisplayName("minusDays subtracts days from LocalDate")
    void testMinusDaysForDate() {
        LocalDate date = LocalDate.of(2026, 4, 8);
        assertEquals(LocalDate.of(2026, 4, 3), LocalDateTimeUtil.minusDays(date, 5));
        assertNull(LocalDateTimeUtil.minusDays((LocalDate) null, 5));
    }

    @Test
    @DisplayName("minusDays subtracts days from LocalDateTime")
    void testMinusDaysForDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 8, 14, 30, 45);
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 45), LocalDateTimeUtil.minusDays(dateTime, 5));
        assertNull(LocalDateTimeUtil.minusDays((LocalDateTime) null, 5));
    }

    // ========== Date Comparison Tests ==========

    @Test
    @DisplayName("isBefore checks if date1 is before date2")
    void testIsBeforeForDate() {
        LocalDate date1 = LocalDate.of(2026, 4, 3);
        LocalDate date2 = LocalDate.of(2026, 4, 8);
        assertTrue(LocalDateTimeUtil.isBefore(date1, date2));
        assertFalse(LocalDateTimeUtil.isBefore(date2, date1));
        assertFalse(LocalDateTimeUtil.isBefore(date1, date1));
        assertFalse(LocalDateTimeUtil.isBefore(null, date2));
        assertFalse(LocalDateTimeUtil.isBefore(date1, null));
    }

    @Test
    @DisplayName("isBefore checks if dateTime1 is before dateTime2")
    void testIsBeforeForDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDateTime dt2 = LocalDateTime.of(2026, 4, 8, 14, 30, 45);
        assertTrue(LocalDateTimeUtil.isBefore(dt1, dt2));
        assertFalse(LocalDateTimeUtil.isBefore(dt2, dt1));
        assertFalse(LocalDateTimeUtil.isBefore(dt1, dt1));
        assertFalse(LocalDateTimeUtil.isBefore(null, dt2));
        assertFalse(LocalDateTimeUtil.isBefore(dt1, null));
    }

    @Test
    @DisplayName("isAfter checks if date1 is after date2")
    void testIsAfterForDate() {
        LocalDate date1 = LocalDate.of(2026, 4, 3);
        LocalDate date2 = LocalDate.of(2026, 4, 8);
        assertFalse(LocalDateTimeUtil.isAfter(date1, date2));
        assertTrue(LocalDateTimeUtil.isAfter(date2, date1));
        assertFalse(LocalDateTimeUtil.isAfter(date1, date1));
        assertFalse(LocalDateTimeUtil.isAfter(null, date2));
        assertFalse(LocalDateTimeUtil.isAfter(date1, null));
    }

    @Test
    @DisplayName("isAfter checks if dateTime1 is after dateTime2")
    void testIsAfterForDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDateTime dt2 = LocalDateTime.of(2026, 4, 8, 14, 30, 45);
        assertFalse(LocalDateTimeUtil.isAfter(dt1, dt2));
        assertTrue(LocalDateTimeUtil.isAfter(dt2, dt1));
        assertFalse(LocalDateTimeUtil.isAfter(dt1, dt1));
        assertFalse(LocalDateTimeUtil.isAfter(null, dt2));
        assertFalse(LocalDateTimeUtil.isAfter(dt1, null));
    }

    @Test
    @DisplayName("daysBetween calculates days between two dates")
    void testDaysBetweenForDate() {
        LocalDate date1 = LocalDate.of(2026, 4, 3);
        LocalDate date2 = LocalDate.of(2026, 4, 8);
        assertEquals(5, LocalDateTimeUtil.daysBetween(date1, date2));
        assertEquals(-5, LocalDateTimeUtil.daysBetween(date2, date1));
        assertEquals(0, LocalDateTimeUtil.daysBetween(date1, date1));
        assertEquals(0, LocalDateTimeUtil.daysBetween(null, date2));
        assertEquals(0, LocalDateTimeUtil.daysBetween(date1, null));
    }

    @Test
    @DisplayName("daysBetween calculates days between two datetimes")
    void testDaysBetweenForDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDateTime dt2 = LocalDateTime.of(2026, 4, 8, 14, 30, 45);
        assertEquals(5, LocalDateTimeUtil.daysBetween(dt1, dt2));
        assertEquals(-5, LocalDateTimeUtil.daysBetween(dt2, dt1));
        assertEquals(0, LocalDateTimeUtil.daysBetween(null, dt2));
    }

    @Test
    @DisplayName("hoursBetween calculates hours between two datetimes")
    void testHoursBetween() {
        LocalDateTime dt1 = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDateTime dt2 = LocalDateTime.of(2026, 4, 3, 17, 30, 45);
        assertEquals(3, LocalDateTimeUtil.hoursBetween(dt1, dt2));
        assertEquals(-3, LocalDateTimeUtil.hoursBetween(dt2, dt1));
        assertEquals(0, LocalDateTimeUtil.hoursBetween(null, dt2));
        assertEquals(0, LocalDateTimeUtil.hoursBetween(dt1, null));
    }

    @Test
    @DisplayName("minutesBetween calculates minutes between two datetimes")
    void testMinutesBetween() {
        LocalDateTime dt1 = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDateTime dt2 = LocalDateTime.of(2026, 4, 3, 14, 45, 45);
        assertEquals(15, LocalDateTimeUtil.minutesBetween(dt1, dt2));
        assertEquals(-15, LocalDateTimeUtil.minutesBetween(dt2, dt1));
        assertEquals(0, LocalDateTimeUtil.minutesBetween(null, dt2));
        assertEquals(0, LocalDateTimeUtil.minutesBetween(dt1, null));
    }

    @Test
    @DisplayName("secondsBetween calculates seconds between two datetimes")
    void testSecondsBetween() {
        LocalDateTime dt1 = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        LocalDateTime dt2 = LocalDateTime.of(2026, 4, 3, 14, 31, 15);
        assertEquals(30, LocalDateTimeUtil.secondsBetween(dt1, dt2));
        assertEquals(-30, LocalDateTimeUtil.secondsBetween(dt2, dt1));
        assertEquals(0, LocalDateTimeUtil.secondsBetween(null, dt2));
        assertEquals(0, LocalDateTimeUtil.secondsBetween(dt1, null));
    }

    // ========== Timestamp Conversion Tests ==========

    @Test
    @DisplayName("timestampToDateTime converts timestamp to LocalDateTime")
    void testTimestampToDateTime() {
        long timestamp = 1712130645000L; // 2024-04-03 14:30:45
        LocalDateTime result = LocalDateTimeUtil.timestampToDateTime(timestamp);
        assertNotNull(result);
        assertEquals(2024, result.getYear());
        assertEquals(4, result.getMonthValue());
        assertEquals(3, result.getDayOfMonth());
    }

    @Test
    @DisplayName("timestampToDate converts timestamp to LocalDate")
    void testTimestampToDate() {
        long timestamp = 1712130645000L; // 2024-04-03
        LocalDate result = LocalDateTimeUtil.timestampToDate(timestamp);
        assertNotNull(result);
        assertEquals(2024, result.getYear());
        assertEquals(4, result.getMonthValue());
        assertEquals(3, result.getDayOfMonth());
    }

    @Test
    @DisplayName("dateTimeToTimestamp converts LocalDateTime to timestamp")
    void testDateTimeToTimestamp() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 45);
        long timestamp = LocalDateTimeUtil.dateTimeToTimestamp(dateTime);
        assertTrue(timestamp > 0);
        assertEquals(0, LocalDateTimeUtil.dateTimeToTimestamp(null));
    }

    @Test
    @DisplayName("dateToTimestamp converts LocalDate to timestamp")
    void testDateToTimestamp() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        long timestamp = LocalDateTimeUtil.dateToTimestamp(date);
        assertTrue(timestamp > 0);
        assertEquals(0, LocalDateTimeUtil.dateToTimestamp(null));
    }

    // ========== Utility Methods Tests ==========

    @Test
    @DisplayName("startOfDay returns LocalDateTime at start of day")
    void testStartOfDay() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        LocalDateTime expected = LocalDateTime.of(2026, 4, 3, 0, 0, 0);
        assertEquals(expected, LocalDateTimeUtil.startOfDay(date));
        assertNull(LocalDateTimeUtil.startOfDay(null));
    }

    @Test
    @DisplayName("endOfDay returns LocalDateTime at end of day")
    void testEndOfDay() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        LocalDateTime result = LocalDateTimeUtil.endOfDay(date);
        assertNotNull(result);
        assertEquals(23, result.getHour());
        assertEquals(59, result.getMinute());
        assertEquals(59, result.getSecond());
        assertNull(LocalDateTimeUtil.endOfDay(null));
    }

    @Test
    @DisplayName("isToday checks if date is today")
    void testIsToday() {
        assertTrue(LocalDateTimeUtil.isToday(LocalDate.now()));
        assertFalse(LocalDateTimeUtil.isToday(LocalDate.of(2025, 1, 1)));
        assertFalse(LocalDateTimeUtil.isToday((LocalDate) null));
    }

    @Test
    @DisplayName("isToday checks if datetime is today")
    void testIsTodayForDateTime() {
        assertTrue(LocalDateTimeUtil.isToday(LocalDateTime.now()));
        assertFalse(LocalDateTimeUtil.isToday(LocalDateTime.of(2025, 1, 1, 12, 0)));
        assertFalse(LocalDateTimeUtil.isToday((LocalDateTime) null));
    }

    @Test
    @DisplayName("firstDayOfMonth returns first day of month")
    void testFirstDayOfMonth() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        assertEquals(LocalDate.of(2026, 4, 1), LocalDateTimeUtil.firstDayOfMonth(date));
        assertNull(LocalDateTimeUtil.firstDayOfMonth(null));
    }

    @Test
    @DisplayName("lastDayOfMonth returns last day of month")
    void testLastDayOfMonth() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        assertEquals(LocalDate.of(2026, 4, 30), LocalDateTimeUtil.lastDayOfMonth(date));
        
        LocalDate febDate = LocalDate.of(2026, 2, 15);
        assertEquals(LocalDate.of(2026, 2, 28), LocalDateTimeUtil.lastDayOfMonth(febDate));
        
        assertNull(LocalDateTimeUtil.lastDayOfMonth(null));
    }

    @Test
    @DisplayName("reformatDateStr reformats date string from one pattern to another")
    void testReformatDateStr() {
        assertEquals("2026/04/03", LocalDateTimeUtil.reformatDateStr("2026-04-03", "yyyy-MM-dd", "yyyy/MM/dd"));
        assertEquals("20260403", LocalDateTimeUtil.reformatDateStr("2026-04-03", "yyyy-MM-dd", "yyyyMMdd"));
        assertNull(LocalDateTimeUtil.reformatDateStr(null, "yyyy-MM-dd", "yyyy/MM/dd"));
        assertNull(LocalDateTimeUtil.reformatDateStr("2026-04-03", null, "yyyy/MM/dd"));
        assertNull(LocalDateTimeUtil.reformatDateStr("2026-04-03", "yyyy-MM-dd", null));
        assertNull(LocalDateTimeUtil.reformatDateStr("invalid", "yyyy-MM-dd", "yyyy/MM/dd"));
    }

    @Test
    @DisplayName("reformatDateTimeStr reformats datetime string from one pattern to another")
    void testReformatDateTimeStr() {
        assertEquals("2026/04/03 14:30:45", 
                     LocalDateTimeUtil.reformatDateTimeStr("2026-04-03 14:30:45", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"));
        assertEquals("20260403143045", 
                     LocalDateTimeUtil.reformatDateTimeStr("2026-04-03 14:30:45", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"));
        assertNull(LocalDateTimeUtil.reformatDateTimeStr(null, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"));
        assertNull(LocalDateTimeUtil.reformatDateTimeStr("2026-04-03 14:30:45", null, "yyyy/MM/dd HH:mm:ss"));
        assertNull(LocalDateTimeUtil.reformatDateTimeStr("2026-04-03 14:30:45", "yyyy-MM-dd HH:mm:ss", null));
        assertNull(LocalDateTimeUtil.reformatDateTimeStr("invalid", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"));
    }

    // ========== ROC Calendar Conversion Tests ==========

    @Test
    @DisplayName("gregorianToRocYear converts Gregorian year to ROC year")
    void testGregorianToRocYear() {
        assertEquals(115, LocalDateTimeUtil.gregorianToRocYear(2026));
        assertEquals(110, LocalDateTimeUtil.gregorianToRocYear(2021));
        assertEquals(1, LocalDateTimeUtil.gregorianToRocYear(1912));
    }

    @Test
    @DisplayName("rocToGregorianYear converts ROC year to Gregorian year")
    void testRocToGregorianYear() {
        assertEquals(2026, LocalDateTimeUtil.rocToGregorianYear(115));
        assertEquals(2021, LocalDateTimeUtil.rocToGregorianYear(110));
        assertEquals(1912, LocalDateTimeUtil.rocToGregorianYear(1));
    }

    @Test
    @DisplayName("dateToRocStr converts LocalDate to ROC string with default pattern")
    void testDateToRocStr() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        String rocStr = LocalDateTimeUtil.dateToRocStr(date);
        assertNotNull(rocStr);
        assertTrue(rocStr.contains("115"));
        assertNull(LocalDateTimeUtil.dateToRocStr(null));
    }

    @Test
    @DisplayName("dateToRocStr converts LocalDate to ROC string with custom pattern")
    void testDateToRocStrWithPattern() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        String rocStr = LocalDateTimeUtil.dateToRocStr(date, LocalDateTimeUtil.PATTERN_ROC_DATE_DASH);
        assertNotNull(rocStr);
        assertTrue(rocStr.contains("115"));
        assertNull(LocalDateTimeUtil.dateToRocStr(null, LocalDateTimeUtil.PATTERN_ROC_DATE));
        assertNull(LocalDateTimeUtil.dateToRocStr(date, null));
    }

    @Test
    @DisplayName("dateTimeToRocStr converts LocalDateTime to ROC string with default pattern")
    void testDateTimeToRocStr() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 0);
        String rocStr = LocalDateTimeUtil.dateTimeToRocStr(dateTime);
        assertNotNull(rocStr);
        assertTrue(rocStr.contains("115"));
        assertNull(LocalDateTimeUtil.dateTimeToRocStr(null));
    }

    @Test
    @DisplayName("dateTimeToRocStr converts LocalDateTime to ROC string with custom pattern")
    void testDateTimeToRocStrWithPattern() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 0);
        String rocStr = LocalDateTimeUtil.dateTimeToRocStr(dateTime, LocalDateTimeUtil.PATTERN_ROC_DATETIME_DASH);
        assertNotNull(rocStr);
        assertTrue(rocStr.contains("115"));
        assertNull(LocalDateTimeUtil.dateTimeToRocStr(null, LocalDateTimeUtil.PATTERN_ROC_DATETIME));
        assertNull(LocalDateTimeUtil.dateTimeToRocStr(dateTime, null));
    }

    @Test
    @DisplayName("rocStrToDate converts ROC string to LocalDate with default pattern")
    void testRocStrToDate() {
        LocalDate result = LocalDateTimeUtil.rocStrToDate("115/04/03");
        assertEquals(LocalDate.of(2026, 4, 3), result);
        assertNull(LocalDateTimeUtil.rocStrToDate(null));
        assertNull(LocalDateTimeUtil.rocStrToDate(""));
        assertNull(LocalDateTimeUtil.rocStrToDate("invalid"));
    }

    @Test
    @DisplayName("rocStrToDate converts ROC string to LocalDate with custom pattern")
    void testRocStrToDateWithPattern() {
        assertEquals(LocalDate.of(2026, 4, 3), 
                     LocalDateTimeUtil.rocStrToDate("115-04-03", LocalDateTimeUtil.PATTERN_ROC_DATE_DASH));
        assertEquals(LocalDate.of(2026, 4, 3), 
                     LocalDateTimeUtil.rocStrToDate("1150403", LocalDateTimeUtil.PATTERN_ROC_DATE_SHORT));
        assertNull(LocalDateTimeUtil.rocStrToDate(null, LocalDateTimeUtil.PATTERN_ROC_DATE));
        assertNull(LocalDateTimeUtil.rocStrToDate("115/04/03", null));
        assertNull(LocalDateTimeUtil.rocStrToDate("invalid", LocalDateTimeUtil.PATTERN_ROC_DATE));
    }

    @Test
    @DisplayName("rocStrToDateTime converts ROC string to LocalDateTime with default pattern")
    void testRocStrToDateTime() {
        LocalDateTime result = LocalDateTimeUtil.rocStrToDateTime("115/04/03 14:30:00");
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 0), result);
        assertNull(LocalDateTimeUtil.rocStrToDateTime(null));
        assertNull(LocalDateTimeUtil.rocStrToDateTime(""));
        assertNull(LocalDateTimeUtil.rocStrToDateTime("invalid"));
    }

    @Test
    @DisplayName("rocStrToDateTime converts ROC string to LocalDateTime with custom pattern")
    void testRocStrToDateTimeWithPattern() {
        assertEquals(LocalDateTime.of(2026, 4, 3, 14, 30, 0), 
                     LocalDateTimeUtil.rocStrToDateTime("115-04-03 14:30:00", LocalDateTimeUtil.PATTERN_ROC_DATETIME_DASH));
        assertNull(LocalDateTimeUtil.rocStrToDateTime(null, LocalDateTimeUtil.PATTERN_ROC_DATETIME));
        assertNull(LocalDateTimeUtil.rocStrToDateTime("115/04/03 14:30:00", null));
        assertNull(LocalDateTimeUtil.rocStrToDateTime("invalid", LocalDateTimeUtil.PATTERN_ROC_DATETIME));
    }

    @Test
    @DisplayName("reformatRocDateStr reformats ROC date string from one pattern to another")
    void testReformatRocDateStr() {
        String result = LocalDateTimeUtil.reformatRocDateStr("115/04/03", 
                       LocalDateTimeUtil.PATTERN_ROC_DATE, LocalDateTimeUtil.PATTERN_ROC_DATE_DASH);
        assertNotNull(result);
        assertTrue(result.contains("115"));
    }

    @Test
    @DisplayName("nowRocDateStr returns current date in ROC format")
    void testNowRocDateStr() {
        String rocStr = LocalDateTimeUtil.nowRocDateStr();
        assertNotNull(rocStr);
        assertTrue(rocStr.matches("\\d{3}/\\d{2}/\\d{2}"));
    }

    @Test
    @DisplayName("nowRocDateTimeStr returns current datetime in ROC format")
    void testNowRocDateTimeStr() {
        String rocStr = LocalDateTimeUtil.nowRocDateTimeStr();
        assertNotNull(rocStr);
        assertTrue(rocStr.matches("\\d{3}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    // ========== ROC Formatter Tests ==========

    @Test
    @DisplayName("FORMATTER_ROC_DATE is available and matches pattern")
    void testFormatterRocDate() {
        assertNotNull(LocalDateTimeUtil.FORMATTER_ROC_DATE);
        assertEquals("yyy/MM/dd", LocalDateTimeUtil.PATTERN_ROC_DATE);
        // Note: Direct use of ROC formatters with LocalDate gives Gregorian year as 3 digits
        // Use dateToRocStr() method for proper ROC calendar conversion
    }

    @Test
    @DisplayName("FORMATTER_ROC_DATE_DASH is available and matches pattern")
    void testFormatterRocDateDash() {
        assertNotNull(LocalDateTimeUtil.FORMATTER_ROC_DATE_DASH);
        assertEquals("yyy-MM-dd", LocalDateTimeUtil.PATTERN_ROC_DATE_DASH);
    }

    @Test
    @DisplayName("FORMATTER_ROC_DATE_SHORT is available and matches pattern")
    void testFormatterRocDateShort() {
        assertNotNull(LocalDateTimeUtil.FORMATTER_ROC_DATE_SHORT);
        assertEquals("yyyMMdd", LocalDateTimeUtil.PATTERN_ROC_DATE_SHORT);
    }

    @Test
    @DisplayName("FORMATTER_ROC_DATETIME is available and matches pattern")
    void testFormatterRocDateTime() {
        assertNotNull(LocalDateTimeUtil.FORMATTER_ROC_DATETIME);
        assertEquals("yyy/MM/dd HH:mm:ss", LocalDateTimeUtil.PATTERN_ROC_DATETIME);
    }

    @Test
    @DisplayName("FORMATTER_ROC_DATETIME_DASH is available and matches pattern")
    void testFormatterRocDateTimeDash() {
        assertNotNull(LocalDateTimeUtil.FORMATTER_ROC_DATETIME_DASH);
        assertEquals("yyy-MM-dd HH:mm:ss", LocalDateTimeUtil.PATTERN_ROC_DATETIME_DASH);
    }

    @Test
    @DisplayName("ROC formatters format date with 3-digit year pattern")
    void testRocFormattersWithDate() {
        LocalDate date = LocalDate.of(2026, 4, 3);
        // Direct formatting with 'yyy' pattern still shows full year (2026)
        String formatted = date.format(LocalDateTimeUtil.FORMATTER_ROC_DATE);
        assertNotNull(formatted);
        assertTrue(formatted.matches("\\d{3,4}/\\d{2}/\\d{2}")); // Can be 3 or 4 digits
        
        // For correct ROC year conversion, use dateToRocStr() method
        String rocStr = LocalDateTimeUtil.dateToRocStr(date);
        assertTrue(rocStr.contains("115")); // Correct ROC year
    }

    @Test
    @DisplayName("ROC formatters format datetime with 3-digit year pattern")
    void testRocFormattersWithDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 3, 14, 30, 0);
        // Direct formatting with 'yyy' pattern still shows full year (2026)
        String formatted = dateTime.format(LocalDateTimeUtil.FORMATTER_ROC_DATETIME);
        assertNotNull(formatted);
        assertTrue(formatted.matches("\\d{3,4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}")); // Can be 3 or 4 digits
        
        // For correct ROC year conversion, use dateTimeToRocStr() method
        String rocStr = LocalDateTimeUtil.dateTimeToRocStr(dateTime);
        assertTrue(rocStr.contains("115")); // Correct ROC year
    }

    @Test
    @DisplayName("ROC formatters can parse strings with 3-digit years")
    void testRocFormattersParseDate() {
        // ROC formatters can parse strings, but interpret 'yyy' as Gregorian year
        LocalDate parsed = LocalDate.parse("115/04/03", LocalDateTimeUtil.FORMATTER_ROC_DATE);
        assertNotNull(parsed);
        assertEquals(115, parsed.getYear()); // Parsed as Gregorian year 115, not 2026
        
        // For correct ROC parsing, use rocStrToDate() method
        LocalDate rocParsed = LocalDateTimeUtil.rocStrToDate("115/04/03");
        assertEquals(2026, rocParsed.getYear()); // Correctly converts ROC 115 to Gregorian 2026
    }

    @Test
    @DisplayName("ROC formatters can parse datetime strings with 3-digit years")
    void testRocFormattersParseDateTime() {
        // ROC formatters can parse strings, but interpret 'yyy' as Gregorian year
        LocalDateTime parsed = LocalDateTime.parse("115/04/03 14:30:00", LocalDateTimeUtil.FORMATTER_ROC_DATETIME);
        assertNotNull(parsed);
        assertEquals(115, parsed.getYear()); // Parsed as Gregorian year 115, not 2026
        
        // For correct ROC parsing, use rocStrToDateTime() method
        LocalDateTime rocParsed = LocalDateTimeUtil.rocStrToDateTime("115/04/03 14:30:00");
        assertEquals(2026, rocParsed.getYear()); // Correctly converts ROC 115 to Gregorian 2026
    }
}
