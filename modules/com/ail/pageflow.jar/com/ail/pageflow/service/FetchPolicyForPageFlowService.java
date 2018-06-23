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

import static com.ail.core.CoreContext.getProductName;
import static com.ail.core.context.RequestAdaptor.PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME;
import static com.ail.core.context.RequestAdaptor.POLICY_PORTLET_REQUEST_PARAMETER_NAME;
import static com.ail.pageflow.PageFlowContext.getPageFlow;
import static com.ail.pageflow.PageFlowContext.getPageFlowName;
import static com.ail.pageflow.PageFlowContext.getPolicySystemId;
import static com.ail.pageflow.PageFlowContext.setNextPageName;
import static com.ail.pageflow.PageFlowContext.setPageFlowInitliased;
import static com.ail.pageflow.PageFlowContext.setPolicy;
import static com.ail.pageflow.PageFlowContext.setPolicySystemId;

import javax.servlet.http.HttpServletRequest;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.ThreadLocale;
import com.ail.core.security.ConfirmObjectAccessibilityToUserService.ConfirmObjectAccessibilityToUserCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;

/**
 * Fetch the policy associated with this PageFlow if there is one. If there is a
 * policy associated with the PageFlow, this service will fetch it and put it
 * into the PageFlow's context. If there is no policy associated, the service
 * has no effect.
 */
@ServiceImplementation
public class FetchPolicyForPageFlowService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {

        if (CoreContext.getRequestWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequestWrapper() == null");
        }

        if (PageFlowContext.getCoreProxy() == null) {
            throw new PreconditionException("PageFlowContext.getCoreProxy() == null");
        }

        String requestedPage = null;

        if (configurationSourceIsRequest() && isNotAnAjaxRequest()) {
            HttpServletRequest servletequest = CoreContext.getRequestWrapper().getServletRequest();

            applyRequestedPolicyId(servletequest);
            requestedPage = determinRequestedPage(servletequest);
        }

        // If the session already has a policy system id associated with it,
        // then load the policy into the PageFlowContext.
        if (getPolicySystemId() != null) {
            CoreProxy proxy = PageFlowContext.getCoreProxy();

            Policy policy = (Policy) proxy.queryUnique("get.policy.by.systemId", getPolicySystemId());

            if (policy == null) {
                throw new PreconditionException("A policy with the systemId: "+getPolicySystemId()+" could not be found");
            }

            if (userIsDeniedAccessTo(policy)) {
                throw new PreconditionException("The current user ("+CoreContext.getRemoteUser()+") does not have permission to access: " + getPolicySystemId() + ".");
            }

            // The request's ThreadLocale could change from one request to the
            // next,if the user switches their browser settings for example, so
            // always use the current settings.
            policy.setLocale(new ThreadLocale(CoreContext.getRequestWrapper().getLocale()));

            // If we have a policy, then having a product name becomes a
            // precondition
            if (getProductName() == null || getProductName().length() == 0) {
                throw new PreconditionException("getProductName() == null || getProductName().length() == 0");
            }

            // Make sure that the product name is correctly populated in the
            // policy as we can't necessarily rely on the policy's type
            // definition to do this.
            policy.setProductTypeId(getProductName());

            // If a request is directing us to a specific page - apply it.
            if (requestedPage != null) {
                policy.setPage(getPageFlowName(), requestedPage);
            }

            // Add the policy to the PageFlowContext
            setPolicy(policy);

            // Set the current page to the latest one visited by this policy for
            // this pageflow.
            getPageFlow().setCurrentPage(policy.getPage(getPageFlowName()));

            // Add the policy to the command argument
            args.setModelArgRet(policy);
        }
    }

    private boolean userIsDeniedAccessTo(Policy policy) throws BaseException {
        ConfirmObjectAccessibilityToUserCommand ca = PageFlowContext.getCoreProxy().newCommand("ConfirmObjectAccessibilityToUserCommand", ConfirmObjectAccessibilityToUserCommand.class);
        ca.setObjectArg(policy);
        ca.setUserIdArg(CoreContext.getRemoteUser());
        ca.invoke();
        return !ca.getWriteAccessRet();
    }

    private boolean configurationSourceIsRequest() {
        return CoreContext.getPreferencesWrapper().isConfiguredByRequest();
    }

    private boolean isNotAnAjaxRequest() {
        return !CoreContext.getRequestWrapper().isAjaxRequest();
    }

    protected void applyRequestedPolicyId(HttpServletRequest request) {
        String policyNumber = request.getParameter(POLICY_PORTLET_REQUEST_PARAMETER_NAME);

        if (policyNumber != null) {
            try {
                setPolicySystemId(Long.parseLong(policyNumber));
                setPageFlowInitliased(true);
                setNextPageName(null);
            } catch (NumberFormatException e) {
                core.logError("Policy number in request was not recognised: "+policyNumber+". Parameter ignored.");
            }
        }
    }

    private String determinRequestedPage(HttpServletRequest request) {
        return request.getParameter(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME);
    }
}