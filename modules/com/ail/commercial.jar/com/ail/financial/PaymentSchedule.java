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

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * A payment schedule groups together a collection of {@link com.ail.financial.MoneyProvision MoneyProvisions}.</p>
 * For example, a schedule might define:-<ol>
 * <li>A single payment of &pound;30 to be made by direct debit; or</li>
 * <li>A payment of &pound;30 to be made by Master Card, followed by 10 monthly payments of &pound;43 to be made by direct debit.</li>
 * </ol>
 */
@Audited
@Entity
@TypeDefinition
public class PaymentSchedule extends Type {
    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = ALL)
    private List<MoneyProvision> moneyProvision;

    /** A textual description of the reason for the payment. */
    private String description;

    @Deprecated
    @Enumerated(STRING)
    private PaymentScheduleType type = PaymentScheduleType.UNDEFINED;

    /**
     * @param moneyProvision List of money provisions
     * @param description Textual description
     */
    public PaymentSchedule(List<MoneyProvision> moneyProvision, String description) {
        super();
        this.moneyProvision = moneyProvision;
        this.description = description;
    }

    /**
     * Constructor
     */
    public PaymentSchedule() {
        moneyProvision=new ArrayList<>();
    }

    /**
     * Getter returning the value of the description property. A textual description of the reason for the payment.
     * @return Value of the description property
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter to update the value of the description property. A textual description of the reason for the payment.
     * @param description New value for the description property
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Fetch the list of money provisions associated with this schedule. This list will never
     * be null.
     * @return List of money provisions.
     */
    public List<MoneyProvision> getMoneyProvision() {
        return moneyProvision;
    }

    /**
     * Set the list of money provisions associated with this schedule.
     * @param moneyProvision
     */
    public void setMoneyProvision(List<MoneyProvision> moneyProvision) {
        if (moneyProvision==null) {
            this.moneyProvision=new ArrayList<>();
        }
        else {
            this.moneyProvision = moneyProvision;
        }
    }

    /**
     * Calculate the total amount represented by this schedule.
     * @return
     * @throws IllegalStateException if the schedule is empty.
     */
    public CurrencyAmount calculateTotal() {
         if (getMoneyProvision()==null || getMoneyProvision().size()==0) {
             throw new IllegalStateException("getMoneyProvision()==null || getMoneyProvision().size()==0");
         }

        CurrencyAmount total=null;

        for(MoneyProvision prov: getMoneyProvision()) {
            if (total==null) {
                total=new CurrencyAmount(prov.calculateTotal());
            }
            else {
                total=total.add(prov.calculateTotal());
            }
        }

        return total;
    }

    /**
     * Return the first payment
     * @return
     */
    public MoneyProvision getFirstProvision() {

        List<MoneyProvision> provisions = getMoneyProvision();
        MoneyProvision selectProvision = null;
        for (Iterator<MoneyProvision> iterator = provisions.iterator(); iterator.hasNext();) {
            MoneyProvision nextProvision = (MoneyProvision)iterator.next();
            if (selectProvision == null) {
                selectProvision = nextProvision;
            }
            if (nextProvision.getPaymentsStartDate().before(
                    selectProvision.getPaymentsStartDate())) {
                selectProvision = nextProvision;
            }
        }
        return selectProvision;
    }

    /** @deprecated Use {@link MoneyProvision#getPurpose()} instead. */
    @Deprecated
    public PaymentScheduleType getType() {
        return type;
    }

    /** @deprecated Use {@link MoneyProvision#setPurpose()} instead. */
    @Deprecated
    public void setType(PaymentScheduleType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((moneyProvision == null) ? 0 : moneyProvision.hashCode());
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
        PaymentSchedule other = (PaymentSchedule) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (moneyProvision == null) {
            if (other.moneyProvision != null)
                return false;
        } else if (!moneyProvision.equals(other.moneyProvision))
            return false;
        if (type == null) {
            if (other.type!= null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentSchedule [moneyProvision=" + moneyProvision + ", description=" + description + ", type=" + type +"]";
    }
}
