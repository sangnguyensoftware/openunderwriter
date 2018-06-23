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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.payment.PaymentRequestService;
import com.ail.payment.implementation.FetchPayPalAccessTokenService.FetchPayPalAccessTokenCommand;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;

/**
 * Implementation of the payment request service using PayPal's REST API. This
 * service relies on configuration parameters and service arguments in order to
 * build the PayPal transaction. Once the transaction's details have been passed
 * to PayPal, the service will return a single return parameter indicating the
 * URL that the client (browser) should be forwarded to in order to complete the
 * process.
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
public class PayPalPaymentRequestService extends Service<PaymentRequestService.PaymentRequestArgument> {

    @Override
    public void invoke() throws BaseException {

        if (args.getAmountArg() == null) {
            throw new PreconditionException("args.getAmountArg()==null");
        }

        if (args.getApprovedURLArg() == null) {
            throw new PreconditionException("args.getApprovedURLArg()==null");
        }

        if (args.getCancelledURLArg() == null) {
            throw new PreconditionException("args.getCancelledURLArg()==null");
        }

        if (args.getDescriptionArg() == null) {
            throw new PreconditionException("args.getDescriptionArg()==null");
        }

        FetchPayPalAccessTokenCommand fppatc = getCore().newCommand(FetchPayPalAccessTokenCommand.class);
        fppatc.setProductTypeIdArg(args.getProductTypeIdArg());
        fppatc.invoke();

        String accessToken = fppatc.getAccessTokenRet();
        String mode = fppatc.getModeRet();

        try {
            Payment payment = createPayment(accessToken, mode);

            args.setPaymentIdRet(payment.getId());

            args.setAuthorisationURLRet(fetchAuthorisationURL(payment));
        } catch (Exception e) {
            throw new PostconditionException("Encountered Exception interfacing with PayPal.", e);
        }

        if (args.getAuthorisationURLRet() == null) {
            throw new PostconditionException("args.getAuthorisationURLRet()==null");
        }

        if (args.getEncryptedRequestRet() != null) {
            throw new PostconditionException("args.getEncryptedRequestRet()!=null");
        }
    }

    private URL fetchAuthorisationURL(Payment payment) throws MalformedURLException {
        for (Links link : payment.getLinks()) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                return new URL(link.getHref());
            }
        }

        return null;
    }

    private Payment createPayment(String accessToken, String mode) throws PayPalRESTException, MalformedURLException {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", mode);

        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(sdkConfig);

        Amount amount = new Amount();
        amount.setCurrency(args.getAmountArg().getCurrencyAsString());
        amount.setTotal(args.getAmountArg().getAmountAsString());

        List<Item> items=new ArrayList<Item>();
        items.add(new Item("1", "Premium", amount.getTotal(), amount.getCurrency()));

        ItemList itemList=new ItemList();
        itemList.setItems(items);

        Transaction transaction = new Transaction();
        transaction.setDescription(args.getDescriptionArg());
        transaction.setItemList(itemList);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(args.getCancelledURLArg().toExternalForm());
        redirectUrls.setReturnUrl(args.getApprovedURLArg().toExternalForm());
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }
}
