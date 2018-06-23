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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.janino.JavaSourceClassLoader;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;

/**
 * This accessor provides access to services implemented using <a href="http://www.janino.net/">Janino</a>. Services
 * that use this accessor either define a script inline (using a Script parameter in the configuration), of they point
 * at a script via a URL (using the Url parameter). Either way, the script is compiled to java byte code by Janino
 * at runtime and the resulting code is invoked by the accssor.<p/>
 * Compilation is only carried on when the service is initially loaded - on the first attempt to execute the service.
 * Once compiled, the resulting class is cached by the accessor for faster access later on.<p/>
 * As used by this accessor, Janino scripts adopt a simple contract: they must define an invoke method which accepts
 * only one argument of a type which is suitable for the command being serviced.<p/>
 *
 * In the following example the command called 'MyTestCommand' had been bound to the Janino based 'DummyService'. The convention
 * within the core is to have a command class (MyTestCommand) paired with an argument implementation (MyTestArgImp); therefore,
 * the invoke method accepts an argument of that type.
 * <pre>
 * &lt;service name="DummyService" builder="CachingClassBuilder" key="com.ail.core.command.JaninoAccessor" &gt;
 *   &lt;parameter name="Script"&gt;&lt;![CDATA[
 *     import com.ail.core.dummyservice.TestArgImp;
 *
 *     public static void invoke(MyTestArgImp args) {
 *       if (args.getX()&gt;1000) {
 *         args.setR(args.getX()-(2*args.getY()));
 *       }
 *     }
 *   ]]&gt;&lt;/parameter&gt;
 * &lt;/service&gt;
 *
 * &lt;command name="MyTestCommand" builder="ClassBuilder" key="com.ail.core.dummyservice.DummyCommand"&gt;
 *    &lt;parameter name="Service"&gt;DummyService&lt;/parameter&gt;
 * &lt;/command&gt;
 * </pre>
 * The JaninoAccessor supports the concept of inheritance (or extension) between services based on it. The concept of one service 'Extend'ing
 * another is common to all Core accessors, but in the case of the JaninoAccessor the approach adopted is better thought of as a chain. In effect
 * the service which form part of the chain are each invoked in turn by the accessor.<p/>
 *
 * Building on the sample code above, an 'extending' service could be added as follows:
 * <pre>
 * &lt;service name="TestExtendingService" builder="CachingClassBuilder" key="com.ail.core.command.JaninoAccessor" &gt;
 *   &lt;parameter name="Extend"&gt;DummyService&lt;/parameter&gt;
 *   &lt;parameter name="Script"&gt;&lt;![CDATA[
 *     import com.ail.core.dummyservice.TestArgImp;
 *
 *     public static void invoke(MyTestArgImp args) {
 *       if (args.getX()&lt;1000) {
 *         args.setR(args.getX()-(4*args.getY()));
 *       }
 *     }
 *   ]]&gt;&lt;/parameter&gt;
 * &lt;/service&gt;
 *
 * &lt;command name="TestExtendingCommand" builder="ClassBuilder" key="com.ail.core.dummyservice.DummyCommand"&gt;
 *    &lt;parameter name="Service"&gt;TestExtendingService&lt;/parameter&gt;
 * &lt;/command&gt;
 * </pre>
 * The 'Extend' parameter in 'TestExtendingService' tells the accessor to execute the named service before this one - a
 * chain with two links. So in this case invoking the 'TestExtendingCommand' will lead to a 'DummyService' being invoked first,
 * and then 'TestExtendingService' being invoked.<p/>
 * Note that the invoke() methods in all the services in a chain must accept an argument of the same type.
 */
public class JaninoAccessor extends Accessor implements ConfigurationOwner {
    private Argument args=null;
    private Core core=null;
    private String script=null;
    private String url=null;
    private String extend=null;
    private String include=null;
    private transient List<Class<?>> clazz=null;
    private String name=null;
    private transient Throwable activationException;

    @Override
    public void setArgs(Argument args) {
        this.args=args;
    }

    @Override
    public Argument getArgs() {
        return args;
    }

