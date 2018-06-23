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

package com.ail.core.configuration;

import static org.junit.Assert.assertEquals;

import java.security.Principal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.ConfigurationTidyService;
import com.ail.core.Core;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationHandler;

/**
 * The tests defined here exercise the Core system's factory.
 * They use the Core class as an Service or core client would.
 * Note: These tests assume that the JDBCConfigurationLoader is being used.
 */
public class TestCoreConfigReset implements CoreUser {
	private Core core=null;
	private VersionEffectiveDate versionEffectiveDate=null;

	/**
     * Tidy up (delete) the config database records, this means deleting the
     * config records for the core namespace!!
     */
	@After
	public void tidyUpTestData() {
        new ConfigurationTidyService().clearCoreNamespace().clearTestNamespace();
	}

    /**
     * Sets up the fixture (run before every test).
     * Get an instance of Core, and delete the testnamespace from the config table.
     */
	@Before
    public void setUp() {
        System.setProperty("org.xml.sax.parser", "org.apache.xerces.parsers.SAXParser");
        System.setProperty("javax.xml.transform.TransformerFactoy", "net.sf.saxon.TransformerFactoryImpl");
        tidyUpTestData();
        core=new Core(this);
		versionEffectiveDate=new VersionEffectiveDate();
    }

    /**
     * Tears down the fixture (run after each test finishes)
     */
	@After
    public void tearDown() {
		tidyUpTestData();
    }

	/**
     * Method demanded by the CoreUser interface.
     * @return A date to use to selecte the corrent version of config info.
     */
    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
		return versionEffectiveDate;
    }

    /**
     * Get the security principal associated with this instance.
     * @return The associated security principal - if defined, null otherwise.
     */
    @Override
    public Principal getSecurityPrincipal() {
        return null;
    }

    @Override
    public String getConfigurationNamespace() {
        return Core.CORE_NAMESPACE;
    }

    /**
	 * Test that the core's configuration can be reset.
     * <ul>
     * <li>Delete the Core's configuration records from the database (this is
     * handled by 'tidyUpTestDate'.</li>
	 * <li>Create and save a known "dummy" configuration.</li>
     * <li>Get the configuration, and check that it is the dummy config.</li>
     * <li>Invoke the Core's resetConfiguration method.</li>
     * <li>Sleep for 1 second (the core's config has a 1 second stale check cycle).</li>
	 * <li>Set the version effective date to now.</li>
     * <li>Get the core's configuration (core.getConfiguration).</li>
     * <li>Fail if the configuration's title is not 'Default Core Configuration'</li>
     * <li>Fail if the configuration's timeout is not 1000.</li>
	 * <li>Fail if any exceptions are thrown.</li>
     */
    @Test
    public void testCoreConfigReset() throws Exception {
        Configuration c=new Configuration();
        c.setName("Dummy test configuration");
        c.setTimeout(2000);
        core.setConfiguration(c);
        c=null;

        Thread.sleep(2100);
        versionEffectiveDate=new VersionEffectiveDate();
        c=core.getConfiguration();
        assertEquals("Dummy test configuration", c.getName());
        assertEquals(2000, c.getTimeout());
        c=null;

        core.resetConfiguration();

        Thread.sleep(1000);
        versionEffectiveDate=new VersionEffectiveDate();
        c=core.getConfiguration();
        assertEquals("Default Core Configuration", c.getName());
        assertEquals(-1, c.getTimeout());
    }

    /**
     * Test that the CoreProxy's configuration can be reset.
     */
    @Test
    public void testCoreProxyConfigReset() throws Exception {
        core.resetConfiguration();
        ConfigurationHandler.resetCache();
        CoreProxy cp=new CoreProxy();
        cp.resetConfiguration();
    }

}
