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
package com.ail.financial.ledger;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum JournalLineType implements TypeEnum {
    COMMISSION("i18n_commission_journal_line_type"),
    PREMIUM("i18n_premium_journal_line_type"),
    CLAIM("i18n_claim_journal_line_type"),
    ARREARS("i18n_arrears_journal_line_type"),
    REFUND("i18n_refund_journal_line_type"),
    THIRDPARTY("i18n_thirdparty_journal_line_type"),
    TAX("i18n_tax_journal_line_type");

    private final String longName;

    JournalLineType() {
        this.longName=name();
    }

    JournalLineType(String longName) {
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
    public static JournalLineType forName(String name) {
        return (JournalLineType)Functions.enumForName(name, values());
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
