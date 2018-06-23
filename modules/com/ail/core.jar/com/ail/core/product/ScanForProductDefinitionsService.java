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

import java.util.Collection;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.product.liferay.LiferayScanForProductDefinitionsService;

@ServiceInterface
public class ScanForProductDefinitionsService {

    @ServiceArgument
    public interface ScanForProductDefinitionsArgument extends Argument {
        /**
         * Getter for the productsRet property. A collection of ProductDetails
         * representing all the products know to the system is returned.
         *
         * @return Value of productsRet, or null if it is unset
         */
        Collection<String> getProductTypeIDsRet();

        /**
         * Setter for the productsRet property.
         *
         * @see #getProductsRet
         * @param productsRet
         *            new value for property.
         */
        void setProductTypeIDsRet(Collection<String> productTypeIDsRet);
    }

    @ServiceCommand(defaultServiceClass = LiferayScanForProductDefinitionsService.class)
    public interface ScanForProductDefinitionsCommand extends Command, ScanForProductDefinitionsArgument {
    }
}
