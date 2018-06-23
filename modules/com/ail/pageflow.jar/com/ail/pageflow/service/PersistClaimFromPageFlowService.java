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
import com.ail.core.CoreProxy;
import com.ail.core.Service;
import com.ail.insurance.claim.Claim;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;

@ServiceImplementation
public class PersistClaimFromPageFlowService extends Service<ExecutePageActionService.ExecutePageActionArgument> {

    @Override
    public void invoke() throws BaseException {
        if (PageFlowContext.getClaim() != null) {
            Claim claim = PageFlowContext.getClaim();

            CoreProxy proxy = getCoreProxyFromPageFlowContext();

            if (claim.isPersisted()) {
                claim = proxy.update(claim);
            } else {
                claim = proxy.create(claim);
            }

            setClaimToPageFlowContext(claim);

            args.setModelArgRet(claim);
        }
    }

    // Wrapped static PageFlowContext call to help testability
    protected String getCurrentPageFromPageFlowContext() {
        return PageFlowContext.getCurrentPageName();
    }

    // Wrapped static PageFlowContext call to help testability
    protected String getPageFlowNameFromPageFlowContext() {
        return PageFlowContext.getPageFlowName();
    }

    // Wrapped static PageFlowContext call to help testability
    protected CoreProxy getCoreProxyFromPageFlowContext() {
        return PageFlowContext.getCoreProxy();
    }

    protected void setClaimToPageFlowContext(Claim claim) {
        PageFlowContext.setClaim(claim);
    }
}