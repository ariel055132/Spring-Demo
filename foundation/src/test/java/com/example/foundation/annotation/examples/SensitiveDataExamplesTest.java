package com.example.foundation.annotation.examples;

import com.example.foundation.annotation.SensitiveData;
import com.example.foundation.enums.SensitiveType;
import com.example.foundation.util.SensitiveDataProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class demonstrating real-world usage of @SensitiveData annotation
 */
@DisplayName("SensitiveData Annotation Examples Test")
class SensitiveDataExamplesTest {

    // ========== Model Classes ==========
    
    static class UserRegistrationRequest {
        @SensitiveData(type = SensitiveType.PHONE)
        private String phoneNumber;
        
        @SensitiveData(type = SensitiveType.EMAIL)
        private String email;
        
        @SensitiveData(type = SensitiveType.PASSWORD)
        private String password;
        
        @SensitiveData(type = SensitiveType.ID_NUMBER)
        private String idNumber;
        
        private String username; // Not sensitive
        
        public UserRegistrationRequest(String phoneNumber, String email, String password, 
                                     String idNumber, String username) {
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.password = password;
            this.idNumber = idNumber;
            this.username = username;
        }
    }
    
    static class PaymentRequest {
        @SensitiveData(type = SensitiveType.CREDIT_CARD_FORMATTED)
        private String cardNumber;
        
        @SensitiveData(type = SensitiveType.PASSWORD)
        private String cvv;
        
        @SensitiveData(type = SensitiveType.NAME)
        private String cardHolderName;
        
        private BigDecimal amount;
        private String currency;
        
        public PaymentRequest(String cardNumber, String cvv, String cardHolderName, 
                            BigDecimal amount, String currency) {
            this.cardNumber = cardNumber;
            this.cvv = cvv;
            this.cardHolderName = cardHolderName;
            this.amount = amount;
            this.currency = currency;
        }
    }
    
    static class ChineseUserProfile {
        @SensitiveData(type = SensitiveType.CHINESE_NAME)
        private String name;
        
        @SensitiveData(type = SensitiveType.PHONE)
        private String mobilePhone;
        
        @SensitiveData(type = SensitiveType.ADDRESS)
        private String homeAddress;
        
        @SensitiveData(type = SensitiveType.ID_NUMBER)
        private String nationalId;
        
        public ChineseUserProfile(String name, String mobilePhone, 
                                String homeAddress, String nationalId) {
            this.name = name;
            this.mobilePhone = mobilePhone;
            this.homeAddress = homeAddress;
            this.nationalId = nationalId;
        }
    }
    
    static class CustomMaskingExample {
        @SensitiveData(
            type = SensitiveType.CUSTOM, 
            keepStart = 4, 
            keepEnd = 4
        )
        private String accountNumber;
        
        @SensitiveData(
            type = SensitiveType.CUSTOM, 
            keepStart = 2, 
            keepEnd = 2,
            maskChar = "#"
        )
        private String referenceCode;
        
        public CustomMaskingExample(String accountNumber, String referenceCode) {
            this.accountNumber = accountNumber;
            this.referenceCode = referenceCode;
        }
    }
    
    static class BankAccountInfo {
        @SensitiveData(type = SensitiveType.BANK_ACCOUNT_PARTIAL)
        private String accountNumber;
        
        @SensitiveData(type = SensitiveType.NAME)
        private String accountHolderName;
        
        private String bankName;
        private String branchCode;
        
        public BankAccountInfo(String accountNumber, String accountHolderName, 
                             String bankName, String branchCode) {
            this.accountNumber = accountNumber;
            this.accountHolderName = accountHolderName;
            this.bankName = bankName;
            this.branchCode = branchCode;
        }
    }
    
    static class DebugExample {
        @SensitiveData(type = SensitiveType.PHONE, enabled = false)
        private String debugPhone;
        
        @SensitiveData(type = SensitiveType.EMAIL)
        private String prodEmail;
        
        public DebugExample(String debugPhone, String prodEmail) {
            this.debugPhone = debugPhone;
            this.prodEmail = prodEmail;
        }
    }
    
    static class UserProfileResponse {
        private Long userId;
        
        @SensitiveData(type = SensitiveType.EMAIL)
        private String email;
        
        @SensitiveData(type = SensitiveType.PHONE)
        private String phoneNumber;
        
        private String displayName;
        private String avatarUrl;
        
        public UserProfileResponse(Long userId, String email, String phoneNumber, 
                                 String displayName, String avatarUrl) {
            this.userId = userId;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
        }
    }

    // ========== Test Methods ==========

