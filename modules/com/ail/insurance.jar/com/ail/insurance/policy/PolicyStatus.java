/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

/**
 * Indicates the state of the policy.
 */
public enum PolicyStatus implements TypeEnum {
    APPLICATION("i18n_policy_status_application"),
    QUOTATION("i18n_policy_status_quotation"),
    SUBMITTED("i18n_policy_status_submitted"),
    ON_RISK("i18n_policy_status_on_risk"),
    REFERRED("i18n_policy_status_referred"),
    DECLINED("i18n_policy_status_declined"),
    CANCELLED("i18n_policy_status_cancelled"),
    CANCELLED_FROM_INCEPTION("i18n_policy_status_canceled_from_inception"),
    NOT_TAKEN_UP("i18n_policy_status_not_taken_up"),
    UNUSED("i18n_policy_status_unused"),
    LAPSED("i18n_policy_status_lapsed"),
    DELETED("i18n_policy_status_deleted");

    private final String longName;

    PolicyStatus() {
        this.longName=name();
    }

    PolicyStatus(String longName) {
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
    public static PolicyStatus forName(String name) {
        return (PolicyStatus)Functions.enumForName(name, values());
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
