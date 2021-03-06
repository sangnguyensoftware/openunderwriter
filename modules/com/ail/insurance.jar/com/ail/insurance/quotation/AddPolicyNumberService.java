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

package com.ail.insurance.quotation;

import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;
import static com.ail.insurance.policy.PolicyStatus.SUBMITTED;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyCommand;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.GeneratePolicyNumberService.GeneratePolicyNumberCommand;

@ServiceImplementation
public class AddPolicyNumberService extends Service<AddPolicyNumberService.AddPolicyNumberArgument> {
    private static final long serialVersionUID = 4156690077014872967L;
    private String configurationNamespace="com.ail.insurance.quotation.addpolicynumber.AddPolicyNumberService";

    @ServiceArgument
    public interface AddPolicyNumberArgument extends Argument {
        /**
         * Getter for the policyArgRet property. The policy to generate a policy number for
         * @return Value of policyArgRet, or null if it is unset
         */
        Policy getPolicyArgRet();

        /**
         * Setter for the policyArgRet property. * @see #getPolicyArgRet
         * @param policyArgRet new value for property.
         */
        void setPolicyArgRet(Policy policyArgRet);
    }

    @ServiceCommand(defaultServiceClass=AddPolicyNumberService.class)
    public interface AddPolicyNumberCommand extends Command, AddPolicyNumberArgument {}

    /**
     * Return the product type id of the policy we're assessing the risk for as the
     * configuration namespace. The has the effect of selecting the product's configuration.
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        return configurationNamespace;
    }

    /**
     * This service adds a policy number to a policy. The specific format and content of the policy number depends on
     * business rules, and may vary depending the policy's product.
     *
     * The service checks the preconditions, and then generates a unique number before passing control into the product
     * specific 'GeneratePolicyNumberRule'. The policy and unique number are passed into this rule. The rule isn't obliged
     * to use the unique number, and may generate other numbers of it's own, but it is expected to set the policy's policy
     * number to something other than null or '' (see the postconditions).
     *
     * The unique number generated by this service is unique in a system wide sense. The numbers it generates aren't
     * necessarily contiguous and (as mentioned above) they may never be used. Numbers are allocated to instances of
     * this service in a block by block basis. The service's configuration defines the last number allocated, and the
     * size of the allocation blocks. The last number allocated is updated every time a new block is issued.
     *
     * @preconditions args.getPolicyArgRet()!=null
     *                args.getPolicyArgRet().getStatus()!=null
     *                args.getPolicyArgRet().getStatus().equals(PolicyStatus.Quotation)
     *                args.getPolicyArgRet().getPolicyNumber()==null || args.getPolicyArgRet().getPolicyNumber().length()==0
     *                args.getPolicyArgReg().getProdctTypeId()!=null
     * @postconditions args.getPolictArgRet().getPolicyNumber()!=null && args.getPolicyArgRet().getPolicyNumber().length()!=0
     */
    @Override
    public void invoke() throws PreconditionException, PostconditionException, BaseException {
        // Select this service's configuration
        configurationNamespace="com.ail.insurance.quotation.addpolicynumber.AddPolicyNumberService";

        if (args.getPolicyArgRet()==null) {
            throw new PreconditionException("args.getPolicyArgRet()==null");
        }

        Policy policy=args.getPolicyArgRet();

        if (policy.getStatus()==null) {
            throw new PreconditionException("policy.getStatus()==null");
        }

        if (!QUOTATION.equals(policy.getStatus()) && !REFERRED.equals(policy.getStatus()) && !SUBMITTED.equals(policy.getStatus())) {
            throw new PreconditionException("!QUOTATION.equals(policy.getStatus()) && !REFERRED.equals(policy.getStatus()) && !SUBMITTED.equals(policy.getStatus())");
        }

        if (policy.getPolicyNumber()!=null && policy.getPolicyNumber().trim().length()!=0) {
            throw new PreconditionException("policy.getPolicyNumber()!=null && policy.getPolicyNumber().trim().length()!=0");
        }

        if (policy.getProductTypeId()==null) {
            throw new PreconditionException("policy.getProductTypeId()==null");
        }

        // switch over to the product's configuration
        configurationNamespace=Functions.productNameToConfigurationNamespace(args.getPolicyArgRet().getProductTypeId());

        GenerateUniqueKeyCommand gukc=getCore().newCommand(GenerateUniqueKeyCommand.class);
        gukc.setKeyIdArg("PolicyNumber");
        gukc.invoke();

        GeneratePolicyNumberCommand command=core.newCommand("GeneratePolicyNumber", GeneratePolicyNumberCommand.class);
        command.setPolicyArg(policy);
        command.setUniqueNumberArg(gukc.getKeyRet());
        command.invoke();
        String policyNumber=command.getPolicyNumberRet();
        core.logInfo("Policy number: "+policyNumber+" generated");
        policy.setPolicyNumber(policyNumber);

        if (policy.getPolicyNumber()==null || policy.getPolicyNumber().length()==0) {
            throw new PostconditionException("policy.getPolicyNumber()==null || policy.getPolicyNumber().length()==0");
        }
    }
}

