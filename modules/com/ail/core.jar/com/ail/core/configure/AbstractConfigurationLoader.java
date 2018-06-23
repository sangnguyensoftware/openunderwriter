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

package com.ail.core.configure;

import static com.ail.core.Functions.classForName;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import com.ail.core.VersionEffectiveDate;

/**
 * This class is extended by classes capable of loading and saving configurations
 * to/from some form of persistent store.<p>
 *
 * The handling of configurations is key to the operation of the Core system. To
 * be as flexible as possible, this abstract class leaves the details of how
 * configurations are loaded and stored to its concrete implementors. At
 * runtime the loadLoader method is responsible for creating an instance of a
 * concrete implementation of this class. That instance is then used by all core users
 * in that VM to load/save their configurations.<p>
 *
 * That actual concrete implementation can be specified in the following ways (in decending order
 * of preference):<ol>
 *
 * <li>
 * The system property <code>com.ail.core.configure.loader</code>. If defined the system will
 * create an instance of the class it names. This instance is then used as the loader for the VM.
 * On the command line you might specify this as follows:<br>
 * &nbsp;&nbsp;<code>-Dcom.ail.core.configure.loader=com.ail.core.configure.EJBConfigurationLoader</code><br>
 * Additional arguments may be passed to the loader using properties with the following naming
 * conversion:<br>
 * &nbsp;&nbsp;<code>com.ail.core.configure.loaderParam.&lt;paramName&gt</code>.<br>
 * For example, the following System properties will select the connection pool loader,
 * and pass it "java:/CodeDS" as the JNDI name for the pool to use:<br><code>
 * &nbsp;&nbsp;-Dcom.ail.core.configure.loader=com.ail.core.configure.ConnectionPoolConfigurationLoader</br>
 * &nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.jndiname=java:/CoreDS</code>
 * </li>
 *
 * <li>
 * The system property <code>com.ail.core.configure.loaderPropertiesFile</code>. If this
 * is found to be defined, the file it names is opened and the properties read from it. The
 * file is expected to be in the same form as defined below.
 * </li>
 *
 * <li>
 * Environment entries in JNDI. The entry <code>java:comp/env/ConfigurationLoader.classname</code>
 * <u>and</u> <code>java:comp/env/ConfigurationLoader.params</code> must be defined, and must
 * be Strings. The former is taken to be the full name of the class to instantiate and use as
 * the concrete loader. The latter is a semi-colon separated list of name=value parameters to
 * initialize the concrete loader.
 * </li>
 *
 * <li>
 * The class resource file <code>loader.properties</code>. This property file
 * must as a minimum define the property <code>loaderClass</code> with a value
 * identifying the fully qualified class name of the implementation. Any additional
 * properties defined in the file will be passed to the concrete implementation.
 * For example, the JDBCConfigurationLoader expects a <code>driver</code> property.
 * </li>
 * </ol>
 * Some (in fact most) of the concrete implementations require other parameters. See their
 * javadocs (from <b>See Also</b> below) for more details.
 * @see JDBCConfigurationLoader
 * @see EJBConfigurationLoader
 * @see ConnectionPoolConfigurationLoader
 * @see FileConfigurationLoader
 */
public abstract class AbstractConfigurationLoader {
    private static Properties params = new Properties();
    private static AbstractConfigurationLoader loader = null;

    /**
     * Try to get loader properties from a file defined by the <code>
     * com.ail.core.configure.loaderPorpertiesFile</code> property.
     * @return loaderClassName, or null if not found.
     * @throws IOException
     */
    private static String loadFromPropertiesFile() throws IOException {
        String loaderClassName = null;

        String propertiesFile = System.getProperty("com.ail.core.configure.loaderPropertiesFile");

        if (propertiesFile != null) {
            params.load(new FileInputStream(propertiesFile));
            loaderClassName = params.getProperty("loaderClass");
        }
        return loaderClassName;
    }

    /**
     * Try to get loader properties from System properties
     * @return loaderClassName, or null if not found.
     */
    private static String loadFromSystemProperties() {
        String loaderClassName = null;

        if (System.getProperty("com.ail.core.configure.loader") != null) {
            String propKey = null;

            loaderClassName = System.getProperty("com.ail.core.configure.loader");

            // loop through the properties looking for params
            for(Enumeration<?> en = System.getProperties().keys(); en.hasMoreElements();) {
                propKey = (String) en.nextElement();
                if (propKey.startsWith("com.ail.core.configure.loaderParam.")) {
                    params.put(propKey.substring(35), System.getProperty(propKey));
                }
            }
        }
        return loaderClassName;
    }

    /**
     * Try to load properties from the class resource.
     * @return loaderClassName, or null if not found.
     * @throws IOException
     */
    private static String loadFromClassResources() throws IOException {
        String loaderClassName;
        params.load(AbstractConfigurationLoader.class.getResourceAsStream("loader.properties"));
        loaderClassName = params.getProperty("loaderClass");
        return loaderClassName;
    }

