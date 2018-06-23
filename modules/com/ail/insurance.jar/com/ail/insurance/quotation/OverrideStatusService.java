/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

package com.ail.insurance.quotation;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

@ServiceImplementation
public class OverrideStatusService extends Service<OverrideStatusService.OverrideStatusArgument> {
    
    private static final long serialVersionUID = 7959054258477621251L;


    @ServiceArgument
    public interface OverrideStatusArgument extends Argument {
        
        /**
         * Fetch the value of the policy argument. The policy for which the premium should be calculate.
         * @return value of policy
         */
        Policy getPolicyArg();

        /**
         * Set the value of the policy argument. The policy for which the premium should be calculate.
         * @param policyArgRet New value for policy argument.
         */
        void setPolicyArg(Policy policyArgRet);
        
        /**
         * Set the new policy status.
         * @param returnStatus
         */
        void setPolicyStatusArg(PolicyStatus newStatus);
        
        /**
         * Return policy status.
         * @return
         */
        PolicyStatus getPolicyStatusArg();
    }

    @ServiceCommand(defaultServiceClass=OverrideStatusService.class)
    public interface OverrideStatusCommand extends Command, OverrideStatusArgument {
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        
        Policy policy = args.getPolicyArg();
        PolicyStatus status = args.getPolicyStatusArg();
        
        if (policy == null) {
            throw new PreconditionException("policy==null");
        }
        
        if (status == null) {
            throw new PreconditionException("status==null");
        }

        policy.setStatus(status);
        
    }
    
}


