/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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

import static com.ail.pageflow.PageElement.OPERATION_PARAM;
import static com.ail.pageflow.portlet.SubmitMode.PAGE;
import static java.lang.Boolean.FALSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.ExceptionRecord;
import com.ail.core.MapAttributeHandler;
import com.ail.core.context.portlet.PortletSessionAdaptor;
import com.ail.core.context.servlet.ServletSessionAdaptor;
import com.ail.core.logging.ServiceRequestRecord;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;
import com.ail.pageflow.portlet.SubmitMode;
import com.ail.pageflow.service.InitialisePageFlowContextService.InitialisePageFlowContextCommand;
import com.ail.party.Party;

/**
 * This class wraps a number of ThreadLocal objects which are initialised at the
 * beginning of each Portal request/response calls and are available to any code
 * executed within the portal container during the processing of the
 * request/response.
 */
public class PageFlowContext extends CoreContext {
    private static ThreadLocal<SessionWrapper> sessionWrapper = new ThreadLocal<>();
    private static ThreadLocal<List<String>> operationsProcessed = new ThreadLocal<>();
    private static ThreadLocal<Properties> operationParameters = new ThreadLocal<>();
    private static ThreadLocal<Policy> policyThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Party> partyThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Claim> claimThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<SubmitMode> submitMode = new ThreadLocal<>();
    private static ThreadLocal<Boolean> submitEnabled = new ThreadLocal<>();

    private static void runInitialisePageFlowContextCommand() throws RenderingError {
        try {
            CoreProxy coreProxy = new CoreProxy();
            InitialisePageFlowContextCommand command = coreProxy.newCommand(InitialisePageFlowContextCommand.class);
            command.invoke();
        } catch (Exception e) {
            if (getPolicy() != null) {
                getPolicy().addException(new ExceptionRecord(e));
            } else {
                throw new RenderingError("Failed to initialise PageFlowContext: " + e, e);
            }
        }
    }

    public static void initialise() {
        sessionWrapper.set(new SessionWrapper(new MapAttributeHandler()));
        operationsProcessed.set(new ArrayList<String>());
        submitEnabled.set(FALSE);
        submitMode.set(PAGE);
        CoreContext.initialise();
    }

    public static void initialise(PortletRequest request, PortletResponse response, PortletConfig config) {
        sessionWrapper.set(new SessionWrapper(new PortletSessionAdaptor(request.getPortletSession())));
        operationsProcessed.set(new ArrayList<String>());
        submitEnabled.set(FALSE);
        submitMode.set(PAGE);
        CoreContext.initialise(request, response, config);
        runInitialisePageFlowContextCommand();
        ServiceRequestRecord srr = new ServiceRequestRecord(getProductName(), getPageFlowName()+"/"+getCurrentPageName(), getPolicy()!=null ? getPolicy().getExternalSystemId() : null);
        CoreContext.getCoreProxy().create(srr);
        CoreContext.setServiceRequestRecord(srr);
    }

    public static void initialise(HttpServletRequest request, HttpServletResponse response, ServletConfig config) {
        sessionWrapper.set(new SessionWrapper(new ServletSessionAdaptor(request.getSession())));
        operationsProcessed.set(new ArrayList<String>());
        submitEnabled.set(FALSE);
        submitMode.set(PAGE);
        CoreContext.initialise(request, response, config);
    }

    public static void destroy() {
        if (CoreContext.getServiceRequestRecord() != null) {
            CoreContext.getServiceRequestRecord().setExitTimestamp();
            CoreContext.getCoreProxy().create(CoreContext.getServiceRequestRecord());
        }
        CoreContext.destroy();
        policyThreadLocal.remove();
        partyThreadLocal.remove();
        claimThreadLocal.remove();
        submitMode.remove();
        submitEnabled.remove();
    }

    /**
     * Reset the Context clearing the current state and moving the flow back to
     * the start page. This clears the state of the current PageFlow, but the
     * context still remains tied to that PageFlow. From the user's perspective
     * this will take them back to the first page with fresh state (i.e. any
     * data they have entered will not be retained).
     */
    public static void restart() {
        CoreContext.restart();
        setPolicy(null);
        sessionWrapper.get().setCurrentPageName(null);
        sessionWrapper.get().setNextPageName(null);
        sessionWrapper.get().setPageFlowInitialised(false);
    }

    public static void reinitialise() {
        restart();

        if (getRequestWrapper().isPortletRequest()) {
            initialise(getRequestWrapper().getPortletRequest(), getResponseWrapper().getPortletResponse(), getConfigWrapper().getPortletConfig());
        }
        else {
            initialise(getRequestWrapper().getServletRequest(), getResponseWrapper().getServletResponse(), getConfigWrapper().getServletConfig());
        }
    }

    public static void clear() {
        CoreContext.clear();
        restart();
        sessionWrapper.get().clear();
    }

    public static Policy getPolicy() {
        return policyThreadLocal.get();
    }

    public static void setPolicy(Policy policyArg) {
        policyThreadLocal.set(policyArg);
        setPolicySystemId(policyArg != null ? policyArg.getSystemId() : null);
    }

