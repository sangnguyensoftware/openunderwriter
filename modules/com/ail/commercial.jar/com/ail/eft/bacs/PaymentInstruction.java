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
 * Represents a single Value row in a Bacs submission file. This is an individual payment instruction
 */
@PositionalRecord
public class PaymentInstruction {

    /**
     * Must be a valid sorting code for Bacs clearing.
     * Must not be allocated to a credit card company.
     */
    private String destinationSortingCode;

    /**
     * • Must be numeric.
     */
    private String destinationAccountNumber;

    /**
     * Must be numeric.
     */
    private String destinationAccountType = "0";

    /**
     * Must be a valid transaction code (see pg 12).
     * Must be 01, 17, 18, 19, 99, Z4, Z5 or 04.
     * • Must be set up as allowed on the originating account or on the main account if substituted
     * • Must be allowed at the destination sorting code.
     */
    private String transactionCode;

    /**
     * • Must be the sorting code of one of the service user’s nominated accounts.
     * • Must be set up to allow the currency of the file.
     */
    private String originatingSortCode;

    /**
     * • Must be the account number of one of the service user’s nominated accounts.
     * • Must be set up to allow the currency of the file.
     */
    private String originatingAccountNumber;

    /**
     * Bacs Direct Credits – for employers making payments to which the Income Tax (Pay as You Earn) regulations
     * apply, the first character (position 32) must be a "/" followed by a three character random string (positions
     * 33-35). The random string can be made up of the Bacs allowed characters excluding blank spaces and ampersands (&).
     * Note: For Direct Debit payment instructions, will be amended to the current processing date in the format bDDD
     */
    private String freeFormat = "";

    /**
     * Must be all numeric, but not all zeros.
     */
    private String amountInPence;

    /**
     * Can be any valid characters.
     */
    private String serviceUserName;

    /**
     * For Direct Debit payment instructions:
     * • Must contain at least six alphanumeric characters. Other valid characters may be included but will not be included in the count of six alphanumerics
     * • After taking out nonalphanumeric characters, must not contain a string of all the same alphanumeric characters.
     */
    private String serviceUserReference;

    /**
     * Must not be blank if field 2 is blank, all zeros or amended by Bacs to all zeros.
     */
    private String destinationAccountName;

    /**
     * • Must be in the format bYYDDD
     * • Must not be earlier than UHL1 date
     * • Must be a valid Bacs processing day
     * • Must not be more than 39 days after UHL1 date.
     */
    private String processingDate;

    @PositionalField(initialPosition = 1, finalPosition = 6)
    public String getDestinationSortingCode() {
        return destinationSortingCode;
    }

    public void setDestinationSortingCode(String destinationSortingCode) {
        this.destinationSortingCode = destinationSortingCode;
    }

    @PositionalField(initialPosition = 7, finalPosition = 14)
    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    @PositionalField(initialPosition = 15, finalPosition = 15)
    public String getDestinationAccountType() {
        return destinationAccountType;
    }

    public void setDestinationAccountType(String destinationAccountType) {
        this.destinationAccountType = destinationAccountType;
    }

    @PositionalField(initialPosition = 16, finalPosition = 17)
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    @PositionalField(initialPosition = 18, finalPosition = 23)
    public String getOriginatingSortCode() {
        return originatingSortCode;
    }

    public void setOriginatingSortCode(String originatingSortCode) {
        this.originatingSortCode = originatingSortCode;
    }

    @PositionalField(initialPosition = 24, finalPosition = 31)
    public String getOriginatingAccountNumber() {
        return originatingAccountNumber;
    }

    public void setOriginatingAccountNumber(String originatingAccountNumber) {
        this.originatingAccountNumber = originatingAccountNumber;
    }

    @PositionalField(initialPosition = 32, finalPosition = 35, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getFreeFormat() {
        return freeFormat;
    }

    public void setFreeFormat(String freeFormat) {
        this.freeFormat = freeFormat;
    }

    @PositionalField(initialPosition = 36, finalPosition = 46, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getAmountInPence() {
        return amountInPence;
    }

    public void setAmountInPence(String amountInPence) {
        this.amountInPence = amountInPence;
    }

    @PositionalField(initialPosition = 47, finalPosition = 64, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getServiceUserName() {
        return serviceUserName;
    }

    public void setServiceUserName(String serviceUserName) {
        this.serviceUserName = serviceUserName;
    }

    @PositionalField(initialPosition = 65, finalPosition = 82, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getServiceUserReference() {
        return serviceUserReference;
    }

    public void setServiceUserReference(String serviceUserReference) {
        this.serviceUserReference = serviceUserReference;
    }

    @PositionalField(initialPosition = 83, finalPosition = 100, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
    }

    @PositionalField(initialPosition = 101, finalPosition = 106, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

}