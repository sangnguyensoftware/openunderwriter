
/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.util.ArrayList;
import java.util.List;

import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.core.language.I18N;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PayPal;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.PaymentMethod;

/**
 * The BuyWithPayPayButton requires that the policy contains a PayPal based
 * MoneyProvision at status NEW. This method creates such a MoneyProvision.
 */
public class AddPayPalMoneyProvisionToPolicyService {
    public static void invoke(ExecutePageActionArgument args) {

        Policy policy = (Policy) args.getModelArgRet();

        if (policy.getPaymentDetails() == null) {
            CurrencyAmount amount = policy.getTotalPremium();
            String description = I18N.i18n("i18n_buy_with_paypal_payment_description");
            PaymentMethod pm = new PayPal();

            MoneyProvision mp = new MoneyProvision(amount, PREMIUM, pm, description);

            List<MoneyProvision> moneyProvision = new ArrayList<MoneyProvision>();

            moneyProvision.add(mp);

            PaymentSchedule ps = new PaymentSchedule(moneyProvision, description);

            policy.setPaymentDetails(ps);
        }
    }
}