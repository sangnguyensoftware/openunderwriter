package com.ail.financial.service;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.FinancialFrequency;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionPurpose;
import com.ail.financial.service.ActualizeMoneyProvisionsService.ActualizeMoneyProvisionsArgument;
import com.google.common.collect.Lists;

public class ActualizeMoneyProvisionsServiceTest {

    private ActualizeMoneyProvisionsService sut;

    @Mock
    private ActualizeMoneyProvisionsArgument args;
    @Mock
    private List<MoneyProvision> normalizedMoneyProvisions;
    @Mock
    private List<MoneyProvision> actializedMoneyProvisions;

    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);

        sut = new ActualizeMoneyProvisionsService();
        sut.setArgs(args);

        doReturn(date("01/01/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/06/2010")).when(args).getPeriodEndDateArg();
        doReturn(normalizedMoneyProvisions).when(args).getNormalizedMoneyProvisionsArg();
        doReturn(actializedMoneyProvisions).when(args).getActualizedMoneyProvisionsRet();
    }

    @Test(expected = PreconditionException.class)
    public void nullPeriodStartDateIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getPeriodStartDateArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullNormalizedListArgIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getNormalizedMoneyProvisionsArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void periodStartDateMustBeBeforePeriodEndDate() throws BaseException, ParseException {
        doReturn(date("01/06/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/01/2010")).when(args).getPeriodEndDateArg();
        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void nullActualizedListArgCannotBeReturned() throws BaseException {
        doReturn(null).when(args).getActualizedMoneyProvisionsRet();
        doReturn(true).when(normalizedMoneyProvisions).isEmpty();
        sut.invoke();
    }

    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void anEmptyNormalizedListResultsInAnEmptyActualizedList() throws BaseException {
        doReturn(true).when(normalizedMoneyProvisions).isEmpty();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());
        assertThat(0, is(captor.getValue().size()));
    }

    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void happyPathOneMonthlyActualizes() throws ParseException, BaseException {
        doReturn(date("01/01/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/06/2010")).when(args).getPeriodEndDateArg();
        doReturn(Lists.newArrayList(
                moneyProvision("01/01/2010", "01/06/2010", MONTHLY, 1, 50)
        )).when(args).getNormalizedMoneyProvisionsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());

        List<MoneyProvision> results = captor.getValue();
        assertThat(results.size(), is(6));

        assertThat(results.get(0).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(0).getFrequency(), is(ONE_TIME));
        assertThat(results.get(0).getPaymentsStartDate(), is(date("01/01/2010")));

        assertThat(results.get(1).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(1).getFrequency(), is(ONE_TIME));
        assertThat(results.get(1).getPaymentsStartDate(), is(date("01/02/2010")));

        assertThat(results.get(2).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(2).getFrequency(), is(ONE_TIME));
        assertThat(results.get(2).getPaymentsStartDate(), is(date("01/03/2010")));

        assertThat(results.get(3).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(3).getFrequency(), is(ONE_TIME));
        assertThat(results.get(3).getPaymentsStartDate(), is(date("01/04/2010")));

        assertThat(results.get(4).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(4).getFrequency(), is(ONE_TIME));
        assertThat(results.get(4).getPaymentsStartDate(), is(date("01/05/2010")));

        assertThat(results.get(5).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(5).getFrequency(), is(ONE_TIME));
        assertThat(results.get(5).getPaymentsStartDate(), is(date("01/06/2010")));
    }

    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void happyPathOneMonthlyWithMidPeriodEndActualizes() throws ParseException, BaseException {
        doReturn(date("01/01/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/06/2010")).when(args).getPeriodEndDateArg();
        doReturn(Lists.newArrayList(
                moneyProvision("01/01/2010", "01/03/2010", MONTHLY, 1, 50)
        )).when(args).getNormalizedMoneyProvisionsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());

        List<MoneyProvision> results = captor.getValue();
        assertThat(results.size(), is(3));

        assertThat(results.get(0).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(0).getFrequency(), is(ONE_TIME));
        assertThat(results.get(0).getPaymentsStartDate(), is(date("01/01/2010")));

        assertThat(results.get(1).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(1).getFrequency(), is(ONE_TIME));
        assertThat(results.get(1).getPaymentsStartDate(), is(date("01/02/2010")));

        assertThat(results.get(2).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(2).getFrequency(), is(ONE_TIME));
        assertThat(results.get(2).getPaymentsStartDate(), is(date("01/03/2010")));
    }

    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void happyPathOneMonthlyWithMidPeriodStartAndEndActualizes() throws ParseException, BaseException {
        doReturn(date("01/01/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/06/2010")).when(args).getPeriodEndDateArg();
        doReturn(Lists.newArrayList(
                moneyProvision("01/02/2010", "01/03/2010", MONTHLY, 1, 50)
        )).when(args).getNormalizedMoneyProvisionsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());

        List<MoneyProvision> results = captor.getValue();
        assertThat(results.size(), is(2));

        assertThat(results.get(0).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(0).getFrequency(), is(ONE_TIME));
        assertThat(results.get(0).getPaymentsStartDate(), is(date("01/02/2010")));

        assertThat(results.get(1).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(1).getFrequency(), is(ONE_TIME));
        assertThat(results.get(1).getPaymentsStartDate(), is(date("01/03/2010")));
    }

    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void happyPathOneMonthlyWithMidPeriodStartActualizes() throws ParseException, BaseException {
        doReturn(date("01/01/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/06/2010")).when(args).getPeriodEndDateArg();
        doReturn(Lists.newArrayList(
                moneyProvision("01/04/2010", "01/06/2010", MONTHLY, 1, 50)
        )).when(args).getNormalizedMoneyProvisionsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());

        List<MoneyProvision> results = captor.getValue();
        assertThat(results.size(), is(3));

        assertThat(results.get(0).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(0).getFrequency(), is(ONE_TIME));
        assertThat(results.get(0).getPaymentsStartDate(), is(date("01/04/2010")));

        assertThat(results.get(1).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(1).getFrequency(), is(ONE_TIME));
        assertThat(results.get(1).getPaymentsStartDate(), is(date("01/05/2010")));

        assertThat(results.get(2).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(2).getFrequency(), is(ONE_TIME));
        assertThat(results.get(2).getPaymentsStartDate(), is(date("01/06/2010")));
    }

    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void happyPathPaymentPeriodExceedsSelectedPeriodActualizes() throws ParseException, BaseException {
        doReturn(date("01/03/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/04/2010")).when(args).getPeriodEndDateArg();
        doReturn(Lists.newArrayList(
                moneyProvision("01/01/2010", "01/06/2010", MONTHLY, 1, 50)
        )).when(args).getNormalizedMoneyProvisionsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());

        List<MoneyProvision> results = captor.getValue();
        assertThat(results.size(), is(2));

        assertThat(results.get(0).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(0).getFrequency(), is(ONE_TIME));
        assertThat(results.get(0).getPaymentsStartDate(), is(date("01/03/2010")));

        assertThat(results.get(1).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(1).getFrequency(), is(ONE_TIME));
        assertThat(results.get(1).getPaymentsStartDate(), is(date("01/04/2010")));
    }


    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void happyPathMoneyProvisionRollup() throws ParseException, BaseException {
        doReturn(date("01/01/2010")).when(args).getPeriodStartDateArg();
        doReturn(date("01/03/2010")).when(args).getPeriodEndDateArg();
        doReturn(Lists.newArrayList(
                moneyProvision("01/01/2010", "01/06/2010", MONTHLY, 1, 50),
                moneyProvision("01/02/2010", "01/02/2010", ONE_TIME, 0, 100)
        )).when(args).getNormalizedMoneyProvisionsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setActualizedMoneyProvisionsRet(captor.capture());

        List<MoneyProvision> results = captor.getValue();
        assertThat(results.size(), is(3));

        assertThat(results.get(0).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(0).getFrequency(), is(ONE_TIME));
        assertThat(results.get(0).getPaymentsStartDate(), is(date("01/01/2010")));
        assertThat(results.get(0).getDescription(), containsString("DirectDebit"));
        assertThat(results.get(0).getDescription(), containsString("50"));
        assertThat(results.get(0).getDescription(), containsString("Dummy Money Provision"));

        assertThat(results.get(1).getAmount(), is(new CurrencyAmount(150, GBP)));
        assertThat(results.get(1).getFrequency(), is(ONE_TIME));
        assertThat(results.get(1).getPaymentsStartDate(), is(date("01/02/2010")));
        assertThat(results.get(1).getDescription(), containsString("DirectDebit"));
        assertThat(results.get(1).getDescription(), containsString("50"));
        assertThat(results.get(1).getDescription(), containsString("100"));
        assertThat(results.get(0).getDescription(), containsString("Dummy Money Provision"));

        assertThat(results.get(2).getAmount(), is(new CurrencyAmount(50, GBP)));
        assertThat(results.get(2).getFrequency(), is(ONE_TIME));
        assertThat(results.get(2).getPaymentsStartDate(), is(date("01/03/2010")));
        assertThat(results.get(0).getDescription(), containsString("50"));
        assertThat(results.get(0).getDescription(), containsString("Dummy Money Provision"));
    }

    private MoneyProvision moneyProvision(String start, String end, FinancialFrequency freq, int day, double amount) throws ParseException {
        MoneyProvision mp = new MoneyProvision(
            1,
            new CurrencyAmount(amount, GBP),
            MoneyProvisionPurpose.PREMIUM,
            freq,
            new DirectDebit("010101010", "020202"),
            date(start),
            date(end),
            "Dummy Money Provision"
        );

        mp.setDay(day);

        return mp;
    }

    private Date date(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").parse(date);
    }
}
