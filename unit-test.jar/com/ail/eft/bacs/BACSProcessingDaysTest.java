package com.ail.eft.bacs;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class BACSProcessingDaysTest {

    @Test
    public void testIsProcessingDayGBLocalDate() {
        LocalDate date = LocalDate.of(2003, 1, 1);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2017, 3, 17);
        assertTrue(BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2027, 3, 26);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2024, 4, 1);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2018, 5, 7);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2048, 5, 25);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2019, 6, 14);
        assertTrue(BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2020, 7, 13);
        assertTrue(BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2021, 8, 30);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2011, 12, 26);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2020, 12, 28);
        assertTrue(!BACSProcessingDays.isProcessingDayGB(date));

        date = LocalDate.of(2027, 12, 29);
        assertTrue(BACSProcessingDays.isProcessingDayGB(date));
    }

    @Test
    public void testIsProcessingDayNILocalDate() {
        LocalDate date = LocalDate.of(2003, 1, 1);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2017, 3, 17);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2027, 3, 26);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2024, 4, 1);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2018, 5, 7);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2048, 5, 25);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2019, 6, 14);
        assertTrue(BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2020, 7, 13);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2021, 8, 30);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2011, 12, 26);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2020, 12, 28);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));

        date = LocalDate.of(2027, 12, 29);
        assertTrue(!BACSProcessingDays.isProcessingDayNI(date));
    }

    @Test
    public void testIsNewYearsDay() {
        LocalDate date = LocalDate.of(2003, 1, 1);
        assertTrue(BACSProcessingDays.isNewYearsDay(date));

        date = LocalDate.of(2012, 1, 1);
        assertTrue(!BACSProcessingDays.isNewYearsDay(date));
        date = LocalDate.of(2012, 1, 2);
        assertTrue(BACSProcessingDays.isNewYearsDay(date));

        date = LocalDate.of(2039, 1, 1);
        assertTrue(!BACSProcessingDays.isNewYearsDay(date));
        date = LocalDate.of(2039, 1, 2);
        assertTrue(!BACSProcessingDays.isNewYearsDay(date));
        date = LocalDate.of(2039, 1, 3);
        assertTrue(BACSProcessingDays.isNewYearsDay(date));
    }

    @Test
    public void testIsStPatricksDay() {
        LocalDate date = LocalDate.of(2017, 3, 17);
        assertTrue(BACSProcessingDays.isStPatricksDay(date));

        date = LocalDate.of(2024, 3, 17);
        assertTrue(!BACSProcessingDays.isStPatricksDay(date));
        date = LocalDate.of(2024, 3, 18);
        assertTrue(BACSProcessingDays.isStPatricksDay(date));

        date = LocalDate.of(2040, 3, 17);
        assertTrue(!BACSProcessingDays.isStPatricksDay(date));
        date = LocalDate.of(2040, 3, 18);
        assertTrue(!BACSProcessingDays.isStPatricksDay(date));
        date = LocalDate.of(2040, 3, 19);
        assertTrue(BACSProcessingDays.isStPatricksDay(date));
    }

    @Test
    public void testIsGoodFriday() {
        LocalDate date = LocalDate.of(2006, 4, 14);
        assertTrue(BACSProcessingDays.isGoodFriday(date));

        date = LocalDate.of(2027, 3, 26);
        assertTrue(BACSProcessingDays.isGoodFriday(date));

        date = LocalDate.of(2048, 4, 3);
        assertTrue(BACSProcessingDays.isGoodFriday(date));
    }

    @Test
    public void testIsEasterMonday() {
        LocalDate date = LocalDate.of(2008, 3, 24);
        assertTrue(BACSProcessingDays.isEasterMonday(date));

        date = LocalDate.of(2024, 4, 1);
        assertTrue(BACSProcessingDays.isEasterMonday(date));

        date = LocalDate.of(2038, 4, 26);
        assertTrue(BACSProcessingDays.isEasterMonday(date));
    }

    @Test
    public void testIsEarlyMay() {
        LocalDate date = LocalDate.of(2000, 5, 1);
        assertTrue(BACSProcessingDays.isEarlyMay(date));

        date = LocalDate.of(2018, 5, 3);
        assertTrue(!BACSProcessingDays.isEarlyMay(date));
        date = LocalDate.of(2018, 5, 7);
        assertTrue(BACSProcessingDays.isEarlyMay(date));
    }

    @Test
    public void testIsSpring() {
        LocalDate date = LocalDate.of(2004, 5, 31);
        assertTrue(BACSProcessingDays.isSpring(date));

        date = LocalDate.of(2022, 5, 31);
        assertTrue(!BACSProcessingDays.isSpring(date));
        date = LocalDate.of(2022, 5, 30);
        assertTrue(BACSProcessingDays.isSpring(date));

        date = LocalDate.of(2048, 5, 31);
        assertTrue(!BACSProcessingDays.isSpring(date));
        date = LocalDate.of(2048, 5, 28);
        assertTrue(!BACSProcessingDays.isSpring(date));
        date = LocalDate.of(2048, 5, 25);
        assertTrue(BACSProcessingDays.isSpring(date));
    }

    @Test
    public void testIsBattleOfTheBoyne() {
        LocalDate date = LocalDate.of(2007, 7, 12);
        assertTrue(BACSProcessingDays.isBattleOfTheBoyne(date));

        date = LocalDate.of(2020, 7, 12);
        assertTrue(!BACSProcessingDays.isBattleOfTheBoyne(date));
        date = LocalDate.of(2020, 7, 13);
        assertTrue(BACSProcessingDays.isBattleOfTheBoyne(date));

        date = LocalDate.of(2042, 7, 12);
        assertTrue(!BACSProcessingDays.isBattleOfTheBoyne(date));
        date = LocalDate.of(2042, 7, 13);
        assertTrue(!BACSProcessingDays.isBattleOfTheBoyne(date));
        date = LocalDate.of(2042, 7, 14);
        assertTrue(BACSProcessingDays.isBattleOfTheBoyne(date));
    }

    @Test
    public void testIsSummer() {
        LocalDate date = LocalDate.of(2009, 8, 31);
        assertTrue(BACSProcessingDays.isSummer(date));

        date = LocalDate.of(2021, 8, 31);
        assertTrue(!BACSProcessingDays.isSummer(date));
        date = LocalDate.of(2021, 8, 30);
        assertTrue(BACSProcessingDays.isSummer(date));

        date = LocalDate.of(2036, 8, 31);
        assertTrue(!BACSProcessingDays.isSummer(date));
        date = LocalDate.of(2036, 8, 29);
        assertTrue(!BACSProcessingDays.isSummer(date));
        date = LocalDate.of(2036, 8, 25);
        assertTrue(BACSProcessingDays.isSummer(date));
    }

    @Test
    public void testIsChristmasDay() {
        LocalDate date = LocalDate.of(2003, 12, 25);
        assertTrue(BACSProcessingDays.isChristmasDay(date));

        date = LocalDate.of(2011, 12, 25);
        assertTrue(!BACSProcessingDays.isChristmasDay(date));
        date = LocalDate.of(2011, 12, 26);
        assertTrue(BACSProcessingDays.isChristmasDay(date));

        date = LocalDate.of(2038, 12, 25);
        assertTrue(!BACSProcessingDays.isChristmasDay(date));
        date = LocalDate.of(2038, 12, 26);
        assertTrue(!BACSProcessingDays.isChristmasDay(date));
        date = LocalDate.of(2038, 12, 27);
        assertTrue(BACSProcessingDays.isChristmasDay(date));
    }

    @Test
    public void testIsBoxingDay() {
        LocalDate date = LocalDate.of(2002, 12, 26);
        assertTrue(BACSProcessingDays.isBoxingDay(date));

        date = LocalDate.of(2020, 12, 26);
        assertTrue(!BACSProcessingDays.isBoxingDay(date));
        date = LocalDate.of(2020, 12, 28);
        assertTrue(BACSProcessingDays.isBoxingDay(date));

        date = LocalDate.of(2027, 12, 26);
        assertTrue(!BACSProcessingDays.isBoxingDay(date));
        date = LocalDate.of(2027, 12, 27);
        assertTrue(!BACSProcessingDays.isBoxingDay(date));
        date = LocalDate.of(2027, 12, 28);
        assertTrue(BACSProcessingDays.isBoxingDay(date));
    }

    @Test
    public void testIs27thDecember() {
        LocalDate date = LocalDate.of(2002, 12, 27);
        assertTrue(BACSProcessingDays.is27thDecember(date));

        date = LocalDate.of(2020, 12, 27);
        assertTrue(!BACSProcessingDays.is27thDecember(date));
        date = LocalDate.of(2020, 12, 29);
        assertTrue(BACSProcessingDays.is27thDecember(date));

        date = LocalDate.of(2027, 12, 27);
        assertTrue(!BACSProcessingDays.is27thDecember(date));
        date = LocalDate.of(2027, 12, 28);
        assertTrue(!BACSProcessingDays.is27thDecember(date));
        date = LocalDate.of(2027, 12, 29);
        assertTrue(BACSProcessingDays.is27thDecember(date));
    }

}
