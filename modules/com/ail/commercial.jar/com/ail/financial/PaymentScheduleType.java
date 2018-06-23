/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

package com.ail.financial;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

/**
 * @deprecated Payment schedules no longer define "type". It has been replaced by a "purpose" on MoneyProvision: {@link MoneyProvision#getPurpose()}
 */
@Deprecated
public enum PaymentScheduleType implements TypeEnum {
    UNDEFINED("?"),
    PREMIUM("i18n_payment_schedule_type_premium"),
    ARREARS("i18n_payment_schedule_type_arrears"),
    CLAIM("i18n_payment_schedule_type_claim");

    private final String longName;

    PaymentScheduleType() {
        this.longName=name();
    }

    PaymentScheduleType(String longName) {
        this.longName=longName;
    }

    @Override
    public String getLongName() {
        return longName;
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
    public static PaymentScheduleType forName(String name) {
        return (PaymentScheduleType)Functions.enumForName(name, values());
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}
