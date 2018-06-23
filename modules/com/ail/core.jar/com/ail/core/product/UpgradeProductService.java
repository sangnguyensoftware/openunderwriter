/* Copyright Applied Industrial Logic Limited 20018. All rights Reserved */
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

import static com.ail.core.Functions.isEmpty;
import static com.ail.core.Functions.productNameToConfigurationNamespace;

import java.util.Optional;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.RunAsUpgradeHandler;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.Parameter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

@ServiceImplementation
public class UpgradeProductService extends Service<UpgradeProductService.UpgradeProductArgument> {
    public static final String PRODUCT_UPGRADE_GROUP_NAME = "ProductUpgradeCommands";

    private CoreProxy core;

    @ServiceArgument
    public interface UpgradeProductArgument extends Argument {
        /**
         * The name of the product to be upgraded.
         *
         * @return The name of the product to be upgraded.
         */
        String getProductNameArg();

        /**
         * The name of the product to be upgraded.
         *
         * @param productName
         *            The name of the product to upgrade
         */
        void setProductNameArg(String productName);
    }

    @ServiceCommand(defaultServiceClass = UpgradeProductService.class)
    public interface UpgradeProductCommand extends Command, UpgradeProductArgument {
    }

    @Override
    public void invoke() throws BaseException {

        if (isEmpty(args.getProductNameArg())) {
            throw new PreconditionException("isEmpty(args.getProductNameArg())");
        }

        String productName = args.getProductNameArg();

        String namespace = productNameToConfigurationNamespace(productName);
        ConfigurationHandler.reset(namespace);
        core = new CoreProxy(namespace);

        if (core.getGroup(productUpgradegroupName(productName)) != null) {
            try {
                executeScriptsAsUpdateHandler(core, productName);
            } catch (Throwable e) {
                throw new ProductUpgradeError("Upgrade scripts could not be executed.", e);
            }
        }
    }

    void executeScriptsAsUpdateHandler(CoreProxy core, String productName) throws PortalException, SystemException, Exception {
        new RunAsUpgradeHandler() {
            @Override
            protected void doRun() throws Exception {
                executeScripts(core, productName);
            }
        }.run();
    }

    void executeScripts(CoreProxy core, String productName) {
        for(Parameter param: core.getGroup(productUpgradegroupName(productName)).getParameter()) {
            String commandName = param.getName();
            if (isScriptNew(core, productName, commandName)) {
                try {
                    core.newCommand(commandName, UpgradeProductCommand.class).invoke();
                    core.create(new ProductUpgradeLog(productName, commandName));
                    core.logInfo("Upgrade command: " + commandName + ", in product: " + productName + " successful.");
                }
                catch(Throwable e) {
                    core.logError("Upgrade command: " + commandName + ", in product: " + productName + " failed.", e);
                    core.create(new ProductUpgradeLog(productName, commandName, e));
                    break; // if one script fails, don't run any others.
                }
            }
        }
    }

    private String productUpgradegroupName(String productName) {
        return PRODUCT_UPGRADE_GROUP_NAME + "." + productName;
    }

    private boolean isScriptNew(CoreProxy core, String productName, String commandName) {
        Optional<ProductUpgradeLog> pul = fetchProductUpgradeLog(core, productName, commandName);
        return !pul.isPresent();
    }

    private Optional<ProductUpgradeLog> fetchProductUpgradeLog(CoreProxy core, String productName, String commandName) {
        Object obj = core.queryUnique("get.sucessful.product.upgrade.by.product.and.command", productName, commandName);
        return obj == null ? Optional.empty() : Optional.of((ProductUpgradeLog)obj);
     }
}
