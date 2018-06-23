/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */
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

package com.ail.core.jsonmapping;

import com.ail.core.JSONException;

/**
 * This interface describes the contract between the Core class and the
 * JSON binding framework.
 */
public interface JSONBinding {
	/**
     * Convert an JSON string into an object of the Type that it represents.
	 * @param clazz The class represented by the <code>json</code> argument.
     * @param json The JSON representation of the object to be unmarshalled.
	 * @return An instance of <code>clazz</code> representing <code>json</code>
	 * @throws JSONException If there is a problem parsing the XML
     */
	<T extends Object> T fromJSON(Class<T> clazz, String json) throws JSONException;

	/**
     * Convert a type into its JSON representation.
     * @param type The object to be marshalled
	 * @throws JSONException
     */
    String toJSON(Object type) throws JSONException;
}
