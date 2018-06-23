package com.ail.jbpm;

import static org.junit.Assert.assertTrue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Test;

public class FunctionsTest {

    @Test
    public void testGetDateNow() {
        ZonedDateTime z1 = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
        String dateNow = Functions.getDateNowISO();
        ZonedDateTime z2 = ZonedDateTime.parse(dateNow, DateTimeFormatter.ISO_DATE_TIME);
        assertTrue(z1.getYear() == z2.getYear()
                    && z1.getMonth() == z2.getMonth()
                    && z1.getDayOfMonth() == z2.getDayOfMonth());
    }

    @Test
    public void testGetFutureDate() {
        ZonedDateTime z1 = ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).plusHours(3).plusMinutes(25).plusSeconds(47).withNano(0);
        String dateFuture = Functions.getFutureDateISO("25m1d47s3h");
        ZonedDateTime z2 = ZonedDateTime.parse(dateFuture, DateTimeFormatter.ISO_DATE_TIME);
        assertTrue(z2.getDayOfMonth() == z1.getDayOfMonth());
        assertTrue(z2.getHour() == z1.getHour());
        assertTrue(z2.getMinute() == z1.getMinute());
        assertTrue(z2.getSecond() == z1.getSecond());
    }

    @Test
    public void testGetFutureDateAt() {
        ZonedDateTime z1 = ZonedDateTime.now(ZoneOffset.UTC);
        String dateFuture = Functions.getFutureDateAtISO("12:00:00");
        ZonedDateTime z2 = ZonedDateTime.parse(dateFuture, DateTimeFormatter.ISO_DATE_TIME);
        assertTrue(z2.getDayOfYear() >= z1.getDayOfYear() || z2.getYear() > z1.getYear());
        assertTrue(z2.getHour() == 12);
        assertTrue(z2.getMinute() == 0);
        assertTrue(z2.getSecond() == 0);
    }

    @Test
    public void testLoopCounter() {
        int loopSize = 5;
        List<Integer> loopCounter = Functions.getLoopCounter(loopSize);
        assertTrue(loopCounter.size() == loopSize);
        assertTrue(loopCounter.get(loopSize - 1) == loopSize);
    }

}
