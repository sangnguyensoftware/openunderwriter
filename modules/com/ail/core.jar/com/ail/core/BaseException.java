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

import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * This Exception class represents the base of all bob exceptions; which
 * indicate conditions within bob that applications might want to catch.
 **/
public abstract class BaseException extends Exception {
    private ArrayList<String> errors = null;

    /**
     * Constructor
     * 
     * @param description
     *            A description of the error.
     **/
    public BaseException(String description) {
        super(description);
    }

    /**
     * Constructor
     * 
     * @param description
     *            A description of the error.
     * @param target
     *            The exception that cause this exception to be thrown.
     **/
    public BaseException(String description, Throwable target) {
        super(description, target);
    }

    /**
     * Constructor Build a BaseException from a BaseError.
     * 
     * @param e
     *            BaseError to be converted.
     **/
    public BaseException(BaseError e) {
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

    /**
     * Add an error to the error list. A BaseException may list more that one
     * 'sub-error'.
     * 
     * @param s
     *            Error message
     **/
    public void addError(String s) {
        if (errors == null) {
            errors = new ArrayList<String>();
        }

        errors.add(s);
    }

    /**
     * Convert this exception into a string
     * 
     * @return String representation of the exception.
     **/
    public String toString() {
        String ret = getClass().getName() + ": " + getDescription();

        if (errors != null) {
            for (String s : errors) {
                ret += "\n" + s;
            }
        }

        return ret;
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
        super.initCause(target);
    }
}
