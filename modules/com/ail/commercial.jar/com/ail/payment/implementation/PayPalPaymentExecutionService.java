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

package com.ail.payment.implementation;

import java.util.HashMap;
import java.util.Map;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.payment.PaymentExecutionService;
import com.ail.payment.implementation.FetchPayPalAccessTokenService.FetchPayPalAccessTokenCommand;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RelatedResources;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;

/**
 * Implementation of the payment execution service using PayPal's REST API. This
 * service relies on configuration parameters and service arguments in order to
 * invoked PayPal "execute" API. Once the transaction's details have been passed
 * to PayPal. This is the last step in the process of a sale. It instructs
 * PayPal to execute a previously saved and authorised payment.
 * 
 * The PayPal payment process starts with a PayPalPaymentRequestService. This
 * returns a URL to forward to in order to get user authorisation. That will
 * either succeed and lead to the PayPalPaymentApprovedService being called; or,
 * fail and lead to PayPalPaymentCancelledService being called. Following the
 * PayPalPaymentApprovedService call, the PayPalPaymentExecutionServer must be
 * called to execute the payment.
 * 
 * Once the process is complete, the transaction is said to be a "sale" in
 * PayPal terminology. It will appear in the user's and merchant's transaction
 * history and is available for refunds etc.
 */
@ServiceImplementation
public class PayPalPaymentExecutionService extends Service<PaymentExecutionService.PaymentExecutionArgument> {

    @Override
    public void invoke() throws BaseException {

        if (args.getPayerIdArg() == null || args.getPayerIdArg().length() == 0) {
            throw new PreconditionException("args.getPayerIdArg() == null || args.getPayerIdArg().length() == 0");
        }

        if (args.getPaymentIdArg() == null || args.getPaymentIdArg().length() == 0) {
            throw new PreconditionException("args.getTransactionIdArg() == null || args.getTransactionIdArg().length() == 0");
        }

        try {
            FetchPayPalAccessTokenCommand fppatc = core.newCommand(FetchPayPalAccessTokenCommand.class);
            fppatc.setProductTypeIdArg(args.getProductTypeIdArg());
            fppatc.invoke();

            executePayment(fppatc.getModeRet(), fppatc.getAccessTokenRet());

        } catch (PayPalRESTException e) {
            throw new PreconditionException("PayPal integration failed.", e);
        }
    }

    private void executePayment(String mode, String accessToken) throws PayPalRESTException {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", mode);

        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(sdkConfig);

        Payment payment = Payment.get(apiContext, args.getPaymentIdArg());

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(args.getPayerIdArg());

        payment = payment.execute(apiContext, paymentExecution);

        for (RelatedResources resources : payment.getTransactions().get(0).getRelatedResources()) {
            if (resources.getSale().getId() != null) {
                args.setSaleIdRet(resources.getSale().getId());
            }
        }
    }
}
