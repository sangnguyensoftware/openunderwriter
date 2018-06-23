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
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.CalculateBrokerageService.CalculateBrokerageCommand;
import com.ail.insurance.quotation.CalculateCommissionService.CalculateCommissionCommand;
import com.ail.insurance.quotation.CalculateManagementChargeService.CalculateManagementChargeCommand;
import com.ail.insurance.quotation.CalculateTaxService.CalculateTaxCommand;
import com.ail.insurance.quotation.RefreshAssessmentSheetsService.RefreshAssessmentSheetsCommand;

@ServiceImplementation
public class CalculateGrossPremiumService extends Service<CalculateGrossPremiumService.CalculateGrossPremiumArgument> {
    
    private static final long serialVersionUID = 7959054658477631251L;

    @ServiceArgument
    public interface CalculateGrossPremiumArgument extends Argument {
        /**
         * Fetch the policy for which the premium should be calculate.
         * @return value of policy
         */
        Policy getPolicyArgRet();

        /**
         * Set the policy for which the premium should be calculate.
         * @param policyArgRet New value for policy argument.
         */
        void setPolicyArgRet(Policy policyArgRet);

        /**
         * For aggregator policies (getPolicyArgRet().isAggregator()==true), the
         * product that the policy premium calculation should be assessed
         * against. This will be used to select the rules which the service
         * should use when performing calculations.
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

    @ServiceCommand(defaultServiceClass=CalculateGrossPremiumService.class)
    public interface CalculateGrossPremiumCommand extends Command, CalculateGrossPremiumArgument {
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

        if (args.getPolicyArgRet() == null) {
            throw new PreconditionException("args.getPolicyArgRet() == null");
        }

        if (args.getPolicyArgRet().isAggregator() && (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0)) {
            throw new PreconditionException("args.getPolicyArgRet().isAggregator() && (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0)");
        }
        
        if (args.getPolicyArgRet().getAssessmentSheetFor(determineAssessmentSheetName()) == null) {
            throw new PreconditionException("args.getPolicyArgRet().getAssessmentSheetFor(\""+determineAssessmentSheetName()+"\") == null");
        }

        calculatePremium();
    }
    
    private void calculatePremium() throws BaseException {
        Policy policy = args.getPolicyArgRet();

        // calculate the assessment sheet so that the other calc services get to see premiums etc.
        RefreshAssessmentSheetsCommand rasc=getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        rasc.setPolicyArgRet(policy);
        rasc.setOriginArg("CalculatePremium");
        rasc.setProductTypeIdArg(args.getProductTypeIdArg());
        rasc.invoke();
        policy=rasc.getPolicyArgRet();

        // calc tax
        CalculateTaxCommand calcTax=getCore().newCommand(CalculateTaxCommand.class);
        calcTax.setPolicyArgRet(policy);
        calcTax.setProductTypeIdArg(args.getProductTypeIdArg());
        calcTax.invoke();
        policy=calcTax.getPolicyArgRet();

        // calc commission
        CalculateCommissionCommand calcCommission=getCore().newCommand(CalculateCommissionCommand.class);
        calcCommission.setPolicyArgRet(policy);
        calcCommission.setProductTypeIdArg(args.getProductTypeIdArg());
        calcCommission.invoke();
        policy=calcCommission.getPolicyArgRet();

        // calc brokerage
        CalculateBrokerageCommand calcBrokerage=getCore().newCommand(CalculateBrokerageCommand.class);
        calcBrokerage.setPolicyArgRet(policy);
        calcBrokerage.setProductTypeIdArg(args.getProductTypeIdArg());
        calcBrokerage.invoke();
        policy=calcBrokerage.getPolicyArgRet();

        // calc management charge
        CalculateManagementChargeCommand calcMgmtChg=getCore().newCommand(CalculateManagementChargeCommand.class);
        calcMgmtChg.setPolicyArgRet(policy);
        calcMgmtChg.setProductTypeIdArg(args.getProductTypeIdArg());
        calcMgmtChg.invoke();
        policy=calcMgmtChg.getPolicyArgRet();

        rasc.setPolicyArgRet(policy);
        rasc.setOriginArg("CalculatePremium");
        rasc.invoke();
        policy=rasc.getPolicyArgRet();
    }

    private String determineAssessmentSheetName() {
        return args.getPolicyArgRet().isAggregator() ? args.getProductTypeIdArg() : DEFAULT_SHEET_NAME;
    }

    private String determineProductName() {
        return args.getPolicyArgRet().isAggregator() ? args.getProductTypeIdArg() : args.getPolicyArgRet().getProductTypeId();
    }
}


