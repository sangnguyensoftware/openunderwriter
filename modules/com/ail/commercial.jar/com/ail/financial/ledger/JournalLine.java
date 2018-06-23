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

import static com.ail.financial.ledger.TransactionType.CREDIT;
import static com.ail.financial.ledger.TransactionType.DEBIT;
import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.PaymentMethod;

@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "sum.debits.for.account.between.two.dates",  query = "select sum(jli.amount.amount) from JournalLine jli where jli.transactionType = 'DEBIT' and jli.ledger.account = ? and jli.journal.transactionDate between ? and ?"),
    @NamedQuery(name = "sum.credits.for.account.between.two.dates", query = "select sum(jli.amount.amount) from JournalLine jli where jli.transactionType = 'CREDIT' and jli.ledger.account = ? and jli.journal.transactionDate between ? and ?"),
    @NamedQuery(name = "journalLines.for.account", query="select jli from JournalLine jli where jli.ledger.account = ? order by jli.journal.transactionDate, jli.systemId"),
    @NamedQuery(name = "journalLines.for.account.between.two.dates", query="select jli from JournalLine jli where jli.ledger.account = ? and jli.journal.transactionDate between ? and ? order by jli.journal.transactionDate, jli.systemId")
})
public class JournalLine extends Type {
    @OneToOne(cascade = ALL)
    @JoinColumn(name = "ledgerUIDled", referencedColumnName = "UID")
    private Ledger ledger;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "journalUIDjou", referencedColumnName = "UID")
    private Journal journal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),  @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private CurrencyAmount amount;

    @Lob
    private String description;

    @Enumerated(STRING)
    private TransactionType transactionType;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "paymentMethodUIDpme", referencedColumnName = "UID")
    private PaymentMethod paymentMethod;

    @Enumerated(STRING)
    private JournalLineType type;

    JournalLine() {
    }

    private JournalLine(JournalLineBuilder builder) {
        amount = builder.amount;
        ledger = builder.ledger;
        description = builder.description;
        transactionType = builder.transactionType;
        paymentMethod = builder.paymentMethod;
        type = builder.type;
        getAttribute().addAll(builder.attributes);
    }

    public JournalLineType getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public CurrencyAmount getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentMethodId() {
        return paymentMethod!=null ? paymentMethod.getExternalSystemId() : null;
    }

    public String getLedgerId() {
        return ledger.getExternalSystemId();
    }

    public void setJournal(Journal journal) throws LedgerValidationException {
        if (this.journal == null) {
            this.journal = journal;
        }
        else {
            throw new LedgerValidationException("this.journal != null (once set journal cannot be altered.");
        }
    }

    public Journal getJournal() {
        return journal;
    }

    @TypeDefinition
    public static class JournalLineBuilder {
        private Ledger ledger;
        private CurrencyAmount amount;
        private String description;
        private TransactionType transactionType;
        private JournalLineType type;
        private List<Attribute> attributes = new ArrayList<>();
        private PaymentMethod paymentMethod;

        public JournalLineBuilder() {
        }

        public JournalLineBuilder ledger(Ledger ledger) {
            this.ledger = ledger;
            return this;
        }

        public JournalLineBuilder credit() {
            this.transactionType = CREDIT;
            return this;
        }

        public JournalLineBuilder debit() {
            this.transactionType = DEBIT;
            return this;
        }

        public JournalLineBuilder transactionType(TransactionType type) {
            this.transactionType = type;
            return this;
        }

        public JournalLineBuilder ofType(JournalLineType type) {
            this.type = type;
            return this;
        }

        public JournalLineBuilder by(CurrencyAmount amount) {
            this.amount = amount;
            return this;
        }

        public JournalLineBuilder using(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public JournalLineBuilder description(String description) {
            this.description = description;
            return this;
        }

        public JournalLineBuilder attribute(Attribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public JournalLine build() throws LedgerValidationException {
            validate();
            return new JournalLine(this);
        }

        private void validate() throws LedgerValidationException {
            if (ledger == null) {
                throw new LedgerValidationException("ledger == null");
            }

            if (amount == null) {
                throw new LedgerValidationException("amount == null");
            }

            if (transactionType == null) {
                throw new LedgerValidationException("transactionType == null");
            }

            if (amount.getCurrency() != ledger.getAccount().getCurrency()) {
                throw new LedgerValidationException("amount.getCurrency() != ledger.getAccount().getCurrency()");
            }

            if (amount.getAmount().compareTo(ZERO) < 0) {
                throw new LedgerValidationException("amount.getAmount() < 0");
            }

            if (type == null) {
                throw new LedgerValidationException("type == null");
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((ledger == null) ? 0 : ledger.hashCode());
        result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
        result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        JournalLine other = (JournalLine) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (ledger == null) {
            if (other.ledger != null)
                return false;
        } else if (!ledger.equals(other.ledger))
            return false;
        if (paymentMethod == null) {
            if (other.paymentMethod != null)
                return false;
        } else if (!paymentMethod.equals(other.paymentMethod))
            return false;
        if (transactionType != other.transactionType)
            return false;
        if (type != other.type)
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "JournalLine [ledger=" + ledger + ", amount=" + amount + ", description=" + description + ", transactionType=" + transactionType + ", paymentMethod=" + paymentMethod + ", type=" + type
                + ", getSystemId()=" + getSystemId() + ", getCreatedDate()=" + getCreatedDate() + ", getUpdatedDate()=" + getUpdatedDate() + ", getCreatedBy()=" + getCreatedBy() + ", getUpdatedBy()="
                + getUpdatedBy() + ", getExternalSystemId()=" + getExternalSystemId() + "]";
    }
}
