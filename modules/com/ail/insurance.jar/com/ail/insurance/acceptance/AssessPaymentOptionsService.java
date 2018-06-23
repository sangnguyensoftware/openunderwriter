/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.insurance.acceptance;

import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

/**
 * Given a policy at quote status this service returns a list of appropriate payment options.
 * This base service implementation adopts the following rules and constraints:<ul>
 * <li>Precondition: policy!=null</li>
 * <li>Precondition: policy.status==QUOTATION</li>
 * <li>Precondition: policy.totalPremium!=null && policy.totalPremium>0</li>
 * <li>Constraint: Only GBP is supported</li>
 * <li>Postcondition: options will contain at least two schedules:<ol>
 *     <li>A single payment by payment card</li>
 *     <li>A single payment by direct debit</li></ol>
 *  <li>Postcondition: If total premium > 200, then schedule will include a third option:<ol>
 *     <li>A one time payment of X, followed by 9 payments of Y per month. All payments by direct debit.</li></ol>
 *  </ul>
 * Note: These rules are a default set only and are expected to be overridden in live implementations.
 */
@ServiceImplementation
public class AssessPaymentOptionsService extends com.ail.core.Service<AssessPaymentOptionsService.AssessPaymentOptionsArgument> {
    private static final long serialVersionUID = 1871676649916485145L;

    @ServiceArgument
    public interface AssessPaymentOptionsArgument extends Argument {
        /**
         * Getter for the policyArg property. Policy to collect premium for
         * @return Value of policyArg, or null if it is unset
         */
        Policy getPolicyArg();

        /**
         * Setter for the policyArg property.
         * @see #getPolicyArg
         * @param policyArg new value for property.
         */
        void setPolicyArg(Policy policyArg);

        /**
         * Getter returning a list of schedules, each of which is an option
         * @return List of schedule
         */
        ArrayList<PaymentSchedule> getOptionsRet();

        /**
         * Setter for the schedule options
         * @see #getOptionRet()
         * @param option
         */
        void setOptionsRet(ArrayList<PaymentSchedule> option);
    }

    @ServiceCommand(defaultServiceClass=AssessPaymentOptionsService.class)
    public interface AssessPaymentOptionsCommand extends Command, AssessPaymentOptionsArgument {}

    @Override
    public void invoke() throws PreconditionException, PostconditionException {
		Policy policy = args.getPolicyArg();

		// check preconditiond
		if(policy==null){
			throw new PreconditionException("policy==null");
		}

		PolicyStatus status = policy.getStatus();
		if(status==null){
			throw new PreconditionException("status==null");
		}
        if(!status.equals(PolicyStatus.QUOTATION)){
			throw new PreconditionException("!status.equals(PolicyStatus.QUOTATION)");
		}

        CurrencyAmount premium=policy.getTotalPremium();
        if (premium==null || premium.getAmount().doubleValue()==0) {
            throw new PreconditionException("premium==null || premium.getAmount().doubleValue()==0");
        }

        args.setOptionsRet(new ArrayList<PaymentSchedule>());

        ArrayList<MoneyProvision> payments;

        // First option: Whole premium in one payment by payment card.
        payments=new ArrayList<>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new PaymentCard(), null, null, null));
        args.getOptionsRet().add(new PaymentSchedule(payments, "A single payment by payment card"));

        // second option: Whole premium in one payment by direct debit.
        payments=new ArrayList<>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new DirectDebit(), null, null, null));
        args.getOptionsRet().add(new PaymentSchedule(payments, "A single payment by direct debit"));

        if (premium.greaterThan(200, Currency.GBP)) {
            // second option: 10 payments. 9 rounded to the nearest (pounds)10, 1 (the first) containing the balance. Payment by direct debit.
            CurrencyAmount pmnt=new CurrencyAmount(premium.getAmount().divide(new BigDecimal(100)).intValue()*10, Currency.GBP);
            CurrencyAmount fstPmnt=new CurrencyAmount(pmnt.getAmount().add(premium.getAmount().remainder(new BigDecimal(100))), Currency.GBP);
            payments=new ArrayList<>();
            payments.add(new MoneyProvision(1, fstPmnt, PREMIUM, ONE_TIME, new DirectDebit(), null, null, null));
            payments.add(new MoneyProvision(9, pmnt, PREMIUM, MONTHLY, new DirectDebit(), null, null, null));
            args.getOptionsRet().add(new PaymentSchedule(payments, "A single payment of "+fstPmnt.getAmountAsString()+", followed by 9 monthly installments of "+pmnt.getAmountAsString()+". All by direct debit."));
        }

        // check postconditions
        if (args.getOptionsRet()==null || args.getOptionsRet().isEmpty()) {
            throw new PostconditionException("args.getOptionsRet()==null || args.getOptionsRet().isEmpty()");
        }
    }
}


