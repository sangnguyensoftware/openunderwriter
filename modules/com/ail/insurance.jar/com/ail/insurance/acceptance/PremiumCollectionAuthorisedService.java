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

import static com.ail.financial.MoneyProvisionStatus.REQUESTED;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionStatus;
import com.ail.financial.PaymentRecord;
import com.ail.financial.PaymentRecordType;
import com.ail.insurance.policy.Policy;

/**
 * Service used to mark previously requested payments to be approved. Using
 * PayPal as an example, this service should be invoked if a request has been
 * sent to PayPal and authorised by the user. In that situation PayPal forward
 * to a URL that we have specified. the handling of that forward should
 * ultimately invoke this service to ensure that the policy's payment history is
 * kept in sync with the event.
 */
@ServiceImplementation
public class PremiumCollectionAuthorisedService extends com.ail.core.Service<PremiumCollectionAuthorisedService.PremiumCollectionAuthorisedArgument> {
    private static final long serialVersionUID = 1871676649916485145L;

    @ServiceArgument
    public interface PremiumCollectionAuthorisedArgument extends Argument {
        /**
         * Getter for the policyArg property. Policy to collect premium for
         *
         * @return Value of policyArg, or null if it is unset
         */
        Policy getPolicyArgRet();

        /**
         * Setter for the policyArg property. * @see #getPolicyArg
         *
         * @param policyArg
         *            new value for property.
         */
        void setPolicyArgRet(Policy policyArg);
    }

    @ServiceCommand(defaultServiceClass = PremiumCollectionAuthorisedService.class)
    public interface PremiumCollectionAuthorisedCommand extends Command, PremiumCollectionAuthorisedArgument {
    }

    @Override
    public void invoke() throws BaseException {
        boolean requestFound = false;

        if (args.getPolicyArgRet() == null) {
            throw new PreconditionException("args.getPolicyArg()==null");
        }

        if (args.getPolicyArgRet().getPaymentHistory() == null) {
            throw new PreconditionException("args.getPolicyArgRet().getPaymentHistory() == null");
        }

        Policy policy = args.getPolicyArgRet();

        for (MoneyProvision mp : policy.getPaymentDetails().getMoneyProvision()) {
            if (REQUESTED.equals(mp.getStatus())) {
                mp.setStatus(MoneyProvisionStatus.AUTHORISED);
                policy.getPaymentHistory().add(new PaymentRecord(mp.getAmount(), mp.getPaymentId(), mp.getPaymentMethod(), PaymentRecordType.AUTHORISED));
                requestFound = true;
            }
        }

        if (!requestFound) {
            throw new PreconditionException("No money provisions were found with a status of REQUESTED.");
        }
    }
}