    @Test
    @DisplayName("Example: User registration request masking")
    void testUserRegistrationExample() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "0912345678",
            "user@example.com",
            "mySecretPassword123",
            "A123456789",
            "johndoe"
        );
        
        String masked = SensitiveDataProcessor.toMaskedString(request);
        
        assertNotNull(masked);
        assertTrue(masked.contains("091***5678"));
        assertTrue(masked.contains("u***@example.com"));
        assertTrue(masked.contains("********"));
        assertTrue(masked.contains("A12****789"));
        assertTrue(masked.contains("johndoe"));
    }

    @Test
    @DisplayName("Example: Payment request masking")
    void testPaymentExample() {
        PaymentRequest payment = new PaymentRequest(
            "1234567890123456",
            "123",
            "John Doe",
            new BigDecimal("99.99"),
            "USD"
        );
        
        Map<String, Object> masked = SensitiveDataProcessor.maskSensitiveFields(payment);
        
        assertEquals(5, masked.size());
        assertTrue(masked.get("cardNumber").toString().endsWith("3456"));
        assertEquals("********", masked.get("cvv"));
        assertEquals("J*** D**", masked.get("cardHolderName"));
        assertEquals(new BigDecimal("99.99"), masked.get("amount"));
        assertEquals("USD", masked.get("currency"));
    }

    @Test
    @DisplayName("Example: Chinese user profile masking")
    void testChineseProfileExample() {
        ChineseUserProfile profile = new ChineseUserProfile(
            "王小明",
            "0912345678",
            "台北市信義區忠孝東路100號",
            "A123456789"
        );
        
        String masked = SensitiveDataProcessor.toMaskedString(profile);
        
        assertNotNull(masked);
        assertTrue(masked.contains("王**"));
        assertTrue(masked.contains("091***5678"));
        assertTrue(masked.contains("台北市信義區"));
        assertTrue(masked.contains("A12****789"));
    }

    @Test
    @DisplayName("Example: Custom masking")
    void testCustomMaskingExample() {
        CustomMaskingExample custom = new CustomMaskingExample(
            "1234567890123456",
            "ABCDEFGH"
        );
        
        Map<String, Object> masked = SensitiveDataProcessor.maskSensitiveFields(custom);
        
        assertEquals(2, masked.size());
        assertEquals("1234********3456", masked.get("accountNumber"));
        assertEquals("AB####GH", masked.get("referenceCode"));
    }

    @Test
    @DisplayName("Example: Bank account masking")
    void testBankAccountExample() {
        BankAccountInfo account = new BankAccountInfo(
            "1234567890123",
            "Jane Smith",
            "Example Bank",
            "001"
        );
        
        String masked = SensitiveDataProcessor.toMaskedString(account);
        
        assertNotNull(masked);
        assertTrue(masked.contains("123******0123"));
        assertTrue(masked.contains("J*** S*****"));
        assertTrue(masked.contains("Example Bank"));
        assertTrue(masked.contains("001"));
    }

    @Test
    @DisplayName("Example: Debug mode with conditional masking")
    void testDebugModeExample() {
        DebugExample debug = new DebugExample("0912345678", "test@example.com");
        
        Map<String, Object> masked = SensitiveDataProcessor.maskSensitiveFields(debug);
        
        assertEquals(2, masked.size());
        assertEquals("0912345678", masked.get("debugPhone")); // Not masked (enabled=false)
        assertEquals("t***@example.com", masked.get("prodEmail")); // Masked
    }

    @Test
    @DisplayName("Example: API response with selective masking")
    void testApiResponseExample() {
        UserProfileResponse response = new UserProfileResponse(
            12345L,
            "john.doe@example.com",
            "0912345678",
            "John D.",
            "https://example.com/avatar.jpg"
        );
        
        // Check if response has sensitive data
        boolean hasSensitive = SensitiveDataProcessor.hasSensitiveFields(response);
        assertTrue(hasSensitive);
        
        // Get masked version
        String auditLog = SensitiveDataProcessor.toMaskedString(response);
        assertNotNull(auditLog);
        assertTrue(auditLog.contains("j***@example.com"));
        assertTrue(auditLog.contains("091***5678"));
        assertTrue(auditLog.contains("John D."));
    }

    @Test
    @DisplayName("Example: Individual field masking")
    void testIndividualFieldMaskingExample() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "0912345678",
            "user@example.com",
            "password123",
            "A123456789",
            "johndoe"
        );
        
        // Mask individual fields
        String maskedPhone = SensitiveDataProcessor.maskField(request, "phoneNumber");
        String maskedEmail = SensitiveDataProcessor.maskField(request, "email");
        String username = SensitiveDataProcessor.maskField(request, "username");
        
        assertEquals("091***5678", maskedPhone);
        assertEquals("u***@example.com", maskedEmail);
        assertEquals("johndoe", username); // Not masked
    }

    @Test
    @DisplayName("Example: All masking types work correctly")
    void testAllMaskingTypesIntegration() {
        // Create instances of all example types
        UserRegistrationRequest user = new UserRegistrationRequest(
            "0912345678", "user@example.com", "password", "A123456789", "user"
        );
        PaymentRequest payment = new PaymentRequest(
            "1234567890123456", "123", "John Doe", new BigDecimal("100"), "USD"
        );
        ChineseUserProfile chinese = new ChineseUserProfile(
            "王小明", "0912345678", "台北市信義區忠孝東路100號", "A123456789"
        );
        
        // All should have sensitive fields
        assertTrue(SensitiveDataProcessor.hasSensitiveFields(user));
        assertTrue(SensitiveDataProcessor.hasSensitiveFields(payment));
        assertTrue(SensitiveDataProcessor.hasSensitiveFields(chinese));
        
        // All should produce masked strings
        assertNotNull(SensitiveDataProcessor.toMaskedString(user));
        assertNotNull(SensitiveDataProcessor.toMaskedString(payment));
        assertNotNull(SensitiveDataProcessor.toMaskedString(chinese));
    }

    @Test
    @DisplayName("Example: Masked data doesn't contain original sensitive values")
    void testMaskedDataSecurityExample() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "0912345678",
            "secretuser@example.com",
            "superSecretPassword123",
            "A123456789",
            "johndoe"
        );
        
        String masked = SensitiveDataProcessor.toMaskedString(request);
        
        // Original sensitive values should not appear in masked string
        assertFalse(masked.contains("0912345678"));
        assertFalse(masked.contains("secretuser@example.com"));
        assertFalse(masked.contains("superSecretPassword123"));
        
        // But should contain masked versions
        assertTrue(masked.contains("091***5678"));
        assertTrue(masked.contains("********"));
        
        // Non-sensitive data should be unchanged
        assertTrue(masked.contains("johndoe"));
    }
}
