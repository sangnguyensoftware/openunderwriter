package com.ail.base.party;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.AccountType;
import com.ail.financial.service.CreateAccountService.CreateAccountCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.party.Address;
import com.ail.party.ContactPreference;
import com.ail.party.Party;

/**
 * Creates a new Party in the system. For more specific Party implementations
 * this can be extended
 */
@ProductServiceCommand(serviceName = "CreatePartyService", commandName = "CreateParty")
public class CreatePartyService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new CreatePartyService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws Exception {
        if (isInvalidArgument(arg)) {
            return new ClientError(HTTP_BAD_REQUEST, arg.error());
        }

        Party party = findOrCreateParty(arg.party);

        saveParty(party);

        return new Return(HTTP_OK, party);
    }

    protected com.ail.party.Party findOrCreateParty(Argument.Party partyArg) throws BaseException {
        Party party = (Party) PageFlowContext.getCoreProxy().queryUnique("get.parties.by.legalName", partyArg.legalName);

        if (party == null) {
            party = new Party();
            party.setAddress(new Address());
            PageFlowContext.getCoreProxy().create(party);

            if (partyArg.account != null) {
                createAccount(party, partyArg.account);
            }
        }

        new CreatePartyHelper(partyArg, party).invoke();

        return party;
    }

    protected void createAccount(Party party, Argument.Account account) throws BaseException {
        CreateAccountCommand cac = (CreateAccountCommand) CoreContext.getCoreProxy().newCommand(CreateAccountCommand.class);

        cac.setPartyArg(party);
        cac.setTypeArg(AccountType.forName(account.type));
        cac.setNameArg(account.name);
        cac.setDescriptionArg(account.description);
        cac.setOpeningDateArg(account.openingDateDate);
        cac.setOpeningBalanceArg(new CurrencyAmount(account.openingBalance, Currency.forName(account.currency)));

        cac.invoke();
    }

    protected void saveParty(Party party) throws BaseException {
        PageFlowContext.getCoreProxy().update(party);
    }

    protected boolean isInvalidArgument(Argument arg) {
        if (arg.party == null) {
            return error(arg, "party not defined");
        }
        if (arg.party.address == null) {
            return error(arg, "party address not defined");
        }
        if (isBlank(arg.party.legalName)) {
            return error(arg, "party legalName not defined");
        }
        if (isBlank(arg.party.address.line1)) {
            return error(arg, "party address line1 not defined");
        }
        if (isBlank(arg.party.address.town)) {
            return error(arg, "party address town not defined");
        }
        if (isBlank(arg.party.address.country)) {
            return error(arg, "party address country not defined");
        }
        if (isBlank(arg.party.address.postcode)) {
            return error(arg, "party address postcode not defined");
        }
        if (isNotBlank(arg.party.contactPreference)) {
            try {
                ContactPreference.forName(arg.party.contactPreference);
            } catch (Exception ignored) {
                return error(arg, "party contactPreference value invalid");
            }
        }
        if (arg.party.account != null) {
            if (isBlank(arg.party.account.currency)) {
                return error(arg, "party account currency not defined");
            }
            try {
                Currency.forName(arg.party.account.currency);
            } catch (Exception ignored) {
                return error(arg, "party account currency value invalid");
            }
            try {
                AccountType.forName(arg.party.account.type);
            } catch (Exception ignored) {
                return error(arg, "party account type value invalid");
            }
            if (isBlank(arg.party.account.type)) {
                return error(arg, "party account type not defined");
            }
            if (isNotBlank(arg.party.account.openingDate)) {
                try {
                    arg.party.account.openingDateDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(arg.party.account.openingDate);
                } catch (ParseException e) {
                    return error(arg, "party account openingDate not valid. Must be in the form of " + DateFormat.getDateInstance(DateFormat.SHORT).toString());
                }
            }
        }

        return false;
    }

    protected boolean error(Argument arg, String error) {
        arg.error = error;
        return true;
    }

    public static class Argument {
        protected String error;

        public Party party;

        public static class Party {
            public String partyId;
            public String legalName;
            public String emailAddress;
            public String mobilephoneNumber;
            public String telephoneNumber;
            public String contactPreference;

            public Address address;

            public Account account;

            public Attribute[] attributes;
        }

        public static class Address {
            public String line1;
            public String line2;
            public String line3;
            public String line4;
            public String line5;
            public String town;
            public String county;
            public String country;
            public String postcode;
        }

        public static class Account {
            public String currency;
            public String type;
            public String name;
            public String description;
            public String openingDate;
            public Date openingDateDate;
            public double openingBalance;
        }

        public static class Attribute {
            public String name;
            public String value;
        }

        protected String error() {
            return error;
        }
    }

    public static class Return extends RestfulServiceReturn {
        public Long partySystemId;
        public String partyExternalSystemId;

        public Return(int status, Party party) {
            super(status);
            this.partySystemId = party.getSystemId();
            this.partyExternalSystemId = party.getExternalSystemId();
        }
    }
}
