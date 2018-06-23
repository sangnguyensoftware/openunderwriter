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

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a record in a transaction file.
 * Could be a Direct Debit or Payment instruction, or could be a DDI or PI output or an Adjustment.
 * Could be a debit or a credit.
 * Could be to/from a person or an organisation.
 */
public class Record {

    /**
     * The full name of the payment account
     */
    private String accountName;

    /**
     * The sort code of the payment account
     */
    private String sortCode;

    /**
     * The account number of the payment account
     */
    private String accountNumber;

    /**
     * The transaction code for the payment or direct debit instruction.
     */
    private TransactionCode transactionCode;

    /**
     * The reference that will appear for the payment or direct debit instruction
     */
    private String reference;

    /**
     * The processingDate for the payment
     */
    private Date processingDate;

    /**
     * The amount of the payment
     */
    private BigDecimal amount;

    private String errorCode;

    public Record() {

    }

    public Record(String accountName, String sortCode, String accountNumber, TransactionCode transactionCode, String reference, Date processingDate) {
        super();
        this.accountName = accountName;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.transactionCode = transactionCode;
        this.reference = reference;
        this.processingDate = processingDate;
    }

    public Record(String accountName, String sortCode, String accountNumber, TransactionCode transactionCode, String reference, Date processingDate, BigDecimal amount) {
        this(accountName, sortCode, accountNumber, transactionCode, reference, processingDate);
        this.amount = amount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public TransactionCode getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(TransactionCode transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
