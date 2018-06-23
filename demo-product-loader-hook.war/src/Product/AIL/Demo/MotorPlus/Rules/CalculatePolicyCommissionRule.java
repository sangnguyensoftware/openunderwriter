import com.ail.insurance.quotation.CalculatePolicyCommissionService.CalculatePolicyCommissionArgument;
import com.ail.util.Rate;
import com.ail.insurance.policy.*;

public class CalculatePolicyCommissionRule {
    public static void invoke(CalculatePolicyCommissionArgument args) {
        AssessmentSheet sheet=args.getPolicyArg().getAssessmentSheet();
        AssessmentLine line=new RateBehaviour(sheet.generateLineId(), "Commission", null, "total premium", "total premium", BehaviourType.COMMISSION, new Rate("5%"));
        sheet.addLine(line);
    }
}
