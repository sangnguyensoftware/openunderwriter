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

package com.ail.insurance.policy;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Identified;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;

/**
 * Coverages define or constrain the types of cover offered by a Section. Typically, a section will contain a collection
 * of coverages. Coverages may exist within a section but not be in effect ('enabled' indicator). They may also be
 * optional - selected at the policy holders discretion ('optional' flag).
 */
@TypeDefinition
@Audited
@Entity
public class Coverage extends Type implements Identified {
    static final long serialVersionUID = 7326823306523810654L;

    @Column(name = "covId" ) // Looks odd? See OU-1064
    private String ref;

    private String coverageTypeId;

    @Column(columnDefinition = "BIT")
    private boolean enabled=false;

    @Column(columnDefinition = "BIT")
    private boolean optional=false;

    private String name;

    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "limitAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "limitCurrency"))
    })
    private CurrencyAmount limit;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "deductibleAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "deductibleCurrency"))
    })
    private CurrencyAmount deductible;

    private Date effectiveDate;
    private Date expiryDate;

    @OneToOne(cascade = { DETACH, MERGE, PERSIST, REFRESH })
    @JoinColumn(name = "brokerUIDpar", referencedColumnName = "UID")
    private Broker broker;

    public Coverage() {
    }

    public Coverage(String id, CurrencyAmount limit, CurrencyAmount deductible) {
        this.ref=id;
        this.limit=limit;
        this.deductible=deductible;
    }

    @Override
    public String getId() {
        return ref; // Looks odd? See OU-1064
    }

    public String getRef() {
        return getId();
    }

    @Override
    public void setId(String id) {
        this.ref = id; // Looks odd? See OU-1064
    }

    public void setRef(String ref) {
        setId(ref);
    }

    public CurrencyAmount getDeductible() {
        return deductible;
    }

    public void setDeductible(CurrencyAmount deductible) {
        this.deductible = deductible;
    }

    public CurrencyAmount getLimit() {
        return limit;
    }

    public void setLimit(CurrencyAmount limit) {
        this.limit = limit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public boolean compareById(Identified that) {
        if (that.getId() != null && this.getId() != null && this.getClass().isAssignableFrom(that.getClass())) {
            return (this.ref.equals(that.getId()));
        } else {
            return false;
        }
    }

    public String getCoverageTypeId() {
        return coverageTypeId;
    }

    public void setCoverageTypeId(String coverageTypeId) {
        this.coverageTypeId = coverageTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deductible == null) ? 0 : deductible.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        result = prime * result + ((limit == null) ? 0 : limit.hashCode());
        result = prime * result + (optional ? 1231 : 1237);
        result = prime * result + ((getAttribute() == null) ? 0 : getAttribute().hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
        result = prime * result + ((coverageTypeId == null) ? 0 : coverageTypeId.hashCode());
        result = prime * result + ((broker == null) ? 0 : broker.hashCode());
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
        Coverage other = (Coverage) obj;
        if (deductible == null) {
            if (other.deductible != null)
                return false;
        } else if (!deductible.equals(other.deductible))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (enabled != other.enabled)
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        if (limit == null) {
            if (other.limit != null)
                return false;
        } else if (!limit.equals(other.limit))
            return false;
        if (optional != other.optional)
            return false;
        if (getAttribute() == null) {
            if (other.getAttribute() != null)
                return false;
        } else if (!getAttribute().equals(other.getAttribute()))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (effectiveDate == null) {
            if (other.effectiveDate != null)
                return false;
        } else if (!effectiveDate.equals(other.effectiveDate))
            return false;
        if (expiryDate == null) {
            if (other.expiryDate != null)
                return false;
        } else if (!expiryDate.equals(other.expiryDate))
            return false;
        if (coverageTypeId == null) {
            if (other.coverageTypeId != null)
                return false;
        } else if (!coverageTypeId.equals(other.coverageTypeId))
            return false;
        if (broker == null) {
            if (other.broker != null)
                return false;
        } else if (!broker.equals(other.broker))
            return false;

        return true;
    }
}
