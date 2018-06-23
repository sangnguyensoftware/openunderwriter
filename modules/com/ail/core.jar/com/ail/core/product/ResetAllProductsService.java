/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.product.ResetProductService.ResetProductCommand;
import com.ail.core.product.ScanForProductDefinitionsService.ScanForProductDefinitionsCommand;

@ServiceImplementation
public class ResetAllProductsService extends Service<ResetAllProductsService.ResetAllProductsArgument> {
    @ServiceArgument
    public interface ResetAllProductsArgument extends Argument {
    }

    @ServiceCommand(defaultServiceClass = ResetAllProductsService.class)
    public interface ResetAllProductsCommand extends Command, ResetAllProductsArgument {
    }

    @Override
    public void invoke() throws PreconditionException {
        CoreProxy coreProxy = CoreContext.getCoreProxy();
        ScanForProductDefinitionsCommand listProductsCommand = coreProxy.newCommand(ScanForProductDefinitionsCommand.class);
        ResetProductCommand resetProductCommand = coreProxy.newCommand(ResetProductCommand.class);

        try {
            listProductsCommand.invoke();
        } catch (BaseException e) {
            coreProxy.logError("Failed to get product list" + e);
            throw new PreconditionException("listProductsCommand.invoke() threw an exception");
        }

        for (String productTypeID : listProductsCommand.getProductTypeIDsRet()) {
            resetProductCommand.setProductNameArg(productTypeID);

            try {
                resetProductCommand.invoke();
            } catch (BaseException e) {
                coreProxy.logError("Failed to reset product '" + resetProductCommand.getProductNameArg() + "':" + e);
                throw new PreconditionException("resetProductCommand.invoke() threw an exception");
            }
        }
    }
}
