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

import static com.ail.core.MimeType.APPLICATION_PDF;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import com.ail.core.CoreContext;
import com.ail.core.context.RequestAdaptor;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.upload.UploadRequest;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

/**
 * Implementation of the RequestAdaptor backed by an PortletRequest.
 */
public class PortletRequestAdaptor implements RequestAdaptor {
    PortletRequest portletRequest;

    public PortletRequestAdaptor(PortletRequest portletRequest) {
        this.portletRequest = portletRequest;
    }

    @Override
    public Locale getLocale() {
        return portletRequest.getLocale();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return portletRequest.getParameterNames();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return portletRequest.getParameterMap();
    }

    @Override
    public String getParameter(String name) {
        return portletRequest.getParameter(name);
    }

    @Override
    public String getHeader(String name) {
        return getServletRequest().getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return getServletRequest().getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return getServletRequest().getHeaders(name);
    }

    @Override
    public User getUser() throws PortalException, SystemException {
        return PortalUtil.getUser(portletRequest);
    }

    @Override
    public Company getCompany() throws PortalException, SystemException {
        return PortalUtil.getCompany(portletRequest);
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
    }

    @Override
    public String getCurrentCompleteURL() {
        return PortalUtil.getCurrentCompleteURL(getServletRequest());
    }

    @Override
    public boolean isDocumentBeingUploaded(String uploadResourceId) {
        if (portletRequest instanceof ResourceRequest) {
            ResourceRequest resourceRequest = (ResourceRequest) portletRequest;
            if (resourceRequest.getResourceID() != null && resourceRequest.getResourceID().contains(uploadResourceId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Document getDocumentBeingUploaded(String uploadFileName) throws IOException {
        ResourceRequest resourceRequest = (ResourceRequest)portletRequest;

        UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(resourceRequest);
        File objFile = uploadRequest.getFile(uploadFileName);
        String objFileName = uploadRequest.getFileName(uploadFileName);

        try (InputStream is = new FileInputStream(objFile)) {
            byte[] buff = new byte[is.available()];
            is.read(buff);
            return new Document(DocumentType.UNKNOWN, buff, "", objFileName, APPLICATION_PDF, CoreContext.getProductName());
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public String getResponseContentType() {
        return portletRequest.getResponseContentType();
    }

    @Override
    public String getServerName() {
        return getServletRequest().getServerName();
    }

    @Override
    public int getServerPort() {
        return getServletRequest().getServerPort();
    }

    @Override
    public String getScheme() {
        return getServletRequest().getScheme();
    }

    @Override
    public boolean isPortletRequest() {
        return true;
    }

    @Override
    public boolean isAjaxRequest() {
        return portletRequest instanceof ResourceRequest;
    }

    @Override
    public boolean isRestfulRequest() {
        return false;
    }

    @Override
    public PortletRequest getPortletRequest() {
        return portletRequest;
    }

    @Override
    public Principal getUserPrincipal() {
        return portletRequest.getUserPrincipal();
    }

    @Override
    public String getProperty(String name) {
        return portletRequest.getProperty(name);
    }
}
