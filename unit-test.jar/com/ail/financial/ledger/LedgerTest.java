package com.ail.financial.ledger;

import static com.ail.financial.Currency.GBP;
import static org.mockito.Mockito.doReturn;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Ledger.LedgerBuilder;

public class LedgerTest {

    @Mock
    private Account account;
    @Mock
    private CurrencyAmount openingBalance;
    @Mock
    private Date openingDate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(GBP).when(account).getCurrency();
        doReturn(GBP).when(openingBalance).getCurrency();
    }

    @Test(expected = LedgerValidationException.class)
    public void builderWithoutAnyArgsMustFail() throws LedgerValidationException {
        new LedgerBuilder().build();
    }

    @Test
    public void happyPathShortForm() throws LedgerValidationException {
        new LedgerBuilder().forAccount(account).build();
    }

    @Test
    public void happyPathLongForm() throws LedgerValidationException {
        new LedgerBuilder().name("Ledger name").description("Ledger description").forAccount(account).build();
    }
}
