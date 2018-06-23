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

package com.ail.core.factory;

import com.ail.core.Core;
import com.ail.core.Type;
import com.ail.core.classloader.NamespaceClassLoader;
import com.ail.core.classloader.NamespaceClassLoaderManager;
import com.ail.core.command.Command;
import com.ail.core.configure.Builder;
import com.ail.core.configure.ConfigurationError;
import com.ail.core.configure.ConfigurationOwner;

public class FactoryHandler {
    private static FactoryHandler instance = null;

    protected FactoryHandler() {
    }

	/**
     * Fetch the builder associated with a type. This method
     * operates a cache to speed type creation. The Hashtable
     * 'builders' maps builder names to instances of the appropriate
     * sub-class of AbstractFactory.
     * @param builderName The name of the builder.
     * @param core The core to use to get configs.
     * @return The Factory to use to create instances of type 't'.
     */
    public AbstractFactory fetchBuilder(String builderName, Core core) {
		// Fetch the Builder to use to create instance of this type.
		Builder builder=(Builder)core.getGroup("_Builders."+builderName);

		if (builder==null) {
		    throw new ConfigurationError("Underfined builder referenced: "+builderName);
		}

        if (builder.getInstance()==null) {
			try {
				Class<?> clazz=Class.forName(builder.getFactory(), true, Thread.currentThread().getContextClassLoader());
				builder.setInstance((AbstractFactory)clazz.newInstance());
			}
            catch(ClassNotFoundException e) {
                throw new FactoryConfigurationError("Builder factory class not found: "+builder.getFactory());
            }
            catch(InstantiationException e) {
                throw new FactoryConfigurationError("Builder factory failed to instantiate: "+builder.getFactory());
            }
            catch(IllegalAccessException e) {
                throw new FactoryConfigurationError("Builder factory failed to instantiate - Illegal access: "+builder.getFactory());
			}
        }

		return builder.getInstance();
    }

	/**
     * Create an instance of a type and cache it as a prototype if required to.
     * @param typeSpec The configuration details of the type.
     * @return An instance of the type.
     */
	private Object createInstance(com.ail.core.configure.Type typeSpec, Core core) {
	    // If there's a prototype already, try using it.
	    if (typeSpec.getPrototype()!=null) {
	        try {
	            if (typeSpec.isSingleInstance()) {
	                return typeSpec.getPrototype();
                }
                else {
                    return typeSpec.getPrototype().clone();
                }
	        }
	        catch(CloneNotSupportedException e) {
	            // Tell the world that the clone failed, but not to worry: we'll fall
	            // through and create the instance the old fashioned method.
	            core.logWarning("Deep clone not supported by type '"+typeSpec.getName()+"'. Attempt to clone threw:"+e);
	        }
	    }

        // get hold of the Factory for this type
        AbstractFactory factory=fetchBuilder(typeSpec.getBuilder(), core);

        Object newInstance=null;

        if (factory.cachePrototype()) {
            synchronized(typeSpec) {
                if (typeSpec.getPrototype()==null) {
                    // Use the factory to create the instance
                    newInstance=factory.createType(typeSpec, core);

                    try {
                        if (typeSpec.isSingleInstance()) {
                            typeSpec.setPrototype((Type)newInstance);
                        }
                        else {
                            typeSpec.setPrototype((Type)((Type)newInstance).clone());
                        }
                    }
                    catch(CloneNotSupportedException e) {
                        // Tell the world about the clone failure, but don't worry about it. Performace may
                        // suffer as a result, but that may not be an issue.
                        core.logWarning("Clone for prototype failed for '"+typeSpec.getName()+"'. Attempt to clone threw:"+e);
                    }
                }
                else {
                    newInstance=createInstance(typeSpec, core);
                }
            }
        }
        else {
            // Use the factory to create the instance
            newInstance=factory.createType(typeSpec, core);
        }

        return newInstance;
	}

    public static synchronized FactoryHandler getInstance() {
        if (instance == null) {
            instance = new FactoryHandler();
        }

        return instance;
    }

    public Object newType(String name, ConfigurationOwner owner, Core core) {
        com.ail.core.configure.Type type = null;

        type = getGroupType(name, core);

        if (type == null) {
            // Maybe type is not in the standard config but was dynamically created by the product classloader...
            NamespaceClassLoader classLoader = NamespaceClassLoaderManager.getClassLoaderOrNull(owner.getConfigurationNamespace(), core);
            if (classLoader != null) {
                type = classLoader.getType(name);
            }
        }

        if (type == null) {
            throw new UndefinedTypeError("Type: '" + name + "' is undefined");
        }

        return (Type) createInstance(type, core);
    }

    private com.ail.core.configure.Type getGroupType(String name, Core core) throws UndefinedTypeError {
        try {
            return (com.ail.core.configure.Type) core.getGroup("_Types." + name);
        } catch (ClassCastException e) {
            throw new UndefinedTypeError("Type: '" + name + "' is not based on Type.class");
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T newType(Class<T> clazz, ConfigurationOwner owner, Core core) {
        return (T)newType(typeNameFromClass(clazz), owner, core);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T newType(Class<T> clazz, String modifier, ConfigurationOwner owner, Core core) {
        return (T)newType(typeNameFromClass(clazz)+"/"+modifier, owner, core);
    }

    public Command newCommand(String name, ConfigurationOwner owner, Core core) {
        Command command=(Command)newType(name, owner, core);
        command.getArgs().setCallersCore(core.getCoreUser());
        return command;
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T newCommand(Class<T> clazz, ConfigurationOwner owner, Core core) {
        return (T)newCommand(typeNameFromClass(clazz), owner, core);
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T newCommand(Class<T> clazz, String modifier, ConfigurationOwner owner, Core core) {
        return (T)newCommand(typeNameFromClass(clazz)+"/"+modifier, owner, core);
    }

    private String typeNameFromClass(Class<?> clazz) {
        String name=clazz.getName();
        return name.replace('$', '.');
    }
}
