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

import static com.ail.core.Functions.productNameToConfigurationNamespace;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationResetError;
import com.ail.core.configure.UnknownNamespaceError;

@ServiceImplementation
public class RemoveProductService  extends Service<RemoveProductService.RemoveProductArgument> {
    private String namespace=null;

    @ServiceArgument
    public interface RemoveProductArgument extends Argument {
        /**
         * Details of the product to be removed from the registry.
         *
         * @return Value of productDetaisArg, or null if it is unset
         */
        ProductDetails getProductDetailsArg();

        /**
         * Details of the product to be removed from the registry
         *
         * @see #getProductDetailsArg
         * @param productsDetailsArg
         *            new value for property.
         */
        void setProductDetailsArg(ProductDetails productsDetailsArg);
    }

    @ServiceCommand(defaultServiceClass = RemoveProductService.class)
    public interface RemoveProductCommand extends Command, RemoveProductArgument {
    }

    @Override
    public String getConfigurationNamespace() {
        return namespace;
    }

    @Override
    public void invoke() throws BaseException {
        namespace = productNameToConfigurationNamespace(args.getProductDetailsArg().getName());

        try {
            Configuration config = getConfiguration();
            config.setValidTo(new VersionEffectiveDate());
            setConfiguration(config);

            core.logInfo("Product sucessfully removed: "+namespace);
        }
        catch(UnknownNamespaceError e) {
            // Ignore. We can't delete a namespace that doesn't exist. This is a common situation when
            // the system configs are being rebuilt.
        }
        catch(ConfigurationResetError e) {
            throw new PreconditionException("Product removal failed for: "+namespace, e);
        }
    }
}
