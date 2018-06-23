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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.ledger.for.party.and.currency", query = "select led from Ledger led where led.account.accountHolder = ? and led.account.currency = ?"),
    @NamedQuery(name = "get.ledger.for.account", query = "select led from Ledger led where led.account = ?")
})
public class Ledger extends Type {
    @OneToOne(cascade = { REFRESH, DETACH, PERSIST })
    @JoinColumn(name = "accountUIDacc", referencedColumnName = "UID")
    private Account account;

    private String name;

    @Lob
    private String description;

    @OneToMany(cascade = { REFRESH, DETACH, PERSIST })
    private List<JournalLine> journalLine;

    Ledger() {
    }

    private Ledger(LedgerBuilder builder) {
        account = builder.account;
        name = builder.name;
        description = builder.description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getAccount() {
        return account;
    }

    public String getAccountId() {
        return account.getExternalSystemId();
    }

    /**
     * Add a new JournalLine to this ledger.
     * @param line
     */
    public void post(JournalLine line) {
        if (journalLine == null) {
            journalLine = new ArrayList<>();
        }

        journalLine.add(line);
    }

    /**
     * Return an unmodifiable list of the journal lines associated with this Ledger.
     * The list of journal lines associated with a ledger can only be appended to
     * using the {@link #post(JournalLine)} method. No other kinds of modification
     * are supported.
     *
     * @return List<JournalLine>
     */
    public List<JournalLine> getJournalLine() {
        return Collections.unmodifiableList(journalLine);
    }

    @TypeDefinition
    public static class LedgerBuilder {
        private Account account;
        private String name;
        private String description;

        public LedgerBuilder forAccount(Account account) {
            this.account = account;
            return this;
        }

        public LedgerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LedgerBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Ledger build() throws LedgerValidationException {
            validate();
            return new Ledger(this);
        }

        private void validate() throws LedgerValidationException {
            if (account == null) {
                throw new LedgerValidationException("account == null");
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((account == null) ? 0 : account.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Ledger other = (Ledger) obj;
        if (account == null) {
            if (other.account != null)
                return false;
        } else if (!account.equals(other.account))
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
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "Ledger [account=" + account + ", name=" + name + ", description=" + description + ", getSystemId()=" + getSystemId() + ", getCreatedDate()=" + getCreatedDate() + ", getUpdatedDate()="
                + getUpdatedDate() + ", getCreatedBy()=" + getCreatedBy() + ", getUpdatedBy()=" + getUpdatedBy() + ", getExternalSystemId()=" + getExternalSystemId() + "]";
    }
}
