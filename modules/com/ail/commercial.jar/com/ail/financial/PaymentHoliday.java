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

package com.ail.financial;

import static com.ail.core.language.I18N.i18n;
import static javax.persistence.CascadeType.ALL;

import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.HasNotes;
import com.ail.core.Note;
import com.ail.core.Type;

/**
 * A payment holiday defines an agreed period for which regular payments are suspended.
 * The period defined by a PaymentHoliday is inclusive of the start and end dates.
 */
@Audited
@Entity
@TypeDefinition
public class PaymentHoliday extends Type implements HasNotes {
    private static final long serialVersionUID = -2392281947246383188L;

    private static final int ONE_DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    /** The Date at which the premium holiday started. */
    @Column(name="phoStartDate")
    private Date startDate;

    /** The date when the premium holiday ended (may be null when the premium holiday is still in effect). */
    @Column(name="phoEndDate")
    private Date endDate;

    @OneToMany(cascade = ALL)
    private List<Note> note;

    public PaymentHoliday() {
    }

    public PaymentHoliday(Date startDate) {
        this.startDate = startDate;
    }

    public PaymentHoliday(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<>();
        }
        return note;
    }

    @Override
    public void setNote(List<Note> note) {
        this.note = note;
    }

    /**
     * Return the duration of the holiday in a locale specific format. Typically something like "4 years 1 month 2 days".
     * If the end date for this holiday is null an end date of today is used. If the two dates are identical, then "0 days" is returned.
     * @return Duration of this holiday as a String.
     */
    public Period durationPeriod() {
        if (getStartDate() == null && getEndDate() == null) {
            return null;
        }

        Date start = getStartDate();
        Date end = (getEndDate()!=null) ? (Date)getEndDate().clone() : new Date();

        end.setTime(end.getTime() + ONE_DAY_IN_MILLISECONDS);

        return Period.between(start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    /**
     * Return the duration of the holiday in a locale specific format. Typically something like "4 years 1 month 2 days".
     * If the end date for this holiday is null an end date of today is used. If the two dates are identical, then "0 days" is returned.
     * @return Duration of this holiday as a String.
     */
    public String duration() {

        Period duration = durationPeriod();

        if (duration == null) {
            return "";
        }

        Date end = (getEndDate()!=null) ? (Date)getEndDate().clone() : new Date();
        end.setTime(end.getTime() + ONE_DAY_IN_MILLISECONDS);

        StringBuffer ret = new StringBuffer();

        if (duration.getYears() > 0) {
            ret.append(duration.getYears());
            if (duration.getYears() == 1) {
                ret.append(' ').append(i18n("i18n_year"));
            } else {
                ret.append(' ').append(i18n("i18n_years"));
            }
        }

        if (duration.getMonths() > 0) {
            if (ret.length() != 0) {
                ret.append(' ');
            }

            ret.append(duration.getMonths());

            if (duration.getMonths() == 1) {
                ret.append(' ').append(i18n("i18n_month"));
            }
            else {
                ret.append(' ').append(i18n("i18n_months"));
            }
        }

        if (duration.getDays() > 0 || ret.length() == 0) {
            if (ret.length() != 0) {
                ret.append(' ');
            }

            ret.append(duration.getDays());

            if (duration.getDays() == 1) {
                ret.append(' ').append(i18n("i18n_day"));
            }
            else {
                ret.append(' ').append(i18n("i18n_days"));
            }
        }

        return ret.toString();
    }

    public boolean overlapsWith(PaymentHoliday that) {
        Date thisEndDate = (this.endDate != null) ? this.endDate : new Date();
        Date thatEndDate = (that.endDate != null) ? that.endDate : new Date();

        if (this.getStartDate() == null || that.getStartDate() == null) {
            return false;
        }

        if (this.getStartDate().equals(that.getStartDate()) || thisEndDate.equals(thatEndDate)) {
            return true;
        }

        if (that.getStartDate().after(this.getStartDate()) && that.getStartDate().before(thisEndDate)) {
            return true;
        }

        if (thatEndDate.after(this.getStartDate()) && thatEndDate.before(thisEndDate)) {
            return true;
        }

        return false;
    }

    /**
     * Check if this payment holiday is active now.
     * @return true if it's a holiday now, false if not.
     */
    public boolean isInPaymentHolidayNow() {
        Date today = new Date();

        // if start date is in the past
        if (this.getStartDate().before(today) && this.getEndDate().after(today)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((getAttribute() == null) ? 0 : getAttribute().hashCode());
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
        PaymentHoliday other = (PaymentHoliday) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (getAttribute() == null) {
            if (other.getAttribute() != null)
                return false;
        } else if (!getAttribute().equals(other.getAttribute()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentHoliday [startDate=" + startDate + ", endDate=" + endDate + "]";
    }
}
