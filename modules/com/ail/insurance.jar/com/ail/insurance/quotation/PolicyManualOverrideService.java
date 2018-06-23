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

import static com.ail.insurance.policy.AssessmentSheet.TOTAL_PREMIUM_LINE_NAME;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.BehaviourType;
import com.ail.insurance.policy.CalculationLine;
import com.ail.insurance.policy.FixedSum;
import com.ail.insurance.policy.Marker;
import com.ail.insurance.policy.MarkerType;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.policy.RateBehaviour;
import com.ail.insurance.quotation.AddManualLoadingService.AddManualLoadingCommand;
import com.ail.insurance.quotation.AddPolicyNumberService.AddPolicyNumberCommand;
import com.ail.insurance.quotation.CalculateGrossPremiumService.CalculateGrossPremiumCommand;
import com.ail.insurance.quotation.OverrideBasePremiumService.OverrideBasePremiumCommand;
import com.ail.insurance.quotation.OverrideStatusService.OverrideStatusCommand;
import com.ail.util.Rate;

@ServiceImplementation
public class PolicyManualOverrideService extends Service<PolicyManualOverrideService.PolicyManualOverrideArgument> {

    private static final String VALIDATION_ERROR_ID = "validation.error";

    private static final long serialVersionUID = 7959051258477641251L;

    public static final String MANUAL_PCT_LOADING_ID = "manual pct loading";
    public static final String MANUAL_FIXED_LOADING_ID = "manual fixed loading";
    public static final String BASE_PREMIUM_ID = "base premium";
    public static final String SHEET_STATUS_ID = "sheet status";

    public static final String TOTAL_PREMIUM_ID = "total premium";

    public static final String MANUAL_PCT_LOADING_PARAM_NAME = MANUAL_PCT_LOADING_ID + " override";
    public static final String MANUAL_FIXED_LOADING_PARAM_NAME = MANUAL_FIXED_LOADING_ID + " override";
    public static final String BASE_PREMIUM_OVERRIDE_PARAM_NAME = BASE_PREMIUM_ID + " override";
    public static final String SHEET_STATUS_PARAM_NAME = SHEET_STATUS_ID + " override";

    public static final String APPLICATION_OPERATION = "application";
    public static final String DECLINE_OPERATION = "decline";
    public static final String ONRISK_OPERATION = "onrisk";
    public static final String QUOTE_OPERATION = "quote";
    public static final String RECALCULATE_OPERATION = "recalculate";
    public static final String REFER_OPERATION = "refer";

    @ServiceArgument
    public interface PolicyManualOverrideArgument extends Argument {

        /**
         * Get the operation to execute.
         * @return operation
         */
        String getOperationArg();

        /**
         * Set the operation to execute - e.g. decline, recalculate.
         * @param operation.
         */
        void setOperationArg(String operation);

        /**
         * Get the policy to run operation on.
         * @return policy
         */
        Policy getPolicyArg();

        /**
         * Set the policy to run operation on.
         *  Note: Set this or policy system id
         * @param policy.
         */
        void setPolicyArg(Policy policy);

        /**
         * Get the parameter map of key / value pairs to apply overrides.
         * @return policy.
         */
        Map<String, String[]> getParameterMapArg();

        /**
         * Set the parameter map of key / value pairs to apply overrides - optional.
         *
         * Key format [ <param name> : <sheet position (0 = policy, 1..n = section(n))> : <policy system id> ]
         *      e.g. "base premium override:1:1001"
         *
         * @param map
         */
        void setParameterMapArg(Map<String, String[]> parameterMap);

    }

    @ServiceCommand(defaultServiceClass=PolicyManualOverrideService.class)
    public interface PolicyManualOverrideCommand extends Command, PolicyManualOverrideArgument {
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {

        String operation = args.getOperationArg();
        Policy policy = args.getPolicyArg();

        Map<String, String[]> parameterMap = args.getParameterMapArg();

        if (operation == null) {
            throw new PreconditionException("operation==null");
        }

        if (!APPLICATION_OPERATION.equals(operation)
                && !DECLINE_OPERATION.equals(operation)
                && !ONRISK_OPERATION.equals(operation)
                && !RECALCULATE_OPERATION.equals(operation)
                && !QUOTE_OPERATION.equals(operation)
                && !REFER_OPERATION.equals(operation)) {
            throw new PreconditionException("(!APPLICATION_OPERATION.equals(operation) && !DECLINE_OPERATION.equals(operation) && !ONRISK_OPERATION.equals(operation) && !RECALCULATE_OPERATION.equals(operation) && !QUOTE_OPERATION.equals(operation))");
        }

        if (policy == null) {
            throw new PreconditionException("policy == null");
        }

        removeValidationError(policy); // clear any previous validation errors

        handleOverride(operation, policy, parameterMap);
    }

