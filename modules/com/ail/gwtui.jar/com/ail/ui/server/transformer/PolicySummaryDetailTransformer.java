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
package com.ail.ui.server.transformer;

import static com.ail.core.language.I18N.i18n;
import static com.ail.insurance.policy.PolicyStatus.forName;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.ail.core.transformer.Transformer;
import com.ail.core.transformer.TransformerException;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyLink;
import com.ail.insurance.policy.PolicyLinkType;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.common.collect.Lists;

public class PolicySummaryDetailTransformer implements Transformer<Policy, PolicyDetailDTO>{

    private static final int STATUS_DISPLAY_LENGTH = 3;

    @Override
    public PolicyDetailDTO apply(Policy policy) throws TransformerException {

        return new PolicyDetailDTO(policy.getSystemId(),
                                   policy.getPolicyNumber(),
                                   policy.getQuotationNumber(),
                                   name(policy.getId()),
                                   address(policy.getId()),
                                   status(policy.getStatusAsString()),
                                   policy.getProductName(),
                                   dateFormat(policy.getCreatedDate()),
                                   dateFormat(policy.getInceptionDate()),
                                   dateFormat(policy.getExpiryDate()),
                                   index(policy.getRenewalIndex()),
                                   index(policy.getMtaIndex()),
                                   supersededBy(policy));
    }

    private String dateFormat(Date date) {
        if (date != null) {
            return DateFormat.getDateInstance().format(date);
        } else {
            return "";
        }
    }

    private String index(Long index) {
        return index == null ? "" : String.valueOf(index);
    }

    private String name(String id) {
        if (StringUtils.hasLength(id)) {
            if (id.contains("|")) {
                return id.substring(0, id.indexOf("|"));
            } else {
                return id;
            }
        }
        return "";
    }

    private List<String> address(String id) {
        if (StringUtils.hasLength(id) && id.contains("|")) {
            String address = id.substring(id.indexOf("|") + 1);
            if (!",".equals(address.trim())) {
                return Lists.newArrayList(address);
            }
        }
        return Lists.newArrayList();
    }

    private String status(String status) {
        if (StringUtils.hasLength(status)) {
            return i18n(forName(status).getLongName()).replace(" ", "").substring(0, STATUS_DISPLAY_LENGTH).toUpperCase();
        }
        return "";
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
