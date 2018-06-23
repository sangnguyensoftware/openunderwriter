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
package com.ail.insurance.policy;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum PolicyLinkType implements TypeEnum {

    DISAGGREGATED_FROM("Disaggregated From"),
    DISAGGREGATED_TO("Disaggregated To"),

    /* @deprecated */
    RENEWAL_OF("Renewal Of"),

    MTA_QUOTATION_FROM("MTA Quotation From"),
    MTA_QUOTATION_FOR("MTA Quotation For"),

    MTA_FROM("MTA From"),
    MTA_FOR("MTA For"),

    RENEWAL_QUOTATION_FROM("Renewal Quotation From"),
    RENEWAL_QUOTATION_FOR("Renewal Quotation For"),

    RENEWAL_FROM("Renewal From"),
    RENEWAL_FOR("Renewal For");

    private final String longName;

    PolicyLinkType(String longName) {
        this.longName=longName;
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    @Override
    public String longName() {
        return longName;
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static PolicyLinkType forName(String name) {
        return (PolicyLinkType)Functions.enumForName(name, values());
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
}
