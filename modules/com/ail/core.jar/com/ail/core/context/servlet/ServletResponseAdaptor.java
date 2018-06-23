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

import java.io.IOException;
import java.io.Writer;

import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ail.core.NotImplementedError;
import com.ail.core.context.ResponseAdaptor;

public class ServletResponseAdaptor implements ResponseAdaptor {
    public HttpServletResponse httpServletResponse;

    public ServletResponseAdaptor(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void sendRedirect(String target) throws IOException {
        httpServletResponse.sendRedirect(target);
    }

    @Override
    public void addCookie(Cookie cookie) {
        httpServletResponse.addCookie(cookie);
    }

    @Override
    public PortletResponse getPortletResponse() {
        throw new NotImplementedError("ServletResponseAdaptor does not define handling for getPortletResponse()");
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return httpServletResponse;
    }

    @Override
    public String getNamespace() {
        throw new NotImplementedError("ServletResponseAdaptor does not define handling for getNamespace()");
    }

    @Override
    public void setContentType(String contentType) {
        httpServletResponse.setContentType(contentType);
    }

    @Override
    public Writer getWriter() throws IOException {
        return httpServletResponse.getWriter();
    }

    @Override
    public String createActionURL() {
        throw new NotImplementedError("ServletResponseAdaptor does not define handling for createActionUrl()");
    }

    @Override
    public String createResourceURL() {
        throw new NotImplementedError("ServletResponseAdaptor does not define handling for createResourceURL()");
    }
}
