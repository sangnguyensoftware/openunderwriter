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

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;

@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.balances.for.account", query = "select bal from Balance bal where bal.account = ?"),
})
public class Balance extends Type {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),  @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private CurrencyAmount amount;

    private Date date;

    @OneToOne(cascade = { REFRESH, DETACH, PERSIST })
    @JoinColumn(name = "journalLineUIDjli", referencedColumnName = "UID")
    private JournalLine journalLine;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="accountUID")
    private Account account;

    Balance() {
    }

    public Balance(Account account, Date date, JournalLine journalLine, CurrencyAmount amount) {
        this.date = date;
        this.journalLine = journalLine;
        this.amount = amount;
        this.account = account;
    }

    public Balance(Account account, Date date, CurrencyAmount amount) {
        this(account, date, null, amount);
    }

    public CurrencyAmount getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public JournalLine getJournalLine() {
        return journalLine;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((journalLine == null) ? 0 : journalLine.hashCode());
        result = prime * result + ((account == null) ? 0 : account.hashCode());
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
        Balance other = (Balance) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (journalLine == null) {
            if (other.journalLine != null)
                return false;
        } else if (!journalLine.equals(other.journalLine))
            return false;
        if (account == null) {
            if (other.account != null)
                return false;
        } else if (!account.equals(other.account))
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "Balance [amount=" + amount + ", date=" + date + ", journalLine=" + journalLine + ", account=" + account + ", getSystemId()=" + getSystemId() + "]";
    }
}
