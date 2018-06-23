package com.ail.financial.service;

import static com.ail.financial.Currency.GBP;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.Balance;
import com.ail.financial.ledger.LedgerException;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceArgument;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreContext.class, CalculateAccountBalanceService.class })
public class CalculateAccountBalanceServiceTest {
    CalculateAccountBalanceService sut;

    @Mock
    private CalculateAccountBalanceArgument args;
    @Mock
    private Account requestedAccount;
    @Mock
    private CurrencyAmount returnedBalance;
    @Mock
    private Date requestedDate;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Criteria criteria;
    @Mock
    private Balance storedBalance;
    @Mock
    private Date storedBalanceDate;

    private List<?> balanceSearcResults;
    private CurrencyAmount storedBalanceAmount = new CurrencyAmount("100", GBP);
    private BigDecimal sumOfCredits = new BigDecimal("50");
    private BigDecimal sumOfDebits = new BigDecimal("25");
    private Currency accountCurrency = Currency.GBP;
    private CurrencyAmount expectedResult = new CurrencyAmount("125", GBP);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new CalculateAccountBalanceService();
        sut.setArgs(args);

        doReturn(requestedAccount).when(args).getAccountArg();
        doReturn(returnedBalance).when(args).getBalanceRet();
        doReturn(requestedDate).when(args).getDateArgRet();

        mockStatic(CoreContext.class);
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        doReturn(criteria).when(coreProxy).criteria(eq(Balance.class));
        doReturn(criteria).when(criteria).add(any(Criterion.class));
        doReturn(criteria).when(criteria).addOrder(any(Order.class));
        doReturn(criteria).when(criteria).setMaxResults(eq(1));
        doReturn(criteria).when(criteria).setReadOnly(eq(true));

        balanceSearcResults = Lists.newArrayList(storedBalance);
        doReturn(balanceSearcResults).when(criteria).list();

        doReturn(storedBalanceAmount).when(storedBalance).getAmount();
        doReturn(storedBalanceDate).when(storedBalance).getDate();

        doReturn(sumOfCredits).when(coreProxy).queryUnique(eq("sum.credits.for.account.between.two.dates"), eq(requestedAccount), eq(storedBalanceDate), eq(requestedDate));
        doReturn(sumOfDebits).when(coreProxy).queryUnique(eq("sum.debits.for.account.between.two.dates"), eq(requestedAccount), eq(storedBalanceDate), eq(requestedDate));

        doReturn(accountCurrency).when(requestedAccount).getCurrency();
    }

    @Test(expected = PreconditionException.class)
    public void accountArgCannotBeNull() throws BaseException {
        doReturn(null).when(args).getAccountArg();

        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void balanceRetCannotBeNull() throws BaseException {
        doReturn(null).when(args).getBalanceRet();

        sut.invoke();
    }

    @Test
    public void whenDateIsNullItIsSetToNow() throws BaseException {
        doReturn(null).doReturn(requestedDate).doReturn(requestedDate).when(args).getDateArgRet();
        sut.invoke();
        verify(args).setDateArgRet(any(Date.class));

    }

    @Test(expected = LedgerException.class)
    public void ledgerExceptionThrownWhenNoStoredBalanceIsFound() throws BaseException {
        doReturn(new ArrayList<Balance>()).when(criteria).list();
        sut.invoke();
    }

    @Test(expected = LedgerException.class)
    public void ledgerExceptionThrownWhenMoreThanOneStoredBalanceIsFound() throws BaseException {
        doReturn(Lists.newArrayList(storedBalance, storedBalance)).when(criteria).list();
        sut.invoke();
    }

    @Test
    public void happyPath() throws BaseException {
        sut.invoke();
        verify(args).setBalanceRet(eq(expectedResult));
    }

}
