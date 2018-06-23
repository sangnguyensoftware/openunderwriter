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

import java.net.URL;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.payment.PaymentRequestService;
import com.sagepay.sdk.api.ApiFactory;
import com.sagepay.sdk.api.ProtocolVersion;
import com.sagepay.sdk.api.TransactionType;
import com.sagepay.sdk.api.messages.IFormPayment;
import com.ail.party.Country;
/**
 */
@ServiceImplementation
public class SagePayPaymentRequestService extends Service<PaymentRequestService.PaymentRequestArgument> {

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
        
        if (args.getCustomerAddressArg() == null) {
            throw new PreconditionException("args.getCustomerAddressArg() == null");
        }
        
        if (args.getCustomerAddressArg().getLine1() == null || args.getCustomerAddressArg().getLine1().length() == 0) {
            throw new PreconditionException("args.getCustomerAddressArg().getLine1() == null || args.getCustomerAddressArg().getLine1().length() == 0");
        }

        if (args.getCustomerAddressArg().getTown() == null || args.getCustomerAddressArg().getTown().length() == 0) {
            throw new PreconditionException("args.getCustomerAddressArg().getTown() == null || args.getCustomerAddressArg().getTown().length() == 0");
        }

        if (args.getCustomerAddressArg().getPostcode() == null || args.getCustomerAddressArg().getPostcode().length() == 0) {
            throw new PreconditionException("args.getCustomerAddressArg().getPostcode() == null || args.getCustomerAddressArg().getPostcode().length() == 0");
        }

        if (args.getCustomerAddressArg().getCountry() == null || args.getCustomerAddressArg().getCountry().length() == 0) {
            throw new PreconditionException("args.getCustomerAddressArg().getCountry() == null || args.getCustomerAddressArg().getCountry().length() == 0");
        }

        if (args.getCustomerFirstnameArg() == null || args.getCustomerFirstnameArg().length() == 0) {
            throw new PreconditionException("args.getCustomerFirstnameArg() == null || args.getCustomerFirstnameArg().length() == 0");
        }

        if (args.getCustomerSurnameArg() == null || args.getCustomerSurnameArg().length() == 0) {
            throw new PreconditionException("args.getCustomerSurnameArg() == null || args.getCustomerSurnameArg().length() == 0");
        }

        try {
            String namespace = Functions.productNameToConfigurationNamespace(args.getProductTypeIdArg());
            setCore(new CoreProxy(namespace, args.getCallersCore()).getCore());

            IFormPayment formPayment = createFormPayment();
            
            args.setEncryptedRequestRet(encryptForm(formPayment));
            args.setPaymentIdRet(formPayment.getVendorTxCode());
            args.setAuthorisationURLRet(new URL(mandatorySetting("PaymentMethods.SagePay.URL")));
        } catch (Exception e) {
            throw new PostconditionException("Encountered Exception interfacing with SagePay.", e);
        }

        if (args.getAuthorisationURLRet() == null) {
            throw new PostconditionException("args.getAuthorisationURLRet()==null");
        }

        if (args.getEncryptedRequestRet() == null) {
            throw new PostconditionException("args.getEncryptedRequestRet()==null");
        }
    }

    String encryptForm(IFormPayment formPayment) throws PreconditionException {
        String key=mandatorySetting("PaymentMethods.SagePay.EncryptionPassword");
        ApiFactory.getFormApi().encrypt(key, formPayment);
        return formPayment.getCrypt();
    }

    IFormPayment createFormPayment() throws PreconditionException {
        IFormPayment ifp = ApiFactory.getFormApi().newFormPaymentRequest();

        ifp.setTransactionType(TransactionType.PAYMENT);

        ifp.setVpsProtocol(ProtocolVersion.V_300);
        ifp.setAmount(args.getAmountArg().getAmount());
        ifp.setCurrency(args.getAmountArg().getCurrencyAsString());
        ifp.setDescription(args.getDescriptionArg());
        ifp.setSuccessUrl(args.getApprovedURLArg().toExternalForm());
        ifp.setFailureUrl(args.getCancelledURLArg().toExternalForm());
        ifp.setCustomerName(args.getCustomerNameArg());
        ifp.setVendorTxCode(generateTransactionCode());
        
        String isoCountryCode=Country.forName(args.getCustomerAddressArg().getCountry()).getTwoLetterCode();
        
        ifp.setBillingFirstnames(args.getCustomerFirstnameArg());
        ifp.setBillingSurname(args.getCustomerSurnameArg());
        ifp.setBillingAddress1(args.getCustomerAddressArg().getLine1());
        ifp.setBillingAddress2(args.getCustomerAddressArg().getLine2());
        ifp.setBillingCity(args.getCustomerAddressArg().getTown());
        if ("US".equals(isoCountryCode)) {
            ifp.setBillingState(args.getCustomerAddressArg().getCounty());
        }
        ifp.setBillingCountry(isoCountryCode);
        ifp.setBillingPostCode(args.getCustomerAddressArg().getPostcode());
        
        ifp.setDeliveryFirstnames(args.getCustomerFirstnameArg());
        ifp.setDeliverySurname(args.getCustomerSurnameArg());
        ifp.setDeliveryAddress1(args.getCustomerAddressArg().getLine1());
        ifp.setDeliveryAddress2(args.getCustomerAddressArg().getLine2());
        ifp.setDeliveryCity(args.getCustomerAddressArg().getTown());
        if ("US".equals(isoCountryCode)) {
            ifp.setDeliveryState(args.getCustomerAddressArg().getCounty());
        }
        ifp.setDeliveryCountry(isoCountryCode);
        ifp.setDeliveryPostCode(args.getCustomerAddressArg().getPostcode());

        if (args.getCustomerEmailAddressArg() != null) {
            ifp.setCustomerEmail(args.getCustomerEmailAddressArg());
        }
        
        ifp.setVendor(mandatorySetting("PaymentMethods.SagePay.Vendor"));
        ifp.setReferrerId(optionalSetting("PaymentMethods.SagePay.PartnerID"));
        ifp.setSurchargeXml(optionalSetting("PaymentMethods.SagePay.SurchargeXml"));
        ifp.setApply3dSecure(Integer.parseInt(mandatorySetting("PaymentMethods.SagePay.Apply3dSecure")));
        ifp.setVendorEmail(optionalSetting("PaymentMethods.SagePay.VendorEmail"));
        ifp.setSendEmail(Integer.parseInt(mandatorySetting("PaymentMethods.SagePay.SendMail")));
        ifp.setEmailMessage(optionalSetting("PaymentMethods.SagePay.EmailMessage"));

        return ifp;
    }

    String generateTransactionCode() throws PreconditionException {
        String vendorName = mandatorySetting("PaymentMethods.SagePay.Vendor");
        String txCode = vendorName.substring(0, Math.min(18, vendorName.length())) + // 18 chars
                "-" + // 1 char
                System.currentTimeMillis() + // 13 chars
                "-" + // 1 char
                (int) Math.abs(Math.random() * 1000000); // 6 chars
        return txCode;
    }

    String mandatorySetting(String propertyName) throws PreconditionException {
        String value = CoreContext.getCoreProxy().getParameterValue(propertyName);

        if (value == null || value.length() == 0) {
            throw new PreconditionException("Registry does not define the mandatory parameter: " + propertyName);
        }

        return value;
    }

    String optionalSetting(String propertyName) throws PreconditionException {
        return CoreContext.getCoreProxy().getParameterValue(propertyName);
    }
}
