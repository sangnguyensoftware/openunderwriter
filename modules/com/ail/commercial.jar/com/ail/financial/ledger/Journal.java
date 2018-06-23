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

import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.EnumType.STRING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.MoneyProvision;

@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.journal.by.origin.and.type.and.transaction.date.range",
                query = "select jou from Journal jou where jou.origin = ? and type = ? and jou.transactionDate >= ? and jou.transactionDate <= ?"),
    @NamedQuery(name = "get.journal.by.subject",
                query = "select jou from Journal jou where jou.subject.type = ? and jou.subject.refId = ?"),
    @NamedQuery(name = "get.journal.by.subject.and.journal.type",
                query = "select jou from Journal jou where jou.subject.type = ? and jou.subject.refId = ? and jou.type = ?")
})

public class Journal extends Type {

    private Date transactionDate;
    private Date postedDate;

    @OneToMany(cascade = {REFRESH, DETACH, PERSIST})
    private List<JournalLine> journalLine;

    @OneToOne(cascade = {REFRESH, DETACH, PERSIST})
    @JoinColumn(name = "contraOfUIDjou", referencedColumnName = "UID")
    private Journal contraOf;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "subjectType")),  @AttributeOverride(name = "refId", column = @Column(name = "subjectId"))
    })
    private Reference subject;

    @Enumerated(STRING)
    private JournalType type;

    @OneToOne(cascade = {REFRESH, DETACH, PERSIST})
    @JoinColumn(name = "originUIDmpr", referencedColumnName = "UID")
    private MoneyProvision origin;

    @OneToOne(cascade = {REFRESH, DETACH, PERSIST})
    @JoinColumn(name = "accountingPeriodUIDape", referencedColumnName = "UID")
    AccountingPeriod accountingPeriod;

    Journal() {
    }

    private Journal(JournalBuilder builder) throws LedgerValidationException {
        transactionDate = builder.transactionDate;
        journalLine = builder.journalLine;
        contraOf = builder.contraOf;
        subject = builder.subject;
        type = builder.type;
        postedDate = new Date();
        origin = builder.origin;

        for(JournalLine line: journalLine) {
            line.setJournal(this);
        }
    }

    public Journal getContraOf() {
        return contraOf;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Return an unmodifiable list of the journal lines associated with this Journal.
     * @return List<JournalLine>
     */
    public List<JournalLine> getJournalLine() {
        return Collections.unmodifiableList(journalLine);
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public Reference getSubject() {
        return subject;
    }

    public JournalType getType() {
        return type;
    }

    public MoneyProvision getOrigin() {
        return origin;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void initialiseAccountingPeriod(AccountingPeriod accountingPeriod) {
        if (this.accountingPeriod == null) {
            this.accountingPeriod = accountingPeriod;
        }
    }

    @TypeDefinition
    public static class JournalBuilder {
        private Date transactionDate;
        private List<JournalLine> journalLine = new ArrayList<>();
        private Journal contraOf;
        private JournalType type;
        private Reference subject;
        private MoneyProvision origin;

        public JournalBuilder withTransactionDate(Date date) {
            this.transactionDate = date;
            return this;
        }

        public JournalBuilder with(JournalLine... journalLine) {
            this.journalLine.addAll(Arrays.asList(journalLine));
            return this;
        }

        public JournalBuilder with(Set<JournalLine> journalLine) {
            this.journalLine.addAll(journalLine);
            return this;
        }

        public Journal build() throws LedgerValidationException {
            validate();
            return new Journal(this);
        }

        public JournalBuilder ofType(JournalType type) {
            this.type = type;
            return this;
        }

        public JournalBuilder contraOf(Journal contraOf) {
            this.contraOf = contraOf;
            return this;
        }

        public JournalBuilder subject(Reference subject) {
            this.subject= subject;
            return this;
        }

        public JournalBuilder forOrigin(MoneyProvision origin) {
            this.origin = origin;
            return this;
        }

        private void validate() throws LedgerValidationException {
            if (transactionDate == null) {
                throw new LedgerValidationException("date == null");
            }

            if (journalLine.size() < 2) {
                throw new LedgerValidationException("journalLine.size() < 2");
            }

            if (type == null) {
                throw new LedgerValidationException("type == null");
            }

            if (jouralLinesDontBalance(journalLine)) {
                throw new LedgerValidationException("journalDoesNotBalance() == true");
            }

            if (journalLinesPredateAccountOpening(journalLine)) {
                throw new LedgerValidationException("journalLinesPredateAccountOpening() == true");
            }

            for(JournalLine line: journalLine) {
                if (line.getJournal() != null) {
                    throw new LedgerValidationException("line.getJournal() != null");
                }
            }
        }

        private boolean jouralLinesDontBalance(List<JournalLine> lines) {
            CurrencyAmount total = null;

            for(JournalLine line: lines) {
                if (total == null) {
                    total = new CurrencyAmount(0, line.getAmount().getCurrency());
                }

                switch(line.getTransactionType()) {
                case CREDIT: total = total.add(line.getAmount()); break;
                case DEBIT: total = total.subtract(line.getAmount()); break;
                }
            }

            return ZERO.compareTo(total.getAmount()) != 0;
        }

        private boolean journalLinesPredateAccountOpening(List<JournalLine> lines) {
            for(JournalLine line: lines) {
                if (!transactionDate.after(line.getLedger().getAccount().getOpeningDate())) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((contraOf == null) ? 0 : contraOf.hashCode());
        result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
        result = prime * result + ((journalLine == null) ? 0 : journalLine.hashCode());
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
        Journal other = (Journal) obj;
        if (contraOf == null) {
            if (other.contraOf != null)
                return false;
        } else if (!contraOf.equals(other.contraOf))
            return false;
        if (transactionDate == null) {
            if (other.transactionDate != null)
                return false;
        } else if (!transactionDate.equals(other.transactionDate))
            return false;
        if (journalLine == null) {
            if (other.journalLine != null)
                return false;
        } else if (!journalLine.equals(other.journalLine))
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "Journal [date=" + transactionDate + ", journalLine=" + journalLine + ", contraOf=" + contraOf + ", getSystemId()=" + getSystemId() + ", getCreatedDate()=" + getCreatedDate()
                + ", getUpdatedDate()=" + getUpdatedDate() + ", getCreatedBy()=" + getCreatedBy() + ", getUpdatedBy()=" + getUpdatedBy() + ", getExternalSystemId()=" + getExternalSystemId() + "]";
    }
}
