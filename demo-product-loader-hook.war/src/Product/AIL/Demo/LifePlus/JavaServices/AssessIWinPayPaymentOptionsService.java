/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.util.ArrayList;
import java.util.List;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.IWinPay;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class AssessIWinPayPaymentOptionsService {

    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        // Only add options if they aren't already there
        if (policy.getPaymentOption() == null || policy.getPaymentOption().size() == 0) {
            policy.setPaymentOption(new ArrayList<PaymentSchedule>());

            CurrencyAmount premium = policy.getTotalPremium();

            List<MoneyProvision> payments = new ArrayList<>();

            payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new IWinPay(), null, null, "LifePlus premium payment"));

            PaymentSchedule schedule = new PaymentSchedule(payments, "A single payment from iWinPay of " + premium.toString());

            policy.getPaymentOption().add(schedule);

            policy.setPaymentDetails(schedule);
        }
    }
}
