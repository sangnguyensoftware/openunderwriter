/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.ail.core;

import static java.util.Locale.CANADA;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestLocale {
    /**
     * @throws Exception
     */
    @Test
    public void testLocaleConstruction() throws Exception {
        try {
            new ThreadLocale().getInstance();
            fail("You should not be able to get an instance from a badly initialised locale");
        }
        catch(IllegalStateException e) {
            // Ignore this - it's what ww want to see
        }
        catch(Throwable e) {
            fail("Wrong exception thrown when getting an instance from a badly initialised locale");
        }

    
        try {
            new ThreadLocale(null, null, null).getInstance();
            fail("You should not be able to get an instance from a badly initialised locale");
        }
        catch(IllegalStateException e) {
            // Ignore this - it's what ww want to see
        }
        catch(Throwable e) {
            fail("Wrong exception thrown when getting an instance from a badly initialised locale");
        }

        try {
            new ThreadLocale(null, null, "p").getInstance();
            fail("You should not be able to get an instance from a badly initialised locale");
        }
        catch(IllegalStateException e) {
            // Ignore this - it's what ww want to see
        }
        catch(Throwable e) {
            fail("Wrong exception thrown when getting an instance from a badly initialised locale");
        }

        try {
            new ThreadLocale(null, java.util.Locale.CANADA.getCountry(), "p").getInstance();
            fail("You should not be able to get an instance from a badly initialised locale");
        }
        catch(IllegalStateException e) {
            // Ignore this - it's what ww want to see
        }
        catch(Throwable e) {
            fail("Wrong exception thrown when getting an instance from a badly initialised locale");
        }

        new ThreadLocale(CANADA.getLanguage(), null, null).getInstance();
        new ThreadLocale(CANADA.getLanguage(), CANADA.getCountry(), null).getInstance();
        new ThreadLocale(CANADA.getLanguage(), CANADA.getCountry(), "p").getInstance();
    }
}
