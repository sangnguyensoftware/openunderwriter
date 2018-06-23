/* Copyright Applied Industrial Logic Limited 2008. All rights reserved. */
import com.ail.core.BaseException;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

/**
 * This service is invoked in response to the agent opting to place the order.
 */
public class PlaceOrderService {
    /**
     * Service entry point.
     * @param args Contains the quotation object to me modified 
     */
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        Policy quote=(Policy)args.getModelArgRet();
        
        quote.setStatus(PolicyStatus.SUBMITTED);
    }
}