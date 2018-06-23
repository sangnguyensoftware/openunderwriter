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

import static org.junit.Assert.*;

import com.ail.core.Core;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.CoreUserBaseCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

/**
 * This test ensures that the VersionEffectiveDate mechanism works across timezones. This
 * test is not like a normal junit test. The {@link #runTests()} test execs two 
 * processes one in a JVM running with a GMT locale which sets a configure property then
 * a second JVM is run with EST as the timezone which reads the property's value.<ol>
 * <li>In the GMT timezone:<ol>
 *  <li>Reset a test class's configuration.</li>
 *  <li>Update the configure by setting the parameter "TestParameter" to the value "2"</li>
 * </ol></li>
 * <li>In the EST timezone:<ol>
 * <li>Load the configuration and fetch the value of "TestParameter"</li>
 * <li>Fail if the test parameter's value is anything other than 2</li>
 * </ol></li>
 */
public class TimezoneTest extends CoreUserBaseCase {

    /**
     * Sets up the fixture (run before every test).
     * Get an instance of Core, and delete the testnamespace from the config table.
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        ConfigurationHandler.resetCache();

        if (System.getProperty("RunTest")==null) {
            setVersionEffectiveDate(new VersionEffectiveDate());
            tidyUpTestData();
            setCore(new Core(this));
            getCore().resetConfiguration();
            setVersionEffectiveDate(new VersionEffectiveDate());
            resetConfiguration();
        }
        else {
            setVersionEffectiveDate(new VersionEffectiveDate());
            setCore(new Core(this));
            getCore().resetConfiguration();
        }

        setVersionEffectiveDate(new VersionEffectiveDate());
        ConfigurationHandler.resetCache();
    }

    @Test
    public void runTests() throws IOException, InterruptedException {
        if (System.getProperty("RunTest")==null) {
            execTestsForTimezone("GMT");
            execTestsForTimezone("EST");
        }
    }
    
    private void execTestsForTimezone(String timezone) throws IOException, InterruptedException {
        String classpath=System.getProperty("java.class.path");
        String s;
        
        Process p=Runtime.getRuntime().exec("java -classpath "+classpath+" -DRunTest=yes -Duser.timezone="+timezone+" org.junit.runner.JUnitCore com.ail.core.TimezoneTest");
        
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        
        assertEquals(0, p.waitFor());
    }

    /**
     * If the timezone is GMT:
     * <ol>
     * <li>Get the class' configuration</li>
     * <li>Set the value of TestParameter to 2</li>
     * <li>Save the configuration</li>
     * </ol>
     * @throws Exception
     */
    @Test
    public void testUpdateConfig() throws Exception {
        if (System.getProperty("RunTest")!=null) {
            if (TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT).equals("GMT")) {
                Configuration config=getConfiguration();
                config.findParameter("TestParameter").setValue("2");
                getCore().setConfiguration(config);
                System.out.println("Configuration written in GMT");
            }
        }
    }

    /**
     * If the timezone in EST:
     * <ol>
     * <li>Get the value of TestParameter</li>
     * <li>Fail if the value is not 2</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     * @throws Exception
     */
    @Test
    public void testQueryConfig() throws Exception {
        if (System.getProperty("RunTest")!=null) {
            if (TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT).equals("EST")) {
                String param=getCore().getParameterValue("TestParameter");
                assertNotNull(param);
                assertEquals("2", param);
                System.out.println("Configuration read in EST");
            }
        }
    }

}