    private void handleOverride(String operation, Policy policy,  Map<String, String[]> paramameterMap) throws NumberFormatException, BaseException {

        if (APPLICATION_OPERATION.equals(operation)) {
            removeQuoteInformation(policy);
            setNewStatus(policy, PolicyStatus.APPLICATION);
        }

        if (RECALCULATE_OPERATION.equals(operation)) {
            recalculateReferral(policy, paramameterMap);
        }

        if (ONRISK_OPERATION.equals(operation)) {
            if (recalculateReferral(policy, paramameterMap)) {
                setNewStatus(policy, PolicyStatus.ON_RISK);
            }
        }

        if (QUOTE_OPERATION.equals(operation)) {
            if (recalculateReferral(policy, paramameterMap)) {
                setNewStatus(policy, PolicyStatus.QUOTATION);
            }
        }

        if (DECLINE_OPERATION.equals(operation)) {
            setNewStatus(policy, PolicyStatus.DECLINED);
        }

        if (REFER_OPERATION.equals(operation)) {
            setNewStatus(policy, PolicyStatus.REFERRED);
        }

    }

    private void removeQuoteInformation(Policy policy) {
        policy.setQuotationNumber(null);
        policy.setQuotationDate(null);
        policy.setQuotationExpiryDate(null);
        for (String sheetName: policy.getAssessmentSheetList().keySet()) {
            AssessmentSheet sheet = (AssessmentSheet)policy.getAssessmentSheetFor(sheetName);
            sheet.getAssessmentLine().clear();
        }
        policy.getAssessmentSheetList().clear();
    }

    private void setNewStatus(Policy policy, PolicyStatus newStatus) throws BaseException {

        if (PolicyStatus.ON_RISK == newStatus) {
            AddPolicyNumberCommand  apnc = core.newCommand(AddPolicyNumberCommand.class);
            apnc.setPolicyArgRet(policy);
            apnc.invoke();
        }

        OverrideStatusCommand osc = core.newCommand(OverrideStatusCommand.class);
        osc.setPolicyArg(policy);
        osc.setPolicyStatusArg(newStatus);
        osc.invoke();

    }

    /**
     * Recalculate referral
     * @param policy
     * @param paramameterMap Map of values to recalculate
     * @return true if recalculation successful
     * @throws BaseException
     */
    private boolean recalculateReferral(Policy policy, Map<String, String[]> paramameterMap) throws BaseException {

        if (paramameterMap != null) {
            for(Map.Entry<String, String[]> param:  paramameterMap.entrySet()) {

                String fullParamName = param.getKey();

                if (!fullParamName.startsWith("op")) { // ignore operation params here

                    String[] paramNameAndIndex = fullParamName.split(":");
                    String name = paramNameAndIndex[0];
                    String sheetName = paramNameAndIndex[1];
                    String policyId = paramNameAndIndex[2];

                    if (policy.getSystemId() == Long.valueOf(policyId).longValue()) { // ignore if different policy id

                        try {
                            String paramValue = param.getValue()[0];
                            if (BASE_PREMIUM_OVERRIDE_PARAM_NAME.equals(name) && isNotBlank(paramValue)) {
                                handleBasePremiumOverride(
                                        policy,
                                        Double.valueOf(paramValue),
                                        sheetName);
                            } else if (MANUAL_PCT_LOADING_PARAM_NAME.equals(name) && isNotBlank(paramValue)) {
                                handleManualPctLoading(
                                        policy,
                                        Double.valueOf(paramValue),
                                        sheetName);
                            } else if (MANUAL_FIXED_LOADING_PARAM_NAME.equals(name) && isNotBlank(paramValue)) {
                                handleManualFixedLoading(
                                        policy,
                                        Double.valueOf(paramValue),
                                        sheetName);
                            } else if (SHEET_STATUS_PARAM_NAME.equals(name) && isNotBlank(paramValue)) {
                                handleSheetStatus(
                                        policy,
                                        paramValue,
                                        sheetName);
                            }

                        } catch (NumberFormatException nfe) {

                            addValidationError(policy, name, nfe);
                            return false;
                        }
                    }
                }
            }

            recalculatePremiums(policy);
        }

        return true;

    }

