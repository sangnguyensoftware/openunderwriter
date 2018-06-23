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

import static java.time.Period.ofMonths;
import static java.time.Period.ofWeeks;
import static java.time.Period.ofYears;

import java.time.Period;
import java.util.Optional;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

/**
 * Type safe enumeration representing constant values for FinancialFrequency.
 */
public enum FinancialFrequency implements TypeEnum {
    UNDEFINED("?"),
    WEEKLY("Weekly", ofWeeks(1)),
    BIWEEKLY("BiWeekly", ofWeeks(2)),
    MONTHLY("Monthly", ofMonths(1)),
    BIMONTHLY("Bi-Monthly", ofMonths(2)),
    QUARTERLY("Quarterly", ofMonths(3)),
    SEMESTERLY("Semesterly", ofMonths(6)), // Every half year
    YEARLY("Yearly", ofYears(1)),
    ONE_TIME("One time"); // Once and once only.

    private String longName;
    private Optional<Period> period = Optional.empty();

    FinancialFrequency() {
        this.longName = name();
    }

    FinancialFrequency(String longName) {
        this.longName = longName;
    }

    FinancialFrequency(String longName, Period period) {
        this.longName = longName;
        this.period = Optional.of(period);
    }

    @Override
    public String getLongName() {
        return longName;
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    @Override
    public String longName() {
        return longName;
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static FinancialFrequency forName(String name) {
        return (FinancialFrequency)Functions.enumForName(name, values());
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    public Optional<Period> toPeriod() {
        return period;
    }
}
