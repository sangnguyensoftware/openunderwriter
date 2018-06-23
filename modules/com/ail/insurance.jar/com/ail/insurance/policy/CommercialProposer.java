/* Copyright Applied Industrial Logic Limited 2008. All rights Reserved */
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
package com.ail.insurance.policy;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.party.Organisation;
import com.ail.party.Person;
import com.ail.party.Title;

/**
 * Represents the organisation that the quotation was prepared for. Generally this type is appropriate
 * for use in commercial lines.
 * @see PersonalProposer
 */
@TypeDefinition
@Audited
@Entity
public class CommercialProposer extends Organisation implements Proposer {
	private static final long serialVersionUID = 1L;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "parContactUIDpar", referencedColumnName = "UID")
	private Person contact;

	public String getCompanyName() {
		return getLegalName();
	}

	public void setCompanyName(String companyName) {
		setLegalName(companyName);
	}

	public CommercialProposer() {
		contact=new Person();
	}

	@Override
    public String getActualTitle() {
		return contact.getActualTitle();
	}

	@Override
    public String getFirstName() {
		return contact.getFirstName();
	}

	@Override
    public String getOtherTitle() {
		return contact.getOtherTitle();
	}

	@Override
    public String getSurname() {
		return contact.getSurname();
	}

	@Override
    public Title getTitle() {
		return contact.getTitle();
	}

	@Override
    public String getTitleAsString() {
		return contact.getTitleAsString();
	}

	@Override
    public void setFirstName(String firstName) {
		contact.setFirstName(firstName);
	}

	@Override
    public void setOtherTitle(String otherTitle) {
		contact.setOtherTitle(otherTitle);
	}

	@Override
    public void setSurname(String surname) {
		contact.setSurname(surname);
	}

	@Override
    public void setTitle(Title title) {
		contact.setTitle(title);
	}

	@Override
    public void setTitleAsString(String title) throws IndexOutOfBoundsException {
		contact.setTitleAsString(title);
	}

	public Person getContact() {
		return contact;
	}

	public void setContact(Person contact) {
		this.contact = contact;
	}

	@Override
    public Type getInstance() {
		return this;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((contact == null) ? 0 : contact.hashCode());
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
        CommercialProposer other = (CommercialProposer) obj;
        if (contact == null) {
            if (other.contact != null)
                return false;
        } else if (!contact.equals(other.contact))
            return false;
        return true;
    }
}
