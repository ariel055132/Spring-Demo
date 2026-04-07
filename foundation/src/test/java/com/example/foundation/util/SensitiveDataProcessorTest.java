package com.example.foundation.util;

import com.example.foundation.annotation.SensitiveData;
import com.example.foundation.enums.SensitiveType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SensitiveDataProcessor Test")
class SensitiveDataProcessorTest {

    // Test model classes
    static class UserDTO {
        @SensitiveData(type = SensitiveType.PHONE)
        private String phoneNumber;

        @SensitiveData(type = SensitiveType.EMAIL)
        private String email;

        @SensitiveData(type = SensitiveType.ID_NUMBER)
        private String idNumber;

        private String username; // Not sensitive

        public UserDTO(String phoneNumber, String email, String idNumber, String username) {
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.idNumber = idNumber;
            this.username = username;
        }
    }

    static class PaymentDTO {
        @SensitiveData(type = SensitiveType.CREDIT_CARD)
        private String cardNumber;

        @SensitiveData(type = SensitiveType.PASSWORD)
        private String cvv;

        @SensitiveData(type = SensitiveType.NAME)
        private String cardHolderName;

        public PaymentDTO(String cardNumber, String cvv, String cardHolderName) {
            this.cardNumber = cardNumber;
            this.cvv = cvv;
            this.cardHolderName = cardHolderName;
        }
    }

    static class CustomMaskDTO {
        @SensitiveData(type = SensitiveType.CUSTOM, keepStart = 2, keepEnd = 2)
        private String customField;

        @SensitiveData(type = SensitiveType.CUSTOM, keepStart = 1, keepEnd = 4, maskChar = "#")
        private String customField2;

        public CustomMaskDTO(String customField, String customField2) {
            this.customField = customField;
            this.customField2 = customField2;
        }
    }

    static class DisabledMaskDTO {
        @SensitiveData(type = SensitiveType.PHONE, enabled = false)
        private String phoneNumber;

        @SensitiveData(type = SensitiveType.EMAIL, enabled = true)
        private String email;

        public DisabledMaskDTO(String phoneNumber, String email) {
            this.phoneNumber = phoneNumber;
            this.email = email;
        }
    }

    static class MixedDTO {
        @SensitiveData(type = SensitiveType.PHONE)
        private String phone;

        private String publicInfo;

        @SensitiveData(type = SensitiveType.EMAIL)
        private String email;

        public MixedDTO(String phone, String publicInfo, String email) {
            this.phone = phone;
            this.publicInfo = publicInfo;
            this.email = email;
        }
    }

    static class ChineseDataDTO {
        @SensitiveData(type = SensitiveType.CHINESE_NAME)
        private String name;

        @SensitiveData(type = SensitiveType.ADDRESS)
        private String address;

        public ChineseDataDTO(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }

    // ========== maskSensitiveFields Tests ==========

    @Test
    @DisplayName("maskSensitiveFields should mask all annotated fields")
    void testMaskSensitiveFields() {
        UserDTO user = new UserDTO("0912345678", "test@example.com", "A123456789", "johndoe");
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(user);

        assertEquals(4, result.size());
        assertEquals("091***5678", result.get("phoneNumber"));
        assertEquals("t***@example.com", result.get("email"));
        assertEquals("A12****789", result.get("idNumber"));
        assertEquals("johndoe", result.get("username")); // Not masked
    }

