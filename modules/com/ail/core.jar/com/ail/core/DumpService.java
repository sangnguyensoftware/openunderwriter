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

package com.ail.core;

import java.lang.reflect.Method;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.command.Argument;

/**
 * This service can be used in place of any other service. It writes the
 * arguments associated with the command being executed to the console and
 * returns without further action.
 */
@ServiceImplementation(serviceName = "DumpService")
public class DumpService extends Service<Argument> {
    public void invoke() {
        Method[] methods = getArgs().getClass().getMethods();

        core.logInfo("DumpService called for service argument of type: "+getArgs().getClass().getName());
        
        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getName().endsWith("Arg")) {
                try {
                    Object arg=method.invoke(getArgs());
                    core.logInfo("Argument: "+method.getName()+", value: "+arg);
                } catch (Exception e) {
                    core.logWarning("Failed to dump argument: "+method.getName()+" because of "+e.getMessage());
                }
            }
        }
    }
}
