package com.example.foundation.annotation;

import com.example.foundation.enums.SensitiveType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields containing sensitive data that should be masked.
 * Can be used on fields in request/response objects to automatically apply masking
 * during serialization, logging, or display.
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class UserRequest {
 *     @SensitiveData(type = SensitiveType.PHONE)
 *     private String phoneNumber;
 *     
 *     @SensitiveData(type = SensitiveType.EMAIL)
 *     private String email;
 *     
 *     @SensitiveData(type = SensitiveType.CUSTOM, keepStart = 2, keepEnd = 2)
 *     private String customField;
 * }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveData {
    
    /**
     * The type of sensitive data, which determines the masking strategy
     */
    SensitiveType type() default SensitiveType.DEFAULT;
    
    /**
     * Number of characters to keep visible at the start (for CUSTOM type)
     */
    int keepStart() default 0;
    
    /**
     * Number of characters to keep visible at the end (for CUSTOM type)
     */
    int keepEnd() default 0;
    
    /**
     * Custom mask character (default is '*')
     */
    String maskChar() default "*";
    
    /**
     * Description of the sensitive field (for documentation purposes)
     */
    String description() default "";
    
    /**
     * Whether to enable masking (can be used to temporarily disable masking for debugging)
     */
    boolean enabled() default true;
}
