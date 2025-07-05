package com.epam.rd.autocode.assessment.appliances.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static com.epam.rd.autocode.assessment.appliances.model.TestConstants.*;
import static com.epam.rd.autocode.assessment.appliances.model.TestConstants.OrderRow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRowTest {
    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        Class<?> clazz = Class.forName(ORDER_ROW_TYPE);
        allFields = Arrays.asList(clazz.getDeclaredFields());
        allConstructors = Arrays.asList(clazz.getConstructors());
    }

    /*Tests for constructors*/
    @Test
    @DisplayName("Count constructors")
    void checkCountConstructors() {
        assertEquals(CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    @DisplayName("Modifiers constructors can be public")
    void checkModifiersConstructors() {
        boolean actual = allConstructors.stream()
                .allMatch(c -> Modifier.isPublic(c.getModifiers()));
        assertTrue(actual);
    }

    @Test
    @DisplayName("OrderRow has to default constructor")
    void checkDefaultConstructor() {
        long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }


    /*Tests for fields*/

    void checkFieldsNames(String name){
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(name))
                .count();
        assertEquals(count,1);
    }
    /*not for student*/
    @ParameterizedTest
    @CsvFileSource(resources = "/OrderRowField.csv")
    void checkNameFieldType(String fieldType, String fieldName) {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(1, countLong);
    }
}