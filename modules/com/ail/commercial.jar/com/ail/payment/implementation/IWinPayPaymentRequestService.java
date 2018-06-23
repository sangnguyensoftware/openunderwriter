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

import static java.util.Calendar.DATE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.payment.PaymentRequestService;

/**
 */
@ServiceImplementation
public class IWinPayPaymentRequestService extends Service<PaymentRequestService.PaymentRequestArgument> {

    private static final String OPERATION_REQUEST = "{\"operation\": \"login\", \"params\": []}";

    @Override
    public void invoke() throws BaseException {

        if (args.getAmountArg() == null) {
            throw new PreconditionException("args.getAmountArg() == null");
        }

        if (args.getApprovedURLArg() == null) {
            throw new PreconditionException("args.getApprovedURLArg() == null");
        }

        if (args.getCancelledURLArg() == null) {
            throw new PreconditionException("args.getCancelledURLArg() == null");
        }

        if (args.getDescriptionArg() == null) {
            throw new PreconditionException("args.getDescriptionArg() == null");
        }

        if (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0) {
            throw new PreconditionException("args.getProductTypeIdArg()==null || args.getProductTypeIdArg().length()==0");
        }

        if (args.getCustomerMobileNumberArg() == null || args.getCustomerMobileNumberArg().length() == 0) {
            throw new PreconditionException("args.getCustomerMobileNumberArg() == null || args.getCustomerMobileNumberArg().length() == 0");
        }

        HttpURLConnection connection = null;

        try {
            selectProductConfiguration();

            connection = openConnection(createRequestURL());

            processResponse(connection);
        } catch(PreconditionException e) {
            throw e;
        }catch (Exception e) {
            throw new PostconditionException("Encountered Exception interfacing with iWinPay.", e);
        }
        finally {
            if (connection!=null) {
                connection.disconnect();
            }
        }

        if (args.getPaymentIdRet() == null) {
            throw new PostconditionException("args.getPaymentIdRet() == null");
        }
    }

    private String createRequestURL() throws PreconditionException, IOException {
        String url = getCore().getParameterValue("PaymentMethods.iWinPay.URL");

        if (url == null) {
            throw new PreconditionException("getCore().getParameterValue('PaymentMethods.iWinPay.URL')==null");
        }

        String clientId = getCore().getParameterValue("PaymentMethods.iWinPay.ClientID");

        if (clientId == null) {
            throw new PreconditionException("getCore().getParameterValue('PaymentMethods.iWinPay.ClientID')==null");
        }

        String clientSecret = getCore().getParameterValue("PaymentMethods.iWinPay.ClientSecret");

        if (clientSecret == null) {
            throw new PreconditionException("getCore().getParameterValue('PaymentMethods.iWinPay.ClientSecret')==null");
        }

        String principalType = getCore().getParameterValue("PaymentMethods.iWinPay.PrincipalType","username");

        return String.format("%s/performRequestPayment?amount=%s&principal=%s&principalType=%s&expiryDate=%s&paymentType=%s&paymentDescription=%s&client_id=%s&client_secret=%s",
                url,
                URLEncoder.encode(args.getAmountArg().getAmount().toString(), "UTF-8"),
                URLEncoder.encode(args.getCustomerMobileNumberArg(), "UTF-8"),
                principalType,
                URLEncoder.encode(calculateExpiryDate(), "UTF-8"),
                URLEncoder.encode(calculatePaymentType(), "UTF-8"),
                URLEncoder.encode(args.getDescriptionArg(), "UTF-8"),
                clientId,
                clientSecret);
    }

    /**
     * There are two variations of payment type:
     * <ol><li>for same currency transaction (where the payer and the payee have the same currency).
     * Payment type is in the form:<br/>&nbsp; [currency]Wallet.[currency]Payment<br/>
     * where <currency> is the lower case three letter currency code from payment.<br/><br/></li>
     * <li>FX transactions where the payer and payee have different currencies. Payment type is in
     * the form:<br/>
     * &nbsp;[currency B]Wallet.[currency A][currency B]FXPayment<br/>
     * For example: usdPayment.usdGbpFXPayment would made a GBP payment into a USD wallet.
     * </li></ol>
     * @throws PreconditionException
     */
    String calculatePaymentType() throws PreconditionException {
        String walletCurrency = getCore().getParameterValue("PaymentMethods.iWinPay.WalletCurrency");

        if (walletCurrency == null) {
            throw new PreconditionException("getCore().getParameterValue('PaymentMethods.iWinPay.WalletCurrency')==null");
        }

        String paymentCurrency = args.getAmountArg().getCurrencyAsString();

        if (paymentCurrency.equals(walletCurrency)) {
            return walletCurrency.toLowerCase() + "Wallet." + walletCurrency.toLowerCase() + "Payment";
        }
        else {
            return walletCurrency.toLowerCase() + "Wallet." + walletCurrency.toLowerCase() + namecase(paymentCurrency) + "FxPayment";
        }
    }

    /**
     * Examples: "name" -> "Name", "GBP" -> "Gbp"
     * @param s
     * @return
     */
    private String namecase(String s) {
        return Character.toUpperCase(s.charAt(0))+s.substring(1).toLowerCase();
    }

    private String calculateExpiryDate() {
        Calendar calendar=Calendar.getInstance();
        calendar.add(DATE, 1);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    private HttpURLConnection openConnection(String requestURL) throws PreconditionException, IOException, PostconditionException {
        String credentials = getCore().getParameterValue("PaymentMethods.iWinPay.Credentials");

        if (credentials == null) {
            throw new PreconditionException("getCore().getParameterValue('PaymentMethods.iWinPay.Credentials')==null");
        }

        HttpURLConnection conn = openHttpURLConnection(requestURL);

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", credentials);

        OutputStream os = conn.getOutputStream();
        os.write(OPERATION_REQUEST.getBytes());
        os.flush();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new PostconditionException("Expected HTTP response code 200 (OK), but received: " + conn.getResponseCode());
        }

        return conn;
    }

    HttpURLConnection openHttpURLConnection(String request) throws MalformedURLException, IOException {
        URL requestURL = new URL(request);
        return (HttpURLConnection) requestURL.openConnection();
    }

    private void processResponse(HttpURLConnection connection) throws IOException, JSONException {

        String result=convertStreamToString(connection.getInputStream());

        JSONObject jsonObject=new JSONObject(result);

        String transactioNumber = jsonObject.getJSONObject("result").getString("transactionNumber");

        args.setPaymentIdRet(transactioNumber);
    }

    static String convertStreamToString(InputStream is) {
        @SuppressWarnings("resource")
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void selectProductConfiguration() {
        String namespace = Functions.productNameToConfigurationNamespace(args.getProductTypeIdArg());

        setCore(new CoreProxy(namespace, args.getCallersCore()).getCore());
    }
}
