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

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * This accessor supports the use of BeanShell scripts as services. BeanShell is an open
 * source java interpreter (www.beanshell.org). It interprets a scripting language that looks
 * very like java - the most notable exception being that the variables are not "typed". It
 * literally interprets - from Strings or files, there is no compilation phase involved.<p>
 * The interpreter runs 'in-jvm' and through the adoption of a simple convention it has access
 * to the command argument instance passed to the service, and can modify the instance's
 * values.<p>
 * This accessor supports the notion of extending (or inheriting) from another BeanShell service.
 * In effect, the script defined in the extended service is prepended to the script defined in
 * this service and the result is executed. The service will walk up a tree of services if
 * necessary - i.e. if the parent script extends a further service then it also get includes.
 * The sample configuration below shows how the extends feature is setup; the line in bold
 * shows forces <code>BeanShellService2</code> to extend <code>BeanShellService</code>:
 * <pre>
 * &lt;service name="BeanShellService" builder="ClassBuilder" key="com.ail.core.command.BeanShellAccessor" &gt;
 *    &lt;parameter name="Script"&gt;&lt;![CDATA[
 *      if (args.getX()&lt;100) {
 *        args.setR(args.getX()+args.getY());
 *      }
 *      else {
 *        args.setR((2*args.getX())+args.getY());
 *      }
 *    ]]&gt;&lt;/parameter&gt;
 * &lt;/service&gt;
 *
 * &lt;service name="BeanShellService2" builder="ClassBuilder" key="com.ail.core.command.BeanShellAccessor"&gt;
 *    <b>&lt;parameter name="Extend"&gt;BeanShellService&lt;/parameter&gt;</b>
 *    &lt;parameter name="Script"&gt;&lt;![CDATA[
 *      if (args.getX()>1000) {
 *          args.setR(args.getX()-(2*args.getY()));
 *      }
 *    ]]&gt;&lt;/parameter&gt;
 * &lt;/service&gt;
 * </pre>
 */
public class BeanShellAccessor extends Accessor implements ConfigurationOwner {
    Argument args=null;
    Core core=null;
    String wholeScript=null;
    String script=null;
    String url=null;
    String extend=null;

    @Override
    public void setArgs(Argument args) {
        this.args=args;
    }

    @Override
    public Argument getArgs() {
        return args;
    }

    /**
     * This method builds the complete script for this service. If the service <b>doesn't</b>
     * extend another (using the Extend parameter), then this method will simply return
     * this service's own script. If it does extend another it will fetch the script from that
     * service and prepend it to its own script. If necessary the method will climb up any
     * number of services that each extend another in order to build the complete script.
     * @param script The script we're building
     * @param extend The name of the service to extend (from the Extend parameter) - this may be null
     * @return The complete script
     */
    private String buildWholeScript(StringBuffer script, String extend) {
        String thisScript=getCore().getParameterValue("_Types."+extend+".Script");
        String nextExtend=getCore().getParameterValue("_Types."+extend+".Extend");

        if (nextExtend!=null) {
            buildWholeScript(script, nextExtend);
        }

        if (thisScript!=null) {
            script.append(thisScript);
        }

        script.append(Functions.loadScriptOrUrlContent(getCore(), getUrl(), getScript()));

        return script.toString();
    }

    @Override
    public void invoke() throws BaseException {
        super.logEntry();

        synchronized(this) {
            if (wholeScript==null) {
                wholeScript=buildWholeScript(new StringBuffer(), extend);
            }
        }

        Interpreter inter=new Interpreter();

        try {
            inter.set("args", args);
            inter.set("core", core);
            inter.eval(wholeScript.toString());
            args=(Argument)inter.get("args");
        }
        catch(EvalError e) {
            throw new BeanShellServiceException(e);
        }

        super.logExit();
    }

    @Override
    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a BeanShell service");
    }

    @Override
    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a BeanShell service");
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

    public String getWholeScript() {
        return wholeScript;
    }

    public void setWholeScript(String wholeScript) {
        this.wholeScript = wholeScript;
    }
}
