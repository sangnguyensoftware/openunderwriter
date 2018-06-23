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

package com.ail.financial.ledger;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.AccountType.CASH;
import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static com.ail.financial.ledger.JournalType.PREMIUM_DUE;
import static com.ail.financial.ledger.TransactionType.CREDIT;
import static com.ail.financial.ledger.TransactionType.DEBIT;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.PaymentMethod;
import com.ail.financial.ledger.Account.AccountBuilder;
import com.ail.financial.ledger.Journal.JournalBuilder;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;
import com.ail.financial.ledger.Ledger.LedgerBuilder;
import com.ail.party.Address;
import com.ail.party.Party;
public class TestFinancialModelXMLTranslation extends CoreUserBaseCase {
    private static Date accountOpeningDate = new Date(System.currentTimeMillis()-(60*1000));
    private static Date transactionDate = new Date();

    @Test
    public void testAccountToXML() throws BaseException {
        Account account = buildAccount();

        String accountXML = new CoreProxy().toXML(account).toString();

        assertThat(accountXML, containsString("Account Name"));
        assertThat(accountXML, containsString("CASH"));
        assertThat(accountXML, containsString("Mr Legal Name"));
        assertThat(accountXML, containsString("legal@example.com"));
        assertThat(accountXML, containsString("01234 1234213"));
        assertThat(accountXML, containsString("03214 421234"));
        assertThat(accountXML, containsString("The House"));
        assertThat(accountXML, containsString("Line1"));
        assertThat(accountXML, containsString("Townton"));
        assertThat(accountXML, containsString("Countyshire"));
        assertThat(accountXML, containsString("UK"));
        assertThat(accountXML, containsString("P124 3AX"));
    }

    @Test
    public void testLedgerToXML() throws LedgerValidationException {
        Ledger ledger = buildLedger();

        String ledgerXML = new CoreProxy().toXML(ledger).toString();

        assertThat(ledgerXML, containsString("ledger-1234-abcd-1234-abcd"));
        assertThat(ledgerXML, containsString("Ledger name"));
        assertThat(ledgerXML, containsString("account-1234-abcd-1234-abcd"));
        assertThat(ledgerXML, containsString("A description of this ledger"));
    }

    @Test
    public void testJournalLineToXML() throws LedgerValidationException {
        JournalLine line = buildJournalLine(100, CREDIT);

        String lineXML = new CoreProxy().toXML(line).toString();

        assertThat(lineXML, containsString("CREDIT"));
        assertThat(lineXML, containsString("ledger-1234-abcd-1234-abcd"));
        assertThat(lineXML, containsString("100.00"));
        assertThat(lineXML, containsString("GBP"));
        assertThat(lineXML, containsString("A description"));
        assertThat(lineXML, containsString("paymentmethod-1234-abcd-1234-abcd"));
        assertThat(lineXML, containsString("PREMIUM"));
    }

    @Test
    public void testJournalToXML() throws LedgerValidationException {
        Journal journal = buildJournal();

        String journalXML = new CoreProxy().toXML(journal).toString();

        System.out.println(journalXML);

        assertThat(journalXML, containsString("CREDIT"));
        assertThat(journalXML, containsString("DEBIT"));
        assertThat(journalXML, containsString("journal-1234-abcd-1234-abcd"));
        assertThat(journalXML, containsString("PREMIUM_DUE"));
        assertThat(journalXML, containsString("GBP"));
        assertThat(journalXML, containsString("1000"));
        assertThat(journalXML, containsString("800"));
        assertThat(journalXML, containsString("200"));
        assertThat(journalXML, containsString("A description"));
    }

    private Journal buildJournal() throws LedgerValidationException {
        Journal journal = new JournalBuilder().
                withTransactionDate(transactionDate).
                ofType(PREMIUM_DUE).
                with(buildJournalLine(1000, CREDIT), buildJournalLine(800, DEBIT), buildJournalLine(200, DEBIT)).
                build();

        journal.setExternalSystemId("journal-1234-abcd-1234-abcd");

        return journal;
    }

    private JournalLine buildJournalLine(int value, TransactionType ttype) throws LedgerValidationException {
        JournalLine line = new JournalLineBuilder().
                transactionType(ttype).
                ledger(buildLedger()).
                by(new CurrencyAmount(value, GBP)).
                ofType(PREMIUM).
                description("A description").
                using(buildPaymentMethod()).
                build();
        return line;
    }

    private PaymentMethod buildPaymentMethod() {
        PaymentMethod paymentMethod = new DirectDebit("09827183", "20-27-21");
        paymentMethod.setExternalSystemId("paymentmethod-1234-abcd-1234-abcd");
        return paymentMethod;
    }

    private Ledger buildLedger() throws LedgerValidationException {
        Ledger ledger = new LedgerBuilder().name("Ledger name").description("A description of this ledger").forAccount(buildAccount()).build();

        ledger.setExternalSystemId("ledger-1234-abcd-1234-abcd");

        return ledger;
    }

    private Account buildAccount() throws LedgerValidationException {
        Account account = new AccountBuilder().
                currency(GBP).
                name("Account Name").
                type(CASH).
                openingDate(accountOpeningDate).
                openingBalance(new CurrencyAmount(0, GBP)).
                accountHolder(
                        new Party(null, "Mr Legal Name", "legal@example.com", "01234 1234213", "03214 421234",
                                new Address("The House", "Line1", null, null, null, "Townton", "Countyshire", "UK", "P124 3AX")
                        )
                )
                .build();

        account.setExternalSystemId("account-1234-abcd-1234-abcd");

        return account;
    }
}
