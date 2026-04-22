package com.example.foundation.codec;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for Base64 encoding and decoding.
 * Backed by Apache Commons Codec — handles standard Base64 and URL-safe Base64.
 * All methods use UTF-8 by default; overloads accept an explicit {@link Charset}.
 */
public class Base64Util {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private Base64Util() {
        // Utility class — prevent instantiation
    }

    // ========== Encode ==========

    /**
     * Encode a byte array to a Base64 string.
     *
     * @param data bytes to encode
     * @return Base64-encoded string, or {@code null} if {@code data} is null
     */
    public static String encode(byte[] data) {
        if (data == null) {
            return null;
        }
        return Base64.encodeBase64String(data);
    }

    /**
     * Encode a string to Base64 using UTF-8.
     *
     * @param text plain-text string to encode
     * @return Base64-encoded string, or {@code null} if {@code text} is null
     */
    public static String encode(String text) {
        if (text == null) {
            return null;
        }
        return encode(text, DEFAULT_CHARSET);
    }

    /**
     * Encode a string to Base64 using the specified charset.
     *
     * @param text    plain-text string to encode
     * @param charset charset used to convert the string to bytes
     * @return Base64-encoded string, or {@code null} if either argument is null
     */
    public static String encode(String text, Charset charset) {
        if (text == null || charset == null) {
            return null;
        }
        return Base64.encodeBase64String(text.getBytes(charset));
    }

    /**
     * Encode a byte array to a URL-safe Base64 string (uses {@code -} and {@code _} instead of {@code +} and {@code /},
     * and omits padding).
     *
     * @param data bytes to encode
     * @return URL-safe Base64-encoded string, or {@code null} if {@code data} is null
     */
    public static String encodeUrlSafe(byte[] data) {
        if (data == null) {
            return null;
        }
        return Base64.encodeBase64URLSafeString(data);
    }

    /**
     * Encode a string to URL-safe Base64 using UTF-8.
     *
     * @param text plain-text string to encode
     * @return URL-safe Base64-encoded string, or {@code null} if {@code text} is null
     */
    public static String encodeUrlSafe(String text) {
        if (text == null) {
            return null;
        }
        return encodeUrlSafe(text.getBytes(DEFAULT_CHARSET));
    }

    /**
     * Encode a string to URL-safe Base64 using the specified charset.
     * Useful for non-UTF-8 encodings such as Big5 or GB2312.
     *
     * @param text    plain-text string to encode
     * @param charset charset used to convert the string to bytes before encoding
     * @return URL-safe Base64-encoded string, or {@code null} if either argument is null
     */
    public static String encodeUrlSafe(String text, Charset charset) {
        if (text == null || charset == null) {
            return null;
        }
        return encodeUrlSafe(text.getBytes(charset));
    }

    // ========== Decode ==========

    /**
     * Decode a Base64 string to a byte array.
     *
     * @param base64 Base64-encoded string
     * @return decoded bytes, or {@code null} if {@code base64} is null
     */
    public static byte[] decodeToBytes(String base64) {
        if (base64 == null) {
            return null;
        }
        return Base64.decodeBase64(base64);
    }

    /**
     * Decode a Base64 string to a plain-text string using UTF-8.
     *
     * @param base64 Base64-encoded string
     * @return decoded string, or {@code null} if {@code base64} is null
     */
    public static String decode(String base64) {
        if (base64 == null) {
            return null;
        }
        return decode(base64, DEFAULT_CHARSET);
    }

    /**
     * Decode a Base64 string to a plain-text string using the specified charset.
     *
     * @param base64  Base64-encoded string
     * @param charset charset used to convert decoded bytes to a string
     * @return decoded string, or {@code null} if either argument is null
     */
    public static String decode(String base64, Charset charset) {
        if (base64 == null || charset == null) {
            return null;
        }
        return new String(Base64.decodeBase64(base64), charset);
    }

    /**
     * Decode a Base64-encoded byte array to a plain-text string using UTF-8.
     *
     * @param base64Bytes Base64-encoded bytes
     * @return decoded string, or {@code null} if {@code base64Bytes} is null
     */
    public static String decode(byte[] base64Bytes) {
        if (base64Bytes == null) {
            return null;
        }
        return new String(Base64.decodeBase64(base64Bytes), DEFAULT_CHARSET);
    }

    // ========== Utility ==========

    /**
     * Check whether a string is valid Base64.
     *
     * @param text string to test
     * @return {@code true} if the string is a valid Base64-encoded value
     */
    public static boolean isBase64(String text) {
        if (text == null) {
            return false;
        }
        return Base64.isBase64(text);
    }
}
