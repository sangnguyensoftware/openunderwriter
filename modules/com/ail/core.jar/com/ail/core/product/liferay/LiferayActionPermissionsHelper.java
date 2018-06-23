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
package com.ail.core.product.liferay;

import static com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS;
import static com.liferay.portal.model.ResourceConstants.SCOPE_COMPANY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ail.core.CoreContext;
import com.ail.core.LiferayException;
import com.ail.core.PreconditionException;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.model.ResourcePermission;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * Utility methods to create, assign and retrieve Liferay resource permissions.
 *
 * There are 2 parts to these, ResourceActions and ResourcePermissions.
 *
 * ResourceActions are actions that are available for a particular resource, e.g. VIEW action on a product quotation flow welcome page.
 *
 * ResourcePermissions assign these permissions to particular roles, e.g. an Underwriter role can have 2 resource permissions which
 * correspond to VIEW and UPDATE actions on a product quotation flow quote details page.
 *
 * Proof of concept code placed here temporarily for convenience. Shows how to programatically add Liferay permissions.
 * Currently set for the existing actions VIEW and UPDATE but can be used to create any arbitrarily named actions and allow roles to use them.
 *
 */
public class LiferayActionPermissionsHelper {

    /**
     * Adds a ResourceAction for the requested Action
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId  The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @throws LiferayException
     */
    public static void addAction(String name, String actionId) throws LiferayException {
        try {
            if (getResourceAction(name, actionId) == null) {
                // Only add if it does not already exist
                long resourceActionId = CounterLocalServiceUtil.increment(ResourceAction.class.getName());
                int existingActionsCount = ResourceActionLocalServiceUtil.getResourceActions(name).size();
                ResourceAction resourceAction = ResourceActionLocalServiceUtil.createResourceAction(resourceActionId);
                resourceAction.setName(name);
                resourceAction.setActionId(actionId);
                resourceAction.setBitwiseValue((long) Math.pow(2, existingActionsCount));

                ResourceActionLocalServiceUtil.addResourceAction(resourceAction);
            }
        } catch (SystemException e) {
            throw new LiferayException("Failed to add " + actionId + " permission to " + name, e);
        }
    }

    /**
     * Gives the specified role permission for the specified action on a resource. Sets the permission at Company scope.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @param roleName  The role name, e.g. Underwriter
     * @throws LiferayException
     */
    public static void givePermission(String name, String actionId, String roleName) throws PreconditionException, LiferayException {
        if (hasPermission(name, actionId, roleName)) {
            // Don't add it again!
            return;
        }

        long companyId = PortalUtil.getCompanyId(CoreContext.getRequestWrapper().getServletRequest());

        ResourceAction resourceAction = getResourceAction(name, actionId);
        if (resourceAction == null) {
            throw new PreconditionException("Cannot give " + roleName + " " + actionId + " permission on " + name + " as the permission does not yet exist.");
        }

        Role role = getRoleForName(roleName);

        try {
            long resourcePermissionId = CounterLocalServiceUtil.increment(ResourcePermission.class.getName());

            ResourcePermission resourcePermission = ResourcePermissionLocalServiceUtil.createResourcePermission(resourcePermissionId);
            resourcePermission.setActionIds(resourceAction.getResourceActionId());
            resourcePermission.setName(resourceAction.getName());
            resourcePermission.setCompanyId(companyId);
            resourcePermission.setPrimKey("" + resourcePermissionId);
            resourcePermission.setScope(SCOPE_COMPANY);
            resourcePermission.setRoleId(role.getRoleId());

            ResourcePermissionLocalServiceUtil.addResourcePermission(resourcePermission);
        } catch (SystemException e) {
            throw new LiferayException("Failed to give " + actionId + " permission on " + name + " for " + role.getName(), e);
        }
    }

