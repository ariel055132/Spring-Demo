package com.example.foundation.annotation;

import com.example.foundation.enums.SensitiveType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SensitiveData Annotation Test")
class SensitiveDataTest {

    // Test model classes
    static class UserRequest {
        @SensitiveData(type = SensitiveType.PHONE)
        private String phoneNumber;

        @SensitiveData(type = SensitiveType.EMAIL)
        private String email;

        @SensitiveData(type = SensitiveType.ID_NUMBER)
        private String idNumber;

        private String username; // Not sensitive

        public UserRequest(String phoneNumber, String email, String idNumber, String username) {
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.idNumber = idNumber;
            this.username = username;
        }

        // Getters
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getIdNumber() { return idNumber; }
        public String getUsername() { return username; }
    }

    static class PaymentRequest {
        @SensitiveData(type = SensitiveType.CREDIT_CARD)
        private String cardNumber;

        @SensitiveData(type = SensitiveType.PASSWORD)
        private String cvv;

        @SensitiveData(type = SensitiveType.NAME)
        private String cardHolderName;

        public PaymentRequest(String cardNumber, String cvv, String cardHolderName) {
            this.cardNumber = cardNumber;
            this.cvv = cvv;
            this.cardHolderName = cardHolderName;
        }

        public String getCardNumber() { return cardNumber; }
        public String getCvv() { return cvv; }
        public String getCardHolderName() { return cardHolderName; }
    }

    static class CustomMaskingRequest {
        @SensitiveData(type = SensitiveType.CUSTOM, keepStart = 2, keepEnd = 2)
        private String customField;

        @SensitiveData(type = SensitiveType.CUSTOM, keepStart = 4, keepEnd = 4, maskChar = "#")
        private String customField2;

        public CustomMaskingRequest(String customField, String customField2) {
            this.customField = customField;
            this.customField2 = customField2;
        }

        public String getCustomField() { return customField; }
        public String getCustomField2() { return customField2; }
    }

    static class DisabledMaskingRequest {
        @SensitiveData(type = SensitiveType.PHONE, enabled = false)
        private String phoneNumber;

        public DisabledMaskingRequest(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneNumber() { return phoneNumber; }
    }

    @Test
    @DisplayName("SensitiveData annotation should be present on fields")
    void testAnnotationPresence() throws NoSuchFieldException {
        // Check UserRequest fields
        assertTrue(UserRequest.class.getDeclaredField("phoneNumber").isAnnotationPresent(SensitiveData.class));
        assertTrue(UserRequest.class.getDeclaredField("email").isAnnotationPresent(SensitiveData.class));
        assertTrue(UserRequest.class.getDeclaredField("idNumber").isAnnotationPresent(SensitiveData.class));
        assertFalse(UserRequest.class.getDeclaredField("username").isAnnotationPresent(SensitiveData.class));
    }

    @Test
    @DisplayName("Annotation should have correct default values")
    void testAnnotationDefaults() throws NoSuchFieldException {
        SensitiveData annotation = UserRequest.class
            .getDeclaredField("phoneNumber")
            .getAnnotation(SensitiveData.class);

        assertEquals(SensitiveType.PHONE, annotation.type());
        assertEquals(0, annotation.keepStart());
        assertEquals(0, annotation.keepEnd());
        assertEquals("*", annotation.maskChar());
        assertEquals("", annotation.description());
        assertTrue(annotation.enabled());
    }

    @Test
    @DisplayName("Annotation should support custom parameters")
    void testCustomParameters() throws NoSuchFieldException {
        SensitiveData annotation = CustomMaskingRequest.class
            .getDeclaredField("customField")
            .getAnnotation(SensitiveData.class);

        assertEquals(SensitiveType.CUSTOM, annotation.type());
        assertEquals(2, annotation.keepStart());
        assertEquals(2, annotation.keepEnd());
        assertEquals("*", annotation.maskChar());
    }

    @Test
    @DisplayName("Annotation should support custom mask character")
    void testCustomMaskChar() throws NoSuchFieldException {
        SensitiveData annotation = CustomMaskingRequest.class
            .getDeclaredField("customField2")
            .getAnnotation(SensitiveData.class);

        assertEquals("#", annotation.maskChar());
        assertEquals(4, annotation.keepStart());
        assertEquals(4, annotation.keepEnd());
    }

    @Test
    @DisplayName("Annotation should support enabled flag")
    void testEnabledFlag() throws NoSuchFieldException {
        SensitiveData annotation = DisabledMaskingRequest.class
            .getDeclaredField("phoneNumber")
            .getAnnotation(SensitiveData.class);

        assertFalse(annotation.enabled());
    }

    @Test
    @DisplayName("Multiple fields can have different annotation configurations")
    void testMultipleFieldConfigurations() throws NoSuchFieldException {
        SensitiveData phoneAnnotation = UserRequest.class
            .getDeclaredField("phoneNumber")
            .getAnnotation(SensitiveData.class);
        
        SensitiveData emailAnnotation = UserRequest.class
            .getDeclaredField("email")
            .getAnnotation(SensitiveData.class);
        
        SensitiveData idAnnotation = UserRequest.class
            .getDeclaredField("idNumber")
            .getAnnotation(SensitiveData.class);

        assertEquals(SensitiveType.PHONE, phoneAnnotation.type());
        assertEquals(SensitiveType.EMAIL, emailAnnotation.type());
        assertEquals(SensitiveType.ID_NUMBER, idAnnotation.type());
    }

    @Test
    @DisplayName("Annotation can be applied to payment-related fields")
    void testPaymentAnnotations() throws NoSuchFieldException {
        assertTrue(PaymentRequest.class.getDeclaredField("cardNumber").isAnnotationPresent(SensitiveData.class));
        assertTrue(PaymentRequest.class.getDeclaredField("cvv").isAnnotationPresent(SensitiveData.class));
        assertTrue(PaymentRequest.class.getDeclaredField("cardHolderName").isAnnotationPresent(SensitiveData.class));

        SensitiveData cardAnnotation = PaymentRequest.class
            .getDeclaredField("cardNumber")
            .getAnnotation(SensitiveData.class);
        
        assertEquals(SensitiveType.CREDIT_CARD, cardAnnotation.type());
    }

    @Test
    @DisplayName("Annotation retention should be RUNTIME")
    void testRetentionPolicy() {
        java.lang.annotation.Retention retention = SensitiveData.class.getAnnotation(java.lang.annotation.Retention.class);
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    @DisplayName("Annotation target should be FIELD and METHOD")
    void testTargetPolicy() {
        java.lang.annotation.Target target = SensitiveData.class.getAnnotation(java.lang.annotation.Target.class);
        assertNotNull(target);
        
        java.lang.annotation.ElementType[] value = target.value();
        assertEquals(2, value.length);
        assertTrue(java.util.Arrays.asList(value).contains(java.lang.annotation.ElementType.FIELD));
        assertTrue(java.util.Arrays.asList(value).contains(java.lang.annotation.ElementType.METHOD));
    }
}
