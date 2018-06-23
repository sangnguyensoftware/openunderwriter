/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchCriteria implements Serializable {


    private static final long serialVersionUID = 1L;

    private String policyId = "";

    private String product = "";
    private List<String> status = new ArrayList<>();

    private NullableDate quoteDatePeriodStart = new NullableDate();
    private NullableDate quoteDatePeriodEnd = new NullableDate();

    private NullableDate createdDatePeriodStart = new NullableDate();
    private NullableDate createdDatePeriodEnd = new NullableDate();

    private NullableDate inceptionDatePeriodStart = new NullableDate();
    private NullableDate inceptionDatePeriodEnd = new NullableDate();

    private NullableDate expiryDatePeriodStart = new NullableDate();
    private NullableDate expiryDatePeriodEnd = new NullableDate();

    private String clientId = "";
    private String clientName = "";
    private String clientAddress = "";
    private String clientEmailAddress = "";

    private Boolean includeTest = false;

    private Boolean isSuperseded = false;

    private Long userId = new Long(-1);

    private Long companyId = new Long(-1);

    private String orderBy = "";
    private String orderDirection = "";

    private int cachedResults = 0;

    public AdvancedSearchCriteria() {
    }

    public AdvancedSearchCriteria(
            String policyId,
            Long userId,
            Long companyId) {
        this.policyId = policyId;
        this.userId = userId;
        this.companyId = companyId;

    }

    public AdvancedSearchCriteria(
            String policyId,
            String product,
            List<String> status,
            NullableDate createdDatePeriodStart,
            NullableDate createdDatePeriodEnd,
            NullableDate quoteDatePeriodStart,
            NullableDate quoteDatePeriodEnd,
            NullableDate inceptionDatePeriodStart,
            NullableDate inceptionDatePeriodEnd,
            NullableDate expiryDatePeriodStart,
            NullableDate expiryDatePeriodEnd,
            String clientId,
            String clientName,
            String clientAddress,
            String clientEmailAddress,
            Long userId,
            Long companyId,
            Boolean includeTest,
            Boolean includeSuperseded,
            String orderBy,
            String orderDirection) {
        this.policyId = policyId;
        this.product = product;
        this.status = status;
        this.createdDatePeriodStart = createdDatePeriodStart;
        this.createdDatePeriodEnd = createdDatePeriodEnd;
        this.quoteDatePeriodStart = quoteDatePeriodStart;
        this.quoteDatePeriodEnd = quoteDatePeriodEnd;
        this.inceptionDatePeriodStart = inceptionDatePeriodStart;
        this.inceptionDatePeriodEnd = inceptionDatePeriodEnd;
        this.expiryDatePeriodStart = expiryDatePeriodStart;
        this.expiryDatePeriodEnd = expiryDatePeriodEnd;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.clientEmailAddress = clientEmailAddress;
        this.userId = userId;
        this.companyId = companyId;
        this.includeTest = includeTest;
        this.isSuperseded = includeSuperseded;
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIncludeTest(boolean includeTest) {
        this.includeTest = includeTest;
    }

    public String getProduct() {
        return product;
    }

    public List<String> getStatus() {
        return status;
    }

    public NullableDate getCreatedDatePeriodStart() {
        return createdDatePeriodStart;
    }

    public NullableDate getCreatedDatePeriodEnd() {
        return createdDatePeriodEnd;
    }

    public NullableDate getQuoteDatePeriodStart() {
        return quoteDatePeriodStart;
    }

    public NullableDate getQuoteDatePeriodEnd() {
        return quoteDatePeriodEnd;
    }

    public NullableDate getInceptionDatePeriodStart() {
        return inceptionDatePeriodStart;
    }

    public NullableDate getInceptionDatePeriodEnd() {
        return inceptionDatePeriodEnd;
    }

    public NullableDate getExpiryDatePeriodStart() {
        return expiryDatePeriodStart;
    }

    public NullableDate getExpiryDatePeriodEnd() {
        return expiryDatePeriodEnd;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getClientEmailAddress() {
        return clientEmailAddress;
    }

    public Boolean isIncludeTest() {
        return includeTest;
    }

    public Boolean isIncludeSuperseded() {
        return isSuperseded ;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getClientId() {
        return clientId;
    }

    public int getCachedResults() {
        return cachedResults;
    }

    public void setCachedResults(int cachedResults) {
        this.cachedResults = cachedResults;
    }


}
