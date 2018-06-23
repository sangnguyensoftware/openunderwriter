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

package com.ail.core.xmlbinding;

import com.ail.core.XMLException;
import com.ail.core.XMLString;

/**
 * This interface describes the contract between the Core class and the
 * XML binding framework.
 */
public interface XMLBinding {
	/**
     * Convert an XMLString into an object of the Type that it represents.
	 * @param clazz The class represented by the <code>xml</code> argument.
     * @param xml The XML representation of the object to be unmarshalled.
	 * @return An instance of <code>clazz</code> representing <code>xml</code>
	 * @throws XMLException If there is a problem parsing the XML
     * @throws VersionException If the request version is not defined.
     */
	<T extends Object> T fromXML(Class<T> clazz, XMLString xml) throws XMLException;

	/**
     * Convert a type into its XML representation.
     * @param type The object to be marshalled
     * @throws VersionException If the requested version either is not defined.
     */
    XMLString toXML(Object type);
}
