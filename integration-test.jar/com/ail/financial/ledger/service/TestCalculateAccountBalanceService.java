/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.ail.financial.ledger.service;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.AccountType.CASH;
import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static com.ail.financial.ledger.JournalType.PREMIUM_DUE;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.Account.AccountBuilder;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.Journal.JournalBuilder;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.ledger.Ledger.LedgerBuilder;
import com.ail.financial.ledger.LedgerValidationException;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceCommand;
import com.ail.party.Address;
import com.ail.party.Party;

public class TestCalculateAccountBalanceService extends CoreUserBaseCase {

    @Before
    public void setUp() throws Exception {
        CoreProxy coreProxy = new CoreProxy(this.getConfigurationNamespace(), this);
        setCore(coreProxy.getCore());
        tidyUpTestData();
        tidyUpFinancialData();
        setupSystemProperties();
        setupConfigurations();
        setupTestProducts();
        coreProxy.setVersionEffectiveDateToNow();
        CoreContext.initialise();
        CoreContext.setCoreProxy(coreProxy);
    }

    @After
    public void tearDown() throws Exception {
        CoreContext.destroy();
        tidyUpTestData();
    }

    @After
    public void tidyUpFinancialData() throws Exception {
        executeSQL("DELETE FROM jJouJliJli");
        executeSQL("DELETE FROM jLedJliJli");
        executeSQL("DELETE FROM jliJournalLine");
        executeSQL("DELETE FROM jouJournal");
        executeSQL("DELETE FROM ledLedger");
        executeSQL("DELETE FROM balBalance");
        executeSQL("DELETE FROM accAccount");
    }

