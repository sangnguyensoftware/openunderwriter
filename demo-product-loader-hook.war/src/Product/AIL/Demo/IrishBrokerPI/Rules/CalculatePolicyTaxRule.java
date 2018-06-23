import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.BehaviourType;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.RateBehaviour;
import com.ail.insurance.policy.SumBehaviour;
import com.ail.insurance.quotation.CalculatePolicyTaxService.CalculatePolicyTaxArgument;
import com.ail.util.Rate;

/**
 * Calculate the tax applicable to Irish Broker PI. The rules here are simple:
 * - New business quotes incur 2% Irish Government Levy, and 1 EUR stamp duty.
 * - Renewals and MTAs incur just 2% Irish Government Levy.   
 */
public class CalculatePolicyTaxRule {
    public static void invoke(CalculatePolicyTaxArgument args) {
        AssessmentSheet sheet = args.getAssessmentSheetArgRet();
        Policy quote = args.getPolicyArg();
        
        boolean renewal=("Yes".equals(quote.xpathGet("/asset[id='company']/attribute[id='renewalQuotation']/value")));
        boolean mta=("Yes".equals(quote.xpathGet("/asset[id='company']/attribute[id='mtaQuotation']/value")));
        
        if (renewal || mta) {
            sheet.addLine(new RateBehaviour(sheet.generateLineId(), "Irish Government Levy", null, "total premium", "total premium", BehaviourType.TAX, new Rate("2%"), -10));
        }
        else {
            sheet.addLine(new RateBehaviour(sheet.generateLineId(), "Irish Government Levy", null, "total premium", "total premium", BehaviourType.TAX, new Rate("2%"), -10));
            sheet.addLine(new SumBehaviour(sheet.generateLineId(), "Stamp Duty", null, "total premium", BehaviourType.TAX, new CurrencyAmount(1, Currency.EUR), -20));
        }
    }
}
