
/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.util.ArrayList;
import java.util.List;

import com.ail.financial.CurrencyAmount;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.acceptance.AssessPaymentOptionsService.AssessPaymentOptionsArgument;

/**
 * Basic assess payment options rule. This rule always returns a single payment
 * option: A single payment in full by card.
 */
public class AssessPaymentOptionsRule {
    /**
     * Class entry point.
     * 
     * @param args
     *            Service arguments
     */
    public static void invoke(AssessPaymentOptionsArgument args) {
        args.setOptionsRet(new ArrayList<PaymentSchedule>());

        CurrencyAmount premium = args.getPolicyArg().getTotalPremium();

        /* One option: Whole premium in one payment by payment card. */
        List<MoneyProvision> payments = new ArrayList<MoneyProvision>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new PaymentCard(), null, null, null));
        args.getOptionsRet().add(new PaymentSchedule(payments, "A single payment by payment card of " + premium.toString()));
    }
}
