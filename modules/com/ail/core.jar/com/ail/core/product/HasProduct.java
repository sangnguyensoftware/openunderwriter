/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

/**
 * Implemented by all model object (sub-classes of {@link com.ail.core.Type Type}) which
 * have and association with a product. Or, more accurately, operate within the context of
 * a product.
 */
public interface HasProduct {
    /**
     * Get the ProductTypeId. This string identifies the product that the policy
     * relates to. Among other things it allows services (e.g. assessRisk) to identify
     * the appropriate rules to run when processing this policy.
     *
     * @return The product type id.
     */
    public String getProductTypeId();

    /**
     * Set the product type id associated with this policy.
     *
     * @param productTypeId New product type id
     * @see #getProductTypeId
     */
    public void setProductTypeId(String productTypeId);
}
