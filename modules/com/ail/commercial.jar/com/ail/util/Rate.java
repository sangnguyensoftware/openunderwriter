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

package com.ail.util;

import java.math.BigDecimal;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * This class represents fractions or rates - with some special handling for common
 * fractions like percentages and permils.
 */
@TypeDefinition
public class Rate extends Type {
    static final long serialVersionUID = 7667424357379598058L;
    private BigDecimal nominator=null;
    private BigDecimal denominator=null;
    private String rate=null;

    /**
     * Constructor. This constructor is provided for use by frameworks (such as
     * castor) which require default constructors. Its use is discouraged in code
     * as the rate created is unusable until the setRate method has been called.
     */
    public Rate() {
    }

    /**
     * Constructor. This constructor accepts rates in a number of formats
     * see the setRate method for details.
     * @param rate
     */
    public Rate(String rate) {
        setRate(rate);
    }

    /**
     * Rates can be expressed in the following formats:
     * <ul>
     * <li>"n%" where 0 &lt;= n &lt;= 100. e.g. "10%", "25.4%".</li>
     * <li>"nPercent" where 0 &lt;= n &lt;= 100, "Percent" can be in any case. e.g. "10Percent", "5PERCENT", "25.5Percent"</li>
     * <li>"nPermil" where 0 &lt;= n &lt;= 1000, "Permil" can be in any case. e.g. "423Permil" , "874PERMIL", "223.4Permil"</li>
     * <li>"n/d" where 0 &lt;= n &lt;= d, and 0 &lt;= d. e.g. "3/20", "9.2/80"</li>
     * <li>A sting representing a value between 0 and 1 inclusive, e.g. "0.25" (25%)</li>
     * </ul>
     * @param rate The representation of a rate.
     * @throws IllegalArgumentException if a value is out of range, e.g. "102%".
     * @throws NumberFormatException if the rate strings format does not match one of those described above.
     */
    public void setRate(String rate) throws IllegalArgumentException, NumberFormatException {
        BigDecimal nominator=null;
        BigDecimal denominator=null;

        if (rate.endsWith("%")) {
            nominator=new BigDecimal(rate.substring(0, rate.length()-1));
            denominator=new BigDecimal(100);

            if (nominator.doubleValue()<0) {
                throw new IllegalArgumentException(nominator+" is not a valid percentage value");
            }
        }
        else if (rate.indexOf('/')!=-1) {
            int slash=rate.indexOf('/');

            nominator=new BigDecimal(rate.substring(0, slash));
            denominator=new BigDecimal(rate.substring(slash+1));

            if (nominator.doubleValue()<0) {
                throw new IllegalArgumentException("Nominator cannot be less than 0");
            }

            if (denominator.doubleValue()<0) {
                throw new IllegalArgumentException("Denominator cannot be less than 0");
            }
        }
        else if (rate.length()>8 && "percent".equalsIgnoreCase(rate.substring(rate.length()-7))) {
            nominator=new BigDecimal(rate.substring(0, rate.length()-7));
            denominator=new BigDecimal(100);
            if (nominator.doubleValue()<0) {
                throw new IllegalArgumentException(nominator+" is not a valid percentage value");
            }
        }
        else if (rate.length()>7 && "permil".equalsIgnoreCase(rate.substring(rate.length()-6))) {
            nominator=new BigDecimal(rate.substring(0, rate.length()-6));
            denominator=new BigDecimal(1000);
            if (nominator.doubleValue()<0 ) {
                throw new IllegalArgumentException(nominator+" is not a valid permil value");
            }
        }
        else {
            // Check that rate is a number
            if (new Double(rate).isNaN()) {
                throw new NumberFormatException("Rate format not recognised:"+rate);
            }

            nominator=new BigDecimal(rate);
            denominator=new BigDecimal(1);

            // Check that rate is >=0 and <=1
            if (BigDecimal.ZERO.compareTo(nominator) > 0) {
                throw new NumberFormatException("Rate format not recognised:"+rate);
            }
        }

        this.rate=rate;
        this.nominator=nominator;
        this.denominator=denominator;
    }

    /**
     * Get the rate as a string. The string returned is the same as that used
     * to initialise the Rate object (or passed to setRate).
     * @return The Rate string, or null if not defined.
     */
    public String getRate() {
        return rate;
    }

    /**
     * Get the rate nominator value.
     * @return
     */
    public BigDecimal getNominator() {
        return nominator;
    }

    /**
     * Apply this rate to a BigDecimal value. The resulting value will be rounded
     * using the rounding mode specified.
     * @see BigDecimal For details of rounding modes.
     * @param value The value to apply the rate to
     * @param scale Scale (decimal places) for the calculation.
     * @param roundingMode The rounding mode to use
     * @return The result of applying this rate to value.
     * @throws IllegalStateException If this rate has not be initialised.
     */
    public BigDecimal applyTo(BigDecimal value, int scale, int roundingMode) throws IllegalStateException {
        if (nominator==null || denominator==null) {
            throw new IllegalStateException("Rate not initialised");
        }
        return value.multiply(nominator).divide(denominator, scale, roundingMode);
    }

    /**
     * Apply this rate to a String value. The string is assumed to represent a
     * BigDecimal value. The rounding mode specified will be used during the
     * calculation.
     * @see BigDecimal For details of rounding modes.
     * @param value String in BigDecimal format
     * @param scale Scale (decimal places) for the calculation.
     * @param roundingMode Rounding to mode to apply
     * @return The result of applying the rate, as a string.
     * @throws IllegalStateException If this rate has not been initialised.
     * @throws NumberFormatException If "value" does not represent a valid BigDecimal.
     */
    public String applyTo(String value, int scale, int roundingMode) throws IllegalStateException, NumberFormatException {
        return applyTo(new BigDecimal(value), scale, roundingMode).toString();
    }

    /**
     * Apply this rate to a double value. The result will be rounded using the
     * specified rounding mode.
     * @param value Value to apply the rate to.
     * @param scale Scale (decimal places) for the calculation.
     * @param roundingMode Rounding mode to use.
     * @return The result of applying the rate.
     * @throws IllegalStateException If this rate has not been initialised.
     */
    public double applyTo(double value, int scale, int roundingMode) throws IllegalStateException {
        return applyTo(new BigDecimal(value), scale, roundingMode).doubleValue();
    }

    @Override
    public Object clone() {
        Rate clone=new Rate();

        if (rate!=null) {
            clone.rate=rate;
            clone.denominator=denominator;
            clone.nominator=nominator;
        }

        return clone;
    }
}
