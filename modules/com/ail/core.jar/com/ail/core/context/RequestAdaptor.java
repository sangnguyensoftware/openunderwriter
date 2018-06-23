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
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.ail.core.document.Document;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;

/**
 * Interface describing a common adaptor interface providing access to request
 * information.
 */
public interface RequestAdaptor {
    public static final String PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME = "pageflow.page";
    public static final String PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME = "pageflow.name";
    public static final String POLICY_PORTLET_REQUEST_PARAMETER_NAME = "pageflow.policy";
    public static final String PRODUCT_PORTLET_REQUEST_PARAMETER_NAME = "pageflow.product";

    Locale getLocale();

    Enumeration<String> getParameterNames();

    Map<String, String[]> getParameterMap();

    String getParameter(String name);

    String getProperty(String name);

    String getHeader(String name);

    Enumeration<String> getHeaderNames();

    Enumeration<String> getHeaders(String name);

    User getUser() throws PortalException, SystemException;

    Company getCompany() throws PortalException, SystemException;

    Principal getUserPrincipal();

    HttpServletRequest getServletRequest();

    PortletRequest getPortletRequest();

    String getCurrentCompleteURL();

    boolean isDocumentBeingUploaded(String uploadResourceId);

    Document getDocumentBeingUploaded(String uploadFileName) throws IOException;

    String getResponseContentType();

    String getServerName();

    int getServerPort();

    String getScheme();

    boolean isPortletRequest();

    boolean isAjaxRequest();

    boolean isRestfulRequest();
}
