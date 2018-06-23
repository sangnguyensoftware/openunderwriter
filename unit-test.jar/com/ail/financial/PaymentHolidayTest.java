package com.ail.financial;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.language.I18N;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PaymentHoliday.class, I18N.class})
public class PaymentHolidayTest {
    private static final int ONE_DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(I18N.class);
        when(I18N.i18n("i18n_years")).thenReturn("years");
        when(I18N.i18n("i18n_year")).thenReturn("year");
        when(I18N.i18n("i18n_months")).thenReturn("months");
        when(I18N.i18n("i18n_month")).thenReturn("month");
        when(I18N.i18n("i18n_days")).thenReturn("days");
        when(I18N.i18n("i18n_day")).thenReturn("day");

    }

    @Test
    public void testSingularDayMonthYear() throws ParseException {
        Date startDate = simpleDateFormat.parse("2010-01-01");
        Date endDate = simpleDateFormat.parse("2011-02-02");

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate, endDate);

        assertThat(paymentHoliday.duration(), is("1 year 1 month 2 days"));
    }

    @Test
    public void testPluralDayMonthYear() throws ParseException {
        Date startDate = simpleDateFormat.parse("2010-01-01");
        Date endDate = simpleDateFormat.parse("2012-03-03");

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate, endDate);

        assertThat(paymentHoliday.duration(), is("2 years 2 months 3 days"));
    }

    @Test
    public void testZeroPeriod() throws ParseException {
        Date startDate = simpleDateFormat.parse("2010-01-01");
        Date endDate = simpleDateFormat.parse("2010-01-01");

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate, endDate);

        assertThat(paymentHoliday.duration(), is("1 day"));
    }

    @Test
    public void testNullStartAndEndDates() throws ParseException {
        PaymentHoliday paymentHoliday = new PaymentHoliday(null, null);

        assertThat(paymentHoliday.duration(), is(""));
    }

    @Test
    public void testDayAndMonthTruncation() throws ParseException {
        Date startDate = simpleDateFormat.parse("2010-01-02");
        Date endDate = simpleDateFormat.parse("2011-01-01");

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate, endDate);

        assertThat(paymentHoliday.duration(), is("1 year"));
    }

    @Test
    public void testDayTruncation() throws ParseException {
        Date startDate = simpleDateFormat.parse("2010-01-02");
        Date endDate = simpleDateFormat.parse("2011-02-01");

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate, endDate);

        assertThat(paymentHoliday.duration(), is("1 year 1 month"));
    }

    @Test
    public void testMonthTruncation() throws ParseException {
        Date startDate = simpleDateFormat.parse("2010-01-02");
        Date endDate = simpleDateFormat.parse("2011-01-04");

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate, endDate);

        assertThat(paymentHoliday.duration(), is("1 year 3 days"));
    }

    @Test
    public void testNullEndDateIsTakenAsToday() throws ParseException {
        Date startDate = new Date(System.currentTimeMillis() - ONE_DAY_IN_MILLISECONDS);

        PaymentHoliday paymentHoliday = new PaymentHoliday(startDate);

        // start and end are includes - so 2 days: yesterday and today.
        assertThat(paymentHoliday.duration(), is("2 days"));
    }

}
