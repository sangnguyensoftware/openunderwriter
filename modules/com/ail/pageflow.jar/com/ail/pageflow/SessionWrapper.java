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
package com.ail.pageflow;

import com.ail.core.context.SessionAdaptor;

/**
 * This class encapsulates access to the portlet session attributes used by the pageflow context.
 * Each attribute has it's own getter/setter method pair. Having them here helps keep the calling
 * code a little cleaner and also centralises the names used for each one.
 */
public class SessionWrapper {
    public static final String PAGEFLOW_NAME_SESSION_ATTRIBUTE = "pageflow-name";
    public static final String CURRENT_PAGE_NAME_ATTRIBUTE = "current-page";
    public static final String NEXT_PAGE_NAME_ATTRIBUTE = "next-page";
    public static final String PAGEFLOW_ATTRIBUTE = "pageflow";
    public static final String POLICY_SYSTEM_ID_ATTRIBUTE = "policy-system-id";
    public static final String PARTY_SYSTEM_ID_ATTRIBUTE = "party-system-id";
    public static final String CLAIM_SYSTEM_ID_ATTRIBUTE = "claim-system-id";
    public static final String PAGEFLOW_INITIALISED_ATTRIBUTE = "pageflow-initialised";
    public static final String VERSION = "version";

    private SessionAdaptor attributeHandler;

    public SessionWrapper(SessionAdaptor attributeHandler) {
        this.attributeHandler = attributeHandler;
    }

    String getPageFlowName() {
        return attributeHandler.get(PAGEFLOW_NAME_SESSION_ATTRIBUTE, String.class);
    }

    String getVersion() {
        return attributeHandler.get(VERSION, String.class);
    }

    void setPageFlowName(String pageFlow) {
        attributeHandler.set(PAGEFLOW_NAME_SESSION_ATTRIBUTE, pageFlow);
    }

    void setVersion(String version) {
        attributeHandler.set(VERSION, version);
    }

    public void clear() {
        attributeHandler.set(PAGEFLOW_NAME_SESSION_ATTRIBUTE, null);
        attributeHandler.set(CURRENT_PAGE_NAME_ATTRIBUTE, null);
        attributeHandler.set(NEXT_PAGE_NAME_ATTRIBUTE, null);
        attributeHandler.set(PAGEFLOW_ATTRIBUTE, null);
        attributeHandler.set(POLICY_SYSTEM_ID_ATTRIBUTE, null);
        attributeHandler.set(PARTY_SYSTEM_ID_ATTRIBUTE, null);
        attributeHandler.set(CLAIM_SYSTEM_ID_ATTRIBUTE, null);
        attributeHandler.set(PAGEFLOW_INITIALISED_ATTRIBUTE, null);
        attributeHandler.set(VERSION, null);
    }

    String getCurrentPageName() {
        return attributeHandler.get(CURRENT_PAGE_NAME_ATTRIBUTE, String.class);
    }

    void setCurrentPageName(String pageName) {
        attributeHandler.set(CURRENT_PAGE_NAME_ATTRIBUTE, pageName);
    }

    String getNextPageName() {
        return attributeHandler.get(NEXT_PAGE_NAME_ATTRIBUTE, String.class);
    }

    void setNextPageName(String pageName) {
        attributeHandler.set(NEXT_PAGE_NAME_ATTRIBUTE, pageName);
    }

    PageFlow getPageFlow() {
        return attributeHandler.get(PAGEFLOW_ATTRIBUTE, PageFlow.class);
    }

    void setPageFlow(PageFlow pageFlow) {
        attributeHandler.set(PAGEFLOW_ATTRIBUTE, pageFlow);
    }

    Long getPolicySystemId() {
        return attributeHandler.get(POLICY_SYSTEM_ID_ATTRIBUTE, Long.class);
    }

    void setPolicySystemId(Long policySystemId) {
        attributeHandler.set(POLICY_SYSTEM_ID_ATTRIBUTE, policySystemId);
    }

    Long getPartySystemId() {
        return attributeHandler.get(PARTY_SYSTEM_ID_ATTRIBUTE, Long.class);
    }

    void setPartySystemId(Long partySystemId) {
        attributeHandler.set(PARTY_SYSTEM_ID_ATTRIBUTE, partySystemId);
    }

    Long getClaimSystemId() {
        return attributeHandler.get(CLAIM_SYSTEM_ID_ATTRIBUTE, Long.class);
    }

    void setClaimSystemId(Long claimSystemId) {
        attributeHandler.set(CLAIM_SYSTEM_ID_ATTRIBUTE, claimSystemId);
    }

    Boolean isPageFlowInitialised() {
        Boolean state = attributeHandler.get(PAGEFLOW_INITIALISED_ATTRIBUTE, Boolean.class);
        return state==null ? false : state;
    }

    void setPageFlowInitialised(Boolean initialised) {
        attributeHandler.set(PAGEFLOW_INITIALISED_ATTRIBUTE, initialised);
    }
}