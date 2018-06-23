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

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.ail.core.CoreProxy;

public class HibernateSessionServletFilter implements Filter {

    private boolean readOnlyMode = false;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            new HibernateRunInTransaction<Object>() {
                private ServletRequest request;
                private ServletResponse response;
                private FilterChain chain;

                HibernateRunInTransaction<Object> with(ServletRequest request, ServletResponse response, FilterChain chain) {
                    this.request = request;
                    this.response = response;
                    this.chain = chain;
                    return this;
                }

                @Override
                public Object run() throws Throwable {
                    chain.doFilter(request, response);

                    return null;
                }
            }.with(request, response, chain).invoke(readOnlyMode);
        } catch (Throwable t) {
            new CoreProxy().logError("Exception thrown processing request. ", t);
            ((HttpServletResponse)response).setStatus(HTTP_INTERNAL_ERROR);
            ((HttpServletResponse)response).setContentType(APPLICATION_JSON);
            ((HttpServletResponse)response).resetBuffer();
            ((HttpServletResponse)response).getWriter().append(errorEntity("i18n_internal_error", t));
        }
    }

    private String errorEntity(String errorCode, String errorMessage) {
        return "{ \"success\": false, \"errorCode\": \""+errorCode+"\", \"errorMessage\": \""+errorMessage+"\"}";
    }

    private String errorEntity(String errorCode, Throwable t) {
        return errorEntity(errorCode, t.toString().replace('"', '\''));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String readonlyInitParam = filterConfig.getInitParameter("readonly");

        if ("true".equals(readonlyInitParam)) {
            readOnlyMode = true;
        }
    }
}
