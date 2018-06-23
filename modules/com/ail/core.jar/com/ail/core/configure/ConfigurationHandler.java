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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.ail.core.Core;
import com.ail.core.CoreUser;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.classloader.NamespaceClassLoaderManager;

/**
 * The configuration handler manages the loading, saving, caching and querying
 * of configurations. A configuration is a collection of properties and groups
 * of properties and other settings which a component uses to hold it's
 * configuration settings. Configurations are time sensitive and are always
 * loaded, saved and queried with respect to a 'version effective date'.
 * Configurations are typically loaded from an external source (class resource,
 * cms store, etc) by the component which owns them and stored into the
 * configuration store. It is this store that holds the whole history of the
 * configuration. The configuration handler delegates to an instance of
 * AbstractConfiruationLoader to handler the actual persistence of
 * configurations.
 */
public class ConfigurationHandler {
    /** The actual handler this class will delegate to. */
    private static ConfigurationHandler instance = null;

	/** This loader will be used to load/save configurations. */
	private AbstractConfigurationLoader loader=null;

	/** Hash of namespaces. Namespace is the key, Lists of Configuration are the values. */
	private static Hashtable<String,ArrayList<Configuration>> namespaces=null;

    /**
     * The timeout to use on namespaces that don't exist. If a configuration isn't found
     * for a namespace a dummy one is created with this timeout. This dummy is then cached
     * and stops us from hitting the database whenever the namespace is used.
     */
    private static int MISSING_NAMESPACE_TIMEOUT = -1; // -1 means never timeout

	/**
     * Protected constructor to prevent instantiation.
     */
	protected ConfigurationHandler() {
		loader=AbstractConfigurationLoader.loadLoader();
	 	namespaces=new Hashtable<>();
    }

	/**
     * Get a reference to the instance of the handler.
     */
	public static ConfigurationHandler getInstance(){
        if (instance == null) {
            synchronized(ConfigurationHandler.class) {
                if (instance == null) {
                    instance = new ConfigurationHandler();
                }
            }
        }
        return instance;
    }

	/**
     * This method resets the ConfigurationHandlers internal cache.
     * There really isn't really much benefit in clearing the cache, unless the
     * system has undergone many many configuration changes to the point where
     * the cache is holding too much memory hostage.<p>
     * Aside from a performance hit, as configuration records are loaded 'on demand'
     * over time, resetting the cache will have no harmful side effects.
	 */
	public static void resetCache() {
		if (namespaces!=null) {
			synchronized(namespaces) {
			 	namespaces=new Hashtable<>();
	        }
		}
    }

    /**
     * This method removes a specific namespace from the ConfigurationHandlers's
     * internal cache and also remove any other namespace that depend on it.
     * This may be useful especially when dealing with undefined namespaces: If
     * the configuration handler is asked to load a namespace but cannot find
     * it, it caches the fact that the namespace is unknown - this prevents it
     * from hitting the database every time the namespace is requested only to
     * find it sill isn't there. However, there are times when when the caller
     * needs to force a reload - for example when a namespace is first create.
     * <p>
     * Removing a namespace from the cache only has a very small performance
     * hit.
     * @param namespace
     *            The namespace to remove from the cache.
     * @return  A list of the namespaces that were removed.
     */
    public static List<String> reset(final String namespace) {
        // list of namespaces that were reset
        List<String> reset=new ArrayList<>();
        Boolean searchAgain=null;

        if (instance==null) {
			ConfigurationHandler.resetCache();
        }

        if (namespaces!=null) {
            // add the namespace we've been passed to the list to be reset
            reset.add(namespace);

            // search through all other namespaces in the cache and add any that depend on
            // the one we're reseting or other that depend on them.
            while(searchAgain==null || searchAgain) {
                searchAgain=false;
                for(Enumeration<ArrayList<Configuration>> configs=namespaces.elements() ; configs.hasMoreElements() ;) {
                    for(Configuration config: configs.nextElement()) {
                        if (!reset.contains(config.getNamespace())) {
                            if (reset.contains(config.getParentNamespace()) || (config.getParentNamespace()==null && reset.contains("com.ail.core.Core"))) {
                                reset.add(config.getNamespace());
                                searchAgain=true;
                            }
                        }
                    }
                }
            }

            // reset all of the caches in the list
            synchronized(namespaces) {
                for(String n: reset) {
                    namespaces.remove(n);
                    NamespaceClassLoaderManager.removeClassLoader(n);
                }
            }
        }

        return reset;
    }

