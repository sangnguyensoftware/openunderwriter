/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.core;

import static com.ail.core.product.Product.AGGREGATOR_CONFIGURATION_PARAMETER_NAME;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ail.core.context.ConfigWrapper;
import com.ail.core.context.PreferencesWrapper;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.ResponseWrapper;
import com.ail.core.context.SessionWrapper;
import com.ail.core.context.portlet.PortletConfigAdaptor;
import com.ail.core.context.portlet.PortletPreferencesAdaptor;
import com.ail.core.context.portlet.PortletRequestAdaptor;
import com.ail.core.context.portlet.PortletResponseAdaptor;
import com.ail.core.context.portlet.PortletSessionAdaptor;
import com.ail.core.context.servlet.ServletConfigAdaptor;
import com.ail.core.context.servlet.ServletPreferencesAdaptor;
import com.ail.core.context.servlet.ServletRequestAdaptor;
import com.ail.core.context.servlet.ServletResponseAdaptor;
import com.ail.core.context.servlet.ServletSessionAdaptor;
import com.ail.core.logging.ServiceRequestRecord;

public class CoreContext {
    private static ThreadLocal<SessionWrapper> sessionWrapper = new ThreadLocal<>();
    private static ThreadLocal<RequestWrapper> requestWrapper = new ThreadLocal<>();
    private static ThreadLocal<ResponseWrapper> responseWrapper = new ThreadLocal<>();
    private static ThreadLocal<PreferencesWrapper> preferencesWrapper = new ThreadLocal<>();
    private static ThreadLocal<ConfigWrapper> configWrapper = new ThreadLocal<>();
    private static ThreadLocal<StringBuffer> productNameThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<CoreProxy> coreProxyThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Principal> userPrincipal = new ThreadLocal<>();

    public static void initialise(RequestWrapper request, ResponseWrapper response, SessionWrapper session, PreferencesWrapper preferences, ConfigWrapper config) {
        sessionWrapper.set(session);
        requestWrapper.set(request);
        responseWrapper.set(response);
        preferencesWrapper.set(preferences);
        configWrapper.set(config);
        userPrincipal.set(request!=null ? request.getUserPrincipal() : null);
        setCoreProxy(new CoreProxy());
    }

    public static void initialise() {
        sessionWrapper.set(new SessionWrapper(new MapAttributeHandler()));
        requestWrapper.set(null);
        responseWrapper.set(null);
        preferencesWrapper.set(null);
        configWrapper.set(null);
        userPrincipal.set(null);
        setCoreProxy(new CoreProxy());
    }

    public static void initialise(PortletRequest request, PortletResponse response, PortletConfig portalConfig) {
        sessionWrapper.set(new SessionWrapper(new PortletSessionAdaptor(request.getPortletSession())));
        requestWrapper.set(new RequestWrapper(new PortletRequestAdaptor(request)));
        responseWrapper.set(new ResponseWrapper(new PortletResponseAdaptor(response)));
        preferencesWrapper.set(new PreferencesWrapper(new PortletPreferencesAdaptor(request)));
        configWrapper.set(new ConfigWrapper(new PortletConfigAdaptor(portalConfig)));
        userPrincipal.set(request!=null ? request.getUserPrincipal() : null);
        setCoreProxy(new CoreProxy());
    }

    public static void initialise(HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig) {
        sessionWrapper.set(new SessionWrapper(new ServletSessionAdaptor(request.getSession())));
        requestWrapper.set(new RequestWrapper(new ServletRequestAdaptor(request)));
        responseWrapper.set(new ResponseWrapper(new ServletResponseAdaptor(response)));
        preferencesWrapper.set(new PreferencesWrapper(new ServletPreferencesAdaptor(request)));
        configWrapper.set(new ConfigWrapper(new ServletConfigAdaptor(servletConfig)));
        userPrincipal.set(request!=null ? request.getUserPrincipal() : null);
        setCoreProxy(new CoreProxy());
    }

    public static void destroy() {
        requestWrapper.remove();
        responseWrapper.remove();
        coreProxyThreadLocal.remove();
        productNameThreadLocal.remove();
    }

