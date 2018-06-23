package com.ail.financial.ledger;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.JournalType.PREMIUM_DUE;
import static com.ail.financial.ledger.TransactionType.CREDIT;
import static com.ail.financial.ledger.TransactionType.DEBIT;
import static org.mockito.Mockito.doReturn;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Journal.JournalBuilder;

public class JournalTest {

    @Mock
    Account account1;
    @Mock
    Account account2;

    @Mock
    Ledger ledger1;
    @Mock
    Ledger ledger2;

    @Mock
    JournalLine journalLine1;
    @Mock
    JournalLine journalLine2;

    @Mock
    Date openingDateForAccount1;
    @Mock
    Date openingDateForAccount2;

    CurrencyAmount currencyAmount1;
    CurrencyAmount currencyAmount2;

    @Mock
    Date transactionDate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(CREDIT).when(journalLine1).getTransactionType();
        doReturn(DEBIT).when(journalLine2).getTransactionType();

        doReturn(ledger1).when(journalLine1).getLedger();
        doReturn(ledger2).when(journalLine2).getLedger();

        doReturn(GBP).when(account1).getCurrency();
        doReturn(GBP).when(account2).getCurrency();

        doReturn(account1).when(ledger1).getAccount();
        doReturn(account2).when(ledger2).getAccount();

        doReturn(openingDateForAccount1).when(account1).getOpeningDate();
        doReturn(openingDateForAccount2).when(account2).getOpeningDate();

        doReturn(true).when(transactionDate).after(openingDateForAccount1);
        doReturn(true).when(transactionDate).after(openingDateForAccount2);

        currencyAmount1 = new CurrencyAmount(100, GBP);
        currencyAmount2 = new CurrencyAmount(100, GBP);

        doReturn(currencyAmount1).when(journalLine1).getAmount();
        doReturn(currencyAmount2).when(journalLine2).getAmount();
    }

    @Test(expected = LedgerValidationException.class)
    public void validateBuilderFailsWithNullArguments() throws LedgerValidationException {
        new JournalBuilder().build();
    }

    @Test(expected = LedgerValidationException.class)
    public void validateMiniumumOfTwoJournalLinesInAJournal() throws LedgerValidationException {
        new JournalBuilder().withTransactionDate(transactionDate).with(journalLine1).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void validateBuilderWithNullDateFails() throws LedgerValidationException {
        new JournalBuilder().withTransactionDate(null).with(journalLine1, journalLine2).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void mustHaveJournalLines() throws LedgerValidationException {
        new JournalBuilder().withTransactionDate(transactionDate).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void mustBalance() throws LedgerValidationException {
        currencyAmount1 = new CurrencyAmount(100, GBP);
        currencyAmount2 = new CurrencyAmount(200, GBP);

        doReturn(currencyAmount1).when(journalLine1).getAmount();
        doReturn(currencyAmount2).when(journalLine2).getAmount();

        new JournalBuilder().withTransactionDate(transactionDate).with(journalLine1, journalLine2).build();
    }

    @Test(expected = LedgerValidationException.class)
    public void transactionCannotPreDateAccountOpeningDates() throws LedgerValidationException {
        doReturn(false).when(transactionDate).after(openingDateForAccount1);
        doReturn(false).when(transactionDate).after(openingDateForAccount2);

        new JournalBuilder().withTransactionDate(transactionDate).with(journalLine1, journalLine2).ofType(PREMIUM_DUE).build();
    }

    @Test
    public void happyPathWithTwoJournaLines() throws LedgerValidationException {
        new JournalBuilder().withTransactionDate(transactionDate).with(journalLine1, journalLine2).ofType(PREMIUM_DUE).build();
    }
}
