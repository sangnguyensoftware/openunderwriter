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

public class RequestWrapper implements RequestAdaptor {
    public static final String UPLOAD_RESOURCE_ID = "UPLOAD_RESOURCE_ID";
    private RequestAdaptor adaptor;
    private String contentType;

    public RequestWrapper(RequestAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    @Override
    public Locale getLocale() {
        return adaptor.getLocale();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return adaptor.getParameterNames();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return adaptor.getParameterMap();
    }

    @Override
    public String getParameter(String name) {
        return adaptor.getParameter(name);
    }

    @Override
    public String getHeader(String name) {
        return adaptor.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return adaptor.getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return adaptor.getHeaders(name);
    }

    @Override
    public User getUser() throws PortalException, SystemException {
        return adaptor.getUser();
    }

    @Override
    public Company getCompany() throws PortalException, SystemException {
        return adaptor.getCompany();
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return adaptor.getServletRequest();
    }

    @Override
    public String getCurrentCompleteURL() {
        return adaptor.getCurrentCompleteURL();
    }

    public boolean isDocumentBeingUploaded() {
        return adaptor.isDocumentBeingUploaded(UPLOAD_RESOURCE_ID);
    }

    @Override
    public boolean isDocumentBeingUploaded(String uploadResourceId) {
        return adaptor.isDocumentBeingUploaded(uploadResourceId);
    }

    @Override
    public Document getDocumentBeingUploaded(String uploadFileName) throws IOException {
        return adaptor.getDocumentBeingUploaded(uploadFileName);
    }

    @Override
    public String getResponseContentType() {
        return contentType == null ? adaptor.getResponseContentType() : contentType;
    }

    @Override
    public String getServerName() {
        return adaptor.getServerName();
    }

    @Override
    public String getScheme() {
        return adaptor.getScheme();
    }

    @Override
    public int getServerPort() {
        return adaptor.getServerPort();
    }

    @Override
    public boolean isPortletRequest() {
        return adaptor.isPortletRequest();
    }

    @Override
    public boolean isAjaxRequest() {
        return adaptor.isAjaxRequest();
    }

    @Override
    public boolean isRestfulRequest() {
        return adaptor.isRestfulRequest();
    }

    @Override
    public PortletRequest getPortletRequest() {
        return adaptor.getPortletRequest();
    }

    @Override
    public Principal getUserPrincipal() {
        return adaptor.getUserPrincipal();
    }

    @Override
    public String getProperty(String name) {
        return adaptor.getProperty(name);
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
