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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import com.ail.core.Cloneable;
import com.ail.core.Mergable;
import com.ail.core.ThreadLocale;
import com.ail.util.Rate;

/**
 * Instances of this class represent amounts of money.
 *
 * @since Commercial 2.4 this class is immutable.
 */
@Embeddable
public class CurrencyAmount implements Serializable, Cloneable, Mergable  {
    static final long serialVersionUID = -7610940646036255844L;
    private static String format = "#0.00";

    private BigDecimal amount = null;

    @Enumerated(STRING)
    private Currency currency = null;

    /**
     * Package private no-args constructor to satisfy castor/hibernate etc.
     */
    CurrencyAmount() {
    }

    /**
     * Create a new instance of CurrencyAmount matching the value and currency
     * of the supplied instance.
     *
     * @param that
     *            instance to copy values from.
     */
    public CurrencyAmount(CurrencyAmount that) {
        setAmount(that.getAmount());
        setCurrency(that.getCurrency());
    }

    /**
     * Constructor
     *
     * @param amount
     *            The amount
     * @param currency
     *            The currency
     */
    public CurrencyAmount(BigDecimal amount, Currency currency) {
        amount = amount.setScale(currency.getFractionDigits(), BigDecimal.ROUND_HALF_UP);
        setAmount(amount);
        setCurrency(currency);
    }

