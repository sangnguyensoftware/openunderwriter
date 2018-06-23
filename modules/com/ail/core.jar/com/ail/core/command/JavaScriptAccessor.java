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
package com.ail.core.command;

import java.security.Principal;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;
import com.ail.core.factory.AbstractFactory;

/**
 * This accessor supports the use of JavaScript as a service implementation. The
 * accessor makes use of Java's built in JavaScript engine. The usage pattern is to
 * have an instance of this accessor wrapping each JavaScript service and so make the
 * service available to commands. Each instance creates a JavaScriptEngine during
 * activation and loads the script (defined either in-line, or via URL) into the engine.
 * This instance is reused by each call into the accessor. It is recommended to use a
 * CachingClassLoader to load the service itself. Without this, the script will be reloaded
 * on each invocation.
 */
public class JavaScriptAccessor extends Accessor implements ConfigurationOwner {
    private Argument args = null;
    private Core core = null;
    private String extend = null;
    private String url = null;
    private String script = null;
    private String serviceName = null;
    private transient Invocable engine = null;
    private transient Throwable activationException = null;

    /**
     * Factory life cycle method. See {@link AbstractFactory} for details.
     *
     * @param core
     */
    public void activate(Core core, com.ail.core.configure.Type typeSpec)  {
        try {
            this.core = core;
            serviceName = typeSpec.getName();
            core.logInfo("Loading JavaScript for: " + typeSpec.getName() + " from: " + getUrl());
            initialiseEngine();
        } catch (ScriptException e) {
            activationException = e;
        }
    }

    @Override
    public void invoke() throws BaseException {
        if (activationException != null) {
            throw new JavaScriptServiceException("Error activating service '"+serviceName+"'.", activationException);
        }

        try {
            engine().invokeFunction(serviceName, args);
        } catch (NoSuchMethodException e) {
            throw new JavaScriptServiceException("Script for service " + serviceName + " must define a function of the same name.");
        } catch (ScriptException e) {
            throw new JavaScriptServiceException("Error executing JavaScript service: " + serviceName + ".", e);
        }
    }

    private Invocable engine() throws ScriptException {
        if (engine == null) {
            initialiseEngine();
        }

        return engine;
    }

    private void initialiseEngine() throws ScriptException {
        synchronized (this) {
            String wholeScript = buildWholeScript(new StringBuffer(), extend);

            ScriptEngine se = new ScriptEngineManager(null).getEngineByName("nashorn");

            se.eval(wholeScript);

            engine = (Invocable)se;
        }
    }

    /**
     * This method builds the complete script for this service. If the service
     * <b>doesn't</b> extend another (using the Extend parameter), then this
     * method will simply return this service's own script. If it does extend
     * another it will fetch the script from that service and prepend it to its
     * own script. If necessary the method will climb up any number of services
     * that each extend another in order to build the complete script.
     *
     * @param script
     *            The script we're building
     * @param extend
     *            The name of the service to extend (from the Extend parameter)
     *            - this may be null
     * @return The complete script
     */
    private String buildWholeScript(StringBuffer script, String extend) {
        String thisScript = getCore().getParameterValue("_Types." + extend + ".Script");
        String nextExtend = getCore().getParameterValue("_Types." + extend + ".Extend");

        if (nextExtend != null) {
            buildWholeScript(script, nextExtend);
        }

        if (thisScript != null) {
            script.append(thisScript);
        }

        script.append(Functions.loadScriptOrUrlContent(getCore(), getUrl(), getScript()));

        return script.toString();
    }

    private Core getCore() {
        if (core == null) {
            core = new Core(this);
        }

        return core;
    }

    @Override
    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a JavaScript service");
    }

    @Override
    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a JavaScript service");
    }

    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return args.getCallersCore().getVersionEffectiveDate();
    }

    @Override
    public Principal getSecurityPrincipal() {
        return args.getCallersCore().getSecurityPrincipal();
    }

    @Override
    public String getConfigurationNamespace() {
        return args.getCallersCore().getConfigurationNamespace();
    }

    @Override
    public void resetConfiguration() {
        // Nothing to do here, this accessor doesn't "own" a configuration of
        // its own, it uses the caller's.
    }

    @Override
    public void setArgs(Argument args) {
        this.args = args;
    }

    @Override
    public Argument getArgs() {
        return args;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
