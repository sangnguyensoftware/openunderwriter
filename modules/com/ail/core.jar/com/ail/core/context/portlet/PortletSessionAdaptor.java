/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

import javax.portlet.PortletSession;

import com.ail.core.context.SessionAdaptor;

/**
 * Implementation of the SessionAdaptor backed by a PortletSession.
 */
public class PortletSessionAdaptor implements SessionAdaptor {
    private PortletSession portletSession;

    public PortletSessionAdaptor(PortletSession portletSession) {
        this.portletSession = portletSession;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Object> T get(String name, Class<T> clazz) {
        return (T) portletSession.getAttribute(name);
    }

    @Override
    public void set(String name, Object value) {
        portletSession.setAttribute(name, value);
    }
}
