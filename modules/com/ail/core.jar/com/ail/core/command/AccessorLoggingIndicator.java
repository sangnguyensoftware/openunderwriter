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
package com.ail.core.command;

import com.ail.core.Functions;

/**
 * Accessors of the facility to output a message to the log giving details of
 * how the service they are accessing is being use. The logging indicator is
 * set in the service's configuration to one of:<ol>
 * <li><b>None</b> No logging will take place (the default).</li>
 * <li><b>Call</b> Log that a call to the service was made, but include no details.</li>
 * <li><b>Full</b> Include full details of the arguments passed to the service, and the results returned.</li>
 * </ol>
 */
public enum AccessorLoggingIndicator {
    /** NONE - perform no logging */
    NONE("None"), 
    /** CALL - Log the values passed into the service call */
    CALL("Call"),
    /** FULL - Log the values passed into the service call and those returned */
    FULL("Full");
    
    private final String longName;
    
    AccessorLoggingIndicator(String longName) {
        this.longName=longName;
    }
    
    public String getLongName() {
        return longName;
    }
    
    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }
}

