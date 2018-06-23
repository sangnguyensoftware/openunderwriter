/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

import static com.ail.core.CoreContext.getResponseWrapper;
import static com.ail.pageflow.PageFlowContext.getPageFlow;
import static javax.portlet.PortletMode.EDIT;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import com.ail.core.persistence.hibernate.ForceSilentRollbackError;
import com.ail.pageflow.PageFlowContext;

public class PageflowContextPortletFilter implements ResourceFilter, RenderFilter, ActionFilter, EventFilter {
    enum FilterOperation {
        ACTION, EVENT, RENDER, RESOURCE
    };

    private void doInContext(PortletRequest request, PortletResponse response, FilterChain chain, FilterOperation operation) throws PortletException {
        if (!EDIT.equals(request.getPortletMode())) {
            PageFlowContext.initialise(request, response, null);
        }

        try {
            switch (operation) {
            case ACTION:
                chain.doFilter((ActionRequest) request, (ActionResponse) response);
                break;
            case EVENT:
                chain.doFilter((EventRequest) request, (EventResponse) response);
                break;
            case RENDER:
                chain.doFilter((RenderRequest) request, (RenderResponse) response);
                break;
            case RESOURCE:
                chain.doFilter((ResourceRequest) request, (ResourceResponse) response);
                break;
            }
        } catch (Throwable t) {
            throw new PortletException(t);
        }
        finally {
            boolean validationRollbackRequired = validationRollbackRequired();

            if (!EDIT.equals(request.getPortletMode())) {
                PageFlowContext.destroy();
            }

            if (validationRollbackRequired) {
                throw new ForceSilentRollbackError();
            }
        }
    }

    private boolean validationRollbackRequired() {
        return getPageFlow() != null && getPageFlow().isRollbackOnValidationFailure() && getResponseWrapper() != null && getResponseWrapper().isValidationErrorsFound();
    }

    @Override
    public void doFilter(EventRequest request, EventResponse response, FilterChain chain) throws IOException, PortletException {
        doInContext(request, response, chain, FilterOperation.EVENT);
    }

    @Override
    public void doFilter(ActionRequest request, ActionResponse response, FilterChain chain) throws IOException, PortletException {
        doInContext(request, response, chain, FilterOperation.ACTION);
    }

    @Override
    public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException {
        doInContext(request, response, chain, FilterOperation.RENDER);
    }

    @Override
    public void doFilter(ResourceRequest request, ResourceResponse response, FilterChain chain) throws IOException, PortletException {
        doInContext(request, response, chain, FilterOperation.RESOURCE);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws PortletException {
    }
}
