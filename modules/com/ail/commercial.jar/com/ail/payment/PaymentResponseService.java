package com.ail.payment;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.DumpService;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.PaymentRequestStatus;

/**
 * Service interface defining a generalised model for payment service
 * implementations.
 *
 * The payment process starts with a PaymentRequestService. This returns a URL
 * to forward to in order to get user authorisation. Once the authorisation
 * service returns (which generally means on return from the payment provider's
 * site) the PaymentStatusService is used to determine what to do next. In the
 * case of success it will lead to the PaymentApprovedService being called; or,
 * in the case of failure will lead to PaymentCancelledService being called.
 * Following the PaymentApprovedService call, the PaymentExecutionServer must be
 * called to execute the payment.
 */
@ServiceInterface
public class PaymentResponseService {

    @ServiceArgument
    public interface PaymentResponseArgument extends Argument {
        String getPaymentIdArgRet();

        void setPaymentIdArgRet(String paymentIdArg);

        PaymentRequestStatus getPaymentStatusRet();

        void setPaymentStatusRet(PaymentRequestStatus statusRet);
    }

    @ServiceCommand(defaultServiceClass = DumpService.class)
    public interface PaymentResponseCommand extends PaymentResponseArgument, Command {
    }
}