    /**
     * Use Janino to compile and load a class.
     * @param name The name of the class
     * @param source The source of the class as a String
     * @return The Class representing the compiled source
     * @throws Exception
     */
    private Class<?> loadClasses(String name, JaninoResources resources) throws Exception {
        ClassLoader cl = new JavaSourceClassLoader(
                Thread.currentThread().getContextClassLoader(),
                resources,
                (String) null);
        return cl.loadClass(name);
    }

    private void addResources(String extended, JaninoResources finder) throws Exception {
        String nextExtend=getCore().getParameterValue("_Types."+extended+".Extend");

        if (nextExtend!=null) {
            addResources(nextExtend, finder);
        }

        if (extended!=null) {
            String thisScript=getCore().getParameterValue("_Types."+extended+".Script");
            String thisUrl=getCore().getParameterValue("_Types."+extended+".Url");

            if (thisUrl!=null) {
                core.logInfo("Compiling janino class body from url: "+thisUrl);
            }
            else {
                core.logInfo("Compiling janino class body from inline script:\n "+thisScript);
            }

            finder.add(extended, Functions.loadScriptOrUrlContent(getCore(), thisUrl, thisScript));
        }
    }

    /**
     * Factory life cycle method. See {@link AbstractFactory} for details.
     * @param core
     * @param typeSpec
     */
    public void activate(Core core, com.ail.core.configure.Type typeSpec) {
        if (clazz == null) {
            try {
                activationException = null;

                name = typeSpec.getName();

                clazz = new ArrayList<Class<?>>();

                this.core = core;

                core.logInfo("Compiling janino class body for service: " + name);

                JaninoResources resources = new JaninoResources();

                addResources(extend, resources);

                if (StringUtils.isNotEmpty(include)) {
                    for(String classToInclude : include.split(",")) {
                        addResources(classToInclude, resources);
                    }
                }

                resources.add(name, Functions.loadScriptOrUrlContent(core, url, script));

                clazz.add(loadClasses(name, resources));

                core.logInfo("Compilation for service: " + name + " successful");
            } catch (Throwable t) {
                activationException = t;
            } finally {
                this.core = null;
            }
        }
    }

    @Override
    public void invoke() throws BaseException {
        super.logEntry();

        if (activationException!=null) {
            throw new JaninoServiceException("Janino activation error", activationException);
        }

        if (clazz==null) {
            throw new IllegalStateException("Janino class not loaded for service: "+name+". Was there an error during compilation?");
        }

        try {
            // search through all the classes that we know about and locate a method
            // named 'invoke' which accepts exactly one argument which is assignable
            // from the argument we're processing... and invoke it.
            boolean methodInvoked=false;
            for(Class<?> c: clazz) {
                for(Method m: c.getMethods()) {
                    if ("invoke".equals(m.getName())) {
                        if (m.getParameterTypes().length==1) {
                            for(Class<?> p: m.getParameterTypes()) {
                                if (p.isAssignableFrom(args.getClass())) {
                                    m.invoke(null, args);
                                    methodInvoked=true;
                                }
                            }
                        }
                    }
                }
            }

            if (!methodInvoked) {
                throw new JaninoServiceException("invoke("+args.getClass()+") method not found on service: "+name);
            }
        }
        catch (SecurityException e) {
            throw new JaninoServiceException("Security exception accessing 'void invoke(...)' method in janino service: "+name+".", e);
        }
        catch (RuntimeException e) {
            throw new JaninoServiceException("Error executing janino method for service: "+name+". Error:"+e.getCause(), e);
        }
        catch (IllegalAccessException e) {
            throw new JaninoServiceException("Cannot access 'void invoke(...)' method in janino service:"+name, e);
        }
        catch (InvocationTargetException e) {
            throw new JaninoServiceException("Exception from janino method in service: "+name+" Exception:"+e.getCause(), e.getCause());
        }

        super.logExit();
    }

    @Override
    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a Janino service");
    }

    @Override
    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a Janino service");
    }

    public void setScript(String script) {
        this.script=script;
    }

    public String getScript() {
        return script;
    }

    public void setUrl(String url) {
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

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
     * @return Caller's namespace
     */
    @Override
    public String getConfigurationNamespace() {
        return args.getCallersCore().getConfigurationNamespace();
    }

    @Override
    public void resetConfiguration() {
        // do nothing, this class doesn't "own" a configuration - it just uses other peoples.
    }

    public Core getCore() {
        if (core==null) {
            core=new Core(this);
        }

        return core;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

}
