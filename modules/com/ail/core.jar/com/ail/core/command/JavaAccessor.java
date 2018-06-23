/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
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
package com.ail.core.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.classloader.NamespaceClassLoaderManager;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;
import com.ail.core.factory.AbstractFactory;

/**
 * This accessor provides access to services which require runtime location and
 * compilation of Java source files using the JavaCompiler api.
 * <p/>
 *
 * Unlike the Janino accessor, it does not handle runtime script compilation.
 * The java source is compiled to java byte code at runtime and the resulting
 * code is invoked by the accessor.
 * <p/>
 * As used by this accessor, Java source services adopt a simple contract: they
 * must define an invoke method which accepts only one argument of a type which
 * is suitable for the command being serviced.
 * <p/>
 *
 */
public class JavaAccessor extends Accessor implements ConfigurationOwner {

    private Argument args;
    private Core core;
    private String name;
    private transient Throwable activationException;

    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return args.getCallersCore().getVersionEffectiveDate();
    }

    @Override
    public Principal getSecurityPrincipal() {
        return args.getCallersCore().getSecurityPrincipal();
    }

    /**
     * Return the caller's configuration namespace.
     *
     * @return Caller's namespace
     */
    @Override
    public String getConfigurationNamespace() {
        return args.getCallersCore().getConfigurationNamespace();
    }

    @Override
    public void resetConfiguration() {
        // do nothing, this class doesn't "own" a configuration - it just uses other
        // peoples.
    }

    @Override
    public void setArgs(Argument args) {
        this.args = args;
    }

    @Override
    public Argument getArgs() {
        return args;
    }

    @Override
    public void invoke() throws BaseException {
        super.logEntry();

        if (activationException != null) {
            throw new JavaServiceException("JavaAccessor activation error", activationException);
        }

        try {
            long start = System.nanoTime();
            Class<?> theClass = NamespaceClassLoaderManager.getClassLoader(getConfigurationNamespace(), core).loadClass(name);
            if (theClass == null) {
                throw new IllegalStateException("No class loaded for name " + name + "! Was there an error during compilation?");
            }
            core.logInfo("Loaded " + theClass.getName()  + " in " + ((System.nanoTime() - start) / 1000000d) + "ms");

            // search through all the methods on this class and locate a method
            // named 'invoke' which accepts exactly one argument which is assignable
            // from the argument we're processing... and invoke it.
            boolean methodInvoked = false;
            for (Method m : theClass.getMethods()) {
                if ("invoke".equals(m.getName())) {
                    if (m.getParameterTypes().length == 1) {
                        for (Class<?> p : m.getParameterTypes()) {
                            if (p.isAssignableFrom(args.getClass())) {
                                m.invoke(null, args);
                                methodInvoked = true;
                            }
                        }
                    }
                }
            }

            if (!methodInvoked) {
                throw new JavaServiceException("invoke(" + args.getClass() + ") method not found on service: " + name);
            }
        } catch (SecurityException e) {
            throw new JavaServiceException("Security exception accessing 'void invoke(...)' method in java service: " + name + ".", e);
        } catch (RuntimeException e) {
            throw new JavaServiceException("Error executing java method for service: " + name + ". Error:" + e.getCause(), e);
        } catch (IllegalAccessException e) {
            throw new JavaServiceException("Cannot access 'void invoke(...)' method in java service:" + name, e);
        } catch (InvocationTargetException e) {
            throw new JavaServiceException("Exception from java method in service: " + name + " Exception:" + e.getCause(), e.getCause());
        } catch (ClassNotFoundException e) {
            core.logError(e.getMessage());
            throw new JavaServiceException("Exception from java method in service: " + name + " Exception:" + e.getCause(), e.getCause());
        }

        super.logExit();

    }

    @Override
    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a Java service");
    }

    @Override
    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a Java service");
    }

    /**
     * Factory life cycle method. See {@link AbstractFactory} for details.
     *
     * @param core
     * @param typeSpec
     */
    public void activate(Core core, com.ail.core.configure.Type typeSpec) {
        this.activationException = null;
        this.core = core;
        this.name = typeSpec.getName();

        core.logInfo("Activating Java class for service: " + name);
    }

    public Core getCore() {
        if (core == null) {
            core = new Core(this);
        }

        return core;
    }
}
