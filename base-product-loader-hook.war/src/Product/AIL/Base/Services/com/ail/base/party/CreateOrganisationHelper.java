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
import com.ail.core.BaseException;
import com.ail.party.Organisation;

public class CreateOrganisationHelper extends CreatePartyHelper {
    private CreateOrganisationService.Argument argument;
    private Organisation organisation;

    public CreateOrganisationHelper(CreateOrganisationService.Argument argument, Organisation organisation) {
        super(argument.party, organisation);
        this.argument = argument;
        this.organisation = organisation;
    }

    public void invoke() throws BaseException {
        enrichOrganisation();
    }

    protected void enrichOrganisation() throws BaseException {
        super.enrichParty();

        organisation.setOrganisationRegistrationNumber(argument.organisation.organisationRegistrationNumber);
        organisation.setTaxRegistrationNumber(argument.organisation.taxRegistrationNumber);
    }

}
