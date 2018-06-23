import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.util.ArrayList;
import java.util.List;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Assess the payment options for InfinityLife. This service is called once the
 * proposer selects to 'confirm and pay'. It prepares the payment schedules
 * which supported by the product and returns them to the caller. In the case of
 * InfinityPlus there is just one option - pay by payment card, so this service
 * creates the schedule (just one payment) and selects it as the active payment
 * details for the quote.
 */
public class AssessPaymentOptionsService {
    /**
     * Class entry point.
     *
     * @param args
     *            Service arguments
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        // Only add options if they aren't already there
        if (policy.getPaymentOption() == null || policy.getPaymentOption().size() == 0) {
            policy.setPaymentOption(new ArrayList<PaymentSchedule>());

            CurrencyAmount premium = policy.getTotalPremium();

            addOptionOne(premium, policy);

            PaymentSchedule schedule = (PaymentSchedule)policy.getPaymentOption().get(0);

            policy.setPaymentDetails(schedule);
        }
    }

    /**
     * Only option: Whole premium in one payment by payment card.
     *
     * @param premium
     *            Premium to base the schedule on
     * @param args
     *            Schedule will be added to args.getOptionsRet()
     */
    private static void addOptionOne(CurrencyAmount premium, Policy policy) {
        List<MoneyProvision> payments = new ArrayList<MoneyProvision>();

        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new PaymentCard(), null, null, null));

        policy.getPaymentOption().add(new PaymentSchedule(payments, "A single payment by payment card of " + premium.toString()));
    }
}
