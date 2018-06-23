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

import static com.ail.core.product.ProductChangeEventType.PRODUCT_UPGRADE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;
import com.ail.core.product.ClearProductCacheService.ClearProductCacheCommand;
import com.ail.core.product.RegisterProductService.RegisterProductCommand;
import com.ail.core.product.RemoveProductService.RemoveProductCommand;
import com.ail.core.product.ResetProductService.ResetProductCommand;
import com.ail.core.product.UpgradeProductService.UpgradeProductCommand;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;

/**
 *
 */
@ServiceImplementation
public class ProductChangeEvenProcessorService extends Service<ProductChangeEvenProcessorService.ProductChangeEvenProcessorArgument> {
    private ResetProductCommand resetProduct;
    private ClearProductCacheCommand clearProductCache;
    private RegisterProductCommand registerProduct;
    private RemoveProductCommand removeProduct;
    private UpgradeProductCommand upgradeProduct;

    public ProductChangeEvenProcessorService() {
        resetProduct = core.newCommand(ResetProductCommand.class);
        clearProductCache = core.newCommand(ClearProductCacheCommand.class);
        registerProduct = core.newCommand(RegisterProductCommand.class);
        removeProduct = core.newCommand(RemoveProductCommand.class);
        upgradeProduct = core.newCommand(UpgradeProductCommand.class);
    }

    @ServiceArgument
    public interface ProductChangeEvenProcessorArgument extends Argument {
    }

    @ServiceCommand(defaultServiceClass = ProductChangeEvenProcessorService.class)
    public interface ProductChangeEvenProcessorCommand extends Command, ProductChangeEvenProcessorArgument {
    }

    @Override
    public void invoke() throws BaseException {
        Set<String> productsToRegister = new HashSet<>();
        Set<String> productsToRemove = new HashSet<>();
        Set<String> productsToReset = new HashSet<>();
        Set<String> cachesToClear = new HashSet<>();
        Set<String> productsToUpgrade = new HashSet<>();

        // Clear the Liferay cache, make sure we're getting the freshest of files
        CacheRegistryUtil.clear();

        Query query = HibernateSessionBuilder.getSessionFactory().getCurrentSession().createQuery("from ProductChangeEvent");

        @SuppressWarnings("unchecked")
        List<ProductChangeEvent> events = query.list();

        for (ProductChangeEvent event : events) {
            switch (event.getType()) {
            case REGISTRY_DELETED:
                productsToRemove.add(event.getPath());
                cachesToClear.add(event.getPath());
                break;
            case REGISTRY_ADDED:
                productsToRegister.add(event.getPath());
                productsToReset.add(event.getPath());
                break;
            case REGISTRY_UPDATED:
                productsToReset.add(event.getPath());
                cachesToClear.add(event.getPath());
                break;
            case CONTENT_UPDATED:
                cachesToClear.add(event.getPath());
                break;
            case PRODUCT_UPGRADE:
                productsToUpgrade.add(event.getPath());
                break;
            }

            core.delete(event);
        }

        for (String product : productsToRegister) {
            registerProduct(product);
        }

        for (String product : productsToRemove) {
            removeProduct(product);
        }

        for (String product : productsToReset) {
            resetProduct(product);
        }

        for (String product : cachesToClear) {
            clearProductCache(product);
        }

        for (String product : productsToUpgrade) {
            upgradeProduct(product);
        }
}

    void clearProductCache(String product) throws BaseException  {
        clearProductCache.setCallersCore(core);
        clearProductCache.setProductNameArg(product);
        clearProductCache.invoke();
    }

    void resetProduct(String product) throws BaseException {
        resetProduct.setCallersCore(core);
        resetProduct.setProductNameArg(product);
        resetProduct.invoke();
        core.create(new ProductChangeEvent(PRODUCT_UPGRADE, product));
    }

    void registerProduct(String product) throws BaseException {
        ProductDetails productsDetails = new ProductDetails();
        productsDetails.setName(product);
        registerProduct.setCallersCore(core);
        registerProduct.setProductDetailsArg(productsDetails);
        registerProduct.invoke();
        core.create(new ProductChangeEvent(PRODUCT_UPGRADE, product));
    }

    void removeProduct(String product) throws BaseException {
        ProductDetails productsDetails = new ProductDetails();
        productsDetails.setName(product);
        removeProduct.setCallersCore(core);
        removeProduct.setProductDetailsArg(productsDetails);
        removeProduct.invoke();
    }

    private void upgradeProduct(String product) throws BaseException {
        upgradeProduct.setCallersCore(core);
        upgradeProduct.setProductNameArg(product);
        upgradeProduct.invoke();
    }
}
