package com.example.foundation.util;

import com.example.foundation.annotation.SensitiveData;
import com.example.foundation.enums.SensitiveType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for processing objects with @SensitiveData annotations
 * and applying appropriate masking to sensitive fields.
 */
public class SensitiveDataProcessor {

    private SensitiveDataProcessor() {
        // Private constructor to prevent instantiation
    }

    /**
     * Process an object and mask all fields annotated with @SensitiveData
     * Returns a new map with masked values
     * 
     * @param obj Object to process
     * @return Map of field names to masked values
     */
    public static Map<String, Object> maskSensitiveFields(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            try {
                Object value = field.get(obj);
                
                if (field.isAnnotationPresent(SensitiveData.class)) {
                    SensitiveData annotation = field.getAnnotation(SensitiveData.class);
                    
                    if (annotation.enabled() && value != null) {
                        String maskedValue = maskValue(value.toString(), annotation);
                        result.put(field.getName(), maskedValue);
                    } else {
                        result.put(field.getName(), value);
                    }
                } else {
                    result.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                // Skip fields that cannot be accessed
                result.put(field.getName(), "[ACCESS_DENIED]");
            }
        }

        return result;
    }

    /**
     * Mask a single value based on the annotation configuration
     * 
     * @param value String value to mask
     * @param annotation SensitiveData annotation with masking configuration
     * @return Masked string
     */
    public static String maskValue(String value, SensitiveData annotation) {
        if (StringUtil.isBlank(value) || !annotation.enabled()) {
            return value;
        }

        SensitiveType type = annotation.type();
        
        return switch (type) {
            case PHONE -> MarkSensitiveDataUtil.maskPhone(value);
            case EMAIL -> MarkSensitiveDataUtil.maskEmail(value);
            case EMAIL_STRICT -> MarkSensitiveDataUtil.maskEmailStrict(value);
            case ID_NUMBER -> MarkSensitiveDataUtil.maskIdNumber(value);
            case CREDIT_CARD -> MarkSensitiveDataUtil.maskCreditCard(value);
            case CREDIT_CARD_FORMATTED -> MarkSensitiveDataUtil.maskCreditCardFormatted(value);
            case NAME -> MarkSensitiveDataUtil.maskName(value);
            case CHINESE_NAME -> MarkSensitiveDataUtil.maskChineseName(value);
            case ADDRESS -> MarkSensitiveDataUtil.maskAddress(value);
            case BANK_ACCOUNT -> MarkSensitiveDataUtil.maskBankAccount(value);
            case BANK_ACCOUNT_PARTIAL -> MarkSensitiveDataUtil.maskBankAccountPartial(value);
            case PASSWORD -> MarkSensitiveDataUtil.maskPassword(value);
            case PASSWORD_WITH_LENGTH -> MarkSensitiveDataUtil.maskPasswordWithLength(value);
            case CUSTOM -> MarkSensitiveDataUtil.mask(value, annotation.keepStart(), annotation.keepEnd(), annotation.maskChar());
            case DEFAULT -> MarkSensitiveDataUtil.mask(value, 3, 3, annotation.maskChar());
        };
    }

    /**
     * Check if a field has @SensitiveData annotation
     * 
     * @param field Field to check
     * @return true if field is annotated with @SensitiveData
     */
    public static boolean isSensitiveField(Field field) {
        return field.isAnnotationPresent(SensitiveData.class);
    }

    /**
     * Get the SensitiveData annotation from a field
     * 
     * @param field Field to get annotation from
     * @return SensitiveData annotation or null if not present
     */
    public static SensitiveData getSensitiveAnnotation(Field field) {
        return field.getAnnotation(SensitiveData.class);
    }

    /**
     * Mask a specific field value in an object
     * 
     * @param obj Object containing the field
     * @param fieldName Name of the field to mask
     * @return Masked value or null if field not found
     */
    public static String maskField(Object obj, String fieldName) {
        if (obj == null || StringUtil.isBlank(fieldName)) {
            return null;
        }

        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            
            Object value = field.get(obj);
            if (value == null) {
                return null;
            }

            if (field.isAnnotationPresent(SensitiveData.class)) {
                SensitiveData annotation = field.getAnnotation(SensitiveData.class);
                return maskValue(value.toString(), annotation);
            } else {
                // If no annotation, return original value
                return value.toString();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Check if an object has any sensitive fields
     * 
     * @param obj Object to check
     * @return true if object has at least one field with @SensitiveData annotation
     */
    public static boolean hasSensitiveFields(Object obj) {
        if (obj == null) {
            return false;
        }

        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(SensitiveData.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a masked string representation of an object
     * Useful for logging or debugging
     * 
     * @param obj Object to convert to string
     * @return String representation with sensitive fields masked
     */
    public static String toMaskedString(Object obj) {
        if (obj == null) {
            return "null";
        }

        Map<String, Object> masked = maskSensitiveFields(obj);
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getSimpleName()).append("{");
        
        int count = 0;
        for (Map.Entry<String, Object> entry : masked.entrySet()) {
            if (count > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            count++;
        }
        
        sb.append("}");
        return sb.toString();
    }
}