    public static void restart() {
        if (getCoreProxy().getParameterValue(AGGREGATOR_CONFIGURATION_PARAMETER_NAME) != null) {
            setProductName(getCoreProxy().getParameterValue(AGGREGATOR_CONFIGURATION_PARAMETER_NAME));
        }

        getSessionWrapper().setSessionTemp(null);
    }

    protected static void clear() {
        getSessionWrapper().clear();
        restart();
    }

    public static CoreProxy getCoreProxy() {
        return coreProxyThreadLocal.get();
    }

    public static void setCoreProxy(CoreProxy coreProxyArg) {
        coreProxyThreadLocal.set(coreProxyArg);
    }

    public static RequestWrapper getRequestWrapper() {
        return requestWrapper.get();
    }

    public static void setRequestWrapper(RequestWrapper request) {
        requestWrapper.set(request);
    }

    public static ResponseWrapper getResponseWrapper() {
        return responseWrapper.get();
    }

    public static void setResponseWrapper(ResponseWrapper response) {
        responseWrapper.set(response);
    }

    public static ConfigWrapper getConfigWrapper() {
        return configWrapper.get();
    }

    public static void setConfigWrapper(ConfigWrapper config) {
        configWrapper.set(config);
    }

    public static PreferencesWrapper getPreferencesWrapper() {
        return preferencesWrapper.get();
    }

    public static void setPreferencesWrapper(PreferencesWrapper preferences) {
        preferencesWrapper.set(preferences);
    }

    /**
     * @deprecated use {@link #getRequestWrapper()} instead
     */
    @Deprecated
    public static PortletRequest getRequest() {
        return requestWrapper.get().getPortletRequest();
    }

    /**
     * @deprecated use {@link #setRequestWrapper(RequestWrapper)} instead
     */
    @Deprecated
    public static void setPortletRequest(PortletRequest request) {
        setRequestWrapper(new RequestWrapper(new PortletRequestAdaptor(request)));
    }

    /**
     * @deprecated use {@link #getResponseWrapper()} instead
     */
    @Deprecated
    public static PortletResponse getResponse() {
        return getResponseWrapper().getPortletResponse();
    }

    /**
     * @deprecated use {@link #setResponseWrapper(ResponseWrapper)} instead
     */
    @Deprecated
    public static void setPortletResponse(PortletResponse response) {
        setResponseWrapper(new ResponseWrapper(new PortletResponseAdaptor(response)));
    }

    /**
     * @deprecated use {@link #getConfigWrapper()} instead
     */
    @Deprecated
    public static PortletConfig getPortletConfig() {
        return configWrapper.get().getPortletConfig();
    }

    /**
     * @deprecated use {@link #setConfigWrapper(ConfigWrapper)} instead
     */
    @Deprecated
    public static void setPortletConfig(PortletConfig portletConfigArg) {
        setConfigWrapper(new ConfigWrapper(new PortletConfigAdaptor(portletConfigArg)));
    }

    /**
     * @deprecated use {@link #setConfigWrapper(ConfigWrapper)} instead
     */
    @Deprecated
    public static void setServletConfig(ServletConfig servletConfig) {
        setConfigWrapper(new ConfigWrapper(new ServletConfigAdaptor(servletConfig)));
    }

    /**
     * @deprecated use {@link #getConfigWrapper()} instead
     */
    @Deprecated
    public static ServletConfig getServletConfig() {
        return configWrapper.get().getServletConfig();
    }

    /**
     * @deprecated use {@link #setResponseWrapper(ResponseWrapper)} instead
     */
    @Deprecated
    public static void setServletResponse(HttpServletResponse response) {
        setResponseWrapper(new ResponseWrapper(new ServletResponseAdaptor(response)));
    }

    /**
     * @deprecated use {@link #getResponseWrapper()} instead
     */
    @Deprecated
    public static HttpServletResponse getServletResponse() {
        return getResponseWrapper().getServletResponse();
    }

    /**
     * @deprecated use {@link #setRequestWrapper(RequestWrapper)} instead
     */
    @Deprecated
    public static void setServletRequest(HttpServletRequest request) {
        setRequestWrapper(new RequestWrapper(new ServletRequestAdaptor(request)));
    }

