/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

@TypeDefinition
@Audited
@Entity
public class PhoneNumber extends ContactSystem {
    static final long serialVersionUID = 6426298757382775043L;

    public final static String MAIN_PHONE_NUMBER = "i18n_contact_system_main_phone_number";
    public final static String MOBILE_PHONE_NUMBER = "i18n_contact_system_mobile_phone_number";
    public final static String HOME_PHONE_NUMBER = "i18n_contact_system_home_phone_number";
    public final static String OFFICE_PHONE_NUMBER = "i18n_contact_system_office_phone_number";
    public final static String FAX_PHONE_NUMBER = "i18n_contact_system_fax_phone_number";

    @Column(name="csyPhoneNumber")
    private String phoneNumber;

    /**
     * @deprecated Default constructor only for use be frameworks.
     */
    @Deprecated
    public PhoneNumber() {
        super();
    }

    public PhoneNumber(Date startDate, Date endDate, String type, String phoneNumber) {
        super(false, startDate, endDate, type);
        setPhoneNumber(phoneNumber);
    }

    public PhoneNumber(String type, String phoneNumber) {
        this(null, null, type, phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.setFullAddress(phoneNumber);
    }

    @Override
    public boolean isSameContactAs(ContactSystem that) {
        if (that == null || !(that instanceof PhoneNumber)) {
            return false;
        }

        PhoneNumber other = (PhoneNumber)that;

        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;

        if (getType() == null) {
            if (other.getType() != null)
                return false;
        } else if (!getType().equals(other.getType()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
        PhoneNumber other = (PhoneNumber) obj;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "PhoneNumber [systemId="+getSystemId()+", isPrimary=" + isPrimary() + ", startDate=" + getStartDate() + ", endDate=" + getEndDate() + ", phoneNumber=" + phoneNumber + ", type=" + getType() + "]";
    }
}
