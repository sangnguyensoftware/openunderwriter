package com.ail.workflow;

import java.util.Locale;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.ail.insurance.policy.Policy;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.BaseAssetRenderer;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;

public class NewBusinessReferralAssetRenderer extends BaseAssetRenderer {

    private NewBusinessReferralAssetRendererData rendererData;

    public NewBusinessReferralAssetRenderer(NewBusinessReferralAssetRendererData data) {
        this.rendererData = data;
    }

    @Override
    public String getClassName() {
        return Policy.class.getName();
    }

    @Override
    public long getClassPK() {
        return rendererData.getSystemId();
    }

    @Override
    public long getGroupId() {
        return 0;
    }

    @Override
    public String getSummary(Locale locale) {
        return getTitle(locale);
    }

    @Override
    public String getTitle(Locale locale) {
        StringBuffer summary = new StringBuffer();

        if (rendererData.getPolicyNumber() != null) {
            summary.append(rendererData.getPolicyNumber());
        } else if (rendererData.getQuotationDate() != null) {
            summary.append(rendererData.getQuotationNumber());
        }

        summary.append(", ").append(rendererData.getProductName());

        if (rendererData.getClientLegalName() != null && rendererData.getClientLegalName().length() > 0) {
            summary.append(", ").append(rendererData.getClientLegalName());
        }

        summary.append(", ").append(rendererData.getStatus());

        return summary.toString();
    }

    @Override
    public long getUserId() {
        return 0;
    }

    @Override
    public String getUserName() {
        return rendererData.getUsername();
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse, String arg2) throws Exception {

        renderRequest.setAttribute("rendererData", rendererData);
        renderRequest.setAttribute("referralWorkflowState", getReferralWorkflowState(renderRequest));

        return "/html/viewReferralDetail.jsp";
    }

    private String getReferralWorkflowState(RenderRequest renderRequest) throws SystemException, WorkflowException, PortalException {

        AssetEntry entry = AssetEntryLocalServiceUtil.fetchEntry(NewBusinessReferral.class.getName(), rendererData.getSystemId());
        WorkflowTask workflowTask = WorkflowTaskManagerUtil.getWorkflowTask(entry.getCompanyId(), ParamUtil.getLong(renderRequest, "workflowTaskId"));

        String referralWorkflowState = "";

        long taskAssigneeId = workflowTask.getAssigneeUserId();
        User currentUser = PortalUtil.getUser(renderRequest);

        if (taskAssigneeId > 0 && currentUser != null) {
            referralWorkflowState += "ASSIGNED";
            if (taskAssigneeId == currentUser.getUserId()) {
                referralWorkflowState += "_TO_ME";
            }
        } else {
            referralWorkflowState = "UNASSIGNED";
        }

        if (workflowTask.isCompleted()) {
            referralWorkflowState += "_CLOSED";
        } else {
            referralWorkflowState += "_OPEN";
        }

        return referralWorkflowState;
    }

}
