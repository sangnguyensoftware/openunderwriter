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

@TypeDefinition(prefix="prl")
@Audited
@Entity(name = "com.ail.party.PartyRelationship") // 'name' required because we're using prefix in @TypeDefinition(). See https://hibernate.atlassian.net/browse/HHH-4312.
public class PartyRelationship extends Type implements Identified, CanMerge {
    @Column(nullable = false)
    private String relationship;

    @ManyToOne()
    @JoinColumn(name = "prlPartyUIDpar", nullable = false)
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    private Party party;

    @Column(name = "prlStartDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date startDate;

    @Column(name = "prlEndDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date endDate;

    /**
     * This constructor only exists for use by frameworks. Use
     * {@link #PartyRelationship(String, Party) or
     * {@link #PartyRelationship(String, Party, Date, Date)} instead.
     */
    protected PartyRelationship() {
    }

    /**
     * @deprecated This is for use by Castor only. Use {@link #PartyRelationship(String, Party) or
     * {@link #PartyRelationship(String, Party, Date, Date)} instead.
     */
    @Deprecated
    protected PartyRelationship(String relationship, Date startDate, Date endDate) {
        this.relationship = relationship;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PartyRelationship(String relationship, Party party, Date startDate, Date endDate) {
        if (Functions.isEmpty(relationship)) {
            throw new IllegalStateException("PartyRelationship cannot be constructed with a null role.");
        }

        if (party == null) {
            throw new IllegalStateException("PartyRelationship cannot be constructed with a null party.");
        }

        this.relationship = relationship;
        this.startDate = startDate;
        this.endDate = endDate;
        this.party = party;
    }

    public PartyRelationship(String relationship, Party party) {
        this(relationship, party, null, null);
    }

    /**
     * The type of party role. This is simply a text definition, the PartyRole model gives it no
     * special meaning. It is up to the user of PartyRole to ensure consistency. The recommendation
     * is to use Labels for this purpose.
     * @return Party role type
     */
    public String getRelationship() {
        return relationship;
    }

    /**
     * Start date of the party relationship. A value of <code>null</code> is taken to mean 'from the beginning of time'.
     * @return start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * End date of this party relationship. A value of <code>null<code> is taken to mean 'until the end of time'.
     * @return end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * End date this party relationship with the specified date.
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
     * End date this party relationship using the current date & time.
     */
    public void endDate() {
        endDate(new Date());
    }

    /**
     * Get the party associated with this party relationship. This may in some circumstances be null
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
            result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
            return Integer.toString(result);
        }
    }

    @Override
    public void setId(String Id) {
        // Ignore this. IDs for party relationships are are not persisted they are calculated.
    }

    @Override
    public boolean compareById(Identified that) {
        return this.getId().equals(that.getId());
    }

    @Override
    public void mergeWithDataFrom(Type donor, Core core) {
        PartyRelationship donorPartyRelationship = (PartyRelationship)donor;

        if (startDate==null) {
            startDate = donorPartyRelationship.startDate;
        }

        if (endDate == null) {
            endDate = donorPartyRelationship.endDate;
        }

        party.mergeWithDataFrom(donorPartyRelationship.party, core);
    }

    @Override
    public int hashCode() {
        final int prime = 3;
        int result = 1;
        result = prime * result + ((party == null) ? 0 : party.hashCode());
        result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
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
        PartyRelationship other = (PartyRelationship) obj;
        if (party == null) {
            if (other.party != null)
                return false;
        } else if (!party.equals(other.party))
            return false;
        if (relationship == null) {
            if (other.relationship != null)
                return false;
        } else if (!relationship.equals(other.relationship))
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
        return "PartyRelationship [relationship=" + relationship + ", party=" + party + ", getSystemId()=" + getSystemId() + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new PartyRelationship(relationship, (Party)party.clone(), startDate, endDate);
    }
}
