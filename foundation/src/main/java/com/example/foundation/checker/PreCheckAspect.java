package com.example.foundation.checker;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect for intercepting methods annotated with @PreCheck
 * Validates data existence/duplication before executing business logic
 * Uses PreCheckHandler beans to perform validation
 */
@Aspect
@Component
public class PreCheckAspect {

    // Constructor injection makes the dependency explicit and allows the aspect to be unit-tested without a Spring container
    private final ApplicationContext applicationContext;

    public PreCheckAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Intercept methods annotated with @PreCheck and perform validation.
     * The annotation is bound directly from the pointcut to avoid redundant reflection.
     */
    @Before("@annotation(preCheck)")
    public void performPreCheck(JoinPoint joinPoint, PreCheck preCheck) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("PreCheck annotation requires at least one argument");
        }
        
        Object arg = args[0];
        if (arg == null) {
            throw new IllegalArgumentException("PreCheck validation argument cannot be null");
        }
        
        Class<? extends PreCheckHandler> checkerClass = preCheck.value();
        
        // Get the checker bean from Spring context
        PreCheckHandler handler = applicationContext.getBean(checkerClass);
        
        // Delegate to handler to perform validation
        handler.doCheck(arg);
    }
}
