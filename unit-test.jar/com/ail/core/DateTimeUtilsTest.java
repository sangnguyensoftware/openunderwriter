package com.ail.core;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;

public class DateTimeUtilsTest {

    @Test
    public void testUnitsBetween() {
        Date start = DateTimeUtils.LocalDateTimeToDateUTC(LocalDateTime.of(2016, 1, 5, 13, 45, 23));
        Date end = DateTimeUtils.LocalDateTimeToDateUTC(LocalDateTime.of(2018, 8, 17, 8, 22, 59));

        assert(DateTimeUtils.unitsBetween(start, end, ChronoUnit.YEARS) == 2);
        assert(DateTimeUtils.unitsBetween(start, end, ChronoUnit.MONTHS) == 31);
        assert(DateTimeUtils.unitsBetween(start, end, ChronoUnit.DAYS) == 954);
        assert(DateTimeUtils.unitsBetween(start, end, ChronoUnit.HOURS) == 22914);
        assert(DateTimeUtils.unitsBetween(start, end, ChronoUnit.MINUTES) == 1374877);
        assert(DateTimeUtils.unitsBetween(start, end, ChronoUnit.SECONDS) == 82492656);
    }

}
