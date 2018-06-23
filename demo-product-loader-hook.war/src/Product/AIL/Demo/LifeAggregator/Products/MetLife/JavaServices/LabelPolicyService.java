import com.ail.core.Attribute;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class LabelPolicyService {
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        policy.getAttribute().add(new Attribute("label-policy-service", "LabelPolicyService called - Success!", "string"));
    }
}
