/* Copyright Applied Industrial Logic Limited 2004. All rights Reserved */
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

import com.ail.core.CoreUserImpl;
import com.ail.core.Core;
import com.ail.core.XMLString;
import com.ail.core.XMLException;

import java.io.InputStream;
import java.security.Principal;

public abstract class ConfigurationOwnerImpl extends CoreUserImpl implements ConfigurationOwner {
    private String defaultConfigurationResourceName=null; // Resource to load default config from.
    private String namespace=null;

    /**
     * Constructor. Create an instance where the default configuration is
     * loaded from a class resource based on the caller's class.
     * @deprecated Use {@link #ConfigurationOwnerImpl(int, Class, Principal)} instead.
     * @param configSelectionFlag
     * @param caller
     */
    public ConfigurationOwnerImpl(int configSelectionFlag, Class<?> caller) {
        super(configSelectionFlag, caller.getName(), null);

        String namespace=caller.getClass().getName();

        if (namespace.endsWith("Service")) {
            namespace=namespace.substring(0, namespace.length()-7);
        }
        else if (namespace.endsWith("Bean")) {
            namespace=namespace.substring(0, namespace.length()-4);
        }

        defaultConfigurationResourceName="/"+namespace.replace('.', '/')+"DefaultConfig.xml";
    }

    /**
     * Constructor. Create an instance where the default configuration is
     * loaded from a class resource based on the caller's class.
     * @param configSelectionFlag Either {@link CoreUserImpl#SELECT_CONSISTENT_CONFIGURATIONS} or {@link CoreUserImpl#SELECT_LATEST_CONFIGURATIONS}
     * @param caller Callers class from which the configuration's namespace is derived.
     * @param securityPrincipal Principal of the caller.
     */
    public ConfigurationOwnerImpl(int configSelectionFlag, Class<?> caller, Principal securityPrincipal) {
        super(configSelectionFlag, caller.getName(), securityPrincipal);

        String namespace=caller.getClass().getName();

        if (namespace.endsWith("Service")) {
            namespace=namespace.substring(0, namespace.length()-7);
        }
        else if (namespace.endsWith("Bean")) {
            namespace=namespace.substring(0, namespace.length()-4);
        }

        defaultConfigurationResourceName="/"+namespace.replace('.', '/')+"DefaultConfig.xml";
    }

    /**
     * Constructor. Create an instance where the default configuration is
     * loaded from a specified namespace.
     * @deprecated Use {@link #ConfigurationOwnerImpl(int, String, Principal)} instead.
     * @param configSelectionFlag
     * @param namespace
     */
    public ConfigurationOwnerImpl(int configSelectionFlag, String namespace) {
        super(configSelectionFlag, namespace, null);
        defaultConfigurationResourceName="/"+namespace.replace('.', '/')+"DefaultConfig.xml";
    }

    /**
     * Constructor. Create an instance where the default configuration is
     * loaded from a specified namespace.
     * @param configSelectionFlag Either {@link CoreUserImpl#SELECT_CONSISTENT_CONFIGURATIONS} or {@link CoreUserImpl#SELECT_LATEST_CONFIGURATIONS}
     * @param caller Callers class from which the configuration's namespace is derived.
     * @param securityPrincipal Principal of the caller.
     */
    public ConfigurationOwnerImpl(int configSelectionFlag, String namespace, Principal securityPrincipal) {
        super(configSelectionFlag, namespace, securityPrincipal);
        defaultConfigurationResourceName="/"+namespace.replace('.', '/')+"DefaultConfig.xml";
    }

    /**
     * Retrieve the classes configuration.
     * @return The classes configuration
     */
    public Configuration getConfiguration() {
        if (getCore()==null) {
            return null;
        }

        return getCore().getConfiguration();
    }

    /**
     * Retrieve the configuration namespace used by this class.
     * This namespace is simply a unique string used by the class to identify
     * its configuration.
     * @return Namespace string
     */
    public String getConfigurationNamespace() {
        return namespace;
    }

    /**
     * Factory reset. Reset the configuration to the factory defaults.
     */
    public void resetConfiguration() {
        if (getCore()==null) {
            return;
        }

        try {
            // load the <name>DefaultConfig resource into an XMLString
            InputStream in=this.getClass().getResourceAsStream(defaultConfigurationResourceName);

            if (in==null) {
                throw new ConfigurationResetError("Couldn't find class resource (name='"+defaultConfigurationResourceName+"')");
            }

            XMLString factoryConfigXML=new XMLString(in);

            // marshal the config XML into an instance of Configuration
            Configuration factoryConfig=getCore().fromXML(Configuration.class, factoryConfigXML);

            // reset the configuration
            setConfiguration(factoryConfig);
        }
        catch(XMLException e) {
            throw new ConfigurationResetError("Default Configuration XML error for '"+defaultConfigurationResourceName+"' sax error is:"+e.getMessage());
        }
        catch(Exception e) {
            throw new ConfigurationResetError("Failed to reset "+defaultConfigurationResourceName+" configuration: "+e);
        }
    }

    /**
     * Update the classes configuration with those passed in.
     * @param config new configuration
     */
    public void setConfiguration(Configuration config) {
        if (getCore()!=null) {
            getCore().setConfiguration(config);
        }
    }

    public abstract Core getCore();
}
