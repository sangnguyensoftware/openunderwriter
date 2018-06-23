package com.ail.payment;

import java.net.URL;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.DumpService;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.CurrencyAmount;
import com.ail.party.Address;

/**
 * Service interface defining a generalised model for payment service
 * implementations.
 *
 * The payment process starts with a PaymentRequestService. This returns a URL
 * to forward to in order to get user authorisation. That will either succeed
 * and lead to the PaymentApprovedService being called; or, fail and lead to
 * PaymentCancelledService being called. Following the PaymentApprovedService
 * call, the PaymentExecutionServer must be called to execute the payment.
 */
@ServiceInterface
public class PaymentRequestService {

    @ServiceArgument
    public interface PaymentRequestArgument extends Argument {

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
         * The amount to be paid
         */
        CurrencyAmount getAmountArg();

        /**
         * @see #getAmountArg()
         */
        void setAmountArg(CurrencyAmount amountArg);

        /**
         * The URL returned by the service which must be used to authorise the
         * payment. The client of this service will typically need to forward
         * the user to this URL in order for them to authorise the payment.
         */
        URL getAuthorisationURLRet();

        /**
         * @see #getAuthorisationURLRet()
         * @param authorisationURLRet
         */
        void setAuthorisationURLRet(URL authorisationURLRet);

        /**
         * The request details encrypted in such a way that they can be transmitted
         * to the payment system.
         */
        String getEncryptedRequestRet();

        /**
         * @see #getEncryptedRequestRet()
         * @param encryptedRequestRet
         */
        void setEncryptedRequestRet(String encryptedRequestRet);

        /**
         * The URL to which the user should be forwarded if the payment process
         * is cancelled.
         */
        URL getCancelledURLArg();

        /**
         * @see #getCancelledURLArg()
         * @param cancelledURLArg
         */
        void setCancelledURLArg(URL cancelledURLArg);

        /**
         * URL to forward the user to when they payment is authorised and
         * approved.
         */
        URL getApprovedURLArg();

        /**
         * @see #getApprovedURLArg()
         * @param approvedURLArg
         */
        void setApprovedURLArg(URL approvedURLArg);

        /**
         * Description of the transaction in terms of what is being purchased.
         */
        String getDescriptionArg();

        /**
         * @see #getDescriptionArg()
         */
        void setDescriptionArg(String descriptionArg);

        /**
         * The ID of the request sent sent to the payment provider.
         *
         * @param id
         */
        void setPaymentIdRet(String id);

        /**
         * @see #setPaymentIdRet(String)
         * @return
         */
        String getPaymentIdRet();

        /**
         * The name of the customer as it will appear on the 3rd party system's screens etc
         * @param customerNameArg
         */
        void setCustomerNameArg(String customerNameArg);

        /**
         * @see #setCustomerNameArg(String)
         */
        String getCustomerNameArg();

        void setCustomerFirstnameArg(String billingFirstnameArg);

        String getCustomerFirstnameArg();

        void setCustomerSurnameArg(String billingSurnameArg);

        String getCustomerSurnameArg();

        /**
         * The billing address of the payment
         * @param addressArg
         */
        void setCustomerAddressArg(Address addressArg);

        /**
         * @see #setCustomerAddressArg(Address)
         */
        Address getCustomerAddressArg();

        void setCustomerEmailAddressArg(String customerEmailAddressArg);

        String getCustomerEmailAddressArg();

        void setCustomerMobileNumberArg(String customerMobileNumberArg);

        String getCustomerMobileNumberArg();

        void setCustomerIdArg(String token);

        String getCustomerIdArg();
    }

    @ServiceCommand(defaultServiceClass = DumpService.class)
    public interface PaymentRequestCommand extends PaymentRequestArgument, Command {
    }
}
