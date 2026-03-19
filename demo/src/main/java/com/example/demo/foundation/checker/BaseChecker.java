package com.example.demo.foundation.checker;

import org.springframework.stereotype.Component;

/**
 * Base checker for validation
 * Implements PreCheckHandler interface
 */
@Component
public abstract class BaseChecker<T> implements PreCheckHandler<Object> {
    
    /**
     * Template method for validation
     * Delegates to doCheckInternal with typed argument
     */
    @Override
    public void doCheck(Object arg) {
        @SuppressWarnings("unchecked")
        T typedArg = (T) arg;
        doCheckInternal(typedArg);
    }
    
    /**
     * Subclasses must implement this to perform actual validation
     * 
     * @param arg The typed argument
     */
    protected abstract void doCheckInternal(T arg);
}

