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
import static com.ail.insurance.policy.PolicyLinkType.DISAGGREGATED_FROM;
import static com.ail.insurance.policy.PolicyLinkType.DISAGGREGATED_TO;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.XMLException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyLink;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.policy.Section;
import com.ail.insurance.quotation.AddQuoteNumberService.AddQuoteNumberCommand;

/**
 * Given an Aggregator policy (containing a number of quotations for different
 * products), create an instance of a new policy for one of them. The new policy
 * will contain all of the policy, section, asset, excess (etc) data from the
 * aggregator, but will only contain the assessment sheets appropriate to its
 * product and non of the assessment sheets associated with other products.
 */
@ServiceImplementation
public class DisaggregatePolicyService extends Service<DisaggregatePolicyService.DisaggregatePolicyArgument> {

    private static final long serialVersionUID = 7919058258677636251L;

    @ServiceArgument
    public interface DisaggregatePolicyArgument extends Argument {
        Policy getAggregatedPolicyArg();

        void setAggregatedPolicyArg(Policy aggregatedPolicyArg);

        String getTargetPolicyTypeIdArg();

        void setTargetPolicyTypeIdArg(String targetPolicyTypeIdArg);

        Policy getPolicyRet();

        void setPolicyRet(Policy policyRet);
    }

    @ServiceCommand(defaultServiceClass = DisaggregatePolicyService.class)
    public interface DisaggregatePolicyCommand extends Command, DisaggregatePolicyArgument {
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

        createNewPolicy();

        mergeFromAggregatorIntoNewPolicy();

        tidyUpNewPolicy();

        addQuoteNumberToNewPolicy();

        linkPolicies();

        if (args.getPolicyRet() == null) {
            throw new PostconditionException("args.getPolicyRet() == null");
        }
    }

    private void linkPolicies() {

        PolicyLink linkFrom = new PolicyLink(DISAGGREGATED_FROM, args.getAggregatedPolicyArg().getSystemId());
        args.getPolicyRet().getPolicyLink().add(linkFrom);
        getCore().update(args.getPolicyRet());

        PolicyLink linkTo = new PolicyLink(DISAGGREGATED_TO, args.getPolicyRet().getSystemId());
        args.getAggregatedPolicyArg().getPolicyLink().add(linkTo);
        getCore().update(args.getAggregatedPolicyArg());
    }

    private void addQuoteNumberToNewPolicy() throws BaseException {
        args.getPolicyRet().setQuotationNumber(null);

        AddQuoteNumberCommand aqnc=getCore().newCommand(AddQuoteNumberCommand.class);
        aqnc.setPolicyArgRet(args.getPolicyRet());
        aqnc.invoke();
        args.setPolicyRet(aqnc.getPolicyArgRet());
    }

    private void createNewPolicy() {
        args.setPolicyRet((Policy)getCore().newProductType(args.getTargetPolicyTypeIdArg(), "Policy"));
    }

    private void mergeFromAggregatorIntoNewPolicy() throws XMLException {
        Policy donorPolicy = core.fromXML(Policy.class, core.toXML(args.getAggregatedPolicyArg()));
        args.getPolicyRet().mergeWithDataFrom(donorPolicy, getCore());
    }

    private void tidyUpNewPolicy() {
        args.getPolicyRet().setAggregator(false);
        args.getPolicyRet().setProductTypeId(args.getTargetPolicyTypeIdArg());
        args.getPolicyRet().markAsNotPersisted();
        args.getPolicyRet().setStatus(PolicyStatus.APPLICATION);

        removeItemsFromNewPolicy();
    }

    private void removeItemsFromNewPolicy() {
        AssessmentSheet sheet;
        String productName = args.getTargetPolicyTypeIdArg();
        Policy policy = args.getPolicyRet();

        sheet = policy.getAssessmentSheetFor(productName);
        sheet.markAsNotPersisted();
        policy.getAssessmentSheetList().clear();
        policy.getAssessmentSheetList().put(DEFAULT_SHEET_NAME, sheet);

        for(Section section: policy.getSection()) {

            sheet = section.getAssessmentSheetFor(productName);

            if (sheet!=null) {
                sheet.markAsNotPersisted();
            }

            section.getAssessmentSheetList().clear();

            if (sheet!=null) {
                section.getAssessmentSheetList().put(DEFAULT_SHEET_NAME, sheet);
            }
            section.markAsNotPersisted();
        }

        policy.getDocument().clear();
    }
}
