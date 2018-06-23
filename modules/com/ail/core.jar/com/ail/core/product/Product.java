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

import com.ail.core.Type;

/**
 * Define the interfaces that the Core class must offer to support Product functionality
 */
public interface Product {
    String BASE_PRODUCT_TYPE_ID = "AIL.BASE";
    String AGGREGATOR_CONFIGURATION_GROUP_NAME = "Aggregator";
    String AGGREGATOR_CONFIGURATION_PARAMETER_NAME = "Aggregator";

    /**
     * Register a new product with the catalog.
     * @param details Details of the product to register
     * @throws DuplicateProduct if a product by the name specified (details.name) is already defined.
     * @since 2.0
     */
    void registerProduct(ProductDetails details) throws DuplicateProductException;

    /**
     * Update the product registry's details WRT the details passed in.
     * @param name The name of the product to be updated.
     * @param details Details to store
     * @throws UnknownProduct if the product (name) isn't defined.
     * @since 2.0
     */
    void updateProduct(String productName, ProductDetails details) throws UnknownProductException;

    /**
     * Remove a product from the registry.
     * @param name Name of the product to remove.
     * @throws UnknownProduct if the named product is not defined.
     * @since 2.0
     */
    void removeProduct(ProductDetails productDetails) throws UnknownProductException;

    /**
     * Reset all the products know to the product manager. The product manager's configuration
     * contains a list of products. This method performs a system reset on each of the products
     * in that list. This amounts to performing a configuration reset using the products name
     * as the configuration namespace.
     * @since 2.0
     */
    void resetAllProducts();

    /**
     * Reset a specific product. Perform a system reset on the named product returning it
     * to it's factory state.
     * @param productName The name of the product to reset
     * @since 2.0
     */
    void resetProduct(String productName);

    /**
     * Instantiate a type associated with a product. A product must define at least one type
     * (see {@link #newProductType(String)}), but may define any number of additional types for
     * use during its life-cycle; this method is used to instantiate specific types by name.<p>
     * For example, a complex insurance product may define several different types to describe
     * the assets the product covers. A commercial combined product might define a stock asset, a
     * vehicle asset, a safe asset, etc. Each of these is described within the product as a separate
     * named type. A client would use this method to instantiate these different types as and when
     * they needed to be added to an instance of a commercial combined policy.
     * @param productName The product "owning" the type.
     * @param typeName The name of type to be instantiated.
     * @return The instantiated type.
     * @since 2.0
     * @deprecated Use {@link #newProductType(String, String, Class)} instead
     */
    @Deprecated
    Type newProductType(String productName, String typeName);

    /**
     * Instantiate a type associated with a product. A product must define at least one type
     * (see {@link #newProductType(String)}), but may define any number of additional types for
     * use during its life-cycle; this method is used to instantiate specific types by name.<p>
     * For example, a complex insurance product may define several different types to describe
     * the assets the product covers. A commercial combined product might define a stock asset, a
     * vehicle asset, a safe asset, etc. Each of these is described within the product as a separate
     * named type. A client would use this method to instantiate these different types as and when
     * they needed to be added to an instance of a commercial combined policy.
     * @param productName The product "owning" the type.
     * @param typeName The name of type to be instantiated.
     * @param clazz Class of object to be returned.
     * @return The instantiated type.
     * @since 3.0
     */
    <T extends Type> T newProductType(String productName, String typeName, Class<T> clazz);

    /**
     * Instantiate the default type associated with a product. Each product must define a default
     * type. This is the type (generally) instantiated at the beginning of the lifecycle.
     * @param productName The name of the product to instantiate for.
     * @return The instantiated type.
     * @since 2.0
     * @deprecated Use {@link #newProductType(String, Class)} instead
     */
    @Deprecated
    Type newProductType(String productName);

    /**
     * Instantiate the default type associated with a product. Each product must define a default
     * type. This is the type (generally) instantiated at the beginning of the life-cycle.
     * @param productName The name of the product to instantiate for.
     * @return The instantiated type.
     * @since 3.0
     */
    <T extends Type> T newProductType(String productName, Class<T> clazz);

    /**
     * Fetch a collection of the products know to the system.
     * @return A collection of product names (as instances of java.lang.String).
     * @since 2.0
     */
    Collection<ProductDetails> listProducts();


}
