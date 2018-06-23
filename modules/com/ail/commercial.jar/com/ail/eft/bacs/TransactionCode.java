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
package com.ail.eft.bacs;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;
/**
 * Represent the only valid transaction codes for BACS Direct Debit Instructions or payment requests.
 */
public enum TransactionCode implements TypeEnum {

	DIRECT_DEBIT_FIRST_COLLECTION("DirectDebitFirstCollection", "01"),
	DIRECT_DEBIT_REGULAR_COLLECTION("DirectDebitRegularCollection", "17"),
	DIRECT_DEBIT_RE_PRESENTED("DirectDebitRePresented", "18"),
	DIRECT_DEBIT_FINAL_COLLECTION("DirectDebitFinalCollection", "19"),
	INTEREST_PAYMENTS("InterestPayments", "Z4"),
	DIVIDEND_PAYMENTS("DividendPayments", "Z5"),
	DIRECT_CREDIT("DirectCredit", "99"),
	DIRECT_DEBIT_INSTRUCTION_NEW("DirectDebitInstructionNew", "0N"),
	DIRECT_DEBIT_INSTRUCTION_CANCELLATION("DirectDebitInstructionCancellation", "0C"),
	DIRECT_DEBIT_INSTRUCTION_CONVERSION("DirectDebitInstructionConversion", "0S");

    private final String longName;
    private final String code;

    TransactionCode() {
        this.longName = name();
        this.code = name();
    }

    TransactionCode(String longName, String code) {
        this.longName=longName;
        this.code = code;
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
    public static TransactionCode forName(String name) {
        return (TransactionCode) Functions.enumForName(name, values());
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLongName() {
        return longName;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}