    @Test
    @DisplayName("maskSensitiveFields should handle null object")
    void testMaskSensitiveFieldsNull() {
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("maskSensitiveFields should handle payment data")
    void testMaskPaymentFields() {
        PaymentDTO payment = new PaymentDTO("1234567890123456", "123", "John Doe");
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(payment);

        assertEquals(3, result.size());
        assertEquals("************3456", result.get("cardNumber"));
        assertEquals("********", result.get("cvv"));
        assertEquals("J*** D**", result.get("cardHolderName"));
    }

    @Test
    @DisplayName("maskSensitiveFields should respect custom masking")
    void testCustomMasking() {
        CustomMaskDTO custom = new CustomMaskDTO("1234567890", "ABCDEFGH");
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(custom);

        assertEquals(2, result.size());
        assertEquals("12******90", result.get("customField"));
        assertEquals("A###EFGH", result.get("customField2"));
    }

    @Test
    @DisplayName("maskSensitiveFields should respect enabled flag")
    void testEnabledFlag() {
        DisabledMaskDTO dto = new DisabledMaskDTO("0912345678", "test@example.com");
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(dto);

        assertEquals(2, result.size());
        assertEquals("0912345678", result.get("phoneNumber")); // Not masked (disabled)
        assertEquals("t***@example.com", result.get("email")); // Masked (enabled)
    }

    // ========== maskValue Tests ==========

    @Test
    @DisplayName("maskValue should mask phone numbers correctly")
    void testMaskValuePhone() throws NoSuchFieldException {
        Field field = UserDTO.class.getDeclaredField("phoneNumber");
        SensitiveData annotation = field.getAnnotation(SensitiveData.class);

        String result = SensitiveDataProcessor.maskValue("0912345678", annotation);
        assertEquals("091***5678", result);
    }

    @Test
    @DisplayName("maskValue should mask email correctly")
    void testMaskValueEmail() throws NoSuchFieldException {
        Field field = UserDTO.class.getDeclaredField("email");
        SensitiveData annotation = field.getAnnotation(SensitiveData.class);

        String result = SensitiveDataProcessor.maskValue("test@example.com", annotation);
        assertEquals("t***@example.com", result);
    }

    @Test
    @DisplayName("maskValue should handle null or blank values")
    void testMaskValueNullOrBlank() throws NoSuchFieldException {
        Field field = UserDTO.class.getDeclaredField("phoneNumber");
        SensitiveData annotation = field.getAnnotation(SensitiveData.class);

        assertNull(SensitiveDataProcessor.maskValue(null, annotation));
        assertEquals("", SensitiveDataProcessor.maskValue("", annotation));
        assertEquals("   ", SensitiveDataProcessor.maskValue("   ", annotation));
    }

    @Test
    @DisplayName("maskValue should handle all sensitive types")
    void testMaskValueAllTypes() throws NoSuchFieldException {
        // Test each type with appropriate data
        assertNotNull(createAndTestMask(SensitiveType.PHONE, "0912345678"));
        assertNotNull(createAndTestMask(SensitiveType.EMAIL, "test@example.com"));
        assertNotNull(createAndTestMask(SensitiveType.EMAIL_STRICT, "test@example.com"));
        assertNotNull(createAndTestMask(SensitiveType.ID_NUMBER, "A123456789"));
        assertNotNull(createAndTestMask(SensitiveType.CREDIT_CARD, "1234567890123456"));
        assertNotNull(createAndTestMask(SensitiveType.NAME, "John Doe"));
        assertNotNull(createAndTestMask(SensitiveType.CHINESE_NAME, "王小明"));
        assertNotNull(createAndTestMask(SensitiveType.ADDRESS, "台北市信義區忠孝東路100號"));
        assertNotNull(createAndTestMask(SensitiveType.BANK_ACCOUNT, "1234567890123"));
        assertNotNull(createAndTestMask(SensitiveType.PASSWORD, "password123"));
    }

    private String createAndTestMask(SensitiveType type, String value) {
        SensitiveData mockAnnotation = new SensitiveData() {
            @Override public SensitiveType type() { return type; }
            @Override public int keepStart() { return 0; }
            @Override public int keepEnd() { return 0; }
            @Override public String maskChar() { return "*"; }
            @Override public String description() { return ""; }
            @Override public boolean enabled() { return true; }
            @Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return SensitiveData.class; }
        };
        return SensitiveDataProcessor.maskValue(value, mockAnnotation);
    }

    // ========== isSensitiveField Tests ==========

    @Test
    @DisplayName("isSensitiveField should identify sensitive fields")
    void testIsSensitiveField() throws NoSuchFieldException {
        assertTrue(SensitiveDataProcessor.isSensitiveField(
            UserDTO.class.getDeclaredField("phoneNumber")));
        assertTrue(SensitiveDataProcessor.isSensitiveField(
            UserDTO.class.getDeclaredField("email")));
        assertFalse(SensitiveDataProcessor.isSensitiveField(
            UserDTO.class.getDeclaredField("username")));
    }

    // ========== getSensitiveAnnotation Tests ==========

    @Test
    @DisplayName("getSensitiveAnnotation should return annotation for sensitive fields")
    void testGetSensitiveAnnotation() throws NoSuchFieldException {
        Field field = UserDTO.class.getDeclaredField("phoneNumber");
        SensitiveData annotation = SensitiveDataProcessor.getSensitiveAnnotation(field);

        assertNotNull(annotation);
        assertEquals(SensitiveType.PHONE, annotation.type());
    }

    @Test
    @DisplayName("getSensitiveAnnotation should return null for non-sensitive fields")
    void testGetSensitiveAnnotationNull() throws NoSuchFieldException {
        Field field = UserDTO.class.getDeclaredField("username");
        SensitiveData annotation = SensitiveDataProcessor.getSensitiveAnnotation(field);

        assertNull(annotation);
    }

    // ========== maskField Tests ==========

