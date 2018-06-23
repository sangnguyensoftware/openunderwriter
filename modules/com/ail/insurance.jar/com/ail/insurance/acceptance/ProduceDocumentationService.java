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
import com.ail.core.PreconditionException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

@ServiceImplementation
public class ProduceDocumentationService extends com.ail.core.Service<ProduceDocumentationService.ProduceDocumentationArgument> {
    private static final long serialVersionUID = 8323843668107021681L;

    @ServiceArgument
    public interface ProduceDocumentationArgument extends Argument {
        /**
         * Getter for the policyArg property. Policy to create documentation for
         * @return Value of policyArg, or null if it is unset
         */
        Policy getPolicyArg();

        /**
         * Setter for the policyArg property. * @see #getPolicyArg
         * @param policyArg new value for property.
         */
        void setPolicyArg(Policy policyArg);

        /**
         * Getter for the documentationRet property. Type containing policy documentation
         * @return Value of documentationRet, or null if it is unset
         */
        PolicyDocumentation getDocumentationRet();

        /**
         * Setter for the documentationRet property. * @see #getDocumentationRet
         * @param documentationRet new value for property.
         */
        void setDocumentationRet(PolicyDocumentation documentationRet);
    }

    @ServiceCommand(defaultServiceClass=ProduceDocumentationService.class)
    public interface ProduceDocumentationCommand extends Command, ProduceDocumentationArgument {}

    @Override
    public void invoke() throws PreconditionException {
        Policy policy = args.getPolicyArg();
		// check arguments
		if(policy==null){
			throw new PreconditionException("policy==null");
		}
		
		PolicyStatus status = policy.getStatus();
		if(status==null){
			throw new PreconditionException("status==null");
		}
		if(!status.equals(PolicyStatus.ON_RISK)){
			throw new PreconditionException("!status.equals(PolicyStatus.OnRisk)");
		}
		
		core.logInfo("Produce Documentation for: " + policy.getPolicyNumber());
 
 		args.setDocumentationRet(new PolicyDocumentation());
    }
}


