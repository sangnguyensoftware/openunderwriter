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

package com.ail.core.configure;

import java.util.Properties;

/**
 * This interface describes the methods that the Core is expected to expose
 * for clients to access configuration settings, and which this package
 * supports.<p>
 * This interface is one half of a pair, the other being ConfigurationOwner.
 * Client code (code using the core) would be expected to use the interface
 * described here to access the values of their settings. They should use
 * ConfigurationOwner to access the configuration settings as a whole, and
 * to make changes to settings.
 */
public interface Configure {
	/**
     * Retrieve a specific parameter by name.
     * The name may be dot separated to drill down through
     * nested groups.
     * @param name Parameter name.
     * @return The parameter associated with the name, or null if the parameter is not defined.
     */
	Parameter getParameter(String name);

    /**
     * Get the value of a parameter by name. The name may be dot separated to
     * drill down through nested groups. The following two bits of code are exactly
     * equivalent with the exception that the second will not fail with a NullPointerException is
     * "parameterName" is undefined:<br>
     * <code>
     * String x=getParameter("parameterName").getValue();
     * <br>
     * String x=getParameterValue("parameterName");
     * </code><br>
     * @param name Name of the parameter to fetch the value for
     * @return Value of the parameter, or null if it is undefined.
     */
    String getParameterValue(String name);

    /**
     * Return the value of a parameter or a default value if the parameter is undefined.
     * @param name Name of the parameter to return the value of.
     * @param defaultValue If the parameter is undefined return this instead
     * @return The value of the parameter, of <code>default</code> if it is undefined.
     */
    String getParameterValue(String name, String defaultValue);

	/**
     * Retrieve a specific group by name.
     * The name may be dot separated to drill down through nested
     * groups.
     * @param name The group's name.
     * @return The located group, .
     */
    Group getGroup(String name);

	/**
     * Retrieve a group of Parameters as Properties.
     * This is a utility method which returns the Parameters found in a Group
     * as a java.util.Properties.
     * @param name The name of the group to convert.
     * @return The Parameters in the group as Properties
     */
	Properties getParametersAsProperties(String name);
}