    /**
     * Merge the default properties into the list already loaded. The loaded list
     * may well be partial - it doesn't always define all the properties that are
     * needed. For example, the connection pool loader normally gets its properties
     * from EJB environment entries. Defining all the loader properties on all EJBs
     * would be a pain (SQL to create the config table, the table name, etc). Defining
     * these settings in the loader.properties file simplifies things.
     * @throws IOException If the property file cannot be loaded.
     */
    private static void addDefaultProperties() throws IOException {
        String key=null;
        Properties defaultProp=new Properties();
        defaultProp.load(AbstractConfigurationLoader.class.getResourceAsStream("loader.properties"));

        for(Enumeration<?> en=defaultProp.keys() ; en.hasMoreElements() ; ) {
            key=(String)en.nextElement();
            if (params.getProperty(key)==null) {
                params.setProperty(key, defaultProp.getProperty(key));
            }
        }
    }

    /**
     * Create an instance of the loader to be used. The details of which loader, and
     * what parameters to pass to it are loaded from the loader.properties file.
     * @return The loader instance to use in this JVM.
     */
    public static synchronized AbstractConfigurationLoader loadLoader() {
        if (loader == null) {
            try {
                String loaderClassName = null;

                // Try a bunch of different loadFrom method to get the loader properties
                loaderClassName = loadFromSystemProperties();

                if (loaderClassName == null) {
                    loaderClassName = loadFromPropertiesFile();
                }

                if (loaderClassName == null) {
                    loaderClassName = loadFromClassResources();
                }

                // get hold of the loader class itself
                Class<?> loaderClass = classForName(loaderClassName);

                addDefaultProperties();

                // instantiate the loader
                loader = (AbstractConfigurationLoader) loaderClass.newInstance();
            } catch(IOException e) {
                throw new BootstrapConfigurationError("Failed to open bootstrap properties (loader.properties):" + e);
            } catch(ClassNotFoundException e) {
                throw new BootstrapConfigurationError("The specified bootstrap property loader class (" + params.getProperty("loaderClass") + ") could not be found.");
            } catch(InstantiationException e) {
                throw new BootstrapConfigurationError("Failed to instantiate bootstrap property loader:" + e);
            } catch(IllegalAccessException e) {
                throw new BootstrapConfigurationError("Failed to instantiate bootstrap property loader:" + e);
            }
        }

        return loader;
    }

    /**
     * Return the configuration parameters associated with the loader.
     * @return The properties from the loader.properties file.
     */
    public Properties getLoaderParams() {
        return params;
    }

    /**
     * Fetch the configuration associated with a given namespace, and return them.
     * @param namespace The namespace to load for.
     * @param date The effective date to load the configuration for.
     * @return loaded configuration.
     */
    public abstract Configuration loadConfiguration(String namespace, VersionEffectiveDate date);

    /**
     * Save (and update) the configuration associated with the specified namespace.
     * @param namespace The namespace to save to.
     * @param config The configuration to save.
     * @throws ConfigurationUpdateCollisionError If the configuration being written is stale (i.e. has be updated in persistent store since it was read).
     */
    public abstract void saveConfiguration(String namespace, Configuration config) throws ConfigurationUpdateCollisionError;

    /**
     * Build and return a list of the namespaces currently being used. More specifically
     * this includes only those that are stored and are current.
     * @return A collection of namespace names as Strings.
     */
    public abstract Collection<String> getNamespaces();

    /**
     * Build and return a list of the details of the namespaces currently being used.
     * More specifically this includes only those that are stored and are current.
     * @return A collection of instances of {@link ConfigurationSummary ConfigurationSummary}..
     */
    public abstract Collection<ConfigurationSummary> getNamespacesSummary();

    /**
     * Build and return a list of the details of a namespaces versions.
     * @param namespace The namespace to fetch the history for.
     * @return A collection of instances of {@link ConfigurationSummary ConfigurationSummary}..
     */
    public abstract Collection<ConfigurationSummary> getNamespacesHistorySummary(String namespace);

    /**
     * When the configuration handler is asked to "reset", it passes that request onto
     * the loader currently in user. The loader should reset any internal state in this
     * method. "state" here means memory state only - the persisted configurations will
     * not be altered.
     */
    public abstract void reset();

    /**
     * Delete ALL configuration information. This will include all historical configuration
     * information. <p>
     * <b>NOTE: ALL CONFIGURATION INFORMATION WILL BE LOST!</b>
     */
    public abstract void purgeAllConfigurations();

    /**
     * Delete the repository holding configuration information. This not only removes all
     * configuration information {@link #purgeAllConfigurations} but also removes the
     * repository itself.<p>
     * <b>NOTE: ALL CONFIGURATION INFORMATION WILL BE LOST!</b>
     */
    public abstract void deleteConfigurationRepository();

    /**
     * Return true if the repository used by the configured loader has been created, false
     * otherwise. Implementations may use the value returned by this method to determine
     * whether they need to create a repository. This is likely to happen only once during
     * the lifetime of a system.
     * @return true if the repository has been created, false otherwise.
     */
    public abstract boolean isConfigurationRepositoryCreated();
}
