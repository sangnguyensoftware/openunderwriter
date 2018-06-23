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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.paypal.core.rest.OAuthTokenCredential;
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
public class FetchPayPalAccessTokenService extends Service<FetchPayPalAccessTokenService.FetchPayPalAccessTokenArgument> {

    @ServiceArgument
    public interface FetchPayPalAccessTokenArgument extends Argument {
        /**
         * The ID of the product this this payment is being made in relation to.
         */
        String getProductTypeIdArg();

        /**
         * @see #getProductTypeIdArg()
         * @param productTypeIdArg
         */
        void setProductTypeIdArg(String productTypeIdArg);

        /**
         * Access token to be used to access PayPal's services
         */
        String getAccessTokenRet();

        /**
         * @see #getAccessTokenRet()
         * @param tokenRet
         */
        void setAccessTokenRet(String tokenRet);

        /**
         * Mode in which PayPal is configured to work for
         * <code>productTypeIdArg</code>. Should be "sandbox" or "live".
         */
        String getModeRet();

        /**
         * @see #getModeRet()
         * @param modeRet
         */
        void setModeRet(String modeRet);
    }

    @ServiceCommand(defaultServiceClass = FetchPayPalAccessTokenService.class)
    public interface FetchPayPalAccessTokenCommand extends FetchPayPalAccessTokenArgument, Command {
    }

    @Override
    public void invoke() throws PreconditionException, PostconditionException {

        if (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0) {
            throw new PreconditionException("args.getProductTypeIdArg()==null || args.getProductTypeIdArg().length()==0");
        }

        String namespace = Functions.productNameToConfigurationNamespace(args.getProductTypeIdArg());
        setCore(new CoreProxy(namespace, args.getCallersCore()).getCore());

        String mode = core.getParameterValue("PaymentMethods.PayPal.Mode");
        String clientID = core.getParameterValue("PaymentMethods.PayPal.ClientID");
        String clientSecret = core.getParameterValue("PaymentMethods.PayPal.ClientSecret");

        if (mode == null) {
            throw new PreconditionException("core.getParameterValue(\"PaymentMethods.PayPal.Mode\")==null");
        }

        if (clientID == null) {
            throw new PreconditionException("core.getParameterValue(\"PaymentMethods.PayPal.ClientID\")==null");
        }

        if (clientSecret == null) {
            throw new PreconditionException("core.getParameterValue(\"PaymentMethods.PayPal.ClientSecret\")==null");
        }

        args.setModeRet(mode);

        try {
            Map<String, String> sdkConfig = new HashMap<String, String>();

            sdkConfig.put("mode", mode);

            String token = new OAuthTokenCredential(clientID, clientSecret, sdkConfig).getAccessToken();

            args.setAccessTokenRet(token);
        } catch (PayPalRESTException e) {
            throw new PreconditionException("PayPal REST invocation failed: " + e);
        }

        if (args.getAccessTokenRet() == null || args.getAccessTokenRet().length() == 0) {
            throw new PostconditionException("args.getAccessTokenRet() == null || args.getAccessTokenRet().length() == 0");
        }
    }
}
