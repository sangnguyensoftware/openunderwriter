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

import java.security.Principal;

import com.ail.core.command.Argument;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;

/**
 * The Service abstract class is implemented by all classes that may be exposed
 * as component entry points.
 * <p>
 */
public abstract class Service<A extends Argument> extends Type implements CoreUser, ConfigurationOwner {
    protected A args;
    protected Core core;

    public Service() {
        core = new Core(this);
    }

    public void setCore(Core core) {
        this.core = core;
    }

    /**
     * Set the arguments to be used by this entry point's business logic.
     *
     * @param args
     *            Arguments for the entry point to process.
     */
    public void setArgs(A args) {
        this.args = args;
    }

    /**
     * Get the arguments used by this entry point. "Arguments" in this context
     * are both the objects passed into the entry point, and the objects
     * returned by it. The entry point user will call getArgs() to retrieve the
     * entry points results.
     *
     * @return The results of executing the entry point.
     */
    public A getArgs() {
        return args;
    }

    /**
     * Invoke the entry point's business logic. This is the core of the entry
     * point. Before this method is called, setArgs() will have been called to
     * supply the entry point with the arguments it will process. After invoke()
     * has been called, getArgs() will be called to retrieve the results.
     */
    public abstract void invoke() throws BaseException;

    /**
     * A default entry point implementation returning the version effective
     * date. Entry points with a critical dependency on selecting the version
     * effective date should override this method. The default behaviour is to
     * use the version effective date from the CallersCore supplied in the Args
     * passed to the service. If that is null, then the date now is used.
     *
     * @return Date date to use.
     */
    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        try {
            return getArgs().getCallersCore().getVersionEffectiveDate();
        } catch (NullPointerException e) {
            return new VersionEffectiveDate();
        }
    }

    /**
     * Fetch the entry point's instance of the core.
     *
     * @return The core being used by the entry points.
     */
    public Core getCore() {
        return core;
    }

    /**
     * Default entry point implementation of the setConfiguration method. This
     * method takes care of saving configurations on the behalf of the entry
     * point.
     *
     * @param configuration
     *            The configuration to save.
     */
    @Override
    public void setConfiguration(Configuration configuration) {
        if (getCore() != null) {
            getCore().setConfiguration(configuration);
        }
    }

    /**
     * Default entry point implementation of the getConfiguration method. This
     * method takes care of loading configurations on the behalf of the entry
     * point.
     *
     * @return The loaded configuration.
     */
    @Override
    public Configuration getConfiguration() {
        if (getCore() == null) {
            return null;
        }

        return getCore().getConfiguration();
    }

    /**
     * Default entry point implementation of the getConfigurationNamespace
     * method. The name of the entry point class is returned.
     *
     * @return The classes namespace
     */
    @Override
    public String getConfigurationNamespace() {
        return this.getClass().getName();
    }

    /**
     * This method may be used by configuration owners who "share" a
     * configuration. It reset the configuration based on their namespace rather
     * than their classname.<br>
     * To use it they would overload the resetConfiguration method as follows:<br>
     *
     * <pre>
     * public void resetConfiguration() {
     *     super.resetConfigurationByNamespace();
     * }
     * </pre>
     */
    protected void resetConfigurationByNamespace() {
        String name;

        // Derive the configuration's name from the namespace
        name = getConfigurationNamespace();
        name = "/" + name.replace('.', '/');
        new ConfigurationResetHandler(getCore(), this, name).invoke();
    }

    /**
     * Default entry point implementation of the resetConfiguration method. This
     * implementation loads the default config from the class resource file
     * named "<Entry Point Name>DefaultConfig.xml". For an entry point named
     * 'RedService', the resource 'RedDefaultConfig.xml' is loaded.
     */
    @Override
    public void resetConfiguration() {
        String name;

        // Derive the name from the class' name
        name = this.getClass().getName();
        name = name.substring(name.lastIndexOf('.') + 1, name.lastIndexOf("Service"));
        new ConfigurationResetHandler(getCore(), this, name).invoke();
    }

    /**
     * Get the security principal associated with this instance.
     *
     * @return The associated security principal - if defined, null otherwise.
     */
    @Override
    public Principal getSecurityPrincipal() {
        try {
            return getArgs().getCallersCore().getSecurityPrincipal();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
