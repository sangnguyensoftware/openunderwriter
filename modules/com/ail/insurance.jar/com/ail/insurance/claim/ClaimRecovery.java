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
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;
import com.ail.party.Party;

@Audited
@Entity
@TypeDefinition
public class ClaimRecovery extends Type {
    private static final long serialVersionUID = -5789050829918007998L;

    private String reason;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "recoveredFromUIDpar", referencedColumnName = "UID")
    private Party recoveredFrom;

    @Enumerated(STRING)
    private RecoveryType recoveryType;

    @Enumerated(STRING)
    private PaymentType paymentType;

    private Date recoveredDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),  @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private CurrencyAmount amount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="claimSectionUIDcse")
    private ClaimSection claimSection;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Party getRecoveredFrom() {
        return recoveredFrom;
    }

    public void setRecoveredFrom(Party recoveredFrom) {
        this.recoveredFrom = recoveredFrom;
    }

    public RecoveryType getRecoveryType() {
        return recoveryType;
    }

    public void setRecoveryType(RecoveryType recoveryType) {
        this.recoveryType = recoveryType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Date getRecoveredDate() {
        return recoveredDate;
    }

    public void setRecoveredDate(Date recoveredDate) {
        this.recoveredDate = recoveredDate;
    }

    public void setAmount(CurrencyAmount amount) {
        this.amount = amount;
    }

    public CurrencyAmount getAmount() {
        return this.amount;
    }
}
