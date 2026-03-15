package com.example.demo.foundation.checker;

import com.example.demo.service.weather.arg.CreateWeatherArg;
import com.example.demo.service.weather.arg.DeleteWeatherArg;
import com.example.demo.service.weather.arg.UpdateWeatherArg;
import com.example.demo.service.weather.checker.WeatherChecker;
import com.example.demo.service.weather.checker.WeatherCheckMessageEnum;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP Aspect for intercepting methods annotated with @PreCheck
 * Validates data existence/duplication before executing business logic
 */
@Aspect
@Component
public class PreCheckAspect {
    
    @Autowired
    private WeatherChecker weatherChecker;
    
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
        CheckType checkType = preCheck.value();
        WeatherCheckMessageEnum messageEnum = preCheck.message();
        
        // Get message template from enum and process placeholders
        String messageTemplate = messageEnum.getMessageTemplate();
        String processedMessage = processMessagePlaceholders(messageTemplate, arg);
        
        // Call appropriate checker method based on operation type and arg type
        switch (checkType) {
            case CREATE:
                if (arg instanceof CreateWeatherArg) {
                    weatherChecker.CreateWeatherChecker((CreateWeatherArg) arg, processedMessage);
                } else {
                    throw new IllegalArgumentException("CREATE check requires CreateWeatherArg");
                }
                break;
            case UPDATE:
                if (arg instanceof UpdateWeatherArg) {
                    weatherChecker.UpdateWeatherChecker((UpdateWeatherArg) arg, processedMessage);
                } else {
                    throw new IllegalArgumentException("UPDATE check requires UpdateWeatherArg");
                }
                break;
            case DELETE:
                if (arg instanceof DeleteWeatherArg) {
                    weatherChecker.DeleteWeatherChecker((DeleteWeatherArg) arg, processedMessage);
                } else {
                    throw new IllegalArgumentException("DELETE check requires DeleteWeatherArg");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown check type: " + checkType);
        }
    }
    
    /**
     * Process message placeholders like {city}, {date} with actual values from arg
     */
    private String processMessagePlaceholders(String message, Object arg) {
        String result = message;
        
        try {
            // Use reflection to replace placeholders
            if (message.contains("{city}")) {
                Object city = getFieldValue(arg, "city");
                result = result.replace("{city}", city != null ? city.toString() : "null");
            }
            if (message.contains("{date}")) {
                Object date = getFieldValue(arg, "date");
                result = result.replace("{date}", date != null ? date.toString() : "null");
            }
        } catch (Exception e) {
            // If placeholder replacement fails, return original message
            return message;
        }
        
        return result;
    }
    
    /**
     * Get field value using reflection
     */
    private Object getFieldValue(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();
        java.lang.reflect.Field field = findField(clazz, fieldName);
        
        if (field == null) {
            return null;
        }
        
        field.setAccessible(true);
        return field.get(obj);
    }
    
    /**
     * Find field in class hierarchy
     */
    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
