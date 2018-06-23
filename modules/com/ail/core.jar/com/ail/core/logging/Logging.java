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

package com.ail.core.logging;

/**
 * This interface defines the contract between the Core class and the logging
 * sub-system. The Core is expected to expose the methods defined here, and
 * this package will expose the required entry points.<p>
 * Four error levels are defined:<ol>
 * <li><b>debug</b> - Messages that are intended to help developers debug
 * a system that is not performing as expected.</li>
 * <li><b>info</b> - Messages that are aimed at monitoring systems. Typically
 * these are "I am X, and I am doing Y" messages.</li>
 * <li><b>warning</b> - These messages indicate that something went wrong, but
 * processing could continue.</li>
 * <li><b>error</b> - The same as warning, but requires investigation.</li>
 * <li><b>fatal</b> - The same as error, but processing could not continue.</li>
 * </ol>
 * <p>Clients of the logger can assume that their messages will have certain
 * information added to them; date-time, security context, and code location.</p>
 * @version $Revision: 1.4 $
 */
public interface Logging {
	/**
     * Output a message to the Debug logging channel.
     * Messages written to this channel are of interest to developers, and to
     * anyone trying to debug a system problem. The channel would generally
     * only be turned on when a problem is being investigated.
     * @param message The text of the message to be output.
     */
	void logDebug(String message);

	/**
     * Output a message to the Debug logging channel with exception details.
     * Messages written to this channel are of interest to developers, and to
     * anyone trying to debug a system problem. The channel would generally
     * only be turned on when a problem is being investigated.
     * @param message The text of the message to be output.
     * @param cause The cause of the debug message.
     */
    void logDebug(String message, Throwable cause);

	/**
     * Output a message to the Info logging channel.
	 * This channel is designed to take messages that are of interest during
     * normal operations. For example, "System ready", "Configuration reloaded".
     * @param message The text of the message to be output.
     */
    void logInfo(String message);

    /**
     * Output a message to the Info logging channel with exception details.
     * This channel is designed to take messages that are of interest during
     * normal operations. For example, "System ready", "Configuration reloaded".
     * @param message The text of the message to be output.
     * @param cause The cause of the info message.
     */
    void logInfo(String message, Throwable cause);

	/**
     * Output a message to the Warning logging channel.
	 * Messages written to this channel indicate that something unexpected
     * occurred, but that it was dealt with and is not thought (by the developer)
     * to be if great importance.
     * @param message The text of the message to be output.
     */
    void logWarning(String message);

    /**
     * Output a message to the Warning logging channel with exception details.
     * Messages written to this channel indicate that something unexpected
     * occurred, but that it was dealt with and is not thought (by the developer)
     * to be if great importance.
     * @param message The text of the message to be output.
     * @param cause The cause of the warning message.
     */
    void logWarning(String message, Throwable cause);

	/**
     * Output a message to the Error logging channel.
	 * The error channel is reserved for messages that describe serious
     * system problems. The problem didn't stop processing, but is significant
     * enough to require investigation.
     * @param message The text of the message to be output.
     */
    void logError(String message);

    /**
     * Output a message to the Error logging channel with an exception.
     * The error channel is reserved for messages that describe serious
     * system problems. The problem didn't stop processing, but is significant
     * enough to require investigation.
     * @param message The text of the message to be output.
     * @param cause The cause of the error.
     */
    void logError(String message, Throwable cause);
    
	/**
     * Output a message to the Fatal logging channel.
	 * An error is fatal if it stops the operation being processed. For example,
     * if the systems configuration information is defined in an inconsistent way
     * a fatal error is generated.
     * @param message The text of the message to be output.
     */
    void logFatal(String message);

    /**
     * Output a message to the Fatal logging channel with an exception.
     * An error is fatal if it stops the operation being processed. For example,
     * if the systems configuration information is defined in an inconsistent way
     * a fatal error is generated.
     * @param message The text of the message to be output.
     * @param cause The cause of the fatal error.
     */
    void logFatal(String message, Throwable cause);
    
    /**
     * The logger to be called if logging through normal means fails for any
     * reason. If the system is in a state where the normal logging channels
     * fail, then this method is used as a last resort.
     * @param message
     * @param cause
     * @param severity
     */
    void logUnloggable(Severity severity, String message, Throwable cause);
}
