package com.epam.rd.autocode.assessment.appliances.model;

import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        final Class<?> userClass = Class.forName(TestConstants.USER_TYPE);
        allFields = Arrays.asList(userClass.getDeclaredFields());
        allConstructors = Arrays.asList(userClass.getConstructors());
    }

    /*Tests constructors*/
    @Test
    @DisplayName("Count constructors have to be " + TestConstants.User.CLASS_COUNT_CONSTRUCTORS)
    void checkCountConstructors() {
        assertEquals(TestConstants.User.CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    @DisplayName("Constructors modifiers can be public")
    void checkModifiersConstructors() {
        boolean actual = allConstructors.stream()
                .allMatch(c -> Modifier.isPublic(c.getModifiers()));
        assertTrue(actual);
    }

    @Test
    @DisplayName(TestConstants.User.CLASS_NAME + " has to default constructor")
    void checkDefaultConstructor() {
        long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }


    @ParameterizedTest
    @CsvSource({"id,1",
            "name,1",
            "email,1",
            "password,1"
    })
    @DisplayName("To " + TestConstants.User.CLASS_NAME + " check fields name")
    void checkFieldNameName(String name, long expected) {
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(name))
                .count();
        assertEquals(expected, count);
    }

    /*not for student*/
    @DisplayName("Check field type and field name")
    @ParameterizedTest
    @CsvFileSource(resources = "/UserField.csv")
    void checkNameFieldType(String fieldType, String fieldName, long expected) {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(expected, countLong);
    }
}