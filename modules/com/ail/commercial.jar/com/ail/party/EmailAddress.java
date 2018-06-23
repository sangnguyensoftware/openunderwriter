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

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

@TypeDefinition
@Audited
@Entity
public class EmailAddress extends ContactSystem {
    static final long serialVersionUID = 6426298757382775043L;
    public final static String MAIN_EMAIL_ADDRESS = "i18n_contact_system_main_email_address";

    private String emailAddress;

    /**
     * @deprecated Default constructor only for use be frameworks.
     */
    @Deprecated
    public EmailAddress() {
        super();
    }

    public EmailAddress(Date startDate, Date endDate, String type, String emailAddress) {
        super(false, startDate, endDate, type);
        setEmailAddress(emailAddress);
    }

    public EmailAddress(String type, String emailAddress) {
        this(null, null, type, emailAddress);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        this.setFullAddress(emailAddress);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
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
        EmailAddress other = (EmailAddress) obj;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        } else if (!emailAddress.equals(other.emailAddress))
            return false;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "EmailAddress [systemId=" + getSystemId() + ", primary=" + isPrimary() + ", startDate=" + getStartDate() + ", endDate=" + getEndDate() + ", emailAddress=" + emailAddress + "]";
    }

    /**
     * EmailAddresses are considered to be the same if the text of the emailAddress
     * field is that same and they have the same <code>type</code>.
     *
     * @return true if <code>that</code> is an instance of {@link EmailAddress} and
     *         has the same email address <code>this</code> and the same
     *         <code>type</code>; false otherwise.
     */
    @Override
    public boolean isSameContactAs(ContactSystem other) {
        if (other == null || !(other instanceof EmailAddress)) {
            return false;
        }

        EmailAddress that = (EmailAddress)other;

        if (this.emailAddress == null) {
            return that.emailAddress == null;
        }

        if (this.getType() == null) {
            return that.getType() == null;
        }

        return this.emailAddress.equals(that.emailAddress) && this.getType().equals(that.getType());
    }
}