    @Test
    public void simpleBalanceCalculation() throws BaseException, ParseException {
        Account account1;
        Account account2;

        try {
            getCore().openPersistenceSession();

            Ledger ledger1 = openAccountAndReturnLedger("Account One", ZERO, date("01/06/2010"));
            Ledger ledger2 = openAccountAndReturnLedger("Account Two", ZERO, date("01/06/2010"));

            buildJournal(ledger1, ledger2, new BigDecimal(100.00), date("01/07/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(50.00), date("01/07/2010"));

            account1 = ledger1.getAccount();
            account2 = ledger2.getAccount();
        } finally {
            getCore().closePersistenceSession();
        }

        try {
            getCore().openPersistenceSession();

            CalculateAccountBalanceCommand cabc = getCore().newCommand(CalculateAccountBalanceCommand.class);

            cabc.setAccountArg(account1);
            cabc.setDateArgRet(date("01/08/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(-150.00, GBP)));

            cabc.setAccountArg(account2);
            cabc.setDateArgRet(date("01/08/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(150.00, GBP)));
        } finally {
            getCore().closePersistenceSession();
        }
    }

    @Test
    public void openingBalanceShouldAffectCalculation() throws BaseException, ParseException {
        Account account1;
        Account account2;

        try {
            getCore().openPersistenceSession();

            Ledger ledger1 = openAccountAndReturnLedger("Account One", new BigDecimal(100.00), date("01/06/2010"));
            Ledger ledger2 = openAccountAndReturnLedger("Account Two", new BigDecimal(500.00), date("01/06/2010"));

            buildJournal(ledger1, ledger2, new BigDecimal(100.00), date("01/07/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(50.00), date("01/07/2010"));

            account1 = ledger1.getAccount();
            account2 = ledger2.getAccount();
        } finally {
            getCore().closePersistenceSession();
        }

        try {
            getCore().openPersistenceSession();

            CalculateAccountBalanceCommand cabc = getCore().newCommand(CalculateAccountBalanceCommand.class);

            cabc.setAccountArg(account1);
            cabc.setDateArgRet(date("01/08/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(-50.00, GBP)));

            cabc.setAccountArg(account2);
            cabc.setDateArgRet(date("01/08/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(650.00, GBP)));
        } finally {
            getCore().closePersistenceSession();
        }
    }

    @Test
    public void checkMidStatementBalanceOnDateWhichHasNoTransactions() throws BaseException, ParseException {
        Account account1;
        Account account2;

        try {
            getCore().openPersistenceSession();

            Ledger ledger1 = openAccountAndReturnLedger("Account One", ZERO, date("01/06/2010"));
            Ledger ledger2 = openAccountAndReturnLedger("Account Two", ZERO, date("01/06/2010"));

            buildJournal(ledger1, ledger2, new BigDecimal(8.00),  date("01/07/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(16.00), date("01/08/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(32.00), date("01/09/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(64.00), date("01/10/2010"));

            account1 = ledger1.getAccount();
            account2 = ledger2.getAccount();
        } finally {
            getCore().closePersistenceSession();
        }

        try {
            getCore().openPersistenceSession();

            CalculateAccountBalanceCommand cabc = getCore().newCommand(CalculateAccountBalanceCommand.class);

            cabc.setAccountArg(account1);
            cabc.setDateArgRet(date("10/08/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(-24.00, GBP)));

            cabc.setAccountArg(account2);
            cabc.setDateArgRet(date("10/08/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(24.00, GBP)));
        } finally {
            getCore().closePersistenceSession();
        }
    }

    @Test
    public void checkMidStatementBalanceOnDateWhichHasTransactions() throws BaseException, ParseException {
        Account account1;
        Account account2;

        try {
            getCore().openPersistenceSession();

            Ledger ledger1 = openAccountAndReturnLedger("Account One", ZERO, date("01/06/2010"));
            Ledger ledger2 = openAccountAndReturnLedger("Account Two", ZERO, date("01/06/2010"));

            buildJournal(ledger1, ledger2, new BigDecimal(8.00),  date("01/07/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(16.00), date("01/08/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(32.00), date("01/09/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(64.00), date("01/10/2010"));

            account1 = ledger1.getAccount();
            account2 = ledger2.getAccount();
        } finally {
            getCore().closePersistenceSession();
        }

        try {
            getCore().openPersistenceSession();

            CalculateAccountBalanceCommand cabc = getCore().newCommand(CalculateAccountBalanceCommand.class);

            cabc.setAccountArg(account1);
            cabc.setDateArgRet(date("01/09/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(-56.00, GBP)));

            cabc.setAccountArg(account2);
            cabc.setDateArgRet(date("01/09/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(56.00, GBP)));
        } finally {
            getCore().closePersistenceSession();
        }
    }

    @Test
    public void checkCreditsAndDebits() throws BaseException, ParseException {
        Account account1;
        Account account2;

        try {
            getCore().openPersistenceSession();

            Ledger ledger1 = openAccountAndReturnLedger("Account One", ZERO, date("01/06/2010"));
            Ledger ledger2 = openAccountAndReturnLedger("Account Two", ZERO, date("01/06/2010"));

            buildJournal(ledger1, ledger2, new BigDecimal(8.00),  date("01/07/2010"));
            buildJournal(ledger2, ledger1, new BigDecimal(16.00), date("01/08/2010"));
            buildJournal(ledger1, ledger2, new BigDecimal(32.00), date("01/09/2010"));
            buildJournal(ledger2, ledger1, new BigDecimal(64.00), date("01/10/2010"));

            account1 = ledger1.getAccount();
            account2 = ledger2.getAccount();
        } finally {
            getCore().closePersistenceSession();
        }

        try {
            getCore().openPersistenceSession();

            CalculateAccountBalanceCommand cabc = getCore().newCommand(CalculateAccountBalanceCommand.class);

            cabc.setAccountArg(account1);
            cabc.setDateArgRet(date("01/11/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(40.00, GBP)));

            cabc.setAccountArg(account2);
            cabc.setDateArgRet(date("01/11/2010"));
            cabc.invoke();
            assertThat(cabc.getBalanceRet(), is(new CurrencyAmount(-40.00, GBP)));
        } finally {
            getCore().closePersistenceSession();
        }
    }

    private Date date(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").parse(date);
    }

    private Ledger openAccountAndReturnLedger(String name, BigDecimal openingBalance, Date openingDate) throws LedgerValidationException {
        Account account = buildAccount(name, openingBalance, openingDate);
        Ledger ledger = buildLedger(account);
        return ledger;
    }

    private Ledger buildLedger(Account account) throws LedgerValidationException {
        Ledger ledger = new LedgerBuilder().name("Ledger name").description("A description of this ledger").forAccount(account).build();
        return getCore().create(ledger);
    }

    private Account buildAccount(String name, BigDecimal openingBalance, Date openingDate) throws LedgerValidationException {
        Account account = new AccountBuilder().currency(GBP).name(name).type(CASH).openingDate(openingDate).openingBalance(new CurrencyAmount(openingBalance, GBP)).accountHolder(
                new Party(null, "Mr Legal Name", "legal@example.com", "01234 1234213", "03214 421234", new Address("The House", "Line1", null, null, null, "Townton", "Countyshire", "UK", "P124 3AX")))
                .build();

        getCore().create(account.getAccountHolder());
        return getCore().create(account);
    }

    private Journal buildJournal(Ledger src, Ledger dst, BigDecimal amount, Date transactionDate) throws LedgerValidationException {
        Journal journal = new JournalBuilder().with(new JournalLineBuilder().debit().ledger(src).by(new CurrencyAmount(amount, GBP)).ofType(PREMIUM).build(),
                new JournalLineBuilder().credit().ledger(dst).by(new CurrencyAmount(amount, GBP)).ofType(PREMIUM).build()).withTransactionDate(transactionDate).ofType(PREMIUM_DUE).build();

        getCore().create(journal.getJournalLine().get(0));
        getCore().create(journal.getJournalLine().get(1));

        return getCore().create(journal);
    }
}
