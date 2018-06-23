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
public class SetCommandScriptService extends Service<SetCommandScriptService.SetCommandScriptArgument> {

    @ServiceArgument
    public interface SetCommandScriptArgument extends Argument {
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
         * Fetch the value of the commandScriptArg argument. The command script to be saved.
         * @see #setCommandScriptArg
         * @return value of commandScriptArg
         */
        CommandScript getCommandScriptArg();

        /**
         * Set the value of the commandScriptArg argument. The command script saved.
         * @see #getCommandScriptArg
         * @param commandScriptArg New value for commandScriptArg argument.
         */
        void setCommandScriptArg(CommandScript commandScriptArg);

        /**
         * Fetch the value of the commandTypeArg. This defines the 'type' of script, e.g.
         * BeanShell script, DRools script, etc.
         * @return The script type.
         */
        String getCommandTypeArg();

        /**
         * Set the value of the commandTypeArg.
         * @see #getCommandTypeArg
         * @param commandTypeArg
         */
        void setCommandTypeArg(String commandTypeArg);
    }

    @ServiceCommand(defaultServiceClass=SetCommandScriptService.class)
    public interface SetCommandScriptCommand extends Command, SetCommandScriptArgument {
    }
    
    /**
     * Return the namespace of the configuration we're updating
     * @return The classes namespace
     */
    @Override
    public String getConfigurationNamespace() {
        return (args.getNamespaceArg()!=null) ? args.getNamespaceArg() : args.getCommandScriptArg().getNamespace();
    }

    @Override
    public void invoke() throws PreconditionException {
        if (args.getCommandScriptArg()==null) {
            throw new PreconditionException("commandScriptArg==null");
        }

        // For this sevice the args can either be specified seperatly in the args (as is normal)
        // or within the CommandScript arg. The args ones have priority
        String namespaceArg   = (args.getNamespaceArg()!=null) ? args.getNamespaceArg() : args.getCommandScriptArg().getNamespace();
        String commandNameArg = (args.getCommandNameArg()!=null) ? args.getCommandNameArg() : args.getCommandScriptArg().getCommandName();
        String commandTypeArg = (args.getCommandTypeArg()!=null) ? args.getCommandTypeArg() : args.getCommandScriptArg().getType();

        if (namespaceArg==null || namespaceArg.length()==0) {
            throw new PreconditionException("namespace==null || namespace==''");
        }

        if (commandNameArg==null || commandNameArg.length()==0) {
            throw new PreconditionException("typeNameArg==null || typeNameArg==''");
        }

        if (commandTypeArg==null || commandTypeArg.length()==0) {
            throw new PreconditionException("commandTypeArg==null || commandTypeArg==''");
        }

        try {
            // Get configuration for specified namespace
            Configuration config=core.getConfiguration();

            // First, find the type and its accessor. Then param's name should be Service, but accept "Accessor"
            // too for backwards compatibility reasons.
            String accessorTypeName;
            try {
              accessorTypeName=config.findType(args.getCommandNameArg()).findParameter("Service").getValue();
            } catch(NullPointerException e) {
              accessorTypeName=config.findType(args.getCommandNameArg()).findParameter("Accessor").getValue();
            }

            Type accessorType=config.findType(accessorTypeName);

            accessorType.setKey("com.ail.core.command."+commandTypeArg+"Accessor");
            accessorType.findParameter("Script").setValue(args.getCommandScriptArg().getScript());

            // Set configuration for namespace
            core.setConfiguration(config);
        }
        catch(NullPointerException e) {
            throw new PreconditionException("Script or accessor not defined for command:"+commandNameArg+" in namespace:"+namespaceArg);
        }
    }
}


