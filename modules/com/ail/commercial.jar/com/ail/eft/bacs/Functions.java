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

import static com.ail.eft.bacs.TransactionCode.DIRECT_CREDIT;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_FINAL_COLLECTION;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_FIRST_COLLECTION;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_INSTRUCTION_CANCELLATION;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_INSTRUCTION_CONVERSION;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_INSTRUCTION_NEW;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_REGULAR_COLLECTION;
import static com.ail.eft.bacs.TransactionCode.DIRECT_DEBIT_RE_PRESENTED;
import static com.ail.eft.bacs.TransactionCode.DIVIDEND_PAYMENTS;
import static com.ail.eft.bacs.TransactionCode.INTEREST_PAYMENTS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class Functions {

    private static Collection<TransactionCode> validPaymentTransactionCodes = Arrays.asList(new TransactionCode[] {
                                                                            DIRECT_DEBIT_FIRST_COLLECTION, DIRECT_DEBIT_REGULAR_COLLECTION, DIRECT_DEBIT_RE_PRESENTED,
                                                                            DIRECT_DEBIT_FINAL_COLLECTION, INTEREST_PAYMENTS, DIVIDEND_PAYMENTS, DIRECT_CREDIT});

    private static Collection<TransactionCode> validDDITransactionCodes = Arrays.asList(new TransactionCode[] {
                                                                            DIRECT_DEBIT_INSTRUCTION_NEW, DIRECT_DEBIT_INSTRUCTION_CANCELLATION, DIRECT_DEBIT_INSTRUCTION_CONVERSION});

    private static final String JULIAN_DAY = "DDD";

    private static final String JULIAN_YEAR_DAY = "yyDDD";

    private static final BigDecimal hundred = new BigDecimal("100");

    public static String formatJulianDay(Date date) {
        return new SimpleDateFormat(JULIAN_DAY).format(date);
    }

    public static String formatJulianYearDay(Date date) {
        return new SimpleDateFormat(JULIAN_YEAR_DAY).format(date);
    }

    public static String toPence(BigDecimal amount) {
        return amount.multiply(hundred).setScale(0).toPlainString();
    }

    public static BigDecimal fromPence(String pence) {
        return new BigDecimal(pence).divide(hundred).setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isValidPaymentTransactionCode(TransactionCode code) {
        return validPaymentTransactionCodes.contains(code);
    }

    public static boolean isValidDDITransactionCode(TransactionCode code) {
        return validDDITransactionCodes.contains(code);
    }
}
