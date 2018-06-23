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

package com.ail.core.command;

import com.ail.core.BaseError;
import com.ail.core.BaseException;

/**
 * This error is thrown by command accessors when they fail to contact the
 * command they relate to.
 */
public class AccessorError extends BaseError {
    public AccessorError(String description) {
        super(description);
    }

    /**
     * Constructor
     * Turn a BaseException into a AccessorError
     * @param e BaseException to convert.
     **/
    public AccessorError(BaseException e) {
        super(e);
    }
}
