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
package com.ail.core.context.servlet;

import javax.servlet.http.HttpSession;

import com.ail.core.context.SessionAdaptor;

/**
 * Implementation of the SessionAdaptor providing access to a
 * HttpServletSession.
 */
public class ServletSessionAdaptor implements SessionAdaptor {

    private HttpSession session;

    public ServletSessionAdaptor(HttpSession session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, Class<T> clazz) {
        return (T) session.getAttribute(name);
    }

    @Override
    public void set(String name, Object value) {
        session.setAttribute(name, value);
    }
}
