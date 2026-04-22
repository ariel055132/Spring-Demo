package com.example.foundation.codec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for Base16 (hexadecimal) encoding and decoding.
 * Backed by Apache Commons Codec {@link Hex}.
 * All methods use UTF-8 by default; overloads accept an explicit {@link Charset}.
 */
public class Base16Util {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private Base16Util() {
        // Utility class — prevent instantiation
    }

    // ========== Encode ==========

    /**
     * Encode a byte array to a lowercase hex string.
     *
     * @param data bytes to encode
     * @return lowercase hex string, or {@code null} if {@code data} is null
     */
    public static String encode(byte[] data) {
        if (data == null) {
            return null;
        }
        return Hex.encodeHexString(data);
    }

    /**
     * Encode a string to a lowercase hex string using UTF-8.
     *
     * @param text plain-text string to encode
     * @return lowercase hex string, or {@code null} if {@code text} is null
     */
    public static String encode(String text) {
        if (text == null) {
            return null;
        }
        return encode(text, DEFAULT_CHARSET);
    }

    /**
     * Encode a string to a lowercase hex string using the specified charset.
     * Useful for non-UTF-8 encodings such as Big5 or GB2312.
     *
     * @param text    plain-text string to encode
     * @param charset charset used to convert the string to bytes before encoding
     * @return lowercase hex string, or {@code null} if either argument is null
     */
    public static String encode(String text, Charset charset) {
        if (text == null || charset == null) {
            return null;
        }
        return Hex.encodeHexString(text.getBytes(charset));
    }

    /**
     * Encode a byte array to an uppercase hex string.
     *
     * @param data bytes to encode
     * @return uppercase hex string, or {@code null} if {@code data} is null
     */
    public static String encodeUpperCase(byte[] data) {
        if (data == null) {
            return null;
        }
        return Hex.encodeHexString(data, false);
    }

    /**
     * Encode a string to an uppercase hex string using UTF-8.
     *
     * @param text plain-text string to encode
     * @return uppercase hex string, or {@code null} if {@code text} is null
     */
    public static String encodeUpperCase(String text) {
        if (text == null) {
            return null;
        }
        return encodeUpperCase(text, DEFAULT_CHARSET);
    }

    /**
     * Encode a string to an uppercase hex string using the specified charset.
     *
     * @param text    plain-text string to encode
     * @param charset charset used to convert the string to bytes before encoding
     * @return uppercase hex string, or {@code null} if either argument is null
     */
    public static String encodeUpperCase(String text, Charset charset) {
        if (text == null || charset == null) {
            return null;
        }
        return Hex.encodeHexString(text.getBytes(charset), false);
    }

    // ========== Decode ==========

    /**
     * Decode a hex string to a byte array.
     *
     * @param hex hex-encoded string
     * @return decoded bytes, or {@code null} if {@code hex} is null
     * @throws RuntimeException if the hex string contains non-hex characters
     */
    public static byte[] decodeToBytes(String hex) {
        if (hex == null) {
            return null;
        }
        try {
            return Hex.decodeHex(hex);
        } catch (DecoderException e) {
            throw new RuntimeException("Failed to decode hex string: " + e.getMessage(), e);
        }
    }

    /**
     * Decode a hex string to a plain-text string using UTF-8.
     *
     * @param hex hex-encoded string
     * @return decoded string, or {@code null} if {@code hex} is null
     * @throws RuntimeException if the hex string contains non-hex characters
     */
    public static String decode(String hex) {
        if (hex == null) {
            return null;
        }
        return decode(hex, DEFAULT_CHARSET);
    }

    /**
     * Decode a hex string to a plain-text string using the specified charset.
     *
     * @param hex     hex-encoded string
     * @param charset charset used to convert decoded bytes to a string
     * @return decoded string, or {@code null} if either argument is null
     * @throws RuntimeException if the hex string contains non-hex characters
     */
    public static String decode(String hex, Charset charset) {
        if (hex == null || charset == null) {
            return null;
        }
        try {
            return new String(Hex.decodeHex(hex), charset);
        } catch (DecoderException e) {
            throw new RuntimeException("Failed to decode hex string: " + e.getMessage(), e);
        }
    }

    // ========== Utility ==========

    /**
     * Check whether a string is a valid hex string (even length, only 0-9 a-f A-F).
     * An empty string is considered valid (it decodes to an empty byte array).
     *
     * @param text string to test
     * @return {@code true} if the string is a valid hex-encoded value (including empty string)
     */
    public static boolean isHex(String text) {
        if (text == null || text.length() % 2 != 0) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }
}

