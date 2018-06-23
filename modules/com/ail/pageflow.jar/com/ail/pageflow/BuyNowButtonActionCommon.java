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
package com.ail.pageflow;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.PaymentRequestStatus;
import com.ail.insurance.acceptance.PremiumCollectionAuthorisedService.PremiumCollectionAuthorisedCommand;
import com.ail.insurance.acceptance.PremiumCollectionCancelledService.PremiumCollectionCancelledCommand;
import com.ail.insurance.acceptance.PremiumCollectionExecutionService.PremiumCollectionExecutionCommand;
import com.ail.insurance.acceptance.PremiumCollectionRequestService.PremiumCollectionRequestCommand;
import com.ail.insurance.acceptance.PremiumCollectionResponseService.PremiumCollectionResponseCommand;
import com.ail.insurance.policy.Policy;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;

abstract class BuyNowButtonActionCommon extends CommandButtonAction {
    private String destinationOnSuccessPageId;
    private String destinationOnFailurePageId;

    public BuyNowButtonActionCommon() {
    }

    /**
     * Invoke the CollectPremium service which (by virtue of the MoneyProvisions
     * within the policy.
     */
    protected void initiatePayment(String resultParamName, String approvedActionName, String cancelledActionName) throws RenderingError {
        CoreProxy core = PageFlowContext.getCoreProxy();
        Policy policy = PageFlowContext.getPolicy();

        try {
            StringBuffer approvedURL = new StringBuffer(), cancelledURL = new StringBuffer();
            generateResultForwardingURLs(approvedURL, cancelledURL, resultParamName, approvedActionName, cancelledActionName);

            PremiumCollectionRequestCommand cpc = core.newCommand(PremiumCollectionRequestCommand.class);
            cpc.setPolicyArgRet(policy);
            cpc.setApprovedURLArg(externaliseURL(approvedURL.toString()));
            cpc.setCancelledURLArg(externaliseURL(cancelledURL.toString()));
            cpc.invoke();

            CoreContext.getResponseWrapper().sendRedirect(cpc.getAuthorisationURLRet().toString());
        } catch (Exception e) {
            throw new RenderingError("Failed to forward intgrate with payment provider.", e);
        }
    }

    protected void executePayment() throws BaseException {
        CoreProxy core = PageFlowContext.getCoreProxy();
        Policy policy = PageFlowContext.getPolicy();

        PremiumCollectionExecutionCommand pcec = core.newCommand(PremiumCollectionExecutionCommand.class);
        pcec.setPolicyArgRet(policy);
        pcec.setPayerIdArg(fetchPayerIdFromRequest());
        pcec.invoke();
    }

    protected String fetchPayerIdFromRequest() {
        RequestWrapper request = PageFlowContext.getRequestWrapper();

        HttpServletRequest servletRequest = request.getServletRequest();

        return servletRequest.getParameter("PayerID");
    }

    protected void recordPaymentCancelled() {
        CoreProxy core = PageFlowContext.getCoreProxy();

        try {
            PremiumCollectionCancelledCommand pcc;
            pcc = core.newCommand(PremiumCollectionCancelledCommand.class);
            pcc.setPolicyArgRet(PageFlowContext.getPolicy());
            pcc.invoke();
        } catch (BaseException e) {
            core.logError("Payment has been sucessfully cancelled, but the policy record could not be updated.", e);
            PageFlowContext.getPageFlow().setNextPage(destinationOnFailurePageId);
        }
    }

    protected void recordPaymentApproval() throws BaseException {
        CoreProxy core = PageFlowContext.getCoreProxy();

        PremiumCollectionAuthorisedCommand pcc;
        pcc = core.newCommand(PremiumCollectionAuthorisedCommand.class);
        pcc.setPolicyArgRet(PageFlowContext.getPolicy());
        pcc.invoke();
    }

    /**
     * Generate forwarding URLs for the payment provider to use once it's
     * processes are complete. Two URLs are needed; one to use if the payment
     * succeeds; and, a second to use if it fails. Both will point back to the
     * current page (ensuring that this widget will be invoked. A parameter is
     * added to the end of each URL to indicate the type of outcome.
     */
    protected void generateResultForwardingURLs(StringBuffer approved, StringBuffer cancelled, String patameterName, String approvedValue, String cancelledValue)
            throws MalformedURLException, PortalException, SystemException {

        String currentCompleteURL = PageFlowContext.getRequestWrapper().getCurrentCompleteURL();

        approved.append(currentCompleteURL + '&' + patameterName + '=' + approvedValue);
        cancelled.append(currentCompleteURL + '&' + patameterName + '=' + cancelledValue);
    }

    public String getDestinationOnSuccessPageId() {
        return destinationOnSuccessPageId;
    }

    public void setDestinationOnSuccessPageId(String destinationOnSuccessPageId) {
        this.destinationOnSuccessPageId = destinationOnSuccessPageId;
    }

    public String getDestinationOnFailurePageId() {
        return destinationOnFailurePageId;
    }

    public void setDestinationOnFailurePageId(String destinationOnFailurePageId) {
        this.destinationOnFailurePageId = destinationOnFailurePageId;
    }

    protected PaymentRequestStatus processPaymentResponse() throws BaseException {
        CoreProxy core = PageFlowContext.getCoreProxy();

        PremiumCollectionResponseCommand pcrc;
        pcrc = core.newCommand(PremiumCollectionResponseCommand.class);
        pcrc.setPolicyArgRet(PageFlowContext.getPolicy());
        pcrc.invoke();

        switch(pcrc.getRequestStatusRet()) {
        case APPROVED:
            PageFlowContext.getPageFlow().setNextPage(getDestinationOnSuccessPageId());
            break;
        case CANCELLED:
        case FAILED:
            PageFlowContext.getPageFlow().setNextPage(getDestinationOnFailurePageId());
            break;
        }

        return pcrc.getRequestStatusRet();
    }

    /**
     * To externalise a URL we have to ensure that the host and port number correctly reflect
     * those that Liferay is configured to use (in portal-ext.properties). When OpenUnderwriter
     * is running behind a proxy, these values must be used in place of local settings which
     * in this instance would probably be localhost and 8080 - which will obviously be of no
     * use to PayPal when it attempts to redirect the user's browser back to us.
     * @param stringUrl
     * @return
     * @throws MalformedURLException
     */
    protected URL externaliseURL(String stringUrl) throws MalformedURLException {

        String host = PropsUtil.get("web.server.host");
        Integer port = new Integer(PropsUtil.get("web.server.http.port"));

        URL url=new URL(stringUrl);

        host = (host==null || host.length()==0) ? host=url.getHost() : host;
        port = (port==-1) ? url.getPort() : port;

        if (port==-1) {
            return new URL(url.getProtocol(), host, url.getFile());
        }
        else {
            return new URL(url.getProtocol(), host, port, url.getFile());
        }
    }
}
