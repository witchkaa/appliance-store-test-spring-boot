package com.epam.rd.autocode.assessment.appliances.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplianceTest {
    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeAll
    static void setup() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(TestConstants.APPLIANCE_TYPE);
        allFields = Arrays.asList(clazz.getDeclaredFields());
        allConstructors = Arrays.asList(clazz.getConstructors());
    }

    /* Tests for CONSTRUCTORS */
    @Test
    @DisplayName("Count constructors")
    void checkCountConstructors() {
        Assertions.assertEquals(TestConstants.Appliance.CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    @DisplayName("Modifiers constructors can be public")
    void checkModifiersConstructors() {
        boolean actual = allConstructors.stream()
                .allMatch(constructor -> Modifier.isPublic(constructor.getModifiers()));
        assertTrue(actual);
    }

    @Test
    @DisplayName(TestConstants.Appliance.CLASS_NAME + " has default constructor")
    void checkDefaultConstructor() {
        long count = allConstructors.stream()
                .filter(constructor -> constructor.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }


    @ParameterizedTest
    @CsvSource({"id,1",
            "name,1",
            "category,1",
            "model,1",
            "manufacturer,1",
            "powerType,1",
            "characteristic,1",
            "description,1",
            "power,1"
    })
    @DisplayName("To " + TestConstants.Appliance.CLASS_NAME + " check fields name")
    void checkFieldNameName(String name, long expected) {
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(name))
                .count();
        assertEquals(expected, count);
    }

    /*not for student*/
    @DisplayName("Check field type and field name")
    @ParameterizedTest
    @CsvFileSource(resources = "/ApplianceFields.csv")
    void checkNameFieldType(String fieldType, String fieldName, long expected) {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(expected, countLong);
    }
}