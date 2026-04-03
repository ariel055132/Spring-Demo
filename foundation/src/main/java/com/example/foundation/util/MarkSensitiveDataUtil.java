package com.example.foundation.util;

/**
 * Utility class for masking sensitive data.
 * Provides methods to mask various types of sensitive information like phone numbers,
 * email addresses, ID numbers, credit cards, names, etc.
 */
public class MarkSensitiveDataUtil {

    private static final String DEFAULT_MASK_CHAR = "*";
    private static final String DEFAULT_MASK = "***";

    private MarkSensitiveDataUtil() {
        // Private constructor to prevent instantiation
    }

    // ========== General Masking Methods ==========

    /**
     * Mask a string by keeping first and last n characters visible
     * @param str String to mask
     * @param keepStart Number of characters to keep at start
     * @param keepEnd Number of characters to keep at end
     * @return Masked string
     */
    public static String mask(String str, int keepStart, int keepEnd) {
        return mask(str, keepStart, keepEnd, DEFAULT_MASK_CHAR);
    }

    /**
     * Mask a string by keeping first and last n characters visible with custom mask character
     * @param str String to mask
     * @param keepStart Number of characters to keep at start
     * @param keepEnd Number of characters to keep at end
     * @param maskChar Character to use for masking
     * @return Masked string
     */
    public static String mask(String str, int keepStart, int keepEnd, String maskChar) {
        if (StringUtil.isBlank(str)) {
            return str;
        }

        int length = str.length();
        
        // If string is too short, just mask everything
        if (length <= keepStart + keepEnd) {
            return maskChar.repeat(length);
        }

        String start = str.substring(0, keepStart);
        String end = str.substring(length - keepEnd);
        int maskLength = length - keepStart - keepEnd;

        return start + maskChar.repeat(maskLength) + end;
    }

