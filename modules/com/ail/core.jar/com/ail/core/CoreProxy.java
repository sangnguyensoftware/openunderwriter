/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

import static com.ail.core.Functions.classForName;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.hibernate.Criteria;

import com.ail.annotation.Configurable;
import com.ail.core.command.Command;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.ConfigurationOwner;
import com.ail.core.configure.ConfigurationResetError;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.factory.AbstractFactory;

/**
 * This class acts as a proxy for those who need to access some of the Core's
 * services, but for whatever reason cannot (or do not want to) implement
 * CoreUser themselves.<p>
 * The versionEffectiveDate of the proxy is taken to be the date at which it was
 * instantiated.
 */
@Configurable
public class CoreProxy implements CoreUser, ConfigurationOwner {
    public static final String DefaultNamespace="com.ail.core.CoreProxy";
    private Core core=null;
    private VersionEffectiveDate versionEffectiveDate=null;
    private Principal securityPrincipal=null;
    private String namespace=null;

    /**
     * Default constructor. This creates a core proxy with the default configuration namespace ("com.ail.core.CoreProxy"),
     * a default version effective date of Date.now() and a security principal set to null.
     */
    public CoreProxy() {
        this(DefaultNamespace, new VersionEffectiveDate(), null);
    }

    /**
     * Construct a proxy to work against a specific configuration namespace. The proxy's version effective date will be
     * set to now (Date.now()), the security principal will default to null.
     * @param namespace Configuration namespace.
     */
    public CoreProxy(String namespace) {
        this(namespace, new VersionEffectiveDate(), null);
    }

    /**
     * Construct a proxy for a CoreUser, and inherit all settings (namespace, version effective date, security
     * principal etc) from an instance of CoreUser.
     * @param coreuser Provides settings.
     */
    public CoreProxy(CoreUser coreuser) {
        this(coreuser.getConfigurationNamespace(), coreuser.getVersionEffectiveDate(), coreuser.getSecurityPrincipal());
    }

    /**
     * Construct a proxy for a specific namespace, and inherit all other settings (version effective date, security
     * principal etc) from an instance of CoreUser.
     * @param namespace Configuration namespace to be used.
     * @param coreuser Provides other settings.
     */
    public CoreProxy(String namespace, CoreUser coreuser) {
        this(namespace, coreuser.getVersionEffectiveDate(), coreuser.getSecurityPrincipal());
    }

    /**
     * Create a core proxy with a specific configuration namespace and version effective date.
     * @param namespace configuration namespace to be used.
     * @param ved version effective date.
     * @param securityPrincipal Security principal to apply.
     */
    public CoreProxy(String namespace, VersionEffectiveDate ved, Principal securityPrincipal) {
        core=new Core(this);
        this.versionEffectiveDate=ved;
        this.namespace=namespace;
        this.securityPrincipal=securityPrincipal;
    }

    /**
     * Return the version effective date associated with this proxy.
     * @return The version effective date.
     */
	@Override
    public VersionEffectiveDate getVersionEffectiveDate() {
		return versionEffectiveDate;
    }

    /**
     * Set the version effective date for the proxy to a specific date.
     * @param ved New date to run as.
     */
    public void setVersionEffectiveDate(VersionEffectiveDate ved) {
        versionEffectiveDate=ved;
    }

    /**
     * Set the version effective date to the date now.
     */
    public void setVersionEffectiveDateToNow() {
        versionEffectiveDate=new VersionEffectiveDate();
    }

    /**
     * Get the security principal associated with this instance.
     * @return The associated security principal - if defined, null otherwise.
     */
    @Override
    public Principal getSecurityPrincipal() {
        return securityPrincipal;
    }

    /**
     * Set the security principal using the proxy
     * @param securityPrincipal
     */
    public void setSecurityPrincipal(Principal securityPrincipal) {
        this.securityPrincipal=securityPrincipal;
    }

