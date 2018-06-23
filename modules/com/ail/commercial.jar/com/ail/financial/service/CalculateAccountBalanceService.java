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
import static java.math.BigDecimal.ZERO;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.le;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.DateTimeUtils;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.Balance;
import com.ail.financial.ledger.LedgerException;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceArgument;

/**
 * Calculate the balance on an account at a specified date (or today if no date
 * is specified).
 */
@ServiceImplementation
public class CalculateAccountBalanceService extends Service<CalculateAccountBalanceArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getAccountArg() == null) {
            throw new PreconditionException("args.getAccountArg() == null");
        }

        args.setDateArgRet(determineEffectiveDate());

        Balance baseBalance = fetchNearestStoredBalanceByDate();

        CurrencyAmount credits = fetchSumOfCreditsSinceStoredBalance(baseBalance);

        CurrencyAmount debits = fetchSumOfDebitsSinceStoredBalance(baseBalance);

        args.setBalanceRet(baseBalance.getAmount().add(credits).subtract(debits));

        if (args.getBalanceRet() == null) {
            throw new PostconditionException("args.getBalanceRet() == null");
        }
    }

    private CurrencyAmount fetchSumOfDebitsSinceStoredBalance(Balance storedBalance) {
        BigDecimal amount = (BigDecimal)getCoreProxy().queryUnique("sum.debits.for.account.between.two.dates", args.getAccountArg(), storedBalance.getDate(), args.getDateArgRet());
        return new CurrencyAmount(amount == null ? ZERO : amount, args.getAccountArg().getCurrency());
    }

    private CurrencyAmount fetchSumOfCreditsSinceStoredBalance(Balance storedBalance) {
        BigDecimal amount = (BigDecimal)getCoreProxy().queryUnique("sum.credits.for.account.between.two.dates", args.getAccountArg(), storedBalance.getDate(), args.getDateArgRet());
        return new CurrencyAmount(amount == null ? ZERO : amount, args.getAccountArg().getCurrency());
    }

    private Date determineEffectiveDate() {
        return args.getDateArgRet() != null ? args.getDateArgRet() : new Date();
    }

    private Balance fetchNearestStoredBalanceByDate() throws LedgerException {
        Criteria criteria = getCoreProxy().criteria(Balance.class);

        criteria.add(eq("account", args.getAccountArg())).
                 add(le("date", args.getDateArgRet())).
                 addOrder(desc("date")).
                 addOrder(desc("systemId")).
                 setMaxResults(1).
                 setReadOnly(true);

        List<?> balanceSearchResults = criteria.list();

        if (balanceSearchResults.size() != 1) {
            throw new LedgerException("balanceSearchResults.size() != 1 (account: "+args.getAccountArg().getSystemId()+", date:"+DateTimeUtils.format(args.getDateArgRet(), "dd/MM/yyyy")+")");
        }

        return (Balance) balanceSearchResults.get(0);
    }

    @ServiceCommand(defaultServiceClass = CalculateAccountBalanceService.class)
    public interface CalculateAccountBalanceCommand extends Command, CalculateAccountBalanceArgument {
    }

    @ServiceArgument
    public interface CalculateAccountBalanceArgument extends Argument {
        void setDateArgRet(Date dateArgRet);

        Date getDateArgRet();

        void setAccountArg(Account accountArg);

        Account getAccountArg();

        void setBalanceRet(CurrencyAmount balanceRet);

        CurrencyAmount getBalanceRet();
    }
}
