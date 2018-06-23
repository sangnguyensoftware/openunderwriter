package com.ail.financial.ledger;

import static com.ail.financial.Currency.USD;
import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;

public class JournalLineTest {
    @Mock
    private Ledger ledger;
    @Mock
    private Account account;
    @Mock
    private CurrencyAmount currencyAmount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(account).when(ledger).getAccount();
        doReturn(Currency.GBP).when(account).getCurrency();
        doReturn(Currency.GBP).when(currencyAmount).getCurrency();
        doReturn(BigDecimal.ZERO).when(currencyAmount).getAmount();
    }

    @Test(expected = LedgerValidationException.class)
    public void ledgerCannotBeNull() throws LedgerValidationException {
        new JournalLineBuilder().debit().ledger(null).by(currencyAmount).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void eitherDebitOrCreditMustBeUsed() throws LedgerValidationException {
        new JournalLineBuilder().ledger(ledger).by(currencyAmount).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void currencyAmountCannotBeNull() throws LedgerValidationException {
        new JournalLineBuilder().debit().ledger(ledger).by(null).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void missmatchingAccountCurrencyFailsValidation() throws LedgerValidationException {
        doReturn(USD).when(account).getCurrency();
        new JournalLineBuilder().debit().ledger(ledger).by(currencyAmount).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void amountMustBePositive() throws LedgerValidationException {
        doReturn(BigDecimal.valueOf(-1)).when(currencyAmount).getAmount();
        new JournalLineBuilder().debit().ledger(ledger).by(currencyAmount).build();
    }

    @Test
    public void happyPath() throws LedgerValidationException {
        new JournalLineBuilder().debit().ledger(ledger).by(currencyAmount).ofType(PREMIUM).build();
    }
}
