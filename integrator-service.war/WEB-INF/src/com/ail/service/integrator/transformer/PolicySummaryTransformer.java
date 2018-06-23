/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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
package com.ail.service.integrator.transformer;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ail.core.PageVisit;
import com.ail.core.XMLException;
import com.ail.core.language.I18N;
import com.ail.core.transformer.Transformer;
import com.ail.core.transformer.TransformerException;
import com.ail.insurance.policy.Policy;
import com.ail.service.integrator.model.PolicySummary;
import com.liferay.portal.service.UserLocalServiceUtil;

public class PolicySummaryTransformer implements Transformer<Policy, PolicySummary>{

    @Override
    public PolicySummary apply(Policy policy) throws TransformerException {
        try {
            return new PolicySummary(policy.getSystemId(),
                                    policy.getExternalSystemId(),
                                    policy.getCreatedDate(),
                                    policy.getUpdatedDate(),
                                    policy.getPolicyNumber(),
                                    policy.getQuotationNumber(),
                                    buildClientName(policy),
                                    buildPolicyQuotationDate(policy),
                                    buildPolicyInceptionDate(policy),
                                    buildPolicyExpiryDate(policy),
                                    policy.getProductTypeId(),
                                    buildPremium(policy),
                                    policy.getStatusAsString(),
                                    buildPageFlowPageMap(policy),
                                    policy.getOwningUser(),
                                    buildOwningUserName(policy));
        } catch (XMLException e) {
            throw new TransformerException(e);
        }
    }

    private String buildOwningUserName(Policy policy) {
        try {
            return UserLocalServiceUtil.getUserById(policy.getOwningUser()).getLogin();
        } catch (Throwable t) {
            return I18N.i18n("i18n_Unknown");
        }
    }

    private Map<String, String> buildPageFlowPageMap(Policy policy) {
        Map<String,String> map=new HashMap<>();
        for(PageVisit visit: policy.getPageVisit()) {
            map.put(visit.getPageFlowName(), visit.getPageName());
        }
        return map;
    }

    private String buildDate(Date date) {
        if (date != null) {
            return DateFormat.getDateInstance().format(date);
        } else {
            return "";
        }
    }

    private String buildPolicyQuotationDate(Policy input) throws XMLException {
        if (input != null) {
            return buildDate(input.getQuotationDate());
        } else {
            return "";
        }
    }

    private String buildPolicyInceptionDate(Policy input) throws XMLException {
        if (input != null) {
            return buildDate(input.getInceptionDate());
        } else {
            return "";
        }
    }

    private String buildPolicyExpiryDate(Policy input) throws XMLException {
        if (input != null) {
            return buildDate(input.getExpiryDate());
        } else {
            return "";
        }
    }

    private String buildClientName(Policy input) throws XMLException {
        if (input != null
                && input.getClient() != null) {
            return input.getClient().getLegalName();
        } else {
            return "";
        }
    }

    private String buildPremium(Policy policy) {
        if (policy != null && policy.isTotalPremiumDefined()) {
            return policy.getTotalPremium().toFormattedString();
        } else {
            return "";
        }
    }
}
