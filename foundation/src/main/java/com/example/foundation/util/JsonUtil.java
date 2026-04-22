package com.example.foundation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON serialization and deserialization using Jackson.
 * A single shared ObjectMapper is used for all operations — ObjectMapper is
 * thread-safe after configuration and expensive to construct per-call.
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            // Required for LocalDate / LocalDateTime support
            .registerModule(new JavaTimeModule())
            // Write dates as ISO-8601 strings, not epoch arrays
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // Tolerate extra fields in JSON that have no matching field in the target class
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonUtil() {
        // Utility class — prevent instantiation
    }

    // ========== Serialization ==========

    /**
     * Serialize an object to a compact JSON string.
     *
     * @param obj object to serialize
     * @return JSON string, or {@code null} if {@code obj} is null
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Serialize an object to an indented (pretty-printed) JSON string.
     *
     * @param obj object to serialize
     * @return indented JSON string, or {@code null} if {@code obj} is null
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to pretty JSON", e);
        }
    }

    // ========== Deserialization ==========

    /**
     * Deserialize a JSON string to an object of the given type.
     *
     * @param json  JSON string
     * @param clazz target class
     * @param <T>   target type
     * @return deserialized object, or {@code null} if {@code json} is blank or {@code clazz} is null
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json) || clazz == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Deserialize a JSON array string to a {@code List} of objects.
     *
     * @param json  JSON array string
     * @param clazz element class
     * @param <T>   element type
     * @return list of deserialized objects, or {@code null} if {@code json} is blank or {@code clazz} is null
     */
    public static <T> List<T> toObjectList(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json) || clazz == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to List<" + clazz.getSimpleName() + ">", e);
        }
    }

    // ========== Object Conversion ==========

    /**
     * Convert one object type to another via Jackson's value conversion.
     * Equivalent to serializing to JSON and back — useful for Map→POJO or POJO→POJO conversions.
     *
     * @param obj   source object
     * @param clazz target class
     * @param <T>   target type
     * @return converted object, or {@code null} if either argument is null
     */
    public static <T> T convertObject(Object obj, Class<T> clazz) {
        if (obj == null || clazz == null) {
            return null;
        }
        return MAPPER.convertValue(obj, clazz);
    }

    /**
     * Convert a bean (POJO) to a {@code Map<String, Object>}.
     * Field names become map keys; nested objects are recursively converted.
     *
     * @param obj source object
     * @return map representation, or {@code null} if {@code obj} is null
     */
    public static Map<String, Object> beanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }
}
