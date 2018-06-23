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

import static com.ail.financial.PaymentRequestStatus.APPROVED;
import static com.ail.financial.PaymentRequestStatus.CANCELLED;
import static com.ail.financial.PaymentRequestStatus.FAILED;

import java.io.IOException;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.financial.PaymentRequestStatus;

public class BuyNowWithPayPalButtonAction extends BuyNowButtonActionCommon {
    private static final String PAYPAL_RESULT_PARAMETER_NAME = "paypal-result";
    private static final String PAYPAL_APPROVED = "approved";
    private static final String PAYPAL_CANCELLED = "cancelled";

    public BuyNowWithPayPalButtonAction() {
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        String op = PageFlowContext.getRequestedOperation();

        if (buttonPressed()) {
            model = super.processActions(model);
            initiatePayment(PAYPAL_RESULT_PARAMETER_NAME, PAYPAL_APPROVED, PAYPAL_CANCELLED);
            PageFlowContext.flagActionAsProcessed();
        } else if (op == null) {
            switch (processPaymentResponse()) {
            case APPROVED:
                try {
                    recordPaymentApproval();
                    executePayment();
                    PageFlowContext.getPageFlow().setNextPage(getDestinationOnSuccessPageId());
                } catch (BaseException e) {
                    PageFlowContext.getCoreProxy().logError("Payment was requested and authorised by the user, but payment execution failed.", e);
                    PageFlowContext.getPageFlow().setNextPage(getDestinationOnFailurePageId());
                }
                break;
            case CANCELLED:
                recordPaymentCancelled();
                PageFlowContext.getPageFlow().setNextPage(getDestinationOnFailurePageId());
                break;
            }
        }

        return model;
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return op != null && "buy-now-with-paypal".equals(op);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("BuyNowWithPayPalButton", model);
    }

    @Override
    protected PaymentRequestStatus processPaymentResponse() throws BaseException {
        String paypalResult = PageFlowContext.getRequestWrapper().getParameter(PAYPAL_RESULT_PARAMETER_NAME);
        if (PAYPAL_APPROVED.equals(paypalResult)) {
            return APPROVED;
        } else if (PAYPAL_CANCELLED.equals(paypalResult)) {
            return CANCELLED;
        } else {
            return FAILED;
        }
    }
}
