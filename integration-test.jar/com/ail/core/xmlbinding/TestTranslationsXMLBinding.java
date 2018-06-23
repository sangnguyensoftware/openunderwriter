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

package com.ail.core.xmlbinding;

import static java.util.Locale.US;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.Core;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.ThreadLocale;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.XMLString;
import com.ail.core.language.Translations;

/**
 */
public class TestTranslationsXMLBinding extends CoreUserBaseCase {

    /**
     * Sets up the fixture (run before every test).
     * Get an instance of Core, and delete the testnamespace from the config table.
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        tidyUpTestData();
        setupSystemProperties();
        setCore(new Core(this));
        getCore().resetConfiguration();
        getCore().clearConfigurationCache();
        setVersionEffectiveDate(new VersionEffectiveDate());
    }

    /**
     * Tears down the fixture (run after each test finishes)
     * @throws Exception 
     */
    @After
    public void tearDown() throws Exception {
        tidyUpTestData();
    }

    @Test
    public void testFromXML() throws Exception {
        XMLString instanceXml = new XMLString(this.getClass().getResourceAsStream("TestTranslationsXMLBinding.xml"));
        Translations instanceObj = getCore().fromXML(Translations.class, instanceXml);
        assertNotNull(instanceObj);

        java.util.Locale saved=ThreadLocale.getThreadLocale();
        ThreadLocale.setThreadLocale(US);

        assertEquals("Second <b>String</b> number two", instanceObj.translate("key2", "two"));

        ThreadLocale.setThreadLocale(saved);
    }
}
