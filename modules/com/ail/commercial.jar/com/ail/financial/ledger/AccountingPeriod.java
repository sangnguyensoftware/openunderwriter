/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

package com.ail.financial.ledger;

import static com.ail.financial.ledger.AccountingPeriodStatus.CLOSED;
import static com.ail.financial.ledger.AccountingPeriodStatus.OPEN;
import static javax.persistence.EnumType.STRING;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.core.Type;

@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.accountingperiods", query = "select ape from AccountingPeriod ape"),
    @NamedQuery(name = "get.accountingperiod.by.systemId", query = "select ape from AccountingPeriod ape where ape.systemId = ?"),
    @NamedQuery(name = "get.accountingperiod.by.externalSystemId", query = "select ape from AccountingPeriod ape where ape.externalSystemId = ?"),
    @NamedQuery(name = "get.accountingperiod.for.date", query = "select ape from AccountingPeriod ape where ? between ape.startDate and ape.endDate"),
    @NamedQuery(name = "get.number.of.open.accountingperiods.before", query = "select count(ape) from AccountingPeriod ape where ape.startDate < ? and ape.status='OPEN'")

})
public class AccountingPeriod extends Type {

    @Column(name="apeStartDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date startDate;

    @Column(name="apeEndDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date endDate;

    @Enumerated(STRING)
    private AccountingPeriodStatus status;

    AccountingPeriod() {
    }

    public AccountingPeriod(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = OPEN;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void closePeriod() {
        this.status = CLOSED;
    }

    public AccountingPeriodStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        AccountingPeriod other = (AccountingPeriod) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (status != other.status)
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "AccountingPeriod [startDate=" + startDate + ", endDate=" + endDate + ", status=" + status + ", getSystemId()=" + getSystemId() + "]";
    }
}
