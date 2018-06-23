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

import java.util.Collection;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.ConfigurationHandler;

@ServiceImplementation
public class GetNamespacesService extends Service<GetNamespacesService.GetNamespacesArgument> {

    /**
     * Arg interface for the GetConfiguration entry point. The entry point takes one
     * argument: a namespace's name, and returns one result: the Configuration object
     * for the namespace. 
     */
    @ServiceArgument
    public interface GetNamespacesArgument extends Argument {
        /**
         * Set the namespaces collection attribute. This collections
         * contains Strings, each representing a namespace.
         * @param namespace The collection of Strings
         */
        void setNamespaces(Collection<String> namespaces);

        /**
         * Get the namespace collection.
         * @see #setNamespaces
         * @return The collection of instances of {@link com.ail.core.configure.ConfigurationSummary ConfigurationSummary}
         */
        Collection<String> getNamespaces();
    }

    /**
     * Arg interface for the GetConfiguration entry point. The entry point takes one
     * argument: a namespace's name, and returns one result: the Configuration object
     * for the namespace. 
     */
    @ServiceCommand(defaultServiceClass=GetNamespacesService.class)
    public interface GetNamespacesCommand extends Command, GetNamespacesArgument {}
    
    /**
     * Fetch the namespace collection from the Configuration handler.
     */
    @Override
    public void invoke() {
        args.setNamespaces(ConfigurationHandler.getInstance().getNamespaces());
    }
}



