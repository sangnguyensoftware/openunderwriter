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

import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.AbstractConfigurationLoader;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.UnknownNamespaceError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JUnit TestCase.
 */
public class TestJDBCConfigurationLoader extends TestCase {
	private AbstractConfigurationLoader loader=null;
	private String TestNamespace="TESTNAMESPACE";

    /** Constructs a test case with the given name. */
    public TestJDBCConfigurationLoader(String name) {
        super(name);
    }

    public static Test suite() {
		return new TestSuite(TestJDBCConfigurationLoader.class);
    }

	public static void main (String[] args) {
		TestRunner.run(suite());
	}

	/** utility method to delete the config table from the database.
     */
	protected void dropConfigTable() {
		// load the loader
		if (loader==null) {
			loader=AbstractConfigurationLoader.loadLoader();
		}

		try {
            loader.deleteConfigurationRepository();
        }
        catch(Throwable e) {
            // output the error if it isn't a table unknown error.
        		if (e.getMessage().indexOf("Unknown table")==0) {
        		    System.err.println("ignored: "+e);
        		}
        	}
    }

	/**
     * Utility method to remove all the test namespaces from the table.
     */
	private void clearNamespaces() {
		// load the loader
		if (loader==null) {
			loader=AbstractConfigurationLoader.loadLoader();
		}

		// load the JDBC driver
		try {
            loader.purgeAllConfigurations();
        }
        catch(Throwable e) {
			// ignore this - some tests delete the table!
        }
    }

    /** Sets up the fixture (run before every test).
     * Get a loader, and delete the testnamespace from the config table.
     */
    protected void setUp() {
		clearNamespaces();
        loader.reset();
    }

    /** Tears down the fixture, for example, close a network connection. This method is called after a test is executed. */
    protected void tearDown() {
    }

	/**
     * Try to load a configuration when the table does not exist.
     * The JDBC config loader should automatically create the config table
     * if it finds that is does not exist. This test ensures that this is
     * the case.
     */
	public void testLoadWithMissingTable() {
		try {
			dropConfigTable();
			loader.loadConfiguration(TestNamespace, new VersionEffectiveDate());
			fail("Loaded an undefined namespace!!!");
        }
		catch(UnknownNamespaceError e) {
            // ignore, this is what we expect.
		}
        catch(Throwable e) {
			fail("unexpected exception:"+e);
        }
    }

	/**
     * Attempt to load an undefined configuration.
     * loadConfiguration should throw an 'UnknownNamespaceError' if an attempt is
     * made to load the configuration of an undefined namespace.
     * <ul>
     * <li>Attempt to load the config for the namespace 'namespace-that-does-not-exist'.<li>
     * <li>Fail if no exception/error is thrown.</li>
     * <li>Fail if any exception/error other than UnknownNamespaceError is thrown.</li>
     * </ul>
     */
    public void testLoadUndefinedConfiguration() {
		try {
			loader.loadConfiguration(TestNamespace, new VersionEffectiveDate());
			fail("Loaded an undefined namespace!!!");
		}
        catch(UnknownNamespaceError e) {
			// ignore this, its what we're expecting
        }
        catch(Throwable t) {
			fail("Unexpected exception:"+t);
        }
    }

	/**
     * Test that a sample configuration can be saved, and reloaded.
	 */
    public void testSaveAndLoadConfiguration() throws Exception {
        Configuration config;

        config=new Configuration();
        config.setName("Peter");
        config.setVersion("1.0");
		config.setTimeout(1);
		
        loader.saveConfiguration(TestNamespace, config);
		Thread.sleep(10);

        config=loader.loadConfiguration(TestNamespace, new VersionEffectiveDate());
        assertEquals(config.getName(), "Peter");
        assertEquals(config.getVersion(), "1.0");
    }

	/**
     * Test the history mechanism. Show that a configuration can be save and
     * loaded, then replaced with a new version, which can also be loaded, and
     * then that the old versions can also be reloaded.<p>
	 * <ul>
     * <li>Save a new configuration.</li>
     * <li>Load the configuration, and check that it is the same.</li>
	 * <li>Fail if the configuration was not the same.</li>
     * <li>wait 10 milliseconds (configurations work from date stamps. 1 millisecond should be enough, but we have time!).<li>
     * <li>Change the configuration and save it again (this should create a second persistent record).</li>
     * <li>wait 10 milliseconds again.<li>
     * <li>Load the configuration, and check that the change just made is present.</li>
	 * <li>Fail if the change made is not present.</li>
     * <li>Load the original configuration by specifiying a timestamp before the 1 second delay.</li>
     * <li>Check the this configuration matches the original.</li>
     * <li>Fail if it does not match.</li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ul>
     */
	public void testSaveLoadSaveLoadLoadOldConfiguration() {
		Configuration  config=null;

		try {
			// Save a new configuration
			config=new Configuration();
	        config.setName("Paul");
	        config.setVersion("1.0");
	        config.setTimeout(1);
	        
	        loader.saveConfiguration(TestNamespace, config);
			Thread.sleep(10);

			config=null;

			VersionEffectiveDate d=new VersionEffectiveDate();

			// Load the configuration, and check that it is the same.
            config=loader.loadConfiguration(TestNamespace, d);

			// Fail if the configuration was not the same.
            assertEquals(config.getName(), "Paul");
            assertEquals(config.getVersion(), "1.0");

			// Wait 1 second (configurations work from date stamps. 1 millisecond should be enough, but we have time!).
			Thread.sleep(10);

			// Change the configuration and save it again (this should create a second persistent record).
			config.setName("John");
            config.setVersion("2.0");
	        loader.saveConfiguration(TestNamespace, config);
			Thread.sleep(10);

			config=null;

			// Load the configuration, and check that the change just made is present.
            config=loader.loadConfiguration(TestNamespace, new VersionEffectiveDate());
            assertEquals(config.getName(), "John");
            assertEquals(config.getVersion(), "2.0");

			config=null;

			// Load the original configuration by specifiying a timestamp before the 1 second delay.
            config=loader.loadConfiguration(TestNamespace, d);

			// Check the this configuration matches the original.
            assertEquals(config.getName(), "Paul");
            assertEquals(config.getVersion(), "1.0");

		}
        catch(Throwable e) {
        	e.printStackTrace();
			fail("Unexpected exception:"+e);
		}
    }

    /**
     * Test the JDBC connections double close disposition.
     * Some drivers object to being told to close a closed connection which is something
     * the JDBCConfigurationLoader does from time to time, this test checks the configured
     * driver to make sure it allows double closing.
     */
    public void testDoubleCloseDisposition() throws Exception {
        if (loader==null) {
            loader=AbstractConfigurationLoader.loadLoader();
        }

        // fetch the loader's properties
        Properties props=loader.getLoaderParams();

        // load the JDBC driver
        Class.forName(props.getProperty("driver"));
        Connection con=DriverManager.getConnection(props.getProperty("url")+props.getProperty("databaseName"), props);
        Statement st=con.createStatement();
        st.executeQuery("SELECT * FROM "+props.getProperty("table"));
        st.close();
        try {
            st.close();
            // double close was ignored if we get here
        }
        catch(SQLException e) {
            fail("double close threw exception:"+e);
        }
        con.close();
    }
}
