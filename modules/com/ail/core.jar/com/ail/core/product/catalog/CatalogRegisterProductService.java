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

package com.ail.core.product.catalog;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.product.DuplicateProductException;
import com.ail.core.product.ListProductsService.ListProductsCommand;
import com.ail.core.product.ProductDetails;
import com.ail.core.product.RegisterProductService.RegisterProductArgument;

@ServiceImplementation
public class CatalogRegisterProductService extends Service<RegisterProductArgument> {
    static final String PRODUCT_GROUP_NAME = "Products";

    @Override
    public void invoke() throws PreconditionException, PostconditionException, DuplicateProductException {
        // Check the essential preconditions
        if (args.getProductDetailsArg().getName() == null || args.getProductDetailsArg().getName().length() == 0) {
            throw new PreconditionException("args.getProductDetailsArg().getName()==null || args.getProductDetailsArg().getName().length()==0");
        }

        CoreProxy coreProxy = CoreContext.getCoreProxy();

        if (!productIsAlreadyRegistered()) {
            // The "Registry" is implemented as a group in the product catalog's configuration namespace -
            // add the new products details to the registry.
            Configuration config = coreProxy.getConfiguration();
            Group prods = config.findGroup(PRODUCT_GROUP_NAME);
            Parameter prod = new Parameter();
            prod.setName(args.getProductDetailsArg().getName());
            prods.addParameter(prod);
            coreProxy.setConfiguration(config);
            coreProxy.setVersionEffectiveDateToNow();
            coreProxy.clearConfigurationCache();

            coreProxy.logInfo("Product sucessfully registered: " + args.getProductDetailsArg().getName());
        } else {
            coreProxy.logInfo("Product registration ignored: " + args.getProductDetailsArg().getName() + " is already registered");
        }
    }

    boolean productIsAlreadyRegistered() throws PreconditionException {
        try {
            ListProductsCommand listProductCommand = core.newCommand(ListProductsCommand.class);
            listProductCommand.invoke();
            for (ProductDetails productDetail : listProductCommand.getProductsRet()) {
                if (productDetail.getName().equals(args.getProductDetailsArg().getName())) {
                    return true;
                }
            }
            return false;
        } catch (BaseException e) {
            throw new PreconditionException("Failed to create ListProductsCommand service", e);
        }
    }
}
