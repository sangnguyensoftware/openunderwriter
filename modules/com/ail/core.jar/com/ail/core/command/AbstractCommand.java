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

package com.ail.core.command;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.configure.Configuration;

/**
 * This is the super class of all command objects.
 */
public abstract class AbstractCommand extends Type {
	/**
 	 * Update this CommandImpl object's arguments with those taken from the command
     * arg passed in. This is in effect a bulk setter. This interface will be
     * implemented by beans that have many more getters/setters to support
     * their properties. The implementations of this method take 'that', and
     * pull all the relevant properties into this.
     * @param that Source for arguments
     */
    public abstract void setArgs(Argument that);

	/**
     * Get the argument set associated with this command.
     * @return This objects arguments.
     */
	public abstract Argument getArgs();

	/**
     * Invoke the command associated with this command object.
     * Before invoke is called, the command object must be populated with
     * all the information (parameters) needed to invoke the service via
     * the command object's setters. Once invoke has been called, the
     * object's getters are used to retrieve the results.
     */
    public abstract void invoke() throws BaseException;

	/**
     * Fetch the configuration of the entry point associated with this command.
     * This is similar to invoke in that control is passed to the entry point
     * object, in this case to the getConfiguration method.
     * Note: This method does not return the configuration of the command
     * object.
     * @return The entry point's configuration.
	 */
    public abstract Configuration getConfiguration();

	/**
     * Update the configuration of the entry point associated with this command.
     * This is similar to the invoke method in that control is pass to the
     * entry point object, in this case to its setConfiguration method.
     * Note: This method does not set the configuration of the command object.
     * @param properties Properties to replace the current configuration.
     */
    public abstract void setConfiguration(Configuration properties);
}
