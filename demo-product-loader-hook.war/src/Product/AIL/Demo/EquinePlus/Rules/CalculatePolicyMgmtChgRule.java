import com.ail.util.Rate;
import com.ail.insurance.policy.*;
import com.ail.insurance.quotation.CalculatePolicyManagementChargeService.CalculatePolicyManagementChargeArgument;

public class CalculatePolicyMgmtChgRule {
    public static void invoke(CalculatePolicyManagementChargeArgument args) {
        AssessmentSheet sheet=args.getPolicyArg().getAssessmentSheet();
        AssessmentLine line=new RateBehaviour(sheet.generateLineId(), "Calculated management charge", null, "total premium", "total premium", BehaviourType.MANAGEMENT_CHARGE, new Rate("5%"));
        sheet.addLine(line);
    }
}
