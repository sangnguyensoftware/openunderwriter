/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
package com.ail.ui.shared.model;

import java.io.Serializable;
import java.util.List;

public class PolicyDetailDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    private long systemId;

    private String externalRef;

    private String policyNumber;

    private String quotationNumber;

    private String clientName;

    private List<String> clientAddress;

    private String email;

    private String phone;

    private String quoteDate;

    private String inceptionDate;

    private String expiryDate;

    private String createdDate;

    private String product;

    private String premium;

    private String status;

    private boolean isDetail;

    private String clientId;

    private String mtaIndex;

    private String renewalIndex;

    private long supersededBy;


    public PolicyDetailDTO() {
        systemId = -1;
        isDetail = false;
    }

    public PolicyDetailDTO(long systemId,
                            String externalRef,
                            String policyNumber,
                            String quotationNumber,
                            String clientName,
                            List<String> clientAddress,
                            String email,
                            String phone,
                            String quoteDate,
                            String inceptionDate,
                            String expiryDate,
                            String product,
                            String premium,
                            String status,
                            String clientId,
                            String renewalIndex,
                            String mtaIndex,
                            long supersededBy) {

        this.systemId = systemId;
        this.externalRef = externalRef;
        this.policyNumber = policyNumber;
        this.quotationNumber = quotationNumber;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.email = email;
        this.phone = phone;
        this.quoteDate = quoteDate;
        this.inceptionDate = inceptionDate;
        this.expiryDate = expiryDate;
        this.product = product;
        this.premium = premium;
        this.status = status;
        this.clientId = clientId;
        this.isDetail = true;
        this.renewalIndex = renewalIndex;
        this.mtaIndex = mtaIndex;
        this.supersededBy = supersededBy;
    }

    public PolicyDetailDTO(long systemId,
                            String policyNumber,
                            String quotationNumber,
                            String clientName,
                            List<String> clientAddress,
                            String status,
                            String product,
                            String createdDate,
                            String inceptionDate,
                            String expiryDate,
                            String renewalIndex,
                            String mtaIndex,
                            long supersededBy) {

        this.systemId = systemId;
        this.policyNumber = policyNumber;
        this.quotationNumber = quotationNumber;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.status = status;
        this.product = product;
        this.createdDate = createdDate;
        this.inceptionDate = inceptionDate;
        this.expiryDate = expiryDate;
        this.isDetail = false;
        this.renewalIndex = renewalIndex;
        this.mtaIndex = mtaIndex;
        this.supersededBy = supersededBy;
    }

    public long getSystemId() {
        return systemId;
    }

    public String getExternalRef() {
        return externalRef;
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

    public List<String> getClientAddress() {
        return clientAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
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
    public String getCreatedDate() {
        return createdDate;
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

    public String getClientId() {
        return this.clientId;
    }

    public String getMtaIndex() {
        return mtaIndex;
    }

    public String getRenewalIndex() {
        return renewalIndex;
    }

    public long getSupersededBy() {
        return supersededBy;
    }

    @Override
    public String toString() {
        return "PolicyDetailDTO [systemId=" + systemId + ", externalRef=" + externalRef + ", policyNumber=" + policyNumber + ", quotationNumber=" + quotationNumber + ", clientName=" + clientName
                + ", clientAddress=" + clientAddress + ", email=" + email + ", phone=" + phone + ", quoteDate=" + quoteDate + ", inceptionDate=" + inceptionDate + ", expiryDate=" + expiryDate
                + ", createdDate=" + createdDate + ", product=" + product + ", premium=" + premium + ", status=" + status + ", isDetail=" + isDetail + ", clientId=" + clientId + ", mtaIndex="
                + mtaIndex + ", renewalIndex=" + renewalIndex + ", supersededBy=" + supersededBy + "]";
    }




}
