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

import java.util.ArrayList;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Owned;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.UpdateException;
import com.ail.core.security.FilterListAccessibilityToUserService.FilterListAccessibilityToUserArgument;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * Object accessibility implementation which filters a list of objects passed in
 * into a second list based on their accessibility to the specified user. Objects
 * are considered accessible to users who either:<ol>
 * <li>own the object; or,</li>
 * <li>are in the same organization as the object's owner; or,</li>
 * <li>are members of an "administrative role" (the name of the administrative role is
 * defined as a service configuration parameter).</li></ol>
 */
@ServiceImplementation
public class OwnerOrganisationAndAdministratorAccessibilityFilterService extends Service<FilterListAccessibilityToUserArgument> {

    @Override
    public void invoke() throws PreconditionException, UpdateException {

        if (args.getListArg() == null) {
            throw new PreconditionException("args.getListArg() == null");
        }

        if (args.getListRet() != null) {
            throw new PreconditionException("args.getListRet() != null");
        }

        if (isUserInOverridingRole()) {
            args.setListRet(args.getListArg());
        } else {
            args.setListRet(new ArrayList<>());

            for (Object obj : args.getListArg()) {
                if (isNotOwnable(obj) || isObjectOwnedByUser((Owned) obj) || isObjectsOwnerInSameOrganisation((Owned)obj) || (isGuestUser() && isObjectOwnedByGuest((Owned) obj)) ) {
                    args.getListRet().add(obj);
                }
            }
        }
    }

    private boolean isNotOwnable(Object obj) {
        return !(obj instanceof Owned);
    }

    boolean isUserInOverridingRole() throws PreconditionException {
        String[] overrideRoles = args.getOverrideRoleNameArg().split(",");
        try {
            User user = UserLocalServiceUtil.getUser(args.getUserIdArg());
            for (Role role : user.getRoles()) {
                for (int i = 0; i < overrideRoles.length; i++) {
                    if (overrideRoles[i].equals(role.getName())) {
                        return true;
                    }
                }

            }
        } catch (PortalException | SystemException e) {
            throw new PreconditionException("Roles could not be queried for user: " + args.getUserIdArg(), e);
        }

        return false;
    }

    boolean isObjectsOwnerInSameOrganisation(Owned obj) throws PreconditionException {
        try {
            if (isObjectOwnedByGuest(obj)) {
                return false;
            }

            User owner = UserLocalServiceUtil.getUser(obj.getOwningUser());
            User user =  UserLocalServiceUtil.getUser(args.getUserIdArg());

            for(Organization o: owner.getOrganizations()) {
                if (user.getOrganizations().contains(o)) {
                    return true;
                }
            }
        } catch(NoSuchUserException e) {
            return false; // a user that does not exist cannot be in the same organisation as the current user!
        } catch (PortalException | SystemException e) {
            throw new PreconditionException("Organization could not be queried for user: "+args.getUserIdArg(), e);
        }

        return false;
    }

    boolean isObjectOwnedByUser(Owned object) {
        return args.getUserIdArg().equals(object.getOwningUser());
    }

    boolean isObjectOwnedByGuest(Owned object) {
        return object.getOwningUser() == null;
    }

    boolean isGuestUser() {
        return args.getUserIdArg() == null;
    }
}
