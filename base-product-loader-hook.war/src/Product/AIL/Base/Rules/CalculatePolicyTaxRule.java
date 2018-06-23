
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
import com.ail.insurance.policy.BehaviourType;
import com.ail.insurance.policy.RateBehaviour;
import com.ail.insurance.quotation.CalculatePolicyTaxService.CalculatePolicyTaxArgument;
import com.ail.util.Rate;

public class CalculatePolicyTaxRule {
    public static void invoke(CalculatePolicyTaxArgument args) {
        AssessmentSheet sheet = args.getAssessmentSheetArgRet();
        AssessmentLine line = new RateBehaviour(sheet.generateLineId(), "IPT", null, "total premium", "total premium", BehaviourType.TAX, new Rate("5%"));
        sheet.addLine(line);
    }
}
