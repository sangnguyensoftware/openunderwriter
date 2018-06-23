package com.ail.financial;

import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.CoreProxy;
import com.ail.core.XMLException;
import com.ail.core.configure.ConfigurationHandler;

public class MoneyProvisionExperiment {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
    private CoreProxy coreProxy;

    @Before
    public void setup() {
        coreProxy = new CoreProxy();
        coreProxy.resetConfigurations();
        coreProxy.setVersionEffectiveDateToNow();
        ConfigurationHandler.resetCache();
    }

    @Test
    public void testDirectDebitPaymentSearch() throws XMLException, ParseException {
        {
            coreProxy.openPersistenceSession();

            createMoneyProvision("01/01/2017", "01/12/2018", 5);
            createMoneyProvision("01/06/2017", "01/12/2017", 5);

            coreProxy.closePersistenceSession();
        }


        {
            List<?> pendingPayments;

            coreProxy.openPersistenceSession();

            pendingPayments = coreProxy.query("get.pending.directdebit.payments", 5, DATE_FORMATTER.parse("01/01/2017"));
            assertThat(pendingPayments, hasSize(1));

            pendingPayments = coreProxy.query("get.pending.directdebit.payments", 5, DATE_FORMATTER.parse("05/08/2017"));
            assertThat(pendingPayments, hasSize(2));

            pendingPayments = coreProxy.query("get.pending.directdebit.payments", 5, DATE_FORMATTER.parse("05/01/2018"));
            assertThat(pendingPayments, hasSize(1));

            pendingPayments = coreProxy.query("get.pending.directdebit.payments", 6, DATE_FORMATTER.parse("05/08/2017"));
            assertThat(pendingPayments, hasSize(0));

            coreProxy.closePersistenceSession();
        }
    }

    private void createMoneyProvision(String startDateString, String endDateString, int day) throws ParseException {
        Date startDate = DATE_FORMATTER.parse(startDateString);
        Date endDate = DATE_FORMATTER.parse(endDateString);

        String description = "Premium payment";
        PaymentMethod paymentMethod = new DirectDebit("09400911", "022132");
        CurrencyAmount amount = new CurrencyAmount("123", Currency.GBP);
        MoneyProvision mp = new MoneyProvision(amount, PREMIUM, paymentMethod, description);

        mp.setPaymentsStartDate(startDate);
        mp.setPaymentsEndDate(endDate);
        mp.setDay(day);

        coreProxy.create(mp);
    }
}
