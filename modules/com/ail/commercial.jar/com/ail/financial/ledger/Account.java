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

import static com.ail.financial.ledger.AccountStatus.OPEN;
import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static org.hibernate.annotations.CascadeType.DETACH;
import static org.hibernate.annotations.CascadeType.MERGE;
import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.REFRESH;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.party.Party;

@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.account.by.systemId", query = "select acc from Account acc where acc.systemId = ?"),
    @NamedQuery(name = "get.accounts.for.party", query = "select acc from Account acc where acc.accountHolder = ?"),
    @NamedQuery(name = "get.accounts.by.transactions.in.accounting.period", query = "select distinct acc from Journal jou join jou.journalLine jli join jli.ledger led join led.account acc where jou.accountingPeriod = ?")
})
public class Account extends Type {

    @Enumerated(STRING)
    private Currency currency;

    private String name;

    @Lob
    private String description;

    @OneToOne
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "accountHolderUIDpar", referencedColumnName = "UID")
    private Party accountHolder;

    @Enumerated(STRING)
    private AccountType type;

    @Enumerated(STRING)
    private AccountStatus status;

    @OneToMany(cascade = { ALL }, mappedBy = "account")
    private List<Balance> balance;

    private Date openingDate;

    Account() {
    }

    private Account(AccountBuilder builder) {
        currency = builder.currency;
        description = builder.description;
        name = builder.name;
        accountHolder = builder.accountHolder;
        type = builder.type;
        openingDate = builder.openingDate;
        status = OPEN;
        getBalance().add(new Balance(this, openingDate, new CurrencyAmount(builder.openingBalance, currency)));
    }

    public Party getAccountHolder() {
        return accountHolder;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public List<Balance> getBalance() {
        if (balance == null) {
            balance = new ArrayList<>();
        }
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @TypeDefinition
    public static class AccountBuilder {
        private BigDecimal openingBalance = ZERO;
        private Date openingDate;
        private Currency currency;
        private String description;
        private String name;
        private Party accountHolder;
        private AccountType type;
        public AccountBuilder() {
        }

        public AccountBuilder accountHolder(Party accountHolder) {
            this.accountHolder = accountHolder;
            return this;
        }

        public AccountBuilder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public AccountBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AccountBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AccountBuilder type(AccountType type) {
            this.type = type;
            return this;
        }

        public AccountBuilder openingBalance(CurrencyAmount openingBalance) {
            if (openingBalance != null) {
                this.currency = openingBalance.getCurrency();
                this.openingBalance = openingBalance.getAmount();
            }
            return this;
        }

        public AccountBuilder openingDate(Date opendingDate) {
            this.openingDate = opendingDate;
            return this;
        }

        public AccountBuilder openingTodayWithZeroBalance() {
            return this;
        }

        public Account build() throws LedgerValidationException {
            validate();
            return new Account(this);
        }

        public void validate() throws LedgerValidationException {
            if (currency == null) {
                throw new LedgerValidationException("currency == null");
            }

            if (accountHolder == null) {
                throw new LedgerValidationException("accountHolder == null");
            }

            if (type == null) {
                throw new LedgerValidationException("type == null");
            }

            if (openingDate == null) {
                openingDate = new Date();
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((accountHolder == null) ? 0 : accountHolder.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((openingDate == null) ? 0 : openingDate.hashCode());
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Account other = (Account) obj;
        if (accountHolder == null) {
            if (other.accountHolder != null)
                return false;
        } else if (!accountHolder.equals(other.accountHolder))
            return false;
        if (balance == null) {
            if (other.balance != null)
                return false;
        } else if (!balance.equals(other.balance))
            return false;
        if (openingDate == null) {
            if (other.openingDate != null)
                return false;
        } else if (!openingDate.equals(other.openingDate))
            return false;
        if (currency != other.currency)
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "Account [currency=" + currency + ", name=" + name + ", description=" + description + ", accountHolder=" + accountHolder + ", type=" + type + ", getSystemId()=" + getSystemId()
                + ", getCreatedDate()=" + getCreatedDate() + ", getUpdatedDate()=" + getUpdatedDate() + ", getCreatedBy()=" + getCreatedBy() + ", getUpdatedBy()=" + getUpdatedBy()
                + ", getExternalSystemId()=" + getExternalSystemId() + "]";
    }
}
