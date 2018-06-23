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
import static com.ail.core.Functions.hideNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.ail.core.BaseException;
import com.ail.party.Address;
import com.ail.party.ContactPreference;
import com.ail.party.Party;

public class CreatePartyHelper {
    private CreatePartyService.Argument.Party partyArg;
    private Party party;

    public CreatePartyHelper(CreatePartyService.Argument.Party partyArg, Party party) {
        this.partyArg = partyArg;
        this.party = party;
    }

    public void invoke() throws BaseException {
        enrichParty();
    }

    protected void enrichParty() throws BaseException {
        party.setPartyId(partyArg.partyId);
        party.setLegalName(partyArg.legalName);
        party.setEmailAddress(partyArg.emailAddress);
        party.setMobilephoneNumber(partyArg.mobilephoneNumber);
        party.setTelephoneNumber(partyArg.telephoneNumber);
        if (isNotBlank(partyArg.contactPreference)) {
            party.setContactPreference(ContactPreference.forName(partyArg.contactPreference));
        }

        Address address = party.getAddress();
        address.setLine1(hideNull(partyArg.address.line1));
        address.setLine2(hideNull(partyArg.address.line2));
        address.setLine3(hideNull(partyArg.address.line3));
        address.setLine4(hideNull(partyArg.address.line4));
        address.setLine5(hideNull(partyArg.address.line5));
        address.setTown(hideNull(partyArg.address.town));
        address.setCounty(hideNull(partyArg.address.county));
        address.setCountry(hideNull(partyArg.address.country));
        address.setPostcode(hideNull(partyArg.address.postcode));

        if (partyArg.attributes != null) {
            for (CreatePartyService.Argument.Attribute attribute : partyArg.attributes) {
                party.xpathSet("attribute[id='" + attribute.name + "']/value", attribute.value);
            }
        }
    }

}
