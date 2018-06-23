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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ail.core.CoreProxy;
import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class LiferayUserGroupDelegate {

    private static final String JBPM_DEFAULT_ROLE = "Administrators";
    private static final String JBPM_DEFAULT_USER = "Administrator";
    private static final String LIFERAY_DEFAULT_ROLE = "Product Administrator";

    Long companyId;
    CoreProxy coreProxy;

    public LiferayUserGroupDelegate() {
        companyId = 10157L;
        coreProxy = new CoreProxy();
    }

    public boolean existsGroup(String group) {
        if (JBPM_DEFAULT_ROLE.equals(group)) {
            return true;
        }

        try {
            return RoleLocalServiceUtil.getRole(companyId, group) != null;
        } catch (NoSuchRoleException e) {
            return false;
        } catch (PortalException | SystemException e) {
            coreProxy.logWarning("Error ("+e.getMessage()+") looking up group: "+group);
        }

        return false;
    }

    public boolean existsUser(String username) {
        if (JBPM_DEFAULT_USER.equals(username)) {
            return true;
        }

        try {
            return UserLocalServiceUtil.getUserByEmailAddress(companyId, username) != null;
        } catch (NoSuchUserException e) {
            return false;
        } catch (PortalException | SystemException e) {
            coreProxy.logWarning("Error ("+e.getMessage()+") looking up user: "+username);
        }

        return false;
    }

    public List<String> getGroupsForUser(String username, List<String> groupIds, List<String> allExistingGroupIds) {
        List<String> results = new ArrayList<>();

        try {
            User user = UserLocalServiceUtil.getUserByEmailAddress(companyId, username);

            for (Role role : user.getRoles()) {
                results.add(role.getName());
            }

            if (results.contains(LIFERAY_DEFAULT_ROLE)) {
                results.add(JBPM_DEFAULT_ROLE);
            }
        } catch (NoSuchUserException e) {
            // ignore - the empty result list will be returned
        } catch (PortalException | SystemException e) {
            coreProxy.logWarning("Error ("+e.getMessage()+") looking up groups for user: "+username);
        }

        return results;
    }

    public String getDisplayName(String username) {
        if (JBPM_DEFAULT_ROLE.equals(username) || JBPM_DEFAULT_USER.equals(username)) {
            return username;
        }
        try {
            return UserLocalServiceUtil.getUserByEmailAddress(companyId, username).getScreenName();
        } catch (PortalException | SystemException e) {
            return username;
        }
    }

    public String getEmailForEntity(String entity) {
        if (JBPM_DEFAULT_ROLE.equals(entity) || JBPM_DEFAULT_USER.equals(entity)) {
            return null;
        }

        try {
            return UserLocalServiceUtil.getUserByEmailAddress(companyId, entity).getEmailAddress();
        } catch (PortalException | SystemException e) {
            return null;
        }
    }

    public String getLanguageForEntity(String entity) {
        if (JBPM_DEFAULT_ROLE.equals(entity) || JBPM_DEFAULT_USER.equals(entity)) {
            return null;
        }

        try {
            return UserLocalServiceUtil.getUserByEmailAddress(companyId, entity).getLanguageId();
        } catch (PortalException | SystemException e) {
            return null;
        }
    }

    public Set<String> getMembersForGroup(String groupName) {
        Set<String> members = new HashSet<>();

        String roleName = JBPM_DEFAULT_ROLE.equals(groupName) ? LIFERAY_DEFAULT_ROLE : groupName;

        try {
            Role role = RoleLocalServiceUtil.fetchRole(companyId, roleName);

            if (role == null) {
                coreProxy.logWarning("Role by name: '"+roleName+" not found.");
                return members;
            }

            Long roleId = role.getRoleId();

            for (User user : UserLocalServiceUtil.getRoleUsers(roleId)) {
                members.add(user.getEmailAddress());
            }
        } catch (SystemException e) {
            coreProxy.logWarning("Error ("+e.getMessage()+") looking up member for group: "+groupName);
        }

        return members;
    }

    public boolean hasEmail(String groupName) {
        return true;
    }
}