    /**
     * Gets a complete list of permissions for a given role.
     * @param roleName  The role name, e.g. Underwriter
     * @return  A list of {@link LiferayResourceActionPermission} giving some light detail about each permission
     * @throws LiferayException
     */
    public static List<LiferayResourceActionPermission> getPermissions(String roleName) throws LiferayException {
        List<LiferayResourceActionPermission> liferayActionPermissions = new ArrayList<>();

        Role role = getRoleForName(roleName);

        try {
            List<ResourcePermission> resourcePermissions = ResourcePermissionLocalServiceUtil.getRoleResourcePermissions(role.getRoleId());
            for (ResourcePermission resourcePermission : resourcePermissions) {
                try {
                    ResourceAction resourceAction = ResourceActionLocalServiceUtil.fetchResourceAction(resourcePermission.getActionIds());
                    if (resourceAction != null) {
                        LiferayResourceActionPermission resourceActionPermission = new LiferayResourceActionPermission();
                        resourceActionPermission.setPageName(resourceAction.getName());
                        resourceActionPermission.setRoleName(role.getName());
                        resourceActionPermission.setActionId(resourceAction.getActionId());

                        liferayActionPermissions.add(resourceActionPermission);
                    }
                } catch (SystemException e1) {
                    // continue trying the next one
                }

            }
        } catch (SystemException e) {
            throw new LiferayException("Failed to get permissions for " + role.getName(), e);
        }

        return liferayActionPermissions;
    }

    /**
     * Get the list of liferay user roles for the current user
     * @return
     * @throws LiferayException
     */
    public static List<Role> getCurrentUserRoles() throws LiferayException {
        try {
            return UserLocalServiceUtil.getUser(CoreContext.getRemoteUser().longValue()).getRoles();
        } catch (SystemException | PortalException e) {
            throw new LiferayException("Failed to get roles for the current user", e);
        }
    }

    /**
     * Checks to see if a role has permission to perform an action on a resource, at Company scope.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @param roleName  The name of the user role, e.g. Underwriter
     * @return  true if the role has permission to perform the action on the resource, else false
     * @throws LiferayException
     */
    public static boolean hasPermission(String name, String actionId, String roleName) throws LiferayException {
        return hasPermission(name, actionId, getRoleForName(roleName));

    }

    /**
     * Tries to get a liferay role for the given name
     * @param roleName  The name of the user role, e.g. Underwriter
     * @return
     * @throws LiferayException
     */
    private static Role getRoleForName(String roleName) throws LiferayException {
        try {
            long companyId = PortalUtil.getCompanyId(CoreContext.getRequestWrapper().getServletRequest());
            return RoleLocalServiceUtil.fetchRole(companyId, roleName);
        } catch (SystemException e) {
            throw new LiferayException("Failed to get Role for " + roleName, e);
        }
    }

    /**
     * Checks to see if a role has permission to perform an action on a resource, at Company scope.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @param role  The user role, e.g. the role of Underwriter
     * @return  true if the role has permission to perform the action on the resource, else false
     * @throws LiferayException
     */
    public static boolean hasPermission(String name, String actionId, Role role) throws LiferayException {
        long companyId = PortalUtil.getCompanyId(CoreContext.getRequestWrapper().getServletRequest());
        try {
            return ResourcePermissionLocalServiceUtil.hasScopeResourcePermission(companyId, name, SCOPE_COMPANY, role.getRoleId(), actionId);
        } catch (SystemException | PortalException e) {
            // hasScopeResourcePermission should have found the resource, but if not we need to go the long way round, getting all permissions
            // for this role in COMPANY scope and testing them against the name and actionId
            try {
                List<ResourcePermission> resourcePermissions = ResourcePermissionLocalServiceUtil.getRoleResourcePermissions(
                                                                                                role.getRoleId(), new int[] {SCOPE_COMPANY}, ALL_POS, ALL_POS);
                for (ResourcePermission resourcePermission : resourcePermissions) {
                    if (resourcePermission.getCompanyId() == companyId && resourcePermission.getName().equals(name)) {
                        try {
                            ResourceAction resourceAction = ResourceActionLocalServiceUtil.fetchResourceAction(resourcePermission.getActionIds());
                            if (resourceAction.getActionId().equals(actionId)) {
                                return true;
                            }
                        } catch (SystemException e1) {
                            // continue trying the next one
                        }
                    }
                }
            } catch (SystemException e1) {
                throw new LiferayException("Failed to check " + actionId + " permission on " + name + " for " + role.getName(), e1);
            }
        }

        return false;
    }

