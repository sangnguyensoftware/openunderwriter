/* Copyright Applied Industrial Logic Limited 2008. All rights reserved. */
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
package com.ail.core.urlhandler.product;

import com.ail.core.BaseError;
import com.ail.core.PostconditionException;

/**
 * Post condition exception thrown by product hander service implementations when
 * they cannot find the product resource being referenced.
 */
public class FileNotFoundPostcondition extends PostconditionException {

    public FileNotFoundPostcondition(BaseError e) {
        super(e);
    }

    public FileNotFoundPostcondition(String description, Throwable target) {
        super(description, target);
    }

    public FileNotFoundPostcondition(String description) {
        super(description);
    }
}
