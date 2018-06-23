/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.party;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

@TypeDefinition
@Audited
@Entity
public class Organisation extends Party {
    static final long serialVersionUID = -385826646268259L;

    @Column(name="parOrganisationRegistrationNumber")
    private String organisationRegistrationNumber;

    @Column(name="parTaxRegistrationNumber")
    private String taxRegistrationNumber;

    /** The name of an individual in the organisation */
    private String contactName;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setTaxRegistrationNumber(String taxRegistrationNumber) {
        this.taxRegistrationNumber = taxRegistrationNumber;
    }

    public String getTaxRegistrationNumber() {
        return taxRegistrationNumber;
    }

    public void setOrganisationRegistrationNumber(String organisationRegistrationNumber) {
        this.organisationRegistrationNumber = organisationRegistrationNumber;
    }

    public String getOrganisationRegistrationNumber() {
        return organisationRegistrationNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((organisationRegistrationNumber == null) ? 0 : organisationRegistrationNumber.hashCode());
        result = prime * result + ((taxRegistrationNumber == null) ? 0 : taxRegistrationNumber.hashCode());
        result = prime * result + ((contactName == null) ? 0 : contactName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Organisation other = (Organisation) obj;
        if (organisationRegistrationNumber == null) {
            if (other.organisationRegistrationNumber != null)
                return false;
        } else if (!organisationRegistrationNumber.equals(other.organisationRegistrationNumber))
            return false;
        if (contactName == null) {
            if (other.contactName != null)
                return false;
        } else if (!contactName.equals(other.contactName))
            return false;
        if (taxRegistrationNumber == null) {
            if (other.taxRegistrationNumber != null)
                return false;
        } else if (!taxRegistrationNumber.equals(other.taxRegistrationNumber))
            return false;
        return true;
    }
}