    /**
     * Constructor
     *
     * @param amount
     *            The amount
     * @param currency
     *            The currency
     */
    public CurrencyAmount(String amount, Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    /**
     * Constructor
     *
     * @param amount
     *            The amount
     * @param currency
     *            The currency
     */
    public CurrencyAmount(BigDecimal amount, String currency) {
        this.setAmount(amount);
        this.setCurrencyAsString(currency);
    }

    /**
     * Constructor
     *
     * @param amount
     *            The amount
     * @param currency
     *            The currency.
     */
    public CurrencyAmount(double amount, Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    /**
     * Constructor
     *
     * @param amount
     *            The amount as a String
     * @param currency
     *            The currency as a String
     */
    public CurrencyAmount(String amount, String currency) {
        this.setCurrencyAsString(currency);
        this.setAmountAsString(amount);
    }

    /**
     * Construct a currency amount for a string formated as "<amount><ISO currency code>". For example: "100GBP", "11.50USD"
     * @param amount String in the format "<number><ISO currency code>".
     */
    public CurrencyAmount(String amount) {
        this.setCurrencyAsString(amount.substring(amount.length()-3));
        this.setAmountAsString(amount.substring(0, amount.length()-3));
    }

    /**
     * Get the amount represented
     *
     * @return the amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Set the amount property.
     *
     * @param amount
     */
    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Set the amount property from a string.
     *
     * @throws NumberFormatException
     *             If string does not represent a valid decimal amount.
     * @param amount
     */
    private void setAmountAsString(String amount) throws NumberFormatException {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(ThreadLocale.getThreadLocale());
        char decimalSeparator = dfs.getDecimalSeparator();
        char groupSeparator = dfs.getGroupingSeparator();

        int decimalPlace = amount.indexOf(decimalSeparator);
        int scale = (decimalPlace >= 0) ? amount.length() - (decimalPlace + 1) : 0;

        StringBuffer integerVal = new StringBuffer();
        for (int i = 0; i < amount.length(); i++) {
            if (amount.charAt(i) != decimalSeparator && amount.charAt(i) != groupSeparator) {
                integerVal.append(amount.charAt(i));
            }
        }

        this.amount = new BigDecimal(new BigInteger(integerVal.toString()), scale);

        if (currency != null) {
            this.amount = this.amount.setScale(currency.getFractionDigits(), BigDecimal.ROUND_HALF_UP);
        }
    }

    public String getAmountAsString() {
        return new DecimalFormat(format).format(amount.doubleValue());
    }

    /**
     * Subtract another currency amount from this one, saving the result in this
     * one.
     *
     * @throws IllegalArgumentException
     *             If the currency types does not match.
     * @param that
     *            Currency amount to subtract
     * @return The result (instance of <i>this</i> to allow chaining e.g.
     *         c.add(2).subtract(4);)
     */
    public CurrencyAmount subtract(CurrencyAmount that) throws IllegalArgumentException {
        if (this.currency != null && !this.currency.equals(that.currency)) {
            throw new IllegalArgumentException("Currencies do not match, cannot subtract " + that.toFormattedString() + " from " + this.toFormattedString());
        }

        CurrencyAmount ret = new CurrencyAmount(amount.subtract(that.amount), that.currency);

        return ret;
    }

    /**
     * Add another currency amount to this one creating, and returning, a new
     * instance.
     *
     * @throws IllegalArgumentException
     *             If the currencies do not match.
     * @param that
     *            CurrencyAmount to add
     * @return The result (instance of <i>this</i> to allow chaining e.g.
     *         c.add(2).subtract(4);)
     */
    public CurrencyAmount add(CurrencyAmount that) throws IllegalArgumentException {
        if (this.currency != null && !this.currency.equals(that.currency)) {
            throw new IllegalArgumentException("Currencies do not match, cannot add " + that.toFormattedString() + " from " + this.toFormattedString());
        }

        CurrencyAmount ret = new CurrencyAmount(amount.add(that.amount), that.currency);

        return ret;
    }

    /**
     * Compare the value of 'that' currency amount with this. The two amounts
     * must represent the same currency.
     *
     * @param that
     *            Currency amount to compare with.
     * @throws IllegalArgumentException
     *             if the currencies do not match.
     * @return true if 'that' is less than this.
     */
    public boolean lessThan(CurrencyAmount that) {
        return lessThan(that.getAmount().doubleValue(), that.getCurrency());
    }

    /**
     * Compare the value of 'that' currency amount with this. The two amounts
     * must represent the same currency.
     *
     * @param that
     *            Currency amount to compare with.
     * @throws IllegalArgumentException
     *             if the currencies do not match.
     * @return true if 'that' is greater than than this.
     */
    public boolean greaterThan(CurrencyAmount that) {
        return greaterThan(that.getAmount().doubleValue(), that.getCurrency());
    }

    /**
     * Compare the value of <code>amount</code> in <code>currency</code> with
     * the amount represented by <code>this</code>. The two currencies must be
     * the same.
     *
     * @param amount
     *            Amount to compare against.
     * @param currency
     *            The currency <code>amount</code> is in.
     * @throws IllegalArgumentException
     *             if the currencies do not match.
     * @return true if the amount represented by <code>amount</code> and
     *         <code>currency</code> is less than than this.
     */
    public boolean lessThan(double amount, Currency currency) {
        if (this.currency.equals(currency)) {
            return (this.amount.doubleValue() < amount);
        } else {
            throw new IllegalArgumentException("Currency type missmatch: " + currency.toString() + " and " + this.currency.toString());
        }
    }

    /**
     * Compare the value of <code>amount</code> in <code>currency</code> with
     * the amount represented by <code>this</code>. The two currencies must be
     * the same.
     *
     * @param amount
     *            Amount to compare against.
     * @param currency
     *            The currency <code>amount</code> is in.
     * @throws IllegalArgumentException
     *             if the currencies do not match.
     * @return true if the amount represented by <code>amount</code> and
     *         <code>currency</code> is greater than than this.
     */
    public boolean greaterThan(double amount, Currency currency) {
        if (this.currency.equals(currency)) {
            return (this.amount.doubleValue() > amount);
        } else {
            throw new IllegalArgumentException("Currency type missmatch: " + currency.toString() + " and " + this.currency.toString());
        }
    }

    /**
     * Set the currency - private because it doesn't make sense for a currency
     * amount to have its currency change, unless some calculation is involved.
     * This calculation is beyone the scope of this value object.
     *
     * @param currency
     */
    private void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Set the currency based on a String representation of a currency.
     *
     * @param currency
     * @throws IndexOutOfBoundsException
     */
    private void setCurrencyAsString(String currency) throws IndexOutOfBoundsException {
        this.currency = Currency.valueOf(currency);
        if (amount != null) {
            amount = amount.setScale(this.currency.getFractionDigits(), BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Get the amount's currency
     *
     * @return currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Get the currency as a string.
     *
     * @return Currency as a string
     */
    public String getCurrencyAsString() {
        if (currency != null) {
            return currency.toString();
        } else {
            return null;
        }
    }

    /**
     * Get the String representation of this amount.
     *
     * @return
     */
    @Override
    public String toString() {
        return toFormattedString();
    }

    /**
     * Get the String representation of this amount.
     *
     * @return
     */
    public String getFormattedValue() {
        return toFormattedString();
    }

    /**
     * Get the value as a string in the format best suited for the current
     * thread's locale. The actual currency symbol used and the number format
     * depend on the locale.
     *
     * @return Formatted string
     */
    public String toFormattedString() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(ThreadLocale.getThreadLocale());
        fmt.setCurrency(java.util.Currency.getInstance(getCurrencyAsString()));
        fmt.setMaximumFractionDigits(currency.getFractionDigits());
        return fmt.format(amount);
    }

    /**
     * Apply a percentage to this currency amount.
     *
     * @param rate
     *            Percentage to apply.
     * @return The result (a new instance of CurrencyAmount)
     */
    public CurrencyAmount apply(Rate rate) {
        CurrencyAmount ret = new CurrencyAmount(rate.applyTo(amount, getCurrency().getFractionDigits(), BigDecimal.ROUND_HALF_UP), getCurrency());
        return ret;
    }

    /**
     * Clone this. Most types can rely on Type's clone method to handle this,
     * but because we want to keep setCurrency() private, we have to handle the
     * method here.
     *
     * @return Exact copy of this.
     * @throws CloneNotSupportedException
     *             If the clone fails
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new CurrencyAmount(this.amount, this.currency);
    }

    /**
     * Equality checker
     *
     * @param that
     *            Amount to check equality with
     * @return true if this has the same currency and value as that.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurrencyAmount) {
            CurrencyAmount that = (CurrencyAmount) obj;
            return this.currency.equals(that.currency) && this.amount.compareTo(that.amount) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return currency.hashCode() + amount.hashCode();
    }

    @Override
    public void mergeFrom(Mergable donor) {
        if (donor instanceof CurrencyAmount) {
            CurrencyAmount donorCurrencyAmount = (CurrencyAmount) donor;

            if (this.amount == null || this.amount.equals(BigDecimal.ZERO)) {
                this.amount = donorCurrencyAmount.amount;
            }

            if (this.currency == null) {
                this.currency = donorCurrencyAmount.currency;
            }
        }
    }
}
