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
package com.ail.pageflow.portlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;

import com.ail.core.context.PreferencesAdaptor;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.portlet.PortletRequestAdaptor;
import com.ail.core.context.portlet.PortletSessionAdaptor;
import com.ail.pageflow.PageFlowContext;

/**
 * This Portlet acts as the controller (in MVC terms) for the quotation process.
 * On initially being called, it inspects the value of the 'product' init
 * parameter to discover the name of the product it has been instantiated to
 * quote for. The product name is used to create an instance of a quotation
 * object (the Model) - which is placed in the session, and an instance of the
 * PageFlow (View). As the PageFlow holds no state, it can be instantiated by
 * the core each time it is needed without significant overhead (the assumption
 * being that the PageFlow type is marked 'singleInstance' in the product's
 * configuration.
 */
public class PageFlowPortlet extends GenericPortlet {
    private static final String PRODUCT_NAME_URL_PARAMETER = "productNameURL";
    private static final String PRODUCT_NAME_PARAMETER = "productName";
    private static final String PAGEFLOW_NAME_PARAMETER = "pageFlowName";
    private static final String SELECTED_SOURCE_PARAMETER = "selectedSource";

    private String editJSP = null;
    private String configureJSP = null;
    private String errorJSP = null;
    private PageFlowCommon pageFlowCommon = null;

    public PageFlowPortlet() {
        pageFlowCommon=new PageFlowCommon();
    }

    @Override
    public void init() throws PortletException {
        editJSP = getInitParameter("edit-jsp");
        configureJSP = getInitParameter("configure-jsp");
        errorJSP = getInitParameter("error-jsp");
    }

    @Override
    protected Collection<PortletMode> getNextPossiblePortletModes(RenderRequest request) {
        if (preferenceConfigurationRequired(request)) {
            return Arrays.asList(PortletMode.EDIT);
        } else {
            return Arrays.asList(PortletMode.EDIT, PortletMode.VIEW);
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws ReadOnlyException, ValidatorException, IOException, PortletModeException {
        if (PortletMode.VIEW.equals(request.getPortletMode())) {
            doProcessPageFlowAction(request, response);
        }

        if (PortletMode.EDIT.equals(request.getPortletMode())) {
            doProcessEditAction(request, response);
        }
    }

    @Override
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");

        PortletURL addNameURL = response.createActionURL();

        String selectedProduct = request.getPreferences().getValue(PreferencesAdaptor.PRODUCT_PORTLET_PREFERENCE_NAME, null);
        String selectedPageFlow = request.getPreferences().getValue(PreferencesAdaptor.PAGEFLOW_PORTLET_PREFERENCE_NAME, null);
        String selectedSource = request.getPreferences().getValue(PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME, PreferencesAdaptor.CONFIGURE_BY_PREFERENCES);

        request.setAttribute(PRODUCT_NAME_PARAMETER, selectedProduct);
        request.setAttribute(PAGEFLOW_NAME_PARAMETER, selectedPageFlow);
        request.setAttribute(PRODUCT_NAME_URL_PARAMETER, addNameURL.toString());
        request.setAttribute(SELECTED_SOURCE_PARAMETER, selectedSource);

        PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(editJSP);

        portletRequestDispatcher.include(request, response);
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if (preferenceConfigurationRequired(request)) {
            doDisplayPageFlowView(request, response);
        } else {
            doDisplayConfigureView(request, response);
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        doProcessPageFlowAction(request, response);
        doDisplayPageFlowView(request, response);
    }

    private void doProcessEditAction(ActionRequest request, ActionResponse response) throws ReadOnlyException, IOException, ValidatorException, PortletModeException {
        PortletPreferences prefs = request.getPreferences();

        if (!"?".equals(request.getParameter(SELECTED_SOURCE_PARAMETER))) {
            prefs.setValue(PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME, request.getParameter(SELECTED_SOURCE_PARAMETER));
        }

        if (!"?".equals(request.getParameter(PAGEFLOW_NAME_PARAMETER))) {
            prefs.setValue(PreferencesAdaptor.PAGEFLOW_PORTLET_PREFERENCE_NAME, request.getParameter(PAGEFLOW_NAME_PARAMETER));
        }

        if (!"?".equals(request.getParameter(PRODUCT_NAME_PARAMETER))) {
            String oldValue = prefs.getValue(PreferencesAdaptor.PRODUCT_PORTLET_PREFERENCE_NAME, "?");
            String newValue = request.getParameter(PRODUCT_NAME_PARAMETER);
            if (oldValue==null || !oldValue.equals(newValue)) {
                prefs.setValue(PreferencesAdaptor.PRODUCT_PORTLET_PREFERENCE_NAME, newValue);
                prefs.setValue(PreferencesAdaptor.PAGEFLOW_PORTLET_PREFERENCE_NAME, null);
            }
        }

        prefs.store();

        PageFlowContext.setRequestWrapper(new RequestWrapper(new PortletRequestAdaptor(request)));
        new com.ail.core.context.SessionWrapper(new PortletSessionAdaptor(request.getPortletSession())).clear();
        new com.ail.pageflow.SessionWrapper(new PortletSessionAdaptor(request.getPortletSession())).clear();

        if (preferenceConfigurationRequired(request)) {
            response.setPortletMode(PortletMode.VIEW);
        }
    }

    private void doDisplayConfigureView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(configureJSP);
        portletRequestDispatcher.include(request, response);
    }

    private void doProcessPageFlowAction(PortletRequest request, PortletResponse response) {
        try {
            pageFlowCommon.processAction();
        } catch (Throwable t) {
            pageFlowCommon.handleError(t, errorJSP, getPortletContext());
        }
    }

    private void doDisplayPageFlowView(PortletRequest request, MimeResponse response) throws IOException {
        response.setContentType("text/html");

        response.getWriter().write("<div id='pageflow-wrapper'>");

        try {
            pageFlowCommon.doView();
        } catch (Throwable t) {
            pageFlowCommon.handleError(t, errorJSP, getPortletContext());
        }

        response.getWriter().write("</div>");
    }

    private boolean preferenceConfigurationRequired(PortletRequest request) {
        String configuredBy = request.getPreferences().getValue(PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME, PreferencesAdaptor.CONFIGURE_BY_PREFERENCES);

        if (PreferencesAdaptor.CONFIGURE_BY_PREFERENCES.equals(configuredBy)) {
            String selectedProduct = request.getPreferences().getValue(PreferencesAdaptor.PRODUCT_PORTLET_PREFERENCE_NAME, null);
            String selectedPageFlow = request.getPreferences().getValue(PreferencesAdaptor.PAGEFLOW_PORTLET_PREFERENCE_NAME, null);

            if (selectedProduct == null || "".equals(selectedProduct) || "?".equals(selectedProduct)
            ||  selectedPageFlow == null || "".equals(selectedPageFlow) || "?".equals(selectedPageFlow)) {
                return false;
            } else {
                return true;
            }
        }
        else {
            return true;
        }
    }
}
