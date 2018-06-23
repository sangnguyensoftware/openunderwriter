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
import com.ail.core.PreconditionException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.AddPolicyNumberService.AddPolicyNumberCommand;

@ServiceImplementation
public class PutOnRiskService extends com.ail.core.Service<PutOnRiskService.PutOnRiskArgument> {
    private static final long serialVersionUID = 5492150960329684094L;

    @ServiceArgument
    public interface PutOnRiskArgument extends Argument {
        /**
         * Getter for the policyArgRet property. Quoted policy to be put on risk
         * @return Value of policyArgRet, or null if it is unset
         */
        Policy getPolicyArgRet();

        /**
         * Setter for the policyArgRet property. * @see #getPolicyArgRet
         * @param policyArgRet new value for property.
         */
        void setPolicyArgRet(Policy policyArgRet);
    }

    @ServiceCommand(defaultServiceClass=PutOnRiskService.class)
    public interface PutOnRiskCommand extends Command, PutOnRiskArgument {}

    /**
     * Return the product type id of the policy we're assessing the risk for as the
     * configuration namespace. The has the effect of selecting the product's configuration.
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        return args.getPolicyArgRet().getProductTypeId();
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
		Policy policy = args.getPolicyArgRet();
		// check arguments
		if(policy==null){
			throw new PreconditionException("policy==null");
		}
		PolicyStatus status = policy.getStatus();
		if(status==null){
			throw new PreconditionException("status==null");
		}
		if(!status.equals(PolicyStatus.QUOTATION) && !status.equals(PolicyStatus.SUBMITTED)){
			throw new PreconditionException("!status.equals(PolicyStatus.QUOTATION) && !status.equals(PolicyStatus.SUBMITTED)");
		}

		// clear out any existing number
		policy.setPolicyNumber(null);

		// run command to add new number
		AddPolicyNumberCommand command = getCore().newCommand(AddPolicyNumberCommand.class);
		command.setPolicyArgRet(policy);
		command.invoke();
		policy = command.getPolicyArgRet();

		// confirm payment details
		if(policy.getPaymentDetails()!=null){
			core.logInfo("Found Payment Details for: " + policy.getPolicyNumber());
		}
		else{
			core.logInfo("No Payment Details for: " + policy.getPolicyNumber());
		}

		// Customer ledger
		core.logInfo("If required, create customer ledger for: " + policy.getPolicyNumber());

		// payment schedule
		core.logInfo("Create payment schedule for: " + policy.getPolicyNumber());

		// put on risk
		policy.setStatus(PolicyStatus.ON_RISK);

		// post to account
		core.logInfo("Create post to account for: " + policy.getPolicyNumber());
    }
}


