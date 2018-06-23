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

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.party.Person;

/**
 * A PersonalProposer represents the individual applying for a product. Generally appropriate for
 * personal lines products.
 * @see CommercialProposer
 */
@TypeDefinition
@Audited
@Entity
public class PersonalProposer extends Person implements Proposer {
	private static final long serialVersionUID = -4521508279619758949L;

	public PersonalProposer() {
	}

	@Override
    public Type getInstance() {
		return this;
	}

    @Override
    public int hashCode() {
        int result = super.hashCode();
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
        return true;
    }
}
