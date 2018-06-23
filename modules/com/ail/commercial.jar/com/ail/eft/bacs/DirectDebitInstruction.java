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
 * Represents a single Direct Debit Instruction row in a Bacs submission file.
 */
@PositionalRecord
public class DirectDebitInstruction {

    /**
     * • Must be a valid sorting code for Bacs clearing.
     */
    private String paymentSortingCode;

    /**
     * • Must be numeric but not zeros (if no account number the originator should use 12345678)
     * • Must pass account validation (modulus) checking.
     */
    private String paymentAccountNumber;

    /**
     * Must be numeric.
     */
    private String paymentAccountType = "0";

    /**
     * • Can be 0N or 0C if service user’s AUDDIS status is live, test or migrate
     * • Can be 0S if service user’s AUDDIS status is test or migrate
     * • Must be allowed at the destination sorting code.
     */
    private String transactionCode;

    /**
     * Must be the sorting code of one of the service user’s nominated accounts.
     */
    private String originatingSortCode;

    /**
     * Must be the account number of one of the service user’s nominated accounts.
     */
    private String originatingAccountNumber;

    /**
     * Can be any valid characters.
     */
    private String freeFormat = "";

    /**
     * Must be zero filled or blank space filled.
     */
    private String amount = "0";

    /**
     * Must be any valid characters but cannot be all blank.
     */
    private String originatorName;

    /**
     * (the unique reference that identifies the DDI and which will be used for all Direct Debits collected)
     * • Must contain at least six alphanumeric characters. Other valid characters may be included but will not be included in the count of six alphanumerics
     * • After taking out nonalphanumeric characters, must not contain a string of all the same alphanumeric characters.
     */
    private String originatorReference;

    /**
     * If field 4 is 0N, must not be spaces. (If field 4 is 0C or 0S no check is made.)
     */
    private String paymentAccountName;

    /**
     *  Must be the current processing day in the format bYYDDD.
     */
    private String processingDate;

    @PositionalField(initialPosition = 1, finalPosition = 6)
    public String getPaymentSortingCode() {
        return paymentSortingCode;
    }

    public void setPaymentSortingCode(String paymentSortingCode) {
        this.paymentSortingCode = paymentSortingCode;
    }

    @PositionalField(initialPosition = 7, finalPosition = 14)
    public String getPaymentAccountNumber() {
        return paymentAccountNumber;
    }

    public void setPaymentAccountNumber(String paymentAccountNumber) {
        this.paymentAccountNumber = paymentAccountNumber;
    }

    @PositionalField(initialPosition = 15, finalPosition = 15)
    public String getPaymentAccountType() {
        return paymentAccountType;
    }

    public void setPaymentAccountType(String paymentAccountType) {
        this.paymentAccountType = paymentAccountType;
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
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @PositionalField(initialPosition = 47, finalPosition = 64, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    @PositionalField(initialPosition = 65, finalPosition = 82, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getOriginatorReference() {
        return originatorReference;
    }

    public void setOriginatorReference(String originatorReference) {
        this.originatorReference = originatorReference;
    }

    @PositionalField(initialPosition = 83, finalPosition = 100, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getPaymentAccountName() {
        return paymentAccountName;
    }

    public void setPaymentAccountName(String paymentAccountName) {
        this.paymentAccountName = paymentAccountName;
    }

    @PositionalField(initialPosition = 101, finalPosition = 106, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

}