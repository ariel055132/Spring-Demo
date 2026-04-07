package com.example.foundation.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SensitiveType Enum Test")
class SensitiveTypeTest {

    @Test
    @DisplayName("All SensitiveType values should exist")
    void testAllValuesExist() {
        SensitiveType[] types = SensitiveType.values();
        assertEquals(15, types.length);
        
        // Verify all expected types exist
        assertNotNull(SensitiveType.valueOf("DEFAULT"));
        assertNotNull(SensitiveType.valueOf("PHONE"));
        assertNotNull(SensitiveType.valueOf("EMAIL"));
        assertNotNull(SensitiveType.valueOf("EMAIL_STRICT"));
        assertNotNull(SensitiveType.valueOf("ID_NUMBER"));
        assertNotNull(SensitiveType.valueOf("CREDIT_CARD"));
        assertNotNull(SensitiveType.valueOf("CREDIT_CARD_FORMATTED"));
        assertNotNull(SensitiveType.valueOf("NAME"));
        assertNotNull(SensitiveType.valueOf("CHINESE_NAME"));
        assertNotNull(SensitiveType.valueOf("ADDRESS"));
        assertNotNull(SensitiveType.valueOf("BANK_ACCOUNT"));
        assertNotNull(SensitiveType.valueOf("BANK_ACCOUNT_PARTIAL"));
        assertNotNull(SensitiveType.valueOf("PASSWORD"));
        assertNotNull(SensitiveType.valueOf("PASSWORD_WITH_LENGTH"));
        assertNotNull(SensitiveType.valueOf("CUSTOM"));
    }

    @Test
    @DisplayName("getDescription should return meaningful descriptions")
    void testGetDescription() {
        assertEquals("Default masking (keep first 3 and last 3)", SensitiveType.DEFAULT.getDescription());
        assertEquals("Phone number", SensitiveType.PHONE.getDescription());
        assertEquals("Email address", SensitiveType.EMAIL.getDescription());
        assertEquals("Email address (strict)", SensitiveType.EMAIL_STRICT.getDescription());
        assertEquals("ID number", SensitiveType.ID_NUMBER.getDescription());
        assertEquals("Credit card number", SensitiveType.CREDIT_CARD.getDescription());
        assertEquals("Credit card number (formatted)", SensitiveType.CREDIT_CARD_FORMATTED.getDescription());
        assertEquals("Person name", SensitiveType.NAME.getDescription());
        assertEquals("Chinese name", SensitiveType.CHINESE_NAME.getDescription());
        assertEquals("Address", SensitiveType.ADDRESS.getDescription());
        assertEquals("Bank account number", SensitiveType.BANK_ACCOUNT.getDescription());
        assertEquals("Bank account number (partial)", SensitiveType.BANK_ACCOUNT_PARTIAL.getDescription());
        assertEquals("Password", SensitiveType.PASSWORD.getDescription());
        assertEquals("Password (with length)", SensitiveType.PASSWORD_WITH_LENGTH.getDescription());
        assertEquals("Custom masking", SensitiveType.CUSTOM.getDescription());
    }

    @Test
    @DisplayName("supportsCustomParameters should return true only for CUSTOM and DEFAULT")
    void testSupportsCustomParameters() {
        assertTrue(SensitiveType.CUSTOM.supportsCustomParameters());
        assertTrue(SensitiveType.DEFAULT.supportsCustomParameters());
        
        assertFalse(SensitiveType.PHONE.supportsCustomParameters());
        assertFalse(SensitiveType.EMAIL.supportsCustomParameters());
        assertFalse(SensitiveType.EMAIL_STRICT.supportsCustomParameters());
        assertFalse(SensitiveType.ID_NUMBER.supportsCustomParameters());
        assertFalse(SensitiveType.CREDIT_CARD.supportsCustomParameters());
        assertFalse(SensitiveType.CREDIT_CARD_FORMATTED.supportsCustomParameters());
        assertFalse(SensitiveType.NAME.supportsCustomParameters());
        assertFalse(SensitiveType.CHINESE_NAME.supportsCustomParameters());
        assertFalse(SensitiveType.ADDRESS.supportsCustomParameters());
        assertFalse(SensitiveType.BANK_ACCOUNT.supportsCustomParameters());
        assertFalse(SensitiveType.BANK_ACCOUNT_PARTIAL.supportsCustomParameters());
        assertFalse(SensitiveType.PASSWORD.supportsCustomParameters());
        assertFalse(SensitiveType.PASSWORD_WITH_LENGTH.supportsCustomParameters());
    }

    @Test
    @DisplayName("Enum values should be comparable")
    void testEnumComparison() {
        assertEquals(SensitiveType.PHONE, SensitiveType.PHONE);
        assertNotEquals(SensitiveType.PHONE, SensitiveType.EMAIL);
    }

    @Test
    @DisplayName("Enum should work in switch statements")
    void testEnumInSwitch() {
        String result = switch (SensitiveType.PHONE) {
            case PHONE -> "phone";
            case EMAIL -> "email";
            default -> "other";
        };
        assertEquals("phone", result);
    }

    @Test
    @DisplayName("valueOf should throw exception for invalid value")
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            SensitiveType.valueOf("INVALID_TYPE");
        });
    }

    @Test
    @DisplayName("Each type should have unique ordinal")
    void testUniqueOrdinals() {
        SensitiveType[] types = SensitiveType.values();
        java.util.Set<Integer> ordinals = new java.util.HashSet<>();
        
        for (SensitiveType type : types) {
            assertTrue(ordinals.add(type.ordinal()), "Duplicate ordinal found: " + type.ordinal());
        }
    }

    @Test
    @DisplayName("Description should not be null or empty for any type")
    void testDescriptionNotEmpty() {
        for (SensitiveType type : SensitiveType.values()) {
            assertNotNull(type.getDescription());
            assertFalse(type.getDescription().isEmpty());
        }
    }
}
