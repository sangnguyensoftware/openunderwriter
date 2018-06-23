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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.CommandScript;
import com.ail.core.Core;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.PreconditionException;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.server.CatalogCarService.CatalogCarCommand;
import com.ail.core.configure.server.DeployCarService.DeployCarCommand;
import com.ail.core.configure.server.GetCommandScriptService.GetCommandScriptCommand;
import com.ail.core.configure.server.PackageCarService.PackageCarCommand;
import com.ail.core.configure.server.SetCommandScriptService.SetCommandScriptCommand;
import com.ail.core.dummyservice.DummyService.DummyCommand;

/**
 */
public class TestConfigurationServices extends CoreUserBaseCase {

    /**
     * Sets up the fixture (run before every test).
     * Get an instance of Core, and delete the testnamespace from the config table.
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        setupSystemProperties();
        tidyUpTestData();
        setCore(new Core(this));
        getCore().resetConfiguration();
        setVersionEffectiveDate(new VersionEffectiveDate());
        resetConfiguration();
        setVersionEffectiveDate(new VersionEffectiveDate());
        ConfigurationHandler.resetCache();
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
     * Test the script fetching command (GetCommandScript). Some commands in the
     * system are defined internally as scripts held in strings - rules and highly
     * configurable logic are typically handled this way. This test checks that
     * the service responsible for fetching such scripts for edit or display is
     * working correctly.
     * <ol>
     * <li>Get and instance of the GetCommandScript command</li>
     * <li>Populate the command's arguments such that it will fetch the TestBeanShellService script</li>
     * <li>Invoke the command</li>
     * <li>Fail if a command object is not returned</li>
     * <li>Fail if the object's Type property isn't set to BeanShell</li>
     * <li>Fail if the object's Script property doesn't look like a script</li>
     * <li>Fail if the object's namespace property isn't set correctly (=TestNamespace)</li>
     * <li>Fail if the object's service name property isn't set to TertBeanShellService</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     * @throws Exception
     */
    @Test
    public void testScriptLoadService() throws Exception {
        GetCommandScriptCommand com=getCore().newCommand(GetCommandScriptCommand.class);
        com.setCommandNameArg("TestBeanShellService");
        com.setNamespaceArg(getConfigurationNamespace());
        com.invoke();
        assertNotNull(com.getCommandScriptRet());
        assertEquals("BeanShell", com.getCommandScriptRet().getType());
        assertTrue("script doesn't look right!", com.getCommandScriptRet().getScript().indexOf("version=new")!=-1);
        assertEquals("TestNamespace", com.getCommandScriptRet().getNamespace());
        assertEquals("TestBeanShellService", com.getCommandScriptRet().getCommandName());
    }

    /**
     * Test the script save command. {@link #testScriptLoadService Scriptable} commands
     * can be modified using the SetCommandScript command. This test ensures that this
     * service is working correctly.
     * <ol>
     * <li>Create an instance of a scriptable test command - one that adds two numbers (TestBeanShellService)</li>
     * <li>Populate the command with arguments (21 and 3)</li>
     * <li>Invoke the command</li>
     * <li>Fail if the result isn't 24</li>
     * <li>Create an instance of the SetCommandScript command</li>
     * <li>Populate the command with arguments to modify the TestBeanShellService script
     * such that it always returns the value <b>5</b></li>
     * <li>Invoke the setCommandScript command.</li>
     * <li>Clear the config cache. Note: this is not really necessary, but we're not testing caching here.</li>
     * <li>Set the version effective date to now - this should ensure that we use the new test
     * command rather than the old one.</li>
     * <li>Run through steps 1-3 again.</li>
     * <li>Fail if the result isn't 5.</li>
     * <li>Fail if any exceptions are thrown</li>
     * <ol>
     * @throws Exception
     */
    @Test
    public void testScriptSaveService() throws Exception {
        DummyCommand command=getCore().newCommand("TestBeanShellService", DummyCommand.class);
        command.setX(21);
        command.setY(3);
        command.invoke();
        assertEquals(24, command.getR());

        SetCommandScriptCommand com=getCore().newCommand(SetCommandScriptCommand.class);
        com.setCommandTypeArg("BeanShell");
        com.setCommandNameArg("TestBeanShellService");
        com.setNamespaceArg(getConfigurationNamespace());
        com.setCommandScriptArg(new CommandScript("args.setR(5);"));
        com.invoke();

        // reset the config, so we can be sure the change should get in
        ConfigurationHandler.resetCache();
        setVersionEffectiveDate(new VersionEffectiveDate());

        command=getCore().newCommand("TestBeanShellService", DummyCommand.class);
        command.setX(21);
        command.setY(3);
        command.invoke();
        assertEquals(5, command.getR());
    }

    /**
     * The core offers two services to package and deploy car files (Configuration Archives).
     * This text checks that the services can be instantiated - it doesn't attempt to invoke them.
     */
    @Test
    public void testCarServiceAvailability() throws Exception {
        getCore().newCommand(PackageCarCommand.class);
        getCore().newCommand(DeployCarCommand.class);
    }
    
