/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

import com.ail.annotation.XPathFunctionDefinition;

/**
 * Provides utility methods to simply handle common date/time operations
 */
@XPathFunctionDefinition(namespace="t")
public class DateTimeUtils {

    /**
     * Convert an instance of java.util.Date to its corresponding java.time.LocalDate for the system default zone.
     * @param date Date to be converted
     * @return Result of conversion.
     */
    public static LocalDate dateToLocalDate(Date date) {
        return dateToLocalDate(date, ZoneId.systemDefault());
    }

    /**
     * Convert an instance of java.util.Date to its corresponding java.time.LocalDate for the UTC zone
     * @param date Date to be converted
     * @return Result of conversion.
     */
    public static LocalDate dateToLocalDateUTC(Date date) {
        return dateToLocalDate(date, ZoneOffset.UTC);
    }

    /**
     * Convert an instance of java.util.Date to its corresponding java.time.LocalDate for the specified zone
     * @param date Date to be converted
     * @param zoneId the ZoneId for the conversion
     * @return Result of conversion.
     */
    public static LocalDate dateToLocalDate(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    /**
     * Convert an instance of java.util.Date to its corresponding java.time.LocalDateTime for the UTC zone
     * @param date Date to be converted
     * @return Result of conversion.
     */
    public static LocalDateTime dateToLocalDateTimeUTC(Date date) {
        return date.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * Convert an instance of java.util.Date to its corresponding java.time.LocalDateTime for the specified zone
     * @param date Date to be converted
     * @param zoneId the ZoneId for the conversion
     * @return Result of conversion.
     */
    public static LocalDateTime dateToLocalDateTime(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    /**
     * Convert an instance of java.time.LocalDate to its corresponding java.util.Date for the system default zone.
     * @param date LocalDate to be converted
     * @return Result of conversion.
     */
    public static Date localDateToDate(LocalDate date) {
        return localDateToDate(date, ZoneId.systemDefault());
    }

    /**
     * Convert an instance of java.time.LocalDate to its corresponding java.util.Date for the UTC zone.
     * @param date LocalDate to be converted
     * @return Result of conversion.
     */
    public static Date localDateToDateUTC(LocalDate date) {
        return localDateToDate(date, ZoneOffset.UTC);
    }

    /**
     * Convert an instance of java.time.LocalDate to its corresponding java.util.Date for the specified zone.
     * @param date LocalDate to be converted
     * @param zoneId the ZoneId for the conversion
     * @return Result of conversion.
     */
    public static Date localDateToDate(LocalDate date, ZoneId zoneId) {
        return Date.from(date.atStartOfDay(zoneId).toInstant());
    }

    /**
     * Convert an instance of java.time.LocalDateTime to its corresponding java.util.Date for the system default zone.
     * @param dateTime LocalDateTime to be converted
     * @return Result of conversion.
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return LocalDateTimeToDate(dateTime, ZoneOffset.of(ZoneId.systemDefault().getId()));
    }

    /**
     * Convert an instance of java.time.LocalDateTime to its corresponding java.util.Date for the UTC zone.
     * @param dateTime LocalDateTime to be converted
     * @return Result of conversion.
     */
    public static Date LocalDateTimeToDateUTC(LocalDateTime dateTime) {
        return LocalDateTimeToDate(dateTime, ZoneOffset.UTC);
    }

    /**
     * Convert an instance of java.time.LocalDateTime to its corresponding java.util.Date for the specified zone.
     * @param dateTime LocalDateTime to be converted
     * @param zoneId the ZoneId for the conversion
     * @return Result of conversion.
     */
    public static Date LocalDateTimeToDate(LocalDateTime dateTime, ZoneOffset zoneId) {
        return Date.from(dateTime.toInstant(zoneId));
    }

    /**
     * Convert a long to date. The <code>date</code> param is taken to represent milliseconds since "the epoch", namely January 1, 1970, 00:00:00 GMT.
     * @param date Value to be converted
     * @return Date representing <code>date</code>.
     */
    public static Date longToDate(long date) {
        return new Date(date);
    }

    /**
     * Get the day of the year from a {@link Date}
     * @param date a date
     * @return the day of the year
     */
    public static int dayOfTheYear(Date date) {
        return dateToLocalDateUTC(date).getDayOfYear();
    }

    /**
     * Get the day of the semester from a {@link Date}.
     * @param date a date
     * @return the day of the year
     */
    public static int dayOfTheSemester(Date date) {
        LocalDate localDate = dateToLocalDateUTC(date);
        int dayOfYear = localDate.getDayOfYear();
        if (localDate.getMonthValue() < 7) {
            return dayOfYear;
        } else {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 6, 30)).getDays();
        }
    }

    /**
     * Get the day of the quarter from a {@link Date}.
     * @param date a date
     * @return the day of the year
     */
    public static int dayOfTheQuarter(Date date) {
        LocalDate localDate = dateToLocalDateUTC(date);
        int dayOfYear = localDate.getDayOfYear();
        int monthOfYear = localDate.getMonthValue();
        if (monthOfYear < 4) {
            return dayOfYear;
        } else if (monthOfYear < 7) {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 3, 31)).getDays();
        } else if (monthOfYear < 10) {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 6, 30)).getDays();
        } else {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 9, 30)).getDays();
        }
    }

    /**
     * Get the day of the bi-month from a {@link Date}.
     * @param date a date
     * @return the day of the year
     */
    public static int dayOfTheBiMonth(Date date) {
        LocalDate localDate = dateToLocalDateUTC(date);
        int dayOfYear = localDate.getDayOfYear();
        int monthOfYear = localDate.getMonthValue();
        if (monthOfYear < 3) {
            return dayOfYear;
        } else if (monthOfYear < 5) {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 3, 1).minusDays(1)).getDays();
        } else if (monthOfYear < 7) {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 4, 30)).getDays();
        } else if (monthOfYear < 9) {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 6, 30)).getDays();
        } else if (monthOfYear < 11) {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 8, 31)).getDays();
        } else {
            return dayOfYear - LocalDate.of(localDate.getYear(), 1, 1).until(LocalDate.of(localDate.getYear(), 10, 31)).getDays();
        }
    }

    /**
     * Get the day of the month from a {@link Date}
     * @param date a date
     * @return the day of the month
     */
    public static int dayOfTheMonth(Date date) {
        return dateToLocalDateUTC(date).getDayOfMonth();
    }

    /**
     * Get the day of the week from a {@link Date}
     * @param date a date
     * @return the day of the week where 1 == Monday and 7 == Sunday
     */
    public static int dayOfTheWeek(Date date) {
        return dateToLocalDateUTC(date).getDayOfWeek().getValue();
    }

    /**
     * Translate a Date into a String using the specified format.
     * @param date Date to be converted
     * @param pattern Format for to return date
     * @return <i>date</i> formatted as <i>pattern</i>
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * Return the specified Date with a period (signed) added.
     * @param date to be truncated.
     * @param ms milliseconds to add.
     * @param s seconds to add.
     * @param min minutes to add.
     * @param hr hours to add.
     * @param day days to add.
     * @param mon months to add.
     * @param yr years to add.
     * @return date with period added.
     */
    public static Date addDateTime(Date date, int ms, int s, int min, int hr, int day, int mon, int yr) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, yr);
        cal.add(Calendar.MONTH, mon);
        cal.add(Calendar.DAY_OF_MONTH, day);
        cal.add(Calendar.HOUR_OF_DAY, hr);
        cal.add(Calendar.MINUTE, min);
        cal.add(Calendar.SECOND, s);
        cal.add(MILLISECOND, ms);
        return cal.getTime();
    }

    /**
     * Return the current date & time.
     * @return date.
     */
    public static Date getDateTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     * Return the specified Date with time set to 00:00:00.000.
     * @param date to be truncated.
     * @return truncated date with time zeroed.
     */
    public static Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(HOUR_OF_DAY, 0);
        cal.set(MINUTE, 0);
        cal.set(SECOND, 0);
        cal.set(MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Works out the number of the specified TemporalUnit units between two dates.
     * @param start
     * @param end
     * @param unit
     * @return
     */
    public static long unitsBetween(Date start, Date end, TemporalUnit unit) {
        LocalDateTime startDateTime = dateToLocalDateTimeUTC(start);
        LocalDateTime endDateTime = dateToLocalDateTimeUTC(end);

        return unit.between(startDateTime, endDateTime);
    }
}
