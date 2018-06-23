
/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class SetPolicyStatusService {
    /**
     * Set the quotation status
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        String status = (String) args.getActionArg().xpathGet("attribute[id='policyStatus']/value");
        policy.setStatus(PolicyStatus.forName(status));
    }
}