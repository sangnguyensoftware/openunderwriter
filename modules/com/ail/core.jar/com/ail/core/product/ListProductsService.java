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

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.product.ListProductsService.ListProductsArgument;

@ServiceImplementation
public class ListProductsService extends Service<ListProductsArgument> {

    @ServiceArgument
    public interface ListProductsArgument extends Argument {
        /**
         * Getter for the productsRet property. A collection of ProductDetails representing all the
         * products know to the system is returned.
         * @return Value of productsRet, or null if it is unset
         */
        Collection<ProductDetails> getProductsRet();

        /**
         * Setter for the productsRet property.
         * @see #getProductsRet
         * @param productsRet new value for property.
         */
        void setProductsRet(Collection<ProductDetails> productsRet);
    }

    @ServiceCommand(defaultServiceClass=ListProductsService.class)
    public interface ListProductsCommand extends Command, ListProductsArgument {
    }

    @Override
    public void invoke() throws PostconditionException {

        List<ProductDetails> details=new ArrayList<>();

        // Each parameter in the 'products' group is a product name. Add them
        // to the return list.
        for(String prod: ConfigurationHandler.getInstance().getNamespaces()) {
            if (prod.endsWith(".Registry")) {
                details.add(new ProductDetails(prod.substring(0, prod.length()-9)));
            }
        }

        details = sort(details, on(ProductDetails.class).getName());

        args.setProductsRet(details);

        if (args.getProductsRet()==null) {
            throw new PostconditionException("args.getProductsRet()==null");
        }
    }
}