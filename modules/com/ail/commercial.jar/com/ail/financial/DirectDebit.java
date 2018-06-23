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

package com.ail.financial;

import static com.ail.financial.PaymentMethodStatus.PENDING;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

/**
 * Represents the details of a direct debit.
 */
@TypeDefinition
@Audited
@Entity
public class DirectDebit extends PaymentMethod {
    private static final long serialVersionUID = 1L;

    /** The bank account's number */
    private String accountNumber;

    /** The account's sort code */
    private String sortCode;

    /** The account holder's name */
    String name;

    /** The bank name, e.g. HSBC, Barclays etc */
    private String bankName;

    /** The bank account's branch name, e.g. London Bridge */
    private String branchName;

    public DirectDebit() {
        super();
    }

    public DirectDebit(String accountNumber, String sortCode, String name, PaymentMethodStatus status) {
        this();
        setAccountNumber(accountNumber);
        setSortCode(sortCode);
        setName(name);
        setStatus(status);
    }

    public DirectDebit(String accountNumber, String sortCode, String name) {
        this(accountNumber, sortCode, name, PENDING);
    }

    public DirectDebit(String accountNumber, String sortCode) {
        this(accountNumber, sortCode, null);
    }

    /**
     * Getter returning the value of the accountNumber property. The bank account's number
     * @return Value of the accountNumber property
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getMaskedAccountNumber() {
        if (accountNumber!=null && accountNumber.length() > 3) {
            return "*****"+accountNumber.substring(accountNumber.length()-3);
        }
        else {
            return "********";
        }
    }

    @Override
    public String getId() {
        return getMaskedAccountNumber();
    }

    /**
     * Setter to update the value of the accountNumber property. The bank account's number
     * @param accountNumber New value for the accountNumber property
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Getter returning the value of the sortCode property. The account's sort code
     * @return Value of the sortCode property
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     * Setter to update the value of the sortCode property. The account's sort code
     * @param sortCode New value for the sortCode property
     */
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
        result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sortCode == null) ? 0 : sortCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DirectDebit other = (DirectDebit) obj;
        if (bankName == null) {
            if (other.bankName != null)
                return false;
        } else if (!bankName.equals(other.bankName))
            return false;
        if (accountNumber == null) {
            if (other.accountNumber != null)
                return false;
        } else if (!accountNumber.equals(other.accountNumber))
            return false;
        if (branchName == null) {
            if (other.branchName != null)
                return false;
        } else if (!branchName.equals(other.branchName))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sortCode == null) {
            if (other.sortCode != null)
                return false;
        } else if (!sortCode.equals(other.sortCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DirectDebit [accountNumber=" + accountNumber + ", sortCode=" + sortCode + ", name=" + name + ", bankName=" + bankName + ", branchName=" + branchName + ", getId()=" + getId()
                + ", getStatus()=" + getStatus() + "]";
    }
}
