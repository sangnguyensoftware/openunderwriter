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

import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.CalculatePolicyBrokerageService.CalculatePolicyBrokerageCommand;

@ServiceImplementation
public class CalculateBrokerageService extends Service<CalculateBrokerageService.CalculateBrokerageArgument> {
    private static final long serialVersionUID = 3821572976058925514L;

    @ServiceArgument
    public interface CalculateBrokerageArgument extends Argument {
        /**
         * Getter for the policyArgRet property. The policy to calculate brokerage for
         * @return Value of policyArgRet, or null if it is unset
         */
        Policy getPolicyArgRet();

        /**
         * Setter for the policyArgRet property. * @see #getPolicyArgRet
         * @param policyArgRet new value for property.
         */
        void setPolicyArgRet(Policy policyArgRet);

        /**
         * For aggregator policies (getPolicyArgRet().isAggregator()==true), the
         * product that the policy's brokerage should be assessed against. This will be
         * used to select the rules which the service should use when assessing the policy
         * brokerage.
         * <p>This argument is ignored for non-aggregator policies.</p>
         * 
         * @since 3.0 
         * @return product type id
         */
        String getProductTypeIdArg();
        
        /**
         * @see #getProductTypeIdArg()
         * @param productTypeIdArg ID of the product to assess risk for.
         */
        void setProductTypeIdArg(String productTypeIdArg);
    }

    @ServiceCommand(defaultServiceClass=CalculateBrokerageService.class)
    public interface CalculateBrokerageCommand extends Command, CalculateBrokerageArgument {
    }

    /**
     * Return the product type id of the policy we're assessing the risk for as the
     * configuration namespace. The has the effect of selecting the product's configuration.
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        return Functions.productNameToConfigurationNamespace(determineProductName());
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        if (args.getPolicyArgRet()==null) {
            throw new PreconditionException("args.getPolicyArgRet()==null");
        }

        if (args.getPolicyArgRet().isAggregator() && (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0)) {
            throw new PreconditionException("args.getPolicyArgRet().isAggregator() && (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0)");
        }

        String assessmentSheetName = determineAssessmentSheetName();

        Policy policy=args.getPolicyArgRet();

        AssessmentSheet assessmentSheet=policy.getAssessmentSheetFor(assessmentSheetName);

        if (assessmentSheet == null) {
            throw new PreconditionException("policy.getAssessmentSheetFor(\""+assessmentSheetName+"\")==null");
        }

        // Remove any lines we added in the past
        assessmentSheet.removeLinesByOrigin("CalculateBrokerage");

        // Lock the assessment sheet
        assessmentSheet.setLockingActor("CalculateBrokerage");

        CalculatePolicyBrokerageCommand command=core.newCommand("CalculatePolicyBrokerage", CalculatePolicyBrokerageCommand.class);
        command.setPolicyArg(policy);
        command.setAssessmentSheetArgRet(assessmentSheet);
        command.invoke();
        assessmentSheet=command.getAssessmentSheetArgRet();

        // unlock the assessment sheet
        assessmentSheet.clearLockingActor();

        // put the assessment sheet back into the policy
        policy.setAssessmentSheetFor(assessmentSheetName, assessmentSheet);

        args.setPolicyArgRet(policy);
    }
    
    private String determineAssessmentSheetName() {
        return args.getPolicyArgRet().isAggregator() ? args.getProductTypeIdArg() : DEFAULT_SHEET_NAME;
    }

    private String determineProductName() {
        return args.getPolicyArgRet().isAggregator() ? args.getProductTypeIdArg() : args.getPolicyArgRet().getProductTypeId();
    }
}
