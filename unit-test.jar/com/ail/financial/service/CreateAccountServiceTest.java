package com.ail.financial.service;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.AccountType.CASH;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.math.BigDecimal;
import java.util.Date;

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
import com.ail.core.PreconditionException;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.Account.AccountBuilder;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.ledger.Ledger.LedgerBuilder;
import com.ail.financial.service.CreateAccountService.CreateAccountArgument;
import com.ail.party.Party;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, CreateAccountService.class, LedgerBuilder.class, AccountBuilder.class} )
public class CreateAccountServiceTest {
    private static final int OPENING_BALANCE_AMOUNT = 123;

    CreateAccountService sut;

    @Mock
    private CreateAccountArgument args;
    @Mock
    private CurrencyAmount openingBalance;
    @Mock
    private Party party;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Account account;
    @Mock
    private AccountBuilder accountBuilder;
    @Mock
    private Ledger ledger;
    @Mock
    private LedgerBuilder ledgerBuilder;


    private BigDecimal openingBalanceAmount=new BigDecimal(OPENING_BALANCE_AMOUNT);
    private Currency currency = GBP;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new CreateAccountService();
        sut.setArgs(args);

        mockStatic(CoreContext.class);

        doReturn(CASH).when(args).getTypeArg();
        doReturn(party).when(args).getPartyArg();
        doReturn(openingBalance).when(args).getOpeningBalanceArg();

        doReturn(currency).when(openingBalance).getCurrency();
        doReturn(openingBalanceAmount).when(openingBalance).getAmount();

        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);
        whenNew(AccountBuilder.class).withNoArguments().thenReturn(accountBuilder);
        whenNew(LedgerBuilder.class).withNoArguments().thenReturn(ledgerBuilder);

        doReturn(account).when(accountBuilder).build();
        doReturn(ledger).when(ledgerBuilder).build();

        doReturn(accountBuilder).when(accountBuilder).type(eq(CASH));
        doReturn(accountBuilder).when(accountBuilder).accountHolder(eq(party));
        doReturn(accountBuilder).when(accountBuilder).openingBalance(eq(openingBalance));
        doReturn(accountBuilder).when(accountBuilder).currency(eq((Currency)null));
        doReturn(accountBuilder).when(accountBuilder).description(eq((String)null));
        doReturn(accountBuilder).when(accountBuilder).name(eq((String)null));
        doReturn(accountBuilder).when(accountBuilder).openingDate(any(Date.class));

        doReturn(ledgerBuilder).when(ledgerBuilder).forAccount(eq(account));

        doReturn(account).when(coreProxy).create(eq(account));
        doReturn(ledger).when(coreProxy).create(eq(ledger));
    }

    @Test
    public void testHappyPathWithOpeningBalance() throws BaseException {
        sut.invoke();

        verify(accountBuilder).accountHolder(eq(party));
        verify(accountBuilder).openingBalance(eq(openingBalance));
        verify(ledgerBuilder).forAccount(eq(account));
        verify(coreProxy).create(account);
        verify(coreProxy).create(ledger);
    }

    @Test(expected=PreconditionException.class)
    public void willThrowExceptionWhenPartyIsNull() throws BaseException {
        doReturn(null).when(args).getPartyArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void willThrowExceptionIfBothOpeningBalanceAndCurrencyAreNull() throws BaseException {
        doReturn(null).when(args).getOpeningBalanceArg();
        doReturn(null).when(args).getCurrencyArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void willThrowExceptionIfBothOpeningBalanceAndCurrencyAreDefined() throws BaseException {
        doReturn(openingBalance).when(args).getOpeningBalanceArg();
        doReturn(currency).when(args).getCurrencyArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void willThrowExceptionIfTypeIsNull() throws BaseException {
        doReturn(null).when(args).getTypeArg();
        sut.invoke();
    }
}
