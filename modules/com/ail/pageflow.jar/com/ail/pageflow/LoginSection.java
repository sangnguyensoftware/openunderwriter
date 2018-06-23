/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.pageflow;

import static com.ail.core.CoreContext.getSessionTemp;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.removeErrorMarkers;

import java.io.IOException;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.core.context.RequestWrapper;
import com.ail.core.user.GuessUsernameService.GuessUsernameCommand;
import com.ail.core.user.IsExistingUserService.IsExistingUserCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.util.Functions;

/**
 * The LoginSection page element provides a single UI section which deals with
 * the following use-cases:
 * <ol>
 * <li>Existing user logs in, is successfully authenticated and forwarded to a
 * secured URL; or if authentication fails is forwarded to a "login failed"
 * page. If authentication does succeed, the user is forwarded to
 * &lt;protocol&gt
 * ;://&lt;host&gt;:&lt;port&gt;/portal/auth/&lt;portalName&gt;/&lt
 * ;<b>pageName</b>&gt where <b>portalName</b> and <b>pageName</b> are taken
 * from the {@link #getForwardToPortalName() forwardToPortalName} and
 * {@link #getForwardToPageName() forwardToPageName} properties. Clicking on the
 * "invitation link" reveals the section shown below (2.); selecting the
 * forgotten password link reveals the section shown in 3.<br/>
 * <img src="doc-files/LoginSection-1.png"/></li>
 * <li>New user creates an account. If the user's email address is already known
 * (/quote/proposer/emailAddress!=null), then the "Email address:" field is
 * automatically populated.<br/>
 * <img src="doc-files/LoginSection-2.png"/><br/>
 * When "Create and Save" is selected, the details are validated and a new
 * portal user is created. Validation include a check for duplicate usernames.
 * The user is then automatically logged in as the new user and forwarded to the
 * same URL as in 1. above.<br/>
 * </li>
 * <li>Existing user requests a password reminder. On selecting "Send Reminder"
 * the form is validated and an email is sent to the specified address providing
 * a new password. An error is displayed if "Email address" is not associated
 * with an existing user.<br/>
 * <img src="doc-files/LoginSection-3.png"/></li>
 * </ol>
 *
 * @see SaveButtonAction
 */
public class LoginSection extends PageContainer {
    private static final long serialVersionUID = 297215265083279666L;
    private String invitationMessageText;
    private String invitationLinkText;
    private String destinationOnSuccessPageId;
    private Boolean hiddenByDefault = false;

    /** Login button's label text. Defaults to "Login" */
    private String loginButtonLabel = "i18n_login_section_login_button_label";

    /**
     * JavaScript to reset the LoginSection to show the "Proposer Login".
     *
     * @see SaveButtonAction
     */
    public static final String reset = "showDivDisplay(\"Proposer Login\"); hideDivDisplay(\"Forgotten Password\");hideDivDisplay(\"Create Login\")";

    public LoginSection() {
        super();
    }

    public String getInvitationLinkText() {
        return invitationLinkText;
    }

    public void setInvitationLinkText(String invitationLinkText) {
        this.invitationLinkText = invitationLinkText;
    }

    public String getInvitationMessageText() {
        return invitationMessageText;
    }

    public void setInvitationMessageText(String invitationMessageText) {
        this.invitationMessageText = invitationMessageText;
    }

    public String getFormattedInvitationMessageText(String link) {
        if (getInvitationMessageText()!=null) {
            return String.format(getInvitationMessageText(), link);
        }
        else {
            return "";
        }
    }

    /**
     * Text of the label to appear on the login button. This defaults to
     * "Login".
     *
     * @return Login button's label text
     */
    public String getLoginButtonLabel() {
        return loginButtonLabel;
    }

    /**
     * @see #getLoginButtonLabel()
     * @param loginButtonLabel
     *            Login button's label text
     */
    public void setLoginButtonLabel(String loginButtonLabel) {
        this.loginButtonLabel = loginButtonLabel;
    }

    public String getDestinationOnSuccessPageId() {
        return destinationOnSuccessPageId;
    }

    public void setDestinationOnSuccessPageId(String destinationOnSuccessPageId) {
        this.destinationOnSuccessPageId = destinationOnSuccessPageId;
    }

