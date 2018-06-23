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

package com.ail.core.product.catalog;

import static com.ail.core.product.catalog.CatalogRegisterProductService.PRODUCT_GROUP_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.Service;
import com.ail.core.configure.Parameter;
import com.ail.core.product.ScanForProductDefinitionsService.ScanForProductDefinitionsArgument;

@ServiceImplementation
public class CatalogScanForProductDefinitionsService extends Service<ScanForProductDefinitionsArgument> {
    @Override
    public void invoke() throws PostconditionException {
        List<String> products = new ArrayList<>();

        CoreProxy coreProxy = CoreContext.getCoreProxy();

        // Each parameter in the 'products' group is a product name. Add them
        // to the return list.
        for (Parameter prod : coreProxy.getGroup(PRODUCT_GROUP_NAME).getParameter()) {
            products.add(prod.getName());
        }

        Collections.sort(products);

        args.setProductTypeIDsRet(products);

        if (args.getProductTypeIDsRet() == null) {
            throw new PostconditionException("args.getProductsRet()==null");
        }
    }
}
