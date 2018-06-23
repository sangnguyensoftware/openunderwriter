/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreProxy;
import com.ail.core.LiferayException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.product.HasPermissionService.HasPermissionArgument;
import com.liferay.portal.model.Role;

/**
 * Find if one of the current user's roles has permission
 */
@ServiceImplementation
public class LiferayHasPermissionService extends Service<HasPermissionArgument> {

    @Override
    public void invoke() throws PreconditionException {
        CoreProxy coreProxy = new CoreProxy();

        boolean permitted = false;

        if (StringUtils.isEmpty(args.getPermissionsArg())) {
            // If there is no permission arg then by default the resource is unrestricted and access is permitted
            permitted = true;
        } else {
            try {
                List<Role> userRoles = LiferayActionPermissionsHelper.getCurrentUserRoles();
                for (Iterator<Role> it = userRoles.iterator(); it.hasNext() && !permitted; ) {
                    permitted = LiferayActionPermissionsHelper.hasPermission(args.getNameArg(), args.getPermissionsArg(), it.next());
                }
            } catch (LiferayException e) {
                coreProxy.logError(e.toString());
            }
        }

        args.setPermittedRet(permitted);
    }
}


