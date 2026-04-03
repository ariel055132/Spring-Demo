package com.example.foundation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtil Test")
class StringUtilTest {

    // ========== isEmpty/isNotEmpty Tests ==========

    @Test
    @DisplayName("isEmpty returns true for null and empty strings")
    void testIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("test"));
    }

    @Test
    @DisplayName("isNotEmpty returns true for non-empty strings")
    void testIsNotEmpty() {
        assertFalse(StringUtil.isNotEmpty(null));
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty(" "));
        assertTrue(StringUtil.isNotEmpty("test"));
    }

    // ========== isBlank/isNotBlank Tests ==========

    @Test
    @DisplayName("isBlank returns true for null, empty and whitespace strings")
    void testIsBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertTrue(StringUtil.isBlank("  "));
        assertTrue(StringUtil.isBlank("\t"));
        assertTrue(StringUtil.isBlank("\n"));
        assertFalse(StringUtil.isBlank("test"));
        assertFalse(StringUtil.isBlank(" test "));
    }

    @Test
    @DisplayName("isNotBlank returns true for non-blank strings")
    void testIsNotBlank() {
        assertFalse(StringUtil.isNotBlank(null));
        assertFalse(StringUtil.isNotBlank(""));
        assertFalse(StringUtil.isNotBlank(" "));
        assertFalse(StringUtil.isNotBlank("\t"));
        assertTrue(StringUtil.isNotBlank("test"));
        assertTrue(StringUtil.isNotBlank(" test "));
    }

    // ========== Trim Tests ==========

    @Test
    @DisplayName("trim removes leading and trailing whitespace")
    void testTrim() {
        assertNull(StringUtil.trim(null));
        assertEquals("", StringUtil.trim(""));
        assertEquals("", StringUtil.trim("  "));
        assertEquals("test", StringUtil.trim("  test  "));
        assertEquals("test test", StringUtil.trim("  test test  "));
    }

    @Test
    @DisplayName("trimToNull converts empty/whitespace to null")
    void testTrimToNull() {
        assertNull(StringUtil.trimToNull(null));
        assertNull(StringUtil.trimToNull(""));
        assertNull(StringUtil.trimToNull("  "));
        assertEquals("test", StringUtil.trimToNull("  test  "));
    }

    @Test
    @DisplayName("trimToEmpty converts null to empty string")
    void testTrimToEmpty() {
        assertEquals("", StringUtil.trimToEmpty(null));
        assertEquals("", StringUtil.trimToEmpty(""));
        assertEquals("", StringUtil.trimToEmpty("  "));
        assertEquals("test", StringUtil.trimToEmpty("  test  "));
    }

    // ========== Substring Tests ==========

    @Test
    @DisplayName("substring handles null and bounds safely")
    void testSubstring() {
        assertNull(StringUtil.substring(null, 0, 5));
        assertEquals("", StringUtil.substring("", 0, 5));
        assertEquals("hel", StringUtil.substring("hello", 0, 3));
        assertEquals("llo", StringUtil.substring("hello", 2, 5));
        assertEquals("hello", StringUtil.substring("hello", 0, 10)); // beyond length
        assertEquals("world", StringUtil.substring("hello world", 6));
    }

    // ========== Contains Tests ==========

    @Test
    @DisplayName("contains checks for substring presence")
    void testContains() {
        assertFalse(StringUtil.contains(null, "test"));
        assertFalse(StringUtil.contains("test", null));
        assertTrue(StringUtil.contains("hello world", "world"));
        assertTrue(StringUtil.contains("hello world", "hello"));
        assertFalse(StringUtil.contains("hello world", "Hello"));
        assertTrue(StringUtil.contains("hello", ""));
    }

    @Test
    @DisplayName("containsIgnoreCase checks substring ignoring case")
    void testContainsIgnoreCase() {
        assertFalse(StringUtil.containsIgnoreCase(null, "test"));
        assertTrue(StringUtil.containsIgnoreCase("hello world", "World"));
        assertTrue(StringUtil.containsIgnoreCase("hello world", "HELLO"));
        assertTrue(StringUtil.containsIgnoreCase("Hello World", "hello world"));
    }

    // ========== Equals Tests ==========

    @Test
    @DisplayName("equals compares strings safely")
    void testEquals() {
        assertTrue(StringUtil.equals(null, null));
        assertFalse(StringUtil.equals(null, "test"));
        assertFalse(StringUtil.equals("test", null));
        assertTrue(StringUtil.equals("test", "test"));
        assertFalse(StringUtil.equals("test", "Test"));
        assertFalse(StringUtil.equals("test", "test2"));
    }

    @Test
    @DisplayName("equalsIgnoreCase compares strings ignoring case")
    void testEqualsIgnoreCase() {
        assertTrue(StringUtil.equalsIgnoreCase(null, null));
        assertFalse(StringUtil.equalsIgnoreCase(null, "test"));
        assertTrue(StringUtil.equalsIgnoreCase("test", "Test"));
        assertTrue(StringUtil.equalsIgnoreCase("TEST", "test"));
        assertTrue(StringUtil.equalsIgnoreCase("Hello World", "hello world"));
        assertFalse(StringUtil.equalsIgnoreCase("test", "test2"));
    }

    // ========== Join Tests ==========

    @Test
    @DisplayName("join concatenates array elements with separator")
    void testJoinArray() {
        assertNull(StringUtil.join((Object[]) null, ","));
        assertEquals("", StringUtil.join(new String[]{}, ","));
        assertEquals("a,b,c", StringUtil.join(new String[]{"a", "b", "c"}, ","));
        assertEquals("a|b|c", StringUtil.join(new String[]{"a", "b", "c"}, "|"));
        assertEquals("abc", StringUtil.join(new String[]{"a", "b", "c"}, ""));
    }

    @Test
    @DisplayName("join concatenates list elements with separator")
    void testJoinList() {
        assertEquals("a,b,c", StringUtil.join(List.of("a", "b", "c"), ","));
        assertEquals("1-2-3", StringUtil.join(List.of(1, 2, 3), "-"));
    }

    // ========== Split Tests ==========

    @Test
    @DisplayName("split divides string by separator")
    void testSplit() {
        assertNull(StringUtil.split(null, ","));
        assertArrayEquals(new String[]{}, StringUtil.split("", ","));
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtil.split("a,b,c", ","));
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtil.split("a|b|c", "|"));
        assertArrayEquals(new String[]{"hello", "world"}, StringUtil.split("hello world", " "));
    }

    @Test
    @DisplayName("splitToList divides string into list")
    void testSplitToList() {
        assertTrue(StringUtil.splitToList(null, ",").isEmpty());
        assertTrue(StringUtil.splitToList("", ",").isEmpty());
        assertEquals(List.of("a", "b", "c"), StringUtil.splitToList("a,b,c", ","));
        assertEquals(List.of("hello", "world"), StringUtil.splitToList("hello world", " "));
    }

    // ========== Replace Tests ==========

    @Test
    @DisplayName("replace replaces all occurrences")
    void testReplace() {
        assertNull(StringUtil.replace(null, "old", "new"));
        assertEquals("hello world", StringUtil.replace("hello world", "foo", "bar"));
        assertEquals("new world", StringUtil.replace("old world", "old", "new"));
        assertEquals("new new world", StringUtil.replace("old old world", "old", "new"));
        assertEquals("X b c", StringUtil.replace("a b c", "a", "X"));
    }

    @Test
    @DisplayName("replaceIgnoreCase replaces ignoring case")
    void testReplaceIgnoreCase() {
        assertEquals("new world", StringUtil.replaceIgnoreCase("OLD world", "old", "new"));
        assertEquals("new new world", StringUtil.replaceIgnoreCase("OLD old world", "old", "new"));
    }

    // ========== Case Conversion Tests ==========

    @Test
    @DisplayName("capitalize capitalizes first letter")
    void testCapitalize() {
        assertNull(StringUtil.capitalize(null));
        assertEquals("", StringUtil.capitalize(""));
        assertEquals("Test", StringUtil.capitalize("test"));
        assertEquals("Test", StringUtil.capitalize("Test"));
        assertEquals("TEST", StringUtil.capitalize("tEST"));
    }

    @Test
    @DisplayName("uncapitalize uncapitalizes first letter")
    void testUncapitalize() {
        assertNull(StringUtil.uncapitalize(null));
        assertEquals("", StringUtil.uncapitalize(""));
        assertEquals("test", StringUtil.uncapitalize("Test"));
        assertEquals("test", StringUtil.uncapitalize("test"));
        assertEquals("tEST", StringUtil.uncapitalize("TEST"));
    }

    @Test
    @DisplayName("upperCase converts to uppercase")
    void testUpperCase() {
        assertNull(StringUtil.upperCase(null));
        assertEquals("", StringUtil.upperCase(""));
        assertEquals("TEST", StringUtil.upperCase("test"));
        assertEquals("HELLO WORLD", StringUtil.upperCase("Hello World"));
    }

    @Test
    @DisplayName("lowerCase converts to lowercase")
    void testLowerCase() {
        assertNull(StringUtil.lowerCase(null));
        assertEquals("", StringUtil.lowerCase(""));
        assertEquals("test", StringUtil.lowerCase("TEST"));
        assertEquals("hello world", StringUtil.lowerCase("Hello World"));
    }

    // ========== Reverse Tests ==========

    @Test
    @DisplayName("reverse reverses string")
    void testReverse() {
        assertNull(StringUtil.reverse(null));
        assertEquals("", StringUtil.reverse(""));
        assertEquals("tset", StringUtil.reverse("test"));
        assertEquals("dlrow olleh", StringUtil.reverse("hello world"));
    }

    // ========== Delete Whitespace Tests ==========

    @Test
    @DisplayName("deleteWhitespace removes all whitespace")
    void testDeleteWhitespace() {
        assertNull(StringUtil.deleteWhitespace(null));
        assertEquals("", StringUtil.deleteWhitespace(""));
        assertEquals("test", StringUtil.deleteWhitespace("test"));
        assertEquals("helloworld", StringUtil.deleteWhitespace("hello world"));
        assertEquals("helloworld", StringUtil.deleteWhitespace("  hello  world  "));
        assertEquals("test", StringUtil.deleteWhitespace("\t\ntest\t\n"));
    }

    // ========== Padding Tests ==========

    @Test
    @DisplayName("leftPad pads string on left")
    void testLeftPad() {
        assertNull(StringUtil.leftPad(null, 5));
        assertEquals("  abc", StringUtil.leftPad("abc", 5));
        assertEquals("00123", StringUtil.leftPad("123", 5, '0'));
        assertEquals("abc", StringUtil.leftPad("abc", 2)); // no padding needed
    }

    @Test
    @DisplayName("rightPad pads string on right")
    void testRightPad() {
        assertNull(StringUtil.rightPad(null, 5));
        assertEquals("abc  ", StringUtil.rightPad("abc", 5));
        assertEquals("123--", StringUtil.rightPad("123", 5, '-'));
        assertEquals("abc", StringUtil.rightPad("abc", 2)); // no padding needed
    }

    // ========== Abbreviate Tests ==========

    @Test
    @DisplayName("abbreviate shortens string with ellipsis")
    void testAbbreviate() {
        assertNull(StringUtil.abbreviate(null, 10));
        assertEquals("ab...", StringUtil.abbreviate("abcdefghij", 5));
        assertEquals("abc", StringUtil.abbreviate("abc", 10)); // no abbreviation needed
    }

    // ========== StartsWith/EndsWith Tests ==========

    @Test
    @DisplayName("startsWith checks string prefix")
    void testStartsWith() {
        assertFalse(StringUtil.startsWith(null, "test"));
        assertFalse(StringUtil.startsWith("test", null));
        assertTrue(StringUtil.startsWith("hello world", "hello"));
        assertFalse(StringUtil.startsWith("hello world", "Hello"));
        assertTrue(StringUtil.startsWith("test", "test"));
    }

    @Test
    @DisplayName("startsWithIgnoreCase checks prefix ignoring case")
    void testStartsWithIgnoreCase() {
        assertTrue(StringUtil.startsWithIgnoreCase("Hello world", "hello"));
        assertTrue(StringUtil.startsWithIgnoreCase("TEST", "test"));
        assertFalse(StringUtil.startsWithIgnoreCase("hello", "world"));
    }

    @Test
    @DisplayName("endsWith checks string suffix")
    void testEndsWith() {
        assertFalse(StringUtil.endsWith(null, "test"));
        assertFalse(StringUtil.endsWith("test", null));
        assertTrue(StringUtil.endsWith("hello world", "world"));
        assertFalse(StringUtil.endsWith("hello world", "World"));
        assertTrue(StringUtil.endsWith("test", "test"));
    }

    @Test
    @DisplayName("endsWithIgnoreCase checks suffix ignoring case")
    void testEndsWithIgnoreCase() {
        assertTrue(StringUtil.endsWithIgnoreCase("Hello World", "world"));
        assertTrue(StringUtil.endsWithIgnoreCase("TEST", "test"));
        assertFalse(StringUtil.endsWithIgnoreCase("hello", "world"));
    }

    // ========== Default String Tests ==========

    @Test
    @DisplayName("defaultString returns default for null")
    void testDefaultString() {
        assertEquals("default", StringUtil.defaultString(null, "default"));
        assertEquals("test", StringUtil.defaultString("test", "default"));
        assertEquals("", StringUtil.defaultString("", "default"));
        assertEquals("", StringUtil.defaultString(null));
    }

    @Test
    @DisplayName("defaultIfBlank returns default for blank strings")
    void testDefaultIfBlank() {
        assertEquals("default", StringUtil.defaultIfBlank(null, "default"));
        assertEquals("default", StringUtil.defaultIfBlank("", "default"));
        assertEquals("default", StringUtil.defaultIfBlank("  ", "default"));
        assertEquals("test", StringUtil.defaultIfBlank("test", "default"));
    }

    @Test
    @DisplayName("defaultIfEmpty returns default for empty strings")
    void testDefaultIfEmpty() {
        assertEquals("default", StringUtil.defaultIfEmpty(null, "default"));
        assertEquals("default", StringUtil.defaultIfEmpty("", "default"));
        assertEquals("  ", StringUtil.defaultIfEmpty("  ", "default"));
        assertEquals("test", StringUtil.defaultIfEmpty("test", "default"));
    }

    // ========== Remove Start/End Tests ==========

    @Test
    @DisplayName("removeStart removes prefix if present")
    void testRemoveStart() {
        assertNull(StringUtil.removeStart(null, "test"));
        assertEquals("world", StringUtil.removeStart("hello world", "hello "));
        assertEquals("hello world", StringUtil.removeStart("hello world", "goodbye"));
        assertEquals("test", StringUtil.removeStart("test", ""));
    }

    @Test
    @DisplayName("removeEnd removes suffix if present")
    void testRemoveEnd() {
        assertNull(StringUtil.removeEnd(null, "test"));
        assertEquals("hello", StringUtil.removeEnd("hello world", " world"));
        assertEquals("hello world", StringUtil.removeEnd("hello world", "goodbye"));
        assertEquals("test", StringUtil.removeEnd("test", ""));
    }

    // ========== Validation Tests ==========

    @Test
    @DisplayName("isNumeric checks if string contains only digits")
    void testIsNumeric() {
        assertFalse(StringUtil.isNumeric(null));
        assertFalse(StringUtil.isNumeric(""));
        assertTrue(StringUtil.isNumeric("123"));
        assertTrue(StringUtil.isNumeric("0"));
        assertFalse(StringUtil.isNumeric("12.3"));
        assertFalse(StringUtil.isNumeric("12a"));
        assertFalse(StringUtil.isNumeric("-123"));
    }

    @Test
    @DisplayName("isAlphanumeric checks if string is alphanumeric")
    void testIsAlphanumeric() {
        assertFalse(StringUtil.isAlphanumeric(null));
        assertFalse(StringUtil.isAlphanumeric(""));
        assertTrue(StringUtil.isAlphanumeric("abc123"));
        assertTrue(StringUtil.isAlphanumeric("Test123"));
        assertFalse(StringUtil.isAlphanumeric("test 123"));
        assertFalse(StringUtil.isAlphanumeric("test-123"));
    }

    @Test
    @DisplayName("isAlpha checks if string contains only letters")
    void testIsAlpha() {
        assertFalse(StringUtil.isAlpha(null));
        assertFalse(StringUtil.isAlpha(""));
        assertTrue(StringUtil.isAlpha("abc"));
        assertTrue(StringUtil.isAlpha("Test"));
        assertFalse(StringUtil.isAlpha("abc123"));
        assertFalse(StringUtil.isAlpha("test test"));
    }

    // ========== Repeat Tests ==========

    @Test
    @DisplayName("repeat repeats string n times")
    void testRepeat() {
        assertNull(StringUtil.repeat(null, 3));
        assertEquals("", StringUtil.repeat("", 3));
        assertEquals("aaa", StringUtil.repeat("a", 3));
        assertEquals("abcabcabc", StringUtil.repeat("abc", 3));
        assertEquals("", StringUtil.repeat("test", 0));
        assertEquals("a,a,a", StringUtil.repeat("a", ",", 3));
        assertEquals("x-x-x-x", StringUtil.repeat("x", "-", 4));
    }

    // ========== Strip Accents Tests ==========

    @Test
    @DisplayName("stripAccents removes accents from characters")
    void testStripAccents() {
        assertNull(StringUtil.stripAccents(null));
        assertEquals("", StringUtil.stripAccents(""));
        assertEquals("cafe", StringUtil.stripAccents("café"));
        assertEquals("naive", StringUtil.stripAccents("naïve"));
        assertEquals("resume", StringUtil.stripAccents("résumé"));
        assertEquals("test", StringUtil.stripAccents("test"));
    }
}
