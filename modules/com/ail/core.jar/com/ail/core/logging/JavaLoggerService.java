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
import java.util.logging.Level;
import java.util.logging.Logger;


import com.ail.annotation.ServiceImplementation;
import com.ail.core.Service;

/**
 * This logging entry point redirects logs messages to the java logger.
 */
@ServiceImplementation
public class JavaLoggerService extends Service<LoggingService.LoggingArgument> {
    private static Map<Severity,Level> severityMapper=new HashMap<Severity,Level>();
    private IdentifyCallingClass identifyCallingClass = new IdentifyCallingClass();

    /**
     * Define the mapping from AIL's Severity logging levels to Java's. The mapping
     * here isn't perfect. Java lacks a level that matches our ERROR level, so we'll
     * map that to SEVERE too.
     */
    static {
        severityMapper.put(Severity.DEBUG, Level.FINEST);
        severityMapper.put(Severity.INFO, Level.INFO);
        severityMapper.put(Severity.WARNING, Level.WARNING);
        severityMapper.put(Severity.ERROR, Level.SEVERE);
        severityMapper.put(Severity.FATAL, Level.SEVERE);
    }
    
    Logger getLogger() {
        return Logger.getLogger(identifyCallingClass.callingClass().getName());
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
