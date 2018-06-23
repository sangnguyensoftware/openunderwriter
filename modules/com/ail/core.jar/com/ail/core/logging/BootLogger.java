/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
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

package com.ail.core.logging;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ail.core.VersionEffectiveDate;

/**
 * The Core uses this class to log boot time messages. At boot time there is every chance that the normal logging
 * services offered by the core will not be available - as they rely on services and hence configurations having been
 * loaded. 
 * @version $Revision$
 */
public class BootLogger {
	private static SimpleDateFormat format=new SimpleDateFormat("dd/MM/yy hh:mm:ss");

	/**
     * Log a message
	 * @param caller The class making the call to the logger
	 * @param ved Effective date that the client is running with
	 * @param severity Severity of the error
	 * @param message Message to display
	 * @param cause Cause of the problem, this may be null.
	 */
    public static void log(Class<?> caller, VersionEffectiveDate ved, Severity severity, String message, Throwable cause) {
		// default to writing to error stream.
		PrintStream logTo=System.err;

		// direct DEBUG and INFO messages to System.out
		if (Severity.DEBUG.equals(severity) || Severity.INFO.equals(severity) || Severity.WARNING.equals(severity)) {
			logTo=System.out;
        }

		// Write the log message
		synchronized(format) {
            if (cause==null) {
                logTo.println(caller.getName()+":"+format.format(new Date())+":"+severity+":"+format.format(ved.getDate())+":"+message);
            }
            else {
                logTo.println(caller.getName()+":"+format.format(new Date())+":"+severity+":"+format.format(ved.getDate())+":"+message+" Cause follows:");
                cause.printStackTrace(logTo);
            }
		}
    }
}
