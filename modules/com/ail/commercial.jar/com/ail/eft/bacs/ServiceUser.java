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
/**
 * Represents a BACS customer user. This will be the recipient of direct debit payments.
 */
public class ServiceUser {

    /**
     * The BACS service user number
     */
    private String userNumber;

    /**
     * The sort code of the account for BACS clearing
     */
    private String sortCode;

    /**
     * The account number of the account for BACS clearing
     */
    private String accountNumber;

    /**
     * The name of the BACS customer, i.e. organisation name
     */
    private String name;

    public ServiceUser() {
    }

    public ServiceUser(String userNumber, String sortCode, String accountNumber, String name) {
        this.userNumber = userNumber;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.name = name;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
