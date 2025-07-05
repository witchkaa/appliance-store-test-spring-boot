package com.epam.rd.autocode.assessment.appliances.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static com.epam.rd.autocode.assessment.appliances.model.TestConstants.*;
import static com.epam.rd.autocode.assessment.appliances.model.TestConstants.Employee.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTest {

    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    private static Class<?> clazz;

    @BeforeAll
    static void setup() throws ClassNotFoundException {
        clazz = Class.forName(EMPLOYEE_TYPE);
        allFields = Arrays.asList(clazz.getDeclaredFields());
        allConstructors = Arrays.asList(clazz.getConstructors());
    }

    @Test
    @DisplayName("Test superclass is User")
    void checkSuperclassIsUser() {
        final Class<?> superclass = clazz.getSuperclass();
        final String actual = superclass.getTypeName();
        assertEquals(USER_TYPE, actual);
    }

    /*Tests for CONSTRUCTORS*/
    @Test
    @DisplayName("Count constructors have to be " + CLASS_COUNT_CONSTRUCTORS)
    void checkCountConstructors() {
        assertEquals(CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    @DisplayName("Modifier constructors can be public")
    void checkModifiersConstructors() {
        boolean actual = allConstructors.stream()
                .allMatch(c -> Modifier.isPublic(c.getModifiers()));
        assertTrue(actual);
    }

    @Test
    @DisplayName(CLASS_NAME + " has to default constructor")
    void checkDefaultConstructor() {
        long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }



    /* Tests for FIELDS */
    @Test
    @DisplayName("Check count fields")
    void checkCountFields() {
        assertEquals(CLASS_COUNT_FIELDS, allFields.size());
    }

    @Test
    @DisplayName("Check count private fields")
    void checkAllFieldIsPrivate() {
        final long count = allFields.stream()
                .filter(p -> Modifier.isPrivate(p.getModifiers()))
                .count();
        assertEquals(CLASS_COUNT_FIELDS, count);
    }

    @Test
    @DisplayName("To " + CLASS_NAME + " check fields name")
    void checkFieldNameName() {
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(FIELD_DEPARTMENT))
                .count();
        assertEquals(1, count);
    }

    /*not for student*/
    @Test
    @DisplayName("Check field type and field name")
    void checkNameFieldType() {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(STRING_TYPE)
                        & f.getName().equals(FIELD_DEPARTMENT))
                .count();
        assertEquals(1, countLong);
    }


}