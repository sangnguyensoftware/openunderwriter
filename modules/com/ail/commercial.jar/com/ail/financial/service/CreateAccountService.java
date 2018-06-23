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

import java.util.Date;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.Account.AccountBuilder;
import com.ail.financial.ledger.AccountType;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.ledger.Ledger.LedgerBuilder;
import com.ail.financial.service.CreateAccountService.CreateAccountArgument;
import com.ail.party.Party;


/**
 * Service to create an account for a party and (implicitly) create a ledger for
 * that account.
 */
@ServiceImplementation
public class CreateAccountService extends Service<CreateAccountArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getPartyArg() == null) {
            throw new PreconditionException("args.getPartyArg() == null");
        }

        if (args.getTypeArg() == null) {
            throw new PreconditionException("args.getTypeArg() == null");
        }

        if (args.getCurrencyArg() == null && args.getOpeningBalanceArg() == null) {
            throw new PreconditionException("args.getCurrencyArg() == null && args.getOpeningBalanceArg() == null");
        }

        if (args.getCurrencyArg() != null && args.getOpeningBalanceArg() != null) {
            throw new PreconditionException("args.getCurrencyArg() != null && args.getOpeningBalanceArg() != null");
        }

        Account account = new AccountBuilder().
                            type(args.getTypeArg()).
                            accountHolder(args.getPartyArg()).
                            currency(args.getCurrencyArg()).
                            openingBalance(args.getOpeningBalanceArg()).
                            description(args.getDescriptionArg()).
                            name(args.getNameArg()).
                            openingDate(args.getOpeningDateArg()).
                            build();

        account = CoreContext.getCoreProxy().create(account);

        Ledger ledger = new LedgerBuilder().
                            forAccount(account).
                            build();

        ledger = CoreContext.getCoreProxy().create(ledger);

        args.setAccountRet(account);
        args.setLedgerRet(ledger);
    }

    @ServiceCommand(defaultServiceClass = CreateAccountService.class)
    public interface CreateAccountCommand extends Command, CreateAccountArgument {
    }

    @ServiceArgument
    public interface CreateAccountArgument extends Argument {
        /**
         * The type to be applied to the account.
         */
        AccountType getTypeArg();

        /**
         * The type to be applied to the account.
         */
        void setTypeArg(AccountType accountTypeArg);

        /**
         * Party for whom the account is being opened.
         */
        Party getPartyArg();

        /**
         * Party for whom the account is being opened.
         */
        void setPartyArg(Party partyArg);

        /**
         * Opening balance for the account. This is an optional argument. If set it defines the
         * account's opening balance and its currency. If not set, then {@link #setCurrencyArg(Currency)} must be set.
         */
        CurrencyAmount getOpeningBalanceArg();

        /**
         * Opening balance for the account. This is an optional argument. If set it defines the
         * account's opening balance and its currency. If not set, then {@link #setCurrencyArg(Currency)} must be set.
         */
        void setOpeningBalanceArg(CurrencyAmount openingBalanceArg);

        /**
         * Date the account is opened from. This is an optional argument. If not set, today's date will be assumed.
         */
        Date getOpeningDateArg();

        /**
         * Date the account is opened from. This is an optional argument. If not set, today's date will be assumed.
         */
        void setOpeningDateArg(Date openingDateArg);

        /**
         * Currency for the account. This is an optional argument. If not set, then
         * {@link #setOpeningBalanceArg(CurrencyAmount)} must be set. If set, then an
         * opening balance of zero is assumed.
         */
        Currency getCurrencyArg();

        /**
         * Currency for the account. This is an optional argument. If not set, then
         * {@link #setOpeningBalanceArg(CurrencyAmount)} must be set. If set, then an
         * opening balance of zero is assumed.
         */
        void setCurrencyArg(Currency currencyArg);

        /**
         * The description to be applied to the account. This is an optional argument. Account descriptions can be blank.
         */
        String getDescriptionArg();

        /**
         * The description to be applied to the account. This is an optional argument. Account descriptions can be blank.
         */
        void setDescriptionArg(String descriptionArg);

        /**
         * The name to be applied to the account. This is an optional argument. Account names can be blank.
         */
        String getNameArg();

        /**
         * The name to be applied to the account. This is an optional argument. Account names can be blank.
         */
        void setNameArg(String nameArg);

        /**
         * Returns the Account created by the service.
         */
        Account getAccountRet();

        /**
         * Returns the Account created by the service.
         */
        void setAccountRet(Account accountRet);

        /**
         * Returns the Ledger created by the service.
         */
        Ledger getLedgerRet();

        /**
         * Returns the Ledger created by the service.
         */
        void setLedgerRet(Ledger ledgerRet);
    }
}
