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

package com.ail.workflow;

import java.util.Date;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;

@ServiceImplementation
public class CreateNewBusinessReferralWorkflowService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {

        if (PageFlowContext.getPageFlow() == null) {
            throw new PreconditionException("PageFlowContext.getPageFlow() == null");
        }

        if (PageFlowContext.getPolicy() == null) {
            throw new PreconditionException("PageFlowContext.getPolicy()");
        }

        // Get the policy that is the subject of the current PageFlow
        Policy policy = PageFlowContext.getPolicy();

        try {
            if (assetDoesNotExistFor(policy)) {
                createAssetFor(policy);
            }

            if (workflowDoesNotExistFor(policy)) {
                createWorkflowFor(policy);
            }
        } catch (Exception e) {
            throw new PostconditionException("Workflow creation failed.", e);
        }

        args.setModelArgRet(policy);

        if (args.getModelArgRet() == null) {
            throw new PostconditionException("args.setModelArgRet(policy)");
        }
    }

    private void createWorkflowFor(Policy policy) throws PortalException, SystemException {
        NewBusinessReferral newBusinessReferral = new NewBusinessReferral(policy);

        User user = determineUser();

        ServiceContext serviceContext = ServiceContextFactory.getInstance(PageFlowContext.getRequestWrapper().getServletRequest());
        Long companyId = PageFlowContext.getRequestWrapper().getCompany().getCompanyId();
        Long userId = user.getUserId();
        String className = NewBusinessReferral.class.getName();
        Long classPK = policy.getSystemId();

        WorkflowHandlerRegistryUtil.startWorkflowInstance(companyId, userId, className, classPK, newBusinessReferral, serviceContext);
    }

    private boolean workflowDoesNotExistFor(Policy policy) throws SystemException, PortalException {
        Company company = PageFlowContext.getRequestWrapper().getCompany();

        Long companyId = company.getCompanyId();
        Long groupId = company.getGroupId();
        String className = NewBusinessReferral.class.getName();
        Long classPK = policy.getSystemId();

        if (WorkflowInstanceLinkLocalServiceUtil.fetchWorkflowInstanceLink(companyId, groupId, className, classPK) == null) {
            return true;
        }

        return false;
    }

    private void createAssetFor(Policy policy) throws PortalException, SystemException {
        User user = determineUser();

        AssetEntry assetEntry = AssetEntryLocalServiceUtil.createAssetEntry(policy.getSystemId());
        assetEntry.setCompanyId(user.getCompanyId());
        assetEntry.setUserId(user.getUserId());
        assetEntry.setUserName(user.getFullName());
        assetEntry.setCreateDate(new Date());
        assetEntry.setClassNameId(PortalUtil.getClassNameId(NewBusinessReferral.class.getName()));
        assetEntry.setClassPK(policy.getSystemId());

        AssetEntryLocalServiceUtil.addAssetEntry(assetEntry);
    }

    private boolean assetDoesNotExistFor(Policy policy) throws SystemException {
        String className = NewBusinessReferral.class.getName();
        Long classPK = policy.getSystemId();

        if (AssetEntryLocalServiceUtil.fetchEntry(className, classPK) == null) {
            return true;
        }

        return false;
    }

    private User determineUser() throws PortalException, SystemException {
        User user = PageFlowContext.getRequestWrapper().getUser();

        if (user == null) {
            Company company = PageFlowContext.getRequestWrapper().getCompany();
            user = UserLocalServiceUtil.getDefaultUser(company.getCompanyId());
        }

        return user;
    }
}