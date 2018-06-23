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

import static com.ail.core.DateTimeUtils.dateToLocalDate;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionStatus.NEW;
import static javax.persistence.EnumType.STRING;
import static org.hibernate.annotations.CascadeType.DETACH;
import static org.hibernate.annotations.CascadeType.MERGE;
import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.REFRESH;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.NotImplementedError;
import com.ail.core.Type;

/**
 * MoneyProvision objects represents an amount of money that is payable or due
 * from one party to another party. It may be a single payment, or a number of
 * payments of an identical amount at a frequency.
 * <p/>
 * For example it may represent:-
 * <ol>
 * <li>a single payment of &pound;30 by direct debit from account number
 * xxxxxxx, sort code yyyyyy; or</li>
 * <li>10 payments of &pound;30 each paid monthly by Master Card with card
 * number xxxxx, card holder yyy.</li>
 * </ol>
 */
@Audited
@Entity
@TypeDefinition
@NamedQueries({
    @NamedQuery(name = "get.moneyprovision.by.externalSystemId", query = "select mpr from MoneyProvision mpr where mpr.externalSystemId = ?"),
    @NamedQuery(name = "get.pending.payments", query = "select mpr from MoneyProvision mpr, PaymentSchedule pse, Policy pol where mpr.status='NEW' and mpr.paymentsStartDate between ? and ? and mpr member of pse.moneyProvision and pse = pol.paymentDetails"),
    @NamedQuery(name = "get.policy.for.moneyprovision", query = "select pol from Policy pol left join pol.paymentDetails.moneyProvision as mpr where mpr = ?" ),
})
public class MoneyProvision extends Type {
    private static final long serialVersionUID = 1L;

