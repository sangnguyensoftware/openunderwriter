/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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
package com.ail.service.integrator.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class PolicySummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private long systemId;
    private String externalSystemId;
    private String policyNumber;
    private String quotationNumber;
    private String clientName;
    private String quoteDate;
    private String inceptionDate;
    private String expiryDate;
    private String product;
    private String premium;
    private String status;
    private Date createdDate;
    private Date updatedDate;
    private Map<String,String> pageFlowPageMap;
    private Long owningUserId;
    private String owningUserName;

    public PolicySummary(long systemId, String externalSystemId, Date createdDate, Date updatedDate, String policyNumber,
                        String quotationNumber, String clientName, String quoteDate, String inceptionDate, String expiryDate,
                        String product, String premium, String status, Map<String,String> pageFlowPageMap, Long owningUserId, String owningUserName) {
        this.systemId = systemId;
        this.externalSystemId = externalSystemId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.policyNumber = policyNumber;
        this.quotationNumber = quotationNumber;
        this.clientName = clientName;
        this.quoteDate = quoteDate;
        this.inceptionDate = inceptionDate;
        this.expiryDate = expiryDate;
        this.product = product;
        this.premium = premium;
        this.status = status;
        this.pageFlowPageMap = pageFlowPageMap;
        this.owningUserId = owningUserId;
        this.owningUserName = owningUserName;
    }

    public long getSystemId() {
        return systemId;
    }

    public String getExternalSystemId() {
        return externalSystemId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getQuotationNumber() {
        return quotationNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getQuoteDate() {
        return quoteDate;
    }

    public String getInceptionDate() {
        return inceptionDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getProduct() {
        return product;
    }

    public String getPremium() {
        return premium;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Long getOwningUserId() {
        return owningUserId;
    }

    public String getOwningUserName() {
        return owningUserName;
    }

    public Map<String,String> getPageFlowPageMap() {
        return pageFlowPageMap;
    }

    @Override
    public String toString() {
        return "PolicySummary [systemId=" + systemId + ", externalSystemId=" + externalSystemId + ", policyNumber=" + policyNumber + ", quotationNumber=" + quotationNumber + ", clientName="
                + clientName + ", quoteDate=" + quoteDate + ", inceptionDate=" + inceptionDate + ", expiryDate=" + expiryDate + ", product=" + product + ", premium=" + premium + ", status=" + status
                + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + ", pageFlowPageMap=" + pageFlowPageMap + ", owningUserId=" + owningUserId + ", owningUserName=" + owningUserName
                + "]";
    }
}
