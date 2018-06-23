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

import static java.util.Locale.CANADA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.ThreadLocale;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.XMLString;
import com.ail.core.configure.ConfigurationHandler;

public class TestLocaleXMLBinding extends CoreUserBaseCase {
    static boolean setup=false;

    /**
     * Sets up the fixture (run before every test).
     * Get an instance of Core, and delete the testnamespace from the config table.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        CoreProxy coreProxy = new CoreProxy(this.getConfigurationNamespace(), this);
        setCore(coreProxy.getCore());
        tidyUpTestData();
        setupSystemProperties();
        getCore().resetConfiguration();
        ConfigurationHandler.resetCache();
        coreProxy.setVersionEffectiveDateToNow();
    }

    /**
     * Tears down the fixture (run after each test finishes)
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        tidyUpTestData();
    }

    /**
     * Always select the latest configurations
     *
     * @return
     */
    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return new VersionEffectiveDate();
    }

    @Test
    public void testLocaleToXML() throws Exception {
        ThreadLocale threadLocale;

        threadLocale=new ThreadLocale(CANADA.getLanguage(), CANADA.getCountry(), "p");

        String xml=getCore().toXML(threadLocale).toString();

        assertTrue(xml.indexOf("language=\"en\"")>0);
        assertTrue(xml.indexOf("country=\"CA\"")>0);
        assertTrue(xml.indexOf("variant=\"p\"")>0);
    }

    @Test
    public void testLocaleFromXML() throws Exception {
        ThreadLocale threadLocale;

        String xml="<locale language='de' country='DE' variant='p' xsi:type='java:com.ail.core.ThreadLocale' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'/>";

        threadLocale=getCore().fromXML(ThreadLocale.class, new XMLString(xml));

        assertEquals("DE", threadLocale.getCountry());
        assertEquals("de", threadLocale.getLanguage());
        assertEquals("p", threadLocale.getVariant());
    }
}
