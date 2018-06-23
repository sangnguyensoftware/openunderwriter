/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
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
 * Assess the payment options for LifePlus. This service is called once the
 * proposer selects to 'confirm and pay'. It prepares the payment schedules
 * which supported by the product and returns them to the caller. In the case of
 * LifePlus there is just one option - pay by payment card, so this service
 * creates the schedule (just one payment) and selects it as the active payment
 * details for the quote.
 */
public class AssessBankPaymentOptionsService {
    /**
     * Class entry point.
     *
     * @param args
     *            Service arguments
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy quote = (Policy) args.getModelArgRet();
        // Only add options if they aren't already there
        if (quote.getPaymentOption() == null || quote.getPaymentOption().size() == 0) {
            quote.setPaymentOption(new ArrayList<PaymentSchedule>());

            CurrencyAmount premium = quote.getTotalPremium();

            addOptionOne(premium, quote);

            PaymentSchedule schedule = (PaymentSchedule) quote.getPaymentOption().get(0);

            quote.setPaymentDetails(schedule);
        }
    }

    /**
     * First option: Whole premium in one payment by payment card.
     *
     * @param premium
     *            Premium to base the schedule on
     * @param args
     *            Schedule will be added to args.getOptionsRet()
     */
    private static void addOptionOne(CurrencyAmount premium, Policy quote) {
        List<MoneyProvision> payments = new ArrayList<MoneyProvision>();

        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new PaymentCard(), null, null, null));

        quote.getPaymentOption().add(new PaymentSchedule(payments, "A single payment by payment card of " + premium.toString()));
    }
}
