/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.ConfigurationSummary;

@ServiceImplementation
public class GetNamespacesHistoryService extends Service<GetNamespacesHistoryService.GetNamespacesHistoryArgument> {

    /**
     * Arg interface for the GetNamesapcesHisotyr entry point. The entry point takes one
     * argument: a namespace's name, and returns one result: a collection of 
     * {@link com.ail.core.configure.ConfigurationSummary ConfigurationSummary}
     * objects representing the namespace's history.
     */
    @ServiceArgument
    public interface GetNamespacesHistoryArgument extends Argument {
        /**
         * Set the namespaces collection attribute. This collections
         * contains {@link com.ail.core.configure.ConfigurationSummary ConfigurationSummary}, each representing a point in the namespace's history.
         * @param namespace The collection of {@link com.ail.core.configure.ConfigurationSummary ConfigurationSummary}.
         */
        void setNamespacesRet(Collection<ConfigurationSummary> namespaces);

        /**
         * Get the namespace collection.
         * @see #setNamespaces
         * @return The collection of instances of {@link com.ail.core.configure.ConfigurationSummary ConfigurationSummary}
         */
        Collection<ConfigurationSummary> getNamespacesRet();

        /**
         * The namespace arg tells the command which namespace to return the history for.
         * @param namespace The namespace to return the history for.
         */
        void setNamespaceArg(String namespace);

        /**
         * @see #setNamespaceArg(String)
         * @return The namespace to return the history for.
         */
        String getNamespaceArg();
     }
    
    /**
     * Arg interface for the GetNamesapcesHisotyr entry point. The entry point takes one
     * argument: a namespace's name, and returns one result: a collection of 
     * {@link com.ail.core.configure.ConfigurationSummary ConfigurationSummary}
     * objects representing the namespace's history.
     */
    @ServiceCommand(defaultServiceClass=GetNamespacesHistoryService.class)
    public interface GetNamespacesHistoryCommand extends Command, GetNamespacesHistoryArgument {}
    
    
    /**
     * Fetch the namespace collection from the Configuration handler.
     */
    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        if (args.getNamespaceArg()==null) {
            throw new PreconditionException("args.getNamespaceArg()==null");
        }
        
        args.setNamespacesRet(ConfigurationHandler.getInstance().getNamespacesHistorySummary(args.getNamespaceArg()));

        if (args.getNamespacesRet()==null) {
            throw new PostconditionException("args.getNamespacesRet()==null");
        }
    }
}