    /**
     * Save (update/create) the configuration associated with the current namespace.
     * @param namespace The configuration namespace to save this config under.
     * @param config The configuration to save.
     * @param core The instance of core making the request.
     */
    public void saveConfiguration(String namespace, Configuration config, Core core) {
        config.setWho(core.getSecurityPrincipal() !=null
                ? core.getSecurityPrincipal().getName()
                : "unknown");

        loader.saveConfiguration(namespace, config);

        // Sleep for 1ms. Configurations are saved with timestamps that go down
        // to the 1ms resolution. This delay stops two configurations being
        // written in the same millisecond.
        try {
            Thread.sleep(1);
            core.getVersionEffectiveDate().setTime(new VersionEffectiveDate().getTime());
            core.logDebug("Configuration saved: "+namespace);
        }
        catch(InterruptedException e) {
            // ignore - it's not the end of the world if two configs do end up
            // with the same timestamp. And thats in the unlikely event that two
            // could be done within 1ms anyway. A message to the log is enough.
            core.logWarning("Sleep failed in configuration update. Overlapping configs may have resulted.");
        }
    }

    /**
     * Save (update/create) the configuration associated with the current namespace.
     * @param owner The configuration owner.
     * @param config The configuration to save.
     * @param core The instance of core making the request.
     */
	public void saveConfiguration(ConfigurationOwner owner, Configuration config, Core core) {
	    saveConfiguration(owner.getConfigurationNamespace(), config, core);
    }

	/**
     * Load (fetch and return) the configuration associated with the current
     * namespace and versionEffectiveDate.
     * @param owner The Configuration owner.
     * @param user The configuration user we're loading the config for.
     * @param core The instance of Core making the request.
     */
	public Configuration loadConfiguration(ConfigurationOwner owner, CoreUser user, Core core) {
        Configuration config=loader.loadConfiguration(owner.getConfigurationNamespace(), user.getVersionEffectiveDate());

		config.setLoadedAt(new Date(System.currentTimeMillis()));

        core.logDebug("Configuration loaded: "+owner.getConfigurationNamespace());

		return config;
    }

    /**
     * Load (fetch and return) the configuration associated with the current
     * namespace and versionEffectiveDate.
     * @param namespace Namespace to load the configuration of.
     * @param owner The Configuration owner.
     * @param core The instance of Core making the request.
     */
    public Configuration loadConfiguration(String namespace, CoreUser user, Core core) {
        Configuration config=loader.loadConfiguration(namespace, user.getVersionEffectiveDate());

        config.setLoadedAt(new Date(System.currentTimeMillis()));

        return config;
    }

    /**
     * Fetch a list of all the namespaces that have configurations
     * associated with them.
     * @return A Collection of String objects
     */
    public Collection<String> getNamespaces() {
        return loader.getNamespaces();
    }

    /**
     * Fetch the details of all the namespaces that have configurations associated
     * with them.
     * @return A collection of {@link ConfigurationSummary ConfigurationSummary} objects.
     */
    public Collection<ConfigurationSummary> getNamespacesSummary() {
        return loader.getNamespacesSummary();
    }

    /**
     * Fetch the details of all the elements in a namespaces history.
     * @return A collection of {@link ConfigurationSummary ConfigurationSummary} objects.
     */
    public Collection<ConfigurationSummary> getNamespacesHistorySummary(String namespace) {
        return loader.getNamespacesHistorySummary(namespace);
    }

    /**
     * Find a specific configuration. Configurations are held by namespace and
     * version effective date. This method manages a cache of previously loaded
     * configurations, which it uses in preference to making a DB round trip.
     * @param owner The Configuration owner (used to get the namespace).
     * @param core The instance of Core making the request.
     * @return The configuration.
     * @throws UnknownNamespaceError if the namespace is not found, or is not valid for the versionEffectiveDate.
     */
    protected Configuration findConfiguration(String owningNamespace, CoreUser user, Core core) {
		ArrayList<Configuration> nsl=null;
		Configuration config=null;

		// if the namespace isn't in the cache...
        if ((nsl=namespaces.get(owningNamespace))==null) {
            // sync this to prevent two threads from trying to load the same config at the same time
            synchronized(namespaces) {
                // prevent threads that were held up from reloading a config
                if ((nsl=namespaces.get(owningNamespace))==null) {
        			// ...load the configuration and add it to the cache. If the config
                    // cannot be found (does not exist) create a dummy one to take its place -
                    // this will stop us hitting the database every time the namespace is
                    // used.
                    try {
                        config=loadConfiguration(owningNamespace, user, core);
                    } catch (UnknownNamespaceError e) {
                        config=new Configuration();
                        config.setTimeout(MISSING_NAMESPACE_TIMEOUT);
                        config.setName("MISSING NAMESPACE DUMMY CONFIG");
                        config.setLoadedAt(new Date());
                        // make the dummy config valid from the beginning of time, and make
                        // it the latest (validTo==null).
                        config.setValidFrom(new VersionEffectiveDate(0));
                        config.setValidTo(null);
                    }

                    // add the loaded (or dummy) config to the cache
                    ArrayList<Configuration> list=new ArrayList<>();
                    list.add(config);
                    namespaces.put(owningNamespace, list);
                }
                else {
                    return findConfiguration(owningNamespace, user, core);
                }
            }
        }
        else {
            // ...search through the list of (historical) configs in the namespace...
            for (Configuration c : nsl) {
                // ...if the config's date range spans the owner's version effective date...
                if (c.getValidFrom().compareTo(user.getVersionEffectiveDate()) <= 0 && (c.getValidTo() == null || c.getValidTo().compareTo(user.getVersionEffectiveDate()) >= 0)) {
                    config = c;
                    // ...if the cached config has timed out, remove it to force a reload.
                    // Note: A -1 as a config timeout means "never timeout".
                    // TODO This needs to say "and there's a newer version of the config in the DB"
                    // TODO we'll need a new 'isNewerConfig(...)' method in the AbstractLoader to help here.
                    if (config.getValidTo() == null && config.getTimeout() > -1 && System.currentTimeMillis() - config.getLoadedAt().getTime() > config.getTimeout()) {
                        nsl.remove(config);
                        config = null;
                    }
                    break;
                }
                config = null;
            }

			// if config is still null, there wasn't one with the right date. Load it,
            // and put it in the cache.
            if (config==null) {
				config=loadConfiguration(owningNamespace, user, core);
                nsl.add(config);
            }
        }
        return config;
    }

