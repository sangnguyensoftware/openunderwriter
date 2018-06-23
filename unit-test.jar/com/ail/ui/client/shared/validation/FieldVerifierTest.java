package com.ail.ui.client.shared.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ail.ui.shared.validation.FieldVerifier;

public class FieldVerifierTest {
    
    public void testDateValidation() {
        
        assertTrue(FieldVerifier.isValidDate("2001-01-01"));
        assertTrue(FieldVerifier.isValidDate("2020-12-31"));

        assertFalse(FieldVerifier.isValidDate("2020-02-31"));
        assertFalse(FieldVerifier.isValidDate("2001-31-01"));
        assertFalse(FieldVerifier.isValidDate("2001-01"));
        assertFalse(FieldVerifier.isValidDate("01-01-2010"));
        assertFalse(FieldVerifier.isValidDate("02/04/2012"));
        assertFalse(FieldVerifier.isValidDate("biscuit"));

    }

    @Test
    public void testHasLengthValidation() {
        assertTrue(FieldVerifier.hasLength("A"));
        assertFalse(FieldVerifier.hasLength(""));
        assertFalse(FieldVerifier.hasLength(null));
        assertFalse(FieldVerifier.hasLength(" "));
    }
    
}