    @Test
    @DisplayName("maskField should mask specific field")
    void testMaskField() {
        UserDTO user = new UserDTO("0912345678", "test@example.com", "A123456789", "johndoe");
        
        assertEquals("091***5678", SensitiveDataProcessor.maskField(user, "phoneNumber"));
        assertEquals("t***@example.com", SensitiveDataProcessor.maskField(user, "email"));
        assertEquals("A12****789", SensitiveDataProcessor.maskField(user, "idNumber"));
        assertEquals("johndoe", SensitiveDataProcessor.maskField(user, "username")); // Not sensitive
    }

    @Test
    @DisplayName("maskField should handle null object or field name")
    void testMaskFieldNull() {
        UserDTO user = new UserDTO("0912345678", "test@example.com", "A123456789", "johndoe");
        
        assertNull(SensitiveDataProcessor.maskField(null, "phoneNumber"));
        assertNull(SensitiveDataProcessor.maskField(user, null));
        assertNull(SensitiveDataProcessor.maskField(user, ""));
        assertNull(SensitiveDataProcessor.maskField(user, "nonExistentField"));
    }

    // ========== hasSensitiveFields Tests ==========

    @Test
    @DisplayName("hasSensitiveFields should detect objects with sensitive fields")
    void testHasSensitiveFields() {
        UserDTO user = new UserDTO("0912345678", "test@example.com", "A123456789", "johndoe");
        assertTrue(SensitiveDataProcessor.hasSensitiveFields(user));
    }

    @Test
    @DisplayName("hasSensitiveFields should return false for objects without sensitive fields")
    void testHasSensitiveFieldsNone() {
        Object plain = new Object();
        assertFalse(SensitiveDataProcessor.hasSensitiveFields(plain));
    }

    @Test
    @DisplayName("hasSensitiveFields should return false for null")
    void testHasSensitiveFieldsNull() {
        assertFalse(SensitiveDataProcessor.hasSensitiveFields(null));
    }

    // ========== toMaskedString Tests ==========

    @Test
    @DisplayName("toMaskedString should create masked string representation")
    void testToMaskedString() {
        UserDTO user = new UserDTO("0912345678", "test@example.com", "A123456789", "johndoe");
        String result = SensitiveDataProcessor.toMaskedString(user);

        assertNotNull(result);
        assertTrue(result.startsWith("UserDTO{"));
        assertTrue(result.contains("phoneNumber=091***5678"));
        assertTrue(result.contains("email=t***@example.com"));
        assertTrue(result.contains("idNumber=A12****789"));
        assertTrue(result.contains("username=johndoe"));
        assertTrue(result.endsWith("}"));
    }

    @Test
    @DisplayName("toMaskedString should handle null")
    void testToMaskedStringNull() {
        String result = SensitiveDataProcessor.toMaskedString(null);
        assertEquals("null", result);
    }

    @Test
    @DisplayName("toMaskedString should handle Chinese data")
    void testToMaskedStringChinese() {
        ChineseDataDTO dto = new ChineseDataDTO("王小明", "台北市信義區忠孝東路100號");
        String result = SensitiveDataProcessor.toMaskedString(dto);

        assertNotNull(result);
        assertTrue(result.contains("name=王**"));
        assertTrue(result.contains("address=台北市信義區********"));
    }

    @Test
    @DisplayName("toMaskedString should handle mixed sensitive and non-sensitive fields")
    void testToMaskedStringMixed() {
        MixedDTO dto = new MixedDTO("0912345678", "public data", "test@example.com");
        String result = SensitiveDataProcessor.toMaskedString(dto);

        assertNotNull(result);
        assertTrue(result.contains("phone=091***5678"));
        assertTrue(result.contains("publicInfo=public data"));
        assertTrue(result.contains("email=t***@example.com"));
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Should handle object with null field values")
    void testNullFieldValues() {
        UserDTO user = new UserDTO(null, null, null, null);
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(user);

        assertEquals(4, result.size());
        assertNull(result.get("phoneNumber"));
        assertNull(result.get("email"));
        assertNull(result.get("idNumber"));
        assertNull(result.get("username"));
    }

    @Test
    @DisplayName("Should handle object with mixed null and non-null values")
    void testMixedNullValues() {
        UserDTO user = new UserDTO("0912345678", null, "A123456789", null);
        Map<String, Object> result = SensitiveDataProcessor.maskSensitiveFields(user);

        assertEquals(4, result.size());
        assertEquals("091***5678", result.get("phoneNumber"));
        assertNull(result.get("email"));
        assertEquals("A12****789", result.get("idNumber"));
        assertNull(result.get("username"));
    }
}
