package com.ail.financial;

import static com.ail.financial.FinancialFrequency.BIMONTHLY;
import static com.ail.financial.FinancialFrequency.BIWEEKLY;
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.QUARTERLY;
import static com.ail.financial.FinancialFrequency.SEMESTERLY;
import static com.ail.financial.FinancialFrequency.WEEKLY;
import static com.ail.financial.FinancialFrequency.YEARLY;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

public class FunctionsTest {

    @Test
    public void testNextPaymentDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -17);

        MoneyProvision moneyProvision = new MoneyProvision(0, null, PREMIUM, WEEKLY, null, cal.getTime(), null, null);
        Calendar nextPaymentCal = Calendar.getInstance();
        Date nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        Calendar testCal = Calendar.getInstance();
        testCal.add(Calendar.DAY_OF_YEAR, 4);
        assert(nextPaymentCal.get(Calendar.DAY_OF_WEEK) == cal.get(Calendar.DAY_OF_WEEK));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));

        moneyProvision = new MoneyProvision(0, null, PREMIUM, BIWEEKLY, null, cal.getTime(), null, null);
        nextPaymentCal = Calendar.getInstance();
        nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        testCal = Calendar.getInstance();
        testCal.add(Calendar.DAY_OF_YEAR, 11);
        assert(nextPaymentCal.get(Calendar.DAY_OF_WEEK) == cal.get(Calendar.DAY_OF_WEEK));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));


        moneyProvision = new MoneyProvision(0, null, PREMIUM, MONTHLY, null, cal.getTime(), null, null);
        nextPaymentCal = Calendar.getInstance();
        nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        testCal = Calendar.getInstance();
        testCal.add(Calendar.DATE, -17);
        testCal.add(Calendar.MONTH, 1);
        assert(nextPaymentCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));


        moneyProvision = new MoneyProvision(0, null, PREMIUM, BIMONTHLY, null, cal.getTime(), null, null);
        nextPaymentCal = Calendar.getInstance();
        nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        testCal = Calendar.getInstance();
        testCal.add(Calendar.DATE, -17);
        testCal.add(Calendar.MONTH, 2);
        assert(nextPaymentCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));


        moneyProvision = new MoneyProvision(0, null, PREMIUM, QUARTERLY, null, cal.getTime(), null, null);
        nextPaymentCal = Calendar.getInstance();
        nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        testCal = Calendar.getInstance();
        testCal.add(Calendar.DATE, -17);
        testCal.add(Calendar.MONTH, 3);
        assert(nextPaymentCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));


        moneyProvision = new MoneyProvision(0, null, PREMIUM, SEMESTERLY, null, cal.getTime(), null, null);
        nextPaymentCal = Calendar.getInstance();
        nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        testCal = Calendar.getInstance();
        testCal.add(Calendar.DATE, -17);
        testCal.add(Calendar.MONTH, 6);
        assert(nextPaymentCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));


        moneyProvision = new MoneyProvision(0, null, PREMIUM, YEARLY, null, cal.getTime(), null, null);
        nextPaymentCal = Calendar.getInstance();
        nextPaymentDate = Functions.nextPaymentDate(moneyProvision);
        nextPaymentCal.setTime(nextPaymentDate);
        testCal = Calendar.getInstance();
        testCal.add(Calendar.DATE, -17);
        testCal.add(Calendar.YEAR, 1);
        assert(nextPaymentCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        assert(nextPaymentCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR));
        assert(nextPaymentCal.get(Calendar.YEAR) == (cal.get(Calendar.YEAR) + 1));
    }

    @Test
    public void testLastPayment() {
        List<PaymentRecord> paymentHistory = new ArrayList<>();
        PaymentRecord actualLatestPaymentRecord = new PaymentRecord(null, null,  null,  null, new GregorianCalendar(2017, 6, 21).getTime());
        paymentHistory.add(new PaymentRecord(null, null,  null,  null, new GregorianCalendar(2017, 4, 20).getTime()));
        paymentHistory.add(actualLatestPaymentRecord);
        paymentHistory.add(new PaymentRecord(null, null,  null,  null, new GregorianCalendar(2017, 3, 23).getTime()));
        paymentHistory.add(new PaymentRecord(null, null,  null,  null, new GregorianCalendar(2017, 5, 22).getTime()));

        PaymentRecord potentialLastPaymentRecord = Functions.lastPayment(paymentHistory);
        assert(potentialLastPaymentRecord.equals(actualLatestPaymentRecord));

        paymentHistory = new ArrayList<>();
        potentialLastPaymentRecord = Functions.lastPayment(paymentHistory);
        assert(potentialLastPaymentRecord == null);

        paymentHistory = null;
        potentialLastPaymentRecord = Functions.lastPayment(paymentHistory);
        assert(potentialLastPaymentRecord == null);
    }

}
