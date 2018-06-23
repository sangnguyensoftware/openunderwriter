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

import static com.ail.core.context.PreferencesAdaptor.PAGEFLOW_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.RequestAdaptor.PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;

/**
 * Get the name of the pageflow we're working for. This comes from one of three
 * places: in normal operation it is picked up from the portlet preference named
 * 'pageflow', or from the request property "pageflow.name". In development
 * mode (i.e. in the sandpit) it is picked up from the session.
 */
@ServiceImplementation
public class AddPageFlowNameToPageFlowContextService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {
        if (CoreContext.getRequestWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequestWrapper() == null");
        }

        if (CoreContext.getPreferencesWrapper() == null) {
            throw new PreconditionException("CoreContext.getPreferencesWrapper() == null");
        }

        String pageFlowName = determinPageFlowName();

        // Regardless of what pageflow name we have, even if it is null, pass it
        // into the context.
        setPageFlowNameToPageFlowContext(pageFlowName);
    }

    private String determinPageFlowName() {
        String pageFlowName=null;

        // The servlet request parameter takes precedence over everything
        if (CoreContext.getPreferencesWrapper().isConfiguredByRequest()) {
            pageFlowName = CoreContext.getRequestWrapper().getServletRequest().getParameter(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME);
        }

        // ...but failing that, try the request property
        if (pageFlowName == null) {
            pageFlowName = CoreContext.getRequestWrapper().getProperty(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME);
        }

        // ...but failing that, try the portlet preference (if we're configured to use it)
        if (pageFlowName == null && CoreContext.getPreferencesWrapper().isConfiguredByPreferences()) {
            pageFlowName = CoreContext.getPreferencesWrapper().getValue(PAGEFLOW_PORTLET_PREFERENCE_NAME, null);
        }

        // ...but failing that, try the session attribute
        if (pageFlowName == null) {
            pageFlowName = getPageFlowNameFromPageFlowContext();
        }

        return pageFlowName;
    }

    // Wrap call to PageFlowContext static to help testability.
    protected String getPageFlowNameFromPageFlowContext() {
        return PageFlowContext.getPageFlowName();
    }

    // Wrap call to PageFlowContext static to help testability.
    protected void setPageFlowNameToPageFlowContext(String pageFlowNameArg) {
        PageFlowContext.setPageFlowName(pageFlowNameArg);
    }
}