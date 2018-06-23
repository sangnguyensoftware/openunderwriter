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
package com.ail.pageflow.servlet;

import static com.ail.core.CoreContext.getResponseWrapper;
import static com.ail.pageflow.PageFlowContext.getPageFlow;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ail.core.persistence.hibernate.ForceSilentRollbackError;
import com.ail.pageflow.PageFlowContext;

public class PageFlowContextServletFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Throwable processingException = null;

        try {
            PageFlowContext.initialise((HttpServletRequest)request, (HttpServletResponse)response, (ServletConfig)null);
            chain.doFilter(request, response);
        } catch(Throwable t) {
            processingException = t;
        } finally {
            boolean validationRollbackRequired = validationRollbackRequired();

            try {
                PageFlowContext.clear();
                PageFlowContext.destroy();
            }
            catch(Throwable t) {
                // An exception during tidy-up is only thrown if processing
                // itself didn't thrown an exception. If it did, we don't want
                // the cleanup error masking the real problem.
                throw new ServletException((processingException != null) ? processingException : t);
            }

            if (validationRollbackRequired) {
                throw new ForceSilentRollbackError();
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // do nothing
    }

    boolean validationRollbackRequired() {
        return getPageFlow() != null && getPageFlow().isRollbackOnValidationFailure() && getResponseWrapper() != null && getResponseWrapper().isValidationErrorsFound();
    }
}
