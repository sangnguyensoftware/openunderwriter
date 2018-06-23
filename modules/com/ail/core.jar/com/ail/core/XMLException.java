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
package com.ail.core;

import com.ail.core.BaseException;

/**
 * Exception class to notify exceptions encountered when
 * XML is being processed. 
 **/
public class XMLException extends BaseException {
    /**
     * Constructor
     * @param description A description of the exception.
     **/
    public XMLException(String description) {
		super(description);
    }

    /**
     * Constructor
     * @param description A description of the exception.
     * @param target The exception that cause this one to be thrown.
     */
    public XMLException(String description, Throwable target) {
        super(description, target);
    }
}
