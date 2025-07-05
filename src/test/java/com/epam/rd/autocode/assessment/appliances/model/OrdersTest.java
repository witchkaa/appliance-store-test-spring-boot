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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersTest {

    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        final Class<?> userClass = Class.forName(TestConstants.ORDERS_TYPE);
        allFields = Arrays.asList(userClass.getDeclaredFields());
        allConstructors = Arrays.asList(userClass.getConstructors());
    }

    /*Tests for constructors*/
    @Test
    void checkCountConstructors() {
        assertEquals(TestConstants.Orders.CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    void checkModifiersConstructors() {
        final boolean actual = allConstructors.stream()
                .allMatch(c -> Modifier.isPublic(c.getModifiers()));
        assertTrue(actual);
    }

    @Test
    void checkDefaultConstructor() {
        final long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }



    @Test
    void checkAllFieldsArePrivate(){
        final boolean isPrivate = allFields.stream()
                .allMatch(f -> Modifier.isPrivate(f.getModifiers()));
        assertTrue(isPrivate);
    }
    @ParameterizedTest
    @CsvSource({"id",
            "client",
            "employee",
            "orderRowSet"
    })
    void checkFieldsNames(String name){
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(name))
                .count();
        assertEquals(1,count);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/OrdersField.csv")
    void checkNameFieldType(String fieldType, String fieldName, long expected) {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(expected, countLong);
    }
}