    /** The method by which the payment will or has been made. */
    @OneToOne
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "mprPaymentMethodUIDpme")
    private PaymentMethod paymentMethod;

    /** The amount of money. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),  @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private CurrencyAmount amount;

    /** The date at which payments start. */
    private Date paymentsStartDate;

    /** The date at which payments end */
    private Date paymentsEndDate;

    /** The number of payments to be made */
    private int number;

    /** A textual description of the reason for the payment. */
    private String description;

    /** Payment frequency */
    @Enumerated(STRING)
    private FinancialFrequency frequency;

    /** The status represents where the money provision is in its life-cycle. */
    @Enumerated(STRING)
    private MoneyProvisionStatus status;

    /** The purpose (or type) for this money provision. */
    @Enumerated(STRING)
    private MoneyProvisionPurpose purpose;

    /**
     * Payment ID. The ID by which this payment is know to the payment
     * system that is processing it. This may be different from the
     * {@link #saleId} and will be set as soon as processing starts (i.e. at the
     * point that the request is sent to the payment system.
     */
    private String paymentId;

    /**
     * The ID by which this payment is know to the payment system that is
     * processing it. This may be different from {@link #paymentId} Id and
     * will only be set when the payment is complete.
     */
    private String saleId;

    /**
     * Day upon which a payment will be made. This is only relevant when the
     * {@link #getFrequency() frequency} is either WEEKLY or MONTHLY. For
     * WEEKLY, valid values are from 1 (Monday) to 7 (Sunday). For MONTHLY,
     * valid values are 1 to 28.
     */
    private int day = -1;

    /**
     * Default constructor
     */
    public MoneyProvision() {
        super();
        this.setStatus(NEW);
    }


    /**
     * Long hand constructor. Creates a new MoneyProvision with the data
     * supplied. The status of the new instance is set to NEW.
     *
     * @param number
     *            The number of payments to be made.
     * @param amount
     *            The amount of each payment (all are equal).
     * @param purpose
     *            The purpose of this provision.
     * @param frequency
     *            The frequency of payments (Monthly, Weekly, etc).
     * @param paymentMethod
     *            The method e.g. payment card, direct debit, etc.
     * @param paymentsStartDate
     *            The date when payments start.
     * @param paymentsEndDate
     *            The date when payments end.
     * @param description
     *            Any textual description.
     */
    public MoneyProvision(int number, CurrencyAmount amount, MoneyProvisionPurpose purpose, FinancialFrequency frequency, PaymentMethod paymentMethod, Date paymentsStartDate, Date paymentsEndDate, String description) {
        super();
        this.paymentMethod = paymentMethod;
        this.number = number;
        this.amount = amount;
        this.purpose = purpose;
        this.paymentsStartDate = paymentsStartDate;
        this.paymentsEndDate = paymentsEndDate;
        this.frequency = frequency;
        this.description = description;
        this.setStatus(NEW);
    }

    /**
     * Long hand constructor. Creates a new MoneyProvision with the data
     * supplied. The status of the new instance is set to NEW.
     * @deprecated Use {@link #MoneyProvision(int, CurrencyAmount, MoneyProvisionPurpose, FinancialFrequency, PaymentMethod, Date, Date, String)} instead.
     * @param number
     *            The number of payments to be made.
     * @param amount
     *            The amount of each payment (all are equal).
     * @param frequency
     *            The frequency of payments (Monthly, Weekly, etc).
     * @param paymentMethod
     *            The method e.g. payment card, direct debit, etc.
     * @param paymentsStartDate
     *            The date when payments start.
     * @param paymentsEndDate
     *            The date when payments end.
     * @param description
     *            Any textual description.
     */
    @Deprecated
    public MoneyProvision(int number, CurrencyAmount amount, FinancialFrequency frequency, PaymentMethod paymentMethod, Date paymentsStartDate, Date paymentsEndDate, String description) {
        this(number, amount, null, frequency, paymentMethod, paymentsStartDate, paymentsEndDate, description);
    }

    /**
     * Shorthand constructor which defaults to creating a single payment with
     * the time now() as the start and end date, and NEW as the status.
     * @param amount
     *            The amount of each payment (all are equal).
     * @param purpose
     *            The purpose of this provision.
     * @param paymentMethod
     *            The method e.g. payment card, direct debit, etc.
     * @param description
     *            Any textual description.
     */
    public MoneyProvision(CurrencyAmount amount, MoneyProvisionPurpose purpose, PaymentMethod paymentMethod, String description) {
        this(1, amount, purpose, ONE_TIME, paymentMethod, new Date(), new Date(), description);
    }

    /**
     * Shorthand constructor which defaults to creating a single payment with
     * the time now() as the start and end date, and NEW as the status.
     * @deprecated Use {@link #MoneyProvision(CurrencyAmount, MoneyProvisionPurpose, PaymentMethod, String)} instead.
     * @param amount
     *            The amount of each payment (all are equal).
     * @param paymentMethod
     *            The method e.g. payment card, direct debit, etc.
     * @param description
     *            Any textual description.
     */
    @Deprecated
    public MoneyProvision(CurrencyAmount amount, PaymentMethod paymentMethod, String description) {
        this(amount, null, paymentMethod, description);
    }

    /**
     * Getter returning the value of the amount property. The amount of money.
     *
     * @return Value of the amount property
     */
    public CurrencyAmount getAmount() {
        return amount;
    }

    /**
     * Setter to update the value of the amount property. The amount of money.
     *
     * @param amount
     *            New value for the amount property
     */
    public void setAmount(CurrencyAmount amount) {
        this.amount = amount;
    }

    /**
     * Getter returning the value of the paymentsStartDate property. The date at
     * which payments start.
     *
     * @return Value of the paymentsStartDate property
     */
    public Date getPaymentsStartDate() {
        return paymentsStartDate;
    }

    /**
     * Setter to update the value of the paymentsStartDate property. The date at
     * which payments start.
     *
     * @param paymentsStartDate
     *            New value for the paymentsStartDate property
     */
    public void setPaymentsStartDate(Date paymentsStartDate) {
        this.paymentsStartDate = paymentsStartDate;
    }

    /**
     * Getter returning the value of the paymentsEndDate property. The date at
     * which payments end
     *
     * @return Value of the paymentsEndDate property
     */
    public Date getPaymentsEndDate() {
        return paymentsEndDate;
    }

    /**
     * Setter to update the value of the paymentsEndDate property. The date at
     * which payments end
     *
     * @param paymentsEndDate
     *            New value for the paymentsEndDate property
     */
    public void setPaymentsEndDate(Date paymentsEndDate) {
        this.paymentsEndDate = paymentsEndDate;
    }

    /**
     * Getter returning the value of the description property. A textual
     * description of the reason for the payment.
     *
     * @return Value of the description property
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter to update the value of the description property. A textual
     * description of the reason for the payment.
     *
     * @param description
     *            New value for the description property
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter returning the value of the frequency property. Payment frequency
     *
     * @return Value of the frequency property
     */
    public FinancialFrequency getFrequency() {
        return frequency;
    }

    /**
     * Setter to update the value of the frequency property. Payment frequency
     *
     * @param frequency
     *            New value for the frequency property
     */
    public void setFrequency(FinancialFrequency frequency) {
        this.frequency = frequency;
    }

    /**
     * Get the value of the frequency property as a string (as opposed to an
     * instance of FinancialFrequency).
     *
     * @return String representation of the frequency, or null if the property
     *         has not been set.
     */
    public String getFrequencyAsString() {
        if (frequency != null) {
            return frequency.name();
        }
        return null;
    }

    /**
     * Set the frequency property as a String. The String must represents a
     * valid FinancialFrequency. i.e. it must be suitable for a call to
     * FinancialFrequency.forName().
     *
     * @param frequency
     *            New value for property.
     * @throws IndexOutOfBoundsException
     *             If frequency is not a valid FinancialFrequency.
     */
    public void setFrequencyAsString(String frequency) throws IndexOutOfBoundsException {
        this.frequency = FinancialFrequency.valueOf(frequency);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public MoneyProvisionStatus getStatus() {
        return status;
    }

    public void setStatus(MoneyProvisionStatus status) {
        this.status = status;
    }

    public MoneyProvisionPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(MoneyProvisionPurpose type) {
        this.purpose = type;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    /**
     * Day upon which a payment will be made. This is only relevant when the
     * {@link #getFrequency() frequency} is either WEEKLY or MONTHLY. For
     * WEEKLY, valid values are from 1 (Monday) to 7 (Sunday). For MONTHLY,
     * valid values are 1 to 28.
     */
    public int getDay() {
        return day;
    }

    /**
     * Day upon which a payment will be made. This is only relevant when the
     * {@link #getFrequency() frequency} is either WEEKLY or MONTHLY. For
     * WEEKLY, valid values are from 1 (Monday) to 7 (Sunday). For MONTHLY,
     * valid values are 1 to 28.
     */
    public void setDay(int day) {
        this.day = day;
    }

    public int moneyProvisionFiresOn(Date time) {
        LocalDate paymentStartDate = dateToLocalDate(getPaymentsStartDate());
        LocalDate paymentEndDate =  dateToLocalDate(getPaymentsEndDate() != null ? getPaymentsEndDate() : new Date(Long.MAX_VALUE));
        LocalDate testDate = dateToLocalDate(time);

        return compareDateWithRange(testDate, paymentStartDate, paymentEndDate);
    }

    /**
     * Test whether this MoneyProvision is payable on a specific day.
     */
    public boolean isPayableOn(LocalDate testDate) {
        LocalDate paymentStartDate = dateToLocalDate(getPaymentsStartDate());
        LocalDate paymentEndDate =  dateToLocalDate(getPaymentsEndDate() != null ? getPaymentsEndDate() : new Date(Long.MAX_VALUE));

        if (compareDateWithRange(testDate, paymentStartDate, paymentEndDate) != 0) {
            return false;
        }

        switch(getFrequency()) {
        case ONE_TIME:
            return paymentStartDate.equals(testDate);
        case WEEKLY:
            return (getDay() == testDate.getDayOfWeek().getValue());
        case MONTHLY:
            return (getDay() == testDate.getDayOfMonth());
        case QUARTERLY:
            return (paymentStartDate.getDayOfMonth() == testDate.getDayOfMonth() &&
                        (paymentStartDate.getMonthValue() == testDate.getMonthValue() ||
                         paymentStartDate.plusMonths(3).getMonthValue() == testDate.getMonthValue() ||
                         paymentStartDate.plusMonths(6).getMonthValue() == testDate.getMonthValue() ||
                         paymentStartDate.plusMonths(9).getMonthValue() == testDate.getMonthValue()
                        )
                   );
        case SEMESTERLY:
            return (paymentStartDate.getDayOfMonth() == testDate.getDayOfMonth() &&
                        (paymentStartDate.getMonthValue() == testDate.getMonthValue() ||
                         paymentStartDate.plusMonths(6).getMonthValue() == testDate.getMonthValue()
                        )
                   );
        case YEARLY:
            return (paymentStartDate.getDayOfMonth() == testDate.getDayOfMonth() &&
                    paymentStartDate.getMonthValue() == testDate.getMonthValue());
        case BIWEEKLY:
        case BIMONTHLY:
        case UNDEFINED:
            throw new NotImplementedError("Payment frequency of: "+getFrequency()+" is not supported by this service.");
        }

        return false;
    }

    private int compareDateWithRange(LocalDate testDate, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(testDate)) {
            return -1;
        }
        else if (endDate.isBefore(testDate) && !endDate.equals(testDate)) {
            return 1;
        }

        return 0;
    }

    /**
     * Set {@link #setDay(int)} to be consistent with {@link #getPaymentsEndDate()}.
     * For example: if the payment <code>frequency</code> is set to
     * <code>MONTHLY<code> and the <code>paymentsStartDate</code> is set to
     * 10/01/2017, then <code>day</code> will be set to 10.
     */
    public void alignDatesToDays() {
        LocalDate startDate;

        switch(frequency) {
        case WEEKLY:
            startDate = dateToLocalDate(paymentsStartDate);
            setDay(startDate.getDayOfWeek().getValue());
            break;
        case MONTHLY:
            startDate = dateToLocalDate(paymentsStartDate);
            setDay(startDate.getDayOfMonth());
            break;
        }
    }

    /**
     * Calculate the total value of this provision assuming that all payments
     * were made.
     *
     * @return amount * number
     */
    public CurrencyAmount calculateTotal() {
        BigDecimal tot = amount.getAmount().multiply(new BigDecimal(number));
        return new CurrencyAmount(tot, amount.getCurrency());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + typeHashCode();
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
        result = prime * result + number;
        result = prime * result + day;
        result = prime * result + ((paymentId == null) ? 0 : paymentId.hashCode());
        result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
        result = prime * result + ((paymentsEndDate == null) ? 0 : paymentsEndDate.hashCode());
        result = prime * result + ((paymentsStartDate == null) ? 0 : paymentsStartDate.hashCode());
        result = prime * result + ((saleId == null) ? 0 : saleId.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        MoneyProvision other = (MoneyProvision) obj;
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
        if (frequency != other.frequency)
            return false;
        if (number != other.number)
            return false;
        if (day != other.day)
            return false;
        if (paymentId == null) {
            if (other.paymentId != null)
                return false;
        } else if (!paymentId.equals(other.paymentId))
            return false;
        if (paymentMethod == null) {
            if (other.paymentMethod != null)
                return false;
        } else if (!paymentMethod.equals(other.paymentMethod))
            return false;
        if (paymentsEndDate == null) {
            if (other.paymentsEndDate != null)
                return false;
        } else if (!paymentsEndDate.equals(other.paymentsEndDate))
            return false;
        if (paymentsStartDate == null) {
            if (other.paymentsStartDate != null)
                return false;
        } else if (!paymentsStartDate.equals(other.paymentsStartDate))
            return false;
        if (saleId == null) {
            if (other.saleId != null)
                return false;
        } else if (!saleId.equals(other.saleId))
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MoneyProvision [systemId="+getSystemId()+", paymentMethod=" + paymentMethod + ", amount=" + amount + ", paymentsStartDate=" + paymentsStartDate + ", paymentsEndDate=" + paymentsEndDate + ", number=" + number
                + ", description=" + description + ", frequency=" + frequency + ", day=" + day + ", status=" + status + ", paymentId=" + paymentId + ", saleId=" + saleId + "]";
    }
}
