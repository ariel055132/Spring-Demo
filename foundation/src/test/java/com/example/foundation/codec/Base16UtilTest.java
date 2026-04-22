package com.example.foundation.codec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Base16Util Test")
class Base16UtilTest {

    private static final String PLAIN = "Hello, World!";
    // echo -n "Hello, World!" | xxd -p  →  48656c6c6f2c20576f726c6421
    private static final String ENCODED_LOWER = "48656c6c6f2c20576f726c6421";
    private static final String ENCODED_UPPER = "48656C6C6F2C20576F726C6421";

    // ──────────────────────────────────────────────
    // encode(String)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(String) produces correct lowercase hex string")
    void encode_string() {
        assertEquals(ENCODED_LOWER, Base16Util.encode(PLAIN));
    }

    @Test
    @DisplayName("encode(String) returns null for null input")
    void encode_string_null() {
        assertNull(Base16Util.encode((String) null));
    }

    @Test
    @DisplayName("encode(String) handles empty string")
    void encode_string_empty() {
        assertEquals("", Base16Util.encode(""));
    }

    // ──────────────────────────────────────────────
    // encode(byte[])
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(byte[]) produces correct lowercase hex string")
    void encode_bytes() {
        assertEquals(ENCODED_LOWER, Base16Util.encode(PLAIN.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("encode(byte[]) returns null for null input")
    void encode_bytes_null() {
        assertNull(Base16Util.encode((byte[]) null));
    }

    // ──────────────────────────────────────────────
    // encode(String, Charset)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(String, Charset) produces correct output with explicit UTF-8 charset")
    void encode_stringWithCharset() {
        assertEquals(ENCODED_LOWER, Base16Util.encode(PLAIN, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("encode(String, Charset) returns null when charset is null")
    void encode_stringWithCharset_nullCharset() {
        assertNull(Base16Util.encode(PLAIN, null));
    }

    @Test
    @DisplayName("encode(String, Charset) returns null when text is null")
    void encode_stringWithCharset_nullText() {
        assertNull(Base16Util.encode(null, StandardCharsets.UTF_8));
    }

    // ──────────────────────────────────────────────
    // encodeUpperCase
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encodeUpperCase(String) produces correct uppercase hex string")
    void encodeUpperCase_string() {
        assertEquals(ENCODED_UPPER, Base16Util.encodeUpperCase(PLAIN));
    }

    @Test
    @DisplayName("encodeUpperCase(byte[]) produces correct uppercase hex string")
    void encodeUpperCase_bytes() {
        assertEquals(ENCODED_UPPER, Base16Util.encodeUpperCase(PLAIN.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("encodeUpperCase(String) returns null for null input")
    void encodeUpperCase_null() {
        assertNull(Base16Util.encodeUpperCase((String) null));
        assertNull(Base16Util.encodeUpperCase((byte[]) null));
    }

    @Test
    @DisplayName("encodeUpperCase(String, Charset) returns null when either argument is null")
    void encodeUpperCase_charsetNullGuards() {
        assertNull(Base16Util.encodeUpperCase(null, StandardCharsets.UTF_8));
        assertNull(Base16Util.encodeUpperCase(PLAIN, null));
    }

    @Test
    @DisplayName("encodeUpperCase output is same value as lowercase, just uppercased")
    void encodeUpperCase_equalsLowerIgnoreCase() {
        assertEquals(
                Base16Util.encode(PLAIN).toUpperCase(),
                Base16Util.encodeUpperCase(PLAIN)
        );
    }

    // ──────────────────────────────────────────────
    // decode(String)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decode(String) recovers original plain text from lowercase hex")
    void decode_lowerHex() {
        assertEquals(PLAIN, Base16Util.decode(ENCODED_LOWER));
    }

    @Test
    @DisplayName("decode(String) recovers original plain text from uppercase hex")
    void decode_upperHex() {
        assertEquals(PLAIN, Base16Util.decode(ENCODED_UPPER));
    }

    @Test
    @DisplayName("decode(String) returns null for null input")
    void decode_null() {
        assertNull(Base16Util.decode((String) null));
    }

    @Test
    @DisplayName("decode(String) handles empty hex string")
    void decode_empty() {
        assertEquals("", Base16Util.decode(""));
    }

    @Test
    @DisplayName("decode(String) throws RuntimeException for invalid hex input")
    void decode_invalid() {
        assertThrows(RuntimeException.class, () -> Base16Util.decode("ZZZ"));
    }

    // ──────────────────────────────────────────────
    // decode(String, Charset)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decode(String, Charset) recovers original text with explicit charset")
    void decode_withCharset() {
        assertEquals(PLAIN, Base16Util.decode(ENCODED_LOWER, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("decode(String, Charset) returns null when charset is null")
    void decode_nullCharset() {
        assertNull(Base16Util.decode(ENCODED_LOWER, null));
    }

    // ──────────────────────────────────────────────
    // decodeToBytes
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("decodeToBytes returns correct byte array")
    void decodeToBytes() {
        byte[] expected = PLAIN.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, Base16Util.decodeToBytes(ENCODED_LOWER));
    }

    @Test
    @DisplayName("decodeToBytes returns null for null input")
    void decodeToBytes_null() {
        assertNull(Base16Util.decodeToBytes(null));
    }

    @Test
    @DisplayName("decodeToBytes throws RuntimeException for invalid hex input")
    void decodeToBytes_invalid() {
        assertThrows(RuntimeException.class, () -> Base16Util.decodeToBytes("GG"));
    }

    // ──────────────────────────────────────────────
    // isHex
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("isHex returns true for a valid lowercase hex string")
    void isHex_validLower() {
        assertTrue(Base16Util.isHex(ENCODED_LOWER));
    }

    @Test
    @DisplayName("isHex returns true for a valid uppercase hex string")
    void isHex_validUpper() {
        assertTrue(Base16Util.isHex(ENCODED_UPPER));
    }

    @Test
    @DisplayName("isHex returns true for empty string")
    void isHex_empty() {
        assertTrue(Base16Util.isHex(""));
    }

    @Test
    @DisplayName("isHex returns false for null")
    void isHex_null() {
        assertFalse(Base16Util.isHex(null));
    }

    @Test
    @DisplayName("isHex returns false for odd-length string")
    void isHex_oddLength() {
        assertFalse(Base16Util.isHex("abc"));
    }

    @Test
    @DisplayName("isHex returns false for string with non-hex characters")
    void isHex_invalidChars() {
        assertFalse(Base16Util.isHex("GGGG"));
        assertFalse(Base16Util.isHex("hello!"));
    }

    // ──────────────────────────────────────────────
    // Charset-specific encoding (e.g. Big5)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode(String, Charset) with Big5 produces different bytes than UTF-8")
    void encode_big5_differentFromUtf8() {
        Charset big5 = Charset.forName("Big5");
        String chineseText = "台灣";

        String encodedBig5 = Base16Util.encode(chineseText, big5);
        String encodedUtf8 = Base16Util.encode(chineseText, StandardCharsets.UTF_8);

        assertNotNull(encodedBig5);
        assertNotNull(encodedUtf8);
        // Big5 = 2 bytes/char; UTF-8 = 3 bytes/char for these code points
        assertNotEquals(encodedBig5, encodedUtf8);
    }

    @Test
    @DisplayName("encode(String, Charset) with Big5 round-trips correctly using decode(String, Charset)")
    void encode_big5_roundTrip() {
        Charset big5 = Charset.forName("Big5");
        String chineseText = "台灣繁體中文";

        String encoded = Base16Util.encode(chineseText, big5);
        String decoded = Base16Util.decode(encoded, big5);

        assertEquals(chineseText, decoded);
    }

    @Test
    @DisplayName("encodeUpperCase(String, Charset) with Big5 round-trips correctly")
    void encodeUpperCase_big5_roundTrip() {
        Charset big5 = Charset.forName("Big5");
        String chineseText = "台北市";

        String encoded = Base16Util.encodeUpperCase(chineseText, big5);

        assertNotNull(encoded);
        // Uppercase hex only
        assertEquals(encoded, encoded.toUpperCase());

        String decoded = Base16Util.decode(encoded, big5);
        assertEquals(chineseText, decoded);
    }

    // ──────────────────────────────────────────────
    // Round-trip
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("encode and decode are inverse operations for various inputs")
    void roundTrip() {
        String[] inputs = {"", "a", "ab", "abc", "Hello, World!", "Special: !@#$%^&*()", "中文字符"};
        for (String input : inputs) {
            assertEquals(input, Base16Util.decode(Base16Util.encode(input)),
                    "Round-trip failed for: " + input);
        }
    }

    @Test
    @DisplayName("encode(byte[]) and decodeToBytes are inverse operations")
    void roundTrip_bytes() {
        byte[] original = new byte[]{0x00, 0x01, (byte) 0xFF, (byte) 0xFE, 0x7F};
        assertArrayEquals(original, Base16Util.decodeToBytes(Base16Util.encode(original)));
    }
}