	/**
     * Fetch the named parameter from the current configuration.
     * The "current configuration" is defined by the namespace (from owner), and
     * the version effective date taken from core.
     * The parameter name may be dot separated indicating that the parameter is
     * nested within one or more groups.<p>
     * The core's own configuration is used as a back-stop. If the group being
     * searched for is not in the namespace defined by the configuration owner,
     * then the core's configuration is checked.
     * @param paramName The name of the parameter to retrieve.
     * @param owner The configuration owner
     * @param core The instance of Core making the call.
     */
	public Parameter getParameter(String paramName, ConfigurationOwner owner, CoreUser user, Core core) {
	    Parameter ret=null;

        // First we'll look in the owner's configuration and see if the parameter is in there.
        Configuration config=findConfiguration(owner.getConfigurationNamespace(), user, core);
        ret=config.findParameter(paramName);

        // We didn't find the parameter paramName in the configurationOwner's config,
        // try that config's parent if it has one. Then try the parent's parent etc.
        while (config.getParentNamespace()!=null && ret==null) {
            config=findConfiguration(config.getParentNamespace(), user, core);
            ret=config.findParameter(paramName);
        }

        // Still no luck? As long as the configuration owner is not the core, try
        // the core's config.
		if (ret==null && !Core.CORE_NAMESPACE.equals(owner.getConfigurationNamespace())) {
            config=findConfiguration(Core.CORE_NAMESPACE, user, core);
            ret=config.findParameter(paramName);
		}

		// set the param's namespace to the config where it was found
		if (ret!=null) {
            ret.setNamespace(config.getNamespace());
        }

        return ret;
    }

    /**
     * Get the value of a parameter by name. The name may be dot separated to
     * drill down through nested groups. The following two bits of code are exactly
     * equivalent with the exception that the second will not fail with a NullPointerException is
     * "parameterName" is undefined:<br>
     * <code>
     * String x=getParameter("parameterName").getValue();
     * <br>
     * String x=getParameterValue("parameterName");
     * </code><br>
     * @param paramName Name of the parameter to fetch the value for
     * @param owner The owning configuration
     * @param user The user making the request
     * @param core The core being used by the requester.
     * @return Value of the parameter, or null if it is undefined.
     */
    public String getParameterValue(String paramName, ConfigurationOwner owner, CoreUser user, Core core) {
        Parameter p=getParameter(paramName, owner, user, core);
        return p!=null ? p.getValue() : null;
    }

    /**
     * Return the value of a parameter or a default value if the parameter is undefined.
     * @param paramName Name of the parameter to return the value of.
     * @param defaultValue If the parameter is undefined return this instead
     * @param owner The owning configuration
     * @param user The user making the request
     * @param core The core being used by the requester.
     * @return The value of the parameter, of <code>default</code> if it is undefined.
     */
    public String getParameterValue(String paramName, String defaultValue, ConfigurationOwner owner, CoreUser user, Core core) {
        String s=getParameterValue(paramName, owner, user, core);
        return s!=null ? s :defaultValue;
    }

