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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;

/**
 * A pageflow HTTP request may contain forwarding URLs. This service will save these in the PageFlowContext for
 * later use by the "RedirectClientWidget".
 *
 * <p>Note: redirects are ignored unless the PageFlow portlet has its configuration.source
 * preference set to "Request".</p>
 */
@ServiceImplementation()
public class SaveRedirectsInSessionService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198662103833694389L;

    public static final String PAGEFLOW_FAILURE_REDIRECT_PARAM_NAME = "pageflow.failure";
    public static final String PAGEFLOW_SUCCESS_REDIRECT_PARAM_NAME = "pageflow.success";

    @Override
    public void invoke() throws BaseException {
        if (CoreContext.getRequestWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequestWrapper() == null");
        }

        if (isNotAnAjaxRequest()) {
            String successRedirect = null;
            String failureRedirect = null;

            if (CoreContext.getPreferencesWrapper().isConfiguredByRequest()) {
                HttpServletRequest servletRequest = CoreContext.getRequestWrapper().getServletRequest();

                successRedirect = servletRequest.getParameter(PAGEFLOW_SUCCESS_REDIRECT_PARAM_NAME);
                failureRedirect = servletRequest.getParameter(PAGEFLOW_FAILURE_REDIRECT_PARAM_NAME);

                if (successRedirect != null) {
                    try {
                        successRedirect = URLDecoder.decode(successRedirect, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new PreconditionException("value of pageflow.success parameter could not be decoded using UTF-8", e);
                    }
                }

                if (failureRedirect != null) {
                    try {
                        failureRedirect = URLDecoder.decode(failureRedirect, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new PreconditionException("value of pageflow.failure parameter could not be decoded using UTF-8", e);
                    }
                }
            }

            PageFlowContext.setSuccessRedirect(successRedirect);
            PageFlowContext.setFailureRedirect(failureRedirect);
        }
    }

    private boolean isNotAnAjaxRequest() {
        return !CoreContext.getRequestWrapper().isAjaxRequest();
    }
}