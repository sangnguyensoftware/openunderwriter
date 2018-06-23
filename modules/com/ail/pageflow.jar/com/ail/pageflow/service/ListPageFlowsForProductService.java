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

import static com.ail.core.Functions.productNameToConfigurationNamespace;

import java.util.HashSet;
import java.util.Set;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Types;
import com.ail.pageflow.PageFlow;

/**
 * Fetch a list of the PageFlows supported by a named product. The list will
 * contain the names of all of the types defined in the product's namespace
 * which are instances of PageFlow, it will also contain the PageFlow types
 * defined in all of the parent namespaces. Only unique types are included;
 * therefore if both the product and its parent define a PageFlow type named
 * "NewBusiness", only one will be included in the returned list. This is
 * consistent because it would not be possible, from the given namespace, to
 * instantiate both the local type and the one defined by the parent.
 */
@ServiceImplementation
public class ListPageFlowsForProductService extends Service<ListPageFlowsForProductService.ListPageFlowsForProductArgument> {
    public static String CONFIGURATION_NOT_FOUND = "getCore().getConfiguration()==null";
    private static final String PAGEFLOW_CLASSNAME = PageFlow.class.getName();
    private String namespace;

    @ServiceArgument
    public interface ListPageFlowsForProductArgument extends Argument {
        void setProductNameArg(String productNameArg);

        String getProductNameArg();

        void setPageFlowNameRet(Set<String> pageFlowNameRet);

        Set<String> getPageFlowNameRet();
    }

    @ServiceCommand(defaultServiceClass = ListPageFlowsForProductService.class)
    public interface ListPageFlowsForProductCommand extends Command, ListPageFlowsForProductArgument {
    }

    @Override
    public String getConfigurationNamespace() {
        return namespace;
    }

    @Override
    public void invoke() throws BaseException {
        if (args.getProductNameArg() == null || args.getProductNameArg().length() == 0) {
            throw new PreconditionException("args.getProductNameArg()==null || args.getProductNameArg().length()==0");
        }

        Set<String> results = new HashSet<String>();

        appendPageFlowTypesToList(productNameToConfigurationNamespace(args.getProductNameArg()), results);

        args.setPageFlowNameRet(results);

        if (args.getPageFlowNameRet() == null) {
            throw new PostconditionException("args.getProductNameArg()==null");
        }
    }

    /**
     * Recursively add the pageflows from the named namespace, and all of that
     * namespace's parent namespaces, to <code>result</code>.
     * 
     * @param namespace
     * @param results
     * @throws PreconditionException
     */
    private void appendPageFlowTypesToList(String namespace, Set<String> results) throws PreconditionException {
        if (namespace != null) {
            this.namespace = namespace;

            Configuration config = getCore().getConfiguration();

            if (config == null) {
                throw new PreconditionException(CONFIGURATION_NOT_FOUND);
            }

            Types types = config.getTypes();

            if (types != null) {
                for (int i = 0; i < types.getTypeCount(); i++) {
                    if (PAGEFLOW_CLASSNAME.equals(types.getType(i).getKey())) {
                        results.add(types.getType(i).getName());
                    }
                }
            }

            appendPageFlowTypesToList(config.getParentNamespace(), results);
        }
    }
}