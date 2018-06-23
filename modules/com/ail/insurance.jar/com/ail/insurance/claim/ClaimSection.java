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

package com.ail.insurance.claim;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;

@Audited
@Entity
@TypeDefinition
public class ClaimSection extends Type {
    private static final long serialVersionUID = -209668224910237416L;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="claimUIDcla")
    private Claim claim;

    @OneToMany(cascade = { ALL }, mappedBy = "claimSection")
    private List<ClaimRecovery> claimRecoveries = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "estimatedReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "estimatedReserveCurrency"))
    })
    private CurrencyAmount estimatedReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "totalRecoveredAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "totalRecoveredCurrency"))
    })
    private CurrencyAmount totalRecovered;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "outstandingClaimAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "outstandingClaimCurrency"))
    })
    private CurrencyAmount outstandingClaim;

    /**
     * Default constructor
     */
    public ClaimSection() {
    }

    public CurrencyAmount getEstimatedReserve() {
        return estimatedReserve;
    }

    public void setEstimatedReserve(CurrencyAmount estimatedReserve) {
        this.estimatedReserve = estimatedReserve;
    }

    public CurrencyAmount getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(CurrencyAmount totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public CurrencyAmount getOutstandingClaim() {
        return outstandingClaim;
    }

    public void setOutstandingClaim(CurrencyAmount outstandingClaim) {
        this.outstandingClaim = outstandingClaim;
    }

    public List<ClaimRecovery> getRecoveries() {
        if (claimRecoveries == null) {
            claimRecoveries = new ArrayList<>();
        }

        return claimRecoveries;
    }

    public void setRecoveries(List<ClaimRecovery> claimRecoveries) {
        this.claimRecoveries = claimRecoveries;
    }

    public void addRecovery(ClaimRecovery claimRecovery) {
        claimRecoveries.add(claimRecovery);
    }
}
