package com.ail.payment;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.DumpService;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

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
public class PaymentCancelledService {
    
    @ServiceArgument
    public interface PaymentCancelledArgument extends Argument {
        String getPaymentTokenArg();
        
        void setPaymentTokenArg(String paymentTokenArg);
    }
    
    @ServiceCommand(defaultServiceClass=DumpService.class)
    public interface PaymentCancelledCommand extends PaymentCancelledArgument, Command {
    }
}
