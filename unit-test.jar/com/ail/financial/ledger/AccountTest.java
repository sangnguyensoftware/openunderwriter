package com.ail.financial.ledger;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.AccountType.CASH;
import static java.math.BigDecimal.ZERO;
import static org.mockito.Mockito.doReturn;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account.AccountBuilder;
import com.ail.party.Party;

public class AccountTest {
    @Mock
    private Party accountHolder;
    @Mock
    private Date openingDate;
    @Mock
    private CurrencyAmount openingBalance;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(GBP).when(openingBalance).getCurrency();
        doReturn(ZERO).when(openingBalance).getAmount();
    }

    @Test
    public void happyPathShortForm() throws LedgerValidationException {
        new AccountBuilder().currency(GBP).accountHolder(accountHolder).type(CASH).openingTodayWithZeroBalance().build();
    }

    @Test
    public void happyPathLongForm() throws LedgerValidationException {
        new AccountBuilder().currency(GBP).accountHolder(accountHolder).type(CASH).openingDate(openingDate).openingBalance(openingBalance).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void builderWithoutArgsFails() throws LedgerValidationException {
        new AccountBuilder().build();
    }

    @Test(expected = LedgerValidationException.class)
    public void builderWithoutCurrencyFails() throws LedgerValidationException {
        new AccountBuilder().accountHolder(accountHolder).type(CASH).openingTodayWithZeroBalance().build();
    }

    @Test(expected = LedgerValidationException.class)
    public void builderWithoutAccountHolderFails() throws LedgerValidationException {
        new AccountBuilder().currency(GBP).type(CASH).openingTodayWithZeroBalance().build();
    }

    @Test(expected = LedgerValidationException.class)
    public void builderWithoutTypeFails() throws LedgerValidationException {
        new AccountBuilder().currency(GBP).accountHolder(accountHolder).openingTodayWithZeroBalance().build();
    }

    @Test
    public void builderWithoutOpeningDateSucceeds() throws LedgerValidationException {
        new AccountBuilder().currency(GBP).accountHolder(accountHolder).type(CASH).openingBalance(openingBalance).build();
    }

    @Test
    public void builderWithoutOpeningBalanceSucceeds() throws LedgerValidationException {
        new AccountBuilder().currency(GBP).accountHolder(accountHolder).type(CASH).openingDate(openingDate).build();
    }
}
