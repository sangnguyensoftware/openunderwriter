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

import static org.hibernate.annotations.CascadeType.DETACH;
import static org.hibernate.annotations.CascadeType.MERGE;
import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.REFRESH;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.CanMerge;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.Identified;
import com.ail.core.Type;

@TypeDefinition
@Audited
@Entity
public class PartyRole extends Type implements Identified, CanMerge {
    @Column(nullable = false)
    private String role;

    @ManyToOne()
    @JoinColumn(name = "proPartyUIDpar", nullable = false)
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    private Party party;

    @Column(name = "proStartDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date startDate;

    @Column(name = "proEndDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date endDate;

    /**
     * This constructor only exists for use by frameworks. Use
     * {@link #PartyRole(String, Party) or
     * {@link #PartyRole(String, Party, Date, Date)} instead.
     */
    protected PartyRole() {
    }

    /**
     * @deprecated This is for use by Castor only. Use {@link #PartyRole(String, Party) or
     * {@link #PartyRole(String, Party, Date, Date)} instead.
     */
    @Deprecated
    protected PartyRole(String role, Date startDate, Date endDate) {
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PartyRole(String role, Party party, Date startDate, Date endDate) {
        if (Functions.isEmpty(role)) {
            throw new IllegalStateException("PartyRole cannot be constructed with a null role.");
        }

        if (party == null) {
            throw new IllegalStateException("PartyRole cannot be constructed with a null party.");
        }

        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
        this.party = party;
    }

    public PartyRole(String role, Party party) {
        this(role, party, null, null);
    }

    /**
     * The type of party role. This is simply a text definition, the PartyRole model gives it no
     * special meaning. It is up to the user of PartyRole to ensure consistency. The recommendation
     * is to use Labels for this purpose.
     * @return Party role type
     */
    public String getRole() {
        return role;
    }

    /**
     * Start date of the party role. A value of <code>null</code> is taken to mean 'from the beginning of time'.
     * @return start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * End date of this party role. A value of <code>null<code> is taken to mean 'until the end of time'.
     * @return end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * End date this party role with the specified date.
     * <p>
     * <b>The end date is always set to 1ms before <code>endDate</code></b>. This
     * ensured that subsequent calls made within the same millisecond find the
     * record deleted.
     * </p>
     *
     * @see #endDate()
     * @param endDate
     */
    public void endDate(Date endDate) {
        this.endDate = new Date(endDate.getTime() - 1);
    }

    /**
     * @see #endDate(Date)
     * End date this party role using the current date & time.
     */
    public void endDate() {
        endDate(new Date());
    }

    /**
     * Get the party associated with this party role. This may in some circumstances be null
     * @return
     */
    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        if (party != null) {
            throw new IllegalStateException("setParty() can only be used to set the party once. It cannot be used to change an existing party association.");
        }
        this.party = party;
    }

    @Override
    public String getId() {
        if (getSystemId() != NOT_PERSISTED) {
            return Long.toString(getSystemId());
        }
        else {
            final int prime = 3;
            int result = 1;
            if (party!=null) {
                if (party.getId() != null) {
                    result = prime * result + party.getId().hashCode();
                }
                else if (party.getPartyId() != null) {
                    result = prime * result + party.getPartyId().hashCode();
                }
            }
            result = prime * result + ((role == null) ? 0 : role.hashCode());
            return Integer.toString(result);
        }
    }

    @Override
    public void setId(String Id) {
        // Ignore this. IDs for party roles are are not persisted they are calculated.
    }

    @Override
    public boolean compareById(Identified that) {
        return this.getId().equals(that.getId());
    }

    @Override
    public void mergeWithDataFrom(Type donor, Core core) {
        PartyRole donorPartyRole = (PartyRole)donor;

        if (startDate==null) {
            startDate = donorPartyRole.startDate;
        }

        if (endDate == null) {
            endDate = donorPartyRole.endDate;
        }

        party.mergeWithDataFrom(donorPartyRole.party, core);
    }

    @Override
    public int hashCode() {
        final int prime = 3;
        int result = 1;
        result = prime * result + ((party == null) ? 0 : party.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
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
        PartyRole other = (PartyRole) obj;
        if (party == null) {
            if (other.party != null)
                return false;
        } else if (!party.equals(other.party))
            return false;
        if (role == null) {
            if (other.role != null)
                return false;
        } else if (!role.equals(other.role))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartyRole [type=" + role + ", party=" + party + ", getSystemId()=" + getSystemId() + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new PartyRole(role, (Party)party.clone(), startDate, endDate);
    }
}
