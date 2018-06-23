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
package com.ail.eft.bacs;

import static com.ail.core.DateTimeUtils.dateToLocalDateUTC;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.TUESDAY;

import java.time.LocalDate;
import java.util.Date;
/**
 * Comprises utility methods to determine if a day is a BACS processing day or not. Nothing should be submitted
 * on a non-processing day or with a date that will fall on a processing day.
 * England, Wales, and Scotland all behave the same regarding BACS clearing despite there being differences in the local bank holidays,
 * while Northern Ireland has 3 extra non-processing days, for St Patrick's day, Battle of the Boyne (Orangemen's) day, and for some
 * reason the 27th of December (substituted if that falls on a weekend or the Christmas or Boxing day bank holiday).
 * You can check the days by doing a web search for 'BACS processing calendar' and the year you need.
 */
public class BACSProcessingDays {

    /**
     * Determines if the date is a Great Britain BACS processing day
     * @param date the date to check
     * @return true if the day is a processing day, else false
     */
    public static boolean isProcessingDayGB(Date date) {
        return isProcessingDayGB(dateToLocalDateUTC(date));
    }

    /**
     * Determines if the date is a Great Britain BACS processing day
     * @param date the date to check
     * @return true if the day is a processing day, else false
     */
    public static boolean isProcessingDayGB(LocalDate date) {
        return !isWeekend(date) && !isNewYearsDay(date) && !isGoodFriday(date) && !isEasterMonday(date) && !isEarlyMay(date)
                && !isSpring(date) && !isSummer(date) && !isChristmasDay(date) && !isBoxingDay(date);
    }

    /**
     * Determines if the date is a Northern Ireland BACS processing day
     * @param date the date to check
     * @return true if the day is a processing day, else false
     */
    public static boolean isProcessingDayNI(Date date) {
        return isProcessingDayNI(dateToLocalDateUTC(date));
    }

    /**
     * Determines if the date is a Northern Ireland BACS processing day
     * @param date the date to check
     * @return true if the day is a processing day, else false
     */
    public static boolean isProcessingDayNI(LocalDate date) {
        return !isWeekend(date) && !isNewYearsDay(date) && !isStPatricksDay(date) && !isGoodFriday(date) && !isEasterMonday(date) && !isEarlyMay(date)
                && !isSpring(date) && !isBattleOfTheBoyne(date) && !isSummer(date) && !isChristmasDay(date) && !isBoxingDay(date) && !is27thDecember(date);
    }

