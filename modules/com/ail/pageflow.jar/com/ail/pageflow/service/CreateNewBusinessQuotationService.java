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

import static com.ail.core.context.RequestAdaptor.PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;

@ServiceImplementation
public class CreateNewBusinessQuotationService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {

        if (PageFlowContext.getProductName() == null || PageFlowContext.getProductName().length() == 0) {
            throw new PreconditionException("PageFlowContext.getProductName() == null || PageFlowContext.getProductName().length() == 0");
        }

        if (PageFlowContext.getPageFlow() == null) {
            throw new PreconditionException("PageFlowContext.getPageFlow() == null");
        }

        if (getPolicyFromPageFlowContext() != null) {
            throw new PreconditionException("PageFlowContext.getPolicy() != null");
        }

        // Create a new policy
        Policy policy = PageFlowContext.getCoreProxy().newProductType(PageFlowContext.getProductName(), "Policy", Policy.class);

        if (isPolicyBeingCreatedInTheSandpit()) {
            policy.setTestCase(true);
        }

        // The productTypeID in the policy must match the product we're working for.
        policy.setProductTypeId(getProductNameFromPageFlowContext());

        if (configurationSourceIsRequest() && isNotAnAjaxRequest()) {
            setPageFlowPageFromRequest(policy);
        }

        // Give the current user ownership - null is okay if the user is a guest.
        policy.setOwningUser(PageFlowContext.getRemoteUser());

        // Put the policy into the PageFlowContext
        setPolicyToPageFlowContext(policy);

        // Put the policy into the command args
        args.setModelArgRet(policy);

        if (PageFlowContext.getPolicy() == null) {
            throw new PostconditionException("getPolicy() == null");
        }
    }

    private boolean isPolicyBeingCreatedInTheSandpit() {
        return CoreContext.isPortletRequest() && CoreContext.getResponseWrapper().getNamespace().contains("_sandpit_");
    }

    // Wrapper to static PageFlowContext method call to help testability.
    protected Policy getPolicyFromPageFlowContext() {
        return PageFlowContext.getPolicy();
    }

    // Wrapper to static PageFlowContext method call to help testability.
    protected void setPolicyToPageFlowContext(Policy policyArg) {
        PageFlowContext.setPolicy(policyArg);
    }

    // Wrapper to static PageFlowContext method call to help testability.
    protected String getProductNameFromPageFlowContext() {
        return PageFlowContext.getProductName();
    }

    protected boolean configurationSourceIsRequest() {
        return CoreContext.getPreferencesWrapper().isConfiguredByRequest();
    }

    protected boolean isNotAnAjaxRequest() {
        return !CoreContext.getRequestWrapper().isAjaxRequest();
    }

    private void setPageFlowPageFromRequest(Policy policy) {
        String requestedPage = CoreContext.getRequestWrapper().getServletRequest().getParameter(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME);

        String pageFlowName = PageFlowContext.getPageFlowName();

        policy.setPage(pageFlowName, requestedPage);

        PageFlowContext.setCurrentPageName(requestedPage);
        PageFlowContext.setNextPageName(requestedPage);
    }
}
