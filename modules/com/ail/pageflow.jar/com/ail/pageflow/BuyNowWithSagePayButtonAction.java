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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.insurance.acceptance.PremiumCollectionRequestService.PremiumCollectionRequestCommand;
import com.ail.insurance.policy.Policy;
import com.liferay.portal.kernel.util.PropsUtil;

public class BuyNowWithSagePayButtonAction extends BuyNowButtonActionCommon {
    private static final String SAGEPAY_RESULT_PARAMETER_NAME = "sagepay-result";
    private static final String SAGEPAY_APPROVED = "approved";
    private static final String SAGEPAY_CANCELLED = "cancelled";

    public BuyNowWithSagePayButtonAction() {
        setLabel("i18n_buy_with_sagepay_payment_description");
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        String op = PageFlowContext.getRequestedOperation();

        if (buttonPressed()) {
            // The widget will handle the submission through a dynamic form.
            PageFlowContext.flagActionAsProcessed();
        } else if (op == null) {
            String sageResult = PageFlowContext.getRequestWrapper().getParameter(SAGEPAY_RESULT_PARAMETER_NAME);
            if (SAGEPAY_APPROVED.equals(sageResult)) {
                try {
                    recordPaymentApproval();
                    executePayment();
                    PageFlowContext.getPageFlow().setNextPage(getDestinationOnSuccessPageId());
                } catch (BaseException e) {
                    PageFlowContext.getCoreProxy().logError("Payment was requested and authorised by the user, but payment execution failed.", e);
                    PageFlowContext.getPageFlow().setNextPage(getDestinationOnFailurePageId());
                }
            } else if (SAGEPAY_CANCELLED.equals(sageResult)) {
                recordPaymentCancelled();
                PageFlowContext.getPageFlow().setNextPage(getDestinationOnFailurePageId());
            }
        }

        return model;
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return op!=null && "buy-now-with-sagepay".equals(op);
    }

    /**
     * Invoke the CollectPremium service which (by virtue of the MoneyProvisions
     * within the policy - {@link #addPaymentDetailsToPolicy()}) will in turn
     * generate a SagePay "Crypt" string containing the payment ready to be
     * submitted to Sage.
     */
    public String getCrypt() throws RenderingError {
        CoreProxy core = PageFlowContext.getCoreProxy();
        Policy policy = PageFlowContext.getPolicy();

        try {
            StringBuffer approvedURL = new StringBuffer(), cancelledURL = new StringBuffer();

            generateResultForwardingURLs(approvedURL, cancelledURL, SAGEPAY_RESULT_PARAMETER_NAME, SAGEPAY_APPROVED, SAGEPAY_CANCELLED);

            PremiumCollectionRequestCommand cpc = core.newCommand(PremiumCollectionRequestCommand.class);
            cpc.setPolicyArgRet(policy);
            cpc.setApprovedURLArg(externalizeURL(approvedURL));
            cpc.setCancelledURLArg(externalizeURL(cancelledURL));
            cpc.invoke();

            return cpc.getEncryptedRequestRet();
        } catch (Exception e) {
            throw new RenderingError("Failed to forward integration with SagePay.", e);
        }
    }

    private URL externalizeURL(StringBuffer url) throws MalformedURLException {

        try {
            URL internalURL = new URL(url.toString());

            String protocol = PropsUtil.get("web.server.protocol");
            String host = PropsUtil.get("web.server.host");
            String port = PropsUtil.get("web.server." + protocol + ".port");
            String file = internalURL.getFile();

            protocol = (protocol != null && protocol.length() > 0) ? protocol : internalURL.getProtocol();
            host = (host != null && host.length() > 0) ? host : internalURL.getHost();
            port = (port != null && port.length() > 0) ? port : Integer.toString(internalURL.getPort());

            int portNumber = (port == null || port.length() == 0) ? -1 : Integer.parseInt(port);

            return new URL(protocol, host, portNumber, file);
        } catch (MalformedURLException e) {
            return new URL(url.toString());
        }
    }

    public String getVPSProtocol() {
        return PageFlowContext.getCoreProxy().getParameterValue("PaymentMethods.SagePay.VPSProtocol");
    }

    public String getVendor() {
        return PageFlowContext.getCoreProxy().getParameterValue("PaymentMethods.SagePay.Vendor");
    }

    public String getGatewayURL() {
        return PageFlowContext.getCoreProxy().getParameterValue("PaymentMethods.SagePay.URL");
    }


    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("BuyNowWithSagePayButton", model);
    }
}
