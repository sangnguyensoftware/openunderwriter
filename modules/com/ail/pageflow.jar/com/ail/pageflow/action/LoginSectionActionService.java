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

package com.ail.pageflow.action;

import static com.ail.pageflow.util.Functions.addError;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.user.LoginService.LoginServiceCommand;
import com.ail.core.user.PasswordReminderService.PasswordReminderCommand;
import com.ail.pageflow.LoginSection;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.action.ActionService.ActionArgument;

@ServiceImplementation
public class LoginSectionActionService extends Service<ActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {

        if (!(args.getPageElementArg() instanceof LoginSection)) {
            throw new PreconditionException("!(args.getPageElementArg() instanceof LoginSection)");
        }

        String op = PageFlowContext.getRequestedOperation();

        if ("login-section-login".equals(op)) {
            handleLogin();
        } else if ("login-section-create".equals(op)) {
            handleCreate();
        } else if ("login-section-reminder".equals(op)) {
            handleReminder();
        }
    }

    void handleReminder() throws BaseException {
        PasswordReminderCommand prc = getCore().newCommand(PasswordReminderCommand.class);

        prc.setUsersEmailAddressArg(PageFlowContext.getRequestWrapper().getParameter("username"));

        prc.invoke();
    }

    void handleCreate() {
    }

    void handleLogin() throws BaseException {
        LoginServiceCommand lsc = getCore().newCommand(LoginServiceCommand.class);

        lsc.setUsernameArg(CoreContext.getRequestWrapper().getParameter("username"));
        lsc.setPasswordArg(CoreContext.getRequestWrapper().getParameter("password"));
        lsc.setRememberMeArg(false);

        lsc.invoke();

        if (lsc.getAuthenticationSucceededRet()) {
            String destinationPage = getDestinationOnSuccessPageIdFromPageElement();
            if (destinationPage != null) {
                setNextPageNameOnPageFlow(destinationPage);
            }
        } else {
            Type errors = CoreContext.getSessionTemp();
            addError("page", args.getPageElementArg().i18n("i18n_login_section_login_failed_error"), errors);
        }
    }

    protected void setNextPageNameOnPageFlow(String nextPage) {
        PageFlowContext.setNextPageName(nextPage);
    }

    protected String getDestinationOnSuccessPageIdFromPageElement() {
        LoginSection loginSection = (LoginSection) args.getPageElementArg();
        return loginSection.getDestinationOnSuccessPageId();
    }
}