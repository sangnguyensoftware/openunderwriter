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

package com.ail.core.user.liferay;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.user.IsExistingUserService.IsExistingUserArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * Implementation of the IsExistingUser service for Liferay.
 * The username is taken to be a user's "screen name" in Liferay terminology. Also, as
 * users are held per-company in Liferay, it is assumed that the request is being made for
 * the company (site) that the request is associated with.
 */
@ServiceImplementation
public class IsExistingUserService extends Service<IsExistingUserArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getUsernameArg() == null || args.getUsernameArg().length() == 0) {
            throw new PreconditionException("args.getUsernameArg()==null || args.getUsernameArg().length()==0");
        }

        if (CoreContext.getRequestWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequestWrapper() == null");
        }

        User user = null;

        try {
            Company company = CoreContext.getRequestWrapper().getCompany();
            if (company != null) {
                user = UserLocalServiceUtil.fetchUserByScreenName(company.getCompanyId(), args.getUsernameArg());
            }
        } catch (SystemException|PortalException e) {
            throw new PreconditionException("Failed to invoke liferay service: ", e);
        }

        if (user==null) {
            args.setExistingUserRet(false);
        }
        else {
            args.setExistingUserRet(true);
        }
    }
}