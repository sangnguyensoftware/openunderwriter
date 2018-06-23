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

import static javax.persistence.DiscriminatorType.STRING;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

@TypeDefinition
@Audited
@Entity
@DiscriminatorColumn(name = "csyDSC", discriminatorType = STRING)
@DiscriminatorValue("Method")
public abstract class ContactSystem extends Type {
    static final long serialVersionUID = 6426298757382775043L;

    public static final String CONTACT_SYSTEM_TYPE_LABEL_DISCRIMINATOR = "contact_system_types";

    @Column(columnDefinition = "BIT")
    private boolean primary;

    private String type;

    @Column(name = "csyStartDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date startDate;

    @Column(name = "csyEndDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date endDate;

    private String fullAddress; // Hibernate persists this - don't delete it!

    public ContactSystem() {
        super();
    }

    public ContactSystem(boolean primary, Date startDate, Date endDate, String type) {
        this();
        this.primary = primary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * End date this contact system with the specified date.
     * <p>
     * <b>The end date is always set to 1ms before <code>endDate</code></b>. This
     * ensures that subsequent calls made within the same millisecond find the
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
     * End date this contact system using the current date & time.
     */
    public void endDate() {
        endDate(new Date());
    }

    /**
     * Returns a representation of the contact system determined by the
     * implementation class. For a postal address this will be a concatenation of
     * address elements, for a phone number it would be just the text of the number.
     *
     * @return
     */
    public String getFullAddress() {
        return fullAddress;
    }

    protected void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    /**
     * Return true if <code>this</code> represents the same contact system and
     * details as <code>that</code>. This is somewhat similar to what
     * <code>equals()<code> would normally does, but this method returns true if
     * <code>this</code> and <code>that</code> represent the same 'address', it
     * takes no account of other properties on the objects. So, for example, if
     * <code>this</code> and <code>that<code> are both phone numbers and both
     * contain the same number - even if the formats are different - the method
     * would return true.
     *
     * @param that The object to compare to.
     * @return true if <code>this</code> and <code>that</code> represent the same
     *         contact address; false otherwise.
     */
    abstract public boolean isSameContactAs(ContactSystem that);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + (primary ? 1231 : 1237);
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        ContactSystem other = (ContactSystem) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (primary != other.primary)
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return super.typeEquals(obj);
    }
}
