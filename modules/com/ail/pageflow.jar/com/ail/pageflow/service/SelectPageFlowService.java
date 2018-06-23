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

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Service;
import com.ail.core.ThreadLocale;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;

/**
 * Fetch the page flow associated with this session if there is one. If there is
 * a page flow associated with the session, this service will fetch it and put
 * it into the PageFlow context. If there is no page flow associated, the
 * service has no effect.
 */
@ServiceImplementation
public class SelectPageFlowService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {
        if (PageFlowContext.getPageFlowName() == null || PageFlowContext.getPageFlowName().length() == 0) {
            return;
        }

        if (PageFlowContext.getProductName() == null || PageFlowContext.getProductName().length() == 0) {
            return;
        }

        PageFlow pageFlow = PageFlowContext.getCoreProxy().newProductType(
                PageFlowContext.getProductName(),
                PageFlowContext.getPageFlowName(),
                PageFlow.class);

        PageFlowContext.setPageFlow(pageFlow);

        if (pageFlow.getCurrentPage()==null) {
            pageFlow.setNextPage(pageFlow.getStartPage());
        }

        // Set the thread's locale. This may change from request to request.
        ThreadLocale.setThreadLocale(PageFlowContext.getRequestLocale());
    }
}