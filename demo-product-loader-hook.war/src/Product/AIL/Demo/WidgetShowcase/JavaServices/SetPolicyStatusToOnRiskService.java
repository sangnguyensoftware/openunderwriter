
/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class SetPolicyStatusToOnRiskService {
    /**
     * Set the quotation date to null to allow the quotation to be processed.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        policy.setStatus(PolicyStatus.ON_RISK);
    }
}