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
package com.ail.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ail.insurance.policy.AssessmentNote;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Marker;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;

public class NewBusinessReferralAssetRendererData {
    private long systemId;
    private String policyNumber;
    private Date quotationDate;
    private String quotationNumber;
    private String productName;
    private String clientLegalName;
    private String status;
    private String username;
    private List<String> markerReasons=new ArrayList<>();
    private List<String> noteReasons=new ArrayList<>();

    public NewBusinessReferralAssetRendererData(Policy policy) {
        this.systemId = policy.getSystemId();
        this.policyNumber = policy.getPolicyNumber();
        this.quotationDate = policy.getQuotationDate();
        this.quotationNumber = policy.getQuotationNumber();
        this.productName = policy.getProductName();
        this.status = policy.getStatus().toString();
        this.username = findUsernameByUserId(policy.getOwningUser());
        this.clientLegalName = policy.getClient() != null ? policy.getClient().getLegalName() : null;

        if (policy.isAggregator()) {
            for (String sheetName : policy.getAssessmentSheetList().keySet()) {
                markerReasons.addAll(markerReasonsFrom(policy.getAssessmentSheetFor(sheetName)));
                noteReasons.addAll(markerReasonsFrom(policy.getAssessmentSheetFor(sheetName)));
            }
        } else {
            markerReasons.addAll(markerReasonsFrom(policy.getAssessmentSheet()));
            noteReasons.addAll(noteReasonsFrom(policy.getAssessmentSheet()));

            for(Section section: policy.getSection()) {
                markerReasons.addAll(markerReasonsFrom(section.getAssessmentSheet()));
                noteReasons.addAll(noteReasonsFrom(section.getAssessmentSheet()));
            }
        }
    }

    private String findUsernameByUserId(Long owningUser) {

        if (owningUser == null) {
            return null;
        }
        try {
            return UserLocalServiceUtil.getUser(owningUser).getScreenName();
        } catch (PortalException | SystemException e) {
            return null;
        }
    }

    public long getSystemId() {
        return systemId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public Date getQuotationDate() {
        return quotationDate;
    }

    public String getQuotationNumber() {
        return quotationNumber;
    }

    public String getProductName() {
        return productName;
    }

    public String getClientLegalName() {
        return clientLegalName;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getMarkerReasons() {
        return markerReasons;
    }

    public List<String> getNoteReasons() {
        return noteReasons;
    }

    private Collection<? extends String> noteReasonsFrom(AssessmentSheet assessmentSheet) {
        List<String> reasons = new ArrayList<>();

        if (assessmentSheet!=null) {
            for(AssessmentNote note: assessmentSheet.noteLines()) {
                reasons.add(note.getReason());
            }
        }

        return reasons;
    }

    private Collection<? extends String> markerReasonsFrom(AssessmentSheet assessmentSheet) {
        List<String> reasons = new ArrayList<>();

        if (assessmentSheet!=null) {
            for(Marker marker: assessmentSheet.markerLines()) {
                reasons.add(marker.getReason());
            }
        }

        return reasons;
    }
}
