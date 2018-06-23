/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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

package com.ail.core.configure.server;

import com.ail.core.BaseError;
import com.ail.core.BaseException;

/**
 * This error is thrown in the event of a system error during the processing of a car (configuration archive) file.
 */
public class CarProcessingError extends BaseError {

    public CarProcessingError(String description) {
        super(description);
    }

    public CarProcessingError(String description, Throwable target) {
        super(description, target);
    }

    public CarProcessingError(BaseException e) {
        super(e);
    }
}
