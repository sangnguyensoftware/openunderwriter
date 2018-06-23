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

import com.github.ffpojo.metadata.positional.PaddingAlign;
import com.github.ffpojo.metadata.positional.annotation.PositionalField;
import com.github.ffpojo.metadata.positional.annotation.PositionalRecord;

/**
 * Record includes the credit and debit value totals, and the count of credit, debit and DDI items.
 */
@PositionalRecord
public class UserTrailerLabel1 {

    /**
     * Must be UTL.
     */
    private String labelIdentifier = "UTL";

    /**
     * Must be 1.
     */
    private String labelNumber = "1";

    /**
     * • Must be all numeric (that is, no currency symbol and no decimal point)
     * • Must be the monetary total in pence of debit payment instructions and credit contras since the preceding UHL1 (note: DDIs have a value of zero).
     */
    private String debitValueTotal;

    /**
     * • Must be all numeric (that is, no currency symbol and no decimal point)
     * • Must be the monetary total in pence of credit payment instructions and debit contras since the preceding UHL1.
     */
    private String creditValueTotal;

    /**
     * • Must be numeric
     * • Must be the count of debit payment instructions and credit contras since the preceding UHL1. Note: do not count DDIs.
     */
    private String debitItemCount;

    /**
     * • Must be numeric
     * • Must be the count of credit payment instructions and debit contras since the preceding UHL1.
     */
    private String creditItemCount;

    /**
     * Must be blank spaces.
     */
    private String reserved = "";

    /**
     * • Must be numeric
     * • Must be the count of Direct Debit instructions since the preceding UHL1
     */
    private String ddiCount;

    /**
     * Must be blank or any valid characters
     */
    private String forUseByServiceUser = "";

    public UserTrailerLabel1() {
    }

    @PositionalField(initialPosition = 1, finalPosition = 3)
    public String getLabelIdentifier() {
        return labelIdentifier;
    }

    public void setLabelIdentifier(String labelIdentifier) {
        this.labelIdentifier = labelIdentifier;
    }

    @PositionalField(initialPosition = 4, finalPosition = 4)
    public String getLabelNumber() {
        return labelNumber;
    }

    public void setLabelNumber(String labelNumber) {
        this.labelNumber = labelNumber;
    }

    @PositionalField(initialPosition = 5, finalPosition = 17, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getDebitValueTotal() {
        return debitValueTotal;
    }

    public void setDebitValueTotal(String debitValueTotal) {
        this.debitValueTotal = debitValueTotal;
    }

    @PositionalField(initialPosition = 18, finalPosition = 30, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getCreditValueTotal() {
        return creditValueTotal;
    }

    public void setCreditValueTotal(String creditValueTotal) {
        this.creditValueTotal = creditValueTotal;
    }

    @PositionalField(initialPosition = 31, finalPosition = 37, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getDebitItemCount() {
        return debitItemCount;
    }

    public void setDebitItemCount(String debitItemCount) {
        this.debitItemCount = debitItemCount;
    }

    @PositionalField(initialPosition = 38, finalPosition = 44, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getCreditItemCount() {
        return creditItemCount;
    }

    public void setCreditItemCount(String creditItemCount) {
        this.creditItemCount = creditItemCount;
    }

    @PositionalField(initialPosition = 45, finalPosition = 52, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    @PositionalField(initialPosition = 53, finalPosition = 59, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getDdiCount() {
        return ddiCount;
    }

    public void setDdiCount(String ddiCount) {
        this.ddiCount = ddiCount;
    }

    @PositionalField(initialPosition = 60, finalPosition = 80, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getForUseByServiceUser() {
        return forUseByServiceUser;
    }

    public void setForUseByServiceUser(String forUseByServiceUser) {
        this.forUseByServiceUser = forUseByServiceUser;
    }
}