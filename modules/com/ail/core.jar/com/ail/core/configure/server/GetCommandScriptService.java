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
import com.ail.core.CommandScript;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Type;

@ServiceImplementation
public class GetCommandScriptService extends Service<GetCommandScriptService.GetCommandScriptArgument> {
    
    @ServiceArgument
    public interface GetCommandScriptArgument extends Argument {
        /**
         * Fetch the value of the namespaceArg argument. The namespace to fetch the command script from.
         * @see #setNamespaceArg
         * @return value of namespaceArg
         */
        String getNamespaceArg();

        /**
         * Set the value of the namespaceArg argument. The namespace to fetch the command script from.
         * @see #getNamespaceArg
         * @param namespaceArg New value for namespaceArg argument.
         */
        void setNamespaceArg(String namespaceArg);

        /**
         * Fetch the value of the commandNameArg argument. The name of the command that which the script should be returned for.
         * @see #setCommandNameArg
         * @return value of commandNameArg
         */
        String getCommandNameArg();

        /**
         * Set the value of the commandNameArg argument. The name of the command that which the script should be returned for.
         * @see #getCommandNameArg
         * @param commandNameArg New value for commandNameArg argument.
         */
        void setCommandNameArg(String commandNameArg);

        /**
         * Fetch the value of the commandScriptRet argument. The command script returned.
         * @see #setCommandScriptRet
         * @return value of commandScriptRet
         */
        CommandScript getCommandScriptRet();

        /**
         * Set the value of the commandScriptRet argument. The command script returned.
         * @see #getCommandScriptRet
         * @param commandScriptRet New value for commandScriptRet argument.
         */
        void setCommandScriptRet(CommandScript commandScriptRet);
    }

    @ServiceCommand(defaultServiceClass=GetCommandScriptService.class)
    public interface GetCommandScriptCommand extends Command, GetCommandScriptArgument {}
    
    
    /**
     * When the core asks us for our namespace, return the one
     * we're getting information from.
     * @return Namespace as defined by the service arguments.
     */
    @Override
    public String getConfigurationNamespace() {
        return args.getNamespaceArg();
    }

    /** The 'business logic' of the entry point. */
    @Override
    public void invoke() throws PreconditionException {
        if (args.getNamespaceArg()==null || args.getNamespaceArg().length()==0) {
            throw new PreconditionException("namespace==null || namespace==''");
        }

        if (args.getCommandNameArg()==null || args.getCommandNameArg().length()==0) {
            throw new PreconditionException("commandName==null || commandName==''");
        }

        try {
            // Get the configuration
            Configuration config=core.getConfiguration();

            // First, find the type and its accessor. Then param's name should be Service, but accept "Accessor"
            // too for backwards compatibility reasons.
            String accessorTypeName;
            try {
              accessorTypeName=config.findType(args.getCommandNameArg()).findParameter("Service").getValue();
            } catch(NullPointerException e) {
              accessorTypeName=config.findType(args.getCommandNameArg()).findParameter("Accessor").getValue();
            }

            // Second, find the accessor (type)
            Type accessorType=config.findType(accessorTypeName);
            String accessorName=accessorType.getKey();

            // Create the command script, and populate it
            CommandScript ret=new CommandScript();
            ret.setScript(accessorType.findParameter("Script").getValue());
            ret.setType(accessorName.substring(accessorName.lastIndexOf('.')+1, accessorName.length()-8));
            ret.setCommandName(args.getCommandNameArg());
            ret.setNamespace(args.getNamespaceArg());

            args.setCommandScriptRet(ret);
        }
        catch(NullPointerException e) {
            throw new PreconditionException("Script or accessor not defined for command:"+args.getCommandNameArg()+" in namespace:"+args.getNamespaceArg());
        }
    }
}


