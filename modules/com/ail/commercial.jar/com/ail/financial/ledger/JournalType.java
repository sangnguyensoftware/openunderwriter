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

public enum JournalType implements TypeEnum {
    PREMIUM_RECEIVED("i18n_premium_received_journal_type"),
    PREMIUM_DUE("i18n_premium_due_journal_type"),
    PREMIUM_WAIVER("i18n_premium_waiver_journal_type"),
    ARREARS_RECEIVED("i18n_arrears_received_journal_type"),
    ARREARS_DUE("i18n_arrears_due_journal_type"),
    CLAIM_DUE("i18n_claim_due_journal_type"),
    CLAIM_RECEIVED("i18n_claim_received_journal_type"),
    COMMISSION_DUE("i18n_commission_due_journal_type"),
    COMMISSION_RECEIVED("i18n_commission_received_journal_type"),
    REFUND_DUE("i18n_refund_due_journal_type"),
    REFUND_RECEIVED("i18n_refund_received_journal_type"),
    THIRDPARTY_DUE("i18n_thirdparty_due_journal_type"),
    THIRDPARTY_RECEIVED("i18n_thirdparty_received_journal_type"),
    TAX_DUE("i18n_tax_due_journal_type"),
    TAX_RECEIVED("i18n_tax_received_journal_type");

    private final String longName;

    JournalType() {
        this.longName=name();
    }

    JournalType(String longName) {
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
    public static JournalType forName(String name) {
        return (JournalType)Functions.enumForName(name, values());
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
