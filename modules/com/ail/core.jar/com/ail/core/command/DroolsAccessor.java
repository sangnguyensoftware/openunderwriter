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

import static com.ail.core.command.AccessorLoggingIndicator.FULL;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.event.DebugWorkingMemoryEventListener;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;

import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;
import com.ail.core.factory.AbstractFactory;

/**
 * This Accessor supports the use of Drools DRL as services. Drools 
 * (www.drools.org) is an open source rules engine which supports a native 
 * rule language (DRL). The usage pattern here is to have an instance of this
 * Accessor for each DRL based ruleset. The Accessor expects to find a URL 
 * pointing at the decision table in it's Script parameter (generally this is
 * set by configuration).
 */
public class DroolsAccessor extends Accessor implements ConfigurationOwner {
    private Core core=null;
    private Argument args=null;
    private String script=null;
    private String extend=null;
    private String url=null;
    private transient RuleBase ruleBase=null;
    private transient List<DroolsError> errors=new ArrayList<DroolsError>();

    /**
     * Factory life cycle method. See {@link AbstractFactory} for details.
     * @param core
     */
    public void activate(Core core, com.ail.core.configure.Type typeSpec) throws Exception {
        this.core=core;
        loadRuleBase();
        this.core=null;
    }
    
    public void setArgs(Argument args) {
        this.args=args;
    }

    public Argument getArgs() {
        return args;
    }

    /**
     * Merge the rule elements (rules, globals, functions, etc.) from donor into subject.
     * @param subject Package to merge into
     * @param donor Package to merge from
     */
    public static void mergeRulePackages(PackageDescr subject, PackageDescr donor) {
        // Add rules from the donor which don't already appear in the subject
        rules: for(RuleDescr donorRule: (List<RuleDescr>)donor.getRules()) {
            for(RuleDescr subjectRule: (List<RuleDescr>)subject.getRules()) {
                if (donorRule.getName().equals(subjectRule.getName())) {
                    continue rules;
                }
            }
            subject.addRule(donorRule);
        }

        // Add Globals from donor if they don't already appear in the subject
        for(GlobalDescr global: (List<GlobalDescr>)donor.getGlobals()) {
            if (!subject.getGlobals().contains(global)) {
                subject.addGlobal(global);
            }
        }

        // Add imports from the donor that don't appear in the subject
        for(ImportDescr imp: (List<ImportDescr>)donor.getImports()) {
            if (!subject.getImports().contains(imp)) {
                subject.addImport(imp);
            }
        }

        // Add functions from the donor that don't appear in the subject
        for(FunctionDescr donorFunc: (List<FunctionDescr>)donor.getFunctions()) {
            if (!subject.getFunctions().contains(donorFunc)) {
                subject.addFunction(donorFunc);
            }
        }
    }
    
    private void resolveInheritance(List<PackageDescr>pkgs, String extend) throws Exception {
        boolean mergeDone=false;
        
        if (extend!=null && extend.length()>0) {
            String donorScript=getCore().getParameterValue("_Types."+extend+".Script");
            String donorUrl=getCore().getParameterValue("_Types."+extend+".Url");

            PackageDescr donor=loadPackage(Functions.loadScriptOrUrlContent(getCore(), donorUrl, donorScript));

            // Check the ruleBase for a package with the same name as the donor
            for(PackageDescr pd: pkgs) {
                if (pd.getName().equals(donor.getName())) {
                    mergeRulePackages(pd, donor);
                    mergeDone=true;
                }
            }
            
            // If the ruleBase doesn't contain a package matching the donor, add it as a new package.
            if (!mergeDone) {
                pkgs.add(donor);
            }
            
            String nextExtend=getCore().getParameterValue("_Types."+extend+".Extend");

            resolveInheritance(pkgs, nextExtend);
        }
    }
    
    /**
     * Load the rules from a String into a package descriptor
     * @param drlString String of statements
     * @return Loaded package
     * @throws IOException
     * @throws DroolsParserException
     */
    private PackageDescr loadPackage(String drlString) throws IOException, DroolsParserException {
        // Parse the DRL string
        Reader reader = new StringReader(drlString);
        DrlParser parser = new DrlParser();

        // Add it to the list
        PackageDescr ret=parser.parse(reader);
        
        if (parser.hasErrors()) {
            errors.addAll(parser.getErrors());
        }
        
        return ret;
    }

    /**
     * Load the rulebase.
     * @throws Exception If the load fails
     */
    private synchronized void loadRuleBase() throws Exception {
        List<PackageDescr> pkgs=new ArrayList<PackageDescr>();

        pkgs.add(loadPackage(Functions.loadScriptOrUrlContent(getCore(), getUrl(), getScript())));
        resolveInheritance(pkgs, getExtend());

        // If any errors were found whilst parsing the DRL, throw an exception.
        if (errors.size()!=0) {
            StringBuffer message=new StringBuffer();
            for(DroolsError error: errors) {
                message.append(error.toString()).append("\n");
            }
            throw new DroolsServiceException("Drools errors detected: \n"+message.toString());
        }
        
        RuleBaseConfiguration rbc=new RuleBaseConfiguration();
        rbc.setClassLoader(this.getClass().getClassLoader());
        
        ruleBase = RuleBaseFactory.newRuleBase(rbc);

        PackageBuilderConfiguration pbc=new PackageBuilderConfiguration(this.getClass().getClassLoader());
        
        for(PackageDescr desc: pkgs) {
            PackageBuilder builder = new PackageBuilder(pbc);
            builder.addPackage( desc );
            ruleBase.addPackage(builder.getPackage());
        }
    }
    
    public void invoke() throws DroolsServiceException {
        super.logEntry();
        
        StatefulSession workingMemory=null;
        
        try {
            workingMemory=ruleBase.newStatefulSession();

            workingMemory.insert(getArgs());
            
            if (FULL.equals(getLoggingIndicator())) {
                workingMemory.addEventListener(new DebugWorkingMemoryEventListener());
            }
            
            workingMemory.fireAllRules();
            
        } catch(Exception e) {
            throw new DroolsServiceException(e);
        }
        finally {
            if (workingMemory!=null) {
                workingMemory.dispose();
            }
        }

        super.logExit();
    }

    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a DroolsAccessor service");
    }

    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a DroolsAccessor service");
    }

    public void setScript(String script) {
        this.script=script;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url=url;
    }

    public String getScript() {
        return script;
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

    /**
     * Clone this object. This clone method is used by all Type subclasses to handle deep cloning.
     * For the factory to operate correctly it is essential that Types can be deep cloned, as it
     * hangs onto prototyped instances by name and simply clones them when a request is made for
     * an instance of a named type.
     * @throws CloneNotSupportedException If the type cannot be deep cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        DroolsAccessor clone=(DroolsAccessor)super.clone();
        clone.ruleBase=ruleBase;
        return clone;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
