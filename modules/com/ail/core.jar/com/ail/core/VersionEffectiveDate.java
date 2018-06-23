/* Copyright Applied Industrial Logic Limited 2003. All rights reserved. */
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

package com.ail.core;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Embeddable;

/**
 * VersionEffectiveDates are used to select the configuration a CoreUser should be using.
 * Configurations are stored with date stamps, when a client (CoreUser) requests some
 * information from configuration their VersionEffectiveDate is used to select the
 * appropriate version from those stored.<p>
 * VersionEffectiveDates are always based on the UTC timezone.
 **/
@Embeddable
public class VersionEffectiveDate implements Serializable, Cloneable, Mergable  {
    private Date ved=null;

    /**
     * Construct a VersionEffectiveDate representing the time now.
     */
    public VersionEffectiveDate() {
        Calendar now=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        ved=now.getTime();
    }

    /**
     * Create a new instance with the same property values as an existing instance.
     * @param that Instance to copy property values from.
     */
    public VersionEffectiveDate(VersionEffectiveDate that) {
        ved = that.ved;
    }

    /**
     * Create a VersionEffectiveDate based on another Date object. The
     * date supplied is used "as is" - no attempt is made to adjust it
     * to UTC.
     * @param ved Date to base this VersionEffectiveDate on.
     */
    public VersionEffectiveDate(Date ved) {
        if (ved==null) {
            throw new IllegalArgumentException("VersionEffectiveDate cannot be null");
        }
        this.ved=ved;
    }

    /**
     * Create a VersionEffectiveDate based on a <code>time</code>. Where <code>time</code>
     * is milliseconds since epoc.
     * @param time
     */
    public VersionEffectiveDate(long time) {
        ved=new Date(time);
    }

    /**
     * Tests if this date is before the specified date
     * @param when A date
     * @return <code>true</code> if and only if the instant of time represented by this
     * <code>VersionEffectiveDate</code> object is strictly earlier than the instant represented
     * by <code>when</code>; <code>false</code> otherwise.
     */
    public boolean before(VersionEffectiveDate when) {
        return this.ved.before(when.ved);
    }

    /**
     * Tests if this date is after the specified date.
     * @param when A date
     * @return <code>true</code> if and only if the instant represented by this
     * <code>VersionEffectiveDate</code> object is strictly later than the instant
     * represented by <code>when</code>; <code>false</code> otherwise.
     */
    public boolean after(VersionEffectiveDate when) {
        return this.ved.after(when.ved);
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this VersionEffectiveDate object.
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this VersionEffectiveDate.
     */
    public long getTime() {
        return ved.getTime();
    }

    /**
     * Set the time property. This represents the number of milliseconds since epoc.
     * @param time milliseconds since epoc
     */
    public void setTime(long time) {
        ved=new Date(time);
    }

    /**
     * Get the Date object represented by this VersionEffectiveDate.
     * @return Date object.
     */
    public Date getDate() {
        return ved;
    }

    /**
     * Compares two dates for equality. The result is true if and only if the argument is
     * not null and is a VersionEffectiveDate object that represents the same point in time, to the
     * millisecond, as this object.<p>
     * Thus, two VersionEffectiveDate objects are equal if and only if the getTime method returns the
     * same long value for both.
     * @param o The object to compare with.
     * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o==null) {
            return false;
        }

        if (!(o instanceof VersionEffectiveDate)) {
            return false;
        }

        VersionEffectiveDate that=(VersionEffectiveDate)o;

        return (that.ved.equals(this.ved));
    }

    @Override
    public int hashCode() {
        return (int)ved.getTime();
    }

    /**
     * @see java.util.Date#toString
     * @return a string representation of this VersionEffectiveDate
     */
    @Override
    public String toString() {
        return ved.toString();
    }

    /**
     * Compare this VEDs with another.
     * @param that VED to compare with.
     * @return the value 0 if the argument Date is equal to
     *         this Date; a value less than 0 if this Date
     *         is before the Date argument; and a value greater than
     *         0 if this Date is after the Date argument.
     */
    public int compareTo(VersionEffectiveDate that) {
        return ved.compareTo(that.ved);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new VersionEffectiveDate(this.ved);
    }

    @Override
    public void mergeFrom(Mergable donor) {
        if (donor instanceof VersionEffectiveDate) {
            VersionEffectiveDate donorVed = (VersionEffectiveDate) donor;

            if (this.ved == null) {
                this.ved = donorVed.ved;
            }
        }
    }
}
