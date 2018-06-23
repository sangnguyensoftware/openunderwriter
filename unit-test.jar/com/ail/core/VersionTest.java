package com.ail.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionTest {

    @Test
    public void testIsNewerVersionNullHandling() throws PreconditionException {
        assertFalse(new Version().isNewerVersionThan(new Version()));
        assertTrue(new Version("1.1").isNewerVersionThan(new Version()));
        assertFalse(new Version().isNewerVersionThan(new Version("1.0")));
    }

    @Test(expected = PreconditionException.class)
    public void testIsNewerVersionBadFormatThis() throws PreconditionException {
        assertFalse(new Version("1.b.c").isNewerVersionThan(new Version("1.0")));
    }

    @Test(expected = PreconditionException.class)
    public void testIsNewerVersionBadFormatThat() throws PreconditionException {
        assertFalse(new Version("1.0").isNewerVersionThan(new Version("1.b.c")));
    }

    @Test
    public void testIsNewerVersionOutlineNumberHandling() throws PreconditionException {
        assertTrue(new Version("1.10").isNewerVersionThan(new Version("1.9")));
        assertFalse(new Version("1.9").isNewerVersionThan(new Version("1.10")));

        assertTrue(new Version("1.9").isNewerVersionThan(new Version("1.8")));
        assertFalse(new Version("1.8").isNewerVersionThan(new Version("1.9")));

        assertTrue(new Version("1.1.1").isNewerVersionThan(new Version("1.1")));
        assertFalse(new Version("1.1").isNewerVersionThan(new Version("1.1.1")));
    }
}