    /**
     * The PackageCar service returns a configuration archive containing a set of
     * configurations specified. Configurations are specified by namespace. This test
     * checks that sending a empty or null collection of namespaces throws the appropriate exception.
     * <ul>
     * <li>Create an instance of the PackageCar command</li>
     * <li>Set the namespace collection arg to null</li>
     * <li>Invoke the service.</li>
     * <li>Fail unless a PreconditionException is thrown.</li>
     * <li>Set the namespace collection to an empty list.</li>
     * <li>Invoke the service again/</li>
     * <li>Fail unless a PreconditionException is thrown.</li>
     * </ul>
     */
    @Test
    public void testPackageCarServicePreconditions() throws Exception {
        ArrayList<String> namespaces=new ArrayList<String>();
        
        PackageCarCommand pcc=getCore().newCommand(PackageCarCommand.class);
        pcc.setNamespacesArg(null);
        try {
            pcc.invoke();
            fail("A PreconditionException should have been thrown.");
        }
        catch(PreconditionException e) {
            // good - this is what we want.
        }
        catch(Throwable e) {
            fail("Caught an "+e+" when a PreconditionException was expected");
        }

        pcc.setNamespacesArg(namespaces);
        try {
            pcc.invoke();
            fail("A PreconditionException should have been thrown.");
        }
        catch(PreconditionException e) {
            // good - this is what we want.
        }
        catch(Throwable e) {
            fail("Caught an "+e+" when a PreconditionException was expected");
        }

        namespaces.add("namspace that does not exist");
        pcc.setNamespacesArg(namespaces);
        try {
            pcc.invoke();
            fail("A PreconditionException should have been thrown.");
        }
        catch(PreconditionException e) {
            // good - this is what we want.
        }
        catch(Throwable e) {
            fail("Caught an "+e+" when a PreconditionException was expected");
        }
    }

    /**
     * The PackageCar service returns a configuration archive containing a set of
     * configurations specified. Configurations are specified by namespace. This test
     * checks that sending one or more valid namespaces does return a Car.
     * <ul>
     * <li>Create an instance of the PackageCar command</li>
     * <li>Specify the namespaces "com.ail.core.Core" and "com.ail.core.CoreProxy" as namespaces.</li>
     * <li>Invoke the service.</li>
     * <li>Fail if any excpetions are thrown</li>
     * <li>Fail if the Car file returned doesn't include the two configs specified</li>
     * <li>Create an instance of the DeployCar service</li>
     * <li>Pass the car archive returned from PackageCar into DeployCar</li>
     * <li>invoke the deploy car service</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Create an instance of the CatalogCar service</li>
     * <li>Pass the same car archive into the Catalog service</li>
     * <li>Invoke the service</li>
     * <li>Fail if the returned catalog doesn't include "com.ail.core.Core" and "com.ail.core.CoreProxy"</li>
     * <li>Fail if any exceptions are thrown</li> 
     * </ul>
     */
    @Test
    public void testPackageAndDeployCarHappyPath() throws Exception {
        ArrayList<String> namespaces=new ArrayList<String>();
        
        PackageCarCommand pcc=getCore().newCommand(PackageCarCommand.class);
        namespaces.add("com.ail.core.Core");
        namespaces.add("TestNamespace");
        pcc.setNamespacesArg(namespaces);
        pcc.invoke();
        assertNotNull(pcc.getCarRet());

        ZipInputStream zis=new ZipInputStream(new ByteArrayInputStream(pcc.getCarRet()));
        assertEquals("com.ail.core.Core", zis.getNextEntry().getName());
        assertEquals("TestNamespace", zis.getNextEntry().getName());
        zis.close();
        
        DeployCarCommand dcc=getCore().newCommand(DeployCarCommand.class);
        dcc.setCarArg(pcc.getCarRet());
        dcc.setNamespacesArg(pcc.getNamespacesArg());
        dcc.invoke();

        CatalogCarCommand ccc=getCore().newCommand(CatalogCarCommand.class);
        ccc.setCarArg(pcc.getCarRet());
        ccc.invoke();
        assertTrue(ccc.getNamespacesRet().contains("com.ail.core.Core"));
        assertTrue(ccc.getNamespacesRet().contains("TestNamespace"));
        assertEquals(2, ccc.getNamespacesRet().size());
    }

    /**
     * The DeployCar service takes a car file and deploys a specified list of the configurations it contains.
     * This test checks the service's preconditions.
     * <ul>
     * <li>Create an instance of the DeployCar command</li>
     * <li>Set the namespace collection arg to null</li>
     * <li>Set the car file arg to null</li>
     * <li>Invoke the service.</li>
     * <li>Fail unless a PreconditionException is thrown.</li>
     * <li>Set the namespace collection to an empty list (leave the car arg as null).</li>
     * <li>Invoke the service again/</li>
     * <li>Fail unless a PreconditionException is thrown.</li>
     * </ul>
     */
    @Test
    public void testDeployCarServicePreconditions() throws Exception {
        ArrayList<String> namespaces=new ArrayList<String>();
        
        DeployCarCommand dcc=getCore().newCommand(DeployCarCommand.class);
        dcc.setNamespacesArg(null);
        dcc.setCarArg(null);
        try {
            dcc.invoke();
            fail("A PreconditionException should have been thrown.");
        }
        catch(PreconditionException e) {
            // good - this is what we want.
        }
        catch(Throwable e) {
            fail("Caught an "+e+" when a PreconditionException was expected");
        }

        dcc.setNamespacesArg(namespaces);
        try {
            dcc.invoke();
            fail("A PreconditionException should have been thrown.");
        }
        catch(PreconditionException e) {
            // good - this is what we want.
        }
        catch(Throwable e) {
            fail("Caught an "+e+" when a PreconditionException was expected");
        }
    }
 }
