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

package com.ail.core.configure.server;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationHandler;

@ServiceImplementation
public class GetConfigurationService extends Service<GetConfigurationService.GetConfigurationArgument> {

    /**
     * Arg interface for the GetConfiguration entry point. The entry point takes one
     * argument: a namespace's name, and returns one result: the Configuration object
     * for the namespace. 
     */
    @ServiceArgument
    public interface GetConfigurationArgument extends Argument {
        void setConfigurationRet(Configuration configurationRet);

        Configuration getConfigurationRet();

        void setNamespaceArg(String namespace);

        String getNamespaceArg();
    }
    
    @ServiceCommand(defaultServiceClass=GetConfigurationService.class)
    public interface GetConfigurationCommand extends Command, GetConfigurationArgument {}

    /**
     * Override and return the namespace we've been asked to fetch.
     * @return The namespace we've been invoked to fetch.
     */
    @Override
    public String getConfigurationNamespace() {
        return args.getNamespaceArg();
    }

	/**
     * The 'business logic' of the entry point.
     */
    @Override
	public void invoke() throws PreconditionException {
        if (args.getNamespaceArg()==null || args.getNamespaceArg().length()==0) {
            throw new PreconditionException("namespace!=null && namespace!=\"\"");
        }

        args.setConfigurationRet(ConfigurationHandler.getInstance().loadConfiguration(this, this, core));
    }
}



