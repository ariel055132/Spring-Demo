package com.example.foundation.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonUtil Test")
class JsonUtilTest {

    // ──────────────────────────────────────────────
    // Shared test fixture
    // ──────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Person {
        private String name;
        private int age;
        private String email;
    }

    private final Person alice = new Person("Alice", 30, "alice@example.com");

    // ──────────────────────────────────────────────
    // toJson
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("toJson serializes object to compact JSON string")
    void toJson_normalObject() {
        String json = JsonUtil.toJson(alice);

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"age\":30"));
        // Compact output must not contain newlines
        assertFalse(json.contains("\n"));
    }

    @Test
    @DisplayName("toJson returns null for null input")
    void toJson_null() {
        assertNull(JsonUtil.toJson(null));
    }

    @Test
    @DisplayName("toJson handles a simple Map input")
    void toJson_map() {
        Map<String, Object> map = Map.of("key", "value", "count", 5);
        String json = JsonUtil.toJson(map);

        assertNotNull(json);
        assertTrue(json.contains("\"key\":\"value\"") || json.contains("\"count\":5"));
    }

    // ──────────────────────────────────────────────
    // toPrettyJson
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("toPrettyJson serializes object with indentation")
    void toPrettyJson_normalObject() {
        String json = JsonUtil.toPrettyJson(alice);

        assertNotNull(json);
        assertTrue(json.contains("\n"), "Pretty JSON should contain newlines");
        assertTrue(json.contains("  "), "Pretty JSON should contain indentation");
        assertTrue(json.contains("\"name\" : \"Alice\""));
        assertTrue(json.contains("\"age\" : 30"));
    }

    @Test
    @DisplayName("toPrettyJson returns null for null input")
    void toPrettyJson_null() {
        assertNull(JsonUtil.toPrettyJson(null));
    }

    // ──────────────────────────────────────────────
    // toObject
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("toObject deserializes JSON string to target class")
    void toObject_validJson() {
        String json = "{\"name\":\"Bob\",\"age\":25,\"email\":\"bob@example.com\"}";
        Person person = JsonUtil.toObject(json, Person.class);

        assertNotNull(person);
        assertEquals("Bob", person.getName());
        assertEquals(25, person.getAge());
        assertEquals("bob@example.com", person.getEmail());
    }

    @Test
    @DisplayName("toObject returns null for blank JSON")
    void toObject_blankJson() {
        assertNull(JsonUtil.toObject("", Person.class));
        assertNull(JsonUtil.toObject("   ", Person.class));
        assertNull(JsonUtil.toObject(null, Person.class));
    }

    @Test
    @DisplayName("toObject returns null when clazz is null")
    void toObject_nullClass() {
        assertNull(JsonUtil.toObject("{\"name\":\"Alice\"}", null));
    }

    @Test
    @DisplayName("toObject ignores unknown fields in JSON")
    void toObject_unknownFields() {
        String json = "{\"name\":\"Alice\",\"age\":30,\"email\":\"alice@example.com\",\"unknown\":\"ignored\"}";
        Person person = JsonUtil.toObject(json, Person.class);

        assertNotNull(person);
        assertEquals("Alice", person.getName());
    }

    @Test
    @DisplayName("toObject throws RuntimeException for invalid JSON")
    void toObject_invalidJson() {
        assertThrows(RuntimeException.class,
                () -> JsonUtil.toObject("not-valid-json", Person.class));
    }

    // ──────────────────────────────────────────────
    // toObjectList
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("toObjectList deserializes JSON array to List")
    void toObjectList_validJsonArray() {
        String json = "[{\"name\":\"Alice\",\"age\":30},{\"name\":\"Bob\",\"age\":25}]";
        List<Person> list = JsonUtil.toObjectList(json, Person.class);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("Alice", list.get(0).getName());
        assertEquals("Bob", list.get(1).getName());
    }

    @Test
    @DisplayName("toObjectList returns empty list for empty JSON array")
    void toObjectList_emptyArray() {
        List<Person> list = JsonUtil.toObjectList("[]", Person.class);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("toObjectList returns null for blank JSON")
    void toObjectList_blankJson() {
        assertNull(JsonUtil.toObjectList("", Person.class));
        assertNull(JsonUtil.toObjectList(null, Person.class));
    }

    @Test
    @DisplayName("toObjectList throws RuntimeException for invalid JSON")
    void toObjectList_invalidJson() {
        assertThrows(RuntimeException.class,
                () -> JsonUtil.toObjectList("not-an-array", Person.class));
    }

    // ──────────────────────────────────────────────
    // convertObject
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("convertObject converts Map to POJO")
    void convertObject_mapToPojo() {
        Map<String, Object> map = Map.of("name", "Charlie", "age", 40, "email", "charlie@example.com");
        Person person = JsonUtil.convertObject(map, Person.class);

        assertNotNull(person);
        assertEquals("Charlie", person.getName());
        assertEquals(40, person.getAge());
    }

    @Test
    @DisplayName("convertObject converts POJO to another POJO type")
    void convertObject_pojoToPojo() {
        // Use Map as intermediate to demonstrate cross-type conversion
        Map<String, Object> asMap = JsonUtil.beanToMap(alice);
        Person copy = JsonUtil.convertObject(asMap, Person.class);

        assertNotNull(copy);
        assertEquals(alice.getName(), copy.getName());
        assertEquals(alice.getAge(), copy.getAge());
    }

    @Test
    @DisplayName("convertObject returns null when obj is null")
    void convertObject_nullObj() {
        assertNull(JsonUtil.convertObject(null, Person.class));
    }

    @Test
    @DisplayName("convertObject returns null when clazz is null")
    void convertObject_nullClass() {
        assertNull(JsonUtil.convertObject(alice, null));
    }

    // ──────────────────────────────────────────────
    // beanToMap
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("beanToMap converts POJO fields to map entries")
    void beanToMap_normalObject() {
        Map<String, Object> map = JsonUtil.beanToMap(alice);

        assertNotNull(map);
        assertEquals("Alice", map.get("name"));
        assertEquals(30, map.get("age"));
        assertEquals("alice@example.com", map.get("email"));
    }

    @Test
    @DisplayName("beanToMap returns null for null input")
    void beanToMap_null() {
        assertNull(JsonUtil.beanToMap(null));
    }

    @Test
    @DisplayName("beanToMap result can be round-tripped back to the original type")
    void beanToMap_roundTrip() {
        Map<String, Object> map = JsonUtil.beanToMap(alice);
        Person restored = JsonUtil.convertObject(map, Person.class);

        assertEquals(alice.getName(), restored.getName());
        assertEquals(alice.getAge(), restored.getAge());
        assertEquals(alice.getEmail(), restored.getEmail());
    }

    // ──────────────────────────────────────────────
    // Round-trip: toJson → toObject
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("toJson and toObject are inverse operations")
    void roundTrip_toJsonAndToObject() {
        String json = JsonUtil.toJson(alice);
        Person restored = JsonUtil.toObject(json, Person.class);

        assertNotNull(restored);
        assertEquals(alice.getName(), restored.getName());
        assertEquals(alice.getAge(), restored.getAge());
        assertEquals(alice.getEmail(), restored.getEmail());
    }

    @Test
    @DisplayName("toJson and toObjectList are inverse operations for lists")
    void roundTrip_toJsonAndToObjectList() {
        List<Person> original = List.of(alice, new Person("Bob", 25, "bob@example.com"));
        String json = JsonUtil.toJson(original);
        List<Person> restored = JsonUtil.toObjectList(json, Person.class);

        assertNotNull(restored);
        assertEquals(2, restored.size());
        assertEquals("Alice", restored.get(0).getName());
        assertEquals("Bob", restored.get(1).getName());
    }
}
