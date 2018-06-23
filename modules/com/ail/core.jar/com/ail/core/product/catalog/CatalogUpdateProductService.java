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

import static com.ail.core.product.catalog.CatalogRegisterProductService.PRODUCT_GROUP_NAME;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.product.UnknownProductException;
import com.ail.core.product.UpdateProductService.UpdateProductArgument;

@ServiceImplementation
public class CatalogUpdateProductService extends Service<UpdateProductArgument> {
    @Override
    public void invoke() throws PreconditionException, PostconditionException, UnknownProductException {
        // Check the essential preconditions
        if (args.getProductDetailsArg().getName() == null || args.getProductDetailsArg().getName().length() == 0) {
            throw new PreconditionException("args.getProductDetailsArg().getName()==null || args.getProductDetailsArg().getName().length()==0");
        }

        if (args.getProductNameArg() == null || args.getProductNameArg().length() == 0) {
            throw new PreconditionException("args.getProductNameArg()==null || args.getProductNameArg().length()==0");
        }

        CoreProxy coreProxy = CoreContext.getCoreProxy();

        // Check that the product exist
        if (coreProxy.getParameter(PRODUCT_GROUP_NAME + "." + args.getProductNameArg()) == null) {
            throw new UnknownProductException(args.getProductNameArg());
        }

        // The "Registry" is implemented as a group in the product catalog's configuration namespace -
        // remove the
        Configuration config = coreProxy.getConfiguration();
        Group prods = config.findGroup(PRODUCT_GROUP_NAME);

        // Find the entry for the product 'as loaded' - it may be that the update
        // changes the product's name, so we find the one that was loaded.
        for (int i = 0; i < prods.getParameterCount(); i++) {
            if (prods.getParameter(i).getName().equals(args.getProductNameArg())) {
                Parameter prod = prods.getParameter(i);
                prod.setName(args.getProductDetailsArg().getName());
                coreProxy.setConfiguration(config);
                coreProxy.setVersionEffectiveDateToNow();
                break;
            }
        }
    }
}