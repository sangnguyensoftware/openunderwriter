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

import static com.ail.pageflow.PageFlowContext.getCoreProxy;
import static com.ail.pageflow.PageFlowContext.getPolicy;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Service;
import com.ail.core.VersionEffectiveDate;
import com.ail.pageflow.ExecutePageActionService;

/**
 * Select the appropriate Version Effective Date (VED) for this session.
 * <p>
 * This service encompasses the business rule defining how a PageFlow determines
 * the version effective data that should be used while processing requests
 * though it.
 * </p>
 * <p>
 * The 'appropriate' VED may be derived from a number of sources including the
 * VED defined against a policy that is in the context (if any), the quotation
 * date of a policy in the context (again, if any), or it may be defaulted to
 * today's date.
 * </p>
 */
@ServiceImplementation
public class SelectVersionEffectiveDateForPageFlowService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {
        VersionEffectiveDate ved = null;

        if (getPolicy() != null) {
            if (getPolicy().getVersionEffectiveDate() != null) {
                ved = getPolicy().getVersionEffectiveDate();
            } else if (getPolicy().getQuotationDate() != null) {
                ved = new VersionEffectiveDate(getPolicy().getQuotationDate());
            }
        }

        if (ved != null) {
            getCoreProxy().setVersionEffectiveDate(ved);
        } else {
            getCoreProxy().setVersionEffectiveDateToNow();
        }
    }
}