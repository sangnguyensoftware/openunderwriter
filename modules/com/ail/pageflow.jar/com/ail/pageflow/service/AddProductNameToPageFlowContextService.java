/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.pageflow.service;

import static com.ail.core.Functions.productNameToConfigurationNamespace;
import static com.ail.core.context.PreferencesAdaptor.PRODUCT_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.RequestAdaptor.PRODUCT_PORTLET_REQUEST_PARAMETER_NAME;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;

/**
 * Get the name of the product we're working for. This comes from one of three
 * places: in normal operation it is picked up from the portlet preference named
 * 'product', or from the request property "pageflow.product". In development
 * mode (i.e. in the sandpit) it is picked up from the session.
 */
@ServiceImplementation
public class AddProductNameToPageFlowContextService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198813603833694389L;

    @Override
    public void invoke() throws BaseException {
        if (CoreContext.getRequestWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequestWrapper() == null");
        }

        if (CoreContext.getPreferencesWrapper() == null) {
            throw new PreconditionException("CoreContext.getPreferencesWrapper() == null");
        }

        String productName = determinProductName();

        // Regardless of what product name we have, even if it is null, pass it
        // into the context.
        setProductNameToPageFlowContext(productName);

        if (productName!=null) {
            String namespace = productNameToConfigurationNamespace(productName);
            PageFlowContext.setCoreProxy(new CoreProxy(namespace));
        }
        else {
            PageFlowContext.setCoreProxy(new CoreProxy());
        }
    }

    private String determinProductName() {
        String productName = null;

        // The servlet request parameter takes precedence over everything
        if (CoreContext.getPreferencesWrapper().isConfiguredByRequest()) {
        	productName = CoreContext.getRequestWrapper().getServletRequest().getParameter(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME);
        }

        // ...but failing that, use the request property
        if (productName == null ) {
            productName = CoreContext.getRequestWrapper().getProperty(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME);
        }

        // ...but failing that, use the pageflow context (session attribute)
        if (productName == null) {
            productName = getProductNameFromPageFlowContext();
        }

        // ...but failing that, use the portlet preference (if we're configured to use it).
        if (productName == null && CoreContext.getPreferencesWrapper().isConfiguredByPreferences()) {
            productName = CoreContext.getPreferencesWrapper().getValue(PRODUCT_PORTLET_PREFERENCE_NAME, null);
        }

        return productName;
    }

    // Wrap call to PageFlowContext static to help testability.
    protected String getProductNameFromPageFlowContext() {
        return PageFlowContext.getProductName();
    }

    // Wrap call to PageFlowContext static to help testability.
    protected void setProductNameToPageFlowContext(String productNameArg) {
        PageFlowContext.setProductName(productNameArg);
    }
}