/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum ProductChangeEventType implements TypeEnum {
    REGISTRY_ADDED("Registry added"),
    REGISTRY_UPDATED("Registry updated"),
    REGISTRY_DELETED("Registry deleted"),
    CONTENT_UPDATED("Content updated"),
    PRODUCT_UPGRADE("Product upgrade");

    private final String longName;

    ProductChangeEventType(String longName) {
        this.longName = longName;
    }

    @Override
    public String longName() {
        return longName;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLongName() {
        return longName;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static ProductChangeEventType forName(String name) {
        return (ProductChangeEventType)Functions.enumForName(name, values());
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }
}
