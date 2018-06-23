/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

package com.ail.party.search;

import java.util.Collection;
import java.util.Date;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.party.Party;
import com.ail.party.search.hibernate.HibernatePartySearchService;

@ServiceInterface
public interface PartySearchService {

    @ServiceArgument
    public interface PartySearchArgument extends Argument {

        Date getCreatedDateMinimumArg();

        void setCreatedDateMinimumArg(Date createdDateMinimumArg);

        Date getCreatedDateMaximumArg();

        void setCreatedDateMaximumArg(Date createdDateMaximumArg);

        Long getUserIdArg();

        void setUserIdArg(Long userIdArg);

        String getPartyIdArg();

        void setPartyIdArg(String partyIdArg);

        String getLegalNameArg();

        void setLegalNameArg(String legalNameArg);

        String getEmailAddressArg();

        void setEmailAddressArg(String emailAddressArg);

        String getMobilephoneNumberArg();

        void setMobilephoneNumberArg(String mobilephoneNumberArg);

        String getTelephoneNumberArg();

        void setTelephoneNumberArg(String telephoneNumberArg);

        String getPostcodeArg();

        void setPostcodeArg(String postcodeArg);

        String getAddressLineArg();

        void setAddressLineArg(String addressLineArg);

        Collection<Party> getPartyRet();

        void setPartyRet(Collection<Party> partyRet);

        String getOrderByArg();

        void setOrderByArg(String orderBy);

        String getOrderDirectionArg();

        void setOrderDirectionArg(String orderDirection);

        Date getUpdatedDateArg();

        void setUpdatedDateArg(Date upatedDateArg);

        Date getDateOfBirthArg();

        void setDateOfBirthArg(Date dateOfBirthAr);
    }

    @ServiceCommand(defaultServiceClass = HibernatePartySearchService.class)
    public interface PartySearchCommand extends Command, PartySearchArgument {


    }
}