    /**
     * Fetch the named group from current configuration.
     * The "current configuration" is defined by the namespace (taken from
     * <code>owner</code>), and the versionEffectiveDate (taken from <code>
     * core</code>). The group's name may be dot separated indicating
     * that the group is nested within other groups.<p>
     * The core's own configuration is used as a back-stop. If the group being
     * searched for is not in the namespace defined by the configuration owner,
     * then the core's configuration is checked.
     * @param owner The configuration owner.
     * @param core The instance of Core making the call.
     */
	public Group getGroup(String name, ConfigurationOwner owner, CoreUser user, Core core) {
	    boolean isSuperType=false;
        Configuration config=null;
        String cannonicalName=name;
        Group ret=null;

        if (name.contains("_Types.super.")) {
            isSuperType=true;
            cannonicalName=name.replace("_Types.super.", "_Types.");
        }

        try {
			config=findConfiguration(owner.getConfigurationNamespace(), user, core);
			if (!isSuperType) {
			    ret=config.findGroup(cannonicalName);
			}
        }
        catch(UnknownNamespaceError e) {
			if (owner==core) {
                throw e;
            }
        }

        while(ret==null && config!=null && config.getParentNamespace()!=null) {
            config=findConfiguration(config.getParentNamespace(), user, core);
            ret=config.findGroup(cannonicalName);
        }

		// If the group wasn't in the config user's namespace, is it in the core
        // namespace?
        if (ret==null && !Core.CORE_NAMESPACE.equals(owner.getConfigurationNamespace())) {
            config=findConfiguration(Core.CORE_NAMESPACE, user, core);
            ret=config.findGroup(cannonicalName);
        }

        // Still not found? Try the bootstrap config
        if (ret==null) {
            ret = BootstrapConfiguration.getInstance().findGroup(cannonicalName);
        }

        // Still not found? Try the config generated by annotations
        if (ret==null) {
            ret = AnnotationConfiguration.getInstance().findGroup(cannonicalName);
        }

        // Set the group's namespace to the config where it was found
        if (ret!=null) {
            ret.setNamespace(config.getNamespace());
        }

        return ret;
    }

	/**
     * Fetch all the Parameters in a group and return them as a java.util.Properties.
     * This is a utility method which returns the parameters in a named group
     * and creates a Properties object with them. Each Parameter's name and value
     * properties are mapped into a property in the Properties object.<p>
     * If the group specified does not exist, null is returned. If the group does
     * exist but contains no Parameters, an empty Properties object is returned.
     * @param name The name of the group whose parameters will be used.
     * @param owner The Configuration owner.
     * @param core The instance of Core making the call.
     * @return The parameters as properties, or null.
     */
	public Properties getParametersAsProperties(String name, ConfigurationOwner owner, CoreUser user, Core core) {
        Properties props=null;
        Group g=getGroup(name, owner, user, core);

		if (g!=null) {
            props=new Properties();
            for(Parameter p: g.getParameter()) {
                props.setProperty(p.getName(), p.getValue());
            }
        }

        return props;
    }

	/**
     * Return the source of the configuration being used by this instance of core. As configurations optionally
     * have a "parent" configuration that they inherit from, this method returns a collection of sources with
     * one element for each configuration in the hierarchy.
     * @param owner The configuration's owner
     * @param user The user asking for the source
     * @param core The core being used - and who's source will be returned
     * @return The sources from which the configuration was loaded.
	 */
    public Collection<String> getConfigurationSources(ConfigurationOwner owner, CoreUser user, Core core) {
        Set<String> sources=new LinkedHashSet<>();

        Configuration config=findConfiguration(owner.getConfigurationNamespace(), user, core);
        sources.add(config.getSource());

        while(config!=null && config.getParentNamespace()!=null) {
            config=findConfiguration(config.getParentNamespace(), user, core);
            sources.add(config.getSource());
        }

        // Add the core's own source if we haven't already.
        if (!Core.CORE_NAMESPACE.equals(config.getNamespace())) {
            config=findConfiguration(Core.CORE_NAMESPACE, user, core);
            sources.add(config.getSource());
        }

        return sources;
    }

    /**
     * Return the namespace(s) of the configuration(s) which are the parent, grandparent, etc of this
     * namespace As configurations optionally have "parent" configuration that they inherit from, this
     * method returns a collection of namespaces with one element for each configuration in the hierarchy.
     * @param owner The configuration's owner
     * @param user The user asking for the source
     * @param core The core being used - and who's source will be returned
     * @return The sources from which the configuration was loaded.
     */
    public Collection<String> getConfigurationNamespaceParent(ConfigurationOwner owner, CoreUser user, Core core) {
        Set<String> sources=new LinkedHashSet<>();

        Configuration config=findConfiguration(owner.getConfigurationNamespace(), user, core);
        sources.add(config.getNamespace());

        while(config!=null && config.getParentNamespace()!=null) {
            config=findConfiguration(config.getParentNamespace(), user, core);
            sources.add(config.getNamespace());
        }

        // Add the core's own namespace if we haven't already.
        if (!Core.CORE_NAMESPACE.equals(config.getNamespace())) {
            sources.add(Core.CORE_NAMESPACE);
        }

        return sources;
    }
}