    /**
     * Determines if a given date is a weekend day
     * @param date
     * @return true if the given date is a Saturday or Sunday, else false
     */
    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;
    }

    /**
     * Determines if the date is equal to the New Year's Day bank holiday day
     * @param date the date to check
     * @return true if the day is the New Year's Day bank holiday, else false
     */
    public static boolean isNewYearsDay(LocalDate date) {
        LocalDate newYearsDay = LocalDate.of(date.getYear(), 1, 1);
        newYearsDay = substitute(newYearsDay);

        return date.equals(newYearsDay);
    }

    /**
     * Determines if the date is equal to the Saint Patrick's Day bank holiday day
     * @param date the date to check
     * @return true if the day is the Saint Patrick's Day bank holiday, else false
     */
    public static boolean isStPatricksDay(LocalDate date) {
        LocalDate newYearsDay = LocalDate.of(date.getYear(), 3, 17);
        newYearsDay = substitute(newYearsDay);

        return date.equals(newYearsDay);
    }

    /**
     * Determines if the date is equal to the Good Friday bank holiday day
     * @param date the date to check
     * @return true if the day is the Good Friday bank holiday, else false
     */
    public static boolean isGoodFriday(LocalDate date) {
        LocalDate easterSunday = getEasterSunday(date.getYear());
        LocalDate goodFriday = easterSunday.minusDays(2);

        return date.equals(goodFriday);
    }

    /**
     * Determines if the date is equal to the Easter Monday bank holiday day
     * @param date the date to check
     * @return true if the day is the Easter Monday bank holiday, else false
     */
    public static boolean isEasterMonday(LocalDate date) {
        LocalDate easterSunday = getEasterSunday(date.getYear());
        LocalDate easterMonday = easterSunday.plusDays(1);

        return date.equals(easterMonday);
    }

    /**
     * Return the date of Easter Sunday for a given year.
     * This is calculated using the Anonymous (also called Meeus/Jones/Butcher) Gregorian algorithm.
     * This code is only valid for years in the Gregorian calendar.
     * @param year the year to find Easter Sunday in
     * @return the date of Easter Sunday for the given year
     */
    public static LocalDate getEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int n = (h + l - 7 * m + 114) / 31;
        int p = (h + l - 7 * m + 114) % 31;

        return LocalDate.of(year, n, p + 1);
    }

    /**
     * Determines if the date is equal to the Early May bank holiday day
     * @param date the date to check
     * @return true if the day is the Early May bank holiday, else false
     */
    public static boolean isEarlyMay(LocalDate date) {
        LocalDate earlyMay = LocalDate.of(date.getYear(), 5, 1);
        while (earlyMay.getDayOfWeek() != MONDAY) {
            earlyMay = earlyMay.plusDays(1);
        }

        return date.equals(earlyMay);
    }

    /**
     * Determines if the date is equal to the Spring bank holiday day
     * @param date the date to check
     * @return true if the day is the Spring bank holiday, else false
     */
    public static boolean isSpring(LocalDate date) {
        LocalDate spring = LocalDate.of(date.getYear(), 5, 31);
        while (spring.getDayOfWeek() != MONDAY) {
            spring = spring.minusDays(1);
        }

        return date.equals(spring);
    }

    /**
     * Determines if the date is equal to the Battle of the Boyne (Orangemen's Day) bank holiday day
     * @param date the date to check
     * @return true if the day is the Battle of the Boyne (Orangemen's Day) bank holiday, else false
     */
    public static boolean isBattleOfTheBoyne(LocalDate date) {
        LocalDate newYearsDay = LocalDate.of(date.getYear(), 7, 12);
        newYearsDay = substitute(newYearsDay);

        return date.equals(newYearsDay);
    }

    /**
     * Determines if the date is equal to the Summer bank holiday day
     * @param date the date to check
     * @return true if the day is the Summer bank holiday, else false
     */
    public static boolean isSummer(LocalDate date) {
        LocalDate summer = LocalDate.of(date.getYear(), 8, 31);
        while (summer.getDayOfWeek() != MONDAY) {
            summer = summer.minusDays(1);
        }

        return date.equals(summer);
    }

    /**
     * Determines if the date is equal to the Christmas Day bank holiday day
     * @param date the date to check
     * @return true if the day is the Christmas Day bank holiday, else false
     */
    public static boolean isChristmasDay(LocalDate date) {
        LocalDate christmasDay = LocalDate.of(date.getYear(), 12, 25);
        christmasDay = substitute(christmasDay);

        return date.equals(christmasDay);
    }

    /**
     * Determines if the date is equal to the Boxing Day bank holiday day
     * @param date the date to check
     * @return true if the day is the Boxing Day bank holiday, else false
     */
    public static boolean isBoxingDay(LocalDate date) {
        LocalDate boxingDay = LocalDate.of(date.getYear(), 12, 26);
        if (boxingDay.getDayOfWeek() == SATURDAY || boxingDay.getDayOfWeek() == SUNDAY) {
            boxingDay = boxingDay.plusDays(2);
        }

        return date.equals(boxingDay);
    }

    /**
     * Determines if the date is equal to the 27th December BACS non-processing day
     * @param date the date to check
     * @return true if the day is the 27th December BACS non-processing, else false
     */
    public static boolean is27thDecember(LocalDate date) {
        LocalDate postBoxingDay = LocalDate.of(date.getYear(), 12, 27);
        if (postBoxingDay.getDayOfWeek() == SATURDAY || postBoxingDay.getDayOfWeek() == SUNDAY || postBoxingDay.getDayOfWeek() == MONDAY) {
            postBoxingDay = postBoxingDay.plusDays(2);
        } else if (postBoxingDay.getDayOfWeek() == TUESDAY) {
            postBoxingDay = postBoxingDay.plusDays(1);
        }

        return date.equals(postBoxingDay);
    }

    /**
     * If the date falls on a weekend then substitute it with the following Monday
     * @param date
     * @return the date param if it is a weekday, else the following Monday
     */
    private static LocalDate substitute(LocalDate date) {
        if (date.getDayOfWeek() == SATURDAY) {
            date = date.plusDays(2);
        } else if (date.getDayOfWeek() == SUNDAY) {
            date = date.plusDays(1);
        }

        return date;
    }
}
