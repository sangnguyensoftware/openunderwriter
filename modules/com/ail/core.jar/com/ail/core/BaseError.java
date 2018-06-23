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

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * This Error class represents the base of all bob errors; which indicate
 * conditions within bob that applications generally do not want to catch. They
 * represent abnormal conditions that cannot be recovered from.
 * <p>
 * These error's may not be fatal to the application, but they do indicate some
 * kind of system failure rather than a failure of business logic.
 **/
public abstract class BaseError extends Error {

    /**
     * Constructor
     * 
     * @param description
     *            A description of the error.
     **/
    public BaseError(String description) {
        super(description);
    }

    /**
     * Constructor
     * 
     * @param description
     *            A description of the error.
     * @param target
     *            The exception that caused this error to be thrown.
     */
    public BaseError(String description, Throwable target) {
        super(description, target);
    }

    /**
     * Constructor Turn a BaseException into a BaseError
     * 
     * @param e
     *            BaseException to convert.
     **/
    public BaseError(BaseException e) {
        super(e.getDescription(), e);
    }

    /**
     * Get the error description. This description is a textual description of
     * the error.
     * 
     * @return description
     **/
    public String getDescription() {
        return super.getMessage();
    }

    public String getMessage() {
        return super.getMessage();
    }

    /**
     * Return a string representing this error.
     * 
     * @return String representation of the error.
     **/
    public String toString() {
        return getClass().getName() + ": " + getDescription();
    }

    public void printStackTrace() {
        if (getCause() != null) {
            getCause().printStackTrace();
        } else {
            super.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream stream) {
        if (getCause() != null) {
            getCause().printStackTrace(stream);
        } else {
            super.printStackTrace(stream);
        }
    }

    public void printStackTrace(PrintWriter writer) {
        if (getCause() != null) {
            getCause().printStackTrace(writer);
        } else {
            super.printStackTrace(writer);
        }
    }

    /**
     * @return the target
     */
    public Throwable getTarget() {
        return getCause();
    }

    /**
     * @param target
     *            the target to set
     */
    public void setTarget(Throwable target) {
        initCause(target);
    }
}
