package com.example.foundation.checker;

import org.springframework.stereotype.Component;

/**
 * Base checker for validation
 * Implements PreCheckHandler interface
 */
@Component
public abstract class BaseChecker<T> implements PreCheckHandler {
    
    /**
     * Template method for validation
     * Delegates to doCheckInternal with typed argument
     */
    @Override
    public void doCheck(Object arg) {
        try {
            // Generic cast is intentional: type safety is enforced by @PreCheck pointing to the correct checker class.
            // If the wrong argument type is passed, ClassCastException is caught and re-thrown with a clear message.
            @SuppressWarnings("unchecked")
            T typedArg = (T) arg;
            doCheckInternal(typedArg);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                "PreCheck argument type mismatch for " + getClass().getSimpleName()
                + ": received " + arg.getClass().getSimpleName(), e);
        }
    }
    
    /**
     * Subclasses must implement this to perform actual validation
     * 
     * @param arg The typed argument
     */
    protected abstract void doCheckInternal(T arg);
}

