package com.ail.payment;

import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;

public class PayPalExperiment {
    private final String PAYPAL_CLIENT_ID = "AQkquBDf1zctJOWGKWUEtKXm6qVhueUEMvXO_-MCI4DQQ4-LWvkDLIN2fGsd";
    private final String PAYPAL_CLIENT_SECRET = "EL1tVxAjhT7cJimnz5-Nsx9k2reTKSVfErNQF-CmrwJgxRtylkGTKlU4RvrX";

    @Test
    public void runIntegrationWithPayPal() throws PayPalRESTException, MalformedURLException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        Payment payment = createPayment(accessToken);
        assertNotNull(payment);

        URL approvalURL = fetchApprovalURL(payment);
        assertNotNull(approvalURL);
        
        System.out.println(approvalURL);
    }

    private URL fetchApprovalURL(Payment payment) throws MalformedURLException {
        for (Links link: payment.getLinks()) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                return new URL(link.getHref());
            }
        }
        throw new IllegalStateException("approval forward URL not found in payment");
    }

    private String getAccessToken() throws PayPalRESTException {
        Map<String, String> sdkConfig = new HashMap<String, String>();

        sdkConfig.put("mode", "sandbox");

        OAuthTokenCredential credential = new OAuthTokenCredential(PAYPAL_CLIENT_ID, PAYPAL_CLIENT_SECRET, sdkConfig);

        return credential.getAccessToken();
    }

    private Payment createPayment(String accessToken) throws PayPalRESTException {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");

        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(sdkConfig);

        Amount amount = new Amount();
        amount.setCurrency("GBP");
        amount.setTotal("12000");

        Transaction transaction = new Transaction();
        transaction.setDescription("MotorPlus Premium Payment");
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
        redirectUrls.setCancelUrl("http://www.bing.com");
        redirectUrls.setReturnUrl("http://www.google.com");
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }
}
