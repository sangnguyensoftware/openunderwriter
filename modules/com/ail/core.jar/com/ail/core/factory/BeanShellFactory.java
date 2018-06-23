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

package com.ail.core.factory;

import bsh.EvalError;
import bsh.Interpreter;

import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.TypeXPathException;
import com.ail.core.configure.Type;
import com.ail.annotation.Builder;

/**
 * Factory to create class instances from BeanShell scripts embedded in configuration.
 * The simple contract is that a type configured to use this factory must define either 
 * a "Script" or a "Url" parameter. The factory takes this parameter's value and either
 * uses it to download a script from in the case of Url, of uses it as the script itself
 * in the case of "Script".<p>
 * The object referred to by "type" will be returned by the factory.
 * The type may optionally define a parameter called "extends" which names another type
 * whose properties should be merged into this type's properties. See {@link com.ail.core.Type#mergeDonorIntoSubject(Type, Type, Core)}
 * for a description of the rules applied during the merge. If "extends" is defined, then
 * the object created by this script will be merged with the object created by <code>core.newType([<i>extends</i>])</code>.<p>
 * In the following sample of configuration a base type called "OtherVersion" defines a
 * type by means of the CastorBuilder. The NewVersion type "extends" OtherVersion (see the
 * extends parameter) and then modifies the type altering the Source property.
 * <pre>
 * &lt;type name="OtherVersion" builder="CastorBuilder" key="com.ail.core.Version"&gt;
 *   &lt;parameter name="script"&gt;&lt;![CDATA[
 *     &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 *     &lt;version serialVersion='0' lock='false' xsi:type='java:com.ail.core.Version' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'&gt;" +
 *       &lt;source&gt;Peach and mint&lt;/source&gt;
 *       &lt;state&gt;state&lt;/state&gt;
 *       &lt;date&gt;14/10/2002&lt;/date&gt;
 *       &lt;author&gt;T.S.Elliot&lt;/author&gt;
 *       &lt;comment&gt;The loganberry's are sweet&lt;/comment&gt;
 *       &lt;copyright&gt;Copyright us.&lt;/copyright&gt;
 *       &lt;version&gt;1.0&lt;/version&gt;
 *     &lt;/version&gt;
 *   ]]&gt;&lt;/parameter&gt;
 * &lt;/type&gt;
 *
 * &lt;type name="NewVersion" builder="BeanShellBuilder" key="com.ail.core.Version"&gt;
 *   &lt;parameter name="extends"&gt;OtherVersion&lt;/parameter&gt;
 *   &lt;parameter name="script"&gt;&lt;![CDATA[
 *      type=new com.ail.core.Version();
 *      type.setSource("peach and lemon");
 *   ]]&gt;&lt;/parameter&gt;
 * &lt;/type&gt;
 * </pre>
 */
@Builder(name="BeanShellBuilder")
public class BeanShellFactory extends AbstractFactory {
    Interpreter interpreter=new Interpreter();

    /**
     * Leave all the work to initialiseType.
     */
    protected Object instantiateType(Type typeSpec) {
        return null;
    }

    protected Object initialiseType(Object o, Type typeSpec, Core core) {
        Object type=null;
        String script;
        String url;
        
        try {
            try {
                script=typeSpec.xpathGet("parameter[nameLowerCase='script']/value", String.class);
            }
            catch(TypeXPathException e) {
                url=typeSpec.xpathGet("parameter[nameLowerCase='url']/value", String.class);            
                script=Functions.loadScriptOrUrlContent(core, url, null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FactoryConfigurationError("Script for BeanShell type: '"+typeSpec.getName()+"' could not be loaded."+e);
        }
        
        if (script==null || script.length()==0) {
            throw new FactoryConfigurationError("BeanShell type: '"+typeSpec.getName()+"' does not define 'script' or 'url' parameter, or script found is zero length.");
        }

        synchronized(interpreter) {
            try {
                interpreter.set("core", core);
                interpreter.eval(script);
                type=interpreter.get("type");
                if (type==null) {
                    throw new FactoryConfigurationError("BeanShell type script: '"+typeSpec.getName()+"' does not define variable 'type'");
                }
                return mergeWithBase((com.ail.core.Type)type, typeSpec, core);
            }
            catch(EvalError e) {
                e.printStackTrace();
                throw new FactoryConfigurationError("BeanShell type script: '"+typeSpec.getName()+"' "+e.getMessage());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.ail.core.factory.AbstractFactory#cachePrototype()
     */
    protected boolean cachePrototype()
    {
        return true;
    }
}
