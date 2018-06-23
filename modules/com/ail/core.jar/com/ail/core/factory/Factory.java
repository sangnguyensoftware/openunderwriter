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

import com.ail.core.command.Command;

/**
 * This interface describes the contract between the Core class and the Factory
 * sub-system. It defines the methods that Core is expected to expose on behalf
 * of the Factory, and for which the Factory provides Entry Points based
 * implementations.
 */
public interface Factory {

    /**
     * Create a new instance of the command specified. The details of the type
     * to be created are loaded from the callers configuration.
     * @param commandName The name of the command to create an instance of
     * @param clazz The expected type of the resulting command 
     * @return An instance of the command.
     */
    <T extends Command> T newCommand(String commandName, Class<T> clazz);
    
    /**
     * Create a new instance of the command specified with a modifier. The details of the type
     * to be created are loaded from the callers configuration.
     * @param commandName The name of the command to create an instance of
     * @param modifier A modifier to apply to the command name.
     * @param clazz The expected type of the resulting command 
     * @return An instance of the command.
     */
    <T extends Command> T newCommand(String commandName, String modifier, Class<T> clazz);
    
    /**
     * Create a new instance of the command specified. The details of the type
     * to be created are loaded from the callers configuration.
     * @param clazz The class of the type to be created.
     * @return An instance of the command.
     */
	<T extends Command> T newCommand(Class<T> clazz);
	
    /**
     * Create a new instance of the command specified. The details of the type
     * to be created are loaded from the callers configuration based on the 
     * clazz's name and the additional modifier.
     * @param clazz The class of the type to be created.
     * @param modifier select the specific configuration required. 
     * @return An instance of the command.
     */
    <T extends Command> T newCommand(Class<T> clazz, String modifier);

    /**
     * Create a new instance of the named type. The typeName argument
     * relates to a type in the callers configuration which defines the
     * specifics of the type to be created.
     * @param typeName The name use to load the type's details.
     * @return An instance of a type.
     */
	Object newType(String typeName);

    /**
     * Create a new instance of the named type with a modifier. The typeName argument
     * relates to a type in the callers configuration which defines the specifics of 
     * the type to be created. The modifier is applied to the typeName to arrive at
     * the name of the type required.
     * @param typeName The name use to load the type's details.
     * @param modifer to be applied
     * @param clazz The expected type of the resulting command 
     * @return An instance of a type.
     */
    <T extends Object> T newType(String typeName, String modifier, Class<T> clazz);

    /**
     * Create a new instance of the named type. The typeName argument
     * relates to a type in the callers configuration which defines the
     * specifics of the type to be created. 
     * the name of the type required.
     * @param typeName The name use to load the type's details.
     * @param clazz The expected type of the resulting command 
     * @return An instance of a type.
     */
    <T extends Object> T newType(String typeName, Class<T> clazz);

    /**
     * Create a new instance of the specified type. The clazz argument
     * relates to a type in the callers configuration which defines the
     * specifics of the type to be created.
     * @param clazz The class to return an instance for.
     * @return An instance of a type.
     */
    <T extends Object> T newType(Class<T> clazz);
    
    /**
     * Create a new instance of the specified type with modifier. The clazz and modifier 
     * arguments relate to a type in the callers configuration which defines the
     * specifics of the type to be created.
     * @param clazz The class to return an instance for.
     * @param modifier A modifier to be applied
     * @return An instance of a type.
     */
    <T extends Object> T newType(Class<T> clazz, String modifier);
    
    /**
     * Find and return a Builder (Factory) by its name. The builders available are defined
     * in the configure system and normally identified in the source tree by their use of the
     * Builder annotation.
     */
    AbstractFactory fetchBuilder(String builderName); 
}
