package com.example.demo.foundation.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.foundation.exception.DataNotFoundException;
import com.example.demo.foundation.exception.DuplicateDataException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Base checker utility for validating data existence and duplication
 * Uses JPA Criteria API for dynamic queries
 */
@Component
public class BaseChecker {
    
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Check if data already exists (for CREATE operations)
     * Throws DuplicateDataException if duplicate is found
     * 
     * @param arg Argument object containing field values to check
     * @param entityClass Entity class to query
     * @param fields Field names to check for uniqueness
     * @param customMessage Custom error message (optional)
     */
    public void checkForDuplicate(Object arg, Class<?> entityClass, String[] fields, String customMessage) {
        if (fields == null || fields.length == 0) {
            return;
        }
        
        List<?> results = findByFields(arg, entityClass, fields);
        
        if (!results.isEmpty()) {
            String message = customMessage != null && !customMessage.isEmpty()
                    ? customMessage
                    : buildDuplicateMessage(entityClass, fields, arg);
            throw new DuplicateDataException(message);
        }
    }
    
    /**
     * Check if data exists (for UPDATE/DELETE operations)
     * Throws DataNotFoundException if data is not found
     * 
     * @param arg Argument object containing field values to check
     * @param entityClass Entity class to query
     * @param fields Field names to check for existence
     * @param customMessage Custom error message (optional)
     */
    public void checkDataExists(Object arg, Class<?> entityClass, String[] fields, String customMessage) {
        if (fields == null || fields.length == 0) {
            return;
        }
        
        List<?> results = findByFields(arg, entityClass, fields);
        
        if (results.isEmpty()) {
            String message = customMessage != null && !customMessage.isEmpty()
                    ? customMessage
                    : buildNotFoundMessage(entityClass, fields, arg);
            throw new DataNotFoundException(message);
        }
    }
    
    /**
     * Find entities by specified fields using JPA Criteria API
     */
    private List<?> findByFields(Object arg, Class<?> entityClass, String[] fields) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery(entityClass);
        Root<?> root = query.from(entityClass);
        
        List<Predicate> predicates = new ArrayList<>();
        
        for (String fieldName : fields) {
            try {
                Object fieldValue = getFieldValue(arg, fieldName);
                if (fieldValue != null) {
                    predicates.add(cb.equal(root.get(fieldName), fieldValue));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to access field: " + fieldName, e);
            }
        }
        
        if (predicates.isEmpty()) {
            return new ArrayList<>();
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        
        TypedQuery<?> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
    
    /**
     * Get field value from object using reflection
     */
    private Object getFieldValue(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = findField(clazz, fieldName);
        
        if (field == null) {
            throw new NoSuchFieldException("Field not found: " + fieldName + " in class " + clazz.getName());
        }
        
        field.setAccessible(true);
        return field.get(obj);
    }
    
    /**
     * Find field in class hierarchy
     */
    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
    
    /**
     * Build duplicate data error message
     */
    private String buildDuplicateMessage(Class<?> entityClass, String[] fields, Object arg) {
        StringBuilder sb = new StringBuilder();
        sb.append("Duplicate ").append(entityClass.getSimpleName()).append(" found with ");
        
        for (int i = 0; i < fields.length; i++) {
            try {
                Object value = getFieldValue(arg, fields[i]);
                sb.append(fields[i]).append("=").append(value);
                if (i < fields.length - 1) {
                    sb.append(", ");
                }
            } catch (Exception e) {
                sb.append(fields[i]).append("=?");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Build data not found error message
     */
    private String buildNotFoundMessage(Class<?> entityClass, String[] fields, Object arg) {
        StringBuilder sb = new StringBuilder();
        sb.append("No ").append(entityClass.getSimpleName()).append(" found with ");
        
        for (int i = 0; i < fields.length; i++) {
            try {
                Object value = getFieldValue(arg, fields[i]);
                sb.append(fields[i]).append("=").append(value);
                if (i < fields.length - 1) {
                    sb.append(", ");
                }
            } catch (Exception e) {
                sb.append(fields[i]).append("=?");
            }
        }
        
        return sb.toString();
    }
}

