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

import static com.ail.core.CoreContext.setRestfulRequestPostData;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.ail.core.NotImplementedError;
import com.ail.core.context.RequestAdaptor;
import com.ail.core.document.Document;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;

/**
 * Implementation of the RequestAdaptor backed by an HttpServletRequest.
 */
public class ServletRequestAdaptor implements RequestAdaptor {
    private HttpServletRequest httpServletRequest;
    private Map<String, String[]> parameters = new HashMap<>();

    public ServletRequestAdaptor(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;

        if (isJsonBodyRequest(httpServletRequest)) {
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(httpServletRequest.getInputStream(), writer, "UTF-8");
                String jsonData = writer.toString();
                parameters.putAll(convertJsonParameterToParameters(jsonData));
                setRestfulRequestPostData(jsonData);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            if (httpServletRequest.getParameterMap().containsKey("json")) {
                String jsonData = httpServletRequest.getParameter("json");
                parameters.putAll(convertJsonParameterToParameters(jsonData));
                setRestfulRequestPostData(jsonData);
            } else {
                parameters.putAll(httpServletRequest.getParameterMap());
                setRestfulRequestPostData(buildFakeJsonFromParams(httpServletRequest));
            }
        }
    }

    private String buildFakeJsonFromParams(HttpServletRequest httpServletRequest) {
        StringBuffer fakeJson = new StringBuffer();
        fakeJson.append("{");
        httpServletRequest.getParameterMap().forEach((k, v) -> {
            if (fakeJson.length()!=1) {
                fakeJson.append(",");
            }
            fakeJson.append("\"").append(k).append("\":\"").append(v).append("\"");
        });
        fakeJson.append("}");
        return fakeJson.toString();
    }

    boolean isJsonBodyRequest(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getContentType() != null && httpServletRequest.getContentType().contains("application/json") && "/pageflow".equals(httpServletRequest.getContextPath());
    }

    protected Map<? extends String, ? extends String[]> convertJsonParameterToParameters(String jsonParameters) {
        try {
            Map<String, String[]> result = new HashMap<>();

            JsonNode root = new ObjectMapper().readTree(jsonParameters);

            for (Iterator<String> names = root.getFieldNames(); names.hasNext();) {
                String name = names.next();
                String value = root.get(name).getTextValue();
                parameters.put(name, new String[] { value });
            }

            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Locale getLocale() {
        return httpServletRequest.getLocale();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getParameter(String name) {
        return parameters.containsKey(name) ? parameters.get(name)[0] : null;
    }

    @Override
    public String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return httpServletRequest.getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return httpServletRequest.getHeaders(name);
    }

    @Override
    public User getUser() {
        throw new NotImplementedError("ServletRequestAdaptor does not define handling for getUser()");
    }

    @Override
    public Company getCompany() {
        throw new NotImplementedError("ServletRequestAdaptor does not define handling for getCompany()");
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return httpServletRequest;
    }

    @Override
    public String getCurrentCompleteURL() {
        throw new NotImplementedError("ServletRequestAdaptor does not define handling for getCurrentCompleteURL()");
    }

    @Override
    public boolean isDocumentBeingUploaded(String uploadResourceId) {
        // A product service should be used for doc uploads in the context of restful
        // pageflows.
        return false;
    }

    @Override
    public Document getDocumentBeingUploaded(String uploadFileName) throws IOException {
        throw new NotImplementedError("ServletRequestAdaptor does not define handling for getDocumentBeingUploaded()");
    }

    @Override
    public String getResponseContentType() {
        return APPLICATION_JSON;
    }

    @Override
    public String getServerName() {
        return httpServletRequest.getServerName();
    }

    @Override
    public int getServerPort() {
        return httpServletRequest.getServerPort();
    }

    @Override
    public boolean isPortletRequest() {
        return false;
    }

    @Override
    public boolean isAjaxRequest() {
        return false;
    }

    @Override
    public boolean isRestfulRequest() {
        return true;
    }

    @Override
    public PortletRequest getPortletRequest() {
        throw new NotImplementedError("ServletRequestAdaptor does not define handling for getPortletRequest()");
    }

    @Override
    public Principal getUserPrincipal() {
        return httpServletRequest.getUserPrincipal();
    }

    @Override
    public String getProperty(String name) {
        // Returning a null here is safe. HttpServletRequest does not support
        // the concept of properties and PortletRequest allows them to be null.
        return null;
    }

    @Override
    public String getScheme() {
        return httpServletRequest.getScheme();
    }

}
