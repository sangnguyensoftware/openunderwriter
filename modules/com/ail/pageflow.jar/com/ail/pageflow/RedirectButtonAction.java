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
package com.ail.pageflow;

import static com.ail.core.context.RequestAdaptor.PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME;
import static com.ail.core.context.RequestAdaptor.PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME;
import static com.ail.core.context.RequestAdaptor.POLICY_PORTLET_REQUEST_PARAMETER_NAME;
import static com.ail.core.context.RequestAdaptor.PRODUCT_PORTLET_REQUEST_PARAMETER_NAME;

import java.io.IOException;

import javax.portlet.ActionResponse;

import com.ail.core.BaseException;
import com.ail.core.Type;

/**
 */
public class RedirectButtonAction extends CommandButtonAction {
    private static final long serialVersionUID = 7575333161831400599L;
    public static final String DESTINATION_URL = "DESTINATION_URL";
    public static final String SUCCESS_REDIRECT = "SUCCESS_REDIRECT";
    public static final String FAILURE_REDIRECT = "FAILURE_REDIRECT";

    private String target = null;
    private String destinationUrl = null;

    @Override
    public Type processActions(Type model) throws BaseException {
        if (buttonPressed()) {
            model = super.processActions(model);

            if (PageFlowContext.getResponseWrapper().getPortletResponse() instanceof ActionResponse) {
                attemptRedirectTo(fetchCanonicalDestinationUrl());
            }

            PageFlowContext.flagActionAsProcessed();
        }

        return model;
    }

    public String fetchCanonicalDestinationUrl() {
        switch (target) {
        case DESTINATION_URL:
            return destinationUrl;
        case FAILURE_REDIRECT:
            return appendPageFlowParamsTo(PageFlowContext.getFailureRedirect());
        case SUCCESS_REDIRECT:
            return appendPageFlowParamsTo(PageFlowContext.getSuccessRedirect());
        default:
            throw new ActionError("Target is not defined for redirect button (id:"+getId()+", label="+getLabel()+")");
        }
    }

    private String appendPageFlowParamsTo(String uri) {
        if (uri == null) {
            return null;
        }

        StringBuffer result = new StringBuffer(uri);

        result.append(uri.indexOf('?') == -1 ? '?' : '&');
        result.append(POLICY_PORTLET_REQUEST_PARAMETER_NAME).append('=').append(PageFlowContext.getPolicySystemId());
        result.append('&').append(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME).append('=').append(PageFlowContext.getProductName());
        result.append('&').append(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME).append('=').append(PageFlowContext.getPageFlowName());
        result.append('&').append(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME).append('=').append(PageFlowContext.getCurrentPageName());

        return result.toString();
    }

    private void attemptRedirectTo(String destinationUrl) {
        try {
            if (destinationUrl != null && !destinationUrl.isEmpty()) {
                PageFlowContext.getResponseWrapper().sendRedirect(destinationUrl);
            }
        } catch (IOException e) {
            PageFlowContext.getCoreProxy().logError("Redirect failed to: " + destinationUrl);
        }
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return (this.getClass().getSimpleName() + getId()).equals(op);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("RedirectButtonAction", model);
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
        if (destinationUrl != null && !destinationUrl.isEmpty()) {
            target = DESTINATION_URL;
        }
    }

    public boolean isDisabled() {
        switch (target) {
        case DESTINATION_URL:
            return isUndefined(destinationUrl);
        case FAILURE_REDIRECT:
            return isUndefined(PageFlowContext.getFailureRedirect());
        case SUCCESS_REDIRECT:
            return isUndefined(PageFlowContext.getSuccessRedirect());
        default:
            throw new ActionError("Target is not defined for redirect button (id:"+getId()+", label="+getLabel()+")");
        }
    }

    private boolean isUndefined(String value) {
        return value == null || value.isEmpty();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
