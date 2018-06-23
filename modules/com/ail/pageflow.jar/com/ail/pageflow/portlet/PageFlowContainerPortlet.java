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
package com.ail.pageflow.portlet;

import static com.ail.core.Functions.productNameToConfigurationNamespace;
import static com.ail.core.language.I18N.i18n;
import static com.ail.insurance.policy.PolicyStatus.forName;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.XMLException;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;
import com.google.common.collect.Lists;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class PageFlowContainerPortlet extends GenericPortlet {

    private static final String ACTION_URL = "actionURL";
    private static final String RESOURCE_URL = "resourceURL";

    private static final PolicyAdminTab DEFAULT_PAGEFLOW_TAB = PolicyAdminTab.PolicySummaryPageFlow;

    private static final String SESSION_PAGEFLOW_TAB = "pageFlowTab";
    public static final String SESSION_POLICY_ENTITY_ID = "policyEntityId";

    private String viewJsp;
    private CoreProxy coreProxy;
    private PageFlowCommon pageFlowCommon;

    public PageFlowContainerPortlet() {
        coreProxy = new CoreProxy();
        pageFlowCommon = new PageFlowCommon();
    }

    @Override
    public void init() throws PortletException {
        viewJsp = getInitParameter("view-jsp");
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        doProcessPageFlowAction(request, response);
        doPageFlowView(request, response);
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        doPageFlowView(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        doProcessPageFlowAction(request, response);
    }

    private void doPageFlowView(PortletRequest request, MimeResponse response) throws PortletException, IOException {
        response.setContentType("text/html");

        response.getWriter().write("<div id='pageflow-wrapper'>");

        try {

            doView(request, response, getPolicy(request));

        } catch (BaseException e) {
            coreProxy.logError("Failed to process view", e);
            response.getWriter().write("There has been an error displaying this page.");
        }

        response.getWriter().write("</div>");
    }

    private void doProcessPageFlowAction(PortletRequest request, PortletResponse response) throws PortletException, IOException {

        boolean processActions = true;

        String pageFlowTab = request.getParameter(SESSION_PAGEFLOW_TAB);
        String policySystemId = request.getParameter(SESSION_POLICY_ENTITY_ID);

        if (StringUtils.isNotBlank(pageFlowTab)) {
            setCurrentPageFlowName(request, PolicyAdminTab.valueOf(pageFlowTab));
            processActions = false;
        }
        if (StringUtils.isNotBlank(policySystemId)) {

            setCurrentPolicySystemId(request, policySystemId);
            processActions = false;
        }

        if (PageFlowContext.getPolicy() == null
                || getCurrentPolicySystemId(request) == null
                || PageFlowContext.getPolicy().getSystemId() != Long.valueOf(getCurrentPolicySystemId(request))) {

            initialisePageFlow(getPolicy(request), getCurrentPageFlowTab(request));
        }

        if (processActions) {
            try {
                pageFlowCommon.processAction();
            } catch (BaseException e) {
                coreProxy.logError("Failed to show policy", e);
                throw new PortletException(e);
            }
        }
    }


    private void doView(PortletRequest request, MimeResponse response, Policy policy) throws IOException, PortletException, BaseException {
        PolicyAdminTab pageFlowTab = getCurrentPageFlowTab(request);

        try {

            PolicyAdminTab tab = setTabs(request, response, pageFlowTab, policy);

            if (tab != PolicyAdminTab.Search) {

                initPageFlow(policy, tab);
                pageFlowCommon.doView();
            } else {
                PageFlowContext.setPageFlowName(null);
            }

        } catch (XMLException e) {
            coreProxy.logError("Failed to show policy", e);
            throw new PortletException(e);
        }
    }

    private void initPageFlow(Policy policy, PolicyAdminTab pageFlowTab) {


            PageFlowContext.setPolicy(policy);
            PageFlowContext.setProductName(policy.getProductTypeId());
            PageFlowContext.setCoreProxy(new CoreProxy(productNameToConfigurationNamespace(policy.getProductTypeId())));

            if (PageFlowContext.getPageFlow() == null
                    || !pageFlowTab.name().equals(PageFlowContext.getPageFlowName())) {

                initialisePageFlow(policy, pageFlowTab);
            }

            PageFlowContext.setPageFlowName(pageFlowTab.name());

    }

    private void initialisePageFlow(Policy policy, PolicyAdminTab pageFlowTab) {
        PageFlow pageFlow = coreProxy.newProductType(policy.getProductTypeId(), pageFlowTab.name(), PageFlow.class);
        PageFlowContext.setPageFlow(pageFlow);
        pageFlow.setNextPage(pageFlow.getStartPage());
    }

    private Policy getPolicy(PortletRequest request) {
        String policyEntityId = getCurrentPolicySystemId(request);

        if (StringUtils.isNotBlank(policyEntityId)) {
            return (Policy) coreProxy.queryUnique("get.policy.by.systemId", Long.valueOf(policyEntityId));
        }

        return null;
    }


    public class PageFlowTab {
        private PolicyAdminTab tab;
        private boolean visible;
        private boolean selected;
        public PageFlowTab(PolicyAdminTab tab, boolean visible, boolean selected) {
            super();
            this.tab = tab;
            this.visible = visible;
            this.selected = selected;
        }
        public PageFlowTab(PolicyAdminTab tab, boolean visible, PolicyAdminTab selectedFlow) {
            this(tab, visible, selectedFlow == tab);
        }
        public PolicyAdminTab getTab() {
            return tab;
        }
        public boolean isVisible() {
            return visible;
        }
        public boolean isSelected() {
            return selected;
        }
    }

    public enum PolicyAdminTab {
        Search("Search"),
        PolicySummaryPageFlow("Summary"),
        PolicyDetailPageFlow("Details"),
        AssessmentSheetPageFlow("Assessment Sheet"),
        PaymentsPageFlow("Payments"),
        PolicyDocumentsPageFlow("Documents"),
        ReferralDetailPageFlow("Referral"),
        QuoteToOnRiskPageFlow("Quotation"),
        PolicyAdminPageFlow("Policy Admin"),
        ClientDetailsPageFlow("Client Details"),
        LogsPageFlow("Logs");

        String label;
        PolicyAdminTab(String label) {
            this.label = label;
        }
        public String getLabel() {
            return label;
        }
    }
    private PolicyAdminTab setTabs(PortletRequest request, MimeResponse response, PolicyAdminTab pageFlowTab, Policy policy) throws PortletException, IOException {

        if (policy != null) {
            // revert to DEFAULT_PAGEFLOW_TAB view if no longer REFERRED / QUOTE etc
            if ((pageFlowTab == PolicyAdminTab.ReferralDetailPageFlow && !isViewableReferralTab(policy))
                || (pageFlowTab == PolicyAdminTab.QuoteToOnRiskPageFlow && !isViewableQuoteToOnRiskTab(policy))
                || (pageFlowTab == PolicyAdminTab.PaymentsPageFlow && !isViewablePaymentsTab(policy))
                || (pageFlowTab == PolicyAdminTab.ClientDetailsPageFlow && !isViewableClientDetailsTab(policy, pageFlowTab))) {

                pageFlowTab = DEFAULT_PAGEFLOW_TAB;
            }

            request.setAttribute("policySystemId", policy.getSystemId() + "");
            request.setAttribute("status", status(policy.getStatusAsString()));

        }

        setCurrentPageFlowName(request, pageFlowTab);

        List<PageFlowTab> tabs = Lists.newArrayList();

        tabs.add(new PageFlowTab(PolicyAdminTab.Search,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.PolicySummaryPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.PolicyDetailPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.AssessmentSheetPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.PaymentsPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.PolicyDocumentsPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.ReferralDetailPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.QuoteToOnRiskPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.PolicyAdminPageFlow,
                true,
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.ClientDetailsPageFlow,
                isViewableClientDetailsTab(policy, pageFlowTab),
                pageFlowTab));

        tabs.add(new PageFlowTab(PolicyAdminTab.LogsPageFlow,
                true,
                pageFlowTab));

        request.setAttribute(RESOURCE_URL, response.createResourceURL().toString());
        request.setAttribute(ACTION_URL, response.createActionURL().toString());
        User user = (User)request.getAttribute("USER");
        request.setAttribute("auth", user.getCompanyId() + "," + user.getUserId() + "");
        request.setAttribute("tabs", tabs);

        PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(viewJsp);
        portletRequestDispatcher.include(request, response);

        return pageFlowTab;
    }

    private String status(String status) {
        if (StringUtils.isNotBlank(status)) {
            return i18n(forName(status).getLongName()).replace(" ", "").substring(0, 3).toUpperCase();
        }
        return "";
    }

    private boolean isViewableClientDetailsTab(Policy policy, PolicyAdminTab pageFlowTab) {
        return policy != null && (pageFlowTab == PolicyAdminTab.ClientDetailsPageFlow // only show if specifically requested
                && policy.getClient() != null);
    }

    private boolean isViewableQuoteToOnRiskTab(Policy policy) {
        return policy != null && (policy.getStatus() == PolicyStatus.QUOTATION
                                || policy.getStatus() == PolicyStatus.SUBMITTED);
    }

    private boolean isViewableReferralTab(Policy policy) {
        return policy != null && (policy.getStatus() == PolicyStatus.REFERRED || policy.getStatus() == PolicyStatus.DECLINED);
    }

    private boolean isViewablePaymentsTab(Policy policy) {
        return policy != null && (policy.getStatus() == PolicyStatus.SUBMITTED
                                || policy.getStatus() == PolicyStatus.ON_RISK
                                || policy.getStatus() == PolicyStatus.QUOTATION
                                || policy.getStatus() == PolicyStatus.CANCELLED);
    }

    private void setCurrentPageFlowName(PortletRequest request, PolicyAdminTab pageFlowTabName) {
        getHttpSession(request).setAttribute(SESSION_PAGEFLOW_TAB, pageFlowTabName.name());
    }

    private PolicyAdminTab getCurrentPageFlowTab(PortletRequest request) {

        String tab = (String)getHttpSession(request).getAttribute(SESSION_PAGEFLOW_TAB);

        if (tab != null) {
            return PolicyAdminTab.valueOf(tab);
        }
        return  PolicyAdminTab.Search;
    }

    private void setCurrentPolicySystemId(PortletRequest request, String policySystemId) {
        getHttpSession(request).setAttribute(SESSION_POLICY_ENTITY_ID, policySystemId);
    }

    private String getCurrentPolicySystemId(PortletRequest request) {
        return (String) getHttpSession(request).getAttribute(SESSION_POLICY_ENTITY_ID);
    }

    private HttpSession getHttpSession(PortletRequest request) {
        return PortalUtil.getHttpServletRequest(request).getSession();
    }
}
