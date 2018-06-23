/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.workflow;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class LiferayLoginModule implements LoginModule {
    private CallbackHandler callbackHandler;
    private Set<Principal> principals;
    private Subject subject;
    private Long userId;
    private String username;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.principals = new HashSet<>();
    }

    @Override
    public boolean login() throws LoginException {
        try {
            if (isAuthenticatedSucessfully()) {
                principals.add(new SimplePrincipal(username));
                principals.add(createRolePrincipals());

                return true;
            }
        } catch (Exception e) {
            throw new LoginException();
        }

        throw new FailedLoginException();
    }

    @Override
    public boolean commit() throws LoginException {
        if (principals.size() > 0) {

            Set<Principal> ps = subject.getPrincipals();

            ps.addAll(principals);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().clear();
        return true;
    }

    private SimpleGroup createRolePrincipals() throws SystemException {
        SimpleGroup roles = new SimpleGroup("Roles");

        for (Role role : RoleLocalServiceUtil.getUserRoles(userId)) {
            roles.addMember(new SimplePrincipal(role.getName()));
        }

        return roles;
    }

    private boolean isAuthenticatedSucessfully() throws IOException, UnsupportedCallbackException, PortalException, SystemException {
        NameCallback nameCallback = new NameCallback("name: ");
        PasswordCallback passwordCallback = new PasswordCallback("password: ", false);

        callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });

        username = nameCallback.getName();

        userId = UserLocalServiceUtil.authenticateForBasic(10157L, "emailAddress", username, new String(passwordCallback.getPassword()));

        return userId != 0;
    }
}
