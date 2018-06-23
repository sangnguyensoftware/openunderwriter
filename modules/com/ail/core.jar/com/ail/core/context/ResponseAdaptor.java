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

import java.io.IOException;
import java.io.Writer;

import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface describing a common adaptor interface providing access to
 * response information.
 */
public interface ResponseAdaptor {
    void sendRedirect(String target) throws IOException;

    void addCookie(Cookie cookie);

    PortletResponse getPortletResponse();

    HttpServletResponse getServletResponse();

    String getNamespace();

    void setContentType(String contentType);

    Writer getWriter() throws IOException;

    String createActionURL();

    String createResourceURL();
}
