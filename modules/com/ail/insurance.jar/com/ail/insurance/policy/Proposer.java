/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.party.Address;
import com.ail.party.Title;

/**
 * Interface describing the information fields that are common to all proposer
 * types.
 */
@TypeDefinition
public interface Proposer {
	String getEmailAddress();

	void setEmailAddress(String emailAddress);

	String getTelephoneNumber();

	void setMobilephoneNumber(String telephoneNumber);

	String getMobilephoneNumber();

	void setTelephoneNumber(String telephoneNumber);

	String getLegalName();

	void setLegalName(String legalName);

	Address getAddress();

	void setAddress(Address address);

	String getFirstName();

	void setFirstName(String firstName);

	String getSurname();

	void setSurname(String surname);

	String getOtherTitle();

	void setOtherTitle(String otherTitle);

	Title getTitle();

	void setTitle(Title title);

	void setTitleAsString(String title) throws IndexOutOfBoundsException;

	String getTitleAsString();

	String getActualTitle();
	
	Type getInstance();
}