    /**
     * @deprecated use {@link #getRequestWrapper()} instead
     */
    @Deprecated
    public static HttpServletRequest getServletRequest() {
        return getRequestWrapper().getServletRequest();
    }

    public static Type getSessionTemp() {
        return getSessionWrapper().getSessionTemp();
    }

    public static void setSessionSessionTemp(Type sessionTemp) {
        getSessionWrapper().setSessionTemp(sessionTemp);
    }

    /**
     * Get the product name (if any) associated with this context. This will either
     * come from portlet session or, if that is null, from a thread local.
     *
     * @return product name, or null if none is selected.
     */
    public static String getProductName() {
        String productName=null;

        try {
            if (getSessionWrapper().getProductName() != null) {
                productName=getSessionWrapper().getProductName();
            }
        }
        catch(CoreContextError e) {
            // ignore this, we'll get it if there is no portlet session available.
        }

        if (productName==null && productNameThreadLocal.get()!=null) {
            productName=productNameThreadLocal.get().toString();
        }

        return productName;
    }

    /**
     * @see #getProductName()
     * @param productNameThreadLocal
     */
    public static void setProductName(String productNameArg) {
        try {
            getSessionWrapper().setProductName(productNameArg);
        }
        catch(CoreContextError e) {
            // ignore this, we'll get it if there is no portlet session available.
        }

        productNameThreadLocal.set(productNameArg==null ? null : new StringBuffer(productNameArg));
    }

    public static void setSuccessRedirect(String redirectUrl) {
        getSessionWrapper().setSuccessRedirect(redirectUrl);
    }

    public static String getSuccessRedirect() {
        return getSessionWrapper().getSuccessRedirect();
    }

    public static void setFailureRedirect(String redirectUrl) {
        getSessionWrapper().setFailureRedirect(redirectUrl);
    }

    public static String getFailureRedirect() {
        return getSessionWrapper().getFailureRedirect();
    }

    public static void setRemoteUser(Long removeUser) {
        getSessionWrapper().setRemoteUser(removeUser);
    }

    public static Long getRemoteUser() {
        if (sessionWrapper != null && getSessionWrapper() != null && getSessionWrapper().getRemoteUser() != null) {
            return getSessionWrapper().getRemoteUser();
        }

        if (requestWrapper != null && requestWrapper.get() != null && requestWrapper.get().getUserPrincipal() != null) {
            return Long.parseLong(requestWrapper.get().getUserPrincipal().getName());
        }

        if (userPrincipal != null && getUserPrincipal() != null) {
            return Long.parseLong(getUserPrincipal().getName());
        }

        return null;
    }

    public static boolean isPortletRequest() {
        return requestWrapper.get().isPortletRequest();
    }

    public static Locale getRequestLocale() {
        return getRequestWrapper() != null ? getRequestWrapper().getLocale() : null;
    }

    public static void setRestfulRequestPostData(String data) {
        getSessionWrapper().setRestfulRequestPostData(data);
    }

    public static String getRestfulRequestData() {
        return getSessionWrapper().getRestfulRequestPostData();
    }

    public static void setRestfulResponse(RestfulServiceReturn ret) {
        getSessionWrapper().setRestfulResponse(ret);
    }

    public static RestfulServiceReturn getRestfulResponse() {
        return getSessionWrapper().getRestfulResponse();
    }

    public static void setRestfulRequestAttachment(Map<String, Object> attachment) {
        getSessionWrapper().setRestfulRequestAttachment(attachment);
    }

    public static Map<String, Object> getRestfulRequestAttachment() {
        return getSessionWrapper().getRestfulRequestAttachment();
    }

    public static Principal getUserPrincipal() {
        return userPrincipal.get();
    }

    public static ServiceRequestRecord getServiceRequestRecord() {
        return getSessionWrapper().getServiceRequestRecord();
    }

    public static void setServiceRequestRecord(ServiceRequestRecord serviceRequestRecord) {
        getSessionWrapper().setServiceRequestRecord(serviceRequestRecord);
    }

    private static SessionWrapper getSessionWrapper() {
        if (sessionWrapper.get() == null) {
            initialise();
        }

        return sessionWrapper.get();
    }
}
