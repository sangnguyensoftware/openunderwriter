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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionStatus;
import com.ail.financial.PaymentRequestStatus;
import com.ail.insurance.acceptance.PremiumCollectionAuthorisedService.PremiumCollectionAuthorisedCommand;
import com.ail.insurance.acceptance.PremiumCollectionCancelledService.PremiumCollectionCancelledCommand;
import com.ail.insurance.acceptance.PremiumCollectionExecutionService.PremiumCollectionExecutionCommand;
import com.ail.insurance.policy.Policy;
import com.ail.payment.PaymentResponseService.PaymentResponseCommand;

@ServiceImplementation
public class PremiumCollectionResponseService extends com.ail.core.Service<PremiumCollectionResponseService.PremiumCollectionResponseArgument> {
    private static final long serialVersionUID = 1871676649916485145L;

    @ServiceArgument
    public interface PremiumCollectionResponseArgument extends Argument {
        Policy getPolicyArgRet();

        void setPolicyArgRet(Policy policyArg);

        PaymentRequestStatus getRequestStatusRet();

        void setRequestStatusRet(PaymentRequestStatus rquestStatusRet);

    }

    @ServiceCommand(defaultServiceClass = PremiumCollectionResponseService.class)
    public interface PremiumCollectionResponseCommand extends Command, PremiumCollectionResponseArgument {
    }

    @Override
    public void invoke() throws BaseException {
        PremiumCollectionAuthorisedCommand pcas;
        PremiumCollectionCancelledCommand pccs;
        PremiumCollectionExecutionCommand pces;
        PaymentResponseCommand prc;
        PaymentRequestStatus aggregateStatus = null;

        setCore(CoreContext.getCoreProxy().getCore());

        pcas = getCore().newCommand(PremiumCollectionAuthorisedCommand.class);
        pccs = getCore().newCommand(PremiumCollectionCancelledCommand.class);
        pces = getCore().newCommand(PremiumCollectionExecutionCommand.class);

        for (MoneyProvision mp : args.getPolicyArgRet().getPaymentDetails().getMoneyProvision()) {
            if (MoneyProvisionStatus.REQUESTED.equals(mp.getStatus())) {
                String commandName = mp.getPaymentMethod().getId() + "PaymentResponseCommand";

                prc = getCore().newCommand(commandName, PaymentResponseCommand.class);
                prc.setPaymentIdArgRet(mp.getPaymentId());
                prc.invoke();

                mp.setPaymentId(prc.getPaymentIdArgRet());

                switch (prc.getPaymentStatusRet()) {
                case APPROVED:
                    pcas.setPolicyArgRet(args.getPolicyArgRet());
                    pcas.invoke();
                    pces.setPolicyArgRet(args.getPolicyArgRet());
                    pces.invoke();
                    if (aggregateStatus == null) {
                        aggregateStatus = PaymentRequestStatus.APPROVED;
                    }
                    break;
                case CANCELLED:
                    pccs.setPolicyArgRet(args.getPolicyArgRet());
                    pccs.invoke();
                    if (aggregateStatus == null || PaymentRequestStatus.APPROVED.equals(aggregateStatus)) {
                        aggregateStatus = PaymentRequestStatus.CANCELLED;
                    }
                    break;
                case FAILED:
                    pccs.setPolicyArgRet(args.getPolicyArgRet());
                    pccs.invoke();
                    aggregateStatus = PaymentRequestStatus.FAILED;
                    break;
                }
            }

            args.setRequestStatusRet(aggregateStatus);
        }
    }
}
