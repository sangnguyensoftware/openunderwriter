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
package com.ail.ui.server.transformer;

import static com.ail.core.language.I18N.i18n;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ail.core.XMLException;
import com.ail.core.transformer.Transformer;
import com.ail.core.transformer.TransformerException;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyLink;
import com.ail.insurance.policy.PolicyLinkType;
import com.ail.party.Address;
import com.ail.ui.client.common.UIUtil;
import com.ail.ui.shared.model.PolicyDetailDTO;

public class PolicyDetailTransformer implements Transformer<Policy, PolicyDetailDTO>{

    @Override
    public PolicyDetailDTO apply(Policy policy) throws TransformerException {

        try {
            return new PolicyDetailDTO(policy.getSystemId(),
                                    policy.getExternalSystemId(),
                                    policy.getPolicyNumber(),
                                    policy.getQuotationNumber(),
                                    clientName(policy),
                                    clientAddress(policy),
                                    email(policy),
                                    phone(policy),
                                    dateFormat(policy.getQuotationDate()),
                                    dateFormat(policy.getInceptionDate()),
                                    dateFormat(policy.getExpiryDate()),
                                    policy.getProductTypeId(),
                                    premium(policy),
                                    i18n(policy.getStatus().longName()),
                                    clientId(policy),
                                    index(policy.getRenewalIndex()),
                                    index(policy.getMtaIndex()),
                                    supersededBy(policy));
        } catch (XMLException e) {
            throw new TransformerException(e);
        }
    }

    private String clientId(Policy input) {
        if (input != null
                && input.getClient() != null) {
            return (StringUtils.isNotEmpty(input.getClient().getPartyId()) ?
                    input.getClient().getPartyId() : input.getClient().getSystemId() + "");
        } else {
            return "";
        }
    }

    private String index(Long index) {
        return index == null ? "" : String.valueOf(index);
    }

    private String dateFormat(Date date) {
        if (date != null) {
            return DateFormat.getDateInstance().format(date);
        } else {
            return "";
        }
    }

    private String clientName(Policy input) throws XMLException {
        if (input != null
                && input.getClient() != null) {
            return input.getClient().getLegalName();
        } else {
            return "";
        }
    }

    private String email(Policy input) throws XMLException {
        if (input != null
                && input.getClient() != null) {
            return input.getClient().getEmailAddress();
        } else {
            return "";
        }
    }

    private String phone(Policy input) throws XMLException {
        if (input != null
                && input.getClient() != null) {
            String phone = input.getClient().getTelephoneNumber();
            String mobile = input.getClient().getMobilephoneNumber();
            StringBuilder numbers = new StringBuilder();
            if (StringUtils.isNotEmpty(phone)) {
                numbers.append(phone);
            }
            if (StringUtils.isNotEmpty(phone)
                    && StringUtils.isNotEmpty(mobile)) {
                numbers.append(", ");
            }
            if (StringUtils.isNotEmpty(mobile)) {
                numbers.append(mobile);
            }
            return numbers.toString();
        } else {
            return "";
        }
    }

    private List<String> clientAddress(Policy input) throws XMLException {
        if (input != null
                && input.getClient() != null
                && input.getClient().getAddress() != null) {

            Address address = input.getClient().getAddress();

            return UIUtil.toListArray(
                    address.getLine1(),
                    address.getLine2(),
                    address.getTown(),
                    address.getCounty(),
                    address.getCountry(),
                    address.getPostcode());
        } else {
            return UIUtil.toListArray();
        }
    }

    private String premium(Policy policy) {
        if (policy != null && policy.isTotalPremiumDefined()) {
            return policy.getTotalPremium().toFormattedString();
        } else {
            return "";
        }
    }

    private long supersededBy(Policy policy) {
        for (PolicyLink link : policy.getPolicyLink()) {
            if (link.getLinkType() == PolicyLinkType.MTA_FOR) {
                return link.getPolicySystemId();
            }
        }
        return -1;
    }
}
