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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * A PaymentRecord represents a single event in a payment history. This type is
 * immutable. Events record the processing of payments in terms of the placement
 * of the original payment request (with PayPal for example), the approval or
 * cancellation of that request. Each element details the amount involved, the
 * date of the event, the type of event.
 */
@Audited
@Entity
@TypeDefinition
@Inheritance(strategy = TABLE_PER_CLASS)
public class PaymentRecord extends Type {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),  @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    CurrencyAmount amount;

    Date date;

    @Enumerated(STRING)
    PaymentRecordType type;

    String methodIdentifier;

    String transactionReference;

    String description;

    public PaymentRecord() {
    }

    public PaymentRecord(String amountAsString, String currencyAsString, String transactionReference, String methodIdentifer, String type, String dateAsString, String description) throws ParseException {
        this.amount = new CurrencyAmount(amountAsString, currencyAsString);
        this.transactionReference = transactionReference;
        this.methodIdentifier = methodIdentifer;
        this.type = PaymentRecordType.valueOf(type);
        this.date = new SimpleDateFormat(DATE_FORMAT).parse(dateAsString);
        this.description = description;
    }

    public PaymentRecord(CurrencyAmount currencyAmount, String transactionReference, String methodIdentifier, PaymentRecordType type, Date date) {
        this.transactionReference = transactionReference;
        this.amount = currencyAmount;
        this.methodIdentifier = methodIdentifier;
        this.type = type;
        this.date = date;
    }

    public PaymentRecord(CurrencyAmount currencyAmount, String transactionReference, PaymentMethod paymentMethod, PaymentRecordType type, String description) {
        this(currencyAmount, transactionReference, paymentMethod.getId(), type, new Date());
        this.description=description;
    }

    public PaymentRecord(CurrencyAmount currencyAmount, String transactionReference, PaymentMethod paymentMethod, PaymentRecordType type) {
        this(currencyAmount, transactionReference, paymentMethod.getId(), type, new Date());
    }

    public CurrencyAmount getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public PaymentRecordType getType() {
        return type;
    }

    public String getMethodIdentifier() {
        return methodIdentifier;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getAmountAsString() {
        return amount.getAmountAsString();
    }

    public String getCurrencyAsString() {
        return amount.getCurrencyAsString();
    }

    public String getDateAsString() {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    public String getTypeAsString() {
        return type.getName();
    }

    public String getDescription() {
        return description;
    }

    /**
     * PaymentRecord is immutable, so we can't leave the cloning to Type's
     * generalised clone implementation. We have to do it locally.
     *
     * @throws CloneNotSupportedException
     *             If the type cannot be deep cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new PaymentRecord(amount, transactionReference, methodIdentifier, type, date);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((methodIdentifier == null) ? 0 : methodIdentifier.hashCode());
        result = prime * result + ((transactionReference == null) ? 0 : transactionReference.hashCode());
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
        if (!typeEquals(obj))
            return false;
        PaymentRecord other = (PaymentRecord) obj;
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
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (methodIdentifier == null) {
            if (other.methodIdentifier != null)
                return false;
        } else if (!methodIdentifier.equals(other.methodIdentifier))
            return false;
        if (transactionReference == null) {
            if (other.transactionReference != null)
                return false;
        } else if (!transactionReference.equals(other.transactionReference))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentRecord [amount=" + amount + ", date=" + date + ", type=" + type + ", methodIdentifier=" + methodIdentifier + ", transactionReference=" + transactionReference + ", description="
                + description + "]";
    }
}
