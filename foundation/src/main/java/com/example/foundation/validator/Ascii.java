package com.example.foundation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure a string contains only ASCII characters
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AsciiValidator.class)
@Documented
public @interface Ascii {
    
    String message() default "String must contain only ASCII characters";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
