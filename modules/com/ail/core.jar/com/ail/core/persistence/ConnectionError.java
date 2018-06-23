/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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
package com.ail.core.persistence;

import com.ail.core.BaseError;
import com.ail.core.BaseException;

/**
 * Error indicating that connection to the persistence store could not be completed. The error indicates some form of
 * configuration problem.
 */
public class ConnectionError extends BaseError {
    /**
     * Constructor
     * @param description A description of the error.
     **/
    public ConnectionError(String description) {
        super(description);
    }

    /**
     * Constructor
     * @param description A description of the error.
     * @param target The exception that caused this error to be thrown.
     */
    public ConnectionError(String description, Throwable target) {
        super(description, target);
    }

    /**
     * Constructor
     * Turn a BaseException into a BaseError
     * @param e BaseException to convert.
     **/
    public ConnectionError(BaseException e) {
        super(e);
    }
}
