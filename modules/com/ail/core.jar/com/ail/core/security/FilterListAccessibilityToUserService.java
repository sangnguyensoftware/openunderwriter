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

package com.ail.core.security;

import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

/**
 * Service interface for services filter a supplied list of objects into
 * a second list. The second list will contain only objects which the specified
 * user has permission to access.
 */
@ServiceInterface
public interface FilterListAccessibilityToUserService {

    /**
     * Arguments required by create service
     */
    @ServiceArgument
    public interface FilterListAccessibilityToUserArgument extends Argument {
        void setListArg(List<Object> listArg);

        List<Object> getListArg();

        void setListRet(List<Object> listRet);

        List<Object> getListRet();

        void setUserIdArg(Long userId);

        Long getUserIdArg();

        void setOverrideRoleNameArg(String overrideRoleNameArg);

        String getOverrideRoleNameArg();
    }

    @ServiceCommand
    public interface FilterListAccessibilityToUserCommand extends Command, FilterListAccessibilityToUserArgument {
    }
}
