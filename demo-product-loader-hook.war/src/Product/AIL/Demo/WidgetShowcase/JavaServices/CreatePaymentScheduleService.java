
/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
import static com.ail.financial.Currency.GBP;
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentMethod;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Create a payment schedule and associate the policy as its paymentDetails.
 */
public class CreatePaymentScheduleService {
    /**
     * Service entry point.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        if (policy.getPaymentDetails() == null) {
            setupPaymentMethod(policy);
            setupPaymentDetails(policy);
        }
    }

    private static void setupPaymentMethod(Policy policy) {
        PaymentMethod pm = new DirectDebit("01200789", "010901", "Mr T. Tester");
        policy.getClient().getPaymentMethod().add(pm);
    }

    private static void setupPaymentDetails(Policy policy) {
        PaymentMethod paymentMethod = (PaymentMethod) policy.getClient().getPaymentMethod().get(0);

        List<MoneyProvision> moneyProvisions = new ArrayList<MoneyProvision>();

        CurrencyAmount amount = new CurrencyAmount(50, GBP);

        MoneyProvision moneyProvision = new MoneyProvision(amount, PREMIUM, paymentMethod, "Monthly payment");
        moneyProvision.setPaymentsStartDate(new Date());
        moneyProvision.setPaymentsEndDate(null);
        moneyProvision.setDay(5);
        moneyProvision.setFrequency(MONTHLY);
        moneyProvision.setPurpose(PREMIUM);

        moneyProvisions.add(moneyProvision);

        PaymentSchedule schedule = new PaymentSchedule(moneyProvisions, "Premium payment");

        policy.setPaymentDetails(schedule);
    }
}