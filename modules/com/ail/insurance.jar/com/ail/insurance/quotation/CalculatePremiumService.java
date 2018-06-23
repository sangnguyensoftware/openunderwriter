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

import static com.ail.core.product.Product.AGGREGATOR_CONFIGURATION_GROUP_NAME;
import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;
import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static com.ail.insurance.policy.PolicyStatus.DECLINED;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.ExceptionRecord;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Parameter;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.AssessRiskService.AssessRiskCommand;
import com.ail.insurance.quotation.CalculateGrossPremiumService.CalculateGrossPremiumCommand;
import com.ail.insurance.quotation.EnforceComplianceService.EnforceComplianceCommand;

@ServiceImplementation
public class CalculatePremiumService extends Service<CalculatePremiumService.CalculatePremiumArgument> {
    private static final long serialVersionUID = 7959054658477631252L;

    @ServiceArgument
    public interface CalculatePremiumArgument extends Argument {
        /**
         * Fetch the value of the policy argument. The policy for which the premium should be calculate.
         * @see #setPolicyArgRet
         * @return value of policy
         */
        Policy getPolicyArgRet();

        /**
         * Set the value of the policy argument. The policy for which the premium should be calculate.
         * @see #getPolicyArgRet
         * @param policyArgRet New value for policy argument.
         */
        void setPolicyArgRet(Policy policyArgRet);
    }

    @ServiceCommand(defaultServiceClass=CalculatePremiumService.class)
    public interface CalculatePremiumCommand extends Command, CalculatePremiumArgument {
    }

    /**
     * Return the product type id of the policy we're assessing the risk for as the
     * configuration namespace. The has the effect of selecting the product's configuration.
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        return Functions.productNameToConfigurationNamespace(args.getPolicyArgRet().getProductTypeId());
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {

        Policy returnPolicy = args.getPolicyArgRet();
        if (returnPolicy == null) {
            throw new PreconditionException("policy==null");
        }

        if (returnPolicy.getStatus() == null || !APPLICATION.equals(returnPolicy.getStatus())) {
            throw new PreconditionException("policy.status==null || policy.status!=APPLICATION");
        }

        if (isAggregatorProduct()) {
            for(Parameter p: core.getGroup(AGGREGATOR_CONFIGURATION_GROUP_NAME).getParameter()) {
                try {
                    invokeForProduct(p.getName());
                } catch (Throwable e) {
                    core.logError("Failed to run Calculate Premium Service for product " + p.getName() + ": policy id:" + returnPolicy.getSystemId());
                    returnPolicy.addException(new ExceptionRecord(e));
                }
            }
        }
        else {
            invokeForProduct(returnPolicy.getProductTypeId());

            updatePolicyStatus();
        }
    }

    private boolean isAggregatorProduct() {
        return core.getGroup(AGGREGATOR_CONFIGURATION_GROUP_NAME)!=null;
    }

    protected void invokeForProduct(String productTypeId) throws BaseException, PreconditionException {
        Policy policy = args.getPolicyArgRet();

        AssessRiskCommand arc=getCore().newCommand(AssessRiskCommand.class);
        arc.setPolicyArgRet(policy);
        arc.setProductTypeIdArg(productTypeId);
        arc.invoke();
        policy=arc.getPolicyArgRet();

        if (policy.getAssessmentSheetFor(determineAssessmentSheetName(productTypeId)) == null) {
            throw new PreconditionException("policy.assessmentSheet==null");
        }

        CalculateGrossPremiumCommand cpc = getCore().newCommand(CalculateGrossPremiumCommand.class);
        cpc.setPolicyArgRet(policy);
        cpc.setProductTypeIdArg(productTypeId);
        cpc.invoke();

        EnforceComplianceCommand ecc = (EnforceComplianceCommand) getCore().newCommand(EnforceComplianceCommand.class);
        ecc.setPolicyArgRet(policy);
        ecc.setProductTypeIdArg(productTypeId);
        ecc.invoke();
        policy = ecc.getPolicyArgRet();
    }

    protected void updatePolicyStatus() {
        Policy policy = args.getPolicyArgRet();

        if (policy.isMarkedForDecline()) {
            policy.setStatus(DECLINED);
        }
        else if (policy.isMarkedForRefer()) {
            policy.setStatus(REFERRED);
        }
        else {
            policy.setStatus(QUOTATION);
        }
    }

    private String determineAssessmentSheetName(String productTypeId) {
        return args.getPolicyArgRet().isAggregator() ? productTypeId : DEFAULT_SHEET_NAME;
    }
}


