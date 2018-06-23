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

import java.io.IOException;

import com.ail.core.BaseException;
import com.ail.core.Type;

public class BuyNowWithEwayButtonAction extends BuyNowButtonActionCommon {
    private static final String EWAY_RESULT_PARAMETER_NAME = "eway-result";
    private static final String EWAY_APPROVED = "approved";
    private static final String EWAY_CANCELLED = "cancelled";

    public BuyNowWithEwayButtonAction() {
        setLabel("i18n_buy_with_eway_payment_description");
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        model = super.processActions(model);

        String op = PageFlowContext.getRequestedOperation();

        if (buttonPressed()) {
            initiatePayment(EWAY_RESULT_PARAMETER_NAME, EWAY_APPROVED, EWAY_CANCELLED);
            PageFlowContext.flagActionAsProcessed();
        } else if (op == null) {
            processPaymentResponse();
        }

        return model;
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return op!=null && "buy-now-with-eway".equals(op);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("BuyNowWithEwayButton", model);
    }
}
