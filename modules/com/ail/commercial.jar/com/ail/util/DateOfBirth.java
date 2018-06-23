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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Embeddable;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Cloneable;
import com.ail.core.Mergable;

@TypeDefinition
@Embeddable
public class DateOfBirth implements Serializable, Cloneable, Mergable {
    static final long serialVersionUID = 4525629482776785515L;

    private Date date;

    /**
     * Default Constructor.
     */
    public DateOfBirth() {
    }

    /**
     * Constructor
     * @param dob
     */
    public DateOfBirth(Date dob) {
        setDate(dob);
    }

    /**
     * Constructor to build a data of birth for a specific date.
     * @param year Four digit year
     * @param month Month number (0-11)
     * @param day Day in month
     */
    public DateOfBirth(int year, int month, int day) {
        Calendar c=Calendar.getInstance();
        c.set(year, month, day);
        setDate(c.getTime());
    }

    /**
     * Get the dob as a dat
     * @return The date of birth
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the date of birth
     * @param date The data of birth
     */
    public void setDate(Date date) {
        if (date!=null && date.after(new Date())) {
            throw new IllegalArgumentException("Attempt to create date of birth in the future.");
        }

        this.date = date;
    }

    /**
     * Fetch the date in the format defined for the default locale.
     * @return A string in the locale defined format.
     */
    public String getDateAsString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(getDate());
    }

    /**
     * Set the date from a string in the format defined for the default locale.
     * @param date The date as a String
     * @throws ParseException If the String does not represent a valid date in this locale.
     */
    public void setDateAsString(String date) throws ParseException {
        setDate(DateFormat.getDateInstance(DateFormat.SHORT).parse(date));
    }

	/**
	 * Fetch the date in the locale defined format.
	 * @param locale ThreadLocale to format date for.
	 * @return A string in the locale defined format.
	 */
	public String getDateAsString(Locale locale) {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(getDate());
	}

	/**
	 * Set the date from a string in the locale defined date format.
	 * @param date The date as a String
	 * @param locale The locale the date string is in
	 * @throws ParseException If the String does not represent a valid date in this locale.
	 */
	public void setDateAsString(String date, Locale locale) throws ParseException {
		setDate(DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(date));
	}

	/**
	 * Fetch the date as a String in a specified format.
	 * @param format The format of the date String required {@see SimpleDateFormat SimpleDateFormat}
	 * @return A string in the locale defined format.
	 */
	public String getDateAsString(String format) {
		DateFormat dateFormat=new SimpleDateFormat(format);
		return dateFormat.format(getDate());
	}

	/**
	 * Set the date from a string in the locale defined date format.
	 * @param date The date as a String
	 * @param locale The locale the date string is in
	 * @throws ParseException If the String does not represent a valid date in this locale.
	 */
	public void setDateAsString(String date, String format) throws ParseException {
		DateFormat dateFormat=new SimpleDateFormat(format);
		setDate(dateFormat.parse(date));
	}

    /**
     * Return the current age
     * @return Current age in years.
     */
    public int currentAge() {
        return ageAtDate(new Date());
    }

    /**
     * Return the age at a specific date.
     * @param then The date the age should be calculate for.
     * @return Current age in years.
     */
    public int ageAtDate(Date then){

        if (date.after(then)) {
            throw new IllegalArgumentException("Age less than zero");
        }

        // Use the default locale
        Calendar cal=Calendar.getInstance();

        // get the year of birth
        cal.setTime(this.date);
        int bornIn=cal.get(Calendar.YEAR);

        // get the year 'then' and return the difference
        cal.setTime(then);
        return cal.get(Calendar.YEAR)-bornIn;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new DateOfBirth(date);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
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
        DateOfBirth other = (DateOfBirth) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DateOfBirth [date=" + date + ", toString()=" + super.toString() + "]";
    }

    @Override
    public void mergeFrom(Mergable donor) {
        if (donor instanceof DateOfBirth) {
            DateOfBirth donorDateOfBirth = (DateOfBirth) donor;

            if (this.date == null) {
                this.date = donorDateOfBirth.date;
            }
        }
    }
}
