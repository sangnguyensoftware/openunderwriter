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

package com.ail.core.security;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Owned;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

/**
 * Service interface for services which are used to confirm what rights a user
 * has over an object.
 */
@ServiceInterface
public interface ConfirmObjectAccessibilityToUserService {

    /**
     * Arguments required by create service
     */
    @ServiceArgument
    public interface ConfirmObjectAccessibilityToUserArgument extends Argument {
        void setObjectArg(Owned objectArg);

        Owned getObjectArg();

        void setUserIdArg(Long userId);

        Long getUserIdArg();

        void setReadAccessRet(Boolean readAccessRet);

        Boolean getReadAccessRet();

        void setWriteAccessRet(Boolean writeAccessRet);

        Boolean getWriteAccessRet();

        void setOverrideRoleNameArg(String overrideRoleNameArg);

        String getOverrideRoleNameArg();
    }

    @ServiceCommand
    public interface ConfirmObjectAccessibilityToUserCommand extends Command, ConfirmObjectAccessibilityToUserArgument {
    }
}