    public static void setPolicySystemId(Long policySystemId) {
        sessionWrapper.get().setPolicySystemId(policySystemId);
    }

    public static Long getPolicySystemId() {
        return sessionWrapper.get().getPolicySystemId();
    }

    public static Party getParty() {
        return partyThreadLocal.get();
    }

    public static void setParty(Party partyArg) {
        partyThreadLocal.set(partyArg);
        setPartySystemId(partyArg != null ? partyArg.getSystemId() : null);
    }

    public static void setPartySystemId(Long partySystemId) {
        sessionWrapper.get().setPartySystemId(partySystemId);
    }

    public static Long getPartySystemId() {
        return sessionWrapper.get().getPartySystemId();
    }

    public static Claim getClaim() {
        return claimThreadLocal.get();
    }

    public static void setClaim(Claim claimArg) {
        claimThreadLocal.set(claimArg);
        setClaimSystemId(claimArg != null ? claimArg.getSystemId() : null);
    }

    public static void setClaimSystemId(Long claimSystemId) {
        sessionWrapper.get().setClaimSystemId(claimSystemId);
    }

    public static Long getClaimSystemId() {
        return sessionWrapper.get().getClaimSystemId();
    }

    public static PageFlow getPageFlow() {
        return sessionWrapper.get().getPageFlow();
    }

    public static void setPageFlow(PageFlow pageFlowArg) {
        sessionWrapper.get().setPageFlow(pageFlowArg);

        // Ensure that all of the PageFlow elements have IDs. If any don't, this
        // will generate IDs for them.
        if (pageFlowArg != null) {
            getPageFlow().applyElementId("OQ0");
        }
    }

    /**
     * Get the pageflow name (if any) associated with this pageflow. In the
     * course of a normal pageflow (i.e. one started from the PageflowPortlet),
     * this will always return a name, but the for pageflow running in the
     * SandpitPortlet it may return null.
     *
     * @return pageflow name, or null.
     */
    public static String getPageFlowName() {
        return sessionWrapper.get().getPageFlowName();
    }

    /**
     * @see #getPageFlowName()
     * @param pageFlowName
     */
    public static void setPageFlowName(String pageFlowNameArg) {
        sessionWrapper.get().setPageFlowName(pageFlowNameArg);
    }

    public static String getCurrentPageName() {
        return sessionWrapper.get().getCurrentPageName();
    }

    public static void setCurrentPageName(String pageName) {
        sessionWrapper.get().setCurrentPageName(pageName);
    }

    public static String getVersion() {
        return sessionWrapper.get().getVersion();
    }

    public static void setVersion(String version) {
        sessionWrapper.get().setVersion(version);
    }

    public static String getNextPageName() {
        return sessionWrapper.get().getNextPageName();
    }

    public static void setNextPageName(String pageName) {
        sessionWrapper.get().setNextPageName(pageName);
    }

    public static Boolean isPageFlowInitialised() {
        return sessionWrapper.get().isPageFlowInitialised();
    }

    public static void setPageFlowInitliased(Boolean initialised) {
        sessionWrapper.get().setPageFlowInitialised(initialised);
    }

    public static void selectProductName(String productName) {
        clear();
        setProductName(productName);
    }

    public static void savePolicy() throws BaseException {
        ExecutePageActionCommand persistPolicyCommand = PageFlowContext.getCoreProxy().newCommand("PersistPolicyFromPageFlowCommand", ExecutePageActionCommand.class);
        persistPolicyCommand.invoke();
    }

    public static void saveParty() throws BaseException {
        ExecutePageActionCommand persistPartyCommand = PageFlowContext.getCoreProxy().newCommand("PersistPartyFromPageFlowCommand", ExecutePageActionCommand.class);
        persistPartyCommand.invoke();
    }

    public static void saveClaim() throws BaseException {
        ExecutePageActionCommand persistClaimCommand = PageFlowContext.getCoreProxy().newCommand("PersistClaimFromPageFlowCommand", ExecutePageActionCommand.class);
        persistClaimCommand.invoke();
    }

    public static List<String> getOperationsProcessed() {
        return operationsProcessed.get();
    }

    public static Properties getOperationParameters() {
        return operationParameters.get();
    }

    public static void setOperationParameters(Properties params) {
        operationParameters.set(params);
    }

    public static void flagActionAsProcessed() {
        operationsProcessed.get().add(getRequestedOperation());
    }

    public static String getRequestedOperation() {
        return operationParameters.get().getProperty(OPERATION_PARAM);
    }

    public static String getRequestedOperationId() {
        return operationParameters.get().getProperty("id");
    }

    public static void setSubmitMode(SubmitMode mode) {
        submitMode.set(mode);
    }

    public static SubmitMode getSubmitMode() {
        return submitMode.get();
    }

    public static void setSubmitEnabled(Boolean mode) {
        submitEnabled.set(mode);
    }

    public static boolean getSubmitEnabled() {
        return submitEnabled.get();
    }
}
