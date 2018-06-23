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

package com.ail.core.product;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.product.liferay.LiferayHasPermissionService;

@ServiceInterface
public class HasPermissionService {

    @ServiceArgument
    public interface HasPermissionArgument extends Argument {
        /**
         * Getter for the nameArg property. The name of the action resource to check the current user role has available to them, typically com.ail.pageflow.PageElement
         * @return Value of nameArg, or null if it is unset
         */
        String getNameArg();

        /**
         * Setter for the permissionsArg property. * @see #getNameArg
         * @param nameArg new value for property.
         */
        void setNameArg(String nameArg);

        /**
         * Getter for the permissionsArg property. The name of the permission to check the current user role has available to them.
         * @return Value of permissionsArg, or null if it is unset
         */
        String getPermissionsArg();

        /**
         * Setter for the permissionsArg property. * @see #getpermissionsArg
         * @param permissionsArg new value for property.
         */
        void setPermissionsArg(String permissionsArg);

        /**
         * Getter for the permittedRet property. Whether the role has permission or not.
         * @return true if user role has permission, else false
         */
        boolean getPermittedRet();

        /**
         * Setter for the permittedRet property. * @see #getPermittedRet
         * @param permittedRet new value for property.
         */
        void setPermittedRet(boolean permittedRet);
    }

    @ServiceCommand(defaultServiceClass = LiferayHasPermissionService.class)
    public interface HasPermissionCommand extends Command, HasPermissionArgument {}
}


