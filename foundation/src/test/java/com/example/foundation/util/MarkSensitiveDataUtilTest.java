package com.example.foundation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MarkSensitiveDataUtil Test")
class MarkSensitiveDataUtilTest {

    // ========== General Masking Tests ==========

    @Test
    @DisplayName("mask should mask middle characters keeping start and end")
    void testMask() {
        assertEquals("123****890", MarkSensitiveDataUtil.mask("1234567890", 3, 3));
        assertEquals("12******90", MarkSensitiveDataUtil.mask("1234567890", 2, 2));
        assertEquals("A12****789", MarkSensitiveDataUtil.mask("A123456789", 3, 3));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.mask(null, 3, 3));
        assertEquals("", MarkSensitiveDataUtil.mask("", 3, 3));
        assertEquals("***", MarkSensitiveDataUtil.mask("123", 3, 3)); // String too short
        assertEquals("*****", MarkSensitiveDataUtil.mask("12345", 3, 3)); // String too short
    }

    @Test
    @DisplayName("mask with custom mask character")
    void testMaskWithCustomChar() {
        assertEquals("123####890", MarkSensitiveDataUtil.mask("1234567890", 3, 3, "#"));
        assertEquals("12XXXXXX90", MarkSensitiveDataUtil.mask("1234567890", 2, 2, "X"));
        assertEquals("A--D", MarkSensitiveDataUtil.mask("ABCD", 1, 1, "-"));
    }

    @Test
    @DisplayName("maskExcept should mask all except specified positions")
    void testMaskExcept() {
        assertEquals("1***5****", MarkSensitiveDataUtil.maskExcept("123456789", 0, 4));
        assertEquals("*2*4*6*8*", MarkSensitiveDataUtil.maskExcept("123456789", 1, 3, 5, 7));
        assertEquals("A*C", MarkSensitiveDataUtil.maskExcept("ABC", 0, 2));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskExcept(null, 0));
        assertEquals("", MarkSensitiveDataUtil.maskExcept("", 0));
    }

    // ========== Phone Number Masking Tests ==========

    @Test
    @DisplayName("maskPhone should mask phone number keeping first 3 and last 4 digits")
    void testMaskPhone() {
        assertEquals("091***5678", MarkSensitiveDataUtil.maskPhone("0912345678"));
        assertEquals("091***5678", MarkSensitiveDataUtil.maskPhone("091-234-5678")); // With dashes
        assertEquals("091***5678", MarkSensitiveDataUtil.maskPhone("091 234 5678")); // With spaces
        assertEquals("091***5678", MarkSensitiveDataUtil.maskPhone("(091)2345678")); // With parentheses
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskPhone(null));
        assertEquals("", MarkSensitiveDataUtil.maskPhone(""));
        assertEquals("***", MarkSensitiveDataUtil.maskPhone("123")); // Too short
    }

    @Test
    @DisplayName("maskPhone with custom visible digits")
    void testMaskPhoneCustom() {
        assertEquals("09****5678", MarkSensitiveDataUtil.maskPhone("0912345678", 2, 4));
        assertEquals("0912**5678", MarkSensitiveDataUtil.maskPhone("0912345678", 4, 4));
        assertEquals("0*****5678", MarkSensitiveDataUtil.maskPhone("0912345678", 1, 4));
    }

    // ========== Email Masking Tests ==========

    @Test
    @DisplayName("maskEmail should mask username keeping first character and full domain")
    void testMaskEmail() {
        assertEquals("e******@gmail.com", MarkSensitiveDataUtil.maskEmail("example@gmail.com"));
        assertEquals("j***@test.com", MarkSensitiveDataUtil.maskEmail("john@test.com"));
        assertEquals("a***@domain.com", MarkSensitiveDataUtil.maskEmail("a@domain.com")); // Single char username still gets mask
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskEmail(null));
        assertEquals("", MarkSensitiveDataUtil.maskEmail(""));
        assertEquals("***", MarkSensitiveDataUtil.maskEmail("invalid")); // No @ sign
        assertEquals("***", MarkSensitiveDataUtil.maskEmail("@domain.com")); // Empty username
    }

    @Test
    @DisplayName("maskEmailStrict should mask both username and domain")
    void testMaskEmailStrict() {
        assertEquals("e******@g****.com", MarkSensitiveDataUtil.maskEmailStrict("example@gmail.com"));
        assertEquals("j***@t***.com", MarkSensitiveDataUtil.maskEmailStrict("john@test.com"));
        assertEquals("a@d*****.com", MarkSensitiveDataUtil.maskEmailStrict("a@domain.com"));
        assertEquals("t***@t.co", MarkSensitiveDataUtil.maskEmailStrict("test@t.co"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskEmailStrict(null));
        assertEquals("", MarkSensitiveDataUtil.maskEmailStrict(""));
        assertEquals("***", MarkSensitiveDataUtil.maskEmailStrict("invalid"));
    }

    // ========== ID Number Masking Tests ==========

    @Test
    @DisplayName("maskIdNumber should mask ID keeping first 3 and last 3 characters")
    void testMaskIdNumber() {
        assertEquals("A12****789", MarkSensitiveDataUtil.maskIdNumber("A123456789"));
        assertEquals("B23****890", MarkSensitiveDataUtil.maskIdNumber("B234567890"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskIdNumber(null));
        assertEquals("", MarkSensitiveDataUtil.maskIdNumber(""));
        assertEquals("***", MarkSensitiveDataUtil.maskIdNumber("A12")); // Too short
    }

    @Test
    @DisplayName("maskIdNumber with custom visible characters")
    void testMaskIdNumberCustom() {
        assertEquals("A1*****789", MarkSensitiveDataUtil.maskIdNumber("A123456789", 2, 3));
        assertEquals("A123**6789", MarkSensitiveDataUtil.maskIdNumber("A123456789", 4, 4));
    }

    // ========== Credit Card Masking Tests ==========

    @Test
    @DisplayName("maskCreditCard should show only last 4 digits")
    void testMaskCreditCard() {
        assertEquals("************3456", MarkSensitiveDataUtil.maskCreditCard("1234567890123456"));
        assertEquals("**********2345", MarkSensitiveDataUtil.maskCreditCard("12345678912345"));
        assertEquals("************3456", MarkSensitiveDataUtil.maskCreditCard("1234-5678-9012-3456")); // With dashes
        assertEquals("************3456", MarkSensitiveDataUtil.maskCreditCard("1234 5678 9012 3456")); // With spaces
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskCreditCard(null));
        assertEquals("", MarkSensitiveDataUtil.maskCreditCard(""));
        assertEquals("***", MarkSensitiveDataUtil.maskCreditCard("123")); // Too short
    }

    @Test
    @DisplayName("maskCreditCardFormatted should mask with formatting")
    void testMaskCreditCardFormatted() {
        assertEquals("xxxx-xxxx-xxxx-3456", MarkSensitiveDataUtil.maskCreditCardFormatted("1234567890123456"));
        assertEquals("xxxx-xxxx-xxxx-3456", MarkSensitiveDataUtil.maskCreditCardFormatted("1234-5678-9012-3456"));
        String result = MarkSensitiveDataUtil.maskCreditCardFormatted("12345678912345");
        assertNotNull(result);
        assertTrue(result.endsWith("2345"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskCreditCardFormatted(null));
        assertEquals("", MarkSensitiveDataUtil.maskCreditCardFormatted(""));
    }

    // ========== Name Masking Tests ==========

    @Test
    @DisplayName("maskName should mask name keeping first character of each part")
    void testMaskName() {
        assertEquals("J*** D**", MarkSensitiveDataUtil.maskName("John Doe"));
        assertEquals("J*** S****", MarkSensitiveDataUtil.maskName("Jane Smith"));
        assertEquals("A", MarkSensitiveDataUtil.maskName("A")); // Single character
        assertEquals("A B C", MarkSensitiveDataUtil.maskName("A B C")); // Single characters
        assertEquals("J***", MarkSensitiveDataUtil.maskName("John"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskName(null));
        assertEquals("", MarkSensitiveDataUtil.maskName(""));
    }

    @Test
    @DisplayName("maskChineseName should mask Chinese name keeping first character")
    void testMaskChineseName() {
        assertEquals("王**", MarkSensitiveDataUtil.maskChineseName("王小明"));
        assertEquals("李*", MarkSensitiveDataUtil.maskChineseName("李四"));
        assertEquals("陳", MarkSensitiveDataUtil.maskChineseName("陳")); // Single character
        assertEquals("歐***", MarkSensitiveDataUtil.maskChineseName("歐陽小明"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskChineseName(null));
        assertEquals("", MarkSensitiveDataUtil.maskChineseName(""));
    }

    // ========== Address Masking Tests ==========

    @Test
    @DisplayName("maskAddress should mask address keeping first 6 characters")
    void testMaskAddress() {
        assertEquals("台北市信義區********", MarkSensitiveDataUtil.maskAddress("台北市信義區忠孝東路100號"));
        assertEquals("新北市板橋區*********", MarkSensitiveDataUtil.maskAddress("新北市板橋區文化路一段100號"));
        assertEquals("ABC", MarkSensitiveDataUtil.maskAddress("ABC")); // Too short
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskAddress(null));
        assertEquals("", MarkSensitiveDataUtil.maskAddress(""));
    }

    @Test
    @DisplayName("maskAddress with custom visible characters")
    void testMaskAddressCustom() {
        assertEquals("台北市***********", MarkSensitiveDataUtil.maskAddress("台北市信義區忠孝東路100號", 3));
        assertEquals("台北市信**********", MarkSensitiveDataUtil.maskAddress("台北市信義區忠孝東路100號", 4));
        assertEquals("台北市信義*********", MarkSensitiveDataUtil.maskAddress("台北市信義區忠孝東路100號", 5));
    }

    // ========== Bank Account Masking Tests ==========

    @Test
    @DisplayName("maskBankAccount should show only last 4 digits")
    void testMaskBankAccount() {
        assertEquals("*********0123", MarkSensitiveDataUtil.maskBankAccount("1234567890123"));
        assertEquals("**********1234", MarkSensitiveDataUtil.maskBankAccount("12345678901234"));
        assertEquals("*********0123", MarkSensitiveDataUtil.maskBankAccount("1234-5678-90123")); // With dashes
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskBankAccount(null));
        assertEquals("", MarkSensitiveDataUtil.maskBankAccount(""));
        assertEquals("***", MarkSensitiveDataUtil.maskBankAccount("123")); // Too short
    }

    @Test
    @DisplayName("maskBankAccountPartial should keep first 3 and last 4 digits")
    void testMaskBankAccountPartial() {
        assertEquals("123******0123", MarkSensitiveDataUtil.maskBankAccountPartial("1234567890123"));
        assertEquals("123*******1234", MarkSensitiveDataUtil.maskBankAccountPartial("12345678901234"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskBankAccountPartial(null));
        assertEquals("", MarkSensitiveDataUtil.maskBankAccountPartial(""));
    }

    // ========== Password Masking Tests ==========

    @Test
    @DisplayName("maskPassword should return fixed length mask")
    void testMaskPassword() {
        assertEquals("********", MarkSensitiveDataUtil.maskPassword("password123"));
        assertEquals("********", MarkSensitiveDataUtil.maskPassword("short"));
        assertEquals("********", MarkSensitiveDataUtil.maskPassword("verylongpassword123456"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskPassword(null));
        assertEquals("", MarkSensitiveDataUtil.maskPassword(""));
    }

    @Test
    @DisplayName("maskPasswordWithLength should return mask with actual password length")
    void testMaskPasswordWithLength() {
        assertEquals("***********", MarkSensitiveDataUtil.maskPasswordWithLength("password123"));
        assertEquals("*****", MarkSensitiveDataUtil.maskPasswordWithLength("short"));
        assertEquals("**********************", MarkSensitiveDataUtil.maskPasswordWithLength("verylongpassword123456"));
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskPasswordWithLength(null));
        assertEquals("", MarkSensitiveDataUtil.maskPasswordWithLength(""));
    }

    // ========== Custom Masking Tests ==========

    @Test
    @DisplayName("maskFrom should mask from specific position to end")
    void testMaskFrom() {
        assertEquals("1234******", MarkSensitiveDataUtil.maskFrom("1234567890", 4));
        assertEquals("12********", MarkSensitiveDataUtil.maskFrom("1234567890", 2));
        assertEquals("**********", MarkSensitiveDataUtil.maskFrom("1234567890", 0));
        assertEquals("1234567890", MarkSensitiveDataUtil.maskFrom("1234567890", 10)); // Position at end
        assertEquals("1234567890", MarkSensitiveDataUtil.maskFrom("1234567890", 15)); // Position beyond end
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskFrom(null, 3));
        assertEquals("", MarkSensitiveDataUtil.maskFrom("", 3));
    }

    @Test
    @DisplayName("maskUntil should mask from start to specific position")
    void testMaskUntil() {
        assertEquals("****567890", MarkSensitiveDataUtil.maskUntil("1234567890", 4));
        assertEquals("**34567890", MarkSensitiveDataUtil.maskUntil("1234567890", 2));
        assertEquals("**********", MarkSensitiveDataUtil.maskUntil("1234567890", 10)); // Position at end
        assertEquals("**********", MarkSensitiveDataUtil.maskUntil("1234567890", 15)); // Position beyond end
        assertEquals("1234567890", MarkSensitiveDataUtil.maskUntil("1234567890", 0)); // No masking
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskUntil(null, 3));
        assertEquals("", MarkSensitiveDataUtil.maskUntil("", 3));
    }

    @Test
    @DisplayName("maskBetween should mask between specific positions")
    void testMaskBetween() {
        assertEquals("1234***890", MarkSensitiveDataUtil.maskBetween("1234567890", 4, 7));
        assertEquals("12*****890", MarkSensitiveDataUtil.maskBetween("1234567890", 2, 7));
        assertEquals("**********", MarkSensitiveDataUtil.maskBetween("1234567890", 0, 10));
        assertEquals("123*******", MarkSensitiveDataUtil.maskBetween("1234567890", 3, 15)); // End beyond length
        assertEquals("1234567890", MarkSensitiveDataUtil.maskBetween("1234567890", 5, 5)); // Same start and end
        assertEquals("1234567890", MarkSensitiveDataUtil.maskBetween("1234567890", 15, 20)); // Start beyond length
        
        // Edge cases
        assertNull(MarkSensitiveDataUtil.maskBetween(null, 2, 5));
        assertEquals("", MarkSensitiveDataUtil.maskBetween("", 2, 5));
    }

    // ========== Utility Method Tests ==========

    @Test
    @DisplayName("isMasked should detect if string contains mask characters")
    void testIsMasked() {
        assertTrue(MarkSensitiveDataUtil.isMasked("123****567"));
        assertTrue(MarkSensitiveDataUtil.isMasked("***"));
        assertTrue(MarkSensitiveDataUtil.isMasked("a*b"));
        assertFalse(MarkSensitiveDataUtil.isMasked("1234567890"));
        assertFalse(MarkSensitiveDataUtil.isMasked(""));
        assertFalse(MarkSensitiveDataUtil.isMasked(null));
    }

    @Test
    @DisplayName("getDefaultMaskChar should return default mask character")
    void testGetDefaultMaskChar() {
        assertEquals("*", MarkSensitiveDataUtil.getDefaultMaskChar());
    }

    @Test
    @DisplayName("getDefaultMask should return default mask string")
    void testGetDefaultMask() {
        assertEquals("***", MarkSensitiveDataUtil.getDefaultMask());
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("should handle multiple consecutive spaces in names")
    void testMultipleSpacesInNames() {
        // split("\\s+") collapses multiple spaces into single space in output
        assertEquals("J*** D**", MarkSensitiveDataUtil.maskName("John  Doe"));
        assertEquals("J*** S****", MarkSensitiveDataUtil.maskName("Jane   Smith"));
    }

    @Test
    @DisplayName("should handle international phone formats")
    void testInternationalPhoneFormats() {
        // + sign is not removed, becomes part of the string
        String result1 = MarkSensitiveDataUtil.maskPhone("+886-912-345-678", 3, 4);
        assertNotNull(result1);
        assertTrue(result1.endsWith("5678"));
        
        String result2 = MarkSensitiveDataUtil.maskPhone("+1(001)234-5678", 3, 4);
        assertNotNull(result2);
        assertTrue(result2.endsWith("5678"));
    }

    @Test
    @DisplayName("should handle emails with subdomains")
    void testEmailsWithSubdomains() {
        assertEquals("a****@mail.example.com", MarkSensitiveDataUtil.maskEmail("admin@mail.example.com"));
        assertEquals("u***@subdomain.example.co.uk", MarkSensitiveDataUtil.maskEmail("user@subdomain.example.co.uk"));
    }

    @Test
    @DisplayName("should handle empty or whitespace strings")
    void testEmptyOrWhitespace() {
        assertEquals("   ", MarkSensitiveDataUtil.maskName("   "));
        assertEquals(" ", MarkSensitiveDataUtil.maskPhone(" "));
        assertEquals("  ", MarkSensitiveDataUtil.maskEmail("  "));
    }

    @Test
    @DisplayName("should handle special characters in data")
    void testSpecialCharacters() {
        assertEquals("A#****89", MarkSensitiveDataUtil.mask("A#234589", 2, 2));
        assertEquals("王**", MarkSensitiveDataUtil.maskChineseName("王@明")); // Masks @ as it's part of the string
        assertEquals("********", MarkSensitiveDataUtil.maskPassword("$%^&*()")); // Always returns 8 asterisks
    }
}
