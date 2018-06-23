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
package com.ail.core.persistence.hibernate;

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

public class HibernateSessionPortletFilter implements ResourceFilter, RenderFilter, ActionFilter, EventFilter {
    enum FilterOperation {
        ACTION, EVENT, RENDER, RESOURCE
    }

    private boolean readOnlyMode = false;

    private void doFilterInTransaction(PortletRequest request, PortletResponse response, FilterChain chain, FilterOperation operation) throws PortletException {

        try {
            new HibernateRunInTransaction<Object>() {
                private PortletRequest request;
                private PortletResponse response;
                private FilterChain chain;
                private FilterOperation operation;

                public HibernateRunInTransaction<Object> with(PortletRequest request, PortletResponse response, FilterChain chain, FilterOperation operation) {
                    this.request = request;
                    this.response = response;
                    this.chain = chain;
                    this.operation = operation;
                    return this;
                }

                @Override
                public Object run() throws Throwable {
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

                    return null;
                }
            }.with(request, response, chain, operation).invoke(readOnlyMode);
        } catch (Throwable t) {
            throw new PortletException(t);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws PortletException {
        String readonlyInitParam = filterConfig.getInitParameter("readonly");

        if ("true".equals(readonlyInitParam)) {
            readOnlyMode = true;
        }
    }

    @Override
    public void doFilter(EventRequest request, EventResponse response, FilterChain chain) throws IOException, PortletException {
        doFilterInTransaction(request, response, chain, FilterOperation.EVENT);
    }

    @Override
    public void doFilter(ActionRequest request, ActionResponse response, FilterChain chain) throws IOException, PortletException {
        doFilterInTransaction(request, response, chain, FilterOperation.ACTION);
    }

    @Override
    public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException {
        doFilterInTransaction(request, response, chain, FilterOperation.RENDER);
    }

    @Override
    public void doFilter(ResourceRequest request, ResourceResponse response, FilterChain chain) throws IOException, PortletException {
        doFilterInTransaction(request, response, chain, FilterOperation.RESOURCE);
    }
}
