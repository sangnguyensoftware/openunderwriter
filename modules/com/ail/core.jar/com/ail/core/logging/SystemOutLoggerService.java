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

package com.ail.core.logging;
import com.ail.annotation.ServiceImplementation;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import com.ail.core.Service;

/**
 * This logging entry point directs log messages to the System print streams 'out'
 * and 'err'.
 * Messages are output to a 'out' and 'err' depending on their severity. DEBUG and
 * INFO messages are written to 'out', WARNING, ERROR, and FATAL messages are
 * written to 'err'.<p>
 * Messages are written in the following form:<p>
 * &nbsp;<i>owner</i>:<i>date</i>:<i>severity</i>:<i>effective date</i>:<i>message</i>
 */
@ServiceImplementation
public class SystemOutLoggerService extends Service<LoggingService.LoggingArgument> {
	private static final SimpleDateFormat format=new SimpleDateFormat("dd/MM/yy hh:mm:ss");

	@Override
	public void invoke() {
		// default to writing to error stream.
		PrintStream logTo=System.err;

		// direct DEBUG and INFO messages to System.out
		if (Severity.DEBUG.equals(args.getSeverity()) || Severity.INFO.equals(args.getSeverity())) {
			logTo=System.out;
        }

		// Write the log message
		synchronized(format) {
		    if (args.getCause()==null) {
		        logTo.println(args.getCallersCore().getClass().getName()+":"+format.format(args.getDate())+":"+args.getSeverity()+":"+format.format(args.getCallersCore().getVersionEffectiveDate().getDate())+":"+args.getMessage());
            }
            else {
                logTo.println(args.getCallersCore().getClass().getName()+":"+format.format(args.getDate())+":"+args.getSeverity()+":"+format.format(args.getCallersCore().getVersionEffectiveDate().getDate())+":"+args.getMessage()+" Cause follows:");
                args.getCause().printStackTrace(logTo);
            }
		}
    }
}
