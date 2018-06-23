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
public class PaymentExecutionService {
    
    @ServiceArgument
    public interface PaymentExecutionArgument extends Argument {

        /**
         * The ID of the product this this payment is being made in relation to.
         */
        String getProductTypeIdArg();
        
        /**
         * @see #getProductTypeIdArg()
         * @param productTypeIdArg
         */
        void setProductTypeIdArg(String productTypeIdArg);
        
        String getPayerIdArg();
        
        void setPayerIdArg(String payerIdArg);
        
        String getPaymentIdArg();
        
        void setPaymentIdArg(String transactionIdArg);
        
        String getSaleIdRet();
        
        void setSaleIdRet(String saleIdRet);
    }
    
    @ServiceCommand(defaultServiceClass=DumpService.class)
    public interface PaymentExecutionCommand extends PaymentExecutionArgument, Command {
    }
}