    /**
     * Create a new instance of the command specified. The details of the type
     * to be created are loaded from the callers configuration. This method is
     * distinct from {@link #newCommand(Class, String)}. This method simple looks
     * for a configured command with the name <i>commandName</i>, whereas method
     * looks for a command named after the fully qualified name of the Class, and
     * modified by the value of String (e.g. <i>"com.ail.core.LoggerCommand/stdout"</i>)
     * @param commandName The name of the command to create an instance of
     * @param clazz The expected type of the resulting command
     * @return An instance of the command.
     */
    public <T extends Command> T newCommand(String commandName, Class<T> clazz) {
        return core.newCommand(commandName, clazz);
    }

    /**
     * Create a new instance of the command specified with a modifier. The details of the type
     * to be created are loaded from the callers configuration.
     * @param commandName The name of the command to create an instance of
     * @param modifier A modifier to apply to the command name.
     * @param clazz The expected type of the resulting command
     * @return An instance of the command.
     */
    public <T extends Command> T newCommand(String commandName, String modifier, Class<T> clazz) {
        return core.newCommand(commandName, modifier, clazz);
    }

    /**
     * Create a new instance of the command specified. The details of the type
     * to be created are loaded from the callers configuration.
     * @param clazz The class of the type to be created.
     * @return An instance of the command.
     */
    public <T extends Command> T newCommand(Class<T> clazz) {
        return core.newCommand(clazz);
    }

    /**
     * Create a new instance of the command specified. The details of the type
     * to be created are loaded from the callers configuration based on the
     * clazz's name and the additional modifier. This method is distinct from
     * {@link #newCommand(String, Class)}. This method looks in configuration
     * for a command named after the fully qualified name of the Class, and
     * modified by the value of String (e.g. <i>"com.ail.core.LoggerCommand/stdout"</i>);
     * whereas, that method simple looks for a configured command with the
     * name <i>commandName</i>
     * @param clazz The class of the type to be created.
     * @param modifier select the specific configuration required.
     * @return An instance of the command.
     */
    public <T extends Command> T newCommand(Class<T> clazz, String modifier) {
        return core.newCommand(clazz, modifier);
    }

	/**
     * Create a instance of the named type object.
	 * The named type is looked up in the current configuration, and
     * created from the specification held.
     * @param typeName The name of the type to create.
     * @return The type object ready for use.
     */
    public Object newType(String typeName) {
		return core.newType(typeName);
    }

    /**
     * Create a new instance of the named type. The typeName argument
     * relates to a type in the callers configuration which defines the
     * specifics of the type to be created.
     * the name of the type required.
     * @param typeName The name use to load the type's details.
     * @param clazz The expected type of the resulting command
     * @return An instance of a type.
     */
    public <T extends Object> T newType(String typeName, Class<T> clazz) {
        return core.newType(typeName, clazz);
    }

    /**
     * Create a new instance of the named type with a modifier. The typeName argument
     * relates to a type in the callers configuration which defines the specifics of
     * the type to be created. The modifier is applied to the typeName to arrive at
     * the name of the type required.
     * @param typeName The name use to load the type's details.
     * @param modifer to be applied
     * @param clazz The expected type of the resulting command
     * @return An instance of a type.
     */
    public <T extends Object> T newType(String typeName, String modifier, Class<T> clazz) {
        return core.newType(typeName, modifier, clazz);
    }

    /**
     * Create a new instance of the specified type. The clazz argument
     * relates to a type in the callers configuration which defines the
     * specifics of the type to be created.
     * @param clazz The class to return an instance for.
     * @return An instance of a type.
     */
    public <T extends Object> T newType(Class<T> clazz) {
        return core.newType(clazz);
    }

    /**
     * Create a new instance of the specified type. The clazz and modifier arguments
     * relate to a type in the callers configuration which defines the
     * specifics of the type to be created.
     * @param clazz The class to return an instance for.
     * @return An instance of a type.
     */
    public <T extends Object> T newType(Class<T> clazz, String modifier) {
        return core.newType(clazz, modifier);
    }

