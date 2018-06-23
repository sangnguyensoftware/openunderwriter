/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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
import static com.ail.core.Functions.isEmpty;
import static com.ail.financial.ledger.AccountingPeriodStatus.OPEN;

import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.persistence.hibernate.HibernateRunInTransaction;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.AccountingPeriod;
import com.ail.financial.ledger.Balance;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceCommand;
import com.ail.financial.service.CloseAccountingPeriodService.CloseAccountingPeriodArgument;

@ServiceImplementation
public class CloseAccountingPeriodService extends Service<CloseAccountingPeriodArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getAccountingPeriodSystemIdArg() == null && isEmpty(args.getAccountingPeriodExternalSystemIdArg())) {
            throw new PreconditionException("args.getAccountingPeriodSystemIdArg() == null && isEmpty(args.getAccountingPeriodExternalSystemIdArg())");
        }

        if (args.getAccountingPeriodSystemIdArg() != null && !isEmpty(args.getAccountingPeriodExternalSystemIdArg())) {
            throw new PreconditionException("args.getAccountingPeriodSystemIdArg() != null && !isEmpty(args.getAccountingPeriodExternalSystemIdArg())");
        }

        AccountingPeriod accountingPeriod = fetchAccountingPeriod();

        if (accountingPeriod == null) {
            throw new PreconditionException("accountingPeriod == null");
        }

        if (OPEN != accountingPeriod.getStatus()) {
            throw new PreconditionException("OPEN != accountingPeriod.getStatus()");
        }

        if (preceedingAccountingPeriodsAreStillOpen(accountingPeriod)) {
            throw new PreconditionException("preceedingAccountingPeriodsAreStillOpen(accountingPeriod='"+accountingPeriod.getSystemId()+"')");
        }

        closeAccountingPeriodInSeparateTransaction(accountingPeriod);

        calculateAccountBalances(accountingPeriod);
    }

    private boolean preceedingAccountingPeriodsAreStillOpen(AccountingPeriod accountingPeriod) {
        Long openPeriods = (Long)getCoreProxy().queryUnique("get.number.of.open.accountingperiods.before", accountingPeriod.getStartDate());
        return openPeriods != 0;
    }

    void closeAccountingPeriodInSeparateTransaction(AccountingPeriod accountingPeriod) throws PostconditionException {
        try {
            new HibernateRunInTransaction<Object>() {
                @Override
                public Object run() throws Throwable {
                    AccountingPeriod ap = (AccountingPeriod)new CoreProxy().queryUnique("get.accountingperiod.by.systemId", accountingPeriod.getSystemId());
                    ap.closePeriod();
                    return null;
                }
            }.invoke();
        } catch (Throwable e) {
            throw new PostconditionException("Accounting period status update failed", e);
        }
    }

    void calculateAccountBalances(AccountingPeriod accountingPeriod) throws BaseException {
        CalculateAccountBalanceCommand cabs = getCoreProxy().newCommand(CalculateAccountBalanceCommand.class);

        @SuppressWarnings("unchecked")
        List<Account> accounts = (List<Account>) getCoreProxy().query("get.accounts.by.transactions.in.accounting.period", accountingPeriod);

        for(Account account: accounts) {
            cabs.setAccountArg(account);
            cabs.setDateArgRet(accountingPeriod.getEndDate());
            cabs.invoke();
            getCoreProxy().create(new Balance(account, accountingPeriod.getEndDate(), cabs.getBalanceRet()));
        }
    }

    private AccountingPeriod fetchAccountingPeriod() {
        if (args.getAccountingPeriodSystemIdArg() != null) {
            return (AccountingPeriod) getCoreProxy().queryUnique("get.accountingperiod.by.systemId", args.getAccountingPeriodSystemIdArg());
        }
        if (args.getAccountingPeriodExternalSystemIdArg() != null) {
            return (AccountingPeriod) getCoreProxy().queryUnique("get.accountingperiod.by.externalSystemId", args.getAccountingPeriodExternalSystemIdArg());
        }
        return null;
    }

    @ServiceCommand(defaultServiceClass = CloseAccountingPeriodService.class)
    public interface CloseAccountingPeriodCommand extends Command, CloseAccountingPeriodArgument {
    }

    @ServiceArgument
    public interface CloseAccountingPeriodArgument extends Argument {
        void setAccountingPeriodSystemIdArg(Long accountingPeriodSystemIdArg);

        Long getAccountingPeriodSystemIdArg();

        void setAccountingPeriodExternalSystemIdArg(String accountingPeriodSystemIdArg);

        String getAccountingPeriodExternalSystemIdArg();
    }
}
