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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Core;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;

@ServiceInterface
public interface AssessPolicyRiskService {

    @ServiceArgument
    public interface AssessPolicyRiskArgument extends Argument {
        /**
         * Fetch the value of the policy argument. $Description$
         * @see #setPolicy
         * @return value of policy
         */
        Policy getPolicyArg();

        /**
         * Set the value of the policy argument. $Description$
         * @see #getPolicy
         * @param policy New value for policy argument.
         */
        void setPolicyArg(Policy policy);

        /**
         * Fetch the value of the assessmentsheet argument. The table to which the service should add behaviours.
         * @see #setAssessmentSheet
         * @return value of assessmentsheet
         */
        AssessmentSheet getAssessmentSheetArgRet();

        /**
         * Set the value of the assessmentsheet argument. The table to which the service should add behaviours.
         * @see #getAssessmentSheet
         * @param assessmentSheet New value for assessmentsheet argument.
         */
        void setAssessmentSheetArgRet(AssessmentSheet assessmentSheet);

        /**
         * Getter for the coreArg property. Assess risk core for logging/lookups etc.
         * @return Value of coreArg, or null if it is unset
         */
        Core getCoreArg();

        /**
         * Setter for the coreArg property. * @see #getCoreArg
         * @param coreArg new value for property.
         */
        void setCoreArg(Core coreArg);
    }

    @ServiceCommand
    public interface AssessPolicyRiskCommand extends Command, AssessPolicyRiskArgument {
    }
}
