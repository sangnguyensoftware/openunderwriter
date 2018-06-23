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
package com.ail.core;

import javax.ejb.EJBException;

/**
 * This exceptions is used to pass server side BaseErrors and BaseExceptions back
 * to the client as an EJBException - so the container will rollback etc.
 */
public class BaseServerException extends EJBException {
    private BaseException exception=null;
    private BaseError error=null;

    /**
     * Create an instance wrapping a BaseError
     * @param error
     */
    public BaseServerException(BaseError error) {
        this.error=error;
    }

    /**
     * Create an instance wrapping a BaseException
     * @param exception
     */
    public BaseServerException(BaseException exception) {
        this.exception=exception;
    }

    /**
     * Get the wrapped exception/error that caused this to the thrown.
     * @return An instance of a BaseError or BaseException.
     */
    public Throwable getCause() {
        if (exception!=null) {
            return exception;
        }
        else {
            return error;
        }
    }

    /**
     * Rethrow the BaseException/BaseError that caused this BaseServerException
     * to be thrown.
     * @throws BaseException If the cause was a BaseException
     */
    public void throwCause() throws BaseException {
        if (exception!=null) {
            throw exception;
        }
        else if (error!=null) {
            throw error;
        }
    }

    /**
     * Get the BaseException that caused this BaseServerException to be thrown.
     * @return BaseExeption or null if the cause was a BaseError
     */
    public BaseException getCauseException() {
        return exception;
    }

    /**
     * Get the BaseError that caused this BaseServerException to be thrown.
     * @return BaseError or null if the case was a BaseException.
     */
    public BaseError getCauseError() {
        return error;
    }

    /**
     * Report the underlying BaseError/BaseException message
     * @return String
     */
    public String toString() {
        if (exception!=null) {
            return exception.toString();
        }
        else {
            return error.toString();
        }
    }
}
