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
import com.ail.core.configure.ConfigurationResetError;

@ServiceImplementation
public class ResetProductService extends Service<ResetProductService.ResetProductArgument> {
    private String namespace = null;

    @ServiceArgument
    public interface ResetProductArgument extends Argument {
        /**
         * Getter for the productNameArg property. The name of the product to be reset
         *
         * @return Value of productNameArg, or null if it is unset
         */
        String getProductNameArg();

        /**
         * Setter for the productNameArg property. * @see #getProductNameArg
         *
         * @param productNameArg
         *            new value for property.
         */
        void setProductNameArg(String productNameArg);
    }

    @ServiceCommand(defaultServiceClass = ResetProductService.class)
    public interface ResetProductCommand extends Command, ResetProductArgument {
    }

    @Override
    public String getConfigurationNamespace() {
        return namespace;
    }

    /**
     * Reset the component's configuration.
     */
    @Override
    public void resetConfiguration() {
        super.resetConfigurationByNamespace();
    }

    @Override
    public void invoke() throws BaseException {
        if (args.getProductNameArg() == null || args.getProductNameArg().length() == 0) {
            throw new PreconditionException("args.getProductNameArg()==null || args.getProductNameArg().length()==0");
        }

        resetProduct();
    }

    private void resetProduct() throws PreconditionException {
        try {
            namespace = productNameToConfigurationNamespace(args.getProductNameArg());
            resetConfiguration();
            core.logInfo("Product sucessfully reset: " + namespace);
        } catch (ConfigurationResetError e) {
            throw new PreconditionException("Product reset failed for: " + namespace + ", error: " + e);
        }
    }

    /**
     * Always use the latest configuration for this and the other product catalog
     * related services.
     */
    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return new VersionEffectiveDate();
    }
}
