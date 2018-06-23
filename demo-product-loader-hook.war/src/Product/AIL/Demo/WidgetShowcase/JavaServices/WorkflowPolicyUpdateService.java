/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;

public class WorkflowPolicyUpdateService {
    public static void invoke(ExecutePageActionArgument args) {
        
    	Policy policy = (Policy) args.getModelArgRet();

        String policyNumber = (String) policy.xpathGet("/asset[id='workflowTestData']/attribute[id='policyNumber']/value");
        
        policy.setPolicyNumber(policyNumber);
    }
}