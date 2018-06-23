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

import java.io.StringReader;
import java.io.StringWriter;
import java.security.Principal;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.XMLString;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;

public class XSLTAccessor extends Accessor implements ConfigurationOwner {
    private transient Transformer transformer=null;
    private Core core=null;
    private Argument args=null;
    private String script=null;
    private String url=null;

    public void setArgs(Argument args) {
        this.args=args;
    }

    public Argument getArgs() {
        return args;
    }

    public void invoke() throws BaseException {
        super.logEntry();

        Transformer t;
        try {
            // setup the transformer using the XSLT service's XSLT script
            t = getTransformer(new StreamSource(new StringReader(Functions.loadScriptOrUrlContent(getCore(), getUrl(), getScript()))));

            // load the source (args) into a string
            StringReader xmlSource=new StringReader(core.toXML(getArgs()).toString());

            // define a place for the results to go
            StringWriter xmlResult=new StringWriter();

            // run the transformation
            t.transform(new StreamSource(xmlSource), new StreamResult(xmlResult));

            // convert output of XSLT back into the arg
            setArgs(core.fromXML(getArgs().getClass(), new XMLString(xmlResult.toString())));
            getArgs().setCallersCore(getCore());
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new AccessorError("System XSLT configuration failed");
        }
        catch (TransformerException e) {
            e.printStackTrace();
            throw new XSLTServiceException("Transformation failed for:"+getArgs().getClass().getName());
        }

        super.logExit();
    }

    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a XSLT service");
    }

    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a XSLT service");
    }

    public void setScript(String script) {
        this.script=script;
    }

    public String getScript() {
        return script;
    }

    private Transformer getTransformer(Source source) throws TransformerConfigurationException {
        if (transformer==null) {
            // Hard code the use of saxon - at least until xalan supports XSLT 2.0
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

            transformer=factory.newTransformer(source);
        }
        
        return transformer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public VersionEffectiveDate getVersionEffectiveDate() {
        return args.getCallersCore().getVersionEffectiveDate();
    }

    public Principal getSecurityPrincipal() {
        return args.getCallersCore().getSecurityPrincipal();
    }

    /**
     * Return the caller's configuration namespace.
     * @return Caller's namespace
     */
    public String getConfigurationNamespace() {
        return args.getCallersCore().getConfigurationNamespace();
    }

    public void resetConfiguration() {
        // do nothing, this class doesn't "own" a configuration - it just uses other peoples.
    }

    public Core getCore() {
        if (core==null) {
            core=new Core(this);
        }
        
        return core;
    }
}
