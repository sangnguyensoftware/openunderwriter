/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.core.context;

import javax.portlet.PortletConfig;
import javax.servlet.ServletConfig;

public class ConfigWrapper implements ConfigAdaptor {

    private ConfigAdaptor configAdaptor;

    public ConfigWrapper(ConfigAdaptor configAdaptor) {
        this.configAdaptor = configAdaptor;
    }

    @Override
    public String getDefaultNamespace() {
       return configAdaptor.getDefaultNamespace();
    }

    @Override
    public PortletConfig getPortletConfig() {
        return configAdaptor.getPortletConfig();
    }

    @Override
    public ServletConfig getServletConfig() {
        return configAdaptor.getServletConfig();
    }
}
