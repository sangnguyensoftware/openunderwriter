package com.ail.financial.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.context.servlet.ServletRequestAdaptor;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.AccountingPeriod;
import com.ail.financial.ledger.AccountingPeriodStatus;
import com.ail.financial.ledger.Balance;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceCommand;
import com.ail.financial.service.CloseAccountingPeriodService.CloseAccountingPeriodArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, ServletRequestAdaptor.class})
public class CloseAccoutingPeriodServiceTest {

    private static final long DUMMY_SYSTEM_ID = 1234L;

    private static final String DUMMY_EXTERNAL_ID = "DUMMY_EXTERNAL_ID";

    private CloseAccountingPeriodService sut = null;

    @Mock
    CloseAccountingPeriodArgument args;
    @Mock
    CoreProxy coreProxy;
    @Mock
    AccountingPeriod accountingPeriod;
    @Mock
    CalculateAccountBalanceCommand calculateAccountBalanceCommand;
    @Mock
    Account account;
    List<Account> accounts = asList(account);
    @Mock
    CurrencyAmount accountBalance;
    @Captor
    ArgumentCaptor<Balance> balanceCaptor;
    @Mock
    Date accountingPeriodStartDate;


    @Before
    public void setup() throws PostconditionException {
        MockitoAnnotations.initMocks(this);

        sut = spy(new CloseAccountingPeriodService());
        sut.setArgs(args);

        mockStatic(CoreContext.class);

        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        doReturn(DUMMY_EXTERNAL_ID).when(args).getAccountingPeriodExternalSystemIdArg();
        doReturn(null).when(args).getAccountingPeriodSystemIdArg();
        doReturn(accountingPeriod).when(coreProxy).queryUnique(eq("get.accountingperiod.by.externalSystemId"), eq(DUMMY_EXTERNAL_ID));
        doReturn(AccountingPeriodStatus.OPEN).when(accountingPeriod).getStatus();
        doReturn(accountingPeriodStartDate).when(accountingPeriod).getStartDate();

        doNothing().when(sut).closeAccountingPeriodInSeparateTransaction(eq(accountingPeriod));

        doReturn(calculateAccountBalanceCommand).when(coreProxy).newCommand(eq(CalculateAccountBalanceCommand.class));
        doReturn(accounts).when(coreProxy).query(eq("get.accounts.by.transactions.in.accounting.period"), eq(accountingPeriod));
        doReturn(0L).when(coreProxy).queryUnique(eq("get.number.of.open.accountingperiods.before"), eq(accountingPeriodStartDate));

        doReturn(accountBalance).when(calculateAccountBalanceCommand).getBalanceRet();
    }

    @Test(expected = PreconditionException.class)
    public void testBothIDArgsCannotBeNull() throws BaseException {
        doReturn(null).when(args).getAccountingPeriodExternalSystemIdArg();
        doReturn(null).when(args).getAccountingPeriodSystemIdArg();

        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testBothIDArgsCannotBeDefined() throws BaseException {
        doReturn(DUMMY_EXTERNAL_ID).when(args).getAccountingPeriodExternalSystemIdArg();
        doReturn(DUMMY_SYSTEM_ID).when(args).getAccountingPeriodSystemIdArg();

        sut.invoke();
    }

    @Test()
    public void testThatSystemIdSearchIsPerformedWhenSystemIdIsPassed() throws BaseException {
        doReturn(null).when(args).getAccountingPeriodExternalSystemIdArg();
        doReturn(DUMMY_SYSTEM_ID).when(args).getAccountingPeriodSystemIdArg();
        doReturn(accountingPeriod).when(coreProxy).queryUnique(eq("get.accountingperiod.by.systemId"), eq(DUMMY_SYSTEM_ID));

        sut.invoke();

        verify(coreProxy).queryUnique(eq("get.accountingperiod.by.systemId"), eq(DUMMY_SYSTEM_ID));
    }

    @Test()
    public void testThatExternalSystemIdSearchIsPerformedWhenExternalSystemIdIsPassed() throws BaseException {
        sut.invoke();

        verify(coreProxy).queryUnique(eq("get.accountingperiod.by.externalSystemId"), eq(DUMMY_EXTERNAL_ID));
    }

    @Test(expected = PreconditionException.class)
    public void testUndefinedAccountingPeriodProducesAnException() throws BaseException {
        doReturn(null).when(coreProxy).queryUnique(eq("get.accountingperiod.by.externalSystemId"), eq(DUMMY_EXTERNAL_ID));

        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testPeriodAlreadyClosedProducesAnException() throws BaseException {
        doReturn(AccountingPeriodStatus.CLOSED).when(accountingPeriod).getStatus();

        sut.invoke();
    }

    @Test
    public void testThatClosePeriodIsCalled() throws BaseException {
        sut.invoke();

        verify(sut).closeAccountingPeriodInSeparateTransaction(eq(accountingPeriod));
    }

    @Test
    public void testThatBalanceIsPosted() throws BaseException {
        sut.invoke();

        verify(coreProxy).create(balanceCaptor.capture());
        Balance createdBalance = balanceCaptor.getValue();
        assertThat(createdBalance.getAmount(), is(accountBalance));
    }

}
