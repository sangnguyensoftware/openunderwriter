/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.ail.core.configure.AbstractConfigurationLoader;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.ConfigurationOwner;
import com.ail.core.configure.ConfigurationResetError;

/**
 * This class should be extended by any test that needs to act like a core user.
 * I implements the CoreUser interface on behalf of test itself - making the
 * test class cleaner. It also handles common tasks like cleaning up test data
 * from the database when tests close.
 */
public class CoreUserBaseCase implements CoreUser, ConfigurationOwner {
    public static String TEST_NAMESPACE = "TestNamespace";
    private Core core = null;
    private CoreUserImpl coreUser = null;

    public CoreUserBaseCase() {
        Authenticator.setDefault(new LocalAuthenticator());
        coreUser = new CoreUserImpl(CoreUserImpl.SELECT_CONSISTENT_CONFIGURATIONS, TEST_NAMESPACE, null);
    }

    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return coreUser.getVersionEffectiveDate();
    }

    public void setVersionEffectiveDate(VersionEffectiveDate versionEffectiveDate) {
        coreUser.setVersionEffectiveDate(versionEffectiveDate);
    }

    /**
     * Tidy up (delete) the config database records, this means deleting the
     * config records for the core namespace!!
     *
     * @throws SQLException
     * @throws Exception
     */
    protected void tidyUpTestData() throws Exception {
        new ConfigurationTidyService().clearTestNamespace();
    }

    public Core getCore() {
        return core;
    }

    public void setCore(Core core) {
        this.core = core;
    }

    /**
     * Method demanded by the ConfigurationOwner interface.
     *
     * @param config
     *            Configuration to use from now on.
     */
    @Override
    public void setConfiguration(Configuration config) {
        core.setConfiguration(config);
    }

    /**
     * Method demanded by the ConfigurationOwner interface.
     *
     * @return The current configuration (at versionEffectiveDate).
     */
    @Override
    public Configuration getConfiguration() {
        return core.getConfiguration();
    }

    /**
     * Method demanded by the ConfigurationOwner interface.
     *
     * @return The configuration namespace we're using
     */
    @Override
    public String getConfigurationNamespace() {
        return TEST_NAMESPACE;
    }

    /**
     * Reset all of the system and local class configurations to the factory
     * defaults and clear the configuration handler's cache so that we can be
     * sure configuration system is in a known state.
     */
    protected void setupConfigurations() {
        new CoreProxy().resetConfigurations();
        resetConfiguration();
        ConfigurationHandler.resetCache();
    }

    protected void setupTestProducts() {
        getCore().resetProduct("com.ail.core.product.TestProduct01");
        getCore().resetProduct("com.ail.core.product.TestProduct02");
        getCore().resetProduct("com.ail.core.product.TestProduct03");
        getCore().resetProduct("com.ail.core.product.TestProduct04");
        getCore().resetProduct("com.ail.core.product.TestProduct05");
        getCore().resetProduct("com.ail.core.product.TestProduct06");
        getCore().resetProduct("com.ail.core.product.TestProduct07");
        getCore().resetProduct("com.ail.core.product.TestProduct08");
        getCore().resetProduct("com.ail.core.product.TestProduct09");
        getCore().resetProduct("com.ail.core.product.TestProduct10");
        getCore().resetProduct("com.ail.core.product.TestProduct11");
        getCore().resetProduct("com.ail.core.product.TestProduct12");
        ConfigurationHandler.resetCache();
    }

    /**
     * Reset the test class' configuration.
     */
    @Override
    public void resetConfiguration() {
        if (getCore() == null) {
            return;
        }

        // Derive the configuration's name
        String cname = this.getClass().getName();
        String name = cname.substring(cname.lastIndexOf('.') + 1);

        try {
            // load the <name>DefaultConfig resource into an XMLString
            URL inputUrl = this.getClass().getResource(name + "DefaultConfig.xml");
            inputUrl.openStream();

            XMLString factoryConfigXML = new XMLString(inputUrl);

            // marshal the config XML into an instance of Configuration
            Configuration factoryConfig = getCore().fromXML(Configuration.class, factoryConfigXML);

            // write details of where we loaded the config from back into the
            // config so that
            // anyone who uses this configuration in future can see where it
            // came from.
            factoryConfig.setSource(inputUrl.toExternalForm());

            // reset the configuration
            setConfiguration(factoryConfig);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationResetError("Failed to reset " + name + " configuration: " + e);
        }
    }

    /**
     * Setup the standard system properties that most test will need.
     */
    public void setupSystemProperties() {
        System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        System.setProperty("java.naming.provider.url", "jnp://localhost:1099");
        System.setProperty("java.naming.factory.url.pkgs", "org.jboss.ejb.client.naming");
        System.setProperty("org.xml.sax.parser", "org.apache.xerces.parsers.SAXParser");
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        System.setProperty("java.protocol.handler.pkgs", "com.ail.core.urlhandler");
        System.setProperty("jboss.naming.client.ejb.context", "true");
    }

    /**
     * Get the security principal associated with this instance.
     *
     * @return The associated security principal - if defined, null otherwise.
     */
    @Override
    public Principal getSecurityPrincipal() {
        return coreUser.getSecurityPrincipal();
    }

    public void executeSQL(String sql) throws Exception {
        AbstractConfigurationLoader loader = AbstractConfigurationLoader.loadLoader();
        Properties properties = loader.getLoaderParams();

        Class.forName(properties.getProperty("driver"));
        Connection con = DriverManager.getConnection(properties.getProperty("url") + properties.getProperty("databaseName"), properties);
        Statement st = con.createStatement();
        st.execute(sql);
        st.close();
        con.close();
    }
}

/**
 * Authenticator to use during test runs.
 */
class LocalAuthenticator extends Authenticator {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("admin", "admin".toCharArray());
    }
}
