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

package com.ail.core.security.strategy;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Owned;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.UpdateException;
import com.ail.core.security.ConfirmObjectAccessibilityToUserService.ConfirmObjectAccessibilityToUserArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * Object accessibility implementation which restricts access to object to users
 * who either own the object or who are members of an administrative role. The
 * name of the administrative role is defined as a service configuration
 * parameter.
 */
@ServiceImplementation
public class OwnerAndAdministratorAccessibilityService extends Service<ConfirmObjectAccessibilityToUserArgument> {

    @Override
    public void invoke() throws PreconditionException, UpdateException {

        if (args.getObjectArg() == null) {
            throw new PreconditionException("args.getObjectArg() == null");
        }

        Long userId = args.getUserIdArg();

        args.setReadAccessRet(false);
        args.setWriteAccessRet(false);

        if (isNotOwnable(args.getObjectArg())) {
            args.setReadAccessRet(true);
            args.setWriteAccessRet(true);
            return;
        }

        if (isGuestUser(userId)) {
            if (isObjectOwnedByGuest()) {
                args.setReadAccessRet(true);
                args.setWriteAccessRet(true);
            }
            return;
        }

        if (objectIsOwnedBy(userId) || userHasOverrideRole(userId)) {
            args.setReadAccessRet(true);
            args.setWriteAccessRet(true);
        }
    }

    private boolean isNotOwnable(Object obj) {
        return !(obj instanceof Owned);
    }

    private boolean userHasOverrideRole(Long userId) throws PreconditionException {
        String[] overrideRoles = args.getOverrideRoleNameArg().split(",");
        try {
            User user = UserLocalServiceUtil.getUser(userId);
            for (Role role : user.getRoles()) {
                for (int i = 0; i < overrideRoles.length; i++) {
                    if (overrideRoles[i].equals(role.getName())) {
                        return true;
                    }
                }

            }
        } catch (PortalException | SystemException e) {
            throw new PreconditionException("Roles could not be queried for user: "+userId, e);
        }

        return false;
    }

    private boolean objectIsOwnedBy(Long userId) {
        return userId.equals(args.getObjectArg().getOwningUser());
    }

    private boolean isObjectOwnedByGuest() {
        return args.getObjectArg().getOwningUser()==null;
    }

    private boolean isGuestUser(Long remoteUser) {
        return remoteUser==null;
    }
}
