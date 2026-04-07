package com.example.foundation.enums;

/**
 * Enum defining different types of sensitive data and their default masking strategies.
 * Each type corresponds to a method in MarkSensitiveDataUtil.
 */
public enum SensitiveType {
    
    /**
     * Default masking - keeps first 3 and last 3 characters
     * Example: A123456789 -> A12****789
     */
    DEFAULT,
    
    /**
     * Phone number masking - keeps first 3 and last 4 digits
     * Example: 0912345678 -> 091***5678
     */
    PHONE,
    
    /**
     * Email address masking - keeps first character of username and full domain
     * Example: example@gmail.com -> e******@gmail.com
     */
    EMAIL,
    
    /**
     * Strict email masking - masks both username and domain
     * Example: example@gmail.com -> e******@g****.com
     */
    EMAIL_STRICT,
    
    /**
     * ID number masking - keeps first 3 and last 3 characters
     * Example: A123456789 -> A12****789
     */
    ID_NUMBER,
    
    /**
     * Credit card masking - shows only last 4 digits
     * Example: 1234567890123456 -> ************3456
     */
    CREDIT_CARD,
    
    /**
     * Credit card formatted masking
     * Example: 1234567890123456 -> xxxx-xxxx-xxxx-3456
     */
    CREDIT_CARD_FORMATTED,
    
    /**
     * Name masking - keeps first character of each part
     * Example: John Doe -> J*** D**
     */
    NAME,
    
    /**
     * Chinese name masking - keeps first character
     * Example: 王小明 -> 王**
     */
    CHINESE_NAME,
    
    /**
     * Address masking - keeps first 6 characters
     * Example: 台北市信義區忠孝東路100號 -> 台北市信義區********
     */
    ADDRESS,
    
    /**
     * Bank account masking - shows only last 4 digits
     * Example: 1234567890123 -> *********0123
     */
    BANK_ACCOUNT,
    
    /**
     * Bank account partial masking - keeps first 3 and last 4 digits
     * Example: 1234567890123 -> 123******0123
     */
    BANK_ACCOUNT_PARTIAL,
    
    /**
     * Password masking - fixed length mask (8 asterisks)
     * Example: anypassword -> ********
     */
    PASSWORD,
    
    /**
     * Password masking with actual length
     * Example: password123 -> ***********
     */
    PASSWORD_WITH_LENGTH,
    
    /**
     * Custom masking - uses keepStart and keepEnd parameters from annotation
     * Allows flexible control over which parts to show/hide
     */
    CUSTOM;
    
    /**
     * Get a human-readable description of the sensitive type
     */
    public String getDescription() {
        return switch (this) {
            case DEFAULT -> "Default masking (keep first 3 and last 3)";
            case PHONE -> "Phone number";
            case EMAIL -> "Email address";
            case EMAIL_STRICT -> "Email address (strict)";
            case ID_NUMBER -> "ID number";
            case CREDIT_CARD -> "Credit card number";
            case CREDIT_CARD_FORMATTED -> "Credit card number (formatted)";
            case NAME -> "Person name";
            case CHINESE_NAME -> "Chinese name";
            case ADDRESS -> "Address";
            case BANK_ACCOUNT -> "Bank account number";
            case BANK_ACCOUNT_PARTIAL -> "Bank account number (partial)";
            case PASSWORD -> "Password";
            case PASSWORD_WITH_LENGTH -> "Password (with length)";
            case CUSTOM -> "Custom masking";
        };
    }
    
    /**
     * Check if this type supports custom keepStart/keepEnd parameters
     */
    public boolean supportsCustomParameters() {
        return this == CUSTOM || this == DEFAULT;
    }
}