	/**
     * Output a message to the Debug logging channel.
     * Messages written to this channel are of interest to developers, and to
     * anyone trying to debug a system problem. The channel would generally
     * only be turned on when a problem is being investigated.
     * @param message The text of the message to be output.
     */
    public void logDebug(String message, Throwable cause) {
		core.logDebug(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void logDebug(String message) {
        core.logDebug(message);
    }

    /**
     * Output a message to the Info logging channel.
	 * This channel is designed to take messages that are of interest during
     * normal operations. For example, "System ready", "Configuration reloaded".
     * @param message The text of the message to be output.
     */
    public void logInfo(String message, Throwable cause) {
		core.logInfo(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void logInfo(String message) {
        core.logInfo(message);
    }

    /**
     * Output a message to the Warning logging channel.
	 * Messages written to this channel indicate that something unexpected
     * occurred, but that it was dealt with and is not thought (by the developer)
     * to be if great importance.
     * @param message The text of the message to be output.
     */
    public void logWarning(String message, Throwable cause) {
		core.logWarning(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void logWarning(String message) {
        core.logWarning(message);
    }

    /**
     * Output a message to the Error logging channel.
	 * The error channel is reserved for messages that describe serious
     * system problems. The problem didn't stop processing, but is significant
     * enough to require investigation.
     * @param message The text of the message to be output.
     */
    public void logError(String message, Throwable cause) {
        core.logError(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void logError(String message) {
        core.logError(message);
    }

	/**
     * Output a message to the Fatal logging channel.
	 * An error is fatal if it stops the operation being processed. For example,
     * if the systems configuration information is defined in an inconsistent way
     * a fatal error is generated.
     * @param message The text of the message to be output.
     */
    public void logFatal(String message, Throwable cause) {
        core.logFatal(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void logFatal(String message) {
        core.logFatal(message);
    }

	/**
     * Transform XML into an object. The XMLString is taken to be an xml
     * representation of an instance of the specified class. This method
     * translates the XML into an instance of the class and returns that
     * instance.
     * @param clazz The Class represented by <code>xml</code>
     * @param xml The XML representing and instance of <code>clazz</code>
     * @return an instance of <code>clazz</code> built up from <code>xml</code>
     */
    public <T extends Object> T fromXML(Class<T> clazz, XMLString xml) throws XMLException {
        return core.fromXML(clazz, xml);
    }

	/**
     * Transform an object into its XML representation.
     * @param obj The object to be transformed.
     * @return An XMLString representing <code>obj</code>
     */
    public XMLString toXML(Object obj) {
		return core.toXML(obj);
    }

    public void resetConfigurations() {
        resetCoreConfiguration();
        ConfigurationHandler.resetCache();
        setVersionEffectiveDateToNow();

        // named
        for (Parameter p : getGroup("NamespacesToResetOnResetAll").getParameter()) {
            try {
                resetConfiguration(p.getName());
            } catch (Throwable t) {
                System.err.println("Error resetting configuration for namespace: " + p.getName());
                t.printStackTrace(System.err);
            }
        }

        ConfigurationHandler.resetCache();
    }

    /**
     * Reset the configuration for a named class. This utility method can be used to reset
     * a named configuration owner to its factory default settings.
     * @param configOwnerClassName The fully qualified name of the class to reset the config for.
     */
    public boolean resetConfiguration(String configOwnerClassName) {
		try {
			// get hold of the config owners class
            Class<?> configOwner=classForName(configOwnerClassName);

			// Create an instance of the owner class
			ConfigurationOwner co=(ConfigurationOwner)configOwner.newInstance();

			// Find the reset method
			Method reset=configOwner.getMethod("resetConfiguration");

			// invoke the reset method
			reset.invoke(co);

			return true;
		}
        catch(ClassNotFoundException e) {
			logError("Failed to reset "+configOwnerClassName+" class not found");
        }
		catch(InstantiationException e) {
            logError("Failed to reset "+configOwnerClassName+" could not instantiate class");
        }
		catch(NoSuchMethodException e) {
			logError("Failed to reset "+configOwnerClassName+" no such method");
        }
		catch(IllegalAccessException e) {
			logError("Failed to reset "+configOwnerClassName+" illegal access");
        }
		catch(InvocationTargetException te) {
			logError("Failed to reset "+configOwnerClassName+":"+te.getTargetException());
        }

		return false;
    }

    /**
     * Update the persisted copy of the CoreProxy's configuration.
     * @param config New configuration.
     */
    @Override
    public void setConfiguration(Configuration config) {
        core.setConfiguration(config);
    }

    /**
     * Get a copy of the CoreProxy's current configuration.
     * @return The current configuration.
     */
    @Override
    public Configuration getConfiguration() {
        return core.getConfiguration();
    }

    /**
     * Get the CoreProxy's namespace.
     */
    @Override
    public String getConfigurationNamespace() {
        return namespace;
    }

	/**
	 * Reset the core's configuration to its factory default settings.
	 */
	public void resetCoreConfiguration() {
		core.resetConfiguration();
	}

    /**
     * Reset the CoreProxy's configuration to its factory default settings.
     */
    @Override
    public void resetConfiguration() {
        try {
            // load the <name>DefaultConfig resource into an XMLString
	        InputStream in=this.getClass().getResourceAsStream("CoreProxyDefaultConfig.xml");
	        XMLString factoryConfigXML=new XMLString(in);

            // marshal the config XML into an instance of Configuration
	        Configuration factoryConfig=(Configuration)core.fromXML(Configuration.class, factoryConfigXML);

            // reset the configuration
	        setConfiguration(factoryConfig);
	    }
        catch(Exception e) {
	        throw new ConfigurationResetError("Failed to reset CoreProxy configuration: ", e);
        }
    }

    /**
     * Instantiate the default type associated with a product. Each product must define a default
     * type. This is the type (generally) instantiated at the beginning of the lifecycle.
     * @param productName The name of the product to instantiate for.
     * @return The instantiated type.
     * @since 2.0
     * @deprecated Use {@link #newProductType(String, Class)}
     */
    @Deprecated
    public Type newProductType(String productName) {
        return core.newProductType(productName);
    }

    /**
     * Instantiate the default type associated with a product. Each product must define a default
     * type. This is the type (generally) instantiated at the beginning of the lifecycle.
     * @param productName The name of the product to instantiate for.
     * @param clazz Class of the object to be returned.
     * @return The instantiated type.
     * @since 3.0
     */
    public  <T extends Type> T newProductType(String productName, Class<T> clazz ) {
        return core.newProductType(productName, clazz);
    }

    /**
     * Instantiate a type associated with a product. A product must define at least one type
     * (see {@link #newProductType(String)}), but may define any number of additional types for
     * use during its lifecycle; this method is used to instantiate specific types by name.<p>
     * For example, a complex insurance product may define several different types to describe
     * the assets the product covers. A commercial combined product might define a stock asset, a
     * vehicle asset, a safe asset, etc. Each of these is described within the product as a separate
     * named type. A client would use this method to instantiate these different types as and when
     * they needed to be added to an instance of a commercial combined policy.
     * @param productName The product "owning" the type.
     * @param typeName The name of type to be instantiated.
     * @return The instantiated type.
     * @since 2.0
     * @deprecated Use {@link #newProductType(String, String, Class)} instead.
     */
    @Deprecated
    public Type newProductType(String productName, String typeName) {
        return core.newProductType(productName, typeName);
    }

    /**
     * Instantiate a type associated with a product. A product must define at least one type
     * (see {@link #newProductType(String)}), but may define any number of additional types for
     * use during its lifecycle; this method is used to instantiate specific types by name.<p>
     * For example, a complex insurance product may define several different types to describe
     * the assets the product covers. A commercial combined product might define a stock asset, a
     * vehicle asset, a safe asset, etc. Each of these is described within the product as a separate
     * named type. A client would use this method to instantiate these different types as and when
     * they needed to be added to an instance of a commercial combined policy.
     * @param productName The product "owning" the type.
     * @param typeName The name of type to be instantiated.
     * @param clazz Class of the object to be returned.
     * @return The instantiated type.
     * @since 3.0
     */
    public <T extends Type> T newProductType(String productName, String typeName, Class<T> clazz) {
        return core.newProductType(productName, typeName, clazz);
    }

    /**
     * Find and return a Builder (Factory) by its name. The builders available are defined
     * in the configure system and normally identified in the source tree by their use of the
     * Builder annotation.
     */
    public AbstractFactory fetchBuilder(String builderName) {
        return core.fetchBuilder(builderName);
    }

    /**
     * Flush the current session to the database
     * @Since 3.0
     */
    public void flush() {
        core.flush();
    }

    /**
     * Detach an object from persistence.
     * @Since 3.0
     */
    public <T extends Type> T detach(T object) {
        return core.detach(object);
    }

    /**
     * Create a persistent copy of an object.
     * @since 2.0
     * @param The object to be persisted.
     * @return The object as it was persisted
     */
    public <T extends Type> T create(T type) {
        return core.create(type);
    }

    /**
     * Update the persistent copy of an object from its in memory copy.
     * @since 2.0
     * @param object The object to be written to persistent storage.
     * @return The object as persisted.
     */
    public <T extends Type> T update(T type) {
        return core.update(type);
    }


    /**
     * Delete one or more objects from persistent storage.
     * @param object Object to be deleted
     * @since 2.0
     * @throws VersionException If the version object is badly defined, or relates to a version that does not exist.
     * @return The number of objects deleted.
     */
    public void delete(Type type) {
        core.delete(type);
    }

    /**
     * Delete objects from persistent storage based on a query.
     * @param object Object to be deleted
     * @since 2.0
     * @throws VersionException If the version object is badly defined, or relates to a version that does not exist.
     * @return The number of objects deleted.
     */
    public void delete(String queryName, Object... queryArgs) {
        core.delete(queryName, queryArgs);
    }
    /**
     * Load a specific instance of a type by ID. This has the same effect as a named query
     * with the following query: from &lt;type&gt; where systemId=&lt;systemId&gt;.
     * @since 2.0
     * @param type The type to be loaded
     * @param systemId the systemId of the instance to load.
     * @return The loaded type
     */
    public <T extends Type> T load(Class<T> type, long systemId) {
        return core.load(type, systemId);
    }

    /**
     * Create a criteria search instance for the specified class
     * @since 3.0
     * @param clazz The class to create a criteria for.
     * @return Instance of Criteria for clazz.
     */
    public Criteria criteria(Class<?> clazz) {
        return core.criteria(clazz);
    }

    /**
     * Create a criteria search instance for the specified class
     * @since 3.0
     * @param clazz The class to create a criteria for.
     * @param alias Alias for criteria class
     * @return Instance of Criteria for clazz.
     */
    public Criteria criteria(Class<?> clazz, String alias) {
        return core.criteria(clazz, alias);
    }

    /**
     * Query persistent storage for the collection of objects returned by a
     * query. The query itself is referenced by name only. This name is
     * interpreted by the underlying persistence engine and resolved to an
     * actual query.
     * @since 2.0
     * @param queryName The name of the query to be executed.
     * @param queryArgs Arguments to be used by the query.
     * @return The results of the query.
     * @throws VersionException The version argument is either badly defined, or relates to a version that does not exist.
     */
    public List<?> query(String queryName, Object... queryArgs) {
        return core.query(queryName, queryArgs);
    }

    /**
     * Query persistent storage for the collection of objects returned by a
     * query. The query is in the form of an HQL query.
     * @since 2.0
     * @param queryString The 'where' part of the query to be executed.
     * @param type type to be queried.
     * @return The results of the query.
     * @throws VersionException The version argument is either badly defined, or relates to a version that does not exist.
     */
    public <T> List<T> query(String queryString, Class<T> type) {
        return core.query(queryString, type);
    }

    /**
     * Query persistent storage for the single object returned by a
     * query. The query itself is referenced by name only. This name is
     * interpreted by the underlying persistence engine and resolved to an
     * actual query.
     * @since 2.0
     * @param queryName The name of the query to be executed.
     * @param queryArgs Arguments to be used by the query.
     * @return The results of the query.
     */
    public Object queryUnique(String queryName, Object... queryArgs) {
        return core.queryUnique(queryName, queryArgs);
    }

    /**
     * Initiate a persistence session. All persistence related methods need to operate within
     * the context of a session. This call generally corresponds to the start of a transaction.
     * The session is associated with the calling thread, so any persistent methods made within
     * the same thread before {@see #closePersistenceSession()} are performed within
     * one transaction.
     */
    public void openPersistenceSession() {
        core.openPersistenceSession();
    }

    /**
     * Close the open session associated with the current thread and commit.
     * @see #openPersistenceSession()
     */
    public void closePersistenceSession() {
        core.closePersistenceSession();
    }

    /**
     * Fetch the named group the from current configuration.
     * The "current configuration" is defined by the configuration namespace
     * and the version effective date. The namespace is taken either from the
     * core user if they implement ConfigurationOwner, or from the core itself.
     * The versionEffectiveDate comes from the core user.<p>
     * The group's name may be dot separated indicating
     * that the group is nested within other groups.
     * @param name The name of the group to be returned.
     * @return The configuration group, or null if one is not defined for this namespace and version effective date.
     * @param name
     * @since 2.0
     */
    public Group getGroup(String name) {
        return core.getGroup(name);
    }

    /**
     * Return the source of the configuration being used by this instance of core. As configurations optionally
     * have "parent" configuration that they inherit from, this method returns a collection of sources with
     * one element for each configuration in the hierarchy.
     * @return The sources from which the configuration was loaded.
     */
    public Collection<String> getConfigurationSource() {
        return core.getConfigurationSource();
    }

    /**
     * Return a list of the namespace(s) of the parent(s) of this configuration.
     * @return List of namespaces which are parents to the current namespace.
     */
    public Collection<String> getConfigurationNamespaceParent() {
        return core.getConfigurationNamespaceParent();
    }

    /**
     * Fetch the named parameter from current configuration.
     * The "current configuration" is defined by the configuration namespace
     * and the version effective date. The namespace is taken either from the
     * core user if they implement ConfigurationOwner, or from the core itself.
     * The versionEffectiveDate comes from the core user.<p>
     * The parameter's name may be dot separated indicating
     * that the parameter is nested within one of more groups.
     * @param name The name of the parameter to be returned.
     * @return The parameter, or null if one is not defined for this namespace and version effective date.
     * @since 2.0
     */
    public Parameter getParameter(String name) {
        return core.getParameter(name);
    }

    /**
     * Fetch the value of the named parameter from current configuration.
     * The "current configuration" is defined by the configuration namespace
     * and the version effective date. Note: The following two pieces of
     * code are exactly equivalent:<p><ol>
     * <li><code>String value=null;<br>
     * Parameter p=core.getParameter("paramName");<br>
     * if (p!=null) {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;value=p.getValue();<br>
     * }</code></li>
     * <li><code>String value=core.getParameterValue("paramName");</code></li></ul>
     * </p>
     * @param name The name of the parameter to return a value for.
     * @return The parameter's value, or null if it is not defined for this namespace and version effective date.
     * @since 2.0
     */
    public Properties getParametersAsProperties(String name) {
        return core.getParametersAsProperties(name);
    }

    /**
     * Fetch all the Parameters in a group and return them as a java.util.Properties.
     * This is a utility method returns the parameters in a named group
     * and creates a Properties object with them. Each Parameter's name and value
     * attributes are mapped into a property in the Properties object.<p>
     * If the group specified does not exist, null is returned. If the group does
     * exist but contains no Parameters, an empty Properties object is returned.
     * @param name The name of the group whose parameters will be used.
     * @return The parameters as properties, or null.
     * @since 2.0
     */
    public String getParameterValue(String name) {
        return core.getParameterValue(name);
    }

    /**
     * Return the value of a parameter or a default if it is null.
     * @see #getParameterValue
     * @param name The name of the parameter
     * @param defaultValue The default value
     * @return The parameter's value, or the value of default if it is undefined.
     * @since 2.0
     */
    public String getParameterValue(String name, String defaultValue) {
        return core.getParameterValue(name, defaultValue);
    }

    /**
     * Generate a document and return it as a byte[]. The product named "<i>productName</i>" is assumed to include a
     * document definition by the name "<i>documentDefinition</i>". Document definitions define all of the information
     * that the generator needs in order to render a document except for dynamic data - this is provided my <i>model</i>.
     * <p/>
     * The type of document returned (pdf, rtf, word, etc) is determined by the document definition.
     * <p/>
     * This method should be assumed to work entirely in memory - i.e. it does not persist the document at all.
     * <p/>
     * A loose contract exist between the document definition and the <i>model</i>. A definition is written with the
     * expectation that it can take whatever dynamic data it needs from the model it is given. Generation will fail
     * if the model passed in does not match the definition's expectations.
     * @param productName The name of product which 'owns' the document definition.
     * @param documentDefinitionName the name of the definition to use.
     * @param model The dynamic data satisfying references in the document definition.
     * @return The rendered document.
     */
    public byte[] generateDocument(String productName, String documentDefinitionName, Type model) {
        return core.generateDocument(productName, documentDefinitionName, model);
    }

    /**
     * Get the instance of {@link Core} being used by this proxy.
     * @return This proxy's core
     */
    public Core getCore() {
        return core;
    }

    /**
     * Reset a specific product. Perform a system reset on the named product returning it
     * to it's factory state.
     * @param productName The name of the product to reset
     * @since 2.0
     */
    public void resetProduct(String productName) {
        core.resetProduct(productName);
    }

    /**
     * Clear the configure cache associated with a product.
     * @param productName Product to clear the cache for.
     * @return list of the namespaces for which the cache was cleared
     */
    public List<String> clearProductCache(String productName) {
        return core.clearProductCache(productName);
    }

    /**
     * Reset the server side cache used to hold configuration information.
     */
    public void clearConfigurationCache() {
        core.clearConfigurationCache();
    }

    /**
     * Reset the server side cache used to hold configuration information.
     * @param namespace The namespace to be cleared from the cache.
     * @return list of the namespaces for which the cache was cleared
     */
    public List<String> clearConfigurationCache(String namespace) {
        return core.clearConfigurationCache(namespace);
    }

    /**
     * Transform JSON into an object. The string is taken to be an JSON
     * representation of an instance of the specified class. This method
     * translates the JSON into an instance of the class and returns that
     * instance.
     * @param clazz The Class represented by <code>json</code>
     * @param xml The JSON representing and instance of <code>clazz</code>
     * @return an instance of <code>clazz</code> built up from <code>json</code>
     */
    public <T> T fromJSON(Class<T> clazz, String json) throws JSONException {
        return core.fromJSON(clazz, json);
    }

    /**
     * Transform an object into its JSON representation.
     * @param obj The object to be transformed.
     * @return An JSON string representing <code>obj</code>
     */
    public String toJSON(Object obj) throws JSONException {
        return core.toJSON(obj);
    }

    /**
     * Fetch the list of audit revisions that exist of a given persisted object.
     * @param clazz The type of object being queried.
     * @param the systemId of the object being queried.
     * @return A list of revision numbers.
     */
    public List<Number> fetchVersionNumbers(Class<? extends Type> clazz, Long systemId) {
        return core.fetchVersionNumbers(clazz, systemId);
    }
}
