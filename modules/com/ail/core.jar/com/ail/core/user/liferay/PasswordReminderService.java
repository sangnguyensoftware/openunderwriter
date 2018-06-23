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

import javax.servlet.http.HttpServletRequest;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.user.PasswordReminderService.PasswordReminderArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * Implementation of the password reminder service for liferay
 */
@ServiceImplementation
public class PasswordReminderService extends Service<PasswordReminderArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getUsersEmailAddressArg() == null || args.getUsersEmailAddressArg().length() == 0) {
            throw new PreconditionException("args.getUsersEmailAddressArg() == null || args.getUsersEmailAddressArg().length() == 0");
        }

        try {
            handleReminder();
        } catch (Throwable e) {
            getCore().logError("Email reminder send failed for: "+args.getUsersEmailAddressArg(), e);
        }
    }

    protected void handleReminder() throws PortalException, SystemException {

        HttpServletRequest httpRequest=CoreContext.getRequestWrapper().getServletRequest();

        ServiceContext serviceContext = ServiceContextFactory.getInstance(httpRequest);

        Company company = CoreContext.getRequestWrapper().getCompany();

        String subject=null; // Use the subject specified in portal.properties (admin.email.password.sent.subject).

        String body=null; // Use the body specified in portal.properties (admin.email.password.sent.body)

        UserLocalServiceUtil.sendPassword(company.getCompanyId(),
                                          args.getUsersEmailAddressArg(),
                                          company.getAdminName(),
                                          company.getEmailAddress(),
                                          subject,
                                          body,
                                          serviceContext);
    }
}