    /**
     * Removes a permission on a resource from the role, if it currently exists.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @param roleName  The name of the user role, e.g. Underwriter
     * @throws LiferayException
     */
    public static void removePermission(String name, String actionId, String roleName) throws LiferayException {
        removePermission(name, actionId, getRoleForName(roleName));
    }

    /**
     * Removes a permission on a resource from the role, if it currently exists.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @param role  The user role, e.g. the role of Underwriter
     * @throws LiferayException
     */
    public static void removePermission(String name, String actionId, Role role) throws LiferayException {
        ResourcePermission resourcePermission = getPermission(name, actionId, role);
        if (resourcePermission != null) {
            try {
                ResourcePermissionLocalServiceUtil.deleteResourcePermission(resourcePermission);
            } catch (SystemException e) {
                throw new LiferayException("Failed to remove " + actionId + " permission on " + name + " for " + role.getName(), e);
            }
        }
    }

    /**
     * Gets the ResourcePermission for the given arguments, at Company scope.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @param role  The user role, e.g. the role of Underwriter
     * @return  the ResourcePermission, or null if there is not one
     * @throws LiferayException
     */
    private static ResourcePermission getPermission(String name, String actionId, Role role) throws LiferayException {
        try {
            long companyId = PortalUtil.getCompanyId(CoreContext.getRequestWrapper().getServletRequest());
            List<ResourcePermission> resourcePermissions = ResourcePermissionLocalServiceUtil.getRoleResourcePermissions(
                                                                                            role.getRoleId(), new int[] {SCOPE_COMPANY}, ALL_POS, ALL_POS);
            for (ResourcePermission resourcePermission : resourcePermissions) {
                if (resourcePermission.getCompanyId() == companyId && resourcePermission.getName().equals(name)) {
                    try {
                        ResourceAction resourceAction = ResourceActionLocalServiceUtil.fetchResourceAction(resourcePermission.getActionIds());
                        if (resourceAction.getActionId().equals(actionId)) {
                            return resourcePermission;
                        }
                    } catch (SystemException e1) {
                        // continue trying the next one
                    }
                }
            }
        } catch (SystemException e1) {
            throw new LiferayException("Failed to get " + actionId + " permission on " + name + " for " + role.getName(), e1);
        }

        return null;
    }

    /**
     * Gets the ResourceAction for a specified actionId. Tries several different Liferay API calls to get this as some of
     * them are not reliable for some reason.
     * @param name    The resource name, typically com.ail.pageflow.PageElement
     * @param actionId    The action id, e.g. ViewFinancialInformation, ViewClaimsInformation
     * @return  The ResourceAction or null if it cannot be found
     * @throws LiferayException
     */
    private static ResourceAction getResourceAction(String name, String actionId) throws LiferayException {
        ResourceAction resourceAction;
        try {
            resourceAction = ResourceActionLocalServiceUtil.getResourceAction(name, actionId); // should work but may not!
        } catch (PortalException e) {
            resourceAction = ResourceActionLocalServiceUtil.fetchResourceAction(name, actionId); // should work but may not!
        }

        if (resourceAction == null) {
            try {
                ResourceAction ra;
                for (Iterator<ResourceAction> it = ResourceActionLocalServiceUtil.getResourceActions(name).iterator(); it.hasNext() && resourceAction == null; ) {
                    ra = it.next();
                    if (name.equals(ra.getName()) && actionId.equals(ra.getActionId())) {
                        resourceAction = ra;
                    }
                }
            } catch (SystemException e) {
                throw new LiferayException("Failed to get " + actionId + " permission on " + name, e);
            }
        }

        return resourceAction;
    }
}
