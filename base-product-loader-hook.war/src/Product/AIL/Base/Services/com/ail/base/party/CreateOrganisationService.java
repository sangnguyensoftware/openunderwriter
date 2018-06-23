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

import com.ail.core.BaseException;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.party.Address;
import com.ail.party.Organisation;

/**
 * Creates a new Party in the system. For more specific Party implementations
 * this can be extended
 */
@ProductServiceCommand(serviceName = "CreateOrganisationService", commandName = "CreateOrganisation")
public class CreateOrganisationService extends CreatePartyService {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new CreateOrganisationService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws Exception {
        if (isInvalidArgument(arg)) {
            return new ClientError(HTTP_BAD_REQUEST, arg.error());
        }

        Organisation organisation = findOrCreateParty(arg);

        saveParty(organisation);

        return new Return(HTTP_OK, organisation);
    }

    protected com.ail.party.Organisation findOrCreateParty(CreateOrganisationService.Argument arg) throws BaseException {
        Organisation organisation = (Organisation) PageFlowContext.getCoreProxy().queryUnique("get.parties.by.legalName", arg.party.legalName);

        if (organisation == null) {
            organisation = new Organisation();
            organisation.setAddress(new Address());
            PageFlowContext.getCoreProxy().create(organisation);

            if (arg.party.account != null) {
                createAccount(organisation, arg.party.account);
            }
        }

        new CreateOrganisationHelper(arg, organisation).invoke();

        return organisation;
    }

    protected boolean isInvalidArgument(CreateOrganisationService.Argument arg) {
        if (!super.isInvalidArgument(arg)) {
            if (isBlank(arg.organisation.organisationRegistrationNumber)) {
                return error(arg, "organisationRegistrationNumber not defined");
            }
        }

        return false;
    }

    public static class Argument extends CreatePartyService.Argument {
        public Organisation organisation;

        public static class Organisation {

            public String organisationRegistrationNumber;
            public String taxRegistrationNumber;

        }

    }
}
