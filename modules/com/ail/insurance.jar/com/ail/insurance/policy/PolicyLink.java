package com.ail.insurance.policy;

import com.ail.core.Type;

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

/**
 * A PolicyLink describes a relationship from a Policy to another Policy. A
 * Policy object carries a collection (set) of such relationships which allow it
 * to define, for example, the policy that it renewed from, the quotation that
 * it was disaggregated from.
 */
public class PolicyLink extends Type {
    private PolicyLinkType linkType;
    private Long policySystemId;

    /* No args constructor for Hibernate/Castor etc. */
    public PolicyLink() {
    }

    public PolicyLink(PolicyLinkType type, Long systemId) {
        this.linkType = type;
        this.policySystemId = systemId;
    }

    public PolicyLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(PolicyLinkType linkType) {
        this.linkType = linkType;
    }

    public Long getPolicySystemId() {
        return policySystemId;
    }

    public void setPolicySystemId(Long policySystemId) {
        this.policySystemId = policySystemId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
        result = prime * result + ((policySystemId == null) ? 0 : policySystemId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PolicyLink other = (PolicyLink) obj;
        if (linkType != other.linkType)
            return false;
        if (policySystemId == null) {
            if (other.policySystemId != null)
                return false;
        } else if (!policySystemId.equals(other.policySystemId))
            return false;
        return true;
    }
}
