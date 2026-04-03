package com.example.foundation.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * String utility class that extends Apache Commons StringUtils functionality.
 * Provides convenient static methods for common string operations.
 */
public class StringUtil {

    private StringUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Check if a string is null or empty
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    /**
     * Check if a string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }

    /**
     * Check if a string is null, empty or contains only whitespace
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * Check if a string is not blank (not null, not empty, not whitespace only)
     */
    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * Trim whitespace from a string, handling null safely
     */
    public static String trim(String str) {
        return StringUtils.trim(str);
    }

    /**
     * Trim whitespace and convert empty to null
     */
    public static String trimToNull(String str) {
        return StringUtils.trimToNull(str);
    }

    /**
     * Trim whitespace and convert null to empty string
     */
    public static String trimToEmpty(String str) {
        return StringUtils.trimToEmpty(str);
    }

    /**
     * Get a substring safely, handling null and index out of bounds
     */
    public static String substring(String str, int start, int end) {
        return StringUtils.substring(str, start, end);
    }

    /**
     * Get a substring from start position
     */
    public static String substring(String str, int start) {
        return StringUtils.substring(str, start);
    }

    /**
     * Check if string contains a search string
     */
    public static boolean contains(String str, String searchStr) {
        return StringUtils.contains(str, searchStr);
    }

    /**
     * Check if string contains a search string, ignoring case
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    /**
     * Compare two strings for equality, handling null safely
     */
    public static boolean equals(String str1, String str2) {
        return StringUtils.equals(str1, str2);
    }

    /**
     * Compare two strings for equality, ignoring case, handling null safely
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    /**
     * Join array elements with a separator
     */
    public static String join(Object[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    /**
     * Join collection elements with a separator
     */
    public static String join(Iterable<?> iterable, String separator) {
        return StringUtils.join(iterable, separator);
    }

    /**
     * Split string by separator into array
     */
    public static String[] split(String str, String separatorChar) {
        return StringUtils.split(str, separatorChar);
    }

    /**
     * Split string by separator into list
     */
    public static List<String> splitToList(String str, String separator) {
        if (isEmpty(str)) {
            return List.of();
        }
        return Arrays.stream(StringUtils.split(str, separator))
                .collect(Collectors.toList());
    }

    /**
     * Replace all occurrences of a search string with replacement
     */
    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }

    /**
     * Replace all occurrences of a search string with replacement, ignoring case
     */
    public static String replaceIgnoreCase(String text, String searchString, String replacement) {
        return StringUtils.replaceIgnoreCase(text, searchString, replacement);
    }

    /**
     * Capitalize first letter of string
     */
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * Uncapitalize first letter of string
     */
    public static String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }

    /**
     * Convert string to uppercase, handling null safely
     */
    public static String upperCase(String str) {
        return StringUtils.upperCase(str);
    }

    /**
     * Convert string to lowercase, handling null safely
     */
    public static String lowerCase(String str) {
        return StringUtils.lowerCase(str);
    }

    /**
     * Reverse a string
     */
    public static String reverse(String str) {
        return StringUtils.reverse(str);
    }

    /**
     * Remove all whitespace from string
     */
    public static String deleteWhitespace(String str) {
        return StringUtils.deleteWhitespace(str);
    }

    /**
     * Left pad string with spaces to specified length
     */
    public static String leftPad(String str, int size) {
        return StringUtils.leftPad(str, size);
    }

    /**
     * Left pad string with specified character to specified length
     */
    public static String leftPad(String str, int size, char padChar) {
        return StringUtils.leftPad(str, size, padChar);
    }

    /**
     * Right pad string with spaces to specified length
     */
    public static String rightPad(String str, int size) {
        return StringUtils.rightPad(str, size);
    }

    /**
     * Right pad string with specified character to specified length
     */
    public static String rightPad(String str, int size, char padChar) {
        return StringUtils.rightPad(str, size, padChar);
    }

    /**
     * Abbreviate string to specified length with ellipsis
     */
    public static String abbreviate(String str, int maxWidth) {
        return StringUtils.abbreviate(str, maxWidth);
    }

    /**
     * Check if string starts with prefix
     */
    public static boolean startsWith(String str, String prefix) {
        return StringUtils.startsWith(str, prefix);
    }

    /**
     * Check if string starts with prefix, ignoring case
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return StringUtils.startsWithIgnoreCase(str, prefix);
    }

    /**
     * Check if string ends with suffix
     */
    public static boolean endsWith(String str, String suffix) {
        return StringUtils.endsWith(str, suffix);
    }

    /**
     * Check if string ends with suffix, ignoring case
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return StringUtils.endsWithIgnoreCase(str, suffix);
    }

    /**
     * Get default string if input is null
     */
    public static String defaultString(String str, String defaultStr) {
        return StringUtils.defaultString(str, defaultStr);
    }

    /**
     * Get empty string if input is null
     */
    public static String defaultString(String str) {
        return StringUtils.defaultString(str);
    }

    /**
     * Get default string if input is blank
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return StringUtils.defaultIfBlank(str, defaultStr);
    }

    /**
     * Get default string if input is empty
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return StringUtils.defaultIfEmpty(str, defaultStr);
    }

    /**
     * Remove start string from main string if present
     */
    public static String removeStart(String str, String remove) {
        return StringUtils.removeStart(str, remove);
    }

    /**
     * Remove end string from main string if present
     */
    public static String removeEnd(String str, String remove) {
        return StringUtils.removeEnd(str, remove);
    }

    /**
     * Check if string is numeric (digits only)
     */
    public static boolean isNumeric(String str) {
        return StringUtils.isNumeric(str);
    }

    /**
     * Check if string is alphanumeric
     */
    public static boolean isAlphanumeric(String str) {
        return StringUtils.isAlphanumeric(str);
    }

    /**
     * Check if string is alphabetic only
     */
    public static boolean isAlpha(String str) {
        return StringUtils.isAlpha(str);
    }

    /**
     * Repeat string n times
     */
    public static String repeat(String str, int repeat) {
        return StringUtils.repeat(str, repeat);
    }

    /**
     * Repeat string with separator
     */
    public static String repeat(String str, String separator, int repeat) {
        return StringUtils.repeat(str, separator, repeat);
    }

    /**
     * Strip accents from string (e.g., "café" -> "cafe")
     */
    public static String stripAccents(String str) {
        return StringUtils.stripAccents(str);
    }
}
