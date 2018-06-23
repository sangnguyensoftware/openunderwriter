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


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Service;

/**
 * This logging entry point redirects logs messages to log4j.
 */
@ServiceImplementation
public class Log4JLoggerService extends Service<LoggingService.LoggingArgument> {
    private static Map<Severity,Level> severityMapper=new HashMap<Severity,Level>();
    private IdentifyCallingClass identifyCallingClass = new IdentifyCallingClass();

    static {
        severityMapper.put(Severity.DEBUG, Level.DEBUG);
        severityMapper.put(Severity.INFO, Level.INFO);
        severityMapper.put(Severity.WARNING, Level.WARN);
        severityMapper.put(Severity.ERROR, Level.ERROR);
        severityMapper.put(Severity.FATAL, Level.FATAL);
    }
    
    Logger getLogger() {
        return Logger.getLogger(identifyCallingClass.callingClass());
    }
    
    @Override
    public void invoke() {
        if (args.getCause()!=null) {
            getLogger().log(severityMapper.get(args.getSeverity()), args.getMessage(), args.getCause());
        }
        else {
            getLogger().log(severityMapper.get(args.getSeverity()), args.getMessage());
        }
    }
}
