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

package com.ail.insurance.search;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.search.hibernate.HibernatePolicySearchService;

@ServiceInterface
public interface PolicySearchService {

    @ServiceArgument
    public interface PolicySearchArgument extends Argument {

        String getPolicyNumberArg();

        void setPolicyNumberArg(String policyNumberArg);

        String getQuotationNumberArg();

        void setQuotationNumberArg(String quotationNumberArg);

        String getProductTypeIdArg();

        void setProductTypeIdArg(String productArg);

        List<PolicyStatus> getPolicyStatusArg();

        void setPolicyStatusArg(List<PolicyStatus> policyStatusArg);

        Date getCreatedDateMinimumArg();

        void setCreatedDateMinimumArg(Date createdDateMinimumArg);

        Date getCreatedDateMaximumArg();

        void setCreatedDateMaximumArg(Date createdDateMaximumArg);

        Date getQuoteDateMinimumArg();

        void setQuoteDateMinimumArg(Date quoteDateMinimumArg);

        Date getQuoteDateMaximumArg();

        void setQuoteDateMaximumArg(Date quoteDateMaximumArg);

        Date getInceptionDateMinimumArg();

        void setInceptionDateMinimumArg(Date inceptionDateMinimumArg);

        Date getInceptionDateMaximumArg();

        void setInceptionDateMaximumArg(Date inceptionDateMaximumArg);

        Date getExpiryDateMinimumArg();

        void setExpiryDateMinimumArg(Date expiryDateMinimumArg);

        Date getExpiryDateMaximumArg();

        void setExpiryDateMaximumArg(Date expiryDateMaximumArg);

        String getPartyIdArg();

        void setPartyIdArg(String partyIdArg);

        String getPartyNameArg();

        void setPartyNameArg(String partyNameArg);

        String getPartyAddressArg();

        void setPartyAddressArg(String partyAddressArg);

        String getPartyEmailAddressArg();

        void setPartyEmailAddressArg(String clientEmailAddress);

        Collection<Policy> getPoliciesRet();

        void setPoliciesRet(Collection<Policy> policiesRet);

        String getOrderByArg();

        void setOrderByArg(String orderBy);

        String getOrderDirectionArg();

        void setOrderDirectionArg(String orderDirection);

        Date getUpdatedDateArg();

        void setUpdatedDateArg(Date upatedDateArg);

        void setUserIdArg(Long userIdArg);

        Long getUserIdArg();

        void setCompanyIdArg(Long companyIdArg);

        Long getCompanyIdArg();

        void setDateOfBirthArg(Date dateOfBirthArg);

        Date getDateOfBirthArg();

        void setIncludeTestArg(Boolean includeTestArg);

        Boolean getIncludeTestArg();

        boolean getIncludeSupersededArg();

        void setIncludeSupersededArg(Boolean include);
    }

    @ServiceCommand(defaultServiceClass = HibernatePolicySearchService.class)
    public interface PolicySearchCommand extends Command, PolicySearchArgument {
    }
}
