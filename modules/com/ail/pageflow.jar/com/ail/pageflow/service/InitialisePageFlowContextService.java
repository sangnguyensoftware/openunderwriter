/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.pageflow.service;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Parameter;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;

@ServiceImplementation
public class InitialisePageFlowContextService extends Service<InitialisePageFlowContextService.InitialisePageFlowContextArgument> {

    @ServiceArgument
    public interface InitialisePageFlowContextArgument extends Argument {
    }

    @ServiceCommand(defaultServiceClass = InitialisePageFlowContextService.class)
    public interface InitialisePageFlowContextCommand extends Command, InitialisePageFlowContextArgument {
    }

    @Override
    public String getConfigurationNamespace() {
        return "AIL.Base.Registry";
    }

    @Override
    public void invoke() throws BaseException {
        if (getCore().getConfiguration() == null) {
            throw new PreconditionException("getCore().getConfiguration() == null");
        }

        if (getCore().getConfiguration().findGroup("PageFlowInitialisationActions") == null) {
            throw new PreconditionException("getCore().getConfiguration().findGroup('PageFlowInitialisationActions') == null");
        }

        for(Parameter p: getCore().getConfiguration().findGroup("PageFlowInitialisationActions").getParameter()) {
            ExecutePageActionCommand command = getCore().newCommand(p.getName(), ExecutePageActionCommand.class);
            command.setModelArgRet(null);
            command.setServiceNameArg(p.getName());
            command.invoke();
        }
    }
}