    private void recalculatePremiums(Policy policy) throws BaseException {
        CalculateGrossPremiumCommand cgpc = core.newCommand(CalculateGrossPremiumCommand.class);
        cgpc.setPolicyArgRet(policy);

        if (policy.isAggregator()) {
            for(String sheetName : policy.getAssessmentSheetList().keySet()) {
                cgpc.setProductTypeIdArg(sheetName);
                cgpc.invoke();
            }
        } else {
            cgpc.setProductTypeIdArg(policy.getProductTypeId());
            cgpc.invoke();
        }
    }


    private void handleSheetStatus(Policy policy, String selectedStatus, String sheetName) {

        AssessmentSheet sheet = policy.getAssessmentSheetFor(sheetName);

        PolicyStatus status = PolicyStatus.valueOf(selectedStatus);

        for (Marker marker: sheet.markerLines()) {
            if (marker.getType() == MarkerType.REFER) {
                marker.setDisabled(PolicyStatus.REFERRED != status);
            }
            if (marker.getType() == MarkerType.DECLINE) {
                marker.setDisabled(PolicyStatus.DECLINED != status);
            }
        }
    }


    private void handleBasePremiumOverride(Policy policy, double value, String sheetName) throws BaseException {

        OverrideBasePremiumCommand cmd = core.newCommand(OverrideBasePremiumCommand.class);

        cmd.setAssessmentSheetArg(
                policy.getAssessmentSheetFor(sheetName));

        cmd.setBasePremiumAmountArg(
                new CurrencyAmount(value, policy.getBaseCurrency()));

        cmd.invoke();

    }


    private void handleManualPctLoading(Policy policy, double rate, String sheetName) throws BaseException {

        AssessmentSheet sheet = policy.getAssessmentSheetFor(sheetName);
        CalculationLine line = new RateBehaviour(
                    MANUAL_PCT_LOADING_ID,
                    "Manually applied percentage loading",
                    null,
                    TOTAL_PREMIUM_LINE_NAME,
                    TOTAL_PREMIUM_LINE_NAME,
                    BehaviourType.LOAD,
                    new Rate(rate + "%"),
                    0);

        addLoading(sheet, line);

    }

    private void handleManualFixedLoading(Policy policy, double value, String sheetName) throws BaseException {

        AssessmentSheet sheet = policy.getAssessmentSheetFor(sheetName);

        CalculationLine line =  new FixedSum(
                    MANUAL_FIXED_LOADING_ID,
                    "Manually applied fixed loading",
                    null,
                    TOTAL_PREMIUM_ID,
                    new CurrencyAmount(value, policy.getBaseCurrency()),
                    0);

        addLoading(sheet, line);

    }

    private void addLoading(AssessmentSheet sheet, CalculationLine line) throws BaseException {

        AddManualLoadingCommand cmd = core.newCommand(AddManualLoadingCommand.class);

        cmd.setAssessmentSheetArg(sheet);
        cmd.setCalculationLineArg(line);

        cmd.invoke();
    }

//    /**
//     * Returns the active sheet for given index.
//     * 0 = policy, 1..n = section(n)
//     *
//     * @param policy Policy with sheet
//     * @param index Sheet index
//     * @return Active sheet
//     */
//    private AssessmentSheet getActiveSheet(Policy policy, String sheetName) {
//        AssessmentSheet sheet = null;
//
//        if ("policy".equals(sheetName)) {
//            sheet = policy.getAssessmentSheet();
//        } else {
//            sheet = policy.getAssessmentSheetFor(sheetName);
//        }
//        return sheet;
//    }

    /**
     * Remove validation attribute from policy
     * @param policy
     */
    private void removeValidationError(Policy policy) {
        Attribute validationAttribute = policy.getAttributeById(VALIDATION_ERROR_ID);
        if (validationAttribute != null) {
            policy.removeAttribute(validationAttribute);
        }
    }

    /**
     * Add validation error message to policy as an attribute
     * @param policy Policy to add message to
     * @param name Failed field name
     * @param e Validation exception
     */
    private void addValidationError(Policy policy, String name, Exception e) {
        policy.addAttribute(
                new Attribute(VALIDATION_ERROR_ID, "For field id: " + name + ": " + e.getMessage(), "string"));
    }
}


