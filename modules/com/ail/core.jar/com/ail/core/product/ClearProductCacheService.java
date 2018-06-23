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

package com.ail.core.product;

import java.util.Collection;
import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

@ServiceImplementation
public class ClearProductCacheService extends Service<ClearProductCacheService.ClearProductCacheArgument> {

    /**
     * Arg interface for the ClearConfigurationCache entry point. The entry
     * point takes one argument: a namespace's name, and returns one result: a
     * collection of Strings representing the namesapces whose caches have been
     * cleared. objects representing the namespace's history.
     */
    @ServiceArgument
    public interface ClearProductCacheArgument extends Argument {
        /**
         * Set the namespaces collection attribute. This collections contains
         * Strings representing the namespaces reset.
         * 
         * @param namespace
         *            The collection of Strings.
         */
        void setNamespacesRet(Collection<String> namespaces);

        /**
         * Get the namespace collection representing the namespaces that have
         * been reset.
         * 
         * @see #setNamespaces
         * @return The collection of Strings.
         */
        Collection<String> getNamespacesRet();

        /**
         * The namespace arg tells the command which namespace to clear the
         * cache for.
         * 
         * @param namespace
         *            The namespace to return the history for.
         */
        void setProductNameArg(String namespace);

        /**
         * @see #setProductNameArg(String)
         * @return The namespace to clear the cache for.
         */
        String getProductNameArg();
    }

    @ServiceCommand(defaultServiceClass = ClearProductCacheService.class)
    public interface ClearProductCacheCommand extends Command, ClearProductCacheArgument {
    }

    /**
     * Fetch the namespace collection from the Configuration handler.
     */
    @Override
    public void invoke() throws PreconditionException, PostconditionException {

        if (args.getProductNameArg() == null || args.getProductNameArg().length() == 0) {
            throw new PreconditionException("args.getProductNameArg() == null || args.getProductNameArg().length()==0");
        }

        String namespace = Functions.productNameToConfigurationNamespace(args.getProductNameArg());

        List<String> res = getCore().clearConfigurationCache(namespace);

        args.setNamespacesRet(res);

        for(String space: res) {
            core.logInfo("Cache successfuly cleared for namespace: "+space);
        }
        
        if (args.getNamespacesRet() == null) {
            throw new PostconditionException("args.getNamespacesRet()==null");
        }
    }
}
