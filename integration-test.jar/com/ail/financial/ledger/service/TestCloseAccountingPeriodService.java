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
import static com.ail.financial.ledger.AccountingPeriodStatus.CLOSED;
import static com.ail.financial.ledger.AccountingPeriodStatus.OPEN;
import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static com.ail.financial.ledger.JournalType.PREMIUM_DUE;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
import com.ail.core.PreconditionException;
import com.ail.core.persistence.CloseSessionService.CloseSessionCommand;
import com.ail.core.persistence.OpenSessionService.OpenSessionCommand;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.Account.AccountBuilder;
import com.ail.financial.ledger.AccountingPeriod;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.Journal.JournalBuilder;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.ledger.Ledger.LedgerBuilder;
import com.ail.financial.ledger.LedgerValidationException;
import com.ail.financial.service.CloseAccountingPeriodService.CloseAccountingPeriodCommand;
import com.ail.financial.service.PostJournalService.PostJournalCommand;
import com.ail.party.Address;
import com.ail.party.Party;

public class TestCloseAccountingPeriodService extends CoreUserBaseCase {
    private Long account1 = 0L;
    private Long account2 = 0L;

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

    @Before
    public void setupLedgers() throws ParseException, BaseException {
        try {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            Ledger ledger1 = openAccountAndReturnLedger("Account One", ZERO, date("01/06/2010"));
            Ledger ledger2 = openAccountAndReturnLedger("Account Two", ZERO, date("01/06/2010"));

            account1 = ledger1.getAccount().getSystemId();
            account2 = ledger2.getAccount().getSystemId();
        } finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
        }
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
        executeSQL("DELETE FROM apeAccountingPeriod");
    }

    @Test
    public void testClosingSinglePeriod() throws BaseException, ParseException {
        postTransaction(account1, account2, new BigDecimal(8.00), date("01/07/2010"));

        assertThat(accountingPeriod("01/07/2010").getStatus(), is(OPEN));

        closeAccountingPeriod("01/07/2010");

        assertThat(accountingPeriod("01/07/2010").getStatus(), is(CLOSED));

        assertThat(fetchLatestSavedBalance(account1), is(new CurrencyAmount("-8GBP")));
        assertThat(fetchLatestSavedBalance(account2), is(new CurrencyAmount("8GBP")));
    }

    @Test
    public void testClosingFourConsecutivePeriods() throws BaseException, ParseException {
        postTransaction(account1, account2, new BigDecimal(8.00), date("01/07/2010"));
        postTransaction(account2, account1, new BigDecimal(16.00), date("01/08/2010"));
        postTransaction(account1, account2, new BigDecimal(32.00), date("01/09/2010"));
        postTransaction(account2, account1, new BigDecimal(64.00), date("01/10/2010"));

        assertThat(accountingPeriod("01/07/2010").getStatus(), is(OPEN));
        assertThat(accountingPeriod("01/08/2010").getStatus(), is(OPEN));
        assertThat(accountingPeriod("01/09/2010").getStatus(), is(OPEN));
        assertThat(accountingPeriod("01/10/2010").getStatus(), is(OPEN));

        closeAccountingPeriod("01/07/2010");
        closeAccountingPeriod("01/08/2010");
        closeAccountingPeriod("01/09/2010");
        closeAccountingPeriod("01/10/2010");

        assertThat(accountingPeriod("01/07/2010").getStatus(), is(CLOSED));
        assertThat(accountingPeriod("01/08/2010").getStatus(), is(CLOSED));
        assertThat(accountingPeriod("01/09/2010").getStatus(), is(CLOSED));
        assertThat(accountingPeriod("01/10/2010").getStatus(), is(CLOSED));

        assertThat(fetchLatestSavedBalance(account1), is(new CurrencyAmount("40GBP")));
        assertThat(fetchLatestSavedBalance(account2), is(new CurrencyAmount("-40GBP")));
    }

    @Test
    public void closingWithOpenPredecessorShouldFail() throws BaseException, ParseException {
        postTransaction(account1, account2, new BigDecimal(8.00), date("01/07/2010"));
        postTransaction(account2, account1, new BigDecimal(16.00), date("01/08/2010"));
        postTransaction(account1, account2, new BigDecimal(32.00), date("01/09/2010"));

        assertThat(accountingPeriod("01/07/2010").getStatus(), is(OPEN));
        assertThat(accountingPeriod("01/08/2010").getStatus(), is(OPEN));
        assertThat(accountingPeriod("01/09/2010").getStatus(), is(OPEN));

        try {
            closeAccountingPeriod("01/07/2010");

            // leave period 01/08/2010" open

            closeAccountingPeriod("01/09/2010");

            fail("Expected PreconditionExcception not thrown.");
        } catch(PreconditionException e) {
            if (!e.getMessage().contains("preceedingAccountingPeriodsAreStillOpen")) {
                throw e;
            }
            // We got the exception we expect. All is good.
        }
    }

    private AccountingPeriod accountingPeriod(String date) throws ParseException {
        try {
            getCore().openPersistenceSession();

            AccountingPeriod accountingPeriod = (AccountingPeriod) getCore().queryUnique("get.accountingperiod.for.date", date(date));

            return accountingPeriod;
        } finally {
            getCore().closePersistenceSession();
        }
    }

    private CurrencyAmount fetchLatestSavedBalance(Long account1uid2) {
        try {
            getCore().openPersistenceSession();

            Account account1 = (Account) getCore().queryUnique("get.account.by.systemId", account1uid2);

            return account1.getBalance().get(account1.getBalance().size() - 1).getAmount();
        } finally {
            getCore().closePersistenceSession();
        }
    }

    private void closeAccountingPeriod(String closingDate) throws ParseException, BaseException {
        try {
            getCore().openPersistenceSession();

            AccountingPeriod accountingPeriod = (AccountingPeriod) getCore().queryUnique("get.accountingperiod.for.date", date(closingDate));

            CloseAccountingPeriodCommand capc = getCore().newCommand(CloseAccountingPeriodCommand.class);
            capc.setAccountingPeriodSystemIdArg(accountingPeriod.getSystemId());
            capc.invoke();
        } finally {
            getCore().closePersistenceSession();
        }
    }

    private void postTransaction(Long srcAccount, Long dstAccount, BigDecimal amount, Date date) throws BaseException, ParseException {
        try {
            getCore().openPersistenceSession();

            Ledger ledger1 = getLedgerForAccountUID(srcAccount);
            Ledger ledger2 = getLedgerForAccountUID(dstAccount);

            buildJournal(ledger1, ledger2, amount, date);
        } finally {
            getCore().closePersistenceSession();
        }
    }

    private Ledger getLedgerForAccountUID(Long account1uid2) {
        Account account = (Account) getCore().queryUnique("get.account.by.systemId", account1uid2);
        Ledger ledger = (Ledger) getCore().queryUnique("get.ledger.for.account", account);
        return ledger;
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

    private void buildJournal(Ledger src, Ledger dst, BigDecimal amount, Date transactionDate) throws BaseException {
        Journal journal = new JournalBuilder().with(new JournalLineBuilder().debit().ledger(src).by(new CurrencyAmount(amount, GBP)).ofType(PREMIUM).build(),
                new JournalLineBuilder().credit().ledger(dst).by(new CurrencyAmount(amount, GBP)).ofType(PREMIUM).build()).withTransactionDate(transactionDate).ofType(PREMIUM_DUE).build();

        PostJournalCommand pjc = getCore().newCommand(PostJournalCommand.class);
        pjc.setJournalArgRet(journal);
        pjc.invoke();
    }
}
