package com.example.foundation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator to check if a string contains only ASCII characters
 */
public class AsciiValidator implements ConstraintValidator<Ascii, String> {
    
    @Override
    public void initialize(Ascii constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull for null checks
        }
        
        // Check if all characters are ASCII (0-127)
        for (char c : value.toCharArray()) {
            if (c > 127) {
                return false;
            }
        }
        
        return true;
    }
}
