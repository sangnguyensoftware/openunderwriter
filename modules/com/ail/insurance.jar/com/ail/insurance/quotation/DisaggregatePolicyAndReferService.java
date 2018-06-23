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

import java.util.Map;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.DisaggregatePolicyAndReferService.DisaggregatePolicyAndReferArgument;
import com.ail.insurance.quotation.DisaggregatePolicyService.DisaggregatePolicyCommand;

/**
 * Augments the functionality of the DisaggregatePolicyService with the following;
 *
 * - The new policy will contain a reference to the same proposer, rather than a copy.
 * - The new policy will not have a policy holder.
 * - The new policy status is set to REFERRED.
 *
 * - The aggregator policy will have an attribute added with id SELECTED_SYSTEM_POLICY_ID containing
 *   the system id of the new policy.
 */
@ServiceImplementation
public class DisaggregatePolicyAndReferService extends Service<DisaggregatePolicyAndReferArgument> {

    private static final String SELECTED_SYSTEM_POLICY_ID = "selectedSystemPolicyId";
    private static final long serialVersionUID = 7919058258677636251L;

    @ServiceArgument
    public interface DisaggregatePolicyAndReferArgument extends Argument {

        Policy getAggregatedPolicyArg();

        void setAggregatedPolicyArg(Policy aggregatedPolicyArg);

        String getTargetPolicyTypeIdArg();

        void setTargetPolicyTypeIdArg(String targetPolicyTypeIdArg);

        Policy getPolicyRet();

        void setPolicyRet(Policy policyRet);
    }

    @ServiceCommand(defaultServiceClass = DisaggregatePolicyAndReferService.class)
    public interface DisaggregatePolicyAndReferCommand extends Command, DisaggregatePolicyAndReferArgument {
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        if (args.getAggregatedPolicyArg() == null) {
            throw new PreconditionException("args.getAggregatedPolicyArg() == null");
        }

        if (!args.getAggregatedPolicyArg().isAggregator()) {
            throw new PreconditionException("!args.getAggregatedPolicyArg().isAggregator()");
        }

        if (args.getTargetPolicyTypeIdArg() == null || args.getTargetPolicyTypeIdArg().length() == 0) {
            throw new PreconditionException("args.getTargetPolicyTypeIdArg() == null || args.getTargetPolicyTypeIdArg().length() == 0");
        }

        if (args.getAggregatedPolicyArg().getAssessmentSheetFor(args.getTargetPolicyTypeIdArg()) == null) {
            throw new PreconditionException("args.getAggregatedPolicyArg().getAssessmentSheetFor(args.getTargetPolicyTypeIdArg()) == null");
        }

        for (Map.Entry<String, AssessmentSheet> entry : args.getAggregatedPolicyArg().getAssessmentSheetList().entrySet()) {
            AssessmentSheet sheet = entry.getValue();
            for (Attribute attr : sheet.getAttribute()) {
                if (SELECTED_SYSTEM_POLICY_ID.equals(attr.getId())) {
                    throw new PreconditionException("Aggregated policy already has selected product");
                }
            }
        }


        Policy newPolicy = disaggregatePolicy();

        setupNewPolicy(newPolicy);

        updateAggregatedPolicy(newPolicy);

        args.setPolicyRet(newPolicy);

        if (args.getPolicyRet() == null) {
            throw new PostconditionException("args.getPolicyRet() == null");
        }
    }

    private void updateAggregatedPolicy(Policy newPolicy) {

        args.getAggregatedPolicyArg().getAssessmentSheetFor(args.getTargetPolicyTypeIdArg()).addAttribute(
                new Attribute(SELECTED_SYSTEM_POLICY_ID, newPolicy.getSystemId() + "", "string"));
        getCore().update(args.getAggregatedPolicyArg());
    }

    private void setupNewPolicy(Policy newPolicy) {

        newPolicy.setStatus(PolicyStatus.REFERRED);
        newPolicy.setClient(args.getAggregatedPolicyArg().getClient());
        getCore().create(newPolicy);
    }

    private Policy disaggregatePolicy() throws BaseException {

        DisaggregatePolicyCommand dpc = getCore().newCommand(DisaggregatePolicyCommand.class);
        dpc.setAggregatedPolicyArg(args.getAggregatedPolicyArg());
        dpc.setTargetPolicyTypeIdArg(args.getTargetPolicyTypeIdArg());
        dpc.invoke();

        return dpc.getPolicyRet();

    }

}
