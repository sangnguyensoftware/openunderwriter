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

import static com.ail.core.Functions.classForName;

import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.FunctionLibrary;

import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.configure.UnknownNamespaceError;

/**
 * This class acts as a singleton defining all the JXPath function libraries available to the system and
 * accessed via the {@link Type} class' xpath methods (e.g. {@link Type#xpathGet(String) xpathGet}).<p/>
 * The library is initially create based on the list of function classes defined in the core 
 * config's 'JXPathExtensions' group. This group has 0..* Parameters whose names and values
 * are taken to be the namesapces and the function classes to be associated with them. 
 */
public final class TypeXPathFunctionRegister {
    private static TypeXPathFunctionRegister functionRegister=new TypeXPathFunctionRegister();
    private FunctionLibrary functionLibrary;
    
    private TypeXPathFunctionRegister() {
    }

    public static TypeXPathFunctionRegister getInstance() {
        return functionRegister;
    }

    /** 
     * Register a new xpath function class. JXPath provides for functions to be executed from xpath
     * expressions - effectively bridging the gap between xpath and java. Functions are referenced
     * from with xpath expressions as follows:<p/>
     * &nbsp;&nbsp;&lt;namespace&gt;:&lt;method name&gt;(&lt;arg1&gt;, ...)<p/>
     * You can register as many function classes as you like as long as each has a unique namespace. <b>An attempt
     * to add a function class using a namespace which is already in use will fail silently</b>.
     * The library of function classes available to JXPath is shared between all instances of <i>Type</i> so care
     * must be taken to keep namespaces unique.<p/>
     * It is anticipated that the use of this method will be pretty minimal. The Core configuration allows for a
     * set of default functions to be loaded automatically using the JXPathExtensions configuration group.
     * @param functionClass Class containing functions to be added.
     * @param namespace Namespace to register the class as.
     */
    public void registerFunctionLibrary(String namespace, Class<?> clazz) {
        getFunctionLibrary().addFunctions(new ClassFunctions(clazz, namespace));
    }
    
    /**
     * Force the function library to be reloaded. It is only necessary to call this method if
     * the list of libraries has been changed in core config. This is unlikely to happen outside
     * of testing.
     */
    public void reloadFunctionLibrary() {
        functionLibrary=null;
        getFunctionLibrary();
    }
    
    /**
     * Return the currently defined function library. 
     * @return FunctionLibrary.
     */
    public FunctionLibrary getFunctionLibrary() {
        if (functionLibrary!=null) {
            return functionLibrary;
        }
        else {
            synchronized(functionRegister) {
                if (functionLibrary==null) {
                    FunctionLibrary fl=new FunctionLibrary();
                    CoreProxy cp=new CoreProxy();
                    Class<?> clazz=null;
                
                    try {
                        Group functionGroup=cp.getGroup("JXPathExtensions");
                        
                        if (functionGroup!=null) {
                            for(Parameter p:functionGroup.getParameter()) {
                                try {
                                    clazz = classForName(p.getValue());
                                    fl.addFunctions(new ClassFunctions(clazz, p.getName()));
                                    cp.logInfo("Loaded JXPath function class: '"+p.getValue()+"' into namespace:"+p.getName());
                                }
                                catch (ClassNotFoundException e) {
                                    // It's an error to define a class that doesn't exist - but lets not have a cow.
                                    cp.logError("Core config defines "+p.getValue()+" as a JXPath function class, but the class could not be found.");
                                }
                            }
                        }
                    }
                    catch(UnknownNamespaceError e) {
                        // cp.logError would be better, but if cp doesn't have a namespace logError won't work.
                        System.err.println("Failed to load JXPath functions from configuration due to: "+e);
                    }
                    
                    functionLibrary=fl;
                }
            }
        }
        
        return functionLibrary;
    }
}
