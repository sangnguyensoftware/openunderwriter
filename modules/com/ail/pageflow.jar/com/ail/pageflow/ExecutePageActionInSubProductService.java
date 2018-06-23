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

package com.ail.pageflow;

import static com.ail.core.product.Product.AGGREGATOR_CONFIGURATION_GROUP_NAME;
import static java.lang.Boolean.TRUE;

import java.util.ArrayList;
import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Parameter;
import com.ail.pageflow.ExecutePageActionInSubProductService.ExecutePageActionInSubProductArgument;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;

@ServiceImplementation
public class ExecutePageActionInSubProductService extends Service<ExecutePageActionInSubProductArgument> {

    @ServiceArgument
    public interface ExecutePageActionInSubProductArgument extends Argument {
        String getProductTypeIdArg();

        void setProductTypeIdArg(String productTypeIdArg);

        Boolean getExecuteInAllSubProductsArg();

        void setExecuteInAllSubProductsArg(Boolean executeInAllSubProductsArg);

        Type getModelArgRet();

        void setModelArgRet(Type modelArgRet);

        String getCommandNameArg();

        void setCommandNameArg(String commandNameArg);

        Boolean getValidationFailedRet();

        void setValidationFailedRet(Boolean validationFailedRet);
    }

    @ServiceCommand(defaultServiceClass = ExecutePageActionInSubProductService.class)
    public interface ExecutePageActionInSubProductCommand extends Command, ExecutePageActionInSubProductArgument {
    }

    @Override
    public void invoke() throws PreconditionException {
        if ((args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0) && args.getExecuteInAllSubProductsArg() == false) {
            throw new PreconditionException("(args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0) && args.getExecuteInAllSubProductsArg() == false");
        }

        if (args.getProductTypeIdArg() != null && TRUE.equals(args.getExecuteInAllSubProductsArg())) {
            throw new PreconditionException("args.getProductTypeIdArg() != null && args.getExecuteInAllSubProductsArg() == true");
        }

        if (args.getCommandNameArg() == null || args.getCommandNameArg().length() == 0) {
            throw new PreconditionException("args.getCommandNameArg() == null || args.getCommandNameArg().length() == 0");
        }

        if (args.getModelArgRet() == null) {
            throw new PreconditionException("args.getModelArgRet() == null");
        }

        CoreProxy savedCoreProxy = PageFlowContext.getCoreProxy();
        String savedProductName = PageFlowContext.getProductName();

        boolean validationFailed = false;

        for (String productTypeId : targetProducts()) {
            try {
                PageFlowContext.setProductName(productTypeId);
                PageFlowContext.setCoreProxy(new CoreProxy(Functions.productNameToConfigurationNamespace(productTypeId)));
                ExecutePageActionCommand command = executeInProduct(args.getModelArgRet());
                args.setModelArgRet(command.getModelArgRet());
                validationFailed |= command.getValidationFailedRet();
            } finally {
                PageFlowContext.setCoreProxy(savedCoreProxy);
                PageFlowContext.setProductName(savedProductName);
            }
        }

        args.setValidationFailedRet(validationFailed);
    }

    List<String> targetProducts() throws PreconditionException {
        List<String> products = new ArrayList<>();

        if (args.getProductTypeIdArg() != null) {
            products.add(args.getProductTypeIdArg());
        } else if (args.getExecuteInAllSubProductsArg()) {
            for (Parameter p : PageFlowContext.getCoreProxy().getGroup(AGGREGATOR_CONFIGURATION_GROUP_NAME).getParameter()) {
                products.add(p.getName());
            }
        } else {
            throw new PreconditionException("one of args.getProductTypeIdArg() or args.getExecuteInAllSubProductsArg() must be set");
        }

        return products;
    }

    ExecutePageActionCommand executeInProduct(Type model) throws RenderingError {
        ExecutePageActionCommand command = PageFlowContext.getCoreProxy().newCommand(ExecutePageActionCommand.class);

        command.setServiceNameArg(args.getCommandNameArg());
        command.setModelArgRet(model);

        try {
            command.invoke();
        } catch (Throwable e) {
            throw new RenderingError("Failed to execute action command: " + args.getCommandNameArg(), e);
        }

        return command;
    }
}