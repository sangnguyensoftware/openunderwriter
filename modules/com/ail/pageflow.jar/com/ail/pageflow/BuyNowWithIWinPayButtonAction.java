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
import com.ail.core.ExceptionRecord;
import com.ail.core.PostconditionException;
import com.ail.core.Type;
import com.ail.insurance.acceptance.PremiumCollectionRequestService.PremiumCollectionRequestCommand;
import com.ail.pageflow.portlet.PageFlowException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

public class BuyNowWithIWinPayButtonAction extends BuyNowButtonActionCommon {
    private static final String IWINPAY_RESULT_PARAMETER_NAME = "iwin-result";
    private static final String IWINPAY_APPROVED = "approved";
    private static final String IWINPAY_CANCELLED = "cancelled";

    public BuyNowWithIWinPayButtonAction() {
        setLabel("i18n_buy_with_iwinpay_payment_description");
    }

    @Override
    public Type processActions(Type model) throws BaseException {

        if (buttonPressed()) {
            model = super.processActions(model);

            initiatePaymentRequest();

            PageFlowContext.flagActionAsProcessed();
        }

        return model;
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return op!=null && "buy-now-with-iwinpay".equals(op);
    }

    private void initiatePaymentRequest() throws BaseException {
        try {
            StringBuffer approvedURL = new StringBuffer(), cancelledURL = new StringBuffer();

            generateResultForwardingURLs(approvedURL, cancelledURL, IWINPAY_RESULT_PARAMETER_NAME, IWINPAY_APPROVED, IWINPAY_CANCELLED);

            PremiumCollectionRequestCommand cpc = PageFlowContext.getCoreProxy().newCommand(PremiumCollectionRequestCommand.class);
            cpc.setPolicyArgRet(PageFlowContext.getPolicy());
            cpc.setApprovedURLArg(new URL(approvedURL.toString()));
            cpc.setCancelledURLArg(new URL(cancelledURL.toString()));
            cpc.invoke();

            PageFlowContext.setNextPageName(getDestinationOnSuccessPageId());
        }
        catch(PostconditionException e) {
            PageFlowContext.getPolicy().addException(new ExceptionRecord(e));
            PageFlowContext.setNextPageName(getDestinationOnFailurePageId());
        } catch (MalformedURLException | PortalException | SystemException e) {
            throw new PageFlowException("iWinPay payment failed", e);
        }
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("BuyNowWithIWinPayButton", model);
    }
}
