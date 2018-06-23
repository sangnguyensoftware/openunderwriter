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

package com.ail.core;

import org.apache.commons.jxpath.JXPathException;

/**
 * The TypeXPathException indicates that an xpath was evaluated on a Type that
 * could not support it - i.e. the xpath could not be evaluated on the type.
 */
public class TypeXPathException extends BaseError {
	/**
     * Constructor
     * @param description A description of the pre-condition that has been
     * violated.
     */
    public TypeXPathException(String description) {
        super(description);
    }

	/**
	 * Constructor
	 * @param exception Create a TypeXPathException based on a JXPathException.
	 */
	public TypeXPathException(JXPathException exception) {
		super(exception.getLocalizedMessage());
	}
}