    /**
     * Mask entire string except specified positions
     * @param str String to mask
     * @param visiblePositions Positions to keep visible (0-based)
     * @return Masked string
     */
    public static String maskExcept(String str, int... visiblePositions) {
        if (StringUtil.isBlank(str)) {
            return str;
        }

        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            boolean shouldMask = true;
            for (int pos : visiblePositions) {
                if (i == pos) {
                    shouldMask = false;
                    break;
                }
            }
            if (shouldMask) {
                chars[i] = '*';
            }
        }
        return new String(chars);
    }

    // ========== Phone Number Masking ==========

    /**
     * Mask phone number, keeping first 3 and last 4 digits visible
     * Example: 0912345678 -> 091****5678
     */
    public static String maskPhone(String phone) {
        return maskPhone(phone, 3, 4);
    }

    /**
     * Mask phone number with custom visible digits
     * @param phone Phone number
     * @param keepStart Number of digits to keep at start
     * @param keepEnd Number of digits to keep at end
     * @return Masked phone number
     */
    public static String maskPhone(String phone, int keepStart, int keepEnd) {
        if (StringUtil.isBlank(phone)) {
            return phone;
        }
        
        // Remove common phone formatting characters
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        
        if (cleaned.length() <= keepStart + keepEnd) {
            return DEFAULT_MASK;
        }
        
        return mask(cleaned, keepStart, keepEnd, DEFAULT_MASK_CHAR);
    }

    // ========== Email Masking ==========

    /**
     * Mask email address, keeping first character of username and full domain
     * Example: example@gmail.com -> e*****@gmail.com
     */
    public static String maskEmail(String email) {
        if (StringUtil.isBlank(email)) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return DEFAULT_MASK;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() == 1) {
            return username + DEFAULT_MASK + domain;
        }

        String maskedUsername = username.charAt(0) + DEFAULT_MASK_CHAR.repeat(username.length() - 1);
        return maskedUsername + domain;
    }

    /**
     * Mask email address with more privacy, masking both username and domain
     * Example: example@gmail.com -> e*****@g*****.com
     */
    public static String maskEmailStrict(String email) {
        if (StringUtil.isBlank(email)) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return DEFAULT_MASK;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        // Mask username
        String maskedUsername;
        if (username.length() == 1) {
            maskedUsername = username;
        } else {
            maskedUsername = username.charAt(0) + DEFAULT_MASK_CHAR.repeat(username.length() - 1);
        }

        // Mask domain
        int dotIndex = domain.lastIndexOf('.');
        String maskedDomain;
        if (dotIndex > 0) {
            String domainName = domain.substring(0, dotIndex);
            String extension = domain.substring(dotIndex);
            maskedDomain = (domainName.length() > 0 ? domainName.charAt(0) + DEFAULT_MASK_CHAR.repeat(Math.max(0, domainName.length() - 1)) : "") + extension;
        } else {
            maskedDomain = domain.length() > 0 ? domain.charAt(0) + DEFAULT_MASK_CHAR.repeat(domain.length() - 1) : domain;
        }

        return maskedUsername + "@" + maskedDomain;
    }

    // ========== ID Number Masking ==========

    /**
     * Mask ID number (Taiwan), keeping first 3 and last 3 characters visible
     * Example: A123456789 -> A12****789
     */
    public static String maskIdNumber(String idNumber) {
        return mask(idNumber, 3, 3);
    }

    /**
     * Mask ID number with custom visible characters
     */
    public static String maskIdNumber(String idNumber, int keepStart, int keepEnd) {
        return mask(idNumber, keepStart, keepEnd);
    }

    // ========== Credit Card Masking ==========

    /**
     * Mask credit card number, showing only last 4 digits
     * Example: 1234567890123456 -> ************3456
     */
    public static String maskCreditCard(String cardNumber) {
        if (StringUtil.isBlank(cardNumber)) {
            return cardNumber;
        }

        // Remove spaces and dashes
        String cleaned = cardNumber.replaceAll("[\\s\\-]", "");

        if (cleaned.length() < 4) {
            return DEFAULT_MASK;
        }

        return DEFAULT_MASK_CHAR.repeat(cleaned.length() - 4) + cleaned.substring(cleaned.length() - 4);
    }

    /**
     * Mask credit card number with formatting (xxxx-xxxx-xxxx-1234)
     */
    public static String maskCreditCardFormatted(String cardNumber) {
        if (StringUtil.isBlank(cardNumber)) {
            return cardNumber;
        }

        // Remove spaces and dashes
        String cleaned = cardNumber.replaceAll("[\\s\\-]", "");

        if (cleaned.length() < 4) {
            return DEFAULT_MASK;
        }

        String last4 = cleaned.substring(cleaned.length() - 4);
        int groups = (cleaned.length() - 4) / 4;
        int remainder = (cleaned.length() - 4) % 4;

        StringBuilder masked = new StringBuilder();
        
        // Add masked groups
        if (remainder > 0) {
            masked.append(DEFAULT_MASK_CHAR.repeat(remainder)).append("-");
        }
        
        for (int i = 0; i < groups; i++) {
            masked.append("xxxx");
            if (i < groups - 1) {
                masked.append("-");
            }
        }
        
        if (groups > 0) {
            masked.append("-");
        }
        
        masked.append(last4);
        
        return masked.toString();
    }

    // ========== Name Masking ==========

    /**
     * Mask name, keeping first character visible
     * Example: John Doe -> J*** D**
     */
    public static String maskName(String name) {
        if (StringUtil.isBlank(name)) {
            return name;
        }

        String[] parts = name.split("\\s+");
        StringBuilder masked = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.length() == 1) {
                masked.append(part);
            } else {
                masked.append(part.charAt(0)).append(DEFAULT_MASK_CHAR.repeat(part.length() - 1));
            }
            
            if (i < parts.length - 1) {
                masked.append(" ");
            }
        }

        return masked.toString();
    }

    /**
     * Mask Chinese name, keeping first character visible
     * Example: 王小明 -> 王**
     */
    public static String maskChineseName(String name) {
        if (StringUtil.isBlank(name)) {
            return name;
        }

        if (name.length() == 1) {
            return name;
        }

        return name.charAt(0) + DEFAULT_MASK_CHAR.repeat(name.length() - 1);
    }

    // ========== Address Masking ==========

    /**
     * Mask address, keeping first 6 characters visible
     * Example: 台北市信義區忠孝東路100號 -> 台北市信義區*******
     */
    public static String maskAddress(String address) {
        return maskAddress(address, 6);
    }

    /**
     * Mask address with custom visible characters
     */
    public static String maskAddress(String address, int keepStart) {
        if (StringUtil.isBlank(address)) {
            return address;
        }

        if (address.length() <= keepStart) {
            return address;
        }

        return address.substring(0, keepStart) + DEFAULT_MASK_CHAR.repeat(address.length() - keepStart);
    }

    // ========== Bank Account Masking ==========

    /**
     * Mask bank account number, showing only last 4 digits
     * Example: 1234567890123 -> *********0123
     */
    public static String maskBankAccount(String accountNumber) {
        if (StringUtil.isBlank(accountNumber)) {
            return accountNumber;
        }

        String cleaned = accountNumber.replaceAll("[\\s\\-]", "");

        if (cleaned.length() <= 4) {
            return DEFAULT_MASK;
        }

        return DEFAULT_MASK_CHAR.repeat(cleaned.length() - 4) + cleaned.substring(cleaned.length() - 4);
    }

    /**
     * Mask bank account number, keeping first 3 and last 4 digits visible
     * Example: 1234567890123 -> 123*****0123
     */
    public static String maskBankAccountPartial(String accountNumber) {
        if (StringUtil.isBlank(accountNumber)) {
            return accountNumber;
        }

        String cleaned = accountNumber.replaceAll("[\\s\\-]", "");
        return mask(cleaned, 3, 4);
    }

    // ========== Password Masking ==========

    /**
     * Mask password completely
     * Returns fixed length mask regardless of actual password length
     */
    public static String maskPassword(String password) {
        if (StringUtil.isBlank(password)) {
            return password;
        }
        return DEFAULT_MASK_CHAR.repeat(8);
    }

    /**
     * Mask password with actual length
     */
    public static String maskPasswordWithLength(String password) {
        if (StringUtil.isBlank(password)) {
            return password;
        }
        return DEFAULT_MASK_CHAR.repeat(password.length());
    }

    // ========== Custom Masking ==========

    /**
     * Mask string from specific position
     * @param str String to mask
     * @param startPos Start position (inclusive, 0-based)
     * @return Masked string
     */
    public static String maskFrom(String str, int startPos) {
        if (StringUtil.isBlank(str) || startPos >= str.length()) {
            return str;
        }

        if (startPos <= 0) {
            return DEFAULT_MASK_CHAR.repeat(str.length());
        }

        return str.substring(0, startPos) + DEFAULT_MASK_CHAR.repeat(str.length() - startPos);
    }

    /**
     * Mask string up to specific position
     * @param str String to mask
     * @param endPos End position (exclusive, 0-based)
     * @return Masked string
     */
    public static String maskUntil(String str, int endPos) {
        if (StringUtil.isBlank(str) || endPos <= 0) {
            return str;
        }

        if (endPos >= str.length()) {
            return DEFAULT_MASK_CHAR.repeat(str.length());
        }

        return DEFAULT_MASK_CHAR.repeat(endPos) + str.substring(endPos);
    }

    /**
     * Mask string between specific positions
     * @param str String to mask
     * @param startPos Start position (inclusive, 0-based)
     * @param endPos End position (exclusive, 0-based)
     * @return Masked string
     */
    public static String maskBetween(String str, int startPos, int endPos) {
        if (StringUtil.isBlank(str) || startPos >= endPos || startPos >= str.length()) {
            return str;
        }

        int actualEndPos = Math.min(endPos, str.length());
        String before = str.substring(0, startPos);
        String after = actualEndPos < str.length() ? str.substring(actualEndPos) : "";
        int maskLength = actualEndPos - startPos;

        return before + DEFAULT_MASK_CHAR.repeat(maskLength) + after;
    }

    // ========== Utility Methods ==========

    /**
     * Check if string is already masked (contains mask characters)
     */
    public static boolean isMasked(String str) {
        return str != null && str.contains(DEFAULT_MASK_CHAR);
    }

    /**
     * Get default mask character
     */
    public static String getDefaultMaskChar() {
        return DEFAULT_MASK_CHAR;
    }

    /**
     * Get default mask string
     */
    public static String getDefaultMask() {
        return DEFAULT_MASK;
    }
}
