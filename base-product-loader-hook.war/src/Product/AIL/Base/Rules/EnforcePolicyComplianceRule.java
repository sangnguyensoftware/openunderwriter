
/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
import com.ail.insurance.policy.AssessmentLine;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.CalculationLine;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.EnforcePolicyComplianceService.EnforcePolicyComplianceArgument;

public class EnforcePolicyComplianceRule {
    public static void invoke(EnforcePolicyComplianceArgument args) {
        Policy policy = args.getPolicyArg();
        AssessmentSheet sheet = args.getAssessmentSheetArgRet();

        AssessmentLine totalPremium = sheet.findLineById(AssessmentSheet.TOTAL_PREMIUM_LINE_NAME);
        if (totalPremium == null) {
            referPolicy(policy, sheet, "Calculation of total premium failed (assessment line 'total premium' is not defined)");
        } else if (!(totalPremium instanceof CalculationLine)) {
            referPolicy(policy, sheet, "Calculation of total premium failed (assessment line 'total premium' is not a CalculationLine). Line removed.");
            sheet.removeLine(totalPremium);
        } else if (((CalculationLine) totalPremium).getAmount() == null) {
            referPolicy(policy, sheet, "Calculation of total premium failed (assessment line 'total premium' has a null value - getAmount()==null). Line removed.");
            sheet.removeLine(totalPremium);
        }
    }

    private static void referPolicy(Policy policy, AssessmentSheet sheet, String reason) {
        sheet.addReferral(reason, null);
        if (!policy.isAggregator() && !PolicyStatus.DECLINED.equals(policy.getStatus())) {
            policy.setStatus(PolicyStatus.REFERRED);
        }
    }
}
