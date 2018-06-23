/* Copyright Applied Industrial Logic Limited 2008. All rights reserved. */
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.BehaviourType;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.policy.SumBehaviour;
import com.ail.insurance.quotation.CalculatePremiumService.CalculatePremiumCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * When the agent requests an alternative premium to the one that the system
 * calculated, this service is called. The premium that the agent defines is the
 * gross premium, so this service has to undo the tax calculations, apply an
 * appropriate discount and re-apply the taxes in order to arrive at the figure
 * that the agent specifies.
 */
public class ApplyAgentPremiumService {
    /**
     * Service entry point.
     *
     * @param args
     *            Contains the quotation object to be modified
     */
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        Policy quote = (Policy) args.getModelArgRet();
        AssessmentSheet sheet = quote.getAssessmentSheet();

        CoreProxy core = new CoreProxy(Functions.productNameToConfigurationNamespace(quote.getProductTypeId()));

        CalculatePremiumCommand cpc = (CalculatePremiumCommand) core.newCommand(CalculatePremiumCommand.class);

        sheet.removeLinesByOrigin("ApplyAgentPremiumService");

        // Remove any lines we may have added in the past, and refresh to get
        // the total premium back to it's
        // non-discounted value
        quote.setStatus(PolicyStatus.APPLICATION);
        cpc.setPolicyArgRet(quote);
        cpc.invoke();
        quote = cpc.getPolicyArgRet();
        quote.setStatus(PolicyStatus.APPLICATION);

        boolean renewal = ("Yes".equals(quote.xpathGet("/asset[id='company']/attribute[id='renewalQuotation']/value")));
        boolean mta = ("Yes".equals(quote.xpathGet("/asset[id='company']/attribute[id='mtaQuotation']/value")));
        double requiredPremium = ((Number) quote.xpathGet("/asset[id='company']/attribute[id='agentPremium']/object")).doubleValue();
        double calculatedPremium = quote.getTotalPremium().getAmount().doubleValue();

        // Remove Stamp Duty where applicable
        if (!renewal && !mta) {
            requiredPremium -= 1.0;
            calculatedPremium -= 1.0;
        }

        // Remove Irish Government Levy
        requiredPremium *= (1.0 / 1.02);
        calculatedPremium *= (1.0 / 1.02);

        // Calculate and apply the discount
        double discount = calculatedPremium - requiredPremium;
        sheet.setLockingActor("ApplyAgentPremiumService");
        sheet.addLine(new SumBehaviour("agent discount", "Discount applied to arrive at agent premium", null, "total premium", BehaviourType.DISCOUNT, new CurrencyAmount(discount, Currency.EUR), -5));
        sheet.addReferral("Agent requested an alternative premium", null);
        sheet.clearLockingActor();

        // Calc premium again - now with the new override line in place
        quote.setStatus(PolicyStatus.APPLICATION);
        cpc.setPolicyArgRet(quote);
        cpc.invoke();
        quote = cpc.getPolicyArgRet();

        args.setModelArgRet(quote);
    }
}