package com.example.foundation.checker;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP Aspect for intercepting methods annotated with @PreCheck
 * Validates data existence/duplication before executing business logic
 * Uses PreCheckHandler beans to perform validation
 */
@Aspect
@Component
public class PreCheckAspect {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    /**
     * Intercept methods annotated with @PreCheck and perform validation
     */
    @Before("@annotation(com.example.demo.foundation.checker.PreCheck)")
    public void performPreCheck(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PreCheck preCheck = method.getAnnotation(PreCheck.class);
        
        if (preCheck == null) {
            return;
        }
        
        // Get the first argument (assuming it's the Arg object)
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("PreCheck annotation requires at least one argument");
        }
        
        Object arg = args[0];
        if (arg == null) {
            throw new IllegalArgumentException("PreCheck validation argument cannot be null");
        }
        
        Class<? extends PreCheckHandler<?>> checkerClass = preCheck.value();
        
        // Get the checker bean from Spring context
        PreCheckHandler<?> handler = applicationContext.getBean(checkerClass);
        
        // Delegate to handler to perform validation
        handler.doCheck(arg);
    }
}
