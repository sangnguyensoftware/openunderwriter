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

import static com.ail.financial.PaymentRecordType.COMPLETE;
import static com.ail.financial.PaymentRecordType.FAILED;
import static com.ail.insurance.policy.PolicyStatus.DECLINED;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionStatus;
import com.ail.financial.PaymentRecord;
import com.ail.insurance.policy.Policy;
import com.ail.payment.PaymentExecutionService.PaymentExecutionCommand;

@ServiceImplementation
public class PremiumCollectionExecutionService extends com.ail.core.Service<PremiumCollectionExecutionService.PremiumCollectionExecutionArgument> {
    public static final String PAYMENT_EXECUTION_COMMAND_NAME_SUFFIX = "PaymentExecutionCommand";
    private static final long serialVersionUID = 1871676649916485145L;

    @ServiceArgument
    public interface PremiumCollectionExecutionArgument extends Argument {
        /**
         * The Policy for which the premium payment is to be executed.
         */
        Policy getPolicyArgRet();

        /**
         * @see #getPolicyArgRet
         */
        void setPolicyArgRet(Policy policyArg);

        /**
         * ID supplied buy the payment authority indicating the payer's payment
         * authorisation ID.
         */
        String getPayerIdArg();

        /**
         * @see #getPayerIdArg()
         */
        void setPayerIdArg(String payerIdArg);
    }

    @ServiceCommand(defaultServiceClass = PremiumCollectionExecutionService.class)
    public interface PremiumCollectionExecutionCommand extends Command, PremiumCollectionExecutionArgument {
    }

    @Override
    public void invoke() throws BaseException {
        if (args.getPolicyArgRet() == null) {
            throw new PreconditionException("args.getPolicyArgRet() == null");
        }

        if (args.getPolicyArgRet().getStatus()==null) {
            throw new PreconditionException("args.getPolicyArgRet().getStatus()==null");
        }

        if (DECLINED.equals(args.getPolicyArgRet().getStatus())) {
            throw new PreconditionException("DECLINED.equals(args.getPolicyArg().getStatus())");
        }

        if (REFERRED.equals(args.getPolicyArgRet().getStatus())) {
            throw new PreconditionException("REFERRED.equals(args.getPolicyArg().getStatus())");
        }

        if (args.getPolicyArgRet().getPaymentDetails() == null) {
            throw new PreconditionException("args.getPolicyArg().getPaymentOption()==null");
        }

        if (args.getPolicyArgRet().getPaymentDetails().getMoneyProvision() == null) {
            throw new PreconditionException("args.getPolicyArg().getPaymentDetails().getMoneyProvision() == null");
        }

        if (args.getPolicyArgRet().getPaymentDetails().getMoneyProvision().size() == 0) {
            throw new PreconditionException("args.getPolicyArg().getPaymentDetails().getMoneyProvision().size() == 0");
        }

        if (args.getPolicyArgRet().getProductTypeId() == null || args.getPolicyArgRet().getProductTypeId().length() == 0) {
            throw new PreconditionException("args.getPolicyArg().getProductTypeId() == null || args.getPolicyArg().getProductTypeId().length() == 0");
        }

        Policy policy = args.getPolicyArgRet();

        // Switch to the product's configuration
        String namespace = Functions.productNameToConfigurationNamespace(policy.getProductTypeId());
        setCore(new CoreProxy(namespace, args.getCallersCore()).getCore());

        for (MoneyProvision mp : policy.getPaymentDetails().getMoneyProvision()) {
            try {
                String command=mp.getPaymentMethod().getId()+PAYMENT_EXECUTION_COMMAND_NAME_SUFFIX;

                PaymentExecutionCommand pec = getCore().newCommand(command, PaymentExecutionCommand.class);
                pec.setProductTypeIdArg(policy.getProductTypeId());
                pec.setPayerIdArg(args.getPayerIdArg());
                pec.setPaymentIdArg(mp.getPaymentId());
                pec.invoke();

                mp.setSaleId(pec.getSaleIdRet());
                mp.setStatus(MoneyProvisionStatus.COMPLETE);

                policy.getPaymentHistory().add(new PaymentRecord(mp.getAmount(), mp.getPaymentId(), mp.getPaymentMethod(), COMPLETE));
            }
            catch(BaseException e) {
                policy.getPaymentHistory().add(new PaymentRecord(mp.getAmount(), mp.getPaymentId(), mp.getPaymentMethod(), FAILED, "Payment request and authorisation were successful, but execution failed: "+e));
                throw e;
            }
        }
    }
}
