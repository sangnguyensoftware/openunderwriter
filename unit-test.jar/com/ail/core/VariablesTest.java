package com.ail.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class VariablesTest {
    private static final String INTEGER_VAR_NAME = "num var 1";
    private static final String DOUBLE_VAR_NAME = "num var 2";
    private static final String STRING_VAR_NAME = "string var 1";
    private static final String LIST_VAR_NAME = "list var 1";
    private static final String LIST_VAR_NAME_2 = "list var 2";

    private static final Integer INTEGER_VAR_VALUE = 123;
    private static final Double DOUBLE_VAR_VALUE = new Double(20.2);
    private static final String STRING_VAR_VALUE = "hello world";
    private static final List<String> LIST_VAR_VALUE = Arrays.asList("one", "two", "three");
    private static final List<String> LIST_VAR_VALUE_2 = Arrays.asList("one", "one", "one");

    private Variables variables;

    @Before
    public void setupSut() {
        variables = new Variables();
        variables.add(INTEGER_VAR_NAME, INTEGER_VAR_VALUE);
        variables.add(STRING_VAR_NAME, STRING_VAR_VALUE);
        variables.add(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE);
        variables.add(LIST_VAR_NAME, LIST_VAR_VALUE);
        variables.add(LIST_VAR_NAME_2, LIST_VAR_VALUE_2);
    }

    @Test
    public void testGreaterThanLessThan() {
        assertFalse(variables.greaterThan(INTEGER_VAR_NAME, INTEGER_VAR_VALUE + 1));
        assertTrue(variables.greaterThan(INTEGER_VAR_NAME, INTEGER_VAR_VALUE - 1));

        assertFalse(variables.lessThan(INTEGER_VAR_NAME, INTEGER_VAR_VALUE - 1));
        assertTrue(variables.lessThan(INTEGER_VAR_NAME, INTEGER_VAR_VALUE + 1));
    }

    @Test
    public void testEquals() {
        assertTrue(variables.equals(INTEGER_VAR_NAME, INTEGER_VAR_VALUE));
        assertFalse(variables.equals(INTEGER_VAR_NAME, INTEGER_VAR_VALUE + 1));

        assertTrue(variables.equals(STRING_VAR_NAME, STRING_VAR_VALUE));
        assertFalse(variables.equals(STRING_VAR_NAME, STRING_VAR_VALUE + "extra"));

        assertTrue(variables.equals(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE));
        assertFalse(variables.equals(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE + 2.2));
    }

    @Test
    public void testBetween() {
        assertTrue(variables.between(INTEGER_VAR_NAME, INTEGER_VAR_VALUE - 1, INTEGER_VAR_VALUE + 1));
        assertTrue(variables.between(INTEGER_VAR_NAME, INTEGER_VAR_VALUE, INTEGER_VAR_VALUE + 1));
        assertTrue(variables.between(INTEGER_VAR_NAME, INTEGER_VAR_VALUE - 1, INTEGER_VAR_VALUE));
        assertFalse(variables.between(INTEGER_VAR_NAME, INTEGER_VAR_VALUE + 1, INTEGER_VAR_VALUE + 2));
        assertFalse(variables.between(INTEGER_VAR_NAME, INTEGER_VAR_VALUE - 2, INTEGER_VAR_VALUE - 2));

        assertTrue(variables.between(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE - 1, DOUBLE_VAR_VALUE + 1));
        assertTrue(variables.between(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE, DOUBLE_VAR_VALUE + 1));
        assertTrue(variables.between(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE - 1, DOUBLE_VAR_VALUE));
        assertFalse(variables.between(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE + 1, DOUBLE_VAR_VALUE + 2));
        assertFalse(variables.between(DOUBLE_VAR_NAME, DOUBLE_VAR_VALUE - 2, DOUBLE_VAR_VALUE - 2));
    }
    
    @Test
    public void testContains() {
        assertTrue(variables.contains(LIST_VAR_NAME, "one"));
        assertFalse(variables.contains(LIST_VAR_NAME, "five"));

        assertTrue(variables.containsOnly(LIST_VAR_NAME_2, "one"));
        assertFalse(variables.containsOnly(LIST_VAR_NAME_2, "five"));
        assertFalse(variables.containsOnly(LIST_VAR_NAME, "one"));
}
}