    @Override
    public Type applyRequestValues(Type model) {
        RequestWrapper request=CoreContext.getRequestWrapper();

        String op = PageFlowContext.getRequestedOperation();

        if ("login-section-login".equals(op)) {
            String username = request.getParameter("username");
            try {
                getSessionTemp().xpathGet("/attribute[id='username']", Attribute.class).setValue(username);
            } catch(TypeXPathException e) {
                getSessionTemp().addAttribute(new Attribute("username", username, "string"));
            }
        }
        else if ("Create".equals(op)) {
            Long userId =  Long.parseLong(request.getParameter("username"));
            ((Policy) model).setOwningUser(userId);
        } else if ("Save".equals(op)) {
            Long userId =  Long.parseLong(request.getParameter("username"));
            ((Policy) model).setOwningUser(userId);
        }

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        // If our condition isn't met, validate nothing.
        if (!conditionIsMet(model)) {
            return false;
        }

        RequestWrapper request=CoreContext.getRequestWrapper();

        try {
            boolean error = false;
            String op = PageFlowContext.getRequestedOperation();
            Type errors = getSessionTemp();

            removeErrorMarkers(errors);

            if ("login-section-login".equals(op)) {
                String u = request.getParameter("username");
                String p = request.getParameter("password");

                if (u == null || u.length() == 0) {
                    addError("username", i18n("i18n_required_error"), errors);
                    error = true;
                }

                if (p == null || p.length() == 0) {
                    addError("password", i18n("i18n_required_error"), errors);
                    error = true;
                }
            }
            else if ("login-section-create".equals(op)) {
                Functions.removeErrorMarkers(errors);

                String u = request.getParameter("username");
                String uc = request.getParameter("cusername");
                String p = request.getParameter("password");
                String pc = request.getParameter("cpassword");

                if (u == null || u.length() == 0) {
                    addError("username", i18n("i18n_required_error"), errors);
                    error = true;
                } else if (!u.equals(uc)) {
                    addError("cusername", i18n("i18n_login_section_username_missmatch_error"), errors);
                    error = true;
                }

                if (p == null || p.length() == 0) {
                    addError("password", i18n("i18n_required_error"), errors);
                    error = true;
                } else if (!p.equals(pc)) {
                    addError("cpassword", i18n("i18n_login_section_password_missmatch_error"), errors);
                    error = true;
                } else {
                    if (isAnExistingUser(u)) {
                        addError("username", i18n("i18n_login_section_username_taken_error"), errors);
                        error = true;
                    }
                }

                // if any errors were found add a summary error. This doesn't ever
                // get displayed, but
                // is used in renderResponse() to make sure the page is opened with
                // the create form on
                // display.
                if (error) {
                    addError("create", "error", errors);
                }
            } else if ("Save".equals(op) && request.getUserPrincipal() == null) {
                // We're saving and the user isn't logged in yet.
                Functions.removeErrorMarkers(errors);

                String u = request.getParameter("username");
                String p = request.getParameter("password");

                if (u == null || u.length() == 0) {
                    addError("username", i18n("i18n_required_error"), errors);
                    error = true;
                } else if (!isAnExistingUser(u)) {
                    addError("username", i18n("i18n_login_section_unknown_username_error"), errors);
                    error = true;
                }

                if (p == null || p.length() == 0) {
                    addError("password", i18n("i18n_required_error"), errors);
                    error = true;
                }

                // if any errors were found add a summary error. This doesn't ever
                // get displayed, but
                // is used in renderResponse() to make sure the page is opened with
                // the save form on
                // display.
                if (error) {
                    addError("login", "error", errors);
                }
            }

            return error;
        } catch (BaseException e) {
            throw new ValidationError(e);
        }
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        return executeActionCommand("LoginSectionAction", model);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("LoginSection", model);
    }

    public boolean isAnExistingUser(String username) throws BaseException {
        IsExistingUserCommand ieuc=PageFlowContext.getCoreProxy().newCommand(IsExistingUserCommand.class);
        ieuc.setUsernameArg(username);
        ieuc.invoke();
        return ieuc.getExistingUserRet();
    }

    public boolean isAuthenticated() throws Exception {
        return PageFlowContext.getRequestWrapper().getUserPrincipal() != null;
    }

    public Boolean isHiddenByDefault() {
        return hiddenByDefault;
    }

    public void setHiddenByDefault(Boolean hiddenByDefault) {
        this.hiddenByDefault = hiddenByDefault;
    }

    public String usernameGuess() throws BaseException {
        GuessUsernameCommand guc = PageFlowContext.getCoreProxy().newCommand("GuessUsernameCommand", GuessUsernameCommand.class);
        guc.setModelArg(PageFlowContext.getPolicy());
        guc.invoke();
        return guc.getUsernameRet();
    }
}
