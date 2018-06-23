/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.financial.service;

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.financial.ledger.AccountingPeriodStatus.OPEN;
import static java.util.Calendar.DATE;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;

import java.util.Calendar;
import java.util.Date;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.ledger.AccountingPeriod;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.JournalLine;
import com.ail.financial.ledger.LedgerValidationException;
import com.ail.financial.service.PostJournalService.PostJournalArgument;

/**
 */
@ServiceImplementation
public class PostJournalService extends Service<PostJournalArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getJournalArgRet() == null) {
            throw new PreconditionException("args.getJournalArgRet() == null");
        }

        Journal journal = args.getJournalArgRet();

        linkToAccountingPeriod(journal);

        journal = getCoreProxy().create(journal);

        for(JournalLine line: journal.getJournalLine()) {
            getCoreProxy().create(line);
        }
    }

    private void linkToAccountingPeriod(Journal journal) throws LedgerValidationException {
        AccountingPeriod accountPeriod = (AccountingPeriod)getCoreProxy().queryUnique("get.accountingperiod.for.date", journal.getTransactionDate());

        if (accountPeriod == null) {
            if (accountingPeriodsAllowedToAutoCreate()) {
                accountPeriod = createAccountingPeriod(journal);
            }
            else {
                throw new LedgerValidationException("accountingPeriod == null");
            }
        }

        if (OPEN != accountPeriod.getStatus()) {
            throw new LedgerValidationException("OPEN != accountPeriod.getStatus()");
        }

        journal.initialiseAccountingPeriod(accountPeriod);
    }

    private AccountingPeriod createAccountingPeriod(Journal journal) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(journal.getTransactionDate());
        cal.set(DATE, 1);
        cal.set(HOUR_OF_DAY, 0);
        cal.set(MINUTE, 0);
        cal.set(SECOND, 0);
        cal.set(MILLISECOND, 0);
        Date startDate = cal.getTime();
        cal.add(MONTH, 1);
        cal.add(MILLISECOND, -1);
        Date endDate = cal.getTime();

        AccountingPeriod accountingPeriod = new AccountingPeriod(startDate, endDate);

        return getCoreProxy().create(accountingPeriod);
    }

    private Boolean accountingPeriodsAllowedToAutoCreate() {
        return new Boolean(getCoreProxy().getParameterValue("Ledgers.AutoCreateAccountingPeriods", "false"));
    }

    @ServiceCommand(defaultServiceClass = PostJournalService.class)
    public interface PostJournalCommand extends Command, PostJournalArgument {
    }

    @ServiceArgument
    public interface PostJournalArgument extends Argument {
        void setJournalArgRet(Journal journalArgRet);

        Journal getJournalArgRet();
    }
}
