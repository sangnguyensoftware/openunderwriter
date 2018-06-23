/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

public class DocumentButtonActionPolicyUpdateService {

	public static void invoke(ExecutePageActionArgument args) {
        
    	Policy policy = (Policy) args.getModelArgRet();

    	handleQuoteNumberOverride(policy);
        
    	handlePolicyStatusOverride(policy);

    	handlePolicyNumberOverride(policy);
    }

    static void handleQuoteNumberOverride(Policy policy) {
    	String quoteNumberOverride = (String) policy.xpathGet("/asset[id='commandActionButton']/attribute[id='quoteNumberOverride']/value");
       	policy.setQuotationNumber(quoteNumberOverride);
    }

    static void handlePolicyNumberOverride(Policy policy) {
    	String policyNumberOverride = (String) policy.xpathGet("/asset[id='commandActionButton']/attribute[id='policyNumberOverride']/value");
       	policy.setPolicyNumber(policyNumberOverride);
    }

    static void handlePolicyStatusOverride(Policy policy) {
    	String policyStatusOverride = (String) policy.xpathGet("/asset[id='commandActionButton']/attribute[id='policyStatusOverride']/formattedValue");

        if (!"?".equals(policyStatusOverride)) {
        	policy.setStatus(PolicyStatus.forName(policyStatusOverride));
        }
    }
}