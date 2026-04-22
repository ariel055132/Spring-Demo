package com.example.foundation.codec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Base64Util Test")
class Base64UtilTest {

    private static final String PLAIN = "Hello, World!";
    // echo -n "Hello, World!" | base64  →  SGVsbG8sIFdvcmxkIQ==
    private static final String ENCODED = "SGVsbG8sIFdvcmxkIQ==";

    // ──────────────────────────────────────────────
    // encode(String)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(String) produces correct Base64 output")
    void encode_string() {
        assertEquals(ENCODED, Base64Util.encode(PLAIN));
    }

    @Test
    @DisplayName("encode(String) returns null for null input")
    void encode_string_null() {
        assertNull(Base64Util.encode((String) null));
    }

    @Test
    @DisplayName("encode(String) handles empty string")
    void encode_string_empty() {
        assertEquals("", Base64Util.encode(""));
    }

    // ──────────────────────────────────────────────
    // encode(byte[])
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(byte[]) produces correct Base64 output")
    void encode_bytes() {
        assertEquals(ENCODED, Base64Util.encode(PLAIN.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("encode(byte[]) returns null for null input")
    void encode_bytes_null() {
        assertNull(Base64Util.encode((byte[]) null));
    }

    // ──────────────────────────────────────────────
    // encode(String, Charset)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(String, Charset) produces correct output with explicit charset")
    void encode_stringWithCharset() {
        assertEquals(ENCODED, Base64Util.encode(PLAIN, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("encode(String, Charset) returns null when charset is null")
    void encode_stringWithCharset_nullCharset() {
        assertNull(Base64Util.encode(PLAIN, null));
    }

    @Test
    @DisplayName("encode(String, Charset) returns null when text is null")
    void encode_stringWithCharset_nullText() {
        assertNull(Base64Util.encode(null, StandardCharsets.UTF_8));
    }

    // ──────────────────────────────────────────────
    // encodeUrlSafe
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encodeUrlSafe(String) produces no + or / characters")
    void encodeUrlSafe_string() {
        // Use bytes that would produce + or / in standard Base64
        byte[] data = new byte[]{(byte) 0xFB, (byte) 0xFF, (byte) 0xFE};
        String urlSafe = Base64Util.encodeUrlSafe(data);
        assertFalse(urlSafe.contains("+"), "URL-safe Base64 must not contain '+'");
        assertFalse(urlSafe.contains("/"), "URL-safe Base64 must not contain '/'");
    }

    @Test
    @DisplayName("encodeUrlSafe(String) returns null for null input")
    void encodeUrlSafe_null() {
        assertNull(Base64Util.encodeUrlSafe((String) null));
        assertNull(Base64Util.encodeUrlSafe((byte[]) null));
    }

    @Test
    @DisplayName("encodeUrlSafe and decode are inverse operations")
    void encodeUrlSafe_roundTrip() {
        String urlSafe = Base64Util.encodeUrlSafe(PLAIN);
        // URL-safe Base64 (no padding) is still decodable by the standard decoder
        String decoded = Base64Util.decode(urlSafe);
        assertEquals(PLAIN, decoded);
    }

    // ──────────────────────────────────────────────
    // decode(String)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decode(String) recovers original plain text")
    void decode_string() {
        assertEquals(PLAIN, Base64Util.decode(ENCODED));
    }

    @Test
    @DisplayName("decode(String) returns null for null input")
    void decode_string_null() {
        assertNull(Base64Util.decode((String) null));
    }

    @Test
    @DisplayName("decode(String) handles empty string")
    void decode_string_empty() {
        assertEquals("", Base64Util.decode(""));
    }

    // ──────────────────────────────────────────────
    // decode(String, Charset)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decode(String, Charset) recovers original text with explicit charset")
    void decode_stringWithCharset() {
        assertEquals(PLAIN, Base64Util.decode(ENCODED, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("decode(String, Charset) returns null when charset is null")
    void decode_stringWithCharset_nullCharset() {
        assertNull(Base64Util.decode(ENCODED, null));
    }

    // ──────────────────────────────────────────────
    // decodeToBytes
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decodeToBytes returns correct byte array")
    void decodeToBytes() {
        byte[] expected = PLAIN.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, Base64Util.decodeToBytes(ENCODED));
    }

    @Test
    @DisplayName("decodeToBytes returns null for null input")
    void decodeToBytes_null() {
        assertNull(Base64Util.decodeToBytes(null));
    }

    // ──────────────────────────────────────────────
    // decode(byte[])
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decode(byte[]) recovers original text from encoded bytes")
    void decode_bytes() {
        byte[] encodedBytes = ENCODED.getBytes(StandardCharsets.UTF_8);
        assertEquals(PLAIN, Base64Util.decode(encodedBytes));
    }

    @Test
    @DisplayName("decode(byte[]) returns null for null input")
    void decode_bytes_null() {
        assertNull(Base64Util.decode((byte[]) null));
    }

    // ──────────────────────────────────────────────
    // isBase64
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("isBase64 returns true for a valid Base64 string")
    void isBase64_valid() {
        assertTrue(Base64Util.isBase64(ENCODED));
    }

    @Test
    @DisplayName("isBase64 returns false for null")
    void isBase64_null() {
        assertFalse(Base64Util.isBase64(null));
    }

    @Test
    @DisplayName("isBase64 returns false for an invalid Base64 string")
    void isBase64_invalid() {
        assertFalse(Base64Util.isBase64("this is not base64!!!"));
    }

    // ──────────────────────────────────────────────
    // Round-trip
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode and decode are inverse operations for various inputs")
    void roundTrip() {
        String[] inputs = {"", "a", "ab", "abc", "Hello, World!", "Special: !@#$%^&*()", "中文字符"};
        for (String input : inputs) {
            assertEquals(input, Base64Util.decode(Base64Util.encode(input)),
                    "Round-trip failed for: " + input);
        }
    }

    @Test
    @DisplayName("encode(byte[]) and decodeToBytes are inverse operations")
    void roundTrip_bytes() {
        byte[] original = new byte[]{0x00, 0x01, (byte) 0xFF, (byte) 0xFE, 0x7F};
        assertArrayEquals(original, Base64Util.decodeToBytes(Base64Util.encode(original)));
    }

    // ──────────────────────────────────────────────
    // Charset-specific encoding (e.g. Big5)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(String, Charset) with Big5 produces different bytes than UTF-8")
    void encode_big5_differentFromUtf8() {
        Charset big5 = Charset.forName("Big5");
        String chineseText = "台灣";  // Traditional Chinese — exists in both Big5 and UTF-8

        String encodedBig5 = Base64Util.encode(chineseText, big5);
        String encodedUtf8 = Base64Util.encode(chineseText, StandardCharsets.UTF_8);

        assertNotNull(encodedBig5);
        assertNotNull(encodedUtf8);
        // Big5 uses 2 bytes per character; UTF-8 uses 3 bytes per character for these code points
        assertNotEquals(encodedBig5, encodedUtf8,
                "Big5 and UTF-8 encodings should produce different Base64 strings");
    }

    @Test
    @DisplayName("encode(String, Charset) with Big5 round-trips correctly using decode(String, Charset)")
    void encode_big5_roundTrip() {
        Charset big5 = Charset.forName("Big5");
        String chineseText = "台灣繁體中文";

        String encoded = Base64Util.encode(chineseText, big5);
        String decoded = Base64Util.decode(encoded, big5);

        assertEquals(chineseText, decoded);
    }

    @Test
    @DisplayName("encodeUrlSafe(String, Charset) with Big5 produces URL-safe output and round-trips")
    void encodeUrlSafe_big5_roundTrip() {
        Charset big5 = Charset.forName("Big5");
        String chineseText = "台北市";

        String urlSafeEncoded = Base64Util.encodeUrlSafe(chineseText, big5);

        assertNotNull(urlSafeEncoded);
        assertFalse(urlSafeEncoded.contains("+"), "URL-safe Base64 must not contain '+'");
        assertFalse(urlSafeEncoded.contains("/"), "URL-safe Base64 must not contain '/'");

        // URL-safe Base64 (no padding) is decodable by the standard decoder
        String decoded = Base64Util.decode(urlSafeEncoded, big5);
        assertEquals(chineseText, decoded);
    }

    @Test
    @DisplayName("encodeUrlSafe(String, Charset) returns null when either argument is null")
    void encodeUrlSafe_charsetOverload_nullGuards() {
        Charset big5 = Charset.forName("Big5");
        assertNull(Base64Util.encodeUrlSafe(null, big5));
        assertNull(Base64Util.encodeUrlSafe("台灣", null));
    }

    @Test
    @DisplayName("encode(String, Charset) returns null when charset is null")
    void encode_charset_nullGuard() {
        assertNull(Base64Util.encode("台灣", null));
    }
}
