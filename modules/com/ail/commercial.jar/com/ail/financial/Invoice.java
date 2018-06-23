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

import java.util.List;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.core.product.HasProduct;
import com.ail.party.Party;

/**
 * Type representing an invoice or debit note.
 */
@TypeDefinition
public class Invoice extends Type implements HasProduct {
    private static final long serialVersionUID = 6168511434898673717L;
    private Party addressee;
    private Party from;
    private String invoiceNumber;
    private String narative;
    private Party registeredOffice;
    private List<MoneyProvision> financialAmounts;
    private String productTypeId;

    public void setAddressee(Party addressee) {
        this.addressee = addressee;
    }

    public Party getAddressee() {
        return addressee;
    }

    public Party getFrom() {
        return from;
    }

    public void setFrom(Party from) {
        this.from = from;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getNarative() {
        return narative;
    }

    public void setNarative(String narative) {
        this.narative = narative;
    }

    public Party getRegisteredOffice() {
        return registeredOffice;
    }

    public void setRegisteredOffice(Party registeredOffice) {
        this.registeredOffice = registeredOffice;
    }

    public List<MoneyProvision> getFinancialAmounts() {
        return financialAmounts;
    }

    public void setFinancialAmounts(List<MoneyProvision> financialAmounts) {
        this.financialAmounts = financialAmounts;
    }

    @Override
    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    @Override
    public String getProductTypeId() {
        return productTypeId;
    }
}
