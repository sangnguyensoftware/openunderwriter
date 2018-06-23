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
package com.ail.core.context.portlet;

import javax.portlet.PortletConfig;
import javax.servlet.ServletConfig;

import com.ail.core.context.ConfigAdaptor;

public class PortletConfigAdaptor implements ConfigAdaptor {
    private PortletConfig portletConfig;

    public PortletConfigAdaptor(PortletConfig portletConfig) {
        this.portletConfig = portletConfig;
    }

    @Override
    public String getDefaultNamespace() {
        return portletConfig.getDefaultNamespace();
    }

    @Override
    public PortletConfig getPortletConfig() {
        return portletConfig;
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }
}
