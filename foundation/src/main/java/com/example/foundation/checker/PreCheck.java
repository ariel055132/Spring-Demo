package com.example.foundation.checker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable pre-check validation before executing business logic.
 * Specifies the checker class that will perform validation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreCheck {
    /**
     * The checker class that implements PreCheckHandler
     * @return The checker class to use for validation
     */
    Class<? extends PreCheckHandler<?>> value();
}
