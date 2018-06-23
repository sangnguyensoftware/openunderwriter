/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;

public class ResetMoneyProvisionInPolicyService {
	public static void invoke(ExecutePageActionArgument args) {

		Policy policy = (Policy) args.getModelArgRet();

		policy.setPaymentDetails(null);
	}
}