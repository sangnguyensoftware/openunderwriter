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
package com.ail.core;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Check if the server is warmed up and ready to process requests. The command
 * server should not attempt to process any messages until the server is full
 * warmed up. Until then we cannot be sure that the services which the commands
 * being executed depend on are actually available.
 */
public class ServerWarmChecker {
    private boolean warmedUp = false;

    public boolean isServerWarmedUp() {
        if (!warmedUp) {
            try {
                if (isLiferayStarted()) {
                    warmedUp = true;
                }
            } catch (Throwable t) {
                // Any exception means the server is not ready - leave
                // "warmedUp" false.
            }
        }

        return warmedUp;
    }

    private boolean isLiferayStarted() throws Throwable {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("jboss.as:deployment=ROOT.war");

        String status = (String) (server.getAttribute(name, "status"));
        Boolean enabled = (Boolean) (server.getAttribute(name, "enabled"));

        return "OK".equals(status) && enabled;
    }
}
