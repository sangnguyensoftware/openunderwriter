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

import java.io.IOException;
import java.io.Writer;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ail.core.context.ResponseAdaptor;
import com.liferay.portal.util.PortalUtil;

public class PortletResponseAdaptor implements ResponseAdaptor {
    PortletResponse portletResponse;

    public PortletResponseAdaptor(PortletResponse portletResponse) {
        this.portletResponse = portletResponse;
    }

    @Override
    public void sendRedirect(String target) throws IOException {
        PortalUtil.getHttpServletResponse(portletResponse).sendRedirect(target);
    }

    @Override
    public void addCookie(Cookie cookey) {
        PortalUtil.getHttpServletResponse(portletResponse).addCookie(cookey);
    }

    @Override
    public PortletResponse getPortletResponse() {
        return portletResponse;
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return PortalUtil.getHttpServletResponse(portletResponse);
    }

    @Override
    public String getNamespace() {
        return portletResponse.getNamespace();
    }

    @Override
    public void setContentType(String contentType) {
        ((MimeResponse) portletResponse).setContentType(contentType);
    }

    @Override
    public Writer getWriter() throws IOException {
        return ((MimeResponse) portletResponse).getWriter();
    }

    @Override
    public String createActionURL() {
        return ((MimeResponse) portletResponse).createActionURL().toString();
    }


    @Override
    public String createResourceURL() {
        return ((MimeResponse) portletResponse).createResourceURL().toString();
    }